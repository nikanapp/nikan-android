package com.bloomlife.android.common.util;
///**
// * 
// */
//package com.bloomlife.gs.util;
//
//import java.lang.ref.WeakReference;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.Context;
//
//import com.bloomlife.gs.app.AppContext;
//import com.bloomlife.gs.exception.HttpException;
//import com.bloomlife.gs.message.CheckInMessage;
//import com.bloomlife.gs.network.HttpAccessor;
//import com.bloomlife.gs.network.NetworkAccessor;
//
///**
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-1-2  下午2:12:40
// */
//public class CheckInThread extends Thread{
//	
//	private int type ;
//	private WeakReference<Context> ctx ;
//	private Activity atx;
//	private Application aptx;
//	
///*	public CheckInThread(int type,Activity ctx){
//		this.type =type ;
//		this.ctx = new WeakReference<Context>(ctx);
//		this.atx = (Activity)ctx;
//		this.aptx = (Application) atx.getApplicationContext();
//	}
//	
//	public CheckInThread(int type,Application ctx){
//		this.type =type ;
//		this.ctx = new WeakReference<Context>(ctx);
////		this.atx = (Activity)ctx;
//		this.aptx = (Application) ctx;
//	}*/
//	
//	public CheckInThread(int type,Context ctx){
//		this.type = type ;
//		this.ctx = new WeakReference<Context>(ctx);
//		if (ctx instanceof Activity) {
//			this.aptx = (Application) ctx.getApplicationContext();
//		}else{
//			this.aptx = (Application) ctx;
//		}
//	}
//	
//	@Override
//	public void run() {
//		// 判断loginUseid是否为空，或者是-1，不执行上下线请求
//		String loginUseid = AppContext.getLoginUserId();
//		if (loginUseid.isEmpty() || loginUseid.equals("-1")) {
//			System.out.println("CheckInThread.run() 不用执行上下线请求 login user id is :"+loginUseid);
//			return;
//		}else {
//			NetworkAccessor accessor = new HttpAccessor(ctx.get());
//			try {
//				accessor.call(new CheckInMessage(type,aptx));
//			} catch (HttpException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//}
