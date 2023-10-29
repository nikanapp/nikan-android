/**
 * 
 */
package com.bloomlife.videoapp.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.model.message.ReportMessage;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.ReportOptionView.ReportListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月10日 上午11:42:00
 */
public class VideoReportListener implements ReportListener {

	private Activity mActivity;
	private String mVideoId;
	
	public VideoReportListener(Activity activity, String videoId){
		mActivity = activity;
		mVideoId = videoId;
	}
	
	public VideoReportListener(){
		
	}

	@Override
	public void onClick(String content) {
		ReportMessage message = new ReportMessage();
		message.setReportId(mVideoId);
		message.setReporType(getReportType(mActivity, content));
		message.setContentType(TYPE_COMMENT);
		Volley.addToTagQueue(new MessageRequest(message, new MessageRequest.Listener() {
			@Override
			public void success(ProcessResult result) {
				new SuperToast(mActivity, R.string.fragment_report_succ);
			}
		}));
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}
	
	protected int getReportType(Context context, String str){
		if(context == null) return 0;
		if(context.getString(R.string.report_text1).equals(str)) return 1;
		if(context.getString(R.string.report_text2).equals(str)) return 2;
		if(context.getString(R.string.report_text3).equals(str)) return 3;
		if(context.getString(R.string.report_text4).equals(str)) return 4;
		return 0;
	}
}
