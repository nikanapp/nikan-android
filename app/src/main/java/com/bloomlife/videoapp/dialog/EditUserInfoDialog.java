package com.bloomlife.videoapp.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.UploadFileRequest;
import com.android.volley.toolbox.UploadFileToQiNiuRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.EditUserInfoMessage;
import com.bloomlife.videoapp.model.message.UploadTokenMessage;
import com.bloomlife.videoapp.model.message.UserInfoMessage;
import com.bloomlife.videoapp.model.result.EditUserInfoResult;
import com.bloomlife.videoapp.model.result.UploadTokenResut;
import com.bloomlife.videoapp.model.result.UserInfoResult;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.bloomlife.videoapp.view.LinearPopWindow;
import com.bloomlife.videoapp.view.SuperToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/24.
 * @parameter INTENT_ACCOUNT Account 传用户的信息进来，使用Account类。
 * @parameter INTENT_FIRST_EDIT boolean 是否第一次登陆后的编辑
 * @parameter INTENT_TYPE int 编辑的类型
 */
public class EditUserInfoDialog extends BaseDialog {

    public static final String INTENT_ACCOUNT = "account";
    public static final String INTENT_FIRST_EDIT = "firstEdit";
    public static final String INTENT_TYPE = "type";
    public static final int EDIT = 1;
    public static final int LOGIN = 2;

    public static final String TEMP = "/imagetemp";

    private static final int CALL_CAMERA = 1001;
    private static final int CALL_ALBUM = 1002;
    private static final int CALL_EDIT_IMG = 1003;

    @ViewInject(id=R.id.edit_user_info_avatar, click=ViewInject.DEFAULT)
    private ImageView mUserAvatar;

    @ViewInject(id=R.id.edit_user_info_btn_male, click=ViewInject.DEFAULT)
    private ImageView mBtnMale;

    @ViewInject(id=R.id.edit_user_info_btn_female, click=ViewInject.DEFAULT)
    private ImageView mBtnFemale;

    @ViewInject(id=R.id.edit_user_info_accept, click=ViewInject.DEFAULT)
    private TextView mBtnAccept;

    @ViewInject(id=R.id.edit_user_info_name)
    private EditText mEditName;

    @ViewInject(id=R.id.edit_user_info_description)
    private EditText mEditDescription;

    @ViewInject(id=R.id.user_info_edit_name_bottom_line)
    private View mNameBottomLine;

    @ViewInject(id=R.id.user_info_edit_description_bottom_line)
    private View mDescBottomLine;

    @ViewInject(id=R.id.edit_user_info_progressbar)
    private GlobalProgressBar mProgressBar;

    @ViewInject(id=R.id.edit_user_info_main_layout, click=ViewInject.DEFAULT)
    private ViewGroup mMainLayout;

    private Account mAccount;
    private int mType;
    private boolean mFirstEdit;

    private File mSdcardTempFile;

    private boolean mIsFirstEditName = true;
    private boolean mIsFirstEditDesc = true;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_edit_user_info;
    }

    @Override
    protected void initLayout(View layout) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mType = getArguments().getInt(INTENT_TYPE, EDIT);
        mAccount = getArguments().getParcelable(INTENT_ACCOUNT);
        mFirstEdit = getArguments().getBoolean(INTENT_FIRST_EDIT);
        ImageLoader.getInstance().displayImage(
                TextUtils.isEmpty(mAccount.getSdcardUserIcon()) ? mAccount.getUserIcon() : mAccount.getSdcardUserIcon(),
                mUserAvatar,
                ImageLoaderUtils.getDecodingOptions(R.drawable.circle_avatar, R.drawable.circle_avatar));
        mEditName.setText(mAccount.getUserName());
        mEditName.setOnFocusChangeListener(mEditFocusListener);
        mEditName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return true;
            }
        });


        mEditDescription.setText(mAccount.getDescription());
        mEditDescription.setOnFocusChangeListener(mEditFocusListener);
        mEditDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return true;
            }
        });
        if (Constants.MALE.equals(mAccount.getGender())){
            pressedMale();
        } else {
            pressedFemale();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            mSdcardTempFile = new File(savedInstanceState.getString("SdcardTempFile"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSdcardTempFile.getPath()))
            outState.putString("SdcardTempFile", mSdcardTempFile.getPath());
    }

    private View.OnFocusChangeListener mEditFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.edit_user_info_name:
//                    mNameBottomLine.setBackgroundResource(hasFocus ? R.color.dialog_edit_line_yellow : R.color.white);
                    if (mIsFirstEditName){
                        mIsFirstEditName = false;
                        mEditName.setSelection(mEditName.getText().length());
                    }
                    break;

                case R.id.edit_user_info_description:
                    mDescBottomLine.setBackgroundResource(hasFocus ? R.color.dialog_edit_line_yellow : R.color.white);
                    if (mIsFirstEditDesc){
                        mIsFirstEditDesc = false;
                        mEditDescription.setSelection(mEditDescription.getText().length());
                    }
                    break;
            }

        }
    };

    private void pressedMale(){
        mBtnMale.setImageResource(R.drawable.btn_user_info_male_enable_selector);
        mBtnFemale.setImageResource(R.drawable.btn_user_info_female_disenable_selector);
    }

    private void pressedFemale(){
        mBtnMale.setImageResource(R.drawable.btn_user_info_male_disenable_selector);
        mBtnFemale.setImageResource(R.drawable.btn_user_info_female_enable_selector);
    }

    private String mName;
    private String mDescription;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_user_info_accept:
                mName = mEditName.getText().toString().trim();
                mDescription = mEditDescription.getText().toString().trim();
                if (TextUtils.isEmpty(mName)){
                    SuperToast.show(getActivity(), R.string.dialog_nickname_tip);
                    return;
                }
                setViewsEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                if (mSdcardTempFile != null){
                    Volley.add(new MessageRequest(new UploadTokenMessage(0, UploadTokenMessage.TYPE_UPLOAD_IMAGE), mGetUploadTokenListener));
                } else {
                    uploadUserInfo();
                }
                break;

            case R.id.edit_user_info_btn_male:
                pressedMale();
                mAccount.setGender(Constants.MALE);
                break;

            case R.id.edit_user_info_btn_female:
                pressedFemale();
                mAccount.setGender(Constants.FEMALE);
                break;

            case R.id.edit_user_info_avatar:
                List<String> names = new ArrayList();
                names.add(getString(R.string.dialog_call_camera));
                names.add(getString(R.string.dialog_call_album));
                LinearPopWindow window = new LinearPopWindow(getActivity(), names, getString(R.string.dialog_picture_choose_title));
                window.setPopListener(mPictureChooseListener);
                break;

            case R.id.edit_user_info_main_layout:
                UIHelper.hideSoftInput(getActivity(), mEditName);
                UIHelper.hideSoftInput(getActivity(), mEditDescription);
                break;
        }
    }

    private void setViewsEnabled(boolean enabled){
        mBtnAccept.setEnabled(enabled);
        mUserAvatar.setEnabled(enabled);
        mBtnFemale.setEnabled(enabled);
        mBtnMale.setEnabled(enabled);
        mEditName.setEnabled(enabled);
        mEditDescription.setEnabled(enabled);
    }

    private AccountDialog.Listener mListener;

    public void setListener(AccountDialog.Listener listener){
        mListener = listener;
    }

    private LinearPopWindow.LinearPopListener mPictureChooseListener = new LinearPopWindow.LinearPopListener() {

        @Override
        public void actionFirst() {
            callCamera();
        }

        @Override
        public void actionSecond() {
            callAlbum();
        }

        @Override
        public void actionThird() {

        }
    };

    private void callCamera() {
        mSdcardTempFile = getTempFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        // 存储卡可用 将照片存储在 sdcard
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSdcardTempFile));
        // Modified
        startActivityForResult(intent, CALL_CAMERA);
    }

    private void callAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(i, CALL_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 当用户执行完拍照返回，选取照片返回之后，若检测到其没有SDcard，则弹出PopupWindow提示用户
        if (requestCode == CALL_CAMERA && resultCode == Activity.RESULT_OK) {
            if (AppContext.deviceInfo.isSdcard()) {
                startPhotoZoom(Uri.fromFile(mSdcardTempFile));
            } else {
                SuperToast.show(getActivity(), R.string.intent_picture_fail);
            }
        } else if (requestCode == CALL_ALBUM && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            startPhotoZoom(uri);
        } else if (requestCode == CALL_EDIT_IMG && resultCode == Activity.RESULT_OK) {
            if (AppContext.deviceInfo.isSdcard()) {
                // 设置图片
                mUserAvatar.setImageBitmap(BitmapFactory.decodeFile(mSdcardTempFile.getAbsolutePath()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startPhotoZoom(Uri uri) {
        mSdcardTempFile = getTempFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSdcardTempFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 返回的图片像素达不到MyBackground的图片大小，系统自动填充黑色。因此调用下列方法将其放大。
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        // modified
        startActivityForResult(intent, CALL_EDIT_IMG);
    }

    private File getTempFile(){
        File temp = new File(getActivity().getExternalCacheDir().getPath() + TEMP);
        if (!temp.exists()){
            temp.mkdir();
        }
        return new File(temp, "tmp_pic_"+ SystemClock.currentThreadTimeMillis() + ".jpg");
    }

    private void uploadUserInfo(){
        if (mType == EDIT){
            uploadEditUserInfo(mName, mDescription);
        } else {
            uploadLoginUserInfo(mName, mDescription, mAccount);
        }
    }

    private void uploadEditUserInfo(String name, String description){
        EditUserInfoMessage msg = new EditUserInfoMessage();
        msg.setUsericon(mAccount.getUserIcon());
        msg.setUsername(name);
        msg.setUsersign(description);
        msg.setGender(mAccount.getGender());
        Volley.add(new MessageRequest(msg, mEditUserInfoReqListener));
    }

    private MessageRequest.Listener mGetUploadTokenListener = new MessageRequest.Listener<UploadTokenResut>(){

        @Override
        public void success(UploadTokenResut result) {
            Volley.uploadFileToQiNiuRequest(
                    new UploadFileToQiNiuRequest(
                            result.getUploadtoken(),
                            result.getFilekey(),
                            mSdcardTempFile.getAbsolutePath(),
                            mUploadImgReqListener));
        }

        @Override
        public void error(VolleyError error) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void failure(FailureResult result) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    };

    private UploadFileRequest.Listener mUploadImgReqListener = new UploadFileRequest.Listener() {

        @Override
        public void start() {

        }

        @Override
        public void progress(double pregress) {

        }

        @Override
        public void error(String msg) {
            setViewsEnabled(true);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void success(Map<String, Object> attr) {
            mAccount.setUserIcon("http://videoapp-video.qiniudn.com/" + attr.get("key").toString());
            uploadUserInfo();
        }
    };

    private void uploadLoginUserInfo(String name, String description,Account account){
        UserInfoMessage uim = new UserInfoMessage();
        uim.setAccesstoken(account.getTokenSecret());
        uim.setOpenid(account.getId());
        uim.setGender(account.getGender());
        uim.setUsername(name);
        uim.setUsericon(account.getUserIcon());
        uim.setCity(account.getLocation());
        uim.setUsersign(description);
        switch (account.getType()){
            case SINA_WEIBO:
                uim.setAuthplatform(UserInfoMessage.SINA_WEIBO);
                break;
            case WECHAT:
                uim.setAuthplatform(UserInfoMessage.WECHAT);
                break;
            case FACEBOOK:
                uim.setAuthplatform(UserInfoMessage.FACEBOOK);
                break;
            case TWITTER:
                uim.setAuthplatform(UserInfoMessage.TWITTER);
                break;
        }
        Volley.add(new MessageRequest(uim, mUploadUserInfoListener));
    }

    private MessageRequest.Listener mUploadUserInfoListener = new MessageRequest.Listener<UserInfoResult>() {

        @Override
        public void success(UserInfoResult result) {
            if (result.getStatecode() == UserInfoResult.SUCC){
                // 保存登陆状态
                mAccount.setUserId(result.getUserid());
                mAccount.setUserIcon(result.getUsericon());
                mAccount.setGender(result.getGender());
                mAccount.setUserName(result.getNickname());
                mAccount.setDescription(result.getUsersign());
                CacheBean.getInstance().setLoginUserId(getActivity(), result.getUserid());
                CacheBean.getInstance().putObject(getActivity(), CacheKeyConstants.KEY_MY_ACCOUNT, mAccount);
                if (mListener != null)
                    mListener.success(mAccount);
                sendLoginBroadcast(result.getFirstauth() == UserInfoResult.FIRSTAUTH);
                dismissAllowingStateLoss();
            } else {
                SuperToast.show(getActivity(), result.getErrmsg());
            }
        }

        @Override
        public void error(VolleyError error) {
            super.error(error);
            if (mListener != null)
                mListener.failure();
        }

        @Override
        public void finish() {
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private MessageRequest.Listener mEditUserInfoReqListener = new MessageRequest.Listener<EditUserInfoResult>(){

        @Override
        public void success(EditUserInfoResult result) {
            if (result.getStatecode() == EditUserInfoResult.SUCC){
                mAccount.setUserName(mName);
                mAccount.setDescription(mDescription);
                if (mSdcardTempFile != null)
                    mAccount.setSdcardUserIcon("file://" + mSdcardTempFile.getAbsolutePath());
                // 保存用户信息
                AppContext.getSysCode().setSex(Constants.FEMALE.equals(mAccount.getGender()) ? Video.FEMALE : Video.MALE);
                CacheBean.getInstance().putObject(getActivity(), CacheKeyConstants.KEY_MY_ACCOUNT, mAccount);
                // 是否第一次登陆后的编辑
                sendLoginBroadcast(mFirstEdit);
                dismissAllowingStateLoss();
            } else if(result.getStatecode() == EditUserInfoResult.NAME_ALREADY_USED){
                SuperToast.show(getActivity(), R.string.dialog_name_already_used);
            }
        }

        @Override
        public void error(VolleyError error) {
            super.error(error);
            SuperToast.show(getActivity(), R.string.no_network);
        }

        @Override
        public void failure(FailureResult result) {
            super.failure(result);
            SuperToast.show(getActivity(), R.string.dialog_edit_fail);
        }

        @Override
        public void finish() {
            setViewsEnabled(true);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    };

    private void sendLoginBroadcast(boolean isFirstLogin){
        Utils.setShowDialog(getActivity(), CacheKeyConstants.KEY_FIRST_LOGIN, isFirstLogin);
        getActivity().sendBroadcast(new Intent(Constants.ACTION_USER_LOGIN));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
