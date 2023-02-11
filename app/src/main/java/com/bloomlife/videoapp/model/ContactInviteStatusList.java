package com.bloomlife.videoapp.model;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.common.CacheKeyConstants;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/7.
 */
public class ContactInviteStatusList {

    public static void set(Context context, List<String> stories){
        CacheBean.getInstance().putString(context, CacheKeyConstants.INVITE_STATUS, JSON.toJSONString(stories));

    }

    public static List<String> get(Context context){
        String cacheStr = CacheBean.getInstance().getString(context, CacheKeyConstants.INVITE_STATUS);
        return TextUtils.isEmpty(cacheStr) ? null : JSON.parseObject(cacheStr, new TypeReference<List<String>>() {
        });
    }
}
