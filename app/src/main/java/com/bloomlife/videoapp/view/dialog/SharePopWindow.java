package com.bloomlife.videoapp.view.dialog;

import com.bloomlife.videoapp.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public abstract class SharePopWindow implements OnClickListener, OnCancelListener, OnShowListener{
	private Dialog dialog;
	
	public SharePopWindow(Context ctx) {
		dialog = new AlertDialog.Builder(ctx).create();
		dialog.setCanceledOnTouchOutside(true);
		initDialog(createView(ctx));
	}

	protected abstract View createView(Context ctx);
	
	private void initDialog(View view){
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.PicturePopWindowStyle); // 添加动画
		dialog.show();
		dialog.setContentView(view);
		dialog.setOnCancelListener(this);
		dialog.setOnShowListener(this);
	}

	public void dismiss() {
		this.dialog.dismiss();
	}

	public void show() {
		this.dialog.show();
	}

	@Override
	public void onClick(View view) {
		int tag = Integer.parseInt(view.getTag().toString()); 
		listener.onClickBtn(tag);
		dialog.dismiss();
	}

	public interface SharePopWindowListener {
		void onShow();
		void onDismiss();
		void onClickBtn(int tag);
	}

	private SharePopWindowListener listener;

	public void setPopListener(SharePopWindowListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (listener != null){
			listener.onDismiss();
		}
	}

	@Override
	public void onShow(DialogInterface dialog) {
		if (listener != null){
			listener.onShow();
		}
	}
}
