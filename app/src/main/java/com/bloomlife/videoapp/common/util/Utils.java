/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.log.Logger;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Database;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.CacheHostStorys;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.Story;
import com.bloomlife.videoapp.model.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentActivity;

import static com.bloomlife.videoapp.common.CacheKeyConstants.IS_FIRST;
import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_FIRST_MAN_TYPE;
import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_HUANXIN_PWD;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年6月15日 下午6:35:04
 */
public class Utils {

	public static boolean isEmpty(Collection<?> c){
		return c == null || c.isEmpty();
	}

	public static boolean isZH(){
		return Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage());
	}

	public static Bitmap createShareBitmap(View layout, TextureView textureView){
		if (layout == null) {
			return textureView.getBitmap();
		}
		Bitmap bitmap3 = null;
		try{
			layout.buildDrawingCache();
			Bitmap baseBitmap1 = layout.getDrawingCache();
			Bitmap bitmap1 = scaleBitmap(baseBitmap1, 0.5f);

			Bitmap baseBitmap2 = textureView.getBitmap();
			Bitmap bitmap2 = scaleBitmap(baseBitmap2, 0.5f);
			baseBitmap2.recycle();

			textureView.destroyDrawingCache();

			bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap3);
			canvas.drawBitmap(bitmap2, 0, 0, new Paint());
			canvas.drawBitmap(bitmap1, 0, 0, new Paint());
			canvas.save();
			canvas.restore();
			bitmap1.recycle();
			bitmap2.recycle();
		} catch(Exception e){
			e.printStackTrace();
		}
		return bitmap3;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, float scale){
		Matrix m = new Matrix();
		m.setScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
	}

	public static void setAccount(Context context, Account account){
		CacheBean.getInstance().putObject(context, com.bloomlife.videoapp.common.CacheKeyConstants.KEY_MY_ACCOUNT, account);
	}

	public static Account getAccount(Context context){
		return CacheBean.getInstance().getObject(context, com.bloomlife.videoapp.common.CacheKeyConstants.KEY_MY_ACCOUNT, Account.class);
	}

	public static boolean isLogin(FragmentActivity activity, boolean isShowLoginDialog){
		if (!CacheBean.getInstance().hasLoginUserId()){
			if (isShowLoginDialog)
				DialogUtils.showAccountDialog(activity);
			return false;
		} else {
			return true;
		}
	}

	public static boolean isLogin(FragmentActivity activity){
		return isLogin(activity, false);
	}

	public static void logout(Activity activity) {
		setAccount(activity, new Account());
		// 重置一些状态
		CacheBean.getInstance().setLoginUserId(activity, CacheBean.NO_LOGIN);
		CacheBean.getInstance().putInt(activity, CacheKeyConstants.KEY_VIDEO_UPLOAD_FAIL, Video.STATUS_UPLOAD_SUCCESS);
		CacheBean.getInstance().putString(activity, KEY_HUANXIN_PWD, "");
		CacheBean.getInstance().putInt(activity, CacheKeyConstants.KEY_MSG_NUM, 0);
		CacheHostStorys.set(activity, new ArrayList<Story>());
		MyHXSDKHelper.logout();
		UiHelper.showToast(activity, " " + activity.getString(R.string.activity_setting_logout_success) + "  ");
	}

	public static void login(Activity activity, Account account, String userId, String pwd){
		CacheBean.getInstance().setLoginUserId(activity, userId);
		CacheBean.getInstance().putString(activity, KEY_HUANXIN_PWD, pwd); //设置环信密码
		CacheBean.getInstance().putObject(activity, CacheKeyConstants.KEY_MY_ACCOUNT, account);
		MyHXSDKHelper.login(activity);
	}

	public static boolean isReauth(){
		return "1".equals(AppContext.getSysCode().getReauth());
	}

	public static boolean isFirstUseApp(Context context){
		return CacheBean.getInstance().getInt(context, KEY_FIRST_MAN_TYPE, IS_FIRST) == IS_FIRST;
	}

	public static void setNoFirstUseApp(Context context){
		CacheBean.getInstance().putInt(context, CacheKeyConstants.KEY_FIRST_MAN_TYPE, CacheKeyConstants.NOT_FIRST);
	}

	public static boolean isMy(String id){
		if (TextUtils.isEmpty(id) || CacheBean.NO_LOGIN.equals(id))
			return false;
		else
			return CacheBean.getInstance().getLoginUserId().equals(id);
	}

	public static int commentCount(List<Comment> tagsComments, List<CommentText> textComments){
		int count = textComments.size();
		for (Comment c:tagsComments){
			count += Integer.valueOf(c.getCount());
		}
		return count;
	}

	public static boolean isFirst(Context context, String key){
		return CacheBean.getInstance().getInt(context, key, CacheKeyConstants.IS_FIRST) == CacheKeyConstants.IS_FIRST;
	}

	public static void notFirst(Context context, String key){
		CacheBean.getInstance().putInt(context, key, CacheKeyConstants.NOT_FIRST);
	}

	public static boolean isShowDialog(Context context, String key){
		return CacheBean.getInstance().getInt(context, key, CacheKeyConstants.NOT_SHOW) == CacheKeyConstants.IS_SHOW;
	}

	public static void setShowDialog(Context context, String key, boolean isShow){
		CacheBean.getInstance().putInt(context, key, isShow ? CacheKeyConstants.IS_SHOW : CacheKeyConstants.NOT_SHOW);
	}

	public static void deleteVideoFile(Context context, Video v){
		// 删除本地拍摄的视频缓存
		if (!TextUtils.isEmpty(v.getFilaPath())){
			new File(v.getFilaPath()).delete();
			return;
		}
		// 如果不是本地，检查视频下载缓存文件夹是否有，有的话也要删除。
		File file = VideoFileManager.getInstance().getLocalCache(context, v.getVideouri());
		if (file != null){
			file.delete();
		}
	}

	public static void deleteStoryVideoFile(Context context, DbStoryVideo v){
		// 删除本地拍摄的视频缓存
		if (!TextUtils.isEmpty(v.getFilePath())){
			new File(v.getFilePath()).delete();
			return;
		}
		// 如果不是本地，检查视频下载缓存文件夹是否有，有的话也要删除。
		File file = VideoFileManager.getInstance().getLocalCache(context, v.getVideouri());
		if (file != null){
			file.delete();
		}
	}

	/** 复制数据库到sd卡中 **/
	public static void copySQLiteToSdCard(Context context, File files){
		for (File file:files.listFiles()){
			if (file.isDirectory()){
				copySQLiteToSdCard(context, file);
			}
			if (file.getParent().contains("databases") && file.getName().equals(Database.DATABASE_NAME)) {
				try {
					FileInputStream fis = new FileInputStream(file);
					FileOutputStream fos = new FileOutputStream(new File(context.getExternalCacheDir(), "videoapp.db"));
					byte[] buffer = new byte[4096];
					int count = 0;
					try {
						while ((count = fis.read(buffer)) != -1){
							fos.write(buffer, 0, count);
						}
						fis.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Size getVideoSize(String filePath){
		MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
		metadataRetriever.setDataSource(filePath);
		int vw = 0;
		int vh = 0;
		int rotate = 0;
		try {
			vw = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
			vh = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
			rotate = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
		} catch (NumberFormatException e){
			e.printStackTrace();
			Logger.e("getVideoSize", "获取视频尺寸失败");
		}
		return new Size(vw, vh, rotate);
	}

	public static class Size{
		public final int width;
		public final int height;
		public final int rotate;

		public Size(int width, int height, int rotate){
			this.width = width;
			this.height = height;
			this.rotate = rotate;
		}
	}
}
