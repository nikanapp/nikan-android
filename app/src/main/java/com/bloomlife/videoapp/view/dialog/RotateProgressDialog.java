/**
 * 
 */
package com.bloomlife.videoapp.view.dialog;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.view.GlobalProgressBar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.Gravity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月16日 下午3:30:02
 */
public class RotateProgressDialog extends Dialog {
	
	public static final String TAG = GlobalProgressBar.class.getSimpleName();

	private OnDismissListener mListener;
	private GlobalProgressBar mProgressBar;
	
	public RotateProgressDialog(Context context) {
		super(context, R.style.RotationProgressdDialog);
		init(context);
	}

	public RotateProgressDialog(Context context, int theme) {
		super(context, R.style.RotationProgressdDialog);
		init(context);
	}

	private void init(Context context){
		mProgressBar = new GlobalProgressBar(context);
		setContentView(mProgressBar);
		getWindow().setGravity(Gravity.CENTER);
		setCanceledOnTouchOutside(true);
	}
	
	@Override
	public void hide() {
		super.hide();
		mProgressBar.stopAnimator();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mProgressBar.stopAnimator();
		if (mListener != null){
			mListener.onDismiss(this);
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		mProgressBar.stopAnimator();
	}
	
	@Override
	public void show() {
		super.show();
		mProgressBar.startAnimator();
	}

	@Override
	public void setOnDismissListener(OnDismissListener listener) {
//		super.setOnDismissListener(listener);
		mListener = listener;
	}
	
}
