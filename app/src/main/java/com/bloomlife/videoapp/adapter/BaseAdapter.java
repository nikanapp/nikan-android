package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public abstract class BaseAdapter<T> extends AbstractAdapter<T> {

    protected LayoutInflater mInflater;
    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mOption;

    public BaseAdapter(Activity activity, List<T> dataList) {
        super(activity, dataList);
        mInflater = LayoutInflater.from(activity);
        mImageLoader = ImageLoader.getInstance();
        mOption = ImageLoaderUtils.getDecodingOptions();
    }

    @Override
    protected View initItemView(int position, ViewGroup parent) {
        return initItemView(position, parent, mInflater);
    }

    abstract protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater);

    public Resources getResources(){
        return activity.getResources();
    }
}
