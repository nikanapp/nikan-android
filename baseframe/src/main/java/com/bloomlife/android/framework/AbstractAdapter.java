/**
 * 
 */
package com.bloomlife.android.framework;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * 	  简单封装了常用操作的adapter
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-25  下午5:25:29
 */
public abstract  class AbstractAdapter<T> extends BaseAdapter{
	
	private static final String TAG = "AbstractAdapter";
	
	protected List<T> dataList ;
	
	protected Activity activity ;
	
	public AbstractAdapter (Activity activity , List<T> dataList){
		this.activity = activity ;
		this.dataList = dataList ;
	}

	@Override
	public int getCount() {
		return dataList==null?0:dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	long start = 0 ;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		T item = dataList.get(position);
		start = System.currentTimeMillis();
		if(convertView==null||convertView.getTag()==null){
			convertView = initItemView(position, parent);
		}
		
		setViewContent(position,convertView,item);
		return convertView;
	}
	
	/***
	 *   初始化列表的item的view。  
	 */
	protected abstract View initItemView(int position, ViewGroup parent);
	
	/***
	 *  设置item的数据内容和行为
	 *  
	 */
	protected abstract void setViewContent(int position, View convertView ,T item);
	
	public List<T> getDataList(){
		return dataList;
	}
	
	public void setDataList(List<T> dataList){
		this.dataList = dataList;
	}
	
	public void addDataList(List<T> dataList){
		if(this.dataList==null) this.dataList = dataList ;
		this.dataList.addAll(dataList);
	}
}
