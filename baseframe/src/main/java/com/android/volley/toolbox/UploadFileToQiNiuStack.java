package com.android.volley.toolbox;

import android.util.Log;

import com.bloomlife.android.log.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/22.
 */
public class UploadFileToQiNiuStack implements UploadFileHttpStack<UploadFileToQiNiuRequest>{

    @Override
    public void upload(final UploadFileToQiNiuRequest request){
        final UploadManager uploadManager = new UploadManager();
        final UploadFileRequest.Listener listener = request.getListener();
        // filterParam是自定义变量的设置，progress...是进度条，cancel...是分片上传判断是否上传需要终止的
        final UploadOptions uploadOptions = new UploadOptions(null, null, true, new UpProgressHandler() {

            @Override
            public void progress(String arg0, double progress) {
                if (listener != null) listener.progress(progress);
            }
        }, null);

        Logger.d("UploadFileHttpStack", " upload file key = " + request.getFilekey());

        // 正常流程
        uploadManager.put(request.getFilePath(), request.getFilekey(), request.getUploadtoken(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                Log.i("qiniu", info.toString());
                doUploadResult(listener, info, response);
            }
        }, uploadOptions);
    }

    /**
     * 	处理上传后返回的结果（证书过期的结果不在这里处理）
     * @param info	七牛sdk的调用结果
     * @param response	七牛返回的json数据
     */
    private static void doUploadResult(final UploadFileRequest.Listener listener, ResponseInfo info, JSONObject response) {
        if (QiniuConstants.Code_Suc != info.statusCode) {
            if (listener != null) listener.error(info.error);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        if (listener != null) {
            // 获取预览图生成的七牛任务id
            try {
                Iterator<String> keys = response.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    map.put(key, response.get(key));
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
            if (listener != null)listener.success(map);
        }
    }
}
