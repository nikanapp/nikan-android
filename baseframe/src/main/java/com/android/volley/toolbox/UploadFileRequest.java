package com.android.volley.toolbox;


import java.util.Map;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/22.
 */
public class UploadFileRequest{

    private Listener mListener;

    private Map<String, String> mFileParams;

    public UploadFileRequest(Listener listener){
        mListener = listener;
    }

    public void setFileParams(Map<String, String> fileParams) {
        this.mFileParams = fileParams;
    }

    public Map<String, String> getFileParams(){
        return this.mFileParams;
    }

    public Listener getListener(){
        return mListener;
    }


    public interface Listener{

        void start();

        void progress(double pregress);

        void error(String msg);

        void success(Map<String, Object> attr);

    }


}
