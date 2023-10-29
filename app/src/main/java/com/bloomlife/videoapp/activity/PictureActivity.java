/**
 * 
 */
package com.bloomlife.videoapp.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bloomlife.android.common.CameraTouchFocusListener;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.CameraActivity.FLASH_MODE;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.util.CameraUtil;

import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.pinguo.edit.sdk.PGEditActivity;
import us.pinguo.edit.sdk.base.PGEditResult;
import us.pinguo.edit.sdk.base.PGEditSDK;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年4月8日 下午6:42:46
 */
public class PictureActivity extends BaseActivity implements Callback {

	private static final String TAG = PictureActivity.class.getSimpleName();
	
	public static final String RESULT_IMAGE_PATH = "bitmap";

	@ViewInject(id = R.id.picture_cancel, click = ViewInject.DEFAULT)
	private View mBtnCancel;

	@ViewInject(id = R.id.picture_textureview)
	private SurfaceView mSurfaceView;

	@ViewInject(id = R.id.picture_switch, click = ViewInject.DEFAULT)
	private View mSwitch;

	@ViewInject(id = R.id.picture_flash, click = ViewInject.DEFAULT)
	private View mFlash;

	@ViewInject(id = R.id.picture_take, click = ViewInject.DEFAULT)
	private View mPicktureTake;
	
	@ViewInject(id = R.id.picture_delete, click = ViewInject.DEFAULT)
	private ImageView mDelete;

	@ViewInject(id = R.id.picture_upload, click = ViewInject.DEFAULT)
	private ImageView mUpload;
	
	@ViewInject(id = R.id.picture_filter, click = ViewInject.DEFAULT)
	private ImageView mFilter;
	
	@ViewInject(id = R.id.picture_imageview)
	private ImageView mPickture;
	
	@ViewInject(id = R.id.picture_main_layout)
	private ViewGroup mMainLayout;

	private boolean mHaveFrontCamera;
	private boolean mIsFront;

	private Camera mCamera;
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mRawBitmap;
	private String mPicturePath;
	private int curPreviewWidth;
	private int curPreviewHeight;
	
	private int mFlashClick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		initLayout();
		PGEditSDK.instance().initSDK(getApplication());
	}

	private void initLayout() {
		Camera.CameraInfo info = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				// 如果有前置摄像头，把按钮和切换手势打开。
				mSwitch.setVisibility(View.VISIBLE);
				mHaveFrontCamera = true;
				return;
			}
		}
		mSwitch.setVisibility(View.INVISIBLE);
		mHaveFrontCamera = false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mIsFront = mHaveFrontCamera;
		createCamera(mHaveFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	private void createCamera(int type) {
		mCamera = openCamera(type);
		if (mCamera == null) {
			UiHelper.shortToast(this, getString(R.string.open_camera_fail));
			return;
		}
		CameraUtil.setCameraDisplayOrientation(this, 0, mCamera);
		Camera.Parameters mParameters = mCamera.getParameters();

		Size previewSize = getOptimalSize(mParameters.getSupportedPreviewSizes());
		curPreviewWidth = previewSize.width;
		curPreviewHeight = previewSize.height;
		mParameters.setPreviewSize(curPreviewWidth, curPreviewHeight);
		Size pictureSize = getOptimalSize(mParameters.getSupportedPictureSizes());
		mParameters.setPictureSize(pictureSize.width, pictureSize.height);
		mParameters.setPictureFormat(ImageFormat.JPEG);
		
		mCamera.setParameters(mParameters);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
			mCamera.release();
		}
		mCamera.startPreview();
		// 因为CamcorderProfile是以横屏为标准的，而当前屏幕录像时竖屏。所以要把Height和Width要反过来传
		setSurfaceViewLayoutParams(curPreviewHeight, curPreviewWidth);
		mMainLayout.setOnTouchListener(new CameraTouchFocusListener(this, mCamera, mFocusCallback));
		initFlash();
	}

	private Camera openCamera(int type) {
		Camera camera = null;
		try {
			camera = Camera.open(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return camera;
	}

	private void initFlash() {
		mFlash.setBackgroundResource(R.drawable.btn_normflash_selector);
		mFlash.setEnabled(false);
		Parameters parameters = mCamera.getParameters();
		List<String> list = parameters.getSupportedFlashModes();
		if (list == null || list.isEmpty())
			return;
		for (String str : list) {
			if (str.equals(Parameters.FLASH_MODE_ON)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mFlash.setVisibility(View.VISIBLE);
				mFlash.setEnabled(true);
				mFlash.setBackgroundResource(R.drawable.btn_flash_auto_selector);
			}
		}
		mCamera.setParameters(parameters);
	}

	private void setSurfaceViewLayoutParams(float width, float height) {
		final int screenHeight = AppContext.deviceInfo.getScreenHeight();
		final int screenWidth = AppContext.deviceInfo.getScreenWidth();
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSurfaceView
				.getLayoutParams();
		if (screenHeight - height > screenWidth - width) {
			// 高度适配
			params.width = (int) (width * (screenHeight / height));
			params.height = screenHeight;
		} else {
			// 宽度适配
			params.width = screenWidth;
			params.height = (int) (height * (screenWidth / width));
		}

		params.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(params);
		mSurfaceView.requestLayout();
	}

	private Size getOptimalSize(List<Size> sizes) {
		if (sizes == null)
			return null;
		int screenHeith = AppContext.deviceInfo.getScreenHeight();
		int screenWidth = AppContext.deviceInfo.getScreenWidth();
		Size targetSize = null;

		List<Size> filterSizes = new ArrayList<Camera.Size>();
		for (Size size : sizes) {
			if (size.width <= 1280 && size.width >= 480) {
				filterSizes.add(size);
			}
		}

		double ratioDiff = 100.0;
		final double targetRatio = (double) screenHeith / screenWidth;
		// 选择和当前屏幕比例最接近的拍摄分辨率
		for (Size size : filterSizes) {
			// 和平面分辨率一样
			if (size.width == screenHeith && size.height == screenWidth) {
				targetSize = size;
				break;
			}
			double tempRatio = Math.abs((double) size.width / size.height - targetRatio);
			if (tempRatio < ratioDiff) {
				ratioDiff = tempRatio; // 更靠近1280
				targetSize = size;
			}
		}
		return targetSize;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private void takePicture() {
		mCamera.takePicture(null, null, mJpegCallback);
	}

	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			camera.stopPreview();
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			mRawBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), getMatrix(), true);
			new SaveBitmapTask(mRawBitmap).execute();
			if (mRawBitmap != null){
				mPickture.setVisibility(View.VISIBLE);
				mPickture.setImageBitmap(mRawBitmap);
			}
			bitmap.recycle();
			camera.startPreview();
			showUploadButton();
			mPicktureTake.setEnabled(true);
		}
	};
	
	private Matrix getMatrix(){
		Matrix matrix = new Matrix();
		if (mIsFront){
			// Y轴方向翻转
			float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
		    Matrix matrixMirrorY = new Matrix();
		    matrixMirrorY.setValues(mirrorY);
		    matrix.postConcat(matrixMirrorY);
		    matrix.preRotate(270);
		} else {
			matrix.postRotate(90);
		}
	    return matrix;
	}
	
	private void showUploadButton(){
		mBtnCancel.animate().scaleX(0).scaleY(0);
		mPicktureTake.animate().scaleX(0).scaleY(0).setListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mDelete.setVisibility(View.VISIBLE);
				mDelete.startAnimation(AnimationUtils.loadAnimation(PictureActivity.this,
						R.anim.video_edit_delete_in));
				mUpload.setVisibility(View.VISIBLE);
				mUpload.startAnimation(AnimationUtils.loadAnimation(PictureActivity.this,
						R.anim.video_edit_upload_in));
				mFilter.setVisibility(View.VISIBLE);
				mFilter.startAnimation(AnimationUtils.loadAnimation(PictureActivity.this,
						R.anim.video_play_filter_in));
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	private void hideUploadButton(){
		mDelete.startAnimation(AnimationUtils.loadAnimation(PictureActivity.this,
				R.anim.video_play_delete_out));
		mFilter.startAnimation(AnimationUtils.loadAnimation(PictureActivity.this,
				R.anim.video_play_filter_out));
		Animation anim = AnimationUtils.loadAnimation(PictureActivity.this, R.anim.video_play_upload_out);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mDelete.setVisibility(View.INVISIBLE);
				mUpload.setVisibility(View.INVISIBLE);
				mFilter.setVisibility(View.INVISIBLE);
				mBtnCancel.animate().scaleX(1).scaleY(1);
				mPicktureTake.animate().scaleX(1).scaleY(1).setListener(null);
			}
		});
		mUpload.startAnimation(anim);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.picture_take:
			mPicktureTake.setEnabled(false);
			takePicture();
			break;

		case R.id.picture_switch:
			switchCamera();
			break;
			
		case R.id.picture_cancel:
			finish();
			break;

		case R.id.picture_delete:
			mPickture.setVisibility(View.INVISIBLE);
			if (mRawBitmap != null){
				mRawBitmap.recycle();
				mRawBitmap = null;
			}
			hideUploadButton();
			break;
			
		case R.id.picture_upload:
			if (mPicturePath != null){
				setPictureResult(mPicturePath);
			}
			break;
			
		case R.id.picture_filter:
			if (mPicturePath != null){
				PGEditSDK.instance().startEdit(this, PGEditActivity.class, mPicturePath, getSaveFile().getAbsolutePath());
			}
			break;
			
		case R.id.picture_flash:
			switchFlash(FLASH_MODE.values()[++mFlashClick % FLASH_MODE.values().length]);
			break;
			
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 切换前后摄像头
	 */
	private void switchCamera(){
		releaseCamera();
		if (mIsFront){
			mIsFront = false;
			createCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
		} else {
			mIsFront = true;
			createCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}
	}
	
	private CameraTouchFocusListener.OnFocusCallback mFocusCallback = new CameraTouchFocusListener.OnFocusCallback() {
		@Override
		public void onTouchFocusEnd(boolean success, Camera camera) {

		}

		@Override
		public void onTouchFocusStart(Camera camera) {

		}
	};
	
	private void switchFlash(FLASH_MODE flash){
		Parameters parameters = mCamera.getParameters();
		switch (flash) {
		case ON:
			parameters.setFlashMode(Parameters.FLASH_MODE_ON);//开启闪光灯
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mFlash.setBackgroundResource(R.drawable.btn_flash_on_selector);
			break;
			
		case OFF:
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mFlash.setBackgroundResource(R.drawable.btn_normflash_selector);
			break;
			
		case AUTO:
			parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
			mFlash.setBackgroundResource(R.drawable.btn_flash_auto_selector);
			break;

		default:
			break;
		}
		mCamera.setParameters(parameters); 
	}
	
	@Override
	public void finish() {
		if (mRawBitmap != null){
			mRawBitmap.recycle();
			mRawBitmap = null;
		}
		super.finish();
	}
	
	public File getSaveFile(){
		return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
	}
	
	class SaveBitmapTask extends AsyncTask<String, Integer, String>{
		
		private Bitmap saveBitmap;
		
		public SaveBitmapTask(Bitmap saveBitmap){
			this.saveBitmap = saveBitmap;
		}

		@Override
		protected String doInBackground(String... params) {
			File file = getSaveFile();
			try {
				saveBitmap.compress(CompressFormat.JPEG, AppContext.getSysCode().getPhotorate(), new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return file != null ? file.getAbsolutePath() : null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null){
				mPicturePath = result;
			} else {
				Toast.makeText(PictureActivity.this, getString(R.string.activity_picture_fail), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        	new File(mPicturePath).delete();
            PGEditResult editResult = PGEditSDK.instance().handleEditResult(data);
            mPicturePath = editResult.getReturnPhotoPath();
            mPickture.setImageBitmap(editResult.getThumbNail());
            mFilter.setBackgroundResource(R.drawable.btn_camera_filter_on_selector);
        }
	}
	
	private void setPictureResult(String result){
		Intent intent = new Intent();
		intent.putExtra(RESULT_IMAGE_PATH, result);
		setResult(RESULT_OK, intent);
		finish();
	}
	
}
