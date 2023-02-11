//package com.bloomlife.videoapp.manager;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//
//import javax.microedition.khronos.opengles.GL10;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.PointF;
//import android.opengl.GLUtils;
//
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.model.LatLng;
///**
// * 
// * 	纹理和矩形绘制类。不知道为什么，百度的gl会将图形放到了1.5倍。
// * 
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-12-9  下午5:06:17
// */
//public class Texture2D {
//	private int mWidth;
//	private int mHeight;
//	private int mPow2Width;
//	private int mPow2Height;
//	private float maxU = 1.0f;
//	private float maxV = 1.0f;
//
//	private Bitmap mBitmap = null;
//
//	private int textureId = 0;
//	
//	// 删除纹理数据
//	public void delete(GL10 gl) {
//		if (textureId != 0) {
//			gl.glDeleteTextures(1, new int[] { textureId }, 0);
//			textureId = 0;
//		}
//	}
//
//	public static int pow2(int size) {
//		int small = (int) (Math.log((double) size) / Math.log(2.0f));
//		if ((1 << small) >= size)
//			return 1 << small;
//		else
//			return 1 << (small + 1);
//	}
//
//	// 构建，推迟到第一次绑定时
//	public Texture2D(Context context , int rid) {
//		
//		init(BitmapFactory.decodeResource(context.getResources(), rid));
//	}
//
//	/**
//	 * @param context
//	 */
//	private void init(Bitmap bmp) {
//		mWidth = bmp.getWidth();
//		mHeight = bmp.getHeight();
//		mBitmap = bmp;
//
//		mPow2Height = pow2(mHeight);
//		mPow2Width = pow2(mWidth);
//
//		if (mWidth == mPow2Width && mHeight == mPow2Height)
//			return;
//
//		maxU = mWidth / (float) mPow2Width;
//		maxV = mHeight / (float) mPow2Height;
//
//		Bitmap bitmap = Bitmap.createBitmap(mPow2Width, mPow2Height, bmp.hasAlpha() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
//		Canvas canvas = new Canvas(bitmap);
//		canvas.drawBitmap(bmp, 0, 0, null);
//		mBitmap.recycle();
//		mBitmap = bitmap;
//	}
//
//	// 第一次会加载纹理数据
//	public void bind(GL10 gl) {
//		if (textureId == 0) {
//			int[] textures = new int[1];
//			gl.glGenTextures(1, textures, 0);
//			textureId = textures[0];
//
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
//
//			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//
//			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
//
//			mBitmap.recycle();
//			mBitmap = null;
//		}
//
//		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
//	}
//
//	public static FloatBuffer floatBuffer;
//
//	public static FloatBuffer fBuffer(float[] a) {
//		// 先初始化buffer,数组的长度*4,因为一个float占4个字节
//		ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
//		// 数组排列用nativeOrder
//		mbb.order(ByteOrder.nativeOrder());
//		floatBuffer = mbb.asFloatBuffer();
//		floatBuffer.put(a);
//		floatBuffer.position(0);
//		return floatBuffer;
//	}
//
//	// 绘制到屏幕上
//	public void draw(GL10 gl, float x, float y) {
//		draw(gl, x, y, mWidth, mHeight);
//	}
//
//	// 绘制到屏幕上
//	public void draw(BaiduMap mMap, GL10 gl, MapStatus mspStatus, LatLng latLng) {
//		PointF polyPoints = mMap.getProjection().toOpenGLLocation(latLng, mspStatus);
//		float x = polyPoints.x;
//		float y = polyPoints.y;
//		draw(gl, x, y);
//	}
//
//	public void draw(GL10 gl, float x, float y, float width, float height) {
//		gl.glEnable(GL10.GL_TEXTURE_2D);
//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		
//		int color = Color.argb(255, 255, 255, 255);
//		float colorA =Color.alpha(color) / 255f;
//		float colorR = Color.red(color) / 255f;
//		float colorG = Color.green(color) / 255f;
//		float colorB = Color.blue(color) / 255f;
//
//		//  绑定
//		bind(gl);
//
//		// 映射
//		FloatBuffer verticleBuffer = fBuffer(new float[] {
//				x-width/2, y, 
//				x + width/2, y, 
//				x-width/2, y + height, 
//				x + width/2, y + height, });
//		FloatBuffer coordBuffer = fBuffer(new float[] { 0, 0, maxU, 0, 0, maxV, maxU, maxV, });
//
//		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coordBuffer);
//		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, verticleBuffer);
//		//不弄这个的话，会被之前的矩形影响。
//		gl.glColor4f(colorR, colorG, colorB, colorA);
//		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
//
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glDisable(GL10.GL_TEXTURE_2D);
//
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glDisable(GL10.GL_TEXTURE_2D);
//
//	}
//
//
//	public void drawSquare(GL10 gl , float width ,float height) {
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		
//		FloatBuffer verticleBuffer = fBuffer(new float[] {
//				0-width, 0-height, 0,
//				0-width,  0+height, 0,
//				0+width, 0 + height, 0,
//				0 + width, 0- height,0 });
//
//		int color = Color.argb(90, 49, 26, 68);
//		float colorA =Color.alpha(color) / 255f;
//		float colorR = Color.red(color) / 255f;
//		float colorG = Color.green(color) / 255f;
//		float colorB = Color.blue(color) / 255f;
//
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticleBuffer);
//		gl.glColor4f(colorR, colorG, colorB, colorA);
//
//		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
//
//		gl.glDisable(GL10.GL_BLEND);
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//	}
//}
