/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.dialog.DialogUtils;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 视频评论页面的文字列表
 * @date 2015年1月30日 下午3:30:51
 */
public class CommentTextListAdapter extends BaseAdapter<CommentText> {

	private String mVideoUid;
	private Context mContext;
	
	class Holder{
		private ImageView symbolIcon;
		private ImageView userIcon;
		private TextView userName;
		private TextView commentText;
		private TextView commentTime;
		private View divider;
	}
	
	public CommentTextListAdapter(FragmentActivity activity, List<CommentText> dataList, String videoUid) {
		super(activity, dataList);
		mVideoUid = videoUid;
		mContext = activity;
	}

	@Override
	protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
		Holder holder = new Holder();
		View view = inflater.inflate(R.layout.item_video_comment_text, parent, false);
		holder.symbolIcon = (ImageView) view.findViewById(R.id.comment_icon);
		holder.commentText = (TextView) view.findViewById(R.id.comment_text);
		holder.commentTime = (TextView) view.findViewById(R.id.comment_time);
		holder.userIcon = (ImageView) view.findViewById(R.id.comment_user_icon);
		holder.userName = (TextView) view.findViewById(R.id.comment_user_name);
		holder.divider = view.findViewById(R.id.comment_divider);
//		holder.userName.setTypeface(UIHelper.getHelveticaLt(activity));
		view.setTag(holder);
		return view;
	}

	@Override
	protected void setViewContent(int position, View convertView, CommentText item) {
		Holder holder = (Holder) convertView.getTag();
		holder.commentText.setText(item.getContent());
		// 最后一条要隐藏掉分割线
		holder.divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);

		holder.commentTime.setText(DateUtils.getTimeString(mContext, item.getSectime() == 0L ? item.getCreatetime() : item.getSectime()));

		// 用户名为空，说明不是story模式下进来的，隐藏掉用户名和头像
		if (TextUtils.isEmpty(item.getUsername())){
			if (CacheBean.getInstance().getLoginUserId().equals(item.getUserid())){
				holder.symbolIcon.setImageResource(R.drawable.comment_me_tag);
				holder.symbolIcon.setVisibility(View.VISIBLE);
			} else if (mVideoUid.equals(item.getUserid())){
				holder.symbolIcon.setImageResource(R.drawable.comment_user_tag);
				holder.symbolIcon.setVisibility(View.VISIBLE);
			} else {
				holder.symbolIcon.setVisibility(View.GONE);
			}

			holder.userIcon.setVisibility(View.GONE);
			holder.userName.setVisibility(View.GONE);
		} else {
			holder.symbolIcon.setVisibility(View.GONE);

			holder.userName.setText(item.getUsername());
			Drawable genderIcon = activity.getResources().getDrawable(
							Constants.MALE.equals(item.getGender())
							? R.drawable.icon_user_info_male
							: R.drawable.icon_user_info_female);
			genderIcon.setBounds(0, 0, genderIcon.getMinimumWidth(), genderIcon.getMinimumHeight());
			holder.userName.setCompoundDrawables(null, null, genderIcon, null);
			mImageLoader.displayImage(item.getUsericon(), holder.userIcon, mOption);
			holder.userIcon.setOnClickListener(mUserIconClickListener);
			holder.userIcon.setTag(item);
		}
	}

	private View.OnClickListener mUserIconClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			DialogUtils.showUserInfo(activity, (CommentText) v.getTag());
		}
	};

}
