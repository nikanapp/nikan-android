package com.bloomlife.videoapp.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.TextView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.android.view.TitleBar;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.view.SuperToast;

/**
 * 
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * @date 2014年8月18日 下午2:52:03
 */
public class AboutActivity extends BaseActivity {

	@ViewInject(id = R.id.userid)
	private TextView mUserId;
	
	@ViewInject(id = R.id.about_titlebar)
	private TitleBar titleBar;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView versionTextView = (TextView) findViewById(R.id.versionNameTextView);
		try {
			String versionName = getString(R.string.activity_about_version) + "\n"
					+ this.getApplicationContext()
							.getPackageManager()
							.getPackageInfo(
									this.getApplicationContext()
											.getPackageName(), 0).versionName
					+ getString(R.string.activity_about_app_version_name_suffix);
			versionTextView.setText(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TitleBar titleBar = (TitleBar) findViewById(R.id.about_titlebar);
		titleBar.setOnTitleBarListener(titleBarListener);
		titleBar.setLeftTextLeftMargin(UiUtils.dip2px(getApplicationContext(), -25));
		
		mUserId.setText(CacheBean.getInstance().getLoginUserId());
		mUserId.setOnLongClickListener(new OnLongClickListener() {
	
				@SuppressLint("NewApi")
				@Override
				public boolean onLongClick(View v) {
					
					if(Integer.parseInt(android.os.Build.VERSION.SDK)>=android.os.Build.VERSION_CODES.HONEYCOMB){
						ClipboardManager copy = (ClipboardManager) AboutActivity.this
								.getSystemService(Context.CLIPBOARD_SERVICE);
						copy.setText(mUserId.getText());
						
				}else{
					android.text.ClipboardManager textCopy =(android.text.ClipboardManager) AboutActivity.this
					.getSystemService(Context.CLIPBOARD_SERVICE);
					textCopy.setText(mUserId.getText());
				}
					new SuperToast(getApplicationContext(), getString(R.string.activity_about_copy_tips));
					return true ;
				}
			});
			
	}


	private TitleBar.OnTitleBarListener titleBarListener = new TitleBar.OnTitleBarListener() {
		
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
