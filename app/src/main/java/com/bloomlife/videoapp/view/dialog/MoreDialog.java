/**
 *
 */
package com.bloomlife.videoapp.view.dialog;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年4月8日 下午5:41:57
 */
public class MoreDialog {

	public enum Type {SHARE, REPORT, CLEAR_DYNAMIC, SHIELD_DYNAMIC}

	public static Dialog show(Context context, final OnClickListener l, String videoUserId, Type... types){
		final Dialog dialog = new AlertDialog.Builder(context).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (l != null){
					l.onCancel();
				}
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (l != null){
					l.onDismiss();
				}
			}
		});
		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				if (l != null) {
					l.onShow();
				}
			}
		});
		dialog.show();
		dialog.setContentView(R.layout.dialog_video_more);

		final TextView share = (TextView) dialog.findViewById(R.id.dialog_video_more_share);
		final TextView report = (TextView) dialog.findViewById(R.id.dialog_video_more_report);
		final TextView clearDynamic = (TextView) dialog.findViewById(R.id.dialog_video_more_clear);
		final TextView shieldDynamic = (TextView) dialog.findViewById(R.id.dialog_video_more_shiel);
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (l != null){
					switch (v.getId()) {
						case R.id.dialog_video_more_share:
							l.onClick(share, Type.SHARE);
							break;

						case R.id.dialog_video_more_report:
							l.onClick(report, Type.REPORT);
							break;

						case R.id.dialog_video_more_clear:
							l.onClick(clearDynamic, Type.CLEAR_DYNAMIC);
							break;

						case R.id.dialog_video_more_shiel:
							l.onClick(shieldDynamic, Type.SHIELD_DYNAMIC);
							break;

						default:
							break;
					}
				}
				dialog.dismiss();
			}
		};
		share.setOnClickListener(listener);
		report.setOnClickListener(listener);
		clearDynamic.setOnClickListener(listener);
		shieldDynamic.setOnClickListener(listener);
		// 只显示需要的部分
		share.setVisibility(View.GONE);
		report.setVisibility(View.GONE);
		clearDynamic.setVisibility(View.GONE);
		shieldDynamic.setVisibility(View.GONE);
		for (Type t:types){
			switch (t){
				case SHARE:
					share.setVisibility(View.VISIBLE);
					break;
				case REPORT:
					report.setVisibility(View.VISIBLE);
					break;
				case CLEAR_DYNAMIC:
					clearDynamic.setVisibility(View.VISIBLE);
					break;
				case SHIELD_DYNAMIC:
					shieldDynamic.setVisibility(View.VISIBLE);
					break;
			}
		}
		// 如果是当前用户的视频则隐藏举报按钮
		if (CacheBean.getInstance().getLoginUserId().equals(videoUserId)){
			report.setVisibility(View.GONE);
		}
		return dialog;
	}

	public static Dialog show(Context context, final OnClickListener l, String videoUserId){
		return show(context, l, videoUserId, Type.SHARE, Type.REPORT, Type.CLEAR_DYNAMIC, Type.SHIELD_DYNAMIC);
	}

	public interface OnClickListener{
		void onClick(TextView tv, Type type);
		void onDismiss();
		void onShow();
		void onCancel();
	}

}