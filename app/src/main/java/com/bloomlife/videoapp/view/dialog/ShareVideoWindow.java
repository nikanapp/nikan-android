/**
 * 
 */
package com.bloomlife.videoapp.view.dialog;

import java.util.Locale;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年6月23日 上午11:03:21
 */
public class ShareVideoWindow extends SharePopWindow {

	public ShareVideoWindow(Context ctx) {
		super(ctx);
	}

	@Override
	protected View createView(Context ctx) {
		View view = null;
		if (UIHelper.isZH()){
			view = LayoutInflater.from(ctx).inflate(R.layout.share_popwindow_zh, null);
			view.findViewById(R.id.share_wechat).setOnClickListener(this);
			view.findViewById(R.id.share_wechatMoments).setOnClickListener(this);
			view.findViewById(R.id.share_sinaWeibo).setOnClickListener(this);
			view.findViewById(R.id.share_QZone).setOnClickListener(this);
			view.findViewById(R.id.share_renren).setOnClickListener(this);
			view.findViewById(R.id.share_douban).setOnClickListener(this);
		} else {
			view = LayoutInflater.from(ctx).inflate(R.layout.share_popwindow_en, null);
			view.findViewById(R.id.share_facebook).setOnClickListener(this);
			view.findViewById(R.id.share_twitter).setOnClickListener(this);
		}
		return view;
	}

}
