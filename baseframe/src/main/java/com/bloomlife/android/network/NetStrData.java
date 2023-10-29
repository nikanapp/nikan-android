package com.bloomlife.android.network;

import java.util.List;
import java.util.Map;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/20.
 */
public class NetStrData {

    public final Map<String, List<String>> headerFields;
    public final String result;

    public NetStrData(Map<String, List<String>> headerFields, String result){
        this.headerFields = headerFields;
        this.result = result;
    }
}
