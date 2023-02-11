/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年11月27日 下午7:21:26
 */
public class TopicAdapter extends AbstractAdapter<String> implements OnItemClickListener{
	
	private Context mContext;
	private Set<Integer> mSelectList;
	private LayoutInflater mInflater;
	
	public TopicAdapter(Activity activity, List<String> dataList) {
		super(activity, dataList);
		mContext = activity;
		mInflater = LayoutInflater.from(activity);
		mSelectList = new HashSet<Integer>();
	}

	public static final String TAG = TopicAdapter.class.getSimpleName();
	
	public void setSelectView(List<Integer> list){
		mSelectList.addAll(list);
	}
	
	public void removeSelectView(String topic){
		int index = getDataList().indexOf(topic);
		if (index != -1){
			mSelectList.remove(index);
		}
		notifyDataSetChanged();
	}
	
	public void clearSelectView(){
		mSelectList.clear();
		notifyDataSetChanged();
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		return mInflater.inflate(R.layout.item_topic, parent, false);
	}

	@Override
	protected void setViewContent(int position, View convertView, String item) {
		TextView itemView = (TextView) convertView;
		itemView.setText(item);
		if (mSelectList.contains(position)){
			itemView.setTextColor(mContext.getResources().getColor(R.color.topic_select));
		} else {
			itemView.setTextColor(mContext.getResources().getColor(R.color.gray));
		}
	}
	
	private OnTopicClickListener listener;
	
	public void setOnTopicClickListener(OnTopicClickListener listener){
		this.listener = listener;
	}
	
	public interface OnTopicClickListener{
		boolean addTopic(CharSequence text, int position);
		boolean removeTopic(CharSequence text, int position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (listener == null ){
			return;
		}
		TextView itemView = (TextView) view;
		if (mSelectList.remove(position)){
			listener.removeTopic(itemView.getText(), position);
			itemView.setTextColor(mContext.getResources().getColor(R.color.gray));
		} else if(mSelectList.size() < 3){
			if(!listener.addTopic(itemView.getText(), position)) return ;
			mSelectList.add(Integer.valueOf(position));
			itemView.setTextColor(mContext.getResources().getColor(R.color.topic_select));
		}
	}
	
}
