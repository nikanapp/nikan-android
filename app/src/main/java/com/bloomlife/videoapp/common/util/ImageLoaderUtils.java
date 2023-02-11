/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-10  下午3:19:24
 */
public class ImageLoaderUtils {

	public static DisplayImageOptions getMyVideoPreviewImageOption(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(false)
		.cacheOnDisk(true)
		.cacheInMemory(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		//看了代码，设置这个东东是没有用的。imageloader是根据传入的iamgeview的大小来设置这个值的，那么这个东东的意义不知道在哪啊。！！！
		options.getDecodingOptions().outHeight=AppContext.deviceInfo.getScreenHeight()/16;
		options.getDecodingOptions().outWidth=AppContext.deviceInfo.getScreenWidth()/16;
		return options;
	}
	
	public static DisplayImageOptions getMsgImageLoadOption(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(false)
		.cacheOnDisk(true)
		.cacheInMemory(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		options.getDecodingOptions().outHeight=AppContext.deviceInfo.getScreenWidth()/4;
		options.getDecodingOptions().outWidth=AppContext.deviceInfo.getScreenWidth()/4;
		return options;
	}
	
	
	public static DisplayImageOptions getDescPreviewImageOption(Context context){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheOnDisk(true)
		.cacheInMemory(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
		options.getDecodingOptions().outHeight=UiUtils.dip2px(context, 65);
		options.getDecodingOptions().outWidth=UiUtils.dip2px(context, 65);
		return options;
	}

	public static DisplayImageOptions getDecodingOptions(int loadingImage, int failImage){
		return new DisplayImageOptions.Builder()
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.showImageOnLoading(loadingImage)
				.showImageOnFail(failImage)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

	public static DisplayImageOptions getDecodingOptions(){
		return new DisplayImageOptions.Builder()
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.displayer(new FadeInBitmapDisplayer(200))
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

	public static DisplayImageOptions getCircleLoadOptions(){
		return new DisplayImageOptions.Builder()
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.displayer(new FadeInBitmapDisplayer(200))
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnLoading(R.drawable.circle_avatar)
				.showImageOnFail(R.drawable.circle_avatar)
				.showImageOnFail(R.drawable.circle_avatar)
				.build();
	}
}
