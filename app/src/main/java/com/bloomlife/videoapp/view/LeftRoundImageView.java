/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 	左边圆角的imageview。
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2015-3-26 下午8:18:29
 * 
 * @organization bloomlife
 * @version 1.0
 */
public class LeftRoundImageView extends ImageView {
	
	public LeftRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public LeftRoundImageView(Context context) {
        super(context);
        init();
    }
 
    private final RectF backgroundRect = new RectF();
    private final RectF roundRect = new RectF();
    private final RectF rectangleRect = new RectF();
    private float rect_adius = 38;
    private final Paint zonePaint = new Paint();
    private int color = -1;
    private PaintFlagsDrawFilter pdf=new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
	 
	 private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
	 private PorterDuffXfermode xfermodeOver = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
	 
	private Bitmap bitmap ;
    
    private int w , h ;
 
    private void init() {
        zonePaint.setAntiAlias(true);
    }
 
    public void setRectAdius(float adius) {
        rect_adius = adius;
    }
 
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        w = getWidth();
        h = getHeight();
        backgroundRect.set(0, 0, w, h);
        roundRect.set(0, 0, (float) (w *0.6), h);
        rectangleRect.set((float) (w *0.4), 0, w , h);
    }

    
    @Override
	public void setImageBitmap(Bitmap bm) {
    	this.bitmap = bm ;
		super.setImageBitmap(bm);
	}
    
    
    
	@Override
	public void setBackgroundColor(int color) {
		this.color = color;
		Log.d("left round image view"," color "+color);
		super.setBackgroundColor(color);
		invalidate();
	}

	@Override
    public void draw(Canvas canvas) {
    	canvas.drawBitmap(createCircleImage(bitmap),0, 0, null);
    }
    

    /** 
     * 根据原图和变长绘制圆形图片 
     *  TODO 这里为了提高效率，可以对生成的bitmap进行缓存！！！
     * @param source 
     * @param min 
     * @return 
     */  
    private Bitmap createCircleImage(Bitmap source)  {  
        if(color!=-1) zonePaint.setColor(color);

        Bitmap target = Bitmap.createBitmap(w, h, Config.ARGB_8888);  
        /** * 产生一个同样大小的画布    */  
        Canvas canvas = new Canvas(target); 
        canvas.setDrawFilter(pdf);
        canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
        /**  * 使用SRC_IN，参考上面的说明  */  
        zonePaint.setXfermode(xfermodeOver);  
        
        canvas.drawRect(rectangleRect, zonePaint);
        
        if(source==null) return target;
        
        zonePaint.setXfermode(xfermode);  
        
        if(source.getWidth()<w || source.getWidth()>source.getHeight()){
        	Matrix 	matrix = new Matrix();
        	if(source.getWidth()>source.getHeight()){
        		//用于解决预览图旋转有时候生成错误90度图的问题。
        		if(source.getHeight()<w){
        			float ratio = w*1.0f/(source.getHeight()*1.0f);
        			matrix.setRotate(-90,w/2,h/2);
        			matrix.postScale(ratio, ratio);
            		canvas.drawBitmap(source, matrix, zonePaint);
        		}else{
        			matrix.setRotate(-90,w/2,h/2);
        			matrix.postTranslate((w-source.getHeight())/2,(h-source.getWidth())/2);
        			canvas.drawBitmap(source, matrix, zonePaint);
        		}
        	}else{
        		float ratio = w*1.0f/(source.getWidth()*1.0f);
        		matrix.setScale(ratio,ratio);
        		matrix.postTranslate(0, (h-source.getHeight()*ratio)/2);
        		canvas.drawBitmap(source, matrix, zonePaint);
        	}
        }else {
        	/** * 绘制图片 */  
        	canvas.drawBitmap(source, 0, (h-source.getHeight())/2, zonePaint);  
        }
        zonePaint.setXfermode(null);  
        return target;  
    }  

}
