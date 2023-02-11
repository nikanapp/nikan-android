/**
 * 
 */
package com.bloomlife.videoapp.view;

import pl.droidsonroids.gif.GifImageView;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.view.ChatAudioRecorderController.ControllerListener.STATE;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 私信页面下方的声音录制控制器
 * @date 2015年4月29日 下午5:19:26
 */
public class ChatAudioRecorderController extends RelativeLayout {

	/**
	 * @param context
	 */
	public ChatAudioRecorderController(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ChatAudioRecorderController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ChatAudioRecorderController(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private static final String TAG = ChatAudioRecorderController.class.getSimpleName();
	
	@ViewInject(id = R.id.view_audio_btn_sound_record)
	private TextView mBtnSoundRecorder;
	
	@ViewInject(id = R.id.view_audio_text_tips)
	private GifImageView mTextTips;
	
	private ControllerListener mListener;
	private Handler mHandler = new Handler();
	private int mTrashHeight;
	private boolean mStart;
	private boolean mTrash;
	private float mDX;
	private float mDY;
	private float mMX;
	private float mMY;
	
	private void init(Context context){
		View layout = inflate(context, R.layout.view_audio_controller, this);
		FinalActivity.initInjectedView(this, layout);
		
		mBtnSoundRecorder.setTypeface(UIHelper.getHelveticaTh(context));
		mBtnSoundRecorder.setOnTouchListener(mTouchRecorder);
		mTrashHeight = context.getResources().getDisplayMetrics().heightPixels / 5;
	}
	
	private View.OnTouchListener mTouchRecorder = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
//			Log.e(TAG, "mTouchRecorder "+event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDY = event.getY();
				mHandler.postDelayed(postStart, 300);
//				getParent().requestDisallowInterceptTouchEvent(true);
				return true;
				
			case MotionEvent.ACTION_MOVE:
				if (!isEnabled()) return false;
				mMY = event.getY();
				if ((mDY - mMY) > mTrashHeight && !mTrash){
					// 手指向上滑动超过屏幕五分之一高度则判断为取消发送状态
					mTrash = true;
					if (mListener != null){
						mListener.status(STATE.trash);
					}
				} else if ((mDY - mMY) < mTrashHeight && mTrash){
					// 小于屏幕五分之一高度则判恢复为发送状态
					mTrash = false;
					if (mListener != null){
						mListener.status(STATE.send);
					}
				} else if (Math.abs(mDX - mMX) > 20){
//					getParent().requestDisallowInterceptTouchEvent(false);
				}
				return true;
				
			case MotionEvent.ACTION_UP:
				if (mTrash){
					trash();
				} else {
					stop();
				}
				return true;
				
			case MotionEvent.ACTION_CANCEL:
				trash();
				return true;
				
			default:
				break;
			}
			return false;
		}
	};
	
	private void trash(){
		if (mStart && mListener != null){
			mListener.trash();
		}else {
			mHandler.removeCallbacks(postStart);
		}
		reset();
	}
	
	public void reset(){
		mStart = false;
		mTrash = false;
		mBtnSoundRecorder.setText(getContext().getText(R.string.activity_chat_audio_record_tips));
		mBtnSoundRecorder.setBackgroundResource(R.drawable.btn_start_speak);
		mTextTips.setVisibility(View.VISIBLE);
	}
	
	private void stop(){
		if (mStart && mListener != null){
			mListener.stop();
		} else {
			mHandler.removeCallbacks(postStart);
		}
		reset();
	}
	
	private Runnable postStart = new Runnable() {
		
		@Override
		public void run() {
			mStart = true;
			mBtnSoundRecorder.setText(getContext().getText(R.string.activity_chat_audio_send_tips));
			mBtnSoundRecorder.setBackgroundResource(R.drawable.btn_stop_speak);
			mTextTips.setVisibility(View.INVISIBLE);
			if (mListener != null){
				mListener.start();
			}
		}
	};
	
	public void setControllerListener(ControllerListener l){
		mListener = l;
	}
	
	public interface ControllerListener{
		enum STATE { trash, send }

		void start();
		void stop();
		void trash();
		void status(STATE state);
	}

}
