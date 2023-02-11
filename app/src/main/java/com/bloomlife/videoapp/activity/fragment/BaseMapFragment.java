package com.bloomlife.videoapp.activity.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.VideoEditActivity;
import com.bloomlife.videoapp.activity.MainActivity;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.activity.VideoViewPagerActivity;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.FinishBroadcast;
import com.bloomlife.videoapp.app.NotificationService;
import com.bloomlife.videoapp.common.util.SoundPlay;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.manager.LocationManager;
import com.bloomlife.videoapp.manager.MessageManager;
import com.bloomlife.videoapp.manager.PreviewImageRandomShower;
import com.bloomlife.videoapp.manager.RandomShower;
import com.bloomlife.videoapp.model.MapControllOption;
import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.PushLatLng;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.GetVideoMessage;
import com.bloomlife.videoapp.model.message.RandomVideoMessage;
import com.bloomlife.videoapp.model.message.UserLocationMessage;
import com.bloomlife.videoapp.model.result.GetVideoListResult;
import com.bloomlife.videoapp.model.result.GetVideoResult;
import com.bloomlife.videoapp.model.result.MoreVideoResult;
import com.bloomlife.videoapp.model.result.RandomVideoResult;
import com.bloomlife.videoapp.view.DescriptionLayoutView;
import com.bloomlife.videoapp.view.MaploadStatusView;
import com.bloomlife.videoapp.view.RandomVideoButton;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.WaveView;
import com.easemob.chat.EMChatManager;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.bloomlife.android.common.CacheKeyConstants.LOCATION_LAT;
import static com.bloomlife.android.common.CacheKeyConstants.LOCATION_LON;
import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_VIDEO_UPLOAD_FAIL;

import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_FIRST_RANDOM_VIDEO;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/3.
 */
public abstract class BaseMapFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BaseMapActivity";

    public static final String INTENT_VIDEO = "video";

    public static final int FIRST_RANDOM_VIDEO = 1;

    public static final int DOT_ANIM_DURATION = 300;

    public static final double Deviation = 0.0001;


    // 用户拍摄的那个视频的文件路径，因为还没有上传完没有id，所以只能根据这个判断
    public static final String INTENT_USER_VIDEO_FILE_PATH = "intent_user_video_file_path";

    @ViewInject(id = R.id.random, click = ViewInject.DEFAULT)
    protected RandomVideoButton mRandomVideoBtn;

    @ViewInject(id = R.id.local, click = ViewInject.DEFAULT)
    private ImageView local;


    @ViewInject(id = R.id.btn_load_status)
    private MaploadStatusView mMapReloadView;

    @ViewInject(id = R.id.activity_main_waveview)
    protected WaveView mWaveView;

    protected MapControllOption option = new MapControllOption();

    protected LocationRecevier locationRecevier;

    protected DeleteVideoReceiver deleteVideoRecevier;

    protected FinishBroadcast finishBroadcast;

    protected UploadVideoReceiver uploadVideoReceiver;

    protected MyLatLng userLatLng;
    protected Video userVideo; // 用户最新发送的，在地图上显示的视频

    protected Video tapMoreVideo; // 点击more图标时生成对应的适配大点

    protected List<Video> mVideoList = new ArrayList<>();
    protected List<MoreVideoResult.MoreVideoVo> mMoreList = new ArrayList<>();

    @ViewInject(id = R.id.senddot)
    protected ImageView senddot;

    protected boolean hasLoadVideo;

    protected PreviewImageRandomShower randomShow;

    private PushVideoReceiver mPushVideoReceiver;

    private SoundPlay mSoundPlay;

    private CacheBean cacheBean = CacheBean.getInstance();

    private int mDotHeight;
    private int mDotWidth;

    private boolean mLoadingRandomVideo;

    /** 随机视频 **/
    protected Video mRandomVideo;

    protected Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initRecevier();
        initData();
        LocationManager.getInstance(getActivity().getApplicationContext()).startLocation();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void bringUiToFront() {
        local.bringToFront();
        mRandomVideoBtn.bringToFront();
    }

    protected void initData() {
        setUserLatLng();
        setUserLocation();
        mSoundPlay = new SoundPlay(getActivity());
    }

    protected abstract void initMap();

    protected void initUi(View layout) {
        mMapReloadView.setListener(mStatusViewListener);
        mPushVideoReceiver = new PushVideoReceiver();
        initMapPoint();
        initRandomDisplayUi(layout);
        checkUploadFail();
    }

    /**
     * 初始化描述弹窗相关的ui信息
     */
    private void initRandomDisplayUi(View layout) {
        // 显示视频描述的imageview集合
        DescriptionLayoutView display1 = (DescriptionLayoutView) layout.findViewById(R.id.desc1);
        display1.setOnClickListener(this);

        DescriptionLayoutView display2 = (DescriptionLayoutView) layout.findViewById(R.id.desc2);
        display2.setOnClickListener(this);

        // 随机一个视频后显示的弹窗
        DescriptionLayoutView display3 = (DescriptionLayoutView) layout.findViewById(R.id.desc3);
        display3.setOnClickListener(this);
        display3.bringToFront();

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.play_dot_female, options);
        mDotHeight = options.outHeight;
        mDotWidth = options.outWidth;
        randomShow = new PreviewImageRandomShower(display1, display2, display3, mDotHeight, getActivity());
        randomShow.setPlayDotWidth(options.outWidth);
    }

    protected void mapStatusChangeFinish() {
        if (mPushLatLng != null) {
            mHandler.postDelayed(postAtTimeVideoPlay, 500);
        }
    }

    protected void initMapPoint() {
        mPushLatLng = getActivity().getIntent().getParcelableExtra(MainActivity.INTENT_LATLNG);
        // 如果不为空，说明是点击推送后打开的。需要把位置设置到推送过来的经纬度。
        if (mPushLatLng != null) {
            changeMap(new MyLatLng(mPushLatLng.getLat(), mPushLatLng.getLon()),
                    option.getZoomBarMaxLevle());
            mHandler.postDelayed(postAtTimeVideoPlay, 1500);
        }
    }

    protected abstract boolean changeMap(MyLatLng myLatLng);

    protected abstract boolean changeMap(MyLatLng myLatLng, float zoom);

    protected abstract void loadVideoData(String topic);

    protected abstract boolean isOnScreenRange(MyLatLng latLng);

    protected abstract PointF getScreenPoint(double lat, double lon);

    protected abstract void addHotMarkerToMap(Video v);

    protected abstract RandomShower.GetScreenPoint getScreenPointUtil();

    protected void initRecevier() {
        locationRecevier = new LocationRecevier();
        IntentFilter filter = new IntentFilter(Constants.ACTION_LOCATION);
        getActivity().registerReceiver(locationRecevier, filter);

        deleteVideoRecevier = new DeleteVideoReceiver();
        filter = new IntentFilter(Constants.ACTION_DELETE_VIDEO);
        getActivity().registerReceiver(deleteVideoRecevier, filter);

        finishBroadcast = new FinishBroadcast(getActivity());
        filter = new IntentFilter(Constants.ACTION_FINISH);
        getActivity().registerReceiver(finishBroadcast, filter);

        uploadVideoReceiver = new UploadVideoReceiver();
        filter = new IntentFilter(Constants.ACTION_UPLOAD_PROGRESS);
        getActivity().registerReceiver(uploadVideoReceiver, filter);

        getActivity().registerReceiver(uploadProgressRecevier, new IntentFilter(
                Constants.ACTION_UPLOAD_PROGRESS));
    }

    /***
     * 定位回调
     *
     * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
     *
     * @date 2014-12-23 下午3:33:04
     */
    public class LocationRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setUserLatLng();
            setUserLocation();
        }
    }

    protected void setUserLatLng() {
        String lat = cacheBean.getString(getActivity().getApplicationContext(),
                LOCATION_LAT);
        String lon = cacheBean.getString(getActivity().getApplicationContext(),
                LOCATION_LON);
        if (StringUtils.isEmpty(lat) || StringUtils.isEmpty(lon))
            return;
        userLatLng = new MyLatLng(Double.parseDouble(lat),
                Double.parseDouble(lon));
    }

    /***
     * 视频删除接收器，用以将已经删除的视频从地图上取消可见
     *
     * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
     *
     * @date 2014-12-23 下午3:32:34
     */
    public class DeleteVideoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String deleteSendPath = intent.getStringExtra(Constants.INTENT_DELETE_SEND);
            if (StringUtils.isNotEmpty(deleteSendPath) && userVideo != null) {
                mVideoList.remove(userVideo);
                userVideo = null;
                Log.d(TAG, " 用户发送的那个视频删除了，清除地图点 ");
            }

            String deleteIds = intent
                    .getStringExtra(Constants.INTENT_DELETE_VIDEOIDS);
            if (StringUtils.isNotEmpty(deleteIds)) {
                Iterator<Video> iterator = mVideoList.iterator();
                Video video = null;
                while (iterator.hasNext()) {
                    video = iterator.next();
                    if (video.getVideoid() != null
                            && deleteIds.contains(video.getVideoid())) {
                        break;
                    } else {
                        video = null;
                    }
                }
                if (video != null) {
                    mVideoList.remove(video); // 如果用户已经删除，那么地图上需要马上删除这个视频。
                    Log.d(TAG, " 用户删除了他的视频:" + video.getVideoid() + " , 清除地图标记");
                }
            }
        }

    }

    private boolean onclickEnable = true;

    @Override
    public void onClick(View v) {
        if (!onclickEnable)
            return;
        onclickEnable = false;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onclickEnable = true;
            }
        }, 1800);
        switch (v.getId()) {
            case R.id.desc1:
            case R.id.desc2:
            case R.id.desc3:
                Video video = (Video) v.getTag();
                if (video != null)
                    jumpToPlayVideo(video);
                break;

            case R.id.random:
			if (cacheBean.getInt(getActivity(), KEY_FIRST_RANDOM_VIDEO, 0) != FIRST_RANDOM_VIDEO) {
				cacheBean.putInt(getActivity(), KEY_FIRST_RANDOM_VIDEO, FIRST_RANDOM_VIDEO);
				SuperToast.show(getActivity(), getString(R.string.activity_main_random_video));
			}
			if (!mLoadingRandomVideo)
				Volley.addToTagQueue(new MessageRequest(new RandomVideoMessage(), mRandomVideoReqListener));
                break;

            default:
                break;
        }
    }

    /**
     * 跳到视频播放页面
     *
     * @param tapVideo
     *
     */
    protected void jumpToPlayVideo(Video tapVideo) {
        Intent intent = new Intent(getActivity(), VideoViewPagerActivity.class);
        ArrayList<Parcelable> videoList = new ArrayList<Parcelable>();
        videoList.addAll(mVideoList);
        if (videoList.isEmpty()) {
            UiHelper.showToast(getActivity(), getString(R.string.activity_video_list_empty));
            return;
        }
        intent.putParcelableArrayListExtra(
                VideoViewPagerActivity.INTENT_VIDEO_LIST, videoList);
        int index = videoList.indexOf(tapVideo);
        if (index < 0) {
            Log.e(TAG,
                    " 在videolist中找不到对应的tapvideo, tapvideo id = "
                            + tapVideo.getVideoid());
            for (int i = 0, n = mVideoList.size(); i < n; i++) {
                if (mVideoList.get(i).getVideoid()
                        .equals(tapVideo.getVideoid())) {
                    index = i;
                    break;
                }
            }
        }
        intent.putExtra(VideoViewPagerActivity.INTENT_VIDEO_POSITION, index);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_camera_in, 0);
        Log.d(TAG, " start jumpToPlayVideo fragment ");
    }

    /**
     * 加载热点视频成功时执行的处理
     */
    public void doLoadVideoSuccess(GetVideoListResult result) {
        videoCacheMap.clear();
        for (int i=0; i < result.getVideos().size(); i++) {
            Video video = result.getVideos().get(i);
            videoCacheMap.put(video.getVideoid(), video);
            if (i == 0){
                mMapReloadView.setCity(video.getCity());
            }
        }
    }

    /** 大点的缓存里面，用来过滤小点和其他特殊的点，避免重复加载使用。 **/
    protected final Map<String, Video> videoCacheMap = new HashMap<String, Video>();

    private List<PointF> videoCachePoints = new ArrayList<PointF>();

    protected final Map<String, MoreVideoResult.MoreVideoVo> moreIdList = new HashMap<String, MoreVideoResult.MoreVideoVo>();

    /**
     * 加载网格视频成功时进行的处理
     *
     * @param result
     */
    public void doLoadMoreVideoSuccess(MoreVideoResult result) {
        mMoreList.clear();

        videoCachePoints.clear();
        for (Video v:videoCacheMap.values()){
            videoCachePoints.add(getScreenPoint(Double.parseDouble(v.getLat()), Double.parseDouble(v.getLon())));
        }

        Log.d(TAG, "set more video ");
        if (!Utils.isEmptyCollection(result.getVideos())) {
            moreIdList.clear();
            for (MoreVideoResult.MoreVideoVo video : result.getVideos()) {
                if (videoCacheMap.containsKey(video.getVideoid()) || isOnHotdot(video))
                    continue; // 这个位置已经有大点了，不要显示小点。
                moreIdList.put(video.getVideoid(), video);
                mMoreList.add(video);
            }
        }
//		if (mRandomVideo != null){
//			PointF point = getScreenPoint(Double.parseDouble(mRandomVideo.getLat()), Double.parseDouble(mRandomVideo.getLon()));
//			// 如果随机视频是在屏幕内，且请求大点返回时随机视频没在里面，需要将返回的视频数增加一
//			if (point.x > 0 && point.y > 0)
//				result.setVideonum(result.getVideonum() + 1);
//		}
        mMapReloadView.setLoadVideoNumber(result.getVideonum());
        if (result.getVideonum() == 0){
            mRandomVideoBtn.startRotationAnim();
        } else {
            mRandomVideoBtn.stopRotationAnim();
        }
    }

    protected void showRandomVideoWindow(){
        if (mRandomVideo != null){
            randomShow.showRandomVideoPreview(mRandomVideo, getScreenPointUtil());
            mRandomVideo = null;
        }
    }

    protected boolean isOnHotdot(MoreVideoResult.MoreVideoVo v){
        PointF moreDotPoint = getScreenPoint(Double.parseDouble(v.getLat()), Double.parseDouble(v.getLon()));
        for (PointF p:videoCachePoints){
            if (Math.abs(p.x - moreDotPoint.x) < mDotWidth && Math.abs(p.y - moreDotPoint.y) < mDotHeight){
                return true;
            }
        }
        return false;
    }

    protected boolean isSendAnimationFinish = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == VideoEditActivity.RESULT_VIDEO && userLatLng != null) {
            final Video resultVideo = data.getParcelableExtra(VideoEditActivity.INTENT_VIDEO);
            if (userVideo != null) {
                Log.d(TAG, " 移除用户上一次的视频");
                mVideoList.remove(userVideo);
            }

            if (StringUtils.isEmpty(resultVideo.getLat())
                    || StringUtils.isEmpty(resultVideo.getLon())) {
                return;
            }
            setUserVideo(resultVideo);
            isSendAnimationFinish = false;
            mVideoList.add(userVideo);
            userSendFilePath = userVideo.getFilaPath();
            mSoundPlay.play(R.raw.flyby);
            showUserSendVideo();
        } else {
            isSendAnimationFinish = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected abstract void showUserSendVideo();


    public void setUserVideo(Video userVideo) {
        this.userVideo = userVideo;
        this.userVideo.setSendVideo(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationRecevier != null)
            getActivity().unregisterReceiver(locationRecevier);
        if (deleteVideoRecevier != null)
            getActivity().unregisterReceiver(deleteVideoRecevier);
        if (finishBroadcast != null)
            getActivity().unregisterReceiver(finishBroadcast);
        getActivity().unregisterReceiver(uploadVideoReceiver);
        getActivity().unregisterReceiver(uploadProgressRecevier);
        mSoundPlay.release();
    }

    public static String userSendFilePath;

    public static String getUserSendFilePath() {
        return userSendFilePath;
    }

    public void showProgress(boolean show) {
        if (show) {
            mMapReloadView.startLoadProgress();
        } else {
            mMapReloadView.stopLoadProgress();
        }
    }

    public void showLoadFailTips() {
        mMapReloadView.showLoadFail();
    }

    protected void startVideoPlayActivity(String videoid) {
        Volley.addToTagQueue(new MessageRequest(new GetVideoMessage(videoid), mGetVideoListener));
    }

    /**
     * 发送用户的地理位置到服务器，进入地图页面时候调用。
     * 如果进入地图页面的时候地理位置还未获取到，那么就不会发送，等到收到地理位置获取成功的广播后会被再次调用。
     */
    private void setUserLocation() {
        String lat = cacheBean.getString(getActivity(), LOCATION_LAT);
        String lon = cacheBean.getString(getActivity(), LOCATION_LON);
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon))
            return;
        UserLocationMessage message = new UserLocationMessage();
        message.setLat(lat);
        message.setLon(lon);
        Volley.addToTagQueue(new MessageRequest(message));
    }

    private MessageRequest.Listener<GetVideoResult> mGetVideoListener = new MessageRequest.Listener<GetVideoResult>(){
        @Override
        public void success(GetVideoResult result) {
            ArrayList<Video> videos = new ArrayList<>();
            videos.add(Video.makeByResult(result));
            Intent intent = new Intent(getActivity(), VideoViewPagerActivity.class);
            intent.putParcelableArrayListExtra(
                    VideoViewPagerActivity.INTENT_VIDEO_LIST, videos);
            intent.putExtra(VideoViewPagerActivity.INTENT_VIDEO_POSITION, 0);
            startActivity(intent);
        }
    };

    class UploadVideoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            VideoProgress progress = (VideoProgress) intent
                    .getSerializableExtra(Constants.INTENT_UPLOAD_PROGRESS);
            if (Constants.PROGRESS_SUCCESSS == progress.getProgress()
                    && userVideo != null) {
                if (!userVideo.getFilaPath().equals(progress.getFileKey()))
                    return; // 当个人主页有多个上传时，上传成功的不一定就是当前的视频
                Log.d(TAG, "Server VideoID: " + progress.getServerVideoId());
                userVideo.setVideoid(progress.getServerVideoId());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mPushVideoReceiver, new IntentFilter(Constants.ACTION_PUSH_VIDEO));
        checkUploadFail();
    }

    private void checkUploadFail(){
        // 判断是否要显示视频上传失败的提示
        if (cacheBean.getInt(getActivity(), KEY_VIDEO_UPLOAD_FAIL, Video.STATUS_UPLOAD_SUCCESS) != Video.STATUS_UPLOAD_SUCCESS) {
            mMapReloadView.showUploadFail();
        } else {
            mMapReloadView.hideUploadFial();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mPushVideoReceiver);
    }

    /**
     * 视频上传进度的广播接受者
     */
    private BroadcastReceiver uploadProgressRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            VideoProgress videoProgress = (VideoProgress) intent
                    .getSerializableExtra(Constants.INTENT_UPLOAD_PROGRESS);
            if (videoProgress.getProgress() == Constants.PROGRESS_FAILURE) {
                mMapReloadView.showUploadFail();
                cacheBean.putInt(getActivity(), KEY_VIDEO_UPLOAD_FAIL, Video.STATUS_UPLOAD_FAIL);
            }
        }

    };



    private PushLatLng mPushLatLng;

    /**
     * 推送视频的广播接收器
     */
    class PushVideoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mPushLatLng = intent
                    .getParcelableExtra(NotificationService.PUSH_LAT_LNG);
            changeMap(new MyLatLng(mPushLatLng.getLat(), mPushLatLng.getLon()),
                    option.getZoomBarMaxLevle());
        }

    }

    private Runnable postAtTimeVideoPlay = new Runnable() {

        @Override
        public void run() {
            if (mPushLatLng != null) {
                startVideoPlayActivity(mPushLatLng.getVideoId());
                mPushLatLng = null;
            }
        }

    };


    private MaploadStatusView.Listener mStatusViewListener = new MaploadStatusView.Listener() {

        @Override
        public void OnRetry() {
            loadVideoData(null);
        }

        @Override
        public void OnReUpload() {
            startMyVideoActivity();
        }
    };

    private void startMyVideoActivity(){
        Intent intent = new Intent(getActivity(), MyVideoActivity.class);
        intent.putExtra(MyVideoActivity.INTENT_FIRST_PAGE, MyVideoActivity.PAGE_VIDEOS);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_bottom_in, 0);
        BackgroundManager.getInstance().capture(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 发送视频回到地图页面时显示的动画
     *
     * @param animatorListener
     */
    protected void playSendDotAnimator(Point point,
                                       Animator.AnimatorListener animatorListener) {
        senddot.setImageResource(AppContext.getSysCode().getSex() == Video.FEMALE ? R.drawable.play_dot_female : R.drawable.play_dot_male);
        playJumpAnimator(senddot, userVideo, 5f, point, animatorListener);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaveView.startAnim();
            }
        }, 130);
    }

    /**
     * 检查随机的点是否在返回的热点视频列表中，如果没有的话，添加到大点列表中
     *
     * @param videos
     */
    protected void checkRandomVideo(List<Video> videos) {
        if (mRandomVideo == null)
            return;
        boolean isInVideos = false;
        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);
            if (mRandomVideo.getVideoid().equals(video.getVideoid())) {
                Log.d(TAG, "小点" + video.getVideoid());
                isInVideos = true;
            }
        }
        // 只有随机的点属于屏幕显示区域中才会添加
        if (!isInVideos && isOnScreenRange(mRandomVideo.obtainLatLng())) {
            videos.add(mRandomVideo);
        }
    }

    protected void playJumpAnimator(View view, Video video, Float ratioY,
                                    Point point, Animator.AnimatorListener animatorListener) {
        view.setX(point.x - view.getWidth() / 2);
        view.setY(point.y - view.getHeight());
        view.setAlpha(1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        if (ratioY == null)
            ratioY = 0.37f;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", view.getY() - view.getHeight() * ratioY,view.getY());
        if (animatorListener != null) { // 最后一个动画需要添加数据on
            objectAnimator.addListener(animatorListener);
        }
        objectAnimator
                .setInterpolator(new AnticipateOvershootInterpolator(1.6f));
        objectAnimator.setDuration(DOT_ANIM_DURATION);

        animatorSet.play(objectAnimator).before(
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(200));
        animatorSet.start();
        view.setVisibility(View.VISIBLE);
    }

    private MessageRequest.Listener<RandomVideoResult> mRandomVideoReqListener = new MessageRequest.Listener<RandomVideoResult>(){
        @Override
        public void start() {
            mLoadingRandomVideo = true;
            mRandomVideo = null; // 重设。
            mRandomVideoBtn.startRotationAnim();
        }

        @Override
        public void success(RandomVideoResult result) {
            mRandomVideo = new Video();
            mRandomVideo.setVideoid(result.getVideoid());
            mRandomVideo.setLooknum(result.getLooknum());
            mRandomVideo.setLikenum(result.getLikenum());
            mRandomVideo.setUploadTime(result.getCreatetime());
            mRandomVideo.setDescription(result.getDescription());
            mRandomVideo.setVideouri(result.getVideouri());
            mRandomVideo.setPreviewurl(result.getPreviewurl());
            mRandomVideo.setLat(String.valueOf(result.getLat()));
            mRandomVideo.setLon(String.valueOf(result.getLon()));
            mRandomVideo.setUid(result.getUid());

            addHotMarkerToMap(mRandomVideo);
            changeMap(new MyLatLng(result.getLat(), result.getLon()));
        }

        @Override
        public void finish() {
            mLoadingRandomVideo = false;
            mRandomVideoBtn.stopRotationAnim();
        }
    };
}
