package com.bloomlife.videoapp.common.util;

import static com.bloomlife.android.common.util.PlatformUtil.PACKAGE_SINA;
import static com.bloomlife.android.common.util.PlatformUtil.isInstallApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.agent.shorturl.ShortUrlInterface;
import com.bloomlife.android.agent.shorturl.ShortUrlUtil;
import com.bloomlife.android.agent.shorturl.UrlObject;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.PlatformUtil;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.executor.RequestAsyncTask;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoShareInfo;
import com.bloomlife.videoapp.model.message.ShareInfromMessage;
import com.bloomlife.videoapp.view.dialog.RotateProgressDialog;
import com.bloomlife.videoapp.view.dialog.ShareAppWindow;
import com.bloomlife.videoapp.view.dialog.SharePopWindow;

public class ShareSDKUtil {
	
	private static final String TAG = "ShareSDKUtil";
	public static final String AUTHORITY = "com.bloomlife.videoapp.fileprovider";

	/**
	 * 分享文本
	 *
	 * @param context
	 * @param path
	 */
	public static void shareUrl(Context context, String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}

		checkFileUriExposure();

		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_TEXT, path);
		it.setType("text/plain");
		context.startActivity(Intent.createChooser(it, "分享APP"));
	}

	/**
	 * 分享文件
	 *
	 * @param context
	 * @param path
	 */
	public static void shareFile(Context context, String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}

		checkFileUriExposure();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));  //传输图片或者文件 采用流的方式
		intent.setType("*/*");   //分享文件
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	/**
	 * 分享单张图片
	 *
	 * @param context
	 * @param path
	 */
	public static void shareImage(Context context, String path) {
		shareImage(context, path, null, null, null);
	}

	/**
	 * 分享多张图片
	 *
	 * @param context
	 * @param pathList
	 */
	public static void shareImage(Context context, List<String> pathList) {
		shareImage(context, null, pathList, null, null);
	}


	/**
	 * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
	 */
	private static void checkFileUriExposure() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
			StrictMode.setVmPolicy(builder.build());
			builder.detectFileUriExposure();
		}
	}


	/**
	 * 分享图片给QQ好友
	 *
	 * @param bitmap
	 */
	public void shareImageToQQ(Context context, Bitmap bitmap) {
		if (isInstallApp(context, PlatformUtil.PACKAGE_MOBILE_QQ)) {
			try {
				Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
						context.getContentResolver(), bitmap, null, null));
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				shareIntent.setType("image/*");
				// 遍历所有支持发送图片的应用。找到需要的应用
				ComponentName componentName = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");

				shareIntent.setComponent(componentName);
				context.startActivity(Intent.createChooser(shareIntent, "Share"));
			} catch (Exception e) {
            	UiHelper.showToast(context, "分享失败");
			}
		} else {
			UiHelper.showToast(context, "未安装QQ");
		}
	}

	public static void shareImageToQQZone(Context context, File picFile) {
		if (isInstallApp(context,PlatformUtil.PACKAGE_QZONE)) {
			if (picFile == null || !picFile.exists()) {
				UiHelper.showToast(context, "文件不存在");
				return;
			}
			Intent intent = new Intent();
			ComponentName componentName = new ComponentName("com.qzone","com.qzonex.module.operation.ui.QZonePublishMoodActivity");
			intent.setComponent(componentName);
			intent.setAction("android.intent.action.SEND");
			intent.setType("image/*");
			intent.putExtra(Intent.EXTRA_TEXT, "");//  分享文本
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picFile));// 分享图片
			context.startActivity(intent);
		} else {
			UiHelper.showToast(context, "未安装QQZone");
		}
	}

	/**
	 * 直接分享图片到微信好友
	 * @param context
	 * @param picFile
	 */
	public static void shareWechatFriend(Context context, String content , File picFile){
		if (isInstallApp(context, PlatformUtil.PACKAGE_WECHAT)){
			Intent intent = new Intent();
			ComponentName cop = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
			intent.setComponent(cop);
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/*");
			if (picFile != null) {
				if (picFile.isFile() && picFile.exists()) {
					Uri uri;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						uri = FileProvider.getUriForFile(context, AUTHORITY, picFile);
					} else {
						uri = Uri.fromFile(picFile);
					}
					intent.putExtra(Intent.EXTRA_STREAM, uri);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(Intent.createChooser(intent, "Share"));
		}else{
			UiHelper.showToast(context, "您需要安装微信客户端");
		}
	}

	/**
	 * 直接分享文本到微信好友
	 *
	 * @param context 上下文
	 */
	public void shareWechatFriend(Context context, String content) {
		if (isInstallApp(context, PlatformUtil.PACKAGE_WECHAT)) {
			Intent intent = new Intent();
			ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
			intent.setComponent(cop);
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra("android.intent.extra.TEXT", content);
			intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			UiHelper.showToast(context, "您需要安装微信客户端");
		}
	}

	/**
	 * 直接分享文本和图片到微信朋友圈
	 * @param context
	 * @param content
	 */
	public static void shareWechatMoment(Context context, String content, File picFile) {
		if (isInstallApp(context,PlatformUtil.PACKAGE_WECHAT)) {
			Intent intent = new Intent();
			//分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
			ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
			intent.setComponent(comp);
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/*");
			//添加Uri图片地址--用于添加多张图片
			//ArrayList<Uri> imageUris = new ArrayList<>();
			//intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
			if (picFile != null) {
				if (picFile.isFile() && picFile.exists()) {
					Uri uri;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						uri = FileProvider.getUriForFile(context, AUTHORITY, picFile);
					} else {
						uri = Uri.fromFile(picFile);
					}
					intent.putExtra(Intent.EXTRA_STREAM, uri);
				}
			}
			intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			UiHelper.showToast(context, "您需要安装微信客户端");
		}
	}

	public static void shareToSinaFriends(Context context, String content, File picFile) {
		if (!isInstallApp(context, PACKAGE_SINA)) {
			UiHelper.showToast(context, "新浪微博没有安装");
			return;
		}
		if (picFile == null || !picFile.exists()) {
			UiHelper.showToast(context, "文件不存在");
			return;
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");// 分享文本|文本+图片|图片 到微博内容时使用
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> matchs = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
		ResolveInfo resolveInfo = null;
		for (ResolveInfo each : matchs) {
			String pkgName = each.activityInfo.applicationInfo.packageName;
			if ("com.sina.weibo".equals(pkgName)) {
				resolveInfo = each;
				break;
			}
		}
		intent.setClassName(PACKAGE_SINA, resolveInfo.activityInfo.name);// 这里在使用resolveInfo的时候需要做判空处理防止crash
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picFile));
		context.startActivity(intent);
	}


	/**
	 * @param context  上下文
	 * @param path     不为空的时候，表示分享单张图片，会检验图片文件是否存在
	 * @param pathList 不为空的时候表示分享多张图片，会检验每一张图片是否存在
	 * @param pkg      分享到的指定app的包名
	 * @param cls      分享到的页面（微博不需要指定页面）
	 */
	private static void shareImage(Context context, String path, List<String> pathList, String pkg, String cls) {
		if (path == null && pathList == null) {
			UiHelper.showToast(context, "找不到您要分享的图片文件");
			return;
		}

		checkFileUriExposure();

		try {
			if (path != null) {
				//单张图片
				if (!new File(path).isFile()) {
					UiHelper.showToast(context, "图片不存在，请检查后重试");
					return;
				}

				Intent intent = new Intent();
				if (pkg != null && cls != null) {
					//指定分享到的app
					if (pkg.equals("com.sina.weibo")) {
						//微博分享的需要特殊处理
						intent.setPackage(pkg);
					} else {
						ComponentName comp = new ComponentName(pkg, cls);
						intent.setComponent(comp);
					}
				}
				intent.setAction(Intent.ACTION_SEND);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
				intent.setType("image/*");   //分享文件
				context.startActivity(Intent.createChooser(intent, "分享"));
			} else {
				//多张图片
				ArrayList<Uri> uriList = new ArrayList<>();
				for (int i = 0; i < pathList.size(); i++) {
					if (!new File(pathList.get(i)).isFile()) {
						UiHelper.showToast(context, "第" + (i + 1) + "张图片不存在，请检查后重试");
						return;
					}
					uriList.add(Uri.fromFile(new File(pathList.get(i))));
				}

				Intent intent = new Intent();

				if (pkg != null && cls != null) {
					//指定分享到的app
					if (pkg.equals("com.sina.weibo")) {
						//微博分享的需要特殊处理
						intent.setPackage(pkg);
					} else {
						ComponentName comp = new ComponentName(pkg, cls);
						intent.setComponent(comp);
					}
				}
				intent.setAction(Intent.ACTION_SEND_MULTIPLE);
				intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setType("image/*");
				context.startActivity(Intent.createChooser(intent, "分享"));
			}

		} catch (Exception e) {
			UiHelper.showToast(context, "分享失败，未知错误");
		}
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
	
	public static void shareApp(final Activity activity){
		ShortUrlUtil shorturl = new ShortUrlUtil(activity.getResources().getString(R.string.App_Share_Url));

		final Dialog pdialog = new RotateProgressDialog(activity);
		pdialog.show();

		shorturl.setShortUrlInterface(new ShortUrlInterface() {

			@Override
			public void handleResult(ShortUrlUtil shortUrlUtil, UrlObject urlobject) {
				pdialog.dismiss();

			}
		});
		shorturl.execute();
	}

	public static void shareAppToFacebook(final Activity activity) {
		final String shortUrl = activity.getResources().getString(R.string.App_Share_Url);
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
						shareWechatFriend(
								activity.getApplicationContext(),
								title + "\n" + activity.getString(R.string.share_app_wechat_content),
								BitmapUtils.saveBitmap("thumb.png", thumb, activity));
						break;
					case 1:
						// 微信朋友圈
						shareWechatMoment(
								activity.getApplicationContext(),
								title + "\n" + activity.getString(R.string.share_app_wechat_content),
								BitmapUtils.saveBitmap("thumb.png", thumb, activity));
						break;
					case 2:
						// 新浪微博
						shareToSinaFriends(activity,
								title + "\n" + activity.getString(R.string.share_app_wechat_content),
								BitmapUtils.saveBitmap("thumb.png", thumb, activity));
						break;

					case 3:
						// qq空间
						shareImageToQQZone(activity, BitmapUtils.saveBitmap("thumb.png", thumb, activity));
						break;
					case 4:
						// 人人
						shareImage(activity, shortUrl);
						break;
					case 5:
						// 短信分享
						shareToMessage(activity, content + "\n" + shortUrl);
						break;

					case 8:
					case 9:
						shareUrl(activity, shortUrl);
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

	public static void shareAppToWeibo(final Activity activity){
		shareUrl(activity, shareAppContent(activity, activity.getResources().getString(R.string.App_Share_Url)));
	}

	public static void shareAppToWechat(final Activity activity){
		shareUrl(activity, shareAppContent(activity, activity.getResources().getString(R.string.App_Share_Url)));
	}

	public static void shareAppToQQ(final Activity activity){
		shareUrl(activity, shareAppContent(activity, activity.getResources().getString(R.string.App_Share_Url)));
	}

	public static void shareAppToTwitter(final Activity activity){
		shareUrl(activity, shareAppContent(activity, activity.getResources().getString(R.string.App_Share_Url)));
	}

	public static void shareAppToMessage(final Activity activity){
		shareToMessage(activity, activity.getResources().getString(R.string.App_Share_Url));
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
		infromToServer(activity, info);
		shareUrl(activity, getShareContent(activity, city, desc, isStoryVideo));
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
