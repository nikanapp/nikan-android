/**
 * 
 */
package com.bloomlife.android.common.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-22  下午1:58:52
 */
public class AnimationUtils {

//	public static Animation getRotateAnimation(Context ctx, int rotate){
///*		int rotateXml = R.anim.rotate;
//		if (rotate != 0) {
//			rotateXml = R.anim.rotate1;
//		}*/
//		int rotateXml;
//		if(rotate>0){
//			rotateXml = R.anim.rotate;
//		}else{
//			rotateXml = R.anim.rotate1;
//		}
//		Animation operatingAnim = AnimationUtils.loadAnimation(ctx,rotateXml);  
//		operatingAnim.setFillAfter(true);
//		operatingAnim.setFillBefore(false);
//		LinearInterpolator la = new LinearInterpolator();
//		
//		operatingAnim.setInterpolator(la);
//		return operatingAnim;
//	}
	
	public static void doRotate(ImageView imageView , int angle ){
		imageView.startAnimation(getRotateAnimation(angle));
	}
	
	public static Animation getRotateAnimation(int angel){
		return getRotateAnimation(angel,300);
	}
	
	public static Animation getRotateAnimation(int angel, int duration){
		RotateAnimation rotateAnimation = new RotateAnimation(0, angel,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setFillBefore(false);
		rotateAnimation.setDuration(duration);
		rotateAnimation.setRepeatCount(0);
		return rotateAnimation;
	}
	
	
	
	
	/**
	 * 从右到左，进入
	 * @param duration 动画持续时间，单位是毫秒
	 * @return
	 */
	public static Animation inFromRightToLeftAnimation(int duration){
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		inFromRight.setDuration(duration);
		inFromRight.setInterpolator(new LinearInterpolator());
		return inFromRight;
	}
	
	/**
	 * 从左到右，退出
	 * @param duration 动画持续时间，单位是毫秒
	 * @return
	 */
	public static Animation outFromLeftToRightAnimation(int duration){
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		inFromRight.setDuration(duration);
		inFromRight.setInterpolator(new LinearInterpolator());
		return inFromRight;
	}
	
	/***
	 * 从下部往上进入的效果
	 * @return
	 */
	public static Animation inFromBottonAnimation(int duration) {
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new AccelerateInterpolator());
		return outtoTop;
	}
	
	public static Animation inFromScreenBottonAnimation(int duration) {
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new LinearInterpolator());
		return outtoTop;
	}
	
	/***
	 * 从上部往下进入的效果
	 * @return
	 */
	public static Animation inFromTopnimation(int duration) {
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new LinearInterpolator());
		return outtoTop;
	}
	
	/***
	 * 从上往下退出的效果
	 * @return
	 */
	public static Animation outFromTopnimation(int duration) {
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new AccelerateInterpolator());
		return outtoTop;
	}
	
	
	/***
	 * 从下往上退出的效果
	 * @return
	 */
	public static Animation outFromBottomAnimation(int duration) {
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f);

		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new AccelerateInterpolator());
		return outtoTop;
	}
	
	public static Animation outFromScreenBottomAnimation(int duration) {
		
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new AccelerateInterpolator());
		return outtoTop;
	}
	
	/***
	 * 淡入效果
	 * @param duration
	 * @return
	 */
	public static Animation fadeInAnimation(int duration) {
		Animation outtoTop = new AlphaAnimation(0,1);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new LinearInterpolator());
		return outtoTop;
	}
	
	/**
	 * 淡入效果
	 * @param duration			持续时间
	 * @param startOffset		开始时间
	 * @return
	 */
	public static Animation fadeInAnimation(int duration,int startOffset) {
		Animation animation = new AlphaAnimation(0,1);
		animation.setFillAfter(true);
		animation.setDuration(duration);
		animation.setStartOffset(startOffset);
		animation.setInterpolator(new LinearInterpolator());
		return animation;
	}
	
	/***
	 * 淡出效果
	 * @param duration
	 * @return
	 */
	public static Animation fadeOutAnimation(int duration) {
		Animation outtoTop = new AlphaAnimation(1,0);
		outtoTop.setDuration(duration);
		outtoTop.setInterpolator(new LinearInterpolator());
		return outtoTop;
	}
	/**
	 * 淡出效果
	 * @param duration			持续时间
	 * @param startOffset		开始时间
	 * @return
	 */
	public static Animation fadeOutAnimation(int duration,int startOffset) {
		Animation animation = new AlphaAnimation(1,0);
		animation.setDuration(duration);
		animation.setStartOffset(startOffset);
		animation.setInterpolator(new LinearInterpolator());
		return animation;
	}
	
	
//	float fromX 动画起始时 X坐标上的伸缩尺寸 
//
//	float toX 动画结束时 X坐标上的伸缩尺寸 
//
//	float fromY 动画起始时Y坐标上的伸缩尺寸 
//
//	float toY 动画结束时Y坐标上的伸缩尺寸 
//
//	int pivotXType 动画在X轴相对于物件位置类型 
//
//	float pivotXValue 动画相对于物件的X坐标的开始位置 
//
//	int pivotYType 动画在Y轴相对于物件位置类型 
//
//	float pivotYValue 动画相对于物件的Y坐标的开始位置 
	
	public static Animation scaleAnimation(float x , float y ){
		final ScaleAnimation animation =new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 
		Animation.ABSOLUTE, x, Animation.ABSOLUTE, y); 
		animation.setDuration(200);//设置动画持续时间 
		/** 常用方法 */ 
		//animation.setRepeatCount(int repeatCount);//设置重复次数 
		//animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态 
		//animation.setStartOffset(long startOffset);//执行前的等待时间 
		return animation;
	}
	
	public static Animation scaleSelfAnimation(float x , float y ){
		final ScaleAnimation animation =new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 
		Animation.RELATIVE_TO_SELF, x, Animation.RELATIVE_TO_SELF, y); 
		animation.setDuration(200);//设置动画持续时间 
		/** 常用方法 */ 
		//animation.setRepeatCount(int repeatCount);//设置重复次数 
		//animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态 
		//animation.setStartOffset(long startOffset);//执行前的等待时间 
		return animation;
	}
	
	
	public static Animation scaleInAnimation(float x , float y ){
		final ScaleAnimation animation =new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 
		Animation.ABSOLUTE, x, Animation.ABSOLUTE, y); 
		animation.setDuration(200);//设置动画持续时间 
		/** 常用方法 */ 
		//animation.setRepeatCount(int repeatCount);//设置重复次数 
		//animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态 
		//animation.setStartOffset(long startOffset);//执行前的等待时间 
		return animation;
	}
	
	/*
	 * ZoomImageView animation
	 * 
	 */
	public static Animation MoveAnimation(int duration,float offsetX,float offsetY){
//		Animation move = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f , Animation.RELATIVE_TO_PARENT, offsetY);
		Animation move = new TranslateAnimation(0.0f, 0.0f , Animation.RELATIVE_TO_PARENT, offsetY);

//		Animation move = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 385);
		move.setDuration(duration);
//		move.setInterpolator(new AccelerateInterpolator());
//		move.setFillEnabled(true);
//		move.setFillAfter(true);
//		move.setFillBefore(true);
		return move;
	}
	
	/**
	 * 定义淡入画效果
	 * 
	 * @return
	 */
	public static  Animation alphaAnimation() {
		Animation alpha = new AlphaAnimation(0,1);
		alpha.setDuration(600);
		alpha.setInterpolator(new AccelerateDecelerateInterpolator());
		return alpha;
	}
	
	public static  Animation alphaAnimation(int duration) {
		Animation alpha = new AlphaAnimation(0,1);
		alpha.setDuration(duration);
		alpha.setInterpolator(new DecelerateInterpolator());
		return alpha;
	}
}
