/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.database.DataSetObserver;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.BitmapLoader;
import com.bloomlife.videoapp.model.VideoFileInfo;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年3月25日 下午3:08:34
 */
public class VideoFileListAdapter extends AbstractAdapter<VideoFileInfo> implements
		StickyGridHeadersSimpleAdapter {
	
	public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("MM月dd日,yyyy", Locale.CHINA);
	
	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;

	private LayoutInflater mInflater;
	private SparseArray<String> mTitleArray;
	private BitmapLoader mLoader;
	private int mTitlePaddingTop;
	
	public VideoFileListAdapter(FragmentActivity activity, List<VideoFileInfo> dataList, SparseArray<String> titleArray) {
		super(activity, dataList);
		mInflater = LayoutInflater.from(activity);
		mTitleArray = titleArray;
		mLoader = new BitmapLoader(activity);
		mTitlePaddingTop = UiUtils.dip2px(activity, 10);
	}

	@Override
	public long getHeaderId(int position) {
		for (int i=0; i<mTitleArray.size(); i++){
			int key = mTitleArray.keyAt(i);
			if (key > position){
				return key;
			}
		}
		return position;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		TextView title = new TextView(activity);
		title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, activity.getResources().getDimensionPixelSize(R.dimen.activity_videofilelist_header_height)));
		title.setPadding(0, mTitlePaddingTop, 0, 0);
		title.setTextColor(activity.getResources().getColor(R.color.black));
		title.setText(FORMAT_DATE.format(new Date( getDataList().get(position).getDate())));
		return title;
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		Holder holder = new Holder();
		View layout = mInflater.inflate(R.layout.item_video_file_list, parent, false);
		holder.mImage = (ImageView) layout.findViewById(R.id.item_video_file_image);
		holder.mDuration = (TextView) layout.findViewById(R.id.item_video_file_duration);
		layout.setTag(holder);
		return layout;
	}

	@Override
	protected void setViewContent(int position, View convertView,
			VideoFileInfo item) {
		Holder holder = (Holder) convertView.getTag();
		mLoader.load(holder.mImage, item.getId());
		holder.mDuration.setText(String.format("%02d", item.getDuration()/MINUTE)+":"+String.format("%02d", item.getDuration()%MINUTE/SECOND));
	}
	
	class Holder{
		ImageView mImage;
		TextView mDuration;
	}

}
