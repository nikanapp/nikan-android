/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import static com.bloomlife.videoapp.common.util.UIHelper.ColorList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.VideoViewPagerActivity;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.bloomlife.videoapp.model.Video;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-17  下午5:22:50
 */
public class ConversationMessageAdapter extends AbstractAdapter<ConversationMessage> implements OnClickListener{

	private static final int REAL_ITEM_ID = 0;
	private static final int ANON_ITEM_ID = 1;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = ImageLoaderUtils.getMsgImageLoadOption();
	
	private Random mRandom;
	private Context mContext;
	
	public ConversationMessageAdapter(Activity activity, List<ConversationMessage> dataList) {
		super(activity, dataList);
		mRandom = new Random();
		mContext = activity;
	}

	private boolean isReal(int position){
		return isReal(getDataList().get(position));
	}

	private boolean isReal(ConversationMessage msg){
		return !TextUtils.isEmpty(msg.getUserName());
	}

	@Override
	public int getItemViewType(int position) {
		if (isReal(position)){
			return REAL_ITEM_ID;
		} else {
			return ANON_ITEM_ID;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		if (isReal(position)){
			return initRealItemView(position, parent);
		} else {
			return initAnonItemView(position, parent);
		}
	}

	private View initAnonItemView(int position, ViewGroup parent){
		AnonHolder holder = new AnonHolder();
		View view = LayoutInflater.from(activity).inflate(R.layout.item_anon_message, parent, false);
		holder.preview 	= (ImageView) view.findViewById(R.id.anon_message_preview);
		holder.msgPlay 	= (ImageView) view.findViewById(R.id.anon_message_msgplay);
		holder.newDot 	= (ImageView) view.findViewById(R.id.anon_message_newdot);
		holder.time		= (TextView) view.findViewById(R.id.anon_message_time);
		holder.msgContent = (TextView) view.findViewById(R.id.anon_message_msgcontent);
		holder.preview.setBackgroundColor(Color.parseColor("#" + ColorList.get(mRandom.nextInt(ColorList.size() - 1))));
		view.setTag(holder);
		return view;
	}

	private View initRealItemView(int position, ViewGroup parent){
		RealHolder holder = new RealHolder();
		View view = LayoutInflater.from(activity).inflate(R.layout.item_real_message, parent, false);
		holder.userIcon = (ImageView) view.findViewById(R.id.real_message_preview);
		holder.newDot 	= (ImageView) view.findViewById(R.id.real_message_newdot);
		holder.time		= (TextView) view.findViewById(R.id.real_message_time);
		holder.msgContent = (TextView) view.findViewById(R.id.real_message_msgcontent);
		holder.userIcon.setImageDrawable(new ColorDrawable(Color.parseColor("#" + ColorList.get(mRandom.nextInt(ColorList.size() - 1)))));
		view.setTag(holder);
		return view;
	}

	@Override
	protected void setViewContent(int position, View convertView, ConversationMessage item) {
		if (isReal(position)){
			setRealItemView(position, convertView, item);
		} else {
			setAnonItemView(position, convertView, item);
		}
	}

	private void setAnonItemView(int position, View convertView, ConversationMessage item){
		AnonHolder holder = (AnonHolder) convertView.getTag();
		if(ConversationMessage.STATUS_UNREAD==item.getStatus()){
			holder.newDot.setVisibility(View.VISIBLE);
		} else holder.newDot.setVisibility(View.INVISIBLE);

		holder.msgContent.setText(item.getContent());
		imageLoader.displayImage(item.getPreviewUrl(), holder.preview, options);
		holder.time.setText(DateUtils.getTimeString(mContext, item.getUpdateTime().getTime() / 1000));
		holder.msgPlay.setOnClickListener(this);
		holder.msgPlay.setTag(item);

		convertView.setTag(R.id.id_msg, item);
	}

	private void setRealItemView(int position, View convertView, ConversationMessage item){
		RealHolder holder = (RealHolder) convertView.getTag();
		if (ConversationMessage.STATUS_UNREAD == item.getStatus()){
			holder.newDot.setVisibility(View.VISIBLE);
		} else {
			holder.newDot.setVisibility(View.INVISIBLE);
		}
		holder.msgContent.setText(item.getUserName() + ":\n" + item.getContent());
		imageLoader.displayImage(item.getUserIcon(), holder.userIcon, options);
		holder.time.setText(DateUtils.getTimeString(mContext, item.getUpdateTime().getTime() / 1000));

		convertView.setTag(R.id.id_msg, item);
	}

	
	private class AnonHolder {
		private ImageView msgPlay ;
		private ImageView preview ;
		private ImageView newDot ;
		private TextView msgContent ;
		private TextView time;
	}

	private class RealHolder {
		private ImageView userIcon;
		private ImageView newDot;
		private TextView msgContent;
		private TextView time;
	}


	@Override
	public void onClick(View v) {
		ConversationMessage msg = (ConversationMessage) v.getTag();
		Video video = new Video();
		video.setVideouri(msg.getVideouri());
		video.setVideoid(msg.getVideoId());
		List<Video> videos =new ArrayList<Video>();
		videos.add(video);
		Intent intent = new Intent(activity, VideoViewPagerActivity.class);
		intent.putParcelableArrayListExtra(VideoViewPagerActivity.INTENT_VIDEO_LIST,(ArrayList<? extends Parcelable>) videos);
		intent.putExtra(VideoViewPagerActivity.INTENT_VIDEO_POSITION,0);
		intent.putExtra(VideoViewPagerActivity.INTENT_FROM_LETTER, true);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.activity_camera_in, 0);
	}
}
