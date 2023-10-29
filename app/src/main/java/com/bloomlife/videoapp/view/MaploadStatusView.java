/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.view.NetworkConnentErrorView.OnRetryListener;
import com.bloomlife.videoapp.view.ScanningView.OnStopListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 地图主页加载失败点击重新加载的图标
 * @date 2015年2月2日 下午7:09:08
 */
public class MaploadStatusView extends RelativeLayout implements OnClickListener {

	public MaploadStatusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MaploadStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MaploadStatusView(Context context) {
		super(context);
		init(context);
	}

	public static final String TAG = MaploadStatusView.class.getSimpleName();

	private static final int NUM_DURATION = 2000;
	private static final int SWITCH_TEXT_DURATION = 400;

	private Handler mHandler = new Handler();
	private boolean mUploadFail;
	private TextView mUploadFailView;
	private ViewGroup mLoadSuccView;
	private TextView mLoadSuccText;
	private NetworkConnentErrorView mLoadFailView;
	private ScanningView mLoadProgressBar;
	private int mNum;
	private String mCity;
	
	private void init(Context context){
		inflate(context, R.layout.view_load_status, this);
		mUploadFailView = (TextView) findViewById(R.id.view_status_upload_fail);
		mLoadSuccView = (ViewGroup) findViewById(R.id.view_status_load_succ_text_layout);
		mLoadSuccView.setAlpha(0f);
		mLoadFailView = (NetworkConnentErrorView) findViewById(R.id.view_status_load_error);
		mLoadSuccText = (TextView) findViewById(R.id.view_status_load_succ_text);
		mLoadSuccText.setTypeface(UIHelper.getBebas(context));
		mLoadProgressBar = (ScanningView) findViewById(R.id.view_status_scaningview);
		mLoadProgressBar.addListener(mProgressBarStopListener);
		mLoadProgressBar.setVisibility(View.INVISIBLE);
		
		mUploadFailView.setOnClickListener(this);
		mLoadFailView.setOnRetryListener(mRetryListener);
	}
	
	public void showUploadFail(){
		// 先检查加载失败的提示是否正在显示
		if (!isShowLoadFail()) {
			hideLoadProgress();
			mUploadFailView.setVisibility(View.VISIBLE);
		} else {
			mUploadFail = true;
		}
	}
	
	public void hideUploadFial(){
		mUploadFailView.setVisibility(View.GONE);
		mUploadFail = false;
	}
	
	public boolean isShowUpLoadfail(){
		return mUploadFailView.getVisibility() == View.VISIBLE;
	}
	
	public void showLoadFail(){
		if (isShowUpLoadfail()){
			mUploadFail = true;
			hideUploadFial();
		}
		mNum = 0;
		hideLoadProgress();
		mLoadFailView.setVisibility(View.VISIBLE);
	}
	
	public void hideLoadFail(){
		mLoadFailView.setVisibility(View.GONE);
	}
	
	public boolean isShowLoadFail(){
		return mLoadFailView.getVisibility() == View.VISIBLE;
	}
	
	public void startLoadProgress(){
		if (isShowLoadFail() || isShowUpLoadfail()) return;
		mLoadSuccView.animate().cancel();
		mLoadSuccView.setAlpha(0);
		mLoadProgressBar.start();
	}
	
	public void stopLoadProgress(){
		mLoadProgressBar.stop();
	}
	
	public void hideLoadProgress(){
		mLoadProgressBar.stop();
		mLoadProgressBar.setVisibility(View.INVISIBLE);
	}

	public void setCity(String city){
		mCity = city;
	}
	
	public void setLoadVideoNumber(int num){
		mNum = num;
		if (mUploadFail || isShowUpLoadfail() || isShowLoadFail()){
			mLoadSuccView.setAlpha(0);
			return;
		}
		// 加载视频数大于零，显示数量提示语
		if (num > 0){
			mLoadSuccText.setText(getNumberText());
		} else {
			mLoadSuccText.setText(R.string.dotload_null);
		}
	}

	private CharSequence getNumberText(){
		String leftText = getContext().getString(R.string.dotload_number_left);
		String rightText = getContext().getString(R.string.dotload_number_right);
		int numberLength = String.valueOf(mNum).length();
		int numStart = leftText.length();
		int numEnd = numberLength + numStart;
		SpannableString ss = new SpannableString(leftText + mNum + " " + rightText);
		// 将数字的字体设大、颜色变成黄色
		ss.setSpan(new ForegroundColorSpan(getColor(R.color.view_load_status_number_yellow)), numStart, numEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		ss.setSpan(new AbsoluteSizeSpan(27, true), numStart, numEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		// 其他文字的字体设为白色
		if (numStart > 0){
			ss.setSpan(new ForegroundColorSpan(getColor(R.color.white)), 0, numStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(15, true), 0, numStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		ss.setSpan(new ForegroundColorSpan(getColor(R.color.white)), numEnd, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		ss.setSpan(new AbsoluteSizeSpan(15, true), numEnd, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		return ss;
	}
	
	private int getColor(int res){
		return getContext().getResources().getColor(res);
	}
	
	private OnStopListener mProgressBarStopListener = new OnStopListener() {
		
		@Override
		public void onStop() {
			if (!isShowLoadFail() && !isShowUpLoadfail()){
				setLoadVideoNumber(mNum);
				mLoadSuccView.setAlpha(1f);
				if (mNum == 0) {
					mLoadSuccView.animate().alpha(0).setStartDelay(NUM_DURATION).setDuration(500).setListener(mScanningAnim);
				} else {
					switchText(mCity, NUM_DURATION);
				}
			} else {
				mLoadSuccView.setAlpha(0);
			}
		}
	};

	private void switchText(final CharSequence text, long delay){
		if (TextUtils.isEmpty(text)) return;
		ObjectAnimator animator = ObjectAnimator.ofFloat(mLoadSuccText, "alpha", 1.0f, 0.3f, 1.0f);
		animator.setStartDelay(delay);
		animator.setDuration(SWITCH_TEXT_DURATION);
		animator.start();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLoadSuccText.setText(text);
			}
		}, delay + SWITCH_TEXT_DURATION / 2);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_status_upload_fail:
			if (mListener != null){
				mListener.OnReUpload();
			}
			break;

		default:
			break;
		}
	}
	
	private Animator.AnimatorListener mScanningAnim = new Animator.AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			if (mUploadFail){
				mUploadFail = false;
				showUploadFail();
			}
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnRetryListener mRetryListener = new OnRetryListener() {
		
		@Override
		public void onRetry() {
			hideLoadFail();
			if (mListener != null){
				mListener.OnRetry();
			}
		}
	};
	
	private Listener mListener;
	
	public void setListener(Listener l){
		mListener = l;
	}
	
	public interface Listener {
		void OnRetry();
		void OnReUpload();
	}

}
