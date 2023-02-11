/**
 *
 */
package com.bloomlife.videoapp.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.model.ChatBean;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.NotificationCompat;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EasyUtils;

import net.tsz.afinal.FinalActivity;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * @date 2014-12-19  下午2:36:00
 */
public class ChatBaseActivity extends FragmentActivity {

    protected NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        EMChatManager.getInstance().activityResumed();
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
