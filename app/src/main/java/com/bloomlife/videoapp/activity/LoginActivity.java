package com.bloomlife.videoapp.activity;

import net.tsz.afinal.annotation.view.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.model.message.SynchronicMessage;
import com.bloomlife.videoapp.model.result.SynchronicResult;

public class LoginActivity extends BaseActivity {
	
	@ViewInject(id=R.id.login_button, click=ViewInject.DEFAULT)
	Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
//		SynchronicData();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	private void SynchronicData(){
		Volley.addToTagQueue(new MessageRequest(new SynchronicMessage(), mListener));
	}

	private MessageRequest.Listener<SynchronicResult> mListener = new MessageRequest.Listener<SynchronicResult>(){
	    @Override
	    public void success(SynchronicResult result) {
			AppContext.topics = result.getTopics();
			CacheBean cacheBean = CacheBean.getInstance();
			cacheBean.setLoginUserId(getApplicationContext(), result.getUserid());
			cacheBean.putInt(getApplicationContext(), CacheKeyConstants.KEY_MAX_LEVEL, result.getMaxlevel());
			cacheBean.putInt(getApplicationContext(), CacheKeyConstants.KEY_MIN_LEVEL, result.getMinlevel());
			cacheBean.putInt(getApplicationContext(), CacheKeyConstants.KEY_DEFAULT_LEVEL, result.getDefaultlevel());
			cacheBean.putString(getApplicationContext(), CacheKeyConstants.KEY_TOPICS, JSONObject.toJSONString(result.getTopics()));
	    }
	};

}
