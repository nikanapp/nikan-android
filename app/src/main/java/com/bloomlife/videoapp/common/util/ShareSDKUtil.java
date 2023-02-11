package com.bloomlife.videoapp.common.util;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.tencent.qq.QQ;
import u.aly.ac;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.douban.Douban;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.agent.shorturl.ShortUrlInterface;
import com.bloomlife.android.agent.shorturl.ShortUrlUtil;
import com.bloomlife.android.agent.shorturl.UrlObject;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.executor.RequestAsyncTask;
import com.bloomlife.android.media.image.Utils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoShareInfo;
import com.bloomlife.videoapp.model.message.ShareInfromMessage;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.dialog.RotateProgressDialog;
import com.bloomlife.videoapp.view.dialog.ShareAppWindow;
import com.bloomlife.videoapp.view.dialog.SharePopWindow;
import com.bloomlife.videoapp.view.dialog.SharePopWindow.SharePopWindowListener;
import com.bloomlife.videoapp.view.dialog.ShareVideoWindow;

public class ShareSDKUtil {
	
	private static final String TAG = "ShareSDKUtil";
	
	/**
	 * 分享一个网页信息到微信朋友圈(只有安装对应版本的微信才可以分享，如果没有安装，提示安装)
	 * @param context
	 * @param title				标题
	 * @param text				文字内容
	 * @param url				网页地址
	 * @param imageBitmap		图片
	 */
	public static void shareToWechatMoments(final Context context,String title,String text,String url,Bitmap imageBitmap){
		shareToPlatform(context, title, text, url, Utils.saveMyBitmap(imageBitmap), WechatMoments.NAME, false);
	}
	
	/**
	 * 分享一个网页信息到微信朋友(只有安装对应版本的微信才可以分享，如果没有安装，提示安装)
	 * @param 
	 * @paramcontext title				标题
	 * @param text				文字内容
	 * @param url				网页地址
	 * @param imageBitmap		图片
	 */
	public static void shareToWechat(final Context context,String title,String text,String url,Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		shareToPlatform(context, title, text, url, imagePath, Wechat.NAME, false);
	}
	
	/**
	 * 分享一个网页信息到新浪微博(有客户端使用客户端分享，没有就使用页面分享)
	 * @param context			
	 * @param text				文字内容
	 * @param url				网页地址
	 * @param imageBitmap		图片	
	 */
	public static void shareToSinaWeibo(final Context context,String text,String url,Bitmap imageBitmap){
		// 新浪微博需要把网页地址放到文字内容的后面
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		shareToPlatform(context, "", text, "", imagePath, SinaWeibo.NAME, false);
	}
	
	/**
	 * 分享一个网页信息到qq空间
	 * @param context
	 * @param title				标题
	 * @param text				文字内容
	 * @param shareUrl			分享的网页地址
	 * @param imageUrl			分享的网络图片
	 */
	public static void shareToQzone(final Context context,String title, String text,String url,Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
        oks.setSilent(false);
        oks.setText(text);
        oks.setTitle(title);
        oks.setImagePath(imagePath);
        oks.setTitleUrl(url);
        oks.setSite(title);
        oks.setPlatform(QZone.NAME);
        oks.setSiteUrl(url);
        oks.setEditPageBackground(getBackground(context));
        shareToPlatform(context, oks);
	}


	/**
	 * 分享一个网页信息到qq
	 * @param context
	 * @param title				标题
	 * @param text				文字内容
	 * @param shareUrl			分享的网页地址
	 * @param imageUrl			分享的网络图片
	 */
	public static void shareToQQ(final Context context,String title, String text,String url,Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
		oks.setSilent(false);
		oks.setText(text);
		oks.setTitle(title);
		oks.setImagePath(imagePath);
		oks.setTitleUrl(url);
		oks.setSite(title);
		oks.setPlatform(QQ.NAME);
		oks.setSiteUrl(url);
		oks.setEditPageBackground(getBackground(context));
		shareToPlatform(context, oks);
	}

	/**
	 * 分享到人人网
	 * @param context
	 * @param title
	 * @param text
	 * @param url
	 * @param imageBitmap
	 */
	public static void shareToRenren(final Context context,String title, String text,String url,Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
        oks.setSilent(false);
        oks.setComment(title);								// 类似标题
        oks.setText(text);
        oks.setPlatform(Renren.NAME);
        oks.setTitle(title);
        oks.setTitleUrl(url);								// 跳转地址
        oks.setImagePath(imagePath);
		shareToPlatform(context, oks);
	}
	
	/**
	 * 分享一个网页到豆瓣
	 * @param context
	 * @param text				标题
	 * @param shareUrl			分享的网页地址
	 * @param imageBitmap		分享的图片
	 */
	public static void shareToDouban(final Context context, String text, String shareUrl, Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		// 豆瓣不能分享网页，只能是分享图片和文字(可以把网址直接放到文字内容的后面)
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
        oks.setSilent(false);
        oks.setText(text);
        oks.setImagePath(imagePath);						// 需要申请权限
        oks.setPlatform(Douban.NAME);
		shareToPlatform(context, oks);
		
	}
	
	public static void shareToMessage(final Context context, final String url){
		shareToMessage(context, null, url);
	}

	public static void shareToMessage(final Context context, final String address, final String url){
		Uri smsToUri = Uri.parse("smsto:"+(TextUtils.isEmpty(address) ? "" : address));
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		final String shareText = shareAppContent(context, url);
		intent.putExtra("sms_body", shareText);
		context.startActivity(intent);
	}

	
	public static void shareToFacebook(final Context context, String text, String shareUrl, Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		OnekeyShare oks = new OnekeyShare();
        oks.setSilent(false);
        oks.setText(text);
        oks.setImagePath(imagePath);
        oks.setPlatform(Facebook.NAME);
		shareToPlatform(context, oks);
	}
	
	public static void shareToTwitter(final Context context, String text, String shareUrl, Bitmap imageBitmap){
		String imagePath = "";
		if (imageBitmap != null) {
			imagePath = Utils.saveMyBitmap(imageBitmap);
		}
		OnekeyShare oks = new OnekeyShare();
        oks.setSilent(false);
        oks.setText(text);
        oks.setImagePath(imagePath);
        oks.setPlatform(Twitter.NAME);
		shareToPlatform(context, oks);
	}

	/**
	 * 分享一个网页信息到一个平台
	 * @param context
	 * @param title				标题
	 * @param text				文字内容
	 * @param url				网页地址
	 * @param imagePath			图片的本地path
	 * @param platformName		平台名称
	 * @param silent			是否直接分享
	 */
	public static void shareToPlatform(final Context context,String title,String text,String url,String imagePath,String platformName,boolean silent){
		OnekeyShare	oks = new OnekeyShare();
        // 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher,context.getString(R.string.app_name));
        oks.setTitle(title);
        // 新浪微博没有url字段设置，需要把url合并到text的最后
        oks.setText(text);
        // url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
        oks.setUrl(url);
        oks.setImagePath(imagePath);
        oks.setPlatform(platformName);
        oks.setSilent(silent);
        shareToPlatform(context, oks);
	}
	
	/**
	 * 分享一个网页信息到一个平台
	 * @param context
	 * @param oks				分享实体
	 */
	public static void shareToPlatform(final Context context, OnekeyShare oks){
		ShareSDK.initSDK(context.getApplicationContext());
		final Handler handler = new Handler();
        oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				arg2.printStackTrace();
				System.out.println("ShareSDKUtil.onError()");
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context.getApplicationContext(), R.string.share_fail, Toast.LENGTH_SHORT).show();
					}

				}, 2000);
			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				System.out.println("ShareSDKUtil.onComplete()");
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context.getApplicationContext(), R.string.share_succ, Toast.LENGTH_SHORT).show();
					}

				}, 2000);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				System.out.println("ShareSDKUtil.onCancel()");
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
//						Toast.makeText(context.getApplicationContext(), "取消分享", Toast.LENGTH_SHORT).show();
					}

				}, 2000);
			}
		});
        oks.show(context.getApplicationContext());
	}
	
	
	public static void shareApp(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		
		shorturl.setShortUrlInterface(new ShortUrlInterface() {

			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				pdialog.dismiss();
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				ShareAppWindow window = new ShareAppWindow(activity);
				window.setPopListener(new SharePopWindow.SharePopWindowListener() {
					Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.start_splash_shoufa);
					String title = activity.getString(R.string.share_title);
					String content = activity.getString(R.string.share_app_content);

					@Override
					public void onClickBtn(int tag) {
						switch (tag) {
							case 0:
								// 分享到微信
								shareToWechat(
										activity.getApplicationContext(),
										activity.getString(R.string.share_app_wechat_content),
										title,
										shortUrl,
										thumb);
								break;
							case 1:
								// 微信朋友圈
								shareToWechatMoments(
										activity.getApplicationContext(),
										activity.getString(R.string.share_app_wechat_content),
										title,
										shortUrl,
										thumb);
								break;
							case 2:
								// 新浪微博
								shareToSinaWeibo(activity, shareAppContent(activity, shortUrl), shortUrl, thumb);
								break;

							case 3:
								// qq空间
								shareToQzone(activity, title, content, shortUrl, thumb);
								break;
							case 4:
								// 人人
								shareToFacebook(activity, content, shortUrl, thumb);
								break;
							case 5:
								// 短信分享
								shareToMessage(activity, shortUrl);
								break;

							case 8:
								shareToFacebook(activity, content, shortUrl, thumb);
								break;

							case 9:
								shareToTwitter(activity, content, shortUrl, thumb);
								break;

							default:
								break;
						}
					}

					@Override
					public void onShow() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		shorturl.execute();
	}

	public static void shareAppToFacebook(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		shorturl.setShortUrlInterface(new ShortUrlInterface() {
			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.start_splash_shoufa);
				String title = activity.getString(R.string.share_title);
				String content = activity.getString(R.string.share_app_content);
				shareToFacebook(activity, content, shortUrl, thumb);
				pdialog.dismiss();
			}
		});
		shorturl.execute();
	}

	public static void shareAppToWeibo(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		shorturl.setShortUrlInterface(new ShortUrlInterface() {
			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.start_splash_shoufa);
				shareToSinaWeibo(activity, shareAppContent(activity, shortUrl), shortUrl, thumb);
				pdialog.dismiss();
			}
		});
		shorturl.execute();
	}

	public static void shareAppToWechat(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		shorturl.setShortUrlInterface(new ShortUrlInterface() {
			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.start_splash_shoufa);
				shareToWechat(
						activity.getApplicationContext(),
						activity.getString(R.string.share_app_wechat_content),
						activity.getString(R.string.share_title),
						shortUrl,
						thumb
				);
				pdialog.dismiss();
			}
		});
		shorturl.execute();
	}

	public static void shareAppToQQ(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		shorturl.setShortUrlInterface(new ShortUrlInterface() {
			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.start_splash_shoufa);
				shareToQQ(
						activity,
						activity.getString(R.string.share_title),
						activity.getString(R.string.share_app_content),
						shortUrl,
						thumb
				);
				pdialog.dismiss();
			}
		});
		shorturl.execute();
	}

	public static void shareAppToTwitter(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		shorturl.setShortUrlInterface(new ShortUrlInterface() {
			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.start_splash_shoufa);
				shareToTwitter(activity, shareAppContent(activity, shortUrl), shortUrl, thumb);
				pdialog.dismiss();
			}
		});
		shorturl.execute();
	}

	public static void shareAppToMessage(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));
		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();
		shorturl.setShortUrlInterface(new ShortUrlInterface() {
			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				if (urlobject == null) return;
				final String shortUrl = urlobject.getUrl_short();
				shareToMessage(activity, shortUrl);
				pdialog.dismiss();
			}
		});
		shorturl.execute();
	}
	
	public static String shareAppContent(Context context, String url){
		return context.getString(R.string.share_app_content)+url;
	}
	
	public static void shareVideo(final Activity activity, final Video video, final Bitmap thumb, final ShareWindowListener listener){
		ShortUrlUtil shortUrl = new ShortUrlUtil(activity.getResources().getString(UIHelper.isZH() ?
						R.string.App_Share_Content_Url :
						R.string.App_Share_En_Content_Url) + video.getVideoid());
		VideoShareInfo info = new VideoShareInfo(video.getVideoid(), Integer.valueOf(video.getWidth()), Integer.valueOf(video.getHeight()), video.getRotate());
		shareContent(activity, thumb, video.getCity(), video.getDescription(), shortUrl, false, info, listener);
	}

	public static void shareStoryVideo(final Activity activity, final StoryVideo video, final Bitmap thumb, final ShareWindowListener listener){
		ShortUrlUtil shortUrl = new ShortUrlUtil(activity.getResources().getString(UIHelper.isZH() ?
				R.string.App_Share_Story_Content_Url :
				R.string.App_Share_En_Story_Content_Url) + video.getId());
		VideoShareInfo info = new VideoShareInfo();
		info.setVideoId(video.getId());
		String path = null;
		if (video.getVideouri().contains("http")){
			File file = VideoFileManager.getLocalCache(activity, video.getVideouri());
			if (file != null)
				path = file.getAbsolutePath();
		} else {
			path = video.getVideouri();
		}
		if (!TextUtils.isEmpty(path)){
			com.bloomlife.videoapp.common.util.Utils.Size size = com.bloomlife.videoapp.common.util.Utils.getVideoSize(path);
			info.setHeight(size.height);
			info.setWidth(size.width);
			info.setRotate(size.rotate);
		}
		shareContent(activity, thumb, video.getCity(), video.getDescription(), shortUrl, true, info, listener);
	}

	private static void shareContent(
			final Activity activity,
			final Bitmap thumb,
			final String city,
			final String desc,
			final ShortUrlUtil shortUrl,
			final boolean isStoryVideo,
			final VideoShareInfo info,
			final ShareWindowListener listener){
		final Dialog dialog = new RotateProgressDialog(activity);
		dialog.show();
		shortUrl.setShortUrlInterface(new ShortUrlInterface() {

			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				dialog.dismiss();
				if (urlobject == null) {
					new SuperToast(activity, activity.getString(R.string.view_load_video_network_fail));
					return;
				}
				final String shortUrl = urlobject.getUrl_short();
				final String title = activity.getString(R.string.share_title);
				SharePopWindow window = new ShareVideoWindow(activity);
				window.setPopListener(new SharePopWindowListener() {
					@Override
					public void onClickBtn(int tag) {
						infromToServer(activity, info);
						switch (tag) {
							case 0:
//							MobclickAgent.onEvent(activity, MobEvent.Event_Share_Wechat);
								// 分享到微信
								shareToWechat(activity, title, getShareContent(activity, city, desc, isStoryVideo), shortUrl, thumb);
								break;
							case 1:
								// 微信朋友圈
//							MobclickAgent.onEvent(activity, MobEvent.Event_Share_Wetchat_Comment);
								shareToWechatMoments(activity, getShareContent(activity, city, desc, isStoryVideo), title, shortUrl, thumb);
								break;
							case 2:
								// 新浪微博
//							MobclickAgent.onEvent(activity, MobEvent.Event_Share_Weibo);
								shareToSinaWeibo(activity, getShareContent(activity, city, desc, shortUrl, isStoryVideo), shortUrl, thumb);
								break;

							case 3:
								// QQ空间
//							MobclickAgent.onEvent(activity, MobEvent.Event_Share_Qzone);
								shareToQzone(activity, title, getShareContent(activity, city, desc, isStoryVideo), shortUrl, thumb);
								break;

							case 4:
								// 人人网
//							MobclickAgent.onEvent(activity, MobEvent.Event_Share_RenRen);
								shareToRenren(activity, title, getShareContent(activity, city, desc, isStoryVideo), shortUrl, thumb);
								break;

							case 5:
								// 豆瓣
//							MobclickAgent.onEvent(activity, MobEvent.Event_Share_Douban);
								shareToDouban(activity, getShareContent(activity, city, desc, shortUrl, isStoryVideo), shortUrl, thumb);
								break;

							case 8:
								shareToFacebook(activity, getShareContent(activity, city, desc, isStoryVideo), shortUrl, thumb);
								break;

							case 9:
								shareToTwitter(activity, getShareContent(activity, city, desc, isStoryVideo), shortUrl, thumb);
								break;

							default:
								break;
						}
						if (listener != null)
							listener.onClick();
					}

					@Override
					public void onDismiss() {
						if (listener != null)
							listener.onCancel();
					}

					@Override
					public void onShow() {
						if (listener != null) {
							listener.show();
						}
					}
				});
			}
		});
		shortUrl.execute();
	}
	
	private static String getShareContent(Context context, String location, String description, boolean isStoryVideo){
		if (isStoryVideo){
			return description;
		} else {
			return context.getString(R.string.share_video_content_first) + location + context.getString(R.string.share_video_content_last) + description;
		}
	}
	
	private static String getShareContent(Context context, String location, String description, String url, boolean isStoryVideo){
		return getShareContent(context, location, description, isStoryVideo) + " "+url;
	}
	
	public static final String Key_Inform_Server = "key_inform_server";
	
	private static void inforToServer(Activity context, String predictid){
		if(StringUtils.isEmpty(CacheBean.getInstance().getString(context, Key_Inform_Server))){
			Log.d(TAG, "  infor  to server");
//			new InfromTask(context, new ShareInformMessage(predictid)).execute();
		}
	}
	
	private static void infromToServer(Activity context , VideoShareInfo videoInfo){
		ShareInfromMessage msg = new ShareInfromMessage();
		try {
			msg.setWidth(videoInfo.getWidth());
			msg.setHeight(videoInfo.getHeight());
		} catch (Exception e){
			msg.setWidth(1280);
			msg.setHeight(720);
			e.printStackTrace();
		}
		if(videoInfo.getRotate()!=null)msg.setRotate(videoInfo.getRotate());
		msg.setVideoid(videoInfo.getVideoId());
			
		new RequestAsyncTask<ProcessResult>(context , msg ) {
			@Override
			protected void onCheckPostExecute(ProcessResult result) {
			}
		}.execute();
		Volley.add(new MessageRequest(msg));
	}
	
	
	private static View getBackground(Context context){
		View view = new ImageView(context);
		view.setBackgroundColor(0xFF2B2B2B);
		return view;
	}
	
	public interface ShareWindowListener{
		void onCancel();
		void onClick();
		void show();
	}
	
}
