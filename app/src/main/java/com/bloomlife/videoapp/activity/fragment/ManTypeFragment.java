/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.business.BusinessTask;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.DefaultAnimatorListener;
import com.bloomlife.videoapp.common.DefaultStartAnimatorListener;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.UpdateUserSexMessage;
import com.bloomlife.videoapp.view.GenderScaleView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 性别选择页
 * @date 2015年4月8日 下午3:00:54
 */
public class ManTypeFragment extends Fragment implements View.OnClickListener{
	
	private static final String TAG = ManTypeFragment.class.getSimpleName();
	
	public static final int ANIM_DURATION = 400;
	
	@ViewInject(id=R.id.man_type_male, click=ViewInject.DEFAULT)
	private View mBtnMale;
	
	@ViewInject(id=R.id.man_type_female, click=ViewInject.DEFAULT)
	private View mBtnFemale;
	
	@ViewInject(id=R.id.fragment_type_sex_scale_next, click=ViewInject.DEFAULT)
	private View mBtnNext;
	
	@ViewInject(id=R.id.fragment_type_sex_scale_layout)
	private ViewGroup mSexScaleLayout;
	
	@ViewInject(id=R.id.fragment_type_select_layout)
	private ViewGroup mSexSelectLayout;
	
	
	@ViewInject(id=R.id.fragment_type_sex_scale_animview)
	private GenderScaleView mSexScaleView;

	@ViewInject(id=R.id.fragment_type_man, click=ViewInject.DEFAULT)
	private ViewGroup mMainLayout;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_man_type, container, false);
		FinalActivity.initInjectedView(this, layout);
		initLayout();
		return layout;
	}
	
	private void initLayout(){
		if (Utils.isFirstUseApp(getActivity())){
			Utils.setNoFirstUseApp(getActivity());
			getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}
	
	private void finish(){
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getChildFragmentManager().beginTransaction().remove(ManTypeFragment.this).commit();
		if (mFinishListener != null){
			mFinishListener.finish();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.man_type_male:
			setUserSex(Video.MALE);
			hideSexSelectLayout();
			break;
			
		case R.id.man_type_female:
			setUserSex(Video.FEMALE);
			hideSexSelectLayout();
			break;
			
		case R.id.fragment_type_sex_scale_next:
			finish();
			break;
		case R.id.fragment_man_type_layout:
			break;
		default:
			break;
		}
	}
	
	private void setUserSex(int sex){
		AppContext.getSysCode().setSex(sex);
		new BusinessTask(getActivity(), new UpdateUserSexMessage(sex)).execute();
	}
	
	private void hideSexSelectLayout(){
		mSexSelectLayout.animate().setListener(new DefaultAnimatorListener() {
			
			@Override
			public void onAnimationEnd(Animator animation) {
				mSexSelectLayout.setVisibility(View.INVISIBLE);
				showSexScaleViewLayout();
			}
		}).alpha(0);
	}
	
	private void showSexScaleViewLayout(){
		mSexScaleLayout.animate().setListener(new DefaultStartAnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				mSexScaleLayout.setAlpha(0);
				mSexScaleLayout.setVisibility(View.VISIBLE);
				mSexScaleView.progress(AppContext.getSysCode().getProportionmale() / 100f);
				mBtnNext.setScaleX(0);
				mBtnNext.setScaleY(0);
				mBtnNext.animate().scaleX(1).scaleY(1);
			}
		}).alpha(1);
	}
	
	private FinishListener mFinishListener;
	
	public void setFinishListener(FinishListener l){
		mFinishListener = l;
	}
	
	public interface FinishListener{
		void finish();
	}

}
