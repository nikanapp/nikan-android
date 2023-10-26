package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Story;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class MainStorysAdapter extends BaseAdapter<Story> implements View.OnClickListener {

    class Holder {
        TextView ignites;
        TextView watch;
        TextView comment;
        TextView location;
        TextView createTime;
        ImageView preView;
        View infoLayout;
    }

    private Drawable mFemale;
    private Drawable mMale;

    public MainStorysAdapter(FragmentActivity activity, List<Story> dataList) {
        super(activity, dataList);
        mFemale = activity.getResources().getDrawable(R.drawable.icon_user_info_female);
        mFemale.setBounds(0, 0, mFemale.getMinimumWidth(), mFemale.getMinimumHeight());
        mMale = activity.getResources().getDrawable(R.drawable.icon_user_info_male);
        mMale.setBounds(0, 0, mFemale.getMinimumWidth(), mFemale.getMinimumHeight());
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        Holder h = new Holder();
        View layout = inflater.inflate(getItemLayoutResId(), parent, false);
        initPreView(h, layout);
        initStoryInfo(h, layout);
        layout.setTag(h);
        return layout;
    }

    protected int getItemLayoutResId(){
        return R.layout.item_user_story;
    }

    protected void initStoryInfo(Holder h, View layout){
        h.ignites = (TextView) layout.findViewById(R.id.item_story_likes);
        h.watch = (TextView) layout.findViewById(R.id.item_story_watch);
        h.comment = (TextView) layout.findViewById(R.id.item_story_comment);
        h.location = (TextView) layout.findViewById(R.id.item_story_location);
        h.createTime = (TextView) layout.findViewById(R.id.item_story_createtime);
        h.infoLayout = layout.findViewById(R.id.item_story_info_layout);
    }

    protected void initPreView(Holder h, View layout){
        h.preView = (ImageView) layout.findViewById(R.id.item_story_preview);
    }

    @Override
    protected void setViewContent(int position, View convertView, Story item) {
        Holder h = (Holder) convertView.getTag();
        h.infoLayout.setTag(item);
        h.infoLayout.setOnClickListener(this);
        setStoryInfo(h, item);
        setPreView(h, item);
    }

    protected void setStoryInfo(Holder h, Story item){
        h.ignites.setText(getStringNum(item.getLikenum()));
        h.watch.setText(getStringNum(item.getLooknum()));
        h.comment.setText(getStringNum(item.getCommentnum()));
        h.createTime.setText(DateUtils.getTimeString(activity, item.getSectime()));
        h.location.setText(item.getCity());
    }

    protected String getStringNum(int num){
        if (num > 9999){
            return "9999+";
        } else {
            return String.valueOf(num);
        }
    }

    protected void setPreView(Holder h, Story item){
        h.preView.setImageBitmap(null);
        String url = CacheBean.getInstance().getString(activity, item.getUid());
        if (!TextUtils.isEmpty(url)){
            mImageLoader.displayImage("file://"+url, h.preView, mOption);
        } else {
            mImageLoader.displayImage(item.getBigpreviewurl(), h.preView, mOption);
        }
    }

    protected void setGenderIcon(TextView name, Story item){
        if (Constants.FEMALE.equals(item.getGender())){
            name.setCompoundDrawables(null, null, mFemale, null);
        } else {
            name.setCompoundDrawables(null, null, mMale, null);
        }
    }

    static class ProgressListener implements ImageLoadingProgressListener {


        private View mProgerssBar;

        public ProgressListener(View progressbar){
            mProgerssBar = progressbar;
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            if (current <= 1){
                mProgerssBar.setVisibility(View.VISIBLE);
            } else {
                mProgerssBar.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_story_info_layout:
                Story story = (Story) v.getTag();
                DialogUtils.showLikeList(activity, story.getId(), null);
                break;
        }
    }

}