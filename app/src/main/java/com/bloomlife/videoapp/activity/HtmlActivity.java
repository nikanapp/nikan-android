/**
 * 
 */
package com.bloomlife.videoapp.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.view.GlobalProgressBar;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * @parameter INTENT_URL String 传一个网页链接进来。
 * @date 2014年12月30日 下午7:03:31
 */
public class HtmlActivity extends BaseActivity implements View.OnClickListener{
	
	public static final String INTENT_URL = "url";
	
	@ViewInject(id=R.id.activity_html_webview)
	private WebView mWebView;

	@ViewInject(id=R.id.activity_html_btn_close, click=ViewInject.DEFAULT)
	private View mBtnClose;
	
	@ViewInject(id=R.id.activity_html_progressbar)
	private GlobalProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		String url = getIntent().getStringExtra(INTENT_URL);
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
		if (url != null) {
			mWebView.loadUrl(url);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()){
			case R.id.activity_html_btn_close:
				finish();
				break;
		}
	}
	
	class MyWebChromeClient extends WebChromeClient{

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress < 100){
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
			super.onProgressChanged(view, newProgress);
		}


	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}
	}


}
