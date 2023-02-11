package com.bloomlife.videoapp.activity.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.adapter.MyVideoAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.FinishBroadcast;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.ModifyVideoMessage;
import com.bloomlife.videoapp.model.message.MyVideoInfoMessage;
import com.bloomlife.videoapp.model.result.UpdateInfoResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.bloomlife.videoapp.view.HeaderFooterGridView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/4.
 */
public class MyVideosFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = MyVideosFragment.class.getSimpleName();

    public static final int DB_CALLBACK = 1;

    public static final int PAGE_SIZE = 20;

    public static final int RESULT_MY = 1001;

    @ViewInject(id=R.id.fragment_myvideo_videolist)
    private HeaderFooterGridView mGridView;

    @ViewInject(id=R.id.fragment_myvideo_bottom)
    private ViewGroup mBottomLayout;

    @ViewInject(id=R.id.fragment_myvideo_delete, click=ViewInject.DEFAULT)
    private Button mDelete;

    @ViewInject(id=R.id.fragment_myvideo_lost, click=ViewInject.DEFAULT)
    private Button mLost;

    @ViewInject(id=R.id.fragment_myvideo_empty)
    private FooterTipsView mEmptyView;

    @ViewInject(id=R.id.mainLayout)
    private RelativeLayout mainLayout;

    private MyVideoAdapter mAdapter;

    private MyVideoHandler mHandler = new MyVideoHandler(this);

    private List<Video> mVideoList;

    private UploadProgressReceiver mReceiver;

    private FinishBroadcast finishBroadcast;

    private View mFooterView;

    private boolean mVideoEmpty;

    private int mPageNum = 1;

    private double mBackDistance;  //手势滑动的触发距离

    private double mFingerStart; //支持手势操作的x坐标起点

    private double mYDistance; //手势操作时y轴的最大移动距离

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_my_videos, container, false);
        FinalActivity.initInjectedView(this, layout);
        initVideoList();
        initUi();
        initRecevier();
        initFooterView();
        return layout;
    }

    /**
     *
     */
    private void initRecevier() {
        //注册刷新广播
        mReceiver = new UploadProgressReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_UPLOAD_PROGRESS);
        getActivity().registerReceiver(mReceiver, intentFilter);

        finishBroadcast = new FinishBroadcast(getActivity());
        intentFilter = new IntentFilter(Constants.ACTION_FINISH);
        getActivity().registerReceiver(finishBroadcast, intentFilter);
    }

    private void initUi(){
        mBackDistance = AppContext.deviceInfo.getScreenWidth()*0.4;
        mFingerStart = AppContext.deviceInfo.getScreenWidth()*0.12;
        mYDistance = AppContext.deviceInfo.getScreenHeight()*0.1;


        mEmptyView.setText(getString(R.string.list_empty));

        View footerView = new View(getActivity());
        mGridView.addFooterView(footerView, null, false);
        mAdapter = new MyVideoAdapter(getActivity(), null, mGridView);
        mGridView.setAdapter(mAdapter);
        mGridView.removeFooterView(footerView);
        mGridView.setOnTouchListener(onTouchListener);
        mGridView.setVerticalScrollBarEnabled(false);
        mGridView.setEmptyView(mEmptyView);
    }

    private void initVideoList(){
        mGridView.setOnScrollListener(scrollListener);
        DbHelper.readVideoList(getActivity(), CacheBean.getInstance().getLoginUserId(), mReadVideosCallback);
    }


    private void initFooterView(){
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(0, UiUtils.dip2px(getActivity(), 15), 0, UiUtils.dip2px(getActivity(), 15));
        layout.setGravity(Gravity.RIGHT);
        layout.addView(new FooterTipsView(getActivity(), getString(R.string.more_empty)));
        mFooterView = layout;
    }

    private void getMyVideoInfo(int number){
        List<Video> list = mAdapter.getDataList();
        MyVideoInfoMessage message = new MyVideoInfoMessage();
        message.setPageNum(number);
        message.setPageSize(PAGE_SIZE);
        if (list != null && list.size() >= PAGE_SIZE){
            long time = list.get(list.size()-1).getUploadTime();
            message.setCreatetime(time > 0 ? time : list.get(list.size()-1).getCreatetime());
        } else {
            message.setCreatetime(System.currentTimeMillis());
        }
        Volley.addToTagQueue(new MessageRequest(message, mUpdateInfoListener));
    }

    /**
     * 此接口是在异步线程里调用的，不能在里面执行UI变化的操作。
     */
    private DbHelper.DbHelperCallback mReadVideosCallback = new DbHelper.DbHelperCallback() {

        @Override
        public void onVideoList(List<Video> videoList) {
            sort(videoList);
            checkFailUpload(videoList);
            Message message = new Message();
            message.what = DB_CALLBACK;
            message.obj = videoList;
            mHandler.sendMessage(message);
        }

        private void checkFailUpload(List<Video> videoList){
            boolean isFail = false;
            for (Video v: videoList){
                if (v.getStatus() == Video.STATUS_UPLOAD_FAIL){
                    isFail = true;
                }
            }
            CacheBean.getInstance().putInt(getActivity(), CacheKeyConstants.KEY_VIDEO_UPLOAD_FAIL, isFail ? Video.STATUS_UPLOAD_FAIL : Video.STATUS_UPLOAD_SUCCESS);
        }

        private void sort(List<Video> result){
            Collections.sort(result, new Comparator<Video>() {
                @Override
                public int compare(Video lhs, Video rhs) {
                    if (lhs.getStatus() != DbStoryVideo.STATUS_UPLOAD_SUCCESS) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }

    };

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState!= AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                mAdapter.setDoAnimation(false);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem != 0 && firstVisibleItem + visibleItemCount >= (mPageNum * PAGE_SIZE) -1){
                getMyVideoInfo(++mPageNum);
            }
        }
    };

    public void setEditStatus(boolean isChecked) {
        if (mVideoEmpty) return;
        mAdapter.setEditable(isChecked);
        if (isChecked){
            mBottomLayout.setVisibility(View.VISIBLE);
            mAdapter.setDoAnimation(true);
            ObjectAnimator.ofFloat(mBottomLayout, "Alpha", 0, 1).setDuration(300).start();
        } else {
            mBottomLayout.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(mBottomLayout, "Alpha", 1, 0).setDuration(300).start();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_MY){
            int position = data.getIntExtra(AnonVideoPlayFragment.RESULT_INTENT_POSITION, 0);
            int likeNum = data.getIntExtra(AnonVideoPlayFragment.RESULT_INTENT_LIKENUM, 0);
            List<Video> dataList = mAdapter.getDataList();
            if (dataList != null){
                Video video = dataList.get(position);
                // 刷新列表中的点赞数和点赞状态
                if (likeNum > video.getLikenum()){
                    video.setLike(true);
                } else {
                    video.setLike(false);
                }
                video.setLikenum(likeNum);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_myvideo_delete:
                if (mAdapter.getSelectVideoCount() < 1) return;
                showDeleteDialog();
                isEmpty();
                break;

            case R.id.fragment_myvideo_lost:
                if (mAdapter.getSelectVideoCount() < 1) return;
                if (CacheBean.getInstance().getInt(getActivity(), CacheKeyConstants.KEY_FIRST_LOST, 0) != 1){
                    CacheBean.getInstance().putInt(getActivity(), CacheKeyConstants.KEY_FIRST_LOST, 1);
                    showLostDialog();
                } else {
                    lostVideos();
                }
                isEmpty();
                break;

            default:
                break;
        }
    }

    private void showDeleteDialog(){
        AlterDialog.showDialog(getActivity(), getString(R.string.activity_my_video_delete_tips), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteVideos();
            }
        });
    }

    private void closeActivityEditBtn(){
        if (getActivity() == null) return;
        ((MyVideoActivity)getActivity()).setEditBtnStatus(false);
    }

    private void deleteVideos(){
        mAdapter.deleteVideoWithType(ModifyVideoMessage.DELETE);
        closeActivityEditBtn();
    }

    private void showLostDialog(){
        AlterDialog.showDialog(getActivity(), getString(R.string.activity_my_video_lost_tips), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lostVideos();
            }
        });
    }

    private void lostVideos(){
        mAdapter.deleteVideoWithType(ModifyVideoMessage.LOST);
        closeActivityEditBtn();
    }

    /**
     * 删除视频或失联视频后检查是否视频为空，是的话显示提示语
     */
    private void isEmpty(){
        if (com.bloomlife.videoapp.common.util.Utils.isEmpty(mAdapter.getDataList())){
            mEmptyView.setVisibility(View.VISIBLE);
            mGridView.removeFooterView(mFooterView);
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().overridePendingTransition(0, R.anim.activity_bottom_out);
        getActivity().sendBroadcast(new Intent(BackgroundManager.ACTION_RECYCLE_BACKGROUND));

        if(mReceiver!=null)	getActivity().unregisterReceiver(mReceiver);
        if(finishBroadcast!=null) getActivity().unregisterReceiver(finishBroadcast);
        super.onDestroyView();
    }

    static class MyVideoHandler extends Handler {

        private WeakReference<MyVideosFragment> mReference;

        public MyVideoHandler(MyVideosFragment activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MyVideosFragment fragment = mReference.get();
            if (fragment == null) return;
            switch (msg.what) {
                case DB_CALLBACK:
                    fragment.mVideoList = (List<Video>) msg.obj;
                    fragment.mAdapter.setDataList(fragment.mVideoList);
                    fragment.getMyVideoInfo(fragment.mPageNum);
                    if(Utils.isEmptyCollection(fragment.mVideoList))
                        fragment.showEmptyView(true);
                    else
                        fragment.mGridView.addFooterView(fragment.mFooterView, null, false);
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public void showEmptyView(boolean empty){
        mVideoEmpty = empty;
    }


    private MessageRequest.Listener<UpdateInfoResult> mUpdateInfoListener = new MessageRequest.Listener<UpdateInfoResult>(){
        @Override
        public void success(UpdateInfoResult result) {
            if (mAdapter == null) return;
            if (!result.getVideos().isEmpty()){
                showEmptyView(false);
            }
            if (mAdapter.getDataList() != null){
                mAdapter.setDataList(mAdapter.syncVideoList(result.getVideos()));
            } else {
                mAdapter.setDataList(result.getVideos());
            }
            mAdapter.notifyDataSetChanged();
        }
    };


    class UploadProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            VideoProgress videoProgress = (VideoProgress) intent.getSerializableExtra(Constants.INTENT_UPLOAD_PROGRESS);

//			Log.e("UploadProgressReceiver", " videoid : "+videoid+" position: "+position+" progress: "+progress);

            if (mAdapter != null && videoProgress.getType() == VideoProgress.VIDEO)
                mAdapter.refreshProgress(videoProgress);
        }

    }


    public RelativeLayout getMainLayout() {
        return mainLayout;
    }

    public void setMainLayout(RelativeLayout mainLayout) {
        this.mainLayout = mainLayout;
    }

    private float lastX;
    private float lastY;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(MotionEvent.ACTION_DOWN==event.getAction()){
                lastX = event.getX();
                Log.d(TAG, " fisrt point x = " + lastX);
                if(lastX> mFingerStart) return false ;
                lastY = event.getY();
                return true;
            }

            if(MotionEvent.ACTION_UP==event.getAction()){
                if(Math.abs(event.getY()-lastY)< mYDistance){
                    if(event.getX()-lastX> mBackDistance){
                        getActivity().finish();
                        return true ;
                    }
                }
            }
            return false;
        }
    };
}