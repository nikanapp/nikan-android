package com.bloomlife.videoapp.app;

import android.content.Context;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.MessageRequest;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.view.SuperToast;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/16.
 */
public abstract class RequestErrorAlertListener<P extends ProcessResult> extends MessageRequest.Listener<P> {

    @Override
    public void error(VolleyError error) {
        super.error(error);
        Context context = AppContext.get();
        if (context != null){
            SuperToast.show(context, R.string.network_error_tips);
        }
    }
}
