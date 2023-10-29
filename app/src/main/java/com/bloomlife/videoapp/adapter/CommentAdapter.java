/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.Commenttags;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月2日 下午4:41:00
 */
public class CommentAdapter extends AbstractAdapter<Commenttags> {
	
	private LayoutInflater mInflater;
	private Resources mResources;
	private Context mContext;
	private String mSelectContent;
	
	public class Holder{
		public CheckedTextView commentTag;
	}
	
	public CommentAdapter(FragmentActivity activity, List<Commenttags> dataList) {
		super(activity, dataList);
		mInflater = LayoutInflater.from(activity);
		mResources = activity.getResources();
		mContext = activity;
	}
	
	public boolean setSelectComment(String commentName){
		if (mSelectContent != null && mSelectContent.equals(commentName)){
			mSelectContent = null;
			return false;
		} else {
			mSelectContent = commentName;
			return true;
		}
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		Holder holder = new Holder();
		View layout = mInflater.inflate(R.layout.item_comment, parent, false);
		holder.commentTag = (CheckedTextView) layout.findViewById(R.id.item_comment_tag);
		holder.commentTag.setTypeface(UIHelper.getHelveticaLt(mContext));
		layout.setTag(holder);
		return layout;
	}

	@Override
	protected void setViewContent(int position, View convertView, Commenttags item) {
		Holder holder = (Holder) convertView.getTag();
		int color = 0;
		try{
			color = Color.parseColor("#"+item.getColor());
		}catch(Exception e){
			e.printStackTrace();
		}
		holder.commentTag.setText(item.getContent());
		UIHelper.setVideoCommentTagSelector(mResources, holder.commentTag, color);
		if (item.getContent().equals(mSelectContent)){
			holder.commentTag.setTextColor(Color.BLACK);
			holder.commentTag.setChecked(true);
		} else {
			holder.commentTag.setChecked(false);
			holder.commentTag.setTextColor(color);
		}
	}

}
