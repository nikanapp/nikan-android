package com.android.volley.toolbox;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.MessageException;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.network.HttpProtocolEntry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/20.
 * 接口请求
 */
public class MessageRequest extends StringRequest {

    private Map<String, String> mParams;

    public MessageRequest(BaseMessage message){
        this(message, new ProcessReqListener());
    }

    public MessageRequest(BaseMessage message, Listener<? extends ProcessResult> listener){
        super(Method.POST, HttpProtocolEntry.URL + "/" + message.getMsgCode() + "/", listener.mListener, listener.mErrorListener);
        try {
            mParams = message.getParams();
        } catch (MessageException e) {
            e.printStackTrace();
        }
        setShouldCache(true);
    }

    protected MessageRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    protected MessageRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams != null)
            Logger.d("MessageRequest", "Request : "+mParams.toString());
        return mParams;
    }

    protected void setParams(Map<String, String> params){
        mParams = params;
    }

    public static class ProcessReqListener extends Listener<ProcessResult> {

        @Override
        public void success(ProcessResult result) {

        }
    }

    public abstract static class Listener<R extends ProcessResult> {

        public void start(){

        }

        abstract public void success(R result);

        public void error(VolleyError error){

        }

        public void failure(FailureResult result){

        }

        public void finish(){

        }

        private Response.Listener<String> mListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.d("MessageRequest", "Response : " + response);
                try {
                    R result =  (R)JSON.parseObject(response, getGenericType(0));
                    if (result.getCode() == ProcessResult.SUC){
                        Listener.this.success(result);
                    } else {
                        Listener.this.failure(new FailureResult(result.getCode(), result.getSubCode(), result.getSubDes()));
                    }
                } catch (JSONException e){
                    Listener.this.failure(new FailureResult(-1, -1, e.getMessage()));
                }
                Listener.this.finish();
            }
            @Override
            public void onStart() {
                Listener.this.start();
            }
        };

        private Response.ErrorListener mErrorListener =  new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Listener.this.error(error);
                Listener.this.finish();
            }
        };

        public Response.Listener<String> getListener(){
            return mListener;
        }

        public Response.ErrorListener getErrorListener(){
            return mErrorListener;
        }

        public Class<?> getGenericType(int index) {
            Type genType = getClass().getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return Object.class;
            }
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index >= params.length || index < 0) {
                throw new RuntimeException("Index outof bounds");
            }
            if (!(params[index] instanceof Class)) {
                return Object.class;
            }
            return (Class<?>) params[index];
        }

    }
}
