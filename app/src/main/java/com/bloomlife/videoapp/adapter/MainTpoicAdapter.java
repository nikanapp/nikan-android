package com.bloomlife.videoapp.adapter;

import java.util.List;

import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public class MainTpoicAdapter extends AbstractAdapter<String> {
	
	private LayoutInflater mInflater;
	
	private boolean mEnabled = true;
	private String selectTopic ;

	public MainTpoicAdapter(FragmentActivity activity, List<String> dataList) {
		super(activity, dataList);
		mInflater = LayoutInflater.from(activity);
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		return mInflater.inflate(R.layout.item_main_topiclist, parent, false);
	}
	
	@Override
	protected void setViewContent(int position, View convertView, String item) {
		if (!mEnabled) return;
		CheckedTextView holder = (CheckedTextView) convertView;
		holder.setText("#"+item+" ");
		holder.setChecked(item.equals(selectTopic));
	}
	
	public void setEnabled(boolean enabled){
		mEnabled = enabled;
	}
	
	public boolean isEnabled(){
		return this.mEnabled;
	}

	public String getSelectTopic() {
		return selectTopic;
	}

	public void setSelectTopic(String selectTopic) {
		this.selectTopic = selectTopic;
	}


}
