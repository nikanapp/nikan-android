package com.bloomlife.videoapp.view.dialog;

import java.util.Locale;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class ShareAppWindow extends SharePopWindow implements OnClickListener {

	/**
	 * @param ctx
	 */
	public ShareAppWindow(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View createView(Context ctx) {
		View view = null;
		if (UIHelper.isZH()){
			view = LayoutInflater.from(ctx).inflate(R.layout.view_share_app_layout, null);
			view.findViewById(R.id.share_wechat).setOnClickListener(this);
			view.findViewById(R.id.share_wechatMoments).setOnClickListener(this);
			view.findViewById(R.id.share_sinaWeibo).setOnClickListener(this);
			view.findViewById(R.id.share_QZone).setOnClickListener(this);
			view.findViewById(R.id.share_renren).setOnClickListener(this);
			view.findViewById(R.id.share_message).setOnClickListener(this);
		} else {
			view = LayoutInflater.from(ctx).inflate(R.layout.share_popwindow_en, null);
			view.findViewById(R.id.share_facebook).setOnClickListener(this);
			view.findViewById(R.id.share_twitter).setOnClickListener(this);
		}
		return view;
	}
}
