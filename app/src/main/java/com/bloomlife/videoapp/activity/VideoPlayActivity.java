/**
 * 
 */
package com.bloomlife.videoapp.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.AnonVideoPlayFragment;
import com.bloomlife.videoapp.model.Video;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月1日 下午7:23:58
 */
public class VideoPlayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_play);
		Bundle bundle = new Bundle();
		bundle.putParcelable(AnonVideoPlayFragment.INTENT_VIDEO, new Video());
		Fragment fragment = new AnonVideoPlayFragment();
		fragment.setArguments(bundle);
		getFragmentManager().beginTransaction().add(R.id.activity_video_play_layout, fragment).commit();
	}
	
}
