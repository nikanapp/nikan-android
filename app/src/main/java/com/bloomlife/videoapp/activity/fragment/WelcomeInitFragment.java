/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.SpalshActivity;
import com.bloomlife.videoapp.activity.UserAgreementActivity;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.DefaultAnimatorListener;
import com.bloomlife.videoapp.manager.LocationManager;
import com.bloomlife.videoapp.view.GenderSelectView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.List;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-3-12 上午10:27:56
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class WelcomeInitFragment extends AbstractWelcomeFragment implements OnClickListener{

	public static final String TAG = WelcomeInitFragment.class.getSimpleName();

	/** 是否有重播视频的按钮 **/
	public static final String INTENT_HAS_REVERSE = "hasReverse";
	
	@ViewInject(id=R.id.activity_welcome_user, click=ViewInject.DEFAULT)
	private TextView userProtocol;
	
	@ViewInject(id=R.id.activity_welcome_btn_tick, click=ViewInject.DEFAULT)
	private View enter;
	
	@ViewInject(id=R.id.activity_welcome_btn_reverse, click=ViewInject.DEFAULT)
	private TextView reverse;
	
	@ViewInject(id=R.id.progressLayout)
	private LinearLayout linearLayout ;
	
	@ViewInject(id=R.id.progressbar)
	private ProgressBar progressBar ;
	
	@ViewInject(id=R.id.inittext)
	private TextView textView;
	
	@ViewInject(id=R.id.activity_welcome_splash_logo)
	private ImageView mSplashLogo;
	
	@ViewInject(id=R.id.splash_layout)
	private ViewGroup mSplashLayout;
	
	@ViewInject(id=R.id.genderSelect)
	private GenderSelectView genderSelectView;

	@ViewInject(id=R.id.activity_welcome_btn_layout)
	private ViewGroup mSplashleBtnLayout;

    private Handler mHandle = new Handler();

	private boolean mHasReverseBtn = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		View layout = inflater.inflate(R.layout.fragment_welcome_init, container, false);
		FinalActivity.initInjectedView(this, layout);
		initLayout();
		makeSplashleAnim();
		return layout;
	}

	private void initLayout(){
		if (getActivity() == null) return;
		userProtocol.setText(Html.fromHtml("<u>" + getString(R.string.welcome_user_text) + "</u>"));
		enter.setOnTouchListener(mEnterBtnTouch);
		if (getArguments() != null){
			mHasReverseBtn = getArguments().getBoolean(INTENT_HAS_REVERSE, true);
		}
		if (mHasReverseBtn){
			reverse.setVisibility(View.VISIBLE);
		} else {
			reverse.setVisibility(View.GONE);
		}
	}
	
	private OnTouchListener mEnterBtnTouch = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				enter.animate().scaleX(0.7f).scaleY(0.7f);
				break;
				
			case MotionEvent.ACTION_UP:
				enter.animate().scaleX(1.0f).scaleY(1.0f).setListener(mEnterClick);
				break;

			default:
				break;
			}
			return false;
		}
	};
	
	private Animator.AnimatorListener mEnterClick = new DefaultAnimatorListener() {
		
		@Override
		public void onAnimationEnd(Animator animation) {
			linearLayout.setVisibility(View.VISIBLE);
			((SpalshActivity)getActivity()).syncServerParam(true);
		}
	};
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_welcome_btn_reverse:
			FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.activity_spalsh_layout, new WelcomeVideoFragment());
			fragmentTransaction.commit();
			break;
			
		case R.id.activity_welcome_btn_tick:
			break;
			
		case R.id.activity_welcome_user:
			getActivity().startActivityForResult(new Intent(getActivity(), UserAgreementActivity.class), SpalshActivity.REQUEST_USER_AGREEMENT);
			break;

		default:
			break;
		}
	}

	public void dismissLoadLayout(){
		progressBar.setVisibility(View.GONE);
		textView.setText(R.string.fragment_welcome_init_forwarding);
		mSplashleBtnLayout.setVisibility(View.INVISIBLE);

		mSplashleAmin.start();

        mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				mCallback.welcomeStartToMainActivity();
			}
		}, 350);
	}
	
	public void hideInitProgress(){
		linearLayout.setVisibility(View.INVISIBLE);
	}
	
	private AnimatorSet mSplashleAmin;
	private void makeSplashleAnim(){
		mSplashleAmin = new AnimatorSet();
		int px = (int) (AppContext.deviceInfo.getScreenHeight()*1.3);
		mSplashleAmin.playTogether(
                ObjectAnimator.ofFloat(mSplashLogo, "scaleX", 1, 10),
                ObjectAnimator.ofFloat(mSplashLogo, "scaleY", 1, 10),
                ObjectAnimator.ofFloat(mSplashLogo, "translationY", 0, -px),
                ObjectAnimator.ofFloat(mSplashLogo, "alpha", 1, 0)
        );
		mSplashleAmin.setDuration(400);
    }
	

}
