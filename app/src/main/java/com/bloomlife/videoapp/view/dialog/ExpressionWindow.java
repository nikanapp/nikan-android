/**
 * 
 */
package com.bloomlife.videoapp.view.dialog;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.ExpressionAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.model.Dynamicimg;
import com.bloomlife.videoapp.view.PageNumberView;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 表情弹幕的弹出选择窗口
 * @date 2015年4月15日 下午5:46:05
 */
public class ExpressionWindow extends RelativeLayout {

	private ViewPager mViewPager;
	private ExpressionAdapter mAdapter;
	private PageNumberView mPageNumberView;
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ExpressionWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ExpressionWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 */
	public ExpressionWindow(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		inflate(context, R.layout.dialog_expression, this);
		mViewPager = (ViewPager) findViewById(R.id.expression_viewpager);
		mPageNumberView = (PageNumberView) findViewById(R.id.expression_pagechange);
		mAdapter = new ExpressionAdapter(context, AppContext.getSysCode().getDynamicimgs());
		mPageNumberView.setDotNumber((AppContext.getSysCode().getDynamicimgs().size() - 1) / 6 + 1);
		mPageNumberView.setCurrentDot(0);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setAdapter(mAdapter);
	}
	
	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			mPageNumberView.setCurrentDot(arg0);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void setExpressionListener(ExpressionListener l){
		mAdapter.setExpressionListener(l);
	}
	
	public interface ExpressionListener{
		void onExpression(Dynamicimg dynamicimg);
		void firstClick();
	}
}
