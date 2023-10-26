package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Story;

import java.util.List;
import java.util.Random;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/3.
 */
public class MainStoryListAdapter extends MainStorysAdapter {

    class MainHolder extends MainStorysAdapter.Holder{
        TextView userName;
        ImageView userIcon;
    }

    private Random mRandom;

    private int mWidth;
    private int mHeight;

    public MainStoryListAdapter(FragmentActivity activity, List<Story> dataList) {
        super(activity, dataList);
        mWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mHeight = mWidth / UiUtils.dip2px(activity, 360) * UiUtils.dip2px(activity, 203);
        mRandom = new Random();
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        MainHolder h = new MainHolder();
        View layout = inflater.inflate(R.layout.item_main_story, parent, false);
        h.userName = (TextView) layout.findViewById(R.id.item_main_story_username);
        h.userIcon = (ImageView) layout.findViewById(R.id.item_main_story_usericon);
        initPreView(h, layout);
        initStoryInfo(h, layout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = mHeight;
        params.width = mWidth;
        layout.setLayoutParams(params);
        layout.setTag(h);
        String color = "#"+ UIHelper.ColorList.get(mRandom.nextInt(UIHelper.ColorList.size()-1));
        layout.setBackgroundColor(Color.parseColor(color));
        return layout;
    }

    public void setStoryInfo(View view, Story story){
        TextView commentText = (TextView) view.findViewById(R.id.item_story_comment);
        TextView lookText = (TextView) view.findViewById(R.id.item_story_watch);
        TextView likeText = (TextView) view.findViewById(R.id.item_story_likes);
        commentText.setText(getStringNum(story.getCommentnum()));
        lookText.setText(getStringNum(story.getLooknum()));
        likeText.setText(getStringNum(story.getLikenum()));
    }

    @Override
    protected void setViewContent(int position, View convertView, Story item) {
        super.setViewContent(position, convertView, item);
        MainHolder h = (MainHolder) convertView.getTag();
        h.userName.setText(item.getUsername());
        h.userIcon.setOnClickListener(this);
        h.userIcon.setTag(position);
        h.userIcon.setImageBitmap(null);
        mImageLoader.displayImage(item.getUsericon(), h.userIcon, mOption);
        setGenderIcon(h.userName, item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.item_main_story_usericon:
                int position = (Integer)v.getTag();
                DialogUtils.showUserInfo(activity, getDataList().get(position));
                break;
        }
    }

}
