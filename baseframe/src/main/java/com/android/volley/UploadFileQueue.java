package com.android.volley;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.toolbox.UploadFileHttpStack;
import com.android.volley.toolbox.UploadFileRequest;
import com.android.volley.toolbox.UploadFileToQiNiuRequest;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/22.
 */
public class UploadFileQueue {

    private static final int DEFAULT_POOL_SIZE = 1;

    private ExecutorService mExecutor;
    private UploadFileHttpStack mStack;
    private UploadFileDelivery mDelivery;
    private int mPoolSize;

    public UploadFileQueue(){
        this(DEFAULT_POOL_SIZE);
    }

    public UploadFileQueue(int poolSize){
        if (poolSize <= 0)
            throw new IllegalArgumentException();
        mPoolSize = poolSize;
    }

    public void setStack(UploadFileHttpStack stack){
        mStack = stack;
    }

    public void start(){
        stop();
        mExecutor = Executors.newFixedThreadPool(mPoolSize);
        mDelivery = new UploadFileDelivery(new Handler(Looper.getMainLooper()));
    }

    public void stop(){
        if (mExecutor != null)
            mExecutor.shutdownNow();
    }

    public void add(UploadFileRequest request){
        Set<Map.Entry<String, String>> set = request.getFileParams().entrySet();
        if (set == null || mExecutor == null) return;
        for (Map.Entry<String, String> e:set){
            mExecutor.execute(new UploadFileRunnable(request, mStack, e, mDelivery));
        }
    }

    public void add(UploadFileToQiNiuRequest request){
        if (request == null || mExecutor == null) return;
        mExecutor.execute(new UploadVideoRunnable(request, mStack));
    }

    static class UploadFileRunnable implements Runnable{

        private UploadFileRequest mRequest;
        private UploadFileHttpStack mStack;
        private Map.Entry<String, String> mEntry;
        private UploadFileDelivery mDelivery;

        public UploadFileRunnable(UploadFileRequest request, UploadFileHttpStack stack, Map.Entry<String, String> entry, UploadFileDelivery delivery){
            mRequest = request;
            mStack = stack;
            mEntry = entry;
            mDelivery = delivery;
        }

        @Override
        public void run() {
            //mStack.upload(mRequest, mEntry, mDelivery);
        }
    }

    static class UploadVideoRunnable implements Runnable{

        private UploadFileToQiNiuRequest mRequest;
        private UploadFileHttpStack mStack;

        UploadVideoRunnable(UploadFileToQiNiuRequest request, UploadFileHttpStack stack){
            mRequest = request;
            mStack = stack;
        }

        @Override
        public void run() {
            mStack.upload(mRequest);
        }
    }

}
