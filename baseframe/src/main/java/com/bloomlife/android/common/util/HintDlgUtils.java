//package com.bloomlife.android.common.util;
//
//import org.apache.commons.logging.Log;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.view.Gravity;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.TextView;
//
//import com.bloomlife.android.R;
//
//public class HintDlgUtils {
//	private static onDismissListener mListener;
//	
//	public static void setOnDismissListener(onDismissListener listener){
//		mListener = listener;
//	}
//	
//	
//	public static void showPopUp(Context ctx,View view,String txt , int time ) {  
//		showPopUp(ctx,view,txt,time,null);
//	}
//	public static void showPopUp(Context ctx,View view,String txt) {  
//		showPopUp(ctx,view,txt,null);
//	}
//	public static void showPopUp(Context ctx,View view,String txt,final Callback callback) {  
//		showPopUp(ctx, view ,txt , 2000 , callback);
//    }  
//	
//	/****
//	 * 一般提示，没有感叹号
//	 * @param ctx
//	 * @param view
//	 * @param txt
//	 * @param time
//	 * @param callback
//	 */
//	public static void showPopUpNotice(Context ctx,View view,String txt) {  
//		showPopUpNotice(ctx, view, txt,null);
//	}
//	
//	public static void showPopUpNotice(Context ctx,View view,String txt,final Callback callback) {  
//		showPopUpNotice(ctx, view, txt, 2000,callback);
//	}
//	
//	public static void showPopUpNotice(Context ctx,View view,String txt, int time ,final Callback callback) {  
//		 makeDlg(ctx, view, txt, time, false ,callback);
//	}
//	
//	public static void showPopUp(Context ctx,View view,String txt, int time ,final Callback callback) {  
//        makeDlg(ctx, view, txt, time, true ,callback);
//    }
//
//	/**
//	 * @param ctx
//	 * @param view
//	 * @param txt
//	 * @param time
//	 * @param callback
//	 */
//	private static final Log log = LogFactory.getLog(HintDlgUtils.class);
//	private static void makeDlg(Context ctx, View view, String txt, int time,boolean warn  , final Callback callback) {
//	
//		LinearLayout layout = new LinearLayout(ctx); 
//        final TextView tv = new TextView(ctx);  
//        tv.setBackgroundColor(Color.TRANSPARENT);
//        tv.setTextColor(Color.WHITE);
//        tv.setSingleLine(false);
//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//        
//        // 提示框的长和高（默认是带警告的图片的长和高）
//     	int popupWindowWidth  = UiUtils.dip2px(ctx, 145);
//        int popupWindowHeight = UiUtils.dip2px(ctx, 110);
//        if (warn) {
//        	// 设置背景图
//        	layout.setBackgroundResource(R.drawable.sys_dialog_bg);
//        	// 设置文字的文字
//        	params.gravity = Gravity.BOTTOM;
//            params.bottomMargin = UiUtils.dip2px(ctx, 5);
//            params.leftMargin = UiUtils.dip2px(ctx, 5);
//		}else {
//			// 设置背景图
//			layout.setBackgroundColor(ctx.getResources().getColor(R.color.ghou_lite_blue));
//			// 不带警告图片，设置提示框的高度和文字的位置
//			popupWindowWidth = LayoutParams.MATCH_PARENT;
//        	popupWindowHeight =  LayoutParams.WRAP_CONTENT;
//			params.gravity = Gravity.CENTER;
//	        params.setMargins(50, 50, 50, 50);
//		}
//        
//        tv.setLayoutParams(params);
//        tv.setGravity(Gravity.CENTER);
//        tv.setText(txt);
//        tv.setTextSize(14);
//        layout.addView(tv);
//        tv.setFocusable(true);
//        tv.requestFocus();
//        
///*        final PopupWindow popupWindow = new PopupWindow(layout,popupWindowWidth,popupWindowHeight,true);  
//          
//        popupWindow.setFocusable(true);  
//        popupWindow.setOutsideTouchable(false); 
//        popupWindow.setBackgroundDrawable(new BitmapDrawable(ctx.getResources()));  
//        if(view==null){
//        	log.info("view is null!!!!!!!!!!!!!!!!!!!!");
//        }else{
//        	log.info("view is not null!!!!!!!!!!!!!!!!!!");
//        	popupWindow.showAtLocation(view,Gravity.CENTER,0,0);	
//        }
//        
//        new Handler().postDelayed(new Runnable(){public void run(){
//        	if(popupWindow.isShowing()){
//        		popupWindow.dismiss();
//        		if(mListener!=null)mListener.DismissDialog();
//        	}
//        	if(callback!=null){
//        		callback.call(null);
//        	}
//        }}, time);*/
//        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//        builder.setCancelable(true);
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams param = window.getAttributes();
//        param.dimAmount = 0.0f;
//        param.width = UiUtils.dip2px(ctx, 145);
//        param.height = UiUtils.dip2px(ctx, 110);
//        window.setAttributes(param);
//        dialog.setContentView(layout);
//        
//        new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(dialog.isShowing()){
//					dialog.dismiss();
//					if(mListener!=null)mListener.DismissDialog();
//				}
//				if(callback!=null){
//					callback.call(callback);
//				}
//			}
//		}, time);
//	}  
//	
//	
//	
//
//	public interface onDismissListener{
//		public void DismissDialog();
//	}
//	
//}
