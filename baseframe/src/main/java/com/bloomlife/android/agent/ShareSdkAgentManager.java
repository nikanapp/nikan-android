///**
// * 
// */
//package com.bloomlife.android.agent;
//
//import java.util.HashMap;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.Log;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
//
//import com.bloomlife.android.agent.shorturl.ShortUrlInterface;
//import com.bloomlife.android.agent.shorturl.ShortUrlUtil;
//import com.bloomlife.android.agent.shorturl.UrlObject;
//import com.bloomlife.android.framework.AppContext;
//
///**
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-8-5  上午11:59:21
// */
//public class ShareSdkAgentManager {
//	
//	public static final String SHARE_PREFIX = 	 "http://highand.me:8000/sharework.html?" ;
//	
//	public static final String TAG = "ShareSdkAgentManager";
//	
//	/**
//	 * 分享秘籍
//	 * @param longUrl
//	 */
//	public static  void shareWork(final Activity activity ,final ShareInfo info){
//		final CustomProgressDialog pdialog = CustomProgressDialog.createDialog(activity);
//		pdialog.show();
//		ShortUrlUtil shorturl = new ShortUrlUtil(info.fullUrl);
//		shorturl.setShortUrlInterface(new ShortUrlInterface() {
//			
//			@Override
//			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
//				pdialog.dismiss();
//				info.shareUrl = urlobject.getUrl_short();
//				ShareSDKUtil.share(activity,info);
//			}
//		});
//		shorturl.execute();
//	}
//	
//	
//	
//	// 分享的连接
//	public  static String getShaerWorkURL(String workid) {
//		return "format=json&clientid=2&method=com.gf.fish.gs.queryCourse&userid=" + AppContext.getLoginUserId() + "&workid=" + workid;
//	}	
//	
//	
//	public static void autoShareWork(final WorkBean workBean , final Context ctx){
//		ShortUrlUtil shorturl = new ShortUrlUtil(ShareInfo.SHARE_URL_PREFIX +getShaerWorkURL(workBean.getWorkId()));
//		shorturl.setShortUrlInterface(new ShortUrlInterface() {
//			
//			@Override
//			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
//				String shareUrl = urlobject.getUrl_short();
//				ShareParams sp = new ShareParams();
//				String text =ShareSDKUtil.makeWorkShareWork(workBean.getTitle(), AppContext.getUser(ctx).getUsername())+ " 查看秘籍:"+shareUrl+" @高手_Highand";
//				Log.e("sharturl ", text);
//				sp.setText(text);
//				sp.setImagePath(workBean.getImage());
//	
//				Platform weibo = ShareSDK.getPlatform(ctx, SinaWeibo.NAME);
//				
//				weibo.setPlatformActionListener( new PlatformActionListener() {
//					
//					@Override
//					public void onError(Platform platform, int i, Throwable throwable) {
//						Log.e(TAG, "  auto share  failure ,   "+throwable);
//					}
//					
//					@Override
//					public void onComplete(Platform platform, int i, HashMap<String, Object> hashmap) {
//						Log.e(TAG, "  auto share  onComplete ,   "+hashmap);
//					}
//					
//					@Override
//					public void onCancel(Platform platform, int i) {
//						Log.e(TAG, "  auto share  onCancel ,   ");
//					}
//				}); // 设置分享事件回调
//				// 执行图文分享
//				weibo.share(sp);
//				Log.e(TAG, "  auto share  success  ");
//			}
//		});
//		shorturl.execute();
//		
//		
//		
//	}
//}
