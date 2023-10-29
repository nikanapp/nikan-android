package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.Emotion;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/17.
 */
public class EmotionListAdapter extends BaseAdapter<Emotion> implements View.OnClickListener {

    private int mSelect;

    public EmotionListAdapter(FragmentActivity activity, List<Emotion> dataList) {
        super(activity, dataList);
    }

    public void setSelected(int position){
        mSelect = position;
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        Holder holder = new Holder();
        View view = inflater.inflate(R.layout.item_emotion, parent, false);
        holder.emotionImg = (ImageView) view.findViewById(R.id.item_emotion_img);
        holder.emotionName = (TextView) view.findViewById(R.id.item_emotion_name);
        holder.emotionNone = (TextView) view.findViewById(R.id.item_emotion_none);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void setViewContent(int position, View convertView, Emotion item) {
        Holder holder = (Holder) convertView.getTag();
        if (position == 0){
            holder.emotionNone.setVisibility(View.VISIBLE);
            holder.emotionImg.setVisibility(View.INVISIBLE);
            holder.emotionName.setVisibility(View.INVISIBLE);
            holder.emotionNone.setSelected(position == mSelect ? true : false);
        } else {
            holder.emotionNone.setVisibility(View.GONE);
            holder.emotionImg.setVisibility(View.VISIBLE);
            holder.emotionName.setVisibility(View.VISIBLE);
            holder.emotionName.setText(UIHelper.isZH() ? item.getEmotionname() : item.getEmotionenname());
            holder.emotionName.setSelected(position == mSelect ? true : false);
            holder.emotionImg.setSelected(position == mSelect ? true : false);
            if (item.getEmotionicon().contains("http")){
                mImageLoader.displayImage(item.getEmotionicon(), holder.emotionImg, mOption);
            } else {
                int id = getResources().getIdentifier(item.getFrameprefix(), "drawable", activity.getPackageName());
                holder.emotionImg.setImageResource(id);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    class Holder{
        ImageView emotionImg;
        TextView emotionName;
        TextView emotionNone;
    }
}
