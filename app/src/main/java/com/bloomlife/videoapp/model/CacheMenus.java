package com.bloomlife.videoapp.model;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.common.CacheKeyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/17.
 */
public class CacheMenus {

    public static void set(Context context, List<DymainicMenu> stories){
        CacheBean cacheBean = CacheBean.getInstance();
        cacheBean.putString(context, CacheKeyConstants.KEY_CACHE_DYMAINIC_MENUS, JSON.toJSONString(stories));

    }

    public static List<DymainicMenu> get(Context context){
        CacheBean cacheBean = CacheBean.getInstance();
        String cacheStr = cacheBean.getString(context, CacheKeyConstants.KEY_CACHE_DYMAINIC_MENUS);
        return TextUtils.isEmpty(cacheStr) ? new ArrayList<DymainicMenu>() : JSON.parseObject(cacheStr, new TypeReference<List<DymainicMenu>>() {});
    }
}
