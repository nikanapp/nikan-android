package com.bloomlife.videoapp.model;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.common.CacheKeyConstants;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/9/25.
 */
public class CacheEmotions {

    public static void set(Context context, List<Emotion> stories){
        CacheBean cacheBean = CacheBean.getInstance();
        cacheBean.putString(context, CacheKeyConstants.KEY_CACHE_EMOTIONS, JSON.toJSONString(stories));

    }

    public static List<Emotion> get(Context context){
        CacheBean cacheBean = CacheBean.getInstance();
        String cacheStr = cacheBean.getString(context, CacheKeyConstants.KEY_CACHE_EMOTIONS);
        return TextUtils.isEmpty(cacheStr) ? null : JSON.parseObject(cacheStr, new TypeReference<List<Emotion>>() {});
    }
}
