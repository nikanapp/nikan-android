/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bloomlife.videoapp.R;


/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-11  下午4:54:19
 */
public class ReportOptionView {
	
	
	private static final int[] rid = {R.id.zhengzhi,R.id.seqing,R.id.zhepian,R.id.guanggao} ;

	/**
	 * 
	 * @param context
	 * @param laytou	自定义的样式。layout中需要有title ，cancle , confirm 三个id分别是dialog的三个view
	 * @param clickListener
	 */
	public static AlertDialog showDialog(Context context ,final  ReportListener clickListener) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		dlg.setContentView(R.layout.view_report_option);
		for (int i = 0 , n=rid.length; i < n; i++) {
			dlg.findViewById(rid[i]).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					dlg.dismiss();
					if(view instanceof TextView){
						if(clickListener!=null)clickListener.onClick(((TextView)view).getText().toString());
					}
				}
			});
			
		}
		dlg.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(clickListener!=null)clickListener.onDismiss(dialog);
			}
		});
		return dlg;
	}
	
	
	public interface ReportListener{
		int TYPE_VIDEO   = 1;
		int TYPE_COMMENT = 2;
		
		void onClick(String content);
		
		void onDismiss(DialogInterface dialog);
	}
	
}
