///**
// * 
// */
//package com.bloomlife.android.common.util;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.location.LocationManager;
//import android.provider.Settings;
//import android.provider.SyncStateContract.Constants;
//import android.view.View;
//import android.view.Window;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.TextView;
//
//import com.bloomlife.android.R;
//
///**
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-5-27  下午3:01:29
// */
//public class LocationUtil {
//
//	public static boolean isGpsOpen(Context content){
//		LocationManager locationManager = (LocationManager)content.getSystemService(Context.LOCATION_SERVICE);  
//		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
//	}
//	
//	public static  boolean ALERT = true ;
//	
//	public static void openGpsSetting(final Context ctx ){
//		final AlertDialog dlg = new AlertDialog.Builder(ctx).create();
//		 dlg.show();
//		 Window window = dlg.getWindow();
//		        // *** 主要就是在这里实现这种效果的.
//		        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
//		 window.setContentView(R.layout.dialog_alert_gps);
//		 
//		 
//			CheckBox checkBox = (CheckBox) window.findViewById(R.id.noalert);
//			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					if(isChecked) ALERT = false;
//					else ALERT = true ;
//							
//				}
//			});
//		 
//		 
//		        // 为确认按钮添加事件,执行退出应用操作
//		 TextView ok = (TextView) window.findViewById(R.id.confirm);
//		 ok.setOnClickListener(new View.OnClickListener() {
//		  public void onClick(View v) {
//			  if(!ALERT) cancleAlert(v.getContext());
//				  dlg.dismiss();
//					Intent intent = new Intent();  
//					intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
//					ctx.startActivity(intent);
//		  }
//		 });
//		 
//		        // 关闭alert对话框架
//		 TextView cancel = (TextView) window.findViewById(R.id.cancle);
//		        cancel.setOnClickListener(new View.OnClickListener() {
//		   public void onClick(View v) {
//			   if(!ALERT) cancleAlert(v.getContext());
//				dlg.dismiss();
//		  }
//		   });
//		
//	}
//	
//	private static void cancleAlert(Context ctx){
//		ctx.getSharedPreferences(Constants.APP_GAOSHOU_KEY,Context.MODE_PRIVATE).edit().putBoolean(Constants.KEY_OPEN_GPS, true).commit();
//	}
//}
