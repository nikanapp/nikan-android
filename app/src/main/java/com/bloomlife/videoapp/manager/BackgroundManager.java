/**
 * 
 */
package com.bloomlife.videoapp.manager;

import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.zxtcode.util.Blur;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 背景图管理器
 * @date 2015年6月8日 下午6:20:29
 */
public class BackgroundManager {
	
	public static final int BLUR_TASK_COMPLETE = 0x1001001;

	public static final String ACTION_REFRESH_BACKGROUND = "refreshBackground";
	public static final String ACTION_RECYCLE_BACKGROUND = "recycleBackground";
	public static final String ACTION_CREATE_BACKGROUND = "createBackground";

	public static final String INTENT_IS_REFRESH = "isRefresh";
	
	private static volatile BackgroundManager sBackgroundManager;

	private static boolean sHaveBackground;

	private volatile Bitmap mBackground;
	
//	private Blur mBlur;

	private BackgroundManager() {
//		mBlur = new Blur();
	}
	
	public static BackgroundManager getInstance(){
		if (sBackgroundManager == null){
			synchronized(BackgroundManager.class){
				if (sBackgroundManager == null){
					sBackgroundManager = new BackgroundManager();
				}
			}
		}
		return sBackgroundManager;
	}

	public static boolean isHaveBackground() {
		return sHaveBackground;
	}

	public static void setHaveBackground(boolean sHaveBackground) {
		BackgroundManager.sHaveBackground = sHaveBackground;
	}

	public void setBackgroundBitmap(@NonNull Bitmap bitmap){
		if (mBackground != null) recycle();
		mBackground = bitmap;
	}
	
	public Bitmap getBackgroundBitmap(){
		return mBackground;
	}

	public void ReleaseMainBitmap(Context context){
		context.sendBroadcast(new Intent(BackgroundManager.ACTION_RECYCLE_BACKGROUND));
	}

	public boolean recycle(){
		if (mBackground != null){
			mBackground.recycle();
			mBackground = null;
			return true;
		}
		return false;
	}

	public void refresh(Context context){
		Intent intent = new Intent(ACTION_REFRESH_BACKGROUND);
		intent.putExtra(INTENT_IS_REFRESH, true);
		context.sendBroadcast(intent);
	}

	public void capture(Context context){
		Intent intent = new Intent(ACTION_REFRESH_BACKGROUND);
		intent.putExtra(INTENT_IS_REFRESH, false);
		context.sendBroadcast(intent);
	}

	public boolean hasBackground(){
		return !(mBackground == null || mBackground.isRecycled());
	}
	
	public void makeBlurBitmap(Context context, Bitmap bitmap, Handler handler){
		mBackground = bitmap;
		AppContext.EXECUTOR_SERVICE.execute(new BlurTask(bitmap, handler, getSaveFile(context)));
	}

	public Bitmap getCacheBitmap(Context context){
		return BitmapFactory.decodeFile(getSaveFile(context).getPath(), new BitmapFactory.Options());
	}

	private File getSaveFile(Context context){
		return new File(context.getCacheDir(), "capture");
	}

	class BlurTask implements Runnable {

		private Bitmap mBitmap;
		private Handler mHandler;
		private File mSavePath;

		public BlurTask(Bitmap bitmap, Handler handler, File saveFile) {
			mBitmap = bitmap;
			mHandler = handler;
			mSavePath = saveFile;
		}

		public void run() {
			Matrix m = new Matrix();
			m.postScale(0.5f, 0.5f);
			Bitmap scaleBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);
			Bitmap blurBitmap = UIHelper.doBlur(scaleBitmap, 65, false);
			if (blurBitmap == null) return;
//			Bitmap blurBitmap = mBlur.bitmapBlur(scaleBitmap, 65);
			setBackgroundBitmap(blurBitmap);
			if (mHandler != null){
				Message msg = new Message();
				msg.obj = getBackgroundBitmap();
				msg.what = BLUR_TASK_COMPLETE;
				mHandler.sendMessage(msg);
			}
			saveBitmap(blurBitmap, mSavePath);
		}

		private void saveBitmap(Bitmap bitmap, File path){
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null){
						fos.flush();
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
