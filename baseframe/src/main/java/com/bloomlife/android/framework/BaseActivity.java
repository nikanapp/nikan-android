/**
 * 
 */
package com.bloomlife.android.framework;

import net.tsz.afinal.FinalActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.android.volley.toolbox.Volley;
import com.cyou.cyanalyticv3.CYAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-6-2  下午12:03:28
 */
public class BaseActivity extends FinalActivity{


	private Object mActivityTag;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((AppContext)getApplication()).addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mActivityTag = Volley.getTag();
	}

	@Override
	public void finish() {
		super.finish();
		Volley.cancelAll(mActivityTag);
	}

	public void onClick(View v) {
		
	}
	
	protected void onResume() {
	    super.onResume();
		Volley.setTag(mActivityTag);
	    MobclickAgent.onPageStart(this.getClass().getSimpleName()); //统计页面
	    MobclickAgent.onResume(this);          //统计时长
	    CYAgent.onPageStart(this.getClass().getSimpleName());
		CYAgent.onResume(this);
	}

	protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(this.getClass().getSimpleName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	    CYAgent.onPageEnd(this.getClass().getSimpleName());
		CYAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
}
