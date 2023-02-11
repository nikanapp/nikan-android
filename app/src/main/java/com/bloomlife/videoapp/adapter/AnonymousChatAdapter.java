package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.EasemobchatDateUtils;
import com.bloomlife.videoapp.model.ChatBean;
import com.easemob.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * User: ZhengXingtian(lan4627@Gmail.com)
 * Date: 2015-08-16
 * Time: 13:48
 * Version: 1.0
 */
public class AnonymousChatAdapter extends RealNameChatAdapter {

    private boolean mWaitOtherMsg;
    private boolean mWaitMyMsg;
    private boolean mCanSendMedia;
    private boolean mNewsVersion;

    private String mCity;

    public AnonymousChatAdapter(Activity activity, List<ChatBean> dataList, ChatAdapterCallback callback, boolean isCustom) {
        super(activity, dataList, callback);
        // 如果是客服对话，需要把提示都移除掉
        if (isCustom)
            removeTipsBean(dataList);
        else
            checkMessage(dataList);
    }

    @Override
    public void setDataList(List<ChatBean> dataList) {
        super.setDataList(dataList);
        refreshSexInfo();
        refreshOtherUserInfo();
        checkMessage(dataList);
    }

    @Override
    public void addDataList(int location, List<ChatBean> dataList) {
        super.addDataList(location, dataList);
        refreshSexInfo();
        refreshOtherUserInfo();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewgroup) {
        ChatBean chat = getItem(position);
        if (chat.getViewType() == ChatBean.VIEW_TYPE_CHAT){
            // 匿名世界对话的视图
            return setChatView(position, view, viewgroup, chat);
        } else if (chat.getViewType() == ChatBean.VIEW_TYPE_TIPS){
            // 提示语的视图
            return setTipsView(position, view, viewgroup, chat);
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return getDataList().get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private void removeTipsBean(List<ChatBean> dataList){
        for (int i=0; i<dataList.size(); i++){
            if (dataList.get(i).getViewType() == ChatBean.VIEW_TYPE_TIPS){
                dataList.remove(i--);
            }
        }
    }

    /**
     * 对方的软件版本是否跟我们一样或者更高。
     * @return
     */
    public boolean isNewVersion(){
        if (mNewsVersion) return true;
        if (getDataList() != null){
            ChatBean chatBean = getLastOtherChat();
            if (chatBean != null)
                return chatBean.getAppVersion() >= 140;
        }
        return false;
    }

    public void setNewVersion(boolean nv){
        mNewsVersion = nv;
    }

    public boolean isCanSendMedia(){
        return mCanSendMedia;
    }

    public void setCanSendMedia(boolean can){
        mCanSendMedia = can;
    }

    /**
     * 检查是否有提示语
     * @param dataList
     */
    private void checkMessage(List<ChatBean> dataList){
        if (dataList == null || dataList.isEmpty()) return;
        ChatBean lastChat = getLastOtherChat();
        // 当没有对话时，设置为等待对方回复
        if (lastChat == null || lastChat.getWaitStatus() == ChatBean.WAIT_OTHER_MSG){
            mCanSendMedia = false;
            mWaitMyMsg = false;
            mWaitOtherMsg = true;
            return;
        }

        if (lastChat.getWaitStatus() == ChatBean.WAIT_OK){
            mCanSendMedia = true;
            mWaitMyMsg = false;
            mWaitOtherMsg = false;
            return;
        }

        if (lastChat.getWaitStatus() == ChatBean.WAIT_MY_MSG){
            mCanSendMedia = true;
            mWaitMyMsg = true;
            mWaitOtherMsg = false;
            return;
        }

        if (lastChat.getWaitStatus() == ChatBean.WAIT_NULL){
            // 如果是旧版本的消息记录，统一提示版本过低无法发送图片和语音
            mCanSendMedia = false;
            mWaitMyMsg = false;
            mWaitOtherMsg = false;
        }

    }

    @Override
    protected String getTipsText(int status){
        switch (status) {
            case ChatBean.WAIT_OTHER_MSG:
                return mActivity.getString(R.string.wait_other_message_tips);

            case ChatBean.WAIT_OK:
                return mActivity.getString(R.string.send_picture_and_audio_activeted);

            case ChatBean.WAIT_MY_MSG:
                return mActivity.getString(R.string.wait_my_message_tips);

            default:
            case ChatBean.WAIT_NULL:
                return "";

            case ChatBean.TIPS_CITY:
                return mCity;
        }
    }

    public void setCity(String city){
        if (TextUtils.isEmpty(city)){
            return;
        }
        if (mCity == null){
            getDataList().add(0, getCityTipsChatBean());
        }
        this.mCity = city;
    }

    public String getCity(){
        return mCity;
    }

    @Override
    protected void setTime(int position, ChatView chatView, ChatBean chatBean) {
        // 前两个是地点和提示语
        if (position <= 1) return;
        if (position == 2) {
            chatView.time.setText(EasemobchatDateUtils.getTimestampString(chatBean.getCreatetime()));
            chatView.time.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(chatBean.getCreatetime().getTime(), getDataList().get(position - 1).getCreatetime().getTime())) {
                chatView.time.setVisibility(View.GONE);
            } else {
                chatView.time.setText(EasemobchatDateUtils.getTimestampString(new Date(chatBean.getCreatetime().getTime())));
                chatView.time.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean isWaitingOtherMsg(){
        return mWaitOtherMsg;
    }

    public void setWaitingOtherMsg(boolean w){
        mWaitOtherMsg = w;
    }

    public boolean isWaitingMyMsg(){
        return mWaitMyMsg;
    }

    public void setWaitingMyMsg(boolean w){
        mWaitMyMsg = w;
    }

}
