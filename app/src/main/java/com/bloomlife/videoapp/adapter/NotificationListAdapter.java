/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import static com.bloomlife.videoapp.common.util.UIHelper.ColorList;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.NotificationMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 *	视频通知列表的adapter
 * @date 2015年2月6日 下午3:04:33
 */
public class NotificationListAdapter extends AbstractAdapter<NotificationMessage> {
	
	private LayoutInflater mInflater;
	private DisplayImageOptions mOption;
	private Context mContext;
	private Random mRandom;

	public NotificationListAdapter(FragmentActivity activity, List<NotificationMessage> dataList) {
		super(activity, dataList);
		mInflater = LayoutInflater.from(activity);
		mOption = ImageLoaderUtils.getMsgImageLoadOption();
		mRandom = new Random();
		mContext = activity;
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		Holder holder = new Holder();
		View layout = mInflater.inflate(R.layout.item_notification, parent, false);
		holder.videoPreview  = (ImageView) layout.findViewById(R.id.item_notification_preview);
		holder.videoPlayIcon = (ImageView) layout.findViewById(R.id.item_notification_play);
		holder.redDot 	= (ImageView) layout.findViewById(R.id.item_notification_dot);
		holder.title 	= (TextView) layout.findViewById(R.id.item_notification_title);
		holder.content 	= (TextView) layout.findViewById(R.id.item_notification_content);
		holder.time     = (TextView) layout.findViewById(R.id.item_notification_time);
		holder.tag		= (ImageView) layout.findViewById(R.id.item_notification_tag);
		holder.userIcon = (ImageView) layout.findViewById(R.id.item_notification_usericon);
		holder.userIcon.setOnClickListener(mUserIconOnClick);
		holder.videoPreview.setBackgroundColor(Color.parseColor("#" + ColorList.get(mRandom.nextInt(ColorList.size() - 1))));
		layout.setTag(holder);
		return layout;
	}

	@Override
	protected void setViewContent(int position, View convertView, NotificationMessage item) {
		Holder holder = (Holder) convertView.getTag();
		holder.userIcon.setTag(item);
		holder.setVideoPreview(item);
	}

	private View.OnClickListener mUserIconOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			NotificationMessage message = (NotificationMessage) v.getTag();
			if (message.getMsgtype() == NotificationMessage.TYPE_STORY_COMMENT_TEXT || message.getMsgtype() == NotificationMessage.TYPE_STORY_COMMENT_TAG){
				// 实名模式下点击弹出用户信息对话框
				DialogUtils.showUserInfo(activity, message);
			} else if (message.getMsgtype() == NotificationMessage.TYPE_STORY_LIKE){
				DialogUtils.showLikeList(activity, message.getStoryId(), null);
			}
		}
	};

	class Holder{

		ImageView tag;
		ImageView userIcon;
		ImageView redDot;
		ImageView videoPreview;
		ImageView videoPlayIcon;
		TextView title;
		TextView content;
		TextView time;

		public void setVideoPreview(NotificationMessage item){
			// 判断是实名世界还是匿名世界来的通知
			if (item.isStoryMsg()){
				videoPreview.setVisibility(View.GONE);
				videoPlayIcon.setVisibility(View.GONE);
				userIcon.setVisibility(View.VISIBLE);

				tag.setImageResource(R.drawable.icon_notification_story);
				ImageLoader.getInstance().displayImage(item.getExtra().getUsericon(), userIcon, mOption);
				title.setText(item.getExtra().getUsername() + ":");
				content.setText(TextUtils.isEmpty(item.getMsg()) ? "" : item.getMsg());

			} else {
				videoPreview.setVisibility(View.VISIBLE);
				videoPlayIcon.setVisibility(View.VISIBLE);
				userIcon.setVisibility(View.GONE);

				tag.setImageResource(R.drawable.icon_notification_glimpse);
				ImageLoader.getInstance().displayImage(item.getPreviewurl(), videoPreview, mOption);
				title.setText(item.getMsg());
				content.setText(TextUtils.isEmpty(item.getDescription()) ? "" : item.getDescription());
			}

			// 未读的要显示红点
			redDot.setVisibility(item.getStatus() == NotificationMessage.STATUS_READ ? View.GONE : View.VISIBLE);
			time.setText(DateUtils.getTimeString(mContext, item.getCreatetime()/1000));
		}

	}

}
