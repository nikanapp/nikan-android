package com.bloomlife.videoapp.view.watermark;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.bloomlife.android.log.Logger;
import com.bloomlife.android.log.LoggerFactory;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.model.Emotion;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/18.
 */
public class StoryWatermarkPlayer extends RelativeLayout implements TextureView.SurfaceTextureListener {

    public StoryWatermarkPlayer(Context context) {
        super(context);
        init(context);
    }

    public StoryWatermarkPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryWatermarkPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StoryWatermarkPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private static final String TAG = StoryWatermarkPlayer.class.getSimpleName();
    private static final int BITMAP_CACHE_NUM = 3;
    /** 绘制一帧的时间 **/
    private static final int FRAME_TIME = 1000 / 24;

    private Logger mLog = LoggerFactory.getLogger(TAG);

    private TextureView mTexture;
    private SurfaceTexture mSurfaceTexture;

    private OnStopListener mStopListener;
    private OnPlayListener mPlayListener;

    private BaseMkView mMkView;
    private Draw mDraw;

    private Load mLoad;
    private ConcurrentLinkedQueue<Bitmap> mBitmapCacheList;
    private BitmapFactory.Options mBitmapOptions;
    private OnPauseListener mPauseListener;
    private volatile boolean mStart;
    private volatile boolean mPause;
    private volatile boolean mRelease;
    private volatile int mFrameCount;

    private volatile float mDrawWidth;
    private volatile float mDrawHeight;

    private String mCity;
    private String mDescription;
    private long mDate;
    private Emotion mEmotion;

    private String mResourceName;

    private AtomicInteger mCurrent = new AtomicInteger(0);
    private AtomicBoolean mClearing = new AtomicBoolean(false);
    private Handler mHandler = new Handler();

    private void init(Context context) {
        inflate(context, R.layout.view_watermark_player, this);
        mTexture = (TextureView) findViewById(R.id.watermark_surface);
        mTexture.setSurfaceTextureListener(this);
        mTexture.setOpaque(false);

        mBitmapCacheList = new ConcurrentLinkedQueue<>();
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inPurgeable = true;
        mBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setDisplayWidthAndHeight();
    }

    public void setInfo(String city, String description, long date){
        mCity = city;
        mDescription = description;
        mDate = date;
        if (mMkView != null){
            mMkView.setInfo(mCity, mDescription, mDate);
        }
    }

    public void start() {
        if (mStart || TextUtils.isEmpty(mResourceName)) {
            return;
        }
        mLog.d("start");
        mStart = true;
        if (mRelease){
            mRelease = false;
            mLog.d("init");
            setEmotion(mEmotion);
        }
        clearCanvas();
        startPlay();
    }

    public void pause() {
        mLog.d("pause");
        if (mStart && !mPause) {
            mPause = true;
            if (mMkView != null) {
                mMkView.pause();
            }
            interruptPlay();
        }
    }

    public void resume(){
        mLog.d("resume");
        if (mStart && mPause){
            // 如果水印动画被暂停了，重新开始
            if (mMkView != null && mMkView.isPause()) {
                mMkView.resume();
            }
            startPlay();
            mPause = false;
        }
    }

    public void stop() {
        mLog.d("stop");
        if (mStart) {
            if (mDraw != null) {
                mDraw.mClear = true;
            }
            if (mMkView != null){
                removeView(mMkView);
                mMkView = null;
            }
            interruptPlay();
            mStart = false;
            mPause = false;
        }
    }

    public void release(){
        mLog.d("release");
        if (mStart){
            stop();
        }
        clear();
        mRelease = true;
    }

    public void time(long time, long totalTime){
        if (!mStart || mMkView == null)
            return;
        mMkView.setCurrentTime(time, totalTime);
    }

    private void startPlay(){
        mLoad = new Load();
        mDraw = new Draw();

        AppContext.EXECUTOR_SERVICE.execute(mLoad);
        AppContext.EXECUTOR_SERVICE.execute(mDraw);
    }

    private void interruptPlay(){
        if (mLoad != null)
            mLoad.mRunning.set(false);
        if (mDraw != null)
            mDraw.mRunning.set(false);
    }

    public boolean isPlaying(){
        return this.mStart;
    }

    private void clear(){
        // 防止被多个线程同时清理，因为在某些Rom上会导致lockCanvas失败，但是却返回的不是null，造成unlockCanvasAndPost失败抛异常。
        if (!mClearing.compareAndSet(false, true))
            return;
        Logger.d(TAG, "clear bitmapList canvas start");
        // 清空图片缓存队列
        for (Bitmap b: mBitmapCacheList){
            b.recycle();
        }
        mBitmapCacheList.clear();
        mCurrent.set(0);
        mLog.d("clear Canvas start");
        clearCanvas();
        mClearing.set(false);
        mLog.d("clear Canvas end");
    }

    public void setEmotion(Emotion emotion){
        if (mMkView != null)
            removeView(mMkView);
        if (emotion == null)
            return;
        else
            mEmotion = emotion;
        switch (emotion.getFrameprefix()){
            case "touched":
                mMkView = new MkAffectView(getContext());
                break;

            case "afraid":
                mMkView = new MkAfraidView(getContext());
                break;

            case "focus":
                mMkView = new MkFocusView(getContext());
                break;

            case "happy":
                mMkView = new MkHappyView(getContext());
                break;

            case "exciting":
                mMkView = new MkInspireView(getContext());
                break;

            case "lonely":
                mMkView = new MkLonelyView(getContext());
                break;

            case "intense":
                mMkView = new MkNervousView(getContext());
                break;

            case "calm":
                mMkView = new MkQuietView(getContext());
                break;

            case "sad":
                mMkView = new MkSadView(getContext());
                break;
        }
        if (mMkView != null){
            mMkView.setInfo(mCity, mDescription, mDate);
            addView(mMkView);
        }
        setSource(emotion.getFrameprefix(), emotion.getFramecount() + 1);
        setDisplayWidthAndHeight();
    }

    public void setSource(@NonNull String resName, int frameCount){
        mResourceName = resName;
        mFrameCount = frameCount;
    }

    private void setDisplayWidthAndHeight(){
        if ("happy".equals(mResourceName)){
            mDrawWidth = getMeasuredWidth();
            mDrawHeight = getMeasuredWidth() * 1.77777f;
        } else {
            mDrawWidth = getMeasuredWidth();
            mDrawHeight = getMeasuredHeight();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTexture = surface;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceTexture = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 绘制水印动画
     */
    class Draw implements Runnable{

        private AtomicBoolean mRunning = new AtomicBoolean(false);
        private volatile boolean mClear;

        @Override
        public void run() {
            mRunning.set(true);
            postPlayMessage();
            long startPlay = System.currentTimeMillis();
            Matrix matrix = new Matrix();
            try {
                for (int i = mCurrent.get(); i < mFrameCount && mRunning.get(); ) {
                    long start = System.currentTimeMillis();
                    // 从队列里获取图片
                    Bitmap bitmap = mBitmapCacheList.poll();
                    if (bitmap == null) {
                        Thread.sleep(10);
                        continue;
                    } else {
                        i++;
                    }
                    if (mTexture == null)
                        break;
                    Canvas canvas = mTexture.lockCanvas();
                    if (canvas == null)
                        break;
                    // 清除画布
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    // 缩放到当前屏幕大小一致
                    matrix.setScale(mDrawWidth / bitmap.getWidth(), mDrawHeight / bitmap.getHeight());
                    canvas.drawBitmap(bitmap, matrix, null);
                    mTexture.unlockCanvasAndPost(canvas);
                    long duration = FRAME_TIME - (System.currentTimeMillis() - start);
                    // 保持24帧每秒，如果绘制的时间小于42毫秒需要睡眠一段时间
                    if (duration > 0)
                        Thread.sleep(duration);
                    mLog.d("draw duration : " + duration);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                mLog.e("Interrupted");
            } catch (IllegalArgumentException e){

            } finally {
                // 是否要清除资源和画布
                if (mClear) {
                    clear();
                    postStopMessage();
                } else {
                    postPauseMessage();
                }
            }
            mLog.d("drawRun end :" + (System.currentTimeMillis() - startPlay));
        }
    }

    /**
     * 加载水印图片资源
     */
    class Load implements Runnable{

        protected AtomicBoolean mRunning = new AtomicBoolean(false);

        @Override
        public void run() {
            mRunning.set(true);
            for (; mCurrent.get() < mFrameCount && mRunning.get();) {
                if (mBitmapCacheList.size() < BITMAP_CACHE_NUM) {
                    // 缓存队列不满，读取下一帧图片
                    Bitmap bitmap = null;
                    String name = mResourceName + String.format("%03d", mCurrent.incrementAndGet());
                    int resId = getResources().getIdentifier(name, "drawable", getContext().getPackageName());
                    if (resId > 0)
                        bitmap = BitmapFactory.decodeResource(getResources(), resId, mBitmapOptions);
                    else
                        break;
                    if (bitmap != null)
                        mBitmapCacheList.add(bitmap);
                } else {
                    // 图片资源达到缓存数，睡眠一帧。
                    try {
                        Thread.sleep(FRAME_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }
            mLog.d("loadRun end");
        }
    }

    /**
     * 向主线程发送水印动画已经停止的消息
     */
    private void postStopMessage(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mStopListener != null)
                    mStopListener.onStop(StoryWatermarkPlayer.this);
            }
        });
    }

    /**
     * 向主线程发送水印动画已经暂停的消息
     */
    private void postPauseMessage(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPauseListener != null)
                    mPauseListener.onPause(StoryWatermarkPlayer.this);
            }
        });
    }

    private void postPlayMessage(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayListener != null){
                    mPlayListener.onPlay(StoryWatermarkPlayer.this);
                }
            }
        });
    }

    public void clearCanvas(){
        if (mTexture == null)
            return;
        Canvas canvas = mTexture.lockCanvas();
        if (canvas == null)
            return;
        // 清除画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mTexture.unlockCanvasAndPost(canvas);
    }

    public void setStopListener(OnStopListener l){
        mStopListener = l;
    }

    public void setPauseListener(OnPauseListener l){
        mPauseListener = l;
    }

    public void setPlayListener(OnPlayListener l){
        mPlayListener = l;
    }

    public interface OnStopListener {
        void onStop(StoryWatermarkPlayer player);
    }

    public interface OnPauseListener {
        void onPause(StoryWatermarkPlayer player);
    }

    public interface OnPlayListener {
        void onPlay(StoryWatermarkPlayer player);
    }

}
