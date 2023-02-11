/**
 * 
 */
package com.bloomlife.android.common;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 点击屏幕对焦
 * @date 2015年4月9日 下午6:07:16
 */
public class CameraTouchFocusListener implements OnTouchListener {
	
	private static final String TAG = CameraTouchFocusListener.class.getSimpleName();

	/** 手动点击对焦区域的大小*/
	public static final int AREA_SIZE = 400;
	/** 对焦失败后重新对焦的次数*/
	public static final int MAX_RETRY_NUM = 1;
	
	private int mAreaX;
	private int mAreaY;
	
	private boolean mSupportAutoFocus;
	private boolean mIsFocusing;
	
	private Context mContext;
	private Camera mCamera;
	private OnFocusCallback mCallback;
	
	public CameraTouchFocusListener(Context context, Camera camera, OnFocusCallback callback){
		mContext = context;
		mCamera = camera;
		mCallback = callback;
		List<String> focusModes = camera.getParameters().getSupportedFocusModes();
		mSupportAutoFocus = focusModes.contains(Parameters.FOCUS_MODE_AUTO);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && mCamera != null && !mIsFocusing){
			try {
				mIsFocusing = true;
				if (mCallback != null)
					mCallback.onTouchFocusStart(mCamera);
				Parameters mParameters = mCamera.getParameters();
				if (mSupportAutoFocus)
					mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
				else 
					return false;
				float x = event.getX();
				float y = event.getY();
				mAreaX = (int) getPointXToArea(x);
				mAreaY = (int) getPointYToArea(y);
				setCameraFocus(mParameters, mAreaX, mAreaY);
				Log.i(TAG, "areaX "+mAreaX+" areaY"+mAreaY);
			} catch (Exception e) {
				e.printStackTrace();
				mIsFocusing = false;
			}
		}
		if(event.getAction()==MotionEvent.ACTION_UP){
			v.performClick();
		}
		return false;
	}
	
	public void setCameraFocus(Parameters parameters, int areaX, int areaY){
		Rect rect = getFocusRect(areaX, areaY);
		Camera.Area area = new Camera.Area(rect, 1);
		List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
		focusAreas.add(area);
		parameters.setFocusAreas(focusAreas);
		if (parameters.getMaxNumMeteringAreas() > 1){
			List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
			meteringAreas.add(new Camera.Area(rect, 1));
			parameters.setMeteringAreas(meteringAreas);
		}
		mCamera.setParameters(parameters);
		mCamera.autoFocus(mTouchFocusCallback);
	}
	
	private Rect getFocusRect(int areaX, int areaY){
		// 防止对焦区域超过-1000~1000的区域，不然会导致崩溃。
		int left   = Math.max(areaX-AREA_SIZE, -1000);
		int top    = Math.max(areaY-AREA_SIZE, -1000);
		int right  = Math.min(areaX+AREA_SIZE,  1000);
		int bottom = Math.min(areaY+AREA_SIZE,  1000);
		Rect rect  = new Rect(left, top, right, bottom);
		return rect;
	}
	
	/**
	 * 将被点击的屏幕X坐标转换成对焦平面的X坐标
	 * @param x
	 * @return
	 */
	private float getPointXToArea(float x){
		int widthCenter = mContext.getResources().getDisplayMetrics().widthPixels/2;
		if (x > widthCenter){
			return ((x-widthCenter)/widthCenter) * -1000;
		} else {
			return ((widthCenter-x)/widthCenter) * 1000;
		}
	}
	
	/**
	 * 将被点击的屏幕Y坐标转换成对焦平面的Y坐标
	 * @param y
	 * @return
	 */
	private float getPointYToArea(float y){
		int heightCenter = mContext.getResources().getDisplayMetrics().heightPixels/2;
		if (y > heightCenter){
			return ((y-heightCenter)/heightCenter) *  1000;
		} else {
			return ((heightCenter-y)/heightCenter) * -1000;
		}
	}
	
	
	
	private Camera.AutoFocusCallback mTouchFocusCallback = new Camera.AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			mIsFocusing = false;
			if (mCallback != null)
				mCallback.onTouchFocusEnd(success, camera);
		}
	};

	public int getAreaX() {
		return mAreaX;
	}

	public int getAreaY() {
		return mAreaY;
	}

	public interface OnFocusCallback {
		void onTouchFocusEnd(boolean success, Camera camera);
		void onTouchFocusStart(Camera camera);
	}

}
