/**
 * 
 */
package com.bloomlife.videoapp.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.model.CheckResult;
import com.bloomlife.videoapp.model.message.CheckMsg;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-19  下午5:46:43
 */
public class CheckActivity extends BaseActivity implements OnClickListener{

	@ViewInject(id=R.id.edittext, click=ViewInject.DEFAULT)
	private EditText editText;
	
	@ViewInject(id=R.id.check, click=ViewInject.DEFAULT)
	private Button check;
	
	@ViewInject(id=R.id.progressBar, click=ViewInject.DEFAULT)
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(StringUtils.isNotEmpty(editText.getText().toString())){
			Volley.addToTagQueue(new MessageRequest(new CheckMsg(editText.getText().toString()), mCheckReqListener));
		}
	}


	private MessageRequest.Listener<CheckResult> mCheckReqListener = new MessageRequest.Listener<CheckResult>(){
		@Override
		public void start() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void success(CheckResult result) {
			if(result.getState()==0){
				CacheBean.getInstance().putString(getApplicationContext(), SpalshActivity.KEY_FIRST_COME	, "1");
				setResult(Activity.RESULT_OK);
				finish();
			} else {
				UiHelper.shortToast(getApplicationContext(),"无法连接到服务器, resulecode :"+ result.getState());
			}
		}

		@Override
		public void error(VolleyError error) {
			super.error(error);
			UiHelper.shortToast(getApplicationContext(), "无法连接到服务器" + error.networkResponse.statusCode);
		}

		@Override
		public void finish() {
			progressBar.setVisibility(View.INVISIBLE);
		}
	};
}
