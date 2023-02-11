/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.view.NetworkConnentErrorView.OnRetryListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 	视频加载视频view
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-30  下午3:52:33
 */
public class VideoLoadView extends RelativeLayout implements OnClickListener{
	
	private static final String TAG = "VideoLoadView";
	
	private GlobalProgressBar  progressBar;
	
	private ImageView backgroudView;
	
	private NetworkConnentErrorView reloadLayout;
	
	private boolean loadSuccess ;
	
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(false)
			.cacheOnDisk(true)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

	/**
	 * @param context
	 * @param attrs
	 */
	public VideoLoadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoLoadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VideoLoadView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		LayoutInflater.from(context).inflate(R.layout.view_video_load_fail, this);
		
		reloadLayout = (NetworkConnentErrorView) findViewById(R.id.reloadView_retry);
		reloadLayout.setVisibility(View.INVISIBLE);
		progressBar = (GlobalProgressBar) findViewById(R.id.activity_video_play_progressbar);
		progressBar.setVisibility(View.INVISIBLE);
		backgroudView = (ImageView) findViewById(R.id.backgroundView);
	}

	public void loadSuccess(){
		setLoadSuccess(true) ;
		if(getVisibility()==View.INVISIBLE) return ;
		setVisibility(View.INVISIBLE);
		progressBar.setText("0%");
		backgroudView.setVisibility(View.INVISIBLE);
		if(backgroudView!=null&&backgroudView.getDrawable()!=null){
			Drawable drawable = backgroudView.getDrawable();
			if(drawable instanceof BitmapDrawable){
				Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
				if(bitmap!=null&&!bitmap.isRecycled()){
					backgroudView.setImageBitmap(null);
					bitmap.recycle();
				}
			}
		}
	}
	
	public void refreshProgress(int lastProgress){
		if(reloadLayout.getVisibility()==View.VISIBLE)reloadLayout.setVisibility(View.INVISIBLE); //如果通过左右滑页的话，那么一回来就会加载的，所以要隐藏reloadlayout
		progressBar.setText(lastProgress+"%");
	}
	
	public void startProgressRefresh(){
		setVisibility(View.VISIBLE);
		backgroudView.setVisibility(View.VISIBLE);
		progressBar.bringToFront();
		progressBar.startAnimator();
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setText("0%");
	}
	
	public void setBigPreviewImage(String imageUrl){
		ImageLoader.getInstance().displayImage(imageUrl, backgroudView, options,imageLoadingListener );
	}
	
	private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
		
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage == null) return;
			if (loadedImage.getWidth()>loadedImage.getHeight()){
				final int screenWidth =   AppContext.deviceInfo.getScreenWidth();
				Matrix matrix = new Matrix();
				matrix.setRotate(-90, screenWidth/2, AppContext.deviceInfo.getScreenHeight()/2);
				if(screenWidth>loadedImage.getHeight()){
					float roate = screenWidth*1.0f / (loadedImage.getHeight()*1.0f);
					matrix.postScale(roate,roate);
				}
				Bitmap newBitmap =  Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
				loadedImage.recycle();
				((ImageView)view).setImageBitmap(newBitmap);
			}
		}
		
		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void showReloadStyle(){
		setLoadSuccess(false) ;
		if(getVisibility()!=View.VISIBLE)setVisibility(View.VISIBLE);
		reloadLayout.setVisibility(View.VISIBLE);
		progressBar.stopAnimator();
		progressBar.setVisibility(View.INVISIBLE);
		backgroudView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 动画结束时执行的回调
	 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
	 *
	 * @date 2014-12-30  下午4:36:27
	 */
	public interface ReloadAnimationListener {
		
		void onAnimationStart();
		
		void onAnimationEnd();
	}

	@Override
	public void onClick(View v) {
		
	}
	
	public boolean isLoadSuccess() {
		return loadSuccess;
	}
	
	public void setOnRetryListener(OnRetryListener l){
		reloadLayout.setOnRetryListener(l);
	}

	public void setLoadSuccess(boolean loadSuccess) {
		this.loadSuccess = loadSuccess;
	}
}
