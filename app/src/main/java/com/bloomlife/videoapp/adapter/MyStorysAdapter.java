package com.bloomlife.videoapp.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.activity.UserStoryPlayActivity;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.UploadBackgroundService;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.UserStory;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.DeleteStoryVideosMessage;
import com.bloomlife.videoapp.view.UploadVideoProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.bloomlife.videoapp.app.UploadBackgroundService.isUploadStoryVideo;
import static com.bloomlife.videoapp.app.UploadBackgroundService.isUploadVideo;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/31.
 */
public class MyStorysAdapter extends BaseAdapter<DbStoryVideo>  {

    public static final int NOT_VIDEO = -1;

    private static final String TAG = MyStorysAdapter.class.getSimpleName();

    private ListView mListView;


    private SparseArray<Integer> mDeleteArray;
    private ArrayList<Comment> mStoryCommentList;
    private ArrayList<CommentText> mStoryCommentTextList;
    private String mSelectComment;
    private boolean mLike;
    private boolean mEdit;
    private boolean mSelectVideoAnimator;

    private int mHeat;
    private int mLook;
    private int mComment;
    private Random mRandom;

    public MyStorysAdapter(Activity activity, List<DbStoryVideo> dataList) {
        super(activity, dataList);
        mDeleteArray = new SparseArray<>();
        mRandom = new Random();
    }

    public void setListView(ListView listView){
        mListView = listView;
        mListView.setOnItemClickListener(mStoryItemClickListener);
    }

    public void setStoryComment(ArrayList<Comment> tagsComments, ArrayList<CommentText> textComents, String selectComment){
        mStoryCommentList = tagsComments;
        mStoryCommentTextList = textComents;
        mSelectComment = selectComment;
    }

    public void setStoryInfo(int heat, int look, int comment, boolean isLike){
        mHeat = heat;
        mLook = look;
        mComment = comment;
        mLike = isLike;
    }

    @Override
    protected void setViewContent(int position, View convertView, DbStoryVideo item) {
        Holder h = (Holder) convertView.getTag();
        convertView.setTag(R.id.item, item);
        convertView.setTag(R.id.position, position);
        h.uploadProgress.setVisibility(View.INVISIBLE);
        h.progressText.setVisibility(View.INVISIBLE);
        h.mUploadComplete.setVisibility(View.INVISIBLE);
        h.mUpload.setVisibility(View.INVISIBLE);
        h.mClick.setVisibility(View.INVISIBLE);
        h.mCover.setVisibility(View.INVISIBLE);
        Animation animation = h.mUploadComplete.getAnimation();
        if (animation != null){
            animation.cancel();
            h.mUploadComplete.clearAnimation();
        }

        // ???????????????
        String preViewUrl = null;
        if (!TextUtils.isEmpty(item.getFilePath())){
            preViewUrl = "file://"+item.getFilePath().replace(".mp4", ".jpg");
        } else {
            preViewUrl = item.getBigpreviewurl() == null ? "" : item.getBigpreviewurl();
        }
        // ?????????????????????????????????????????????????????????????????????
        if (!preViewUrl.equals(h.preView.getTag())){
            h.preView.setTag(preViewUrl);
            h.preView.setImageBitmap(null);
            mImageLoader.displayImage(preViewUrl, h.preView, mOption);
        }
        if(isUploadStoryVideo(item.getId())){
            //?????????????????????????????????
            setUploadingProgress(item.getUploadProgress(), h);
        } else if (mEdit){
            editMode(h, item, position);
        } else {
            /*  ???????????????????????????   */
            normalMode(h, item, position);
        }

    }

    private void editMode(Holder h, DbStoryVideo item, int position){
        h.mClick.setVisibility(View.VISIBLE);
        h.mCover.setVisibility(View.VISIBLE);
        if (mDeleteArray.get(position) != null) {
            h.mClick.setChecked(true);
            h.mCover.setBackgroundResource(R.drawable.item_myvideo_select_mock);
        } else {
            h.mClick.setChecked(false);
            h.mCover.setBackgroundResource(R.color.item_myvideo_cover);
        }
        if (mSelectVideoAnimator)
            ObjectAnimator.ofFloat(h.mClick, "Alpha", 0, 1).setDuration(300).start();
//      h.setInfo(item);
    }

    private void normalMode(Holder h, DbStoryVideo item, int position){
        if (item.getStatus() == DbStoryVideo.STATUS_UPLOAD_SUCCESS){
            //?????????????????????,????????????????????????
            h.setInfo(item);
        } else {
            h.infoLayout.setVisibility(View.INVISIBLE);
            //?????????????????????
            if (isUploadStoryVideo(item.getId())){
                // ?????????????????????????????????????????????????????????????????????????????????????????????
                setUploadingProgress(item.getUploadProgress(), h);
            } else {
                h.mUpload.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        Holder h = new Holder();
        View layout = inflater.inflate(R.layout.item_my_story, parent, false);
        h.ignites           = (TextView) layout.findViewById(R.id.item_story_likes);
        h.watch             = (TextView) layout.findViewById(R.id.item_story_watch);
        h.comment           = (TextView) layout.findViewById(R.id.item_story_comment);
        h.location          = (TextView) layout.findViewById(R.id.item_story_location);
        h.createTime        = (TextView) layout.findViewById(R.id.item_story_createtime);
        h.preView           = (ImageView) layout.findViewById(R.id.item_story_preview);
        h.infoLayout        = (ViewGroup) layout.findViewById(R.id.item_my_story_info_layout);
        h.preViewLayout     = (ViewGroup) layout.findViewById(R.id.item_my_story_preview_layout);

        h.uploadLayout      = (ViewGroup) layout.findViewById(R.id.item_my_story_info_progressLayout);
        h.uploadProgress    = (UploadVideoProgressBar) layout.findViewById(R.id.item_myself_video_upload_progressbar);
        h.progressText      = (TextView) layout.findViewById(R.id.item_myself_video_upload_progressbar_text);
        h.mUpload           = (TextView) layout.findViewById(R.id.item_myself_video_upload);
        h.mUploadComplete	= (TextView) layout.findViewById(R.id.item_myself_video_upload_complete);
        h.mClick            = (CheckBox) layout.findViewById(R.id.item_my_story_click);
        h.mCover            = layout.findViewById(R.id.item_my_story_cover);

        String color = "#"+ UIHelper.ColorList.get(mRandom.nextInt(UIHelper.ColorList.size()-1));
        h.preView.setBackgroundColor(Color.parseColor(color));
        layout.setTag(h);
        return layout;
    }

    public void refreshProgress(VideoProgress videoProgress){
        int videoid = videoProgress.getId();
        final double progress = videoProgress.getProgress();
        final int position = getVideoPosition(videoid);
        if (position == NOT_VIDEO)
            return;
        DbStoryVideo video = getDataList().get(position);
        View view = null;
        //???????????????????????????????????????????????????????????????????????????????????????
        //???????????????????????????notifyDataSetChanged()????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        for (int i = 0 , n= mListView.getChildCount(); i < n; i++) {
            View childView = mListView.getChildAt(i);
            DbStoryVideo tagVideo = (DbStoryVideo) childView.getTag(R.id.item);
            if(tagVideo != null && tagVideo.getId() != null && tagVideo.getId()==videoid){
                view = childView;
                break;
            }
        }
        if (view == null) return;
        final Holder holder = (Holder) view.getTag();
        //??????????????????
        if (progress == Constants.PROGRESS_FAILURE){
            Log.d(TAG, "  ???????????????????????????????????????????????????  ");
            video.setStatus(DbStoryVideo.STATUS_UPLOAD_FAIL);
            video.setUploadProgress(0);
            holder.infoLayout.setVisibility(View.GONE);
            if (view != null) notifyDataSetChanged();  //???????????????????????????????????????????????????
        } else if (progress != Constants.PROGRESS_SUCCESSS){
            Log.d(TAG, "  ???????????????????????? ???  " + progress);
            video.setUploadProgress(progress);
            if (view == null) return;
            setUploadingProgress(progress, holder);
        } else {
            Log.d(TAG, "  ????????????????????????????????????");
            video.setStatus(DbStoryVideo.STATUS_UPLOAD_SUCCESS);
            video.setSectime(System.currentTimeMillis() / 1000);
            video.setVideoid(videoProgress.getServerVideoId());
            //????????????
            if (view == null) return;
            //??????????????????????????????????????????????????????
            holder.mUpload.setVisibility(View.GONE);
            holder.uploadProgress.setVisibility(View.GONE);
            holder.progressText.setVisibility(View.INVISIBLE);

            Animation animation = makeUploadSucAnimation(holder, video);
            holder.mUploadComplete.setAnimation(animation);
            holder.mUploadComplete.setVisibility(View.INVISIBLE);
        }
    }

    private void clickedEditItem(DbStoryVideo video, int position, View v){
        if (mDeleteArray.get(position) != null){
            mDeleteArray.remove(position);
            ((CheckBox)v.findViewById(R.id.item_my_story_click)).setChecked(false);
            v.findViewById(R.id.item_my_story_cover).setBackgroundResource(R.color.item_myvideo_cover);
        } else {
            mDeleteArray.put(position, video.getStatus());
            Log.d(TAG, " delete vidoe : " + video);
            ((CheckBox)v.findViewById(R.id.item_my_story_click)).setChecked(true);
            v.findViewById(R.id.item_my_story_cover).setBackgroundResource(R.drawable.item_myvideo_select_mock);
        }
    }

    private void clickedItem(DbStoryVideo video, int position, View v){
        if(video.getStatus() != DbStoryVideo.STATUS_UPLOAD_SUCCESS){
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if(StringUtils.isEmpty(video.getLat())||StringUtils.isEmpty(video.getLon())){
                video.setLat(CacheBean.getInstance().getString(activity, CacheKeyConstants.LOCATION_LAT));
                video.setLon(CacheBean.getInstance().getString(activity, CacheKeyConstants.LOCATION_LON));
            }

            if(StringUtils.isEmpty(video.getLat())||StringUtils.isEmpty(video.getLon())){
                UiHelper.shortToast(activity, activity.getResources().getString(R.string.tips_no_location));
                return;
            }
            // ????????????????????????????????????????????????????????????
            uploadVideo(video, v);
            video.setStatus(DbStoryVideo.STATUS_NOT_COMPLETE);
            DbHelper.updateStoryVideo(activity.getApplicationContext(), video);
        } else {
            // ????????????????????????????????????????????????????????????
            UserStory story = new UserStory();
            story.setVideos(getStoryVideo());
            story.setCommentnum(mComment);
            story.setLikeNum(mHeat);
            story.setLooknum(mLook);
            story.setUserId(CacheBean.getInstance().getLoginUserId());
            story.setStoryid(video.getStoryid());

            Intent intent = new Intent(activity, UserStoryPlayActivity.class);
            intent.putExtra(UserStoryPlayActivity.INTENT_USER_STORY, story);
            intent.putExtra(UserStoryPlayActivity.INTENT_INDEX, position);
            intent.putExtra(UserStoryPlayActivity.INTENT_LIKE, mLike);
            intent.putExtra(UserStoryPlayActivity.INTENT_COMMENT_SELECT, mSelectComment);
            intent.putParcelableArrayListExtra(UserStoryPlayActivity.INTENT_COMMENT_LIST, mStoryCommentList);
            intent.putParcelableArrayListExtra(UserStoryPlayActivity.INTENT_COMMENT_TEXT_LIST, mStoryCommentTextList);
            activity.startActivityForResult(intent, UserStoryPlayActivity.REQUEST_STORY);
        }
    }

    private void uploadVideo(DbStoryVideo video, View v){
        // ???????????????????????????????????????????????????????????????????????????????????????????????????
        int position = (Integer) v.getTag(R.id.position);
        if (!isUploadStoryVideo(video.getId())){
            Log.i(TAG, " put position : " + position + " into map ");
            Intent intent = new Intent(activity.getApplicationContext(), UploadBackgroundService.class);
            intent.putExtra(UploadBackgroundService.INTENT_UPLOAD_STORY_VIDEO, video);
            activity.startService(intent);
            Holder holder = (Holder)v.getTag();
            setUploadingProgress(0, holder);
        }
    }


    /**
     * ??????????????????????????????????????????
     * @param videoId
     * @return
     */
    public int getVideoPosition(int videoId){
        List<DbStoryVideo> list = getDataList();
        if (Utils.isEmpty(list))
            return NOT_VIDEO;
        for (int i=0; i<list.size(); i++){
            if (list.get(i).getId() != null && list.get(i).getId()==videoId){
                return i;
            }
        }
        return NOT_VIDEO;
    }

    /**
     * ????????????????????????????????????
     * @param holder
     * @return
     */
    private Animation makeUploadSucAnimation(final Holder holder , final DbStoryVideo video) {
        Animation animation = new AlphaAnimation(90, 0);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holder.setInfo(video);
            }
        });
        return animation;
    }

    private void setUploadingProgress(double progress, Holder holder) {
        holder.uploadProgress.setVisibility(View.VISIBLE);
        holder.progressText.setVisibility(View.VISIBLE);
        holder.uploadProgress.setProgress((float) progress);

        holder.mUpload.setVisibility(View.GONE);
        holder.mUploadComplete.setVisibility(View.GONE);
        holder.infoLayout.setVisibility(View.GONE);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param resultVideos
     */
    public List<DbStoryVideo> syncStoryVideoList(List<StoryVideo> resultVideos, String storyId){
        List<DbStoryVideo> originalVideos = getDataList();
        List<DbStoryVideo> commonVideos = commonFilter(resultVideos, originalVideos);
        List<DbStoryVideo> newStoryVideos = null;
        if (!resultVideos.isEmpty()){
            // ????????????????????????????????????????????????????????????
            newStoryVideos = new ArrayList<>(getDbStoryVideo(resultVideos));
            // ???????????????????????????????????????????????????
            commonVideos.addAll(newStoryVideos);
        }
        for (DbStoryVideo v:commonVideos){
            v.setStoryid(storyId);
        }
        // ??????????????????????????????????????????????????????????????????????????????
        DbHelper.syncStoryVideoList(activity.getApplicationContext(), newStoryVideos, originalVideos);
        return commonVideos;
    }

    private List<DbStoryVideo> commonFilter(List<StoryVideo> resultVideos, List<DbStoryVideo> originalVideos){
        List<DbStoryVideo> commonVideos = new ArrayList<>();
        if (Utils.isEmpty(resultVideos) || Utils.isEmpty(originalVideos)){
            return commonVideos;
        }
        Iterator<StoryVideo> newIterator = resultVideos.iterator();
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        while (newIterator.hasNext()){
            StoryVideo newVideo = newIterator.next();
            Iterator<DbStoryVideo> oldIterator = originalVideos.iterator();
            while (oldIterator.hasNext()){
                com.bloomlife.videoapp.model.DbStoryVideo dbVideo = oldIterator.next();
                // ???????????????????????????
                if (dbVideo.getStatus() != DbStoryVideo.STATUS_UPLOAD_SUCCESS){
                    oldIterator.remove();
                    commonVideos.add(dbVideo);
                    break;
                }
                // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (dbVideo.getVideoid() != null && newVideo.getId().contains(dbVideo.getVideoid())){
                    newIterator.remove();
                    oldIterator.remove();
                    commonVideos.add(dbVideo);
                    break;
                }
            }
        }
        return commonVideos;
    }

    public List<DbStoryVideo> getDbStoryVideo(List<StoryVideo> newVideoList){
        List<DbStoryVideo> storyVideos = new ArrayList<>();
        for (StoryVideo newVideo : newVideoList){
            DbStoryVideo storyVideo = DbStoryVideo.make(newVideo);
            storyVideo.setStatus(DbStoryVideo.STATUS_UPLOAD_SUCCESS);
            storyVideos.add(storyVideo);
        }
        return storyVideos;
    }

    public List<StoryVideo> getStoryVideo(){
        List<StoryVideo> videos = new ArrayList<>(getDataList().size());
        for (DbStoryVideo storyVideo:getDataList()){
            videos.add(StoryVideo.make(storyVideo));
        }
        return videos;
    }

    public int getComment() {
        return mComment;
    }

    public int getHeat() {
        return mHeat;
    }

    public int getLook() {
        return mLook;
    }

    public void setEditable(boolean isEdit){
        mEdit = isEdit;
        mDeleteArray.clear();
        notifyDataSetChanged();
    }

    public void setSelectVideoAnimator(boolean isAnim){
        mSelectVideoAnimator = isAnim;
    }

    public int getSelectVideoCount(){
        return mDeleteArray.size();
    }

    public void deleteStoryVideo(){
        String deleteIds = "";
        List<String> deletePathList= new ArrayList<>();
        List<DbStoryVideo> removeList = new ArrayList<>();
        //???????????????????????????????????????????????????
        for (int i=0; i<mDeleteArray.size(); i++){
            int position = mDeleteArray.keyAt(i);
            int status = mDeleteArray.valueAt(i);
            DbStoryVideo video = getDataList().get(position);
            removeList.add(video);

            if(Video.STATUS_UPLOAD_SUCCESS != status){
                Log.d(TAG , " delete local story video = " + video);
                DbHelper.deleteStoryVideo(activity, video);
                deleteVideoFile(video);
            } else {
                Log.d(TAG, " delete server story video = " + video);
                deleteIds = deleteIds+video.getVideoid()+",";
                // ???????????????APP?????????????????????????????????????????????????????????????????????
                if (TextUtils.isEmpty(video.getFilePath())){
                    deletePathList.add(VideoFileManager.getInstance().getLocalCache(activity, video.getVideouri()).getAbsolutePath());
                } else {
                    deletePathList.add(video.getFilePath());
                }
            }
        }
        // ???????????????????????????
        for (DbStoryVideo video : removeList) {
            getDataList().remove(video);
        }
        if(!StringUtils.isEmpty(deleteIds)){
            deleteIds = deleteIds.substring(0, deleteIds.length()-1);
            sendDeleteStoryVideoMessage(deleteIds, deletePathList);
        }
        mDeleteArray.clear();
        notifyDataSetChanged();
        if(getDataList().isEmpty()){
            ((MyVideoActivity)activity).showEmptyAnimation(true);
        }
    }

    private void sendDeleteStoryVideoMessage(final String videoIds, final List<String> deletePathList){
        Volley.addToTagQueue(new MessageRequest(new DeleteStoryVideosMessage(videoIds), new MessageRequest.Listener<ProcessResult>(){
            @Override
            public void success(ProcessResult result) {
                DbHelper.deleteStoryVideos(activity, videoIds.split(","));
                for (String path:deletePathList) {
                    deleteFile(path);
                }
                VideoFileManager.getInstance().clearUrlCache();
                CacheBean.getInstance().putString(activity, CacheBean.getInstance().getLoginUserId(), "");
            }
        }));

    }

    private void deleteVideoFile(DbStoryVideo video){
        if (StringUtils.isEmpty(video.getFilePath()))
            return;
        deleteFile(video.getFilePath());
    }

    private void deleteFile(String path){
        new File(path).delete();
    }

    private AdapterView.OnItemClickListener mStoryItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DbStoryVideo video = (DbStoryVideo) getItem(position);
            if (isUploadVideo(video.getId())){ //??????????????????????????????????????????
                return;
            } else if (mEdit){
                clickedEditItem(video, position, view);
            } else {
                clickedItem(video, position, view);
            }
        }
    };

    class Holder {
        TextView ignites;
        TextView watch;
        TextView comment;
        TextView location;
        TextView createTime;
        ImageView preView;
        ViewGroup infoLayout;
        ViewGroup preViewLayout;

        /* * ?????????????????????***/

        /**??????????????????**/
        UploadVideoProgressBar uploadProgress;
        /**???????????????????????????**/
        TextView progressText;
        /** ??????????????????***/
        TextView mUpload;
        /** ????????????ui ***/
        TextView mUploadComplete;

        /**?????????????????????**/
        ViewGroup uploadLayout;

        /** ?????????????????????????????? **/
        CheckBox mClick;

        View mCover;

        public void setInfo(DbStoryVideo item){
            infoLayout.setVisibility(View.VISIBLE);
            ignites.setText(String.valueOf(mHeat));
            watch.setText(String.valueOf(mLook));
            comment.setText(String.valueOf(mComment));
            createTime.setText(DateUtils.getTimeString(activity, item.getSectime()));
            location.setText(item.getCity());
        }

    }

}
