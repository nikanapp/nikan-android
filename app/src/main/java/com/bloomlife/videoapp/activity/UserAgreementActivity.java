/**
 * 
 */
package com.bloomlife.videoapp.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;

import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.android.view.TitleBar;
import com.bloomlife.android.view.TitleBar.OnTitleBarListener;
import com.bloomlife.videoapp.R;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月3日 下午7:10:07
 */
public class UserAgreementActivity extends BaseActivity {

	
	@ViewInject(id=R.id.user_agreement_titlebar)
	private TitleBar mTitlebar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_agreement);
		mTitlebar.setOnTitleBarListener(mTitleBarListener);
	}
	
	private OnTitleBarListener mTitleBarListener = new OnTitleBarListener() {
		
		@Override
		public void onTitleClick() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onRightClick() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLeftClick() {
			finish();
		}
	};

}
