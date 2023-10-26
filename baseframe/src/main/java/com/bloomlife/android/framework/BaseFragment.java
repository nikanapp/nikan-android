/**
 * 
 */
package com.bloomlife.android.framework;

import androidx.core.app.Fragment;

import com.cyou.cyanalyticv3.CYAgent;
import com.umeng.analytics.MobclickAgent;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-12-9  下午4:52:40
 */
public abstract class BaseFragment extends Fragment {
	
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
	    MobclickAgent.onResume(this.getActivity());
	    CYAgent.onPageStart(this.getClass().getSimpleName());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getSimpleName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this.getActivity());
		 CYAgent.onPageEnd(this.getClass().getSimpleName());
	}
}
