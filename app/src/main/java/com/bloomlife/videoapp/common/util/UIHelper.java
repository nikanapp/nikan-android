/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.Scroller;
import android.widget.TextView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.model.Emotion;
import com.easemob.chat.ImageMessageBody;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * @date 2014年11月27日 下午7:03:06
 */
public class UIHelper {

	private static HashMap<String, Integer> sPopImageMap = new HashMap<String, Integer>() {

		private static final long serialVersionUID = 1L;

		{
			put("ce0d09", R.drawable.pop02);
			put("e8511a", R.drawable.pop03);
			put("ce9d40", R.drawable.pop04);
			put("ed9d60", R.drawable.pop05);
			put("e4c764", R.drawable.pop06);
			put("375d81", R.drawable.pop07);
			put("3a7ea5", R.drawable.pop08);
			put("4971b6", R.drawable.pop09);
			put("379edc", R.drawable.pop10);
			put("5bb8d8", R.drawable.pop11);
			put("2c133d", R.drawable.pop12);
			put("5a4a6e", R.drawable.pop13);
			put("282866", R.drawable.pop14);
			put("5b53a9", R.drawable.pop15);
			put("ca9ec1", R.drawable.pop16);
			put("2b5c52", R.drawable.pop17);
			put("4b6367", R.drawable.pop18);
			put("90a24c", R.drawable.pop19);
			put("4ba593", R.drawable.pop20);
			put("61969c", R.drawable.pop21);
			put("7b5548", R.drawable.pop22);
			put("7e918b", R.drawable.pop23);
			put("7cc576", R.drawable.pop24);
			put("3d4049", R.drawable.pop25);
			put("bb9e76", R.drawable.pop26);
			put("6dabde", R.drawable.pop27);
			put("cc374b", R.drawable.pop28);
			put("f36861", R.drawable.pop29);
			put("f9644e", R.drawable.pop30);
			put("ff2b7f", R.drawable.pop31);
			put("49c1a9", R.drawable.pop32);
			put("e93c8c", R.drawable.pop33);
			put("169e49", R.drawable.pop34);
			put("921d22", R.drawable.pop35);
			put("f2b96c", R.drawable.pop36);
		}
	};
	
	@SuppressWarnings("deprecation")
	public static void setVideoCommentTagSelector(Resources res, CheckedTextView view, int color){
		GradientDrawable normal = (GradientDrawable) res.getDrawable(R.drawable.gridview_comment_normal);
		GradientDrawable select = (GradientDrawable) res.getDrawable(R.drawable.gridview_comment_select);
		
		normal.setStroke(res.getDimensionPixelSize(R.dimen.video_tag_stroke_width), color);
		select.setColor(color);
		
		StateListDrawable selector = new StateListDrawable();
		selector.addState(new int[] {android.R.attr.state_checked}, select);
		selector.addState(new int[] {-android.R.attr.state_checked}, normal);
		selector.addState(new int[] {android.R.attr.stateNotNeeded}, normal);
		view.setBackgroundDrawable(selector);
	}

	public static final List<String> ColorList = Arrays.asList("ce0d09",
			"e8511a", "ce9d40", "ed9d60", "e4c764", "375d81", "3a7ea5",
			"4971b6", "379edc", "5bb8d8", "2c133d", "5a4a6e", "282866",
			"5b53a9", "ca9ec1", "2b5c52", "4b6367", "90a24c", "4ba593",
			"61969c", "7b5548", "7e918b", "7cc576", "3d4049", "bb9e76",
			"6dabde", "cc374b", "f36861", "f9644e", "ff2b7f", "49c1a9",
			"e93c8c", "169e49", "921d22", "f2b96c");

	public static int getPopImageResource(String color) {
		return sPopImageMap.get(color);
	}

	public static ArrayList<String> getTopicList() {
		ArrayList<String> topicList = new ArrayList<String>();
		for (String topic : AppContext.getSysCode().getTopics()) {
			topicList.add("#" + topic + " ");
		}
		return topicList;
	}

	public static void showSoftInput(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public static void hideSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static Typeface getImpact(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "impact.ttf");
	}

	public static Typeface getBebas(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "BEBASNEUE_0.OTF");
	}
	
	public static Typeface getHelveticaTh(Context context){
		return Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTStd-ThCn_0.otf");
	}
	
	public static Typeface getHelveticaLt(Context context){
		return Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTStd-LtCn_0.otf");
	}
	
	public static boolean isZH(){
		return Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage());
	}

	public static String getEMMessageImage(ImageMessageBody body) {
		if (body.getRemoteUrl() == null) {
			return "file://" + body.getLocalUrl();
		} else {
			return body.getRemoteUrl();
		}
	}
	
	public static String getEMMessageThumbnailUrl(ImageMessageBody body){
		if (body.getThumbnailUrl() == null) {
			return "file://" + body.getLocalUrl();
		} else {
			return body.getThumbnailUrl();
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
	
	public static void measure(View v){
		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	}
	
	public static Bitmap makeRootViewBitmap(Context context, View rootView, float scale, boolean hasSystemBar){
		int width = rootView.getWidth();
		int height = rootView.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		rootView.draw(canvas);
		// 去掉标题栏，同时缩小一半
		Matrix matrix = new Matrix();
		matrix.preScale(scale, scale);
		int statusBarHeight = 0;
		if (hasSystemBar)
			statusBarHeight = getStatusBarHeight(context);
		Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height-statusBarHeight, matrix, false);
		bitmap.recycle();
		return scaleBitmap;
	}
	
	public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {  
		  
        // Stack Blur v1.0 from  
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html  
        //  
        // Java Author: Mario Klingemann <mario at quasimondo.com>  
        // http://incubator.quasimondo.com  
        // created Feburary 29, 2004  
        // Android port : Yahel Bouaziz <yahel at kayenko.com>  
        // http://www.kayenko.com  
        // ported april 5th, 2012  
  
        // This is a compromise between Gaussian Blur and Box blur  
        // It creates much better looking blurs than Box Blur, but is  
        // 7x faster than my Gaussian Blur implementation.  
        //  
        // I called it Stack Blur because this describes best how this  
        // filter works internally: it creates a kind of moving stack  
        // of colors whilst scanning through the image. Thereby it  
        // just has to add one new block of color to the right side  
        // of the stack and remove the leftmost color. The remaining  
        // colors on the topmost layer of the stack are either added on  
        // or reduced by one, depending on if they are on the right or  
        // on the left side of the stack.  
        //  
        // If you are using this algorithm in your code please add  
        // the following line:  
        //  
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>  
  
        Bitmap bitmap;  
        if (canReuseInBitmap) {  
            bitmap = sentBitmap;  
        } else {  
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);  
        }  
  
        if (radius < 1) {  
            return (null);  
        }  
  
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();  
  
        int[] pix = new int[w * h];  
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);  
  
        int wm = w - 1;  
        int hm = h - 1;  
        int wh = w * h;  
        int div = radius + radius + 1;  
  
        int r[] = new int[wh];  
        int g[] = new int[wh];  
        int b[] = new int[wh];  
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;  
        int vmin[] = new int[Math.max(w, h)];  
  
        int divsum = (div + 1) >> 1;  
        divsum *= divsum;  
        int dv[] = new int[256 * divsum];  
        for (i = 0; i < 256 * divsum; i++) {  
            dv[i] = (i / divsum);  
        }  
  
        yw = yi = 0;  
  
        int[][] stack = new int[div][3];  
        int stackpointer;  
        int stackstart;  
        int[] sir;  
        int rbs;  
        int r1 = radius + 1;  
        int routsum, goutsum, boutsum;  
        int rinsum, ginsum, binsum;  
  
        for (y = 0; y < h; y++) {  
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;  
            for (i = -radius; i <= radius; i++) {  
                p = pix[yi + Math.min(wm, Math.max(i, 0))];  
                sir = stack[i + radius];  
                sir[0] = (p & 0xff0000) >> 16;  
                sir[1] = (p & 0x00ff00) >> 8;  
                sir[2] = (p & 0x0000ff);  
                rbs = r1 - Math.abs(i);  
                rsum += sir[0] * rbs;  
                gsum += sir[1] * rbs;  
                bsum += sir[2] * rbs;  
                if (i > 0) {  
                    rinsum += sir[0];  
                    ginsum += sir[1];  
                    binsum += sir[2];  
                } else {  
                    routsum += sir[0];  
                    goutsum += sir[1];  
                    boutsum += sir[2];  
                }  
            }  
            stackpointer = radius;  
  
            for (x = 0; x < w; x++) {  
  
                r[yi] = dv[rsum];  
                g[yi] = dv[gsum];  
                b[yi] = dv[bsum];  
  
                rsum -= routsum;  
                gsum -= goutsum;  
                bsum -= boutsum;  
  
                stackstart = stackpointer - radius + div;  
                sir = stack[stackstart % div];  
  
                routsum -= sir[0];  
                goutsum -= sir[1];  
                boutsum -= sir[2];  
  
                if (y == 0) {  
                    vmin[x] = Math.min(x + radius + 1, wm);  
                }  
                p = pix[yw + vmin[x]];  
  
                sir[0] = (p & 0xff0000) >> 16;  
                sir[1] = (p & 0x00ff00) >> 8;  
                sir[2] = (p & 0x0000ff);  
  
                rinsum += sir[0];  
                ginsum += sir[1];  
                binsum += sir[2];  
  
                rsum += rinsum;  
                gsum += ginsum;  
                bsum += binsum;  
  
                stackpointer = (stackpointer + 1) % div;  
                sir = stack[(stackpointer) % div];  
  
                routsum += sir[0];  
                goutsum += sir[1];  
                boutsum += sir[2];  
  
                rinsum -= sir[0];  
                ginsum -= sir[1];  
                binsum -= sir[2];  
  
                yi++;  
            }  
            yw += w;  
        }  
        for (x = 0; x < w; x++) {  
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;  
            yp = -radius * w;  
            for (i = -radius; i <= radius; i++) {  
                yi = Math.max(0, yp) + x;  
  
                sir = stack[i + radius];  
  
                sir[0] = r[yi];  
                sir[1] = g[yi];  
                sir[2] = b[yi];  
  
                rbs = r1 - Math.abs(i);  
  
                rsum += r[yi] * rbs;  
                gsum += g[yi] * rbs;  
                bsum += b[yi] * rbs;  
  
                if (i > 0) {  
                    rinsum += sir[0];  
                    ginsum += sir[1];  
                    binsum += sir[2];  
                } else {  
                    routsum += sir[0];  
                    goutsum += sir[1];  
                    boutsum += sir[2];  
                }  
  
                if (i < hm) {  
                    yp += w;  
                }  
            }  
            yi = x;  
            stackpointer = radius;  
            for (y = 0; y < h; y++) {  
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )  
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];  
  
                rsum -= routsum;  
                gsum -= goutsum;  
                bsum -= boutsum;  
  
                stackstart = stackpointer - radius + div;  
                sir = stack[stackstart % div];  
  
                routsum -= sir[0];  
                goutsum -= sir[1];  
                boutsum -= sir[2];  
  
                if (x == 0) {  
                    vmin[y] = Math.min(y + r1, hm) * w;  
                }  
                p = x + vmin[y];  
  
                sir[0] = r[p];  
                sir[1] = g[p];  
                sir[2] = b[p];  
  
                rinsum += sir[0];  
                ginsum += sir[1];  
                binsum += sir[2];  
  
                rsum += rinsum;  
                gsum += ginsum;  
                bsum += binsum;  
  
                stackpointer = (stackpointer + 1) % div;  
                sir = stack[stackpointer];  
  
                routsum += sir[0];  
                goutsum += sir[1];  
                boutsum += sir[2];  
  
                rinsum -= sir[0];  
                ginsum -= sir[1];  
                binsum -= sir[2];  
  
                yi += w;  
            }  
        }  
  
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);  
  
        return (bitmap);  
    }

    public static TextView getInviteTitle(Context context){
        TextView title = new TextView(context);
        title.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UiUtils.dip2px(context, 17)));
        title.setTextColor(context.getResources().getColor(R.color.white));
        title.setBackgroundResource(R.color.activity_invite_title_bg);
        title.setGravity(Gravity.LEFT | Gravity.CENTER);
        title.setTextSize(12);
        title.setPadding(UiUtils.dip2px(context, 4), 0, 0, 0);
        return title;
    }

    public static void setViewPagerScrollSpeed(ViewPager viewPager, int duration){
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(
                    viewPager.getContext(),
                    new AccelerateInterpolator(),
                    duration
            );
            field.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class FixedSpeedScroller extends Scroller {
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context, int duration) {
            super(context);
            mDuration = duration;
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            super(context, interpolator);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setDuration(int time) {
            mDuration = time;
        }
    }
}
