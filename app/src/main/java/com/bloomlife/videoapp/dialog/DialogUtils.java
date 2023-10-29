package com.bloomlife.videoapp.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.NotificationMessage;
import com.bloomlife.videoapp.model.Recommend;
import com.bloomlife.videoapp.model.Story;
import com.bloomlife.videoapp.model.User;
import com.bloomlife.videoapp.model.UserInfo;
import com.bloomlife.videoapp.model.Video;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class DialogUtils {

    public static void showUsarInfo(FragmentActivity activity, Account account){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(account.getUserId());
        userInfo.setIconUrl(TextUtils.isEmpty(account.getSdcardUserIcon()) ? account.getUserIcon() : account.getSdcardUserIcon());
        userInfo.setGender(account.getGender());
        userInfo.setDescription(account.getDescription());
        userInfo.setName(account.getUserName());
        showUserInfo(activity, userInfo);
    }

    public static void showUserInfo(FragmentActivity activity, CommentText commentText){
        UserInfo userInfo = new UserInfo();
        userInfo.setName(commentText.getUsername());
        userInfo.setIconUrl(commentText.getUsericon());
        userInfo.setGender(commentText.getGender());
        userInfo.setId(commentText.getUserid());
        showUserInfo(activity, userInfo);
    }

    public static void showUserInfo(FragmentActivity activity, NotificationMessage msg){
        UserInfo userInfo = new UserInfo();
        userInfo.setName(msg.getExtra().getUsername());
        userInfo.setId(msg.getExtra().getUserid());
        userInfo.setIconUrl(msg.getExtra().getUsericon());
        showUserInfo(activity, userInfo);
    }

    public static void showUserInfo(FragmentActivity activity){
        UserInfo userInfo = new UserInfo();
        userInfo.setName("");
        userInfo.setIconUrl("");
        userInfo.setGender(Constants.FEMALE);
        userInfo.setDescription("");
        userInfo.setId("55bb596ee4b094e6925fdc13");
        showUserInfo(activity, userInfo);
    }

    public static void showUserInfo(FragmentActivity activity, ChatBean chatBean){
        UserInfo userInfo = new UserInfo();
        userInfo.setName(chatBean.getUserName());
        userInfo.setIconUrl(chatBean.getUserIcon());
        userInfo.setCity(chatBean.getCity());
        userInfo.setId(chatBean.getFromUser());
        userInfo.setGender(chatBean.getSex() == Video.MALE ? Constants.MALE : Constants.FEMALE);
        showUserInfo(activity, userInfo);
    }

    public static void showAccountDialog(FragmentActivity activity){
        AccountDialog dialog = new AccountDialog();
        dialog.show(activity);
    }

    public static void showEditDialog(FragmentActivity activity, Account account){
        Bundle bundle = new Bundle();
        bundle.putParcelable(EditUserInfoDialog.INTENT_ACCOUNT, account);
        EditUserInfoDialog userInfoDialog = new EditUserInfoDialog();
        userInfoDialog.setArguments(bundle);
        userInfoDialog.show(activity);
    }


    public static void showUserInfo(FragmentActivity activity, Recommend rd){
        if (activity == null || rd == null) return;
        UserInfo userInfo = new UserInfo();
        userInfo.setId(rd.getId());
        userInfo.setName(rd.getUsername());
        userInfo.setIconUrl(rd.getUsericon());
        userInfo.setGender(rd.getGender());
        userInfo.setDescription(rd.getUsersign());
        userInfo.setFansNum(rd.getFansnum());
        userInfo.setFollowerNum(rd.getFollownum());
        userInfo.setStoriesNum(rd.getVideonum());
        showUserInfo(activity, userInfo);
    }

    public static void showUserInfo(FragmentActivity activity, Story story){
        if (activity == null || story == null) return;
        UserInfo userInfo = new UserInfo();
        userInfo.setId(story.getUid());
        userInfo.setName(story.getUsername());
        userInfo.setIconUrl(story.getUsericon());
        userInfo.setGender(story.getGender());
        userInfo.setCity(story.getCity());
        userInfo.setStoryId(story.getId());
        userInfo.setFansNum(story.getFansnum());
        userInfo.setFollowerNum(story.getFollownum());
        userInfo.setStoriesNum(story.getVideonum());
        userInfo.setDescription(story.getUsersign());
        showUserInfo(activity, userInfo);
    }

    public static void showUserInfo(FragmentActivity activity, User user){
        showUserInfo(activity, user, null);
    }

    public static void showUserInfo(FragmentActivity activity, User user, UserInfoDialog.OnUserStatusChangeListener listener){
        if (activity == null || user == null) return;
        UserInfo userInfo = new UserInfo();
        userInfo.setName(user.getUsername());
        userInfo.setIconUrl(user.getUsericon());
        userInfo.setGender(user.getGender());
        userInfo.setId(user.getId());
        userInfo.setDescription(user.getUsersign());
        userInfo.setFansNum(user.getFansnum());
        userInfo.setFollowerNum(user.getFollownum());
        userInfo.setStatus(user.getStatus());
        showUserInfo(activity, userInfo, listener);
    }

    public static void showUserInfo(FragmentActivity activity, UserInfo userInfo){
        showUserInfo(activity, userInfo, null);
    }

    public static void showUserInfo(FragmentActivity activity, UserInfo userInfo, UserInfoDialog.OnUserStatusChangeListener listener){
        Bundle bundle = new Bundle();
        bundle.putParcelable(UserInfoDialog.INTENT_USER_INFO, userInfo);
        UserInfoDialog userInfoDialog = new UserInfoDialog();
        userInfoDialog.setArguments(bundle);
        userInfoDialog.setOnUserStatusChangeListener(listener);
        userInfoDialog.show(activity);
    }

    public static void showUserInfo(FragmentActivity activity, String userId){
        UserInfo info = new UserInfo();
        info.setId(userId);
        showUserInfo(activity, info);
    }

    public static void showLikeList(FragmentActivity activity, String storyId, BaseDialog.OnDismissListener l){
        LikeListDialog dialog = new LikeListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(LikeListDialog.INTENT_STORY_ID, storyId);
        dialog.setArguments(bundle);
        dialog.setOnDismissListener(l);
        dialog.show(activity);
    }

    public static void showRecommendList(FragmentActivity activity){
        RecommendListDialog dialog = new RecommendListDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        dialog.show(activity);
    }

}
