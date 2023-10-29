package com.android.volley.toolbox;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/27.
 */
public interface UploadFileHttpStack<R extends UploadFileRequest> {
    void upload(R request);
}
