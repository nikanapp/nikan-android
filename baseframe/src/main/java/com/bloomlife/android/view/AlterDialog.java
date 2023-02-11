/**
 * 
 */
package com.bloomlife.android.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bloomlife.android.R;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-9-4 下午12:01:16
 */
public class AlterDialog {

	public static  void showDialog(Context context ,final String title ,final OnClickListener clickListener) {
		showDialog(context,R.layout.view_alertdialog, title, getString(context, R.string.dialog_cancel), getString(context, R.string.dialog_confirm), clickListener);
	}
	
	public static  void showDialog(Context context ,int laytou,final String title ,final OnClickListener clickListener) {
		showDialog(context,laytou, title, getString(context, R.string.dialog_cancel), getString(context, R.string.dialog_confirm), clickListener);
	}
	
	private static String getString(Context context, int res){
		return context.getResources().getString(res);
	}
	
	public static  void showDialog(Context context ,String title ,final String cancleText ,final String confirmText,final OnClickListener clickListener) {
		showDialog(context, R.layout.view_alertdialog, title, cancleText, confirmText, clickListener);
	}
	
	/**
	 * 
	 * @param context
	 * @param laytou	自定义的样式。layout中需要有title ，cancle , confirm 三个id分别是dialog的三个view
	 * @param clickListener
	 */
	public static  void showDialog(Context context , int layout ,String title ,final String cancleText ,final String confirmText,final OnClickListener clickListener) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		dlg.setContentView(layout);
		
		((TextView)dlg.findViewById(R.id.title)).setText(title);
		
		// 为确认按钮添加事件,执行退出应用操作
		TextView cancle = (TextView) dlg.findViewById(R.id.cancle);
		cancle.setText(cancleText);
		cancle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.cancel();
			}
		});

		// 关闭alert对话框架
		TextView confirm = (TextView) dlg.findViewById(R.id.confirm);
		confirm.setText(confirmText);
		confirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.cancel();
				clickListener.onClick(v);
			}
		});
	}
}
