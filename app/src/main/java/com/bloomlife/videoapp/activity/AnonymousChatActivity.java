package com.bloomlife.videoapp.activity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.AnonymousChatAdapter;
import com.bloomlife.videoapp.adapter.RealNameChatAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Database;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.common.util.SystemUtils;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.view.SuperToast;
import com.easemob.chat.EMMessage;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_CITY;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_PRVIEWURI;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_SEX;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VERSION;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VIDEO_ID;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VIDEO_URI;

/**
 * Description:
 * User: ZhengXingtian(lan4627@Gmail.com)
 * Date: 2015-08-16
 * Time: 04:15
 * Version: 1.0
 * @parameter INTENT_USERNAME String 从消息页面进来时，传对话方的用户名
 * @parameter INTENT_CHAT_ID String 从消息页面进来时，传ID
 * @parameter INTENT_VIDEO Video 从视频播放页面跳转时，需要传视频实体进来
 * @parameter INTENT_FROM_NOTIFICATION boolean 是从通知栏跳转进来
 */
public class AnonymousChatActivity extends RealNameChatActivity {

    private static final String TAG = AnonymousChatActivity.class.getSimpleName();

    public static final String INTENT_VIDEO = "intent_video";
    public static final String INTENT_CHAT_ID = "intent_chat_id";

    private AnonymousChatAdapter adapter;

    private Toast mStatusToast;

    private boolean isCustom;

    private Video video;

    /**
     * 检查是否客服对话
     */
    @Override
    protected void initChatData(){
        toChatUsername = getIntent().getExtras().getString(INTENT_USERNAME);
        mChatId = getIntent().getExtras().getString(INTENT_CHAT_ID);
        video = getIntent().getExtras().getParcelable(INTENT_VIDEO);
        if (StringUtils.isEmpty(toChatUsername)) {
            toChatUsername = video.getUid();
            mChatId = video.getVideoid();
        }
        isCustom = toChatUsername.equals(getString(R.string.custom_name));
        if (isCustom){
            mChatId = getString(R.string.custom_video_id);
            video = new Video();
            video.setVideoid(getString(R.string.custom_video_id));
            video.setVideouri("");
        }
        Assert.assertNotNull("私信用户不能为空", toChatUsername);
        Assert.assertNotNull("ChatId不能为空", mChatId);
    }

    @Override
    protected RealNameChatAdapter initChatList(List<ChatBean> chatList){
        Database.ChatCacheData otherData = Database.checkChatBeanVersion(this, 140, toChatUsername, mChatId);
        if (Utils.isCollectionEmpty(chatList)) {
            // 当没有对话时，添加一条拍照提示语
            ChatBean tipsBean = ChatBean.makeTipsChatBean(ChatBean.WAIT_OTHER_MSG, toChatUsername, mChatId, video != null ? video.getCity() : null);
            if (chatList == null)
                chatList = new ArrayList<>();
            chatList.add(tipsBean);
            Database.saveChat(this, tipsBean);
        }
        // 有用户信息说明是从精选集进来的
        adapter = new AnonymousChatAdapter(this, chatList, mPictureCallback, isCustom);
        // 能不能发送语音和照片，如果是新版的应用设置为True
        adapter.setCanSendMedia(otherData.equals(Database.ChatCacheData.NEW_VERSION));
        adapter.setCity(getOriginalCity(chatList));
        // 对方是不是新版的APP
        adapter.setNewVersion(otherData.equals(Database.ChatCacheData.NEW_VERSION));
        // 是否有对方发来的信息。因为每次只取出20条信息，所以靠当前chatList判断是不准确的。所以要查询本会话的所有信息，如果是ChatCacheData.EMPTY_OTHER说明数据库里没有对方发来的信息。
        adapter.setWaitingOtherMsg(otherData.equals(Database.ChatCacheData.EMPTY_OTHER));
        Log.d(TAG, "on Resume , set adapter ");
        listView.setAdapter(adapter);
        if (!chatList.isEmpty()) {
            DbHelper.updateUserVideoChatToRead(this, toChatUsername, mChatId);
            listView.setSelection(chatList.size());
            Log.d(TAG, "on Resume , setSection to Listview  ");
        }
        return adapter;
    }

    private String getOriginalCity(List<ChatBean> chatBeans){
        if (!Utils.isCollectionEmpty(chatBeans)){
            return chatBeans.get(0).getCity();
        } else {
            return null;
        }
    }

    @Override
    protected void onPicture() {
        if (adapter == null) return;
        // 是否能发送照片
        if (!isCustom && !adapter.isCanSendMedia()) {
            if (mStatusToast != null)
                mStatusToast.cancel();
            mStatusToast = SuperToast.show(AnonymousChatActivity.this, getString(R.string.activity_chat_wait_other_picture));
            return;
        }
        // 是否版本过低
        if (!isCustom && !adapter.isNewVersion() && !adapter.isWaitingOtherMsg()) {
            if (mStatusToast != null)
                mStatusToast.cancel();
            mStatusToast = SuperToast.show(AnonymousChatActivity.this, getString(R.string.activity_chat_app_version_under_picture));
            return;
        }
        super.onPicture();
    }

    @Override
    protected void onVoice() {
        if (adapter == null) return;
        // 是否能发送语音
        if (!isCustom && !adapter.isCanSendMedia()) {
            SuperToast.show(AnonymousChatActivity.this, getString(R.string.activity_chat_wait_other_audio));
            mControllerView.setEnabled(false);
            mControllerView.reset();
            return;
        }
        // 是否版本过低
        if (!isCustom && !adapter.isNewVersion() && !adapter.isWaitingOtherMsg()){
            SuperToast.show(AnonymousChatActivity.this, getString(R.string.activity_chat_app_version_under_audio));
            mControllerView.setEnabled(false);
            mControllerView.reset();
            return;
        }
        super.onVoice();
    }

    @Override
    protected ChatBean saveChatAndUpdateListView(Context context, EMMessage message, boolean send, Integer sendstatus) {
        // 如果消息列表只有一条记录，说明是拍照提示语。要把这条提示语保存进数据库
        if (!isCustom && adapter.getDataList().size() == 1) {
            Database.saveChat(getApplicationContext(), adapter.getDataList().get(0));
        }
        mChatBean = super.saveChatAndUpdateListView(context, message, send, sendstatus);
        // 对方发来的信息
        if (mChatBean.getDirect() == EMMessage.Direct.RECEIVE.ordinal()){
            // 刷新性别和城市信息
            adapter.setSex(mChatBean.getSex());
            adapter.setCity(mChatBean.getCity());
            // 判断是否要加入提示和开启拍照。
            if (adapter.isWaitingOtherMsg() && !isCustom) {
                adapter.setCanSendMedia(true);
                adapter.setWaitingOtherMsg(false);
                addTipsBean(mChatBean.getFromUser());
            }
        }
        return mChatBean;
    }

    @Override
    protected ConversationMessage updateConversationMsg(ChatBean chatBean, String videoid) {
        if (isCustom)
            return null;
        else
            return super.updateConversationMsg(chatBean, videoid);
    }

    @Override
    protected void receiveChat(EMMessage message, String username) {
        if (isCustom){
            message.setAttribute(ATTRIBUTE_VIDEO_ID, getString(R.string.custom_video_id));
        }
        super.receiveChat(message, username);
    }

    protected void setAttributes(EMMessage message){
        if (video != null) {
            message.setAttribute(ATTRIBUTE_VIDEO_URI, video.getVideouri());
            message.setAttribute(ATTRIBUTE_VIDEO_ID, video.getVideoid());
            String previewUri = video.getPreviewurl();
            if (StringUtils.isNotEmpty(previewUri)) {
                message.setAttribute(ATTRIBUTE_PRVIEWURI, previewUri);
            }
        } else {
            ChatBean chatBean = adapter.getFirstChat();
            if (chatBean != null){
                message.setAttribute(ATTRIBUTE_VIDEO_URI, chatBean.getVideoUri());
                message.setAttribute(ATTRIBUTE_VIDEO_ID, chatBean.getVideoId());
                message.setAttribute(ATTRIBUTE_PRVIEWURI, chatBean.getPreviewUri());
            }

        }
        message.setAttribute(ATTRIBUTE_VERSION, SystemUtils.getVersionCode(this));
        message.setAttribute(ATTRIBUTE_SEX, AppContext.getSysCode().getSex());
        message.setAttribute(ATTRIBUTE_CITY, CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_CITY));
    }

    protected void resetAttributes(EMMessage message, ChatBean chatBean){
        message.setAttribute(ATTRIBUTE_VIDEO_URI, chatBean.getVideoUri());
        message.setAttribute(ATTRIBUTE_VIDEO_ID, chatBean.getVideoId());
        message.setAttribute(ATTRIBUTE_PRVIEWURI, chatBean.getPreviewUri());
        message.setAttribute(ATTRIBUTE_VERSION, SystemUtils.getVersionCode(this));
        message.setAttribute(ATTRIBUTE_SEX, AppContext.getSysCode().getSex());
        message.setAttribute(ATTRIBUTE_CITY, CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_CITY));
    }

    private void addTipsBean(String from) {
        if (Database.findTipType(this.getApplicationContext(), from, mChatId, ChatBean.WAIT_OK).isEmpty()){
            ChatBean tipsBean = ChatBean.makeTipsChatBean(ChatBean.WAIT_OK, from, mChatId, adapter.getCity());
            adapter.getDataList().add(adapter.getDataList().size(), tipsBean);
            Database.saveChat(getApplicationContext(), tipsBean);
        }
    }

    @Override
    protected void sendEchatMessage(EMMessage message, ChatBean chatBean) {
        if (adapter.isWaitingOtherMsg()){
            chatBean.setWaitStatus(ChatBean.WAIT_OTHER_MSG);
        }
        super.sendEchatMessage(message, chatBean);
    }

    @Override
    protected void onSendChatSuccess(ChatBean chatBean) {
        super.onSendChatSuccess(chatBean);
        setTips(chatBean);
    }

    private void setTips(ChatBean chatBean){
        if (adapter.isWaitingMyMsg()) {
            adapter.setWaitingMyMsg(false);
            addTipsBean(chatBean.getFromUser());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
