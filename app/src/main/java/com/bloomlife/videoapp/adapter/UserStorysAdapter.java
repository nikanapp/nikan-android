package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.StoryVideo;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.List;
import java.util.Random;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/4.
 */
public class UserStorysAdapter extends BaseAdapter<StoryVideo> {

    private int mLike;
    private int mLook;
    private int mComment;
    private Random mRandom;

    public UserStorysAdapter(FragmentActivity activity, List<StoryVideo> dataList) {
        super(activity, dataList);
        mRandom = new Random();
    }

    public void setStoryInfo(int like, int look, int comment){
        mLike = like;
        mLook = look;
        mComment = comment;
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        Holder h = new Holder();
        View layout = inflater.inflate(R.layout.item_user_story, parent, false);
        h.ignites = (TextView) layout.findViewById(R.id.item_story_likes);
        h.watch = (TextView) layout.findViewById(R.id.item_story_watch);
        h.comment = (TextView) layout.findViewById(R.id.item_story_comment);
        h.location = (TextView) layout.findViewById(R.id.item_story_location);
        h.createTime = (TextView) layout.findViewById(R.id.item_story_createtime);
        h.preView = (ImageView) layout.findViewById(R.id.item_story_preview);
        String color = "#"+ UIHelper.ColorList.get(mRandom.nextInt(UIHelper.ColorList.size()-1));
        layout.setBackgroundColor(Color.parseColor(color));
        layout.setTag(h);
        return layout;
    }

    @Override
    protected void setViewContent(int position, View convertView, StoryVideo item) {
        Holder h = (Holder) convertView.getTag();
        h.ignites.setText(String.valueOf(mLike));
        h.watch.setText(String.valueOf(mLook));
        h.comment.setText(String.valueOf(mComment));
        h.createTime.setText(DateUtils.getTimeString(activity, item.getSectime()));
        h.location.setText(item.getCity());
        h.preView.setImageBitmap(null);
        mImageLoader.displayImage(item.getBigpreviewurl(), h.preView, mOption, mImageLoadingListener, mProgressListener);
    }

    private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };

    private ImageLoadingProgressListener mProgressListener = new ImageLoadingProgressListener() {
        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {

        }
    };

    public int getComment() {
        return mComment;
    }

    public int getLike() {
        return mLike;
    }

    public int getLook() {
        return mLook;
    }

    class Holder {
        TextView ignites;
        TextView watch;
        TextView comment;
        TextView location;
        TextView createTime;
        ImageView preView;
    }

}
