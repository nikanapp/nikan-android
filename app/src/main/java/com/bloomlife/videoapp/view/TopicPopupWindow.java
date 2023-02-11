package com.bloomlife.videoapp.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.MainTpoicAdapter;
import com.bloomlife.videoapp.common.util.UIHelper;

public class TopicPopupWindow extends FrameLayout implements OnClickListener {

	public TopicPopupWindow(Context context) {
		super(context);
		init(context);
	}

	public TopicPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TopicPopupWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private static final int MAX_LIST_HEIGHT = 312;
	
	private Handler mHandler = new Handler();
	
	private ViewGroup mTopicLayout;
	private ListView mTopicList;
	private TextView mTopicEmptyView;
	private MainTpoicAdapter mAdapter;
	private TopicAnimButton animButton;
	private Animation animOut;
	private Animation animIn;
	
	private boolean mIsShow;
	
	private void init(Context context){
		createLayout(context);
		setOnClickListener(this);
	}

	private void createLayout(Context context){
		inflate(context, R.layout.popupwindow_topic, this);
		
		TextView title = (TextView) findViewById(R.id.popupwindow_topic_text);
		title.setTypeface(UIHelper.getBebas(context));
		
		mTopicLayout 	= (ViewGroup) findViewById(R.id.popupwindow_topic);
		mTopicEmptyView = (TextView) findViewById(R.id.popupwindow_topic_list_null);
		mTopicList 		= (ListView) findViewById(R.id.popupwindow_topic_list);
		mTopicList.setVerticalScrollBarEnabled(false);
		mAdapter = new MainTpoicAdapter((Activity)context, null);
		mTopicList.setAdapter(mAdapter);
		animOut = AnimationUtils.loadAnimation(getContext(), R.anim.topiclist_window_out);
		animIn  = AnimationUtils.loadAnimation(getContext(), R.anim.topiclist_window_in);
	}
	
	public void setTopicList(List<String> list){
		if(!StringUtils.isEmpty(mAdapter.getSelectTopic())){
			if(list!=null && !list.contains(mAdapter.getSelectTopic())){
				mAdapter.setSelectTopic(null); //情况上一次选择的话题
			}
		}
		if (list == null || list.isEmpty() || TextUtils.isEmpty(list.get(0).trim())){
			mAdapter.setDataList(null);
			mTopicEmptyView.setVisibility(View.VISIBLE);
		} else {
			mAdapter.setDataList(list);
			mTopicEmptyView.setVisibility(View.INVISIBLE);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	public void setTopicItemClickListener(OnItemClickListener itemClickListener){
		mTopicList.setOnItemClickListener(itemClickListener);
	}
	
	public void show(){
		if (mIsShow) return;
		mIsShow = true;
		setVisibility(View.VISIBLE);
		mTopicLayout.setVisibility(View.VISIBLE);
		setTopicListSize();
		mTopicLayout.startAnimation(animIn);
		mTopicList.setEnabled(false);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mTopicList.setEnabled(true);
			}
		}, 250);
	}

	/**
	 * 设置列表长度，防止列表过长
	 */
	private void setTopicListSize(){
		if (mTopicList.getMeasuredHeight() == 0 && mTopicList.getAdapter().getCount() > 8){
			setTopicListMaxHeight();
			return;
		}
		if (mTopicList.getMeasuredHeight() > UiUtils.dip2px(getContext(), MAX_LIST_HEIGHT)){
			setTopicListMaxHeight();
			return;
		}
	}
	
	private void setTopicListMaxHeight(){
		ViewGroup.LayoutParams params = mTopicList.getLayoutParams();
		params.height = UiUtils.dip2px(getContext(), MAX_LIST_HEIGHT);
		mTopicList.setLayoutParams(params);
	}
	
	/**
	 * 恢复列表大小的初始状态，不然列表有可能一直是MAX_LIST_HEIGHT高度了。
	 */
	private void restoreTopicListSize(){
		ViewGroup.LayoutParams params = mTopicList.getLayoutParams();
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		mTopicList.setLayoutParams(params);
	}
	
	public void dismiss() {
		if (!mIsShow) return;
		mIsShow = false;
		mTopicLayout.setVisibility(View.INVISIBLE);
		mTopicLayout.startAnimation(animOut);
		animOut.setAnimationListener(animOutListener);
		if(!animButton.isAnimationFinish()) 
			return;
		if (mListener != null){
			mListener.onDismiss();
		}
	}
	
	public boolean isShowing(){
		return getVisibility() == View.VISIBLE ? true : false;
	}
	
	private WindowListener mListener;
	
	public void setWindowListener(WindowListener listener){
		mListener = listener;
	}
	
	public String getSelectTopic() {
		return mAdapter.getSelectTopic();
	}

	public void setSelectTopic(String selectTopic) {
		mAdapter.setSelectTopic(selectTopic);
	}

	public TopicAnimButton getAnimButton() {
		return animButton;
	}

	public void setAnimButton(TopicAnimButton animButton) {
		this.animButton = animButton;
	}

	public interface WindowListener{
		void onDismiss();
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}
	
	private Animation.AnimationListener animOutListener = new Animation.AnimationListener() {
		
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
			setVisibility(View.INVISIBLE);
			restoreTopicListSize();
		}
	};
}
