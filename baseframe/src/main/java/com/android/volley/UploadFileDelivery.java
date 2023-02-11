package com.android.volley;

import android.os.Handler;

import com.android.volley.toolbox.UploadFileRequest;

import java.util.Map;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/22.
 */
public class UploadFileDelivery {

    private Handler mUiHandler;

    public UploadFileDelivery(Handler uiHandler){
        mUiHandler = uiHandler;
    }

    public void start(final UploadFileRequest.Listener listener){
        if (listener == null) return;
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.start();
            }
        });
    }

    public void progress(final UploadFileRequest.Listener listener, final int progress){
        if (listener == null) return;
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.progress(progress);
            }
        });
    }

    public void error(final UploadFileRequest.Listener listener, final String msg){
        if (listener == null) return;
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.error(msg);
            }
        });
    }

    public void success(final UploadFileRequest.Listener listener, final Map<String, Object> attrs){
        if (listener == null) return;
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.success(attrs);
            }
        });
    }
}
