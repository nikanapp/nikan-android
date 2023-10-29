/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import android.app.Activity;
import androidx.fragment.app.Fragment;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年6月24日 下午9:07:40
 */
public class AbstractWelcomeFragment extends Fragment {

	protected Callback mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (Callback) activity;
	}

	public interface Callback {
		void welcomeStartToMainActivity();
	}

}
