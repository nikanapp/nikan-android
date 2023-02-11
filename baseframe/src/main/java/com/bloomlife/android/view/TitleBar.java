package com.bloomlife.android.view;

import com.bloomlife.android.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 标题栏控件
 * 
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * @date 2014年8月15日 上午11:35:09
 */
public class TitleBar extends RelativeLayout {

	private static final float DEF_TEXT_SIZE = 24f;
	private static final int DEF_ICON_ID = -1;

	private LinearLayout mLeftButton;
	private LinearLayout mRightButton;
	private TextView mTitleText;
	private TextView mTextLeft;
	private TextView mTextRight;
	private ImageView mTitleIcon;
	private ImageView mIconLeft;
	private ImageView mIconBack;
	private ImageView mIconRight;
	private ProgressBar progressBar;
	

	private OnTitleBarListener listener;

	public void setOnTitleBarListener(OnTitleBarListener l) {
		this.listener = l;
		mLeftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					listener.onLeftClick();
			}
		});
		mTitleText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					listener.onTitleClick();
			}
		});
		mRightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onRightClick();
			}
		});
		mIconBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onLeftClick();
			}
		});
	}

	public interface OnTitleBarListener {
		void onLeftClick();

		void onTitleClick();

		void onRightClick();
	}

	public TitleBar(Context context) {
		super(context);
		init(context, null);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		View layout = LayoutInflater.from(context).inflate(
				R.layout.title_layout, this, false);
		if (isInEditMode()) {
			return;
		}
		addView(layout);
		RelativeLayout titleBar = (RelativeLayout) layout
				.findViewById(R.id.titlebar);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		titleBar.setLayoutParams(params);

		mLeftButton  = (LinearLayout) layout.findViewById(R.id.btn_left);
		mRightButton = (LinearLayout) layout.findViewById(R.id.btn_right);
		mTitleText = (TextView) layout.findViewById(R.id.title_text);
		mTitleIcon = (ImageView) layout.findViewById(R.id.title_icon);
		mTextLeft = (TextView) layout.findViewById(R.id.text_left);
		mIconLeft = (ImageView) layout.findViewById(R.id.icon_left);
		mIconRight = (ImageView) layout.findViewById(R.id.icon_right);
		mTextRight = (TextView) layout.findViewById(R.id.text_right);
		mIconBack = (ImageView) layout.findViewById(R.id.icon_back);
		progressBar  = (ProgressBar) layout.findViewById(R.id.progressbar);

		if (attrs != null) {
			TypedArray tArray = context.obtainStyledAttributes(attrs,
					R.styleable.TitleBar);

			String titleText = tArray.getString(R.styleable.TitleBar_titleText);

			boolean isDarkStyle = tArray.getBoolean(R.styleable.TitleBar_darkStyle, true);

			int defaultTextColor = isDarkStyle ? Color.WHITE : Color.BLACK;

			int titleIcon = tArray.getResourceId(R.styleable.TitleBar_titleIcon,
					DEF_ICON_ID);

			float titleTextSzie = tArray.getDimension(
					R.styleable.TitleBar_titleTextSize, DEF_TEXT_SIZE);
			int titleTextColor = tArray.getColor(
					R.styleable.TitleBar_titleTextColor, defaultTextColor);

			String leftText = tArray.getString(R.styleable.TitleBar_leftText);
			float leftTextSzie = tArray.getDimension(
					R.styleable.TitleBar_leftTextSize, DEF_TEXT_SIZE);
			int leftTextColor = tArray.getColor(
					R.styleable.TitleBar_leftTextColor, defaultTextColor);

			String rightText = tArray.getString(R.styleable.TitleBar_rightText);
			float rightTextSzie = tArray.getDimension(
					R.styleable.TitleBar_rightTextSize, DEF_TEXT_SIZE);
			int rightTextColor = tArray.getColor(
					R.styleable.TitleBar_rightTextColor, defaultTextColor);

			int leftIcon = tArray.getResourceId(R.styleable.TitleBar_leftIcon,
					DEF_ICON_ID);
			int rightIcon = tArray.getResourceId(
					R.styleable.TitleBar_rightIcon, DEF_ICON_ID);
			
			boolean isEnableBack = tArray.getBoolean(R.styleable.TitleBar_enableBack, true);

			tArray.recycle();

			darkStyle(isDarkStyle);

			setTitleText(titleText);
			setTitleIcon(titleIcon);
			setTitleSize(titleTextSzie);
			setTitleColor(titleTextColor);

			setLeftIcon(leftIcon);
			setLeftText(leftText);
			setLeftTextSize(leftTextSzie);
			setLeftTextColor(leftTextColor);
			
			if (TextUtils.isEmpty(rightText)){
				mTextRight.setVisibility(View.GONE);
			} else {
				setRightText(rightText);
				setRightTextSize(rightTextSzie);
				setRightTextColor(rightTextColor);
			}
			
			setRightIcon(rightIcon);
			setEnableBackIcon(isEnableBack);
		}
	}

	public void setTitleText(String text) {
		mTitleText.setText(text);
	}
	
	public void setTitleIcon(int resId){
		if (resId != DEF_ICON_ID)
			mTitleIcon.setBackgroundResource(resId);
	}

	public void setTitleSize(float size) {
		mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setTitleColor(int color) {
		mTitleText.setTextColor(color);
	}

	// 设置左边文字
	public void setLeftText(String text) {
		mTextLeft.setText(text);
	}

	// 设置左边文字大小
	public void setLeftTextSize(float size) {
		mTextLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setLeftTextColor(int color) {
		mTextLeft.setTextColor(color);
	}

	// 设置右边文字
	public void setRightText(String text) {
		mTextRight.setText(text);
	}

	// 设置右边文字大小
	public void setRightTextSize(float size) {
		mTextRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setRightTextColor(int color) {
		mTextRight.setTextColor(color);
	}

	public void setLeftIcon(int id) {
		if (id == DEF_ICON_ID)
			mIconLeft.setVisibility(View.GONE);
		else
			mIconLeft.setImageResource(id);
	}

	public void setRightIcon(int id) {
		if (id == DEF_ICON_ID)
			mIconRight.setVisibility(View.GONE);
		else
			mIconRight.setImageResource(id);
	}

	public void setEnableBackIcon(boolean isVisible) {
		if (isVisible)
			mIconBack.setVisibility(View.VISIBLE);
		else
			mIconBack.setVisibility(View.GONE);
	}

	public void darkStyle(boolean isDark){
		mIconBack.setImageResource(isDark ? R.drawable.nav_bar_back_light : R.drawable.nav_bar_back_dark);
	}

	public void setRightVisiable(int isVisible){
		if(mIconRight!=null)mIconRight.setVisibility(isVisible);
		if(mTextRight!=null)mTextRight.setVisibility(isVisible);
	}
	
	public void setLeftTextLeftMargin(int leftMargin){
		if(View.VISIBLE==mTextLeft.getVisibility()){
			((android.widget.LinearLayout.LayoutParams)mTextLeft.getLayoutParams()).leftMargin = leftMargin;
		}
	}
	
	public void setProgressBarVisibility(int visiable){
		progressBar.setVisibility(visiable);
	}
	
	public void setLeftVisibility(int visiable){
		if(mLeftButton!=null)mLeftButton.setVisibility(visiable);
	}
}
