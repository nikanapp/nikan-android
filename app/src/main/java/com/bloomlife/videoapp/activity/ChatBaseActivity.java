/**
 *
 */
package com.bloomlife.videoapp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bloomlife.android.framework.BaseActivity;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * @date 2014-12-19  下午2:36:00
 */
public class ChatBaseActivity extends BaseActivity {

    protected NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     *
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
//	    	MyHXSDKHelper.getInstance().notifyNewMessage(message, this);
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

}
