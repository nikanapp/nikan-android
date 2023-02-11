//package com.bloomlife.android.common.util;
//
//import java.util.HashMap;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Handler;
//import android.provider.SyncStateContract.Constants;
//import android.text.ClipboardManager;
//import android.widget.Toast;
//import cn.sharesdk.douban.Douban;
//import cn.sharesdk.evernote.Evernote;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.renren.Renren;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.sharesdk.wechat.moments.WechatMoments;
//
//import com.bloomlife.android.R;
//import com.bloomlife.android.agent.shorturl.ShortUrlInterface;
//import com.bloomlife.android.agent.shorturl.ShortUrlUtil;
//import com.bloomlife.android.agent.shorturl.UrlObject;
//import com.bloomlife.android.media.image.Utils;
//
//public class ShareSDKUtil {
//	
//	public static String AppShareImageUrl = "http://hh.highand.cn/appservice/share_logo.jpg";
//	public static String AppShareTextShort = "想学点高逼格技能？不知如何利用碎片时间？发现小而美良心App【高手】一枚！";
//	public static String AppShareText = "想学点高逼格技能？不知如何利用碎片时间？发现小而美良心App【高手】一枚！最轻便上传秘籍工具，最贴心分享交友模式，让你轻松点满各类高大上技能点。";
//	public static String AppShareTextD = AppShareText + "秀绝活，交朋友,就下载高手：";
//	public static String AppShareTitleShort = "秀绝活，交朋友。";
//	public static String AppShareTitle = "\"高手\"秀绝活，交朋友。";
//	
//	public static String makeWorkShareWork(String workName,String userName){
//		return "这个秘籍太具水准，太有范儿了！小伙伴们快来围观~~《"+workName+"》作者："+userName;
//	}
//	
//	/**
//	 * 分享一个网页信息到微信朋友圈(只有安装对应版本的微信才可以分享，如果没有安装，提示安装)
//	 * @param context
//	 * @param title				标题
//	 * @param text				文字内容
//	 * @param url				网页地址
//	 * @param imageBitmap		图片
//	 */
//	public static void shareToWechatMoments(final Context context,String title,String text,String url,Bitmap imageBitmap){
//		shareToPlatform(context, title, text, url, Utils.saveMyBitmap(imageBitmap), WechatMoments.NAME, false);
//	}
//	
//	/**
//	 * 分享一个网页信息到微信朋友(只有安装对应版本的微信才可以分享，如果没有安装，提示安装)
//	 * @param context
//	 * @param title				标题
//	 * @param text				文字内容
//	 * @param url				网页地址
//	 * @param imageBitmap		图片
//	 */
//	public static void shareToWechat(final Context context,String title,String text,String url,Bitmap imageBitmap){
//		String imagePath = "";
//		if (imageBitmap != null) {
//			imagePath = Utils.saveMyBitmap(imageBitmap);
//		}
//		shareToPlatform(context, title, text, url, imagePath, Wechat.NAME, false);
//	}
//	
//	/**
//	 * 分享一个网页信息到新浪微博(有客户端使用客户端分享，没有就使用页面分享)
//	 * @param context			
//	 * @param text				文字内容
//	 * @param url				网页地址
//	 * @param imageBitmap		图片	
//	 */
//	public static void shareToSinaWeibo(final Context context,String text,String url,Bitmap imageBitmap){
//		// 新浪微博需要把网页地址放到文字内容的后面
//		String imagePath = "";
//		if (imageBitmap != null) {
//			imagePath = Utils.saveMyBitmap(imageBitmap);
//		}
//		shareToPlatform(context, "", text, "", imagePath, SinaWeibo.NAME, false);
//	}
//
//	/**
//	 * 分享一个网页信息到一个平台
//	 * @param context
//	 * @param title				标题
//	 * @param text				文字内容
//	 * @param url				网页地址
//	 * @param imagePath			图片的本地path
//	 * @param platformName		平台名称
//	 * @param silent			是否直接分享
//	 */
//	public static void shareToPlatform(final Context context,String title,String text,String url,String imagePath,String platformName,boolean silent){
//		OnekeyShare	oks = new OnekeyShare();
//        // 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
//        oks.setTitle(title);
//        // 新浪微博没有url字段设置，需要把url合并到text的最后
//        oks.setText(text);
//        // url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
//        oks.setUrl(url);
//        oks.setImagePath(imagePath);
//        oks.setPlatform(platformName);
//        oks.setSilent(silent);
//        shareToPlatform(context, oks);
//	}
//	
//	/**
//	 * 分享一个网页信息到qq空间
//	 * @param context
//	 * @param title				标题
//	 * @param text				文字内容
//	 * @param shareUrl			分享的网页地址
//	 * @param imageUrl			分享的网络图片
//	 */
//	public static void shareToQzone(final Context context,String title,String text,String shareUrl,String imageUrl){
//		
//		OnekeyShare oks = new OnekeyShare();
//		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
//        oks.setSilent(false);
//        oks.setText(text);
//        oks.setTitle(title);
//        oks.setImageUrl(imageUrl);
//        oks.setTitleUrl(shareUrl);
//        oks.setSite(title);
//        oks.setPlatform(QZone.NAME);
//        oks.setSiteUrl(shareUrl);
//        shareToPlatform(context, oks);
//	}
//	
//	/**
//	 * 分享一个网页到豆瓣
//	 * @param context
//	 * @param text				标题
//	 * @param shareUrl			分享的网页地址
//	 * @param shareImage		分享的图片
//	 */
//	public static void shareToDouban(final Context context,String text,String shareUrl,Bitmap shareImage){
//		// 豆瓣不能分享网页，只能是分享图片和文字(可以把网址直接放到文字内容的后面)
//		OnekeyShare oks = new OnekeyShare();
//		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
//        oks.setSilent(false);
//        oks.setText(text);
//        oks.setImagePath(Utils.saveMyBitmap(shareImage));					// 需要申请权限
//        oks.setPlatform(Douban.NAME);
//		ShareSDKUtil.shareToPlatform(context, oks);
//	}
//	
//	public static void shareToRenren(final Context context,String title,String text,String shareUrl,String imageUrl){
//		OnekeyShare oks = new OnekeyShare();
//		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
//        oks.setSilent(false);
//        oks.setComment(title);								// 类似标题
//        oks.setText(text);
//        oks.setPlatform(Renren.NAME);
//        oks.setTitle(title);
//        oks.setTitleUrl(shareUrl);						// 跳转地址
//        oks.setImageUrl(imageUrl);
//		shareToPlatform(context, oks);
//	}
//	
//	/**
//	 * 分享一个网页信息到evernte
//	 * @param context
//	 * @param title				标题
//	 * @param text				文字内容
//	 * @param shareUrl			分享的网页地址
//	 * @param imageBitmap		分享的本地图片
//	 */
//	public static void shareToEvernote(final Context context,String title,String text,String shareUrl,Bitmap imageBitmap){
//		// 印象笔记不能分享网页，只能是分享图片和文字(可以把网址直接放到文字内容的后面)
//		OnekeyShare oks = new OnekeyShare();
//		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.about_logo,context.getString(R.string.app_name));
//        oks.setSilent(false);
//        oks.setTitle(title);
//        oks.setText(text);
//        oks.setImagePath(Utils.saveMyBitmap(imageBitmap));
//        oks.setPlatform(Evernote.NAME);
//		shareToPlatform(context, oks);		
//	}
//	
//	/**
//	 * 分享一个网页信息到一个平台
//	 * @param context
//	 * @param oks				分享实体
//	 */
//	public static void shareToPlatform(final Context context,OnekeyShare oks){
//		ShareSDK.initSDK(context.getApplicationContext());
//		final Handler handler = new Handler();
//        oks.setCallback(new PlatformActionListener() {
//			
//			@Override
//			public void onError(Platform arg0, int arg1, Throwable arg2) {
//				arg2.printStackTrace();
//				System.out.println("ShareSDKUtil.onError()");
//				handler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(context.getApplicationContext(), "分享失败", Toast.LENGTH_SHORT).show();
//					}
//			
//				},2000);
//			}
//			
//			@Override
//			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//				System.out.println("ShareSDKUtil.onComplete()");
//				handler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(context.getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
//					}
//			
//				},2000);
//			}
//			
//			@Override
//			public void onCancel(Platform arg0, int arg1) {
//				System.out.println("ShareSDKUtil.onCancel()");
//				handler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(context.getApplicationContext(), "取消分享", Toast.LENGTH_SHORT).show();
//					}
//			
//				},2000);
//			}
//		});
//        oks.show(context.getApplicationContext());
//	}
//	
//	public static void share(final Activity activity, final ShareInfo shareInfo){
//		SharePopWindow window = new SharePopWindow(activity);
//		window.setPopListener(new SharePopWindowListener() {
//			
//			@Override
//			public void onClickBtn(int tag) {
//				switch (tag) {
//				case 0:
//					// 分享到微信
//					shareToWechat(activity.getApplicationContext(),AppShareTitle, shareInfo.text,shareInfo.shareUrl, shareInfo.thumb);
//					break;
//				case 1:
//					// 微信朋友圈
//					shareToWechatMoments(activity.getApplicationContext(),AppShareTitle,shareInfo.text,shareInfo.shareUrl, shareInfo.thumb);
//					break;
//				case 2:
//					// 新浪微博
//					shareToSinaWeibo(activity.getApplicationContext(), shareInfo.text+" 查看秘籍:"+shareInfo.shareUrl+" @高手_Highand",shareInfo.shareUrl, shareInfo.thumb);
//					break;
//				case 3:
//					// qq空间
//					shareToQzone(activity, AppShareTitle,  shareInfo.text,shareInfo.shareUrl, shareInfo.imageUrl);
//					break;
//				case 4:
//					// 人人
//					shareToRenren(activity, AppShareTitleShort,  shareInfo.text,shareInfo.shareUrl, shareInfo.imageUrl);
//					break;
//				case 5:
//					// 豆瓣
//					shareToDouban(activity, shareInfo.text+" 查看秘籍:"+shareInfo.shareUrl,shareInfo.shareUrl, shareInfo.thumb);
//					break;
//				case 6:
//					// 印象笔记
//					 shareToEvernote(activity, AppShareTitle,  shareInfo.text+" 查看秘籍:"+shareInfo.shareUrl,shareInfo.shareUrl, shareInfo.thumb);
//					break;
//				case 7:
//					ClipboardManager cmb = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
//					cmb.setText(shareInfo.shareUrl.trim()); 
//				default:
//					break;
//				}
//			}
//		});
//	}
//	
//	
//	
//	
//	public static void shareApp(final Activity activity){
//		ShortUrlUtil shorturl = new ShortUrlUtil(Constants.kAPPShareURL);
//		final CustomProgressDialog pdialog = CustomProgressDialog.createDialog(activity);
//		pdialog.show();
//		shorturl.setShortUrlInterface(new ShortUrlInterface() {
//			
//			@Override
//			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
//				pdialog.dismiss();
//				showSharePopWindow(activity ,urlobject.getUrl_short());
//			}
//		});
//		shorturl.execute();
//	}
//	
//	private static  void showSharePopWindow(final Activity activity , final String shortUrl ){
//		SharePopWindow window = new SharePopWindow(activity);
//		window.setPopListener(new SharePopWindowListener() {
//			Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.share_logo);
//			@Override
//			public void onClickBtn(int tag) {
//				// TODO Auto-generated method stub
//				switch (tag) {
//				case 0:
//					// 分享到微信
//					ShareSDKUtil.shareToWechat(activity.getApplicationContext(),
//							ShareSDKUtil.AppShareTitle, ShareSDKUtil.AppShareTextShort, shortUrl, thumb);
//					break;
//				case 1:
//					// 微信朋友圈
//					ShareSDKUtil.shareToWechatMoments(activity.getApplicationContext(),
//							ShareSDKUtil.AppShareTitle, ShareSDKUtil.AppShareTextShort, shortUrl, thumb);
//					break;
//				case 2:
//					// 新浪微博
//					ShareSDKUtil.shareToSinaWeibo(activity,ShareSDKUtil.AppShareTextD+shortUrl+" @高手_Highand", shortUrl, thumb);
//					break;
//				case 3:
//					// qq空间
//					ShareSDKUtil.shareToQzone(activity, ShareSDKUtil.AppShareTitle, ShareSDKUtil.AppShareTextShort,shortUrl, ShareSDKUtil.AppShareImageUrl);
//					break;
//				case 4:
//					// 人人
//					ShareSDKUtil.shareToRenren(activity, ShareSDKUtil.AppShareTitleShort, ShareSDKUtil.AppShareText,shortUrl, ShareSDKUtil.AppShareImageUrl);
//					break;
//				case 5:
//					// 豆瓣
//					ShareSDKUtil.shareToDouban(activity, ShareSDKUtil.AppShareTextD+shortUrl, shortUrl, thumb);
//					break;
//				case 6:
//					// 印象笔记
//					ShareSDKUtil.shareToEvernote(activity, ShareSDKUtil.AppShareTitle, ShareSDKUtil.AppShareTextD+shortUrl, shortUrl, thumb);
//					break;
//				case 7:
//					ClipboardManager cmb = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
//					cmb.setText(shortUrl.trim());  
//					break;
//				default:
//					break;
//				}
//			}
//		});
//	}
//}
