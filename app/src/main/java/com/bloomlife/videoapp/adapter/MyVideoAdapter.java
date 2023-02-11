package com.bloomlife.videoapp.adapter;

import static com.bloomlife.videoapp.app.UploadBackgroundService.isUploadVideo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiUtils;

import com.bloomlife.android.common.util.Utils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.activity.MyVideoViewPagerActivity;
import com.bloomlife.videoapp.activity.VideoViewPagerActivity;
import com.bloomlife.videoapp.activity.fragment.BaseMapFragment;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.UploadBackgroundService;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.ModifyVideoMessage;
import com.bloomlife.videoapp.view.UploadVideoProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 我视频页面的视频预览列表适配器。
 * 
 * @notice 这里要十分关心在点击多个上传时，滑动列表时控件重用，进度刷新等ui错乱的问题。尝试过好几种判断的方式
 * 最终得到了目前这种，使用对滑动事件的监听获取firstItemt 区间判断会比较复杂，而且要处理的情况很对。
 * 大多数处理方式都木有很好能解决多线程+滑动时ui控件刷新要对应上的问题。 这个在refreshProgress方法中也有一些说明。
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年11月21日 上午10:29:56
 */
public class MyVideoAdapter extends AbstractAdapter<Video> implements OnItemClickListener , OnScrollListener {
	
	private static final String TAG = "MyVideoAdapter";
	
	public static final int NOT_VIDEO = -1;
	
	private Activity mActivity;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	
	private DisplayImageOptions mImageLoaderOption = ImageLoaderUtils.getMyVideoPreviewImageOption();
	
	private boolean doAnimation = false ;
	
	/**
	 * key： 删除的video的position
	 * value：删除的video的状态。 
	 */
	private Map<Integer, Integer> deleteMap = new HashMap<>();
	
	private boolean isEdit;
	
	private GridView gridView;
	
	private Random random ;
	
	private CacheBean cacheBean = CacheBean.getInstance();

	public MyVideoAdapter(Activity activity, List<Video> dataList , GridView gridView) {
		super(activity, dataList);
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		
		this.gridView=gridView;
		gridView.setOnScrollListener(this);
		gridView.setOnItemClickListener(this);
		int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
		int paddingTopAndBottom = UiUtils.dip2px(activity, 14);
		int gridviewColumn = activity.getResources().getInteger(R.integer.gridview_myvideo_numcolumns);
		int itemWidth = activity.getResources().getDimensionPixelOffset(R.dimen.item_myvideo_width);
		int paddingLeftAndRight = (widthPixels - gridviewColumn * itemWidth - UiUtils.dip2px(activity, 16)) / 2;
		gridView.setPadding(paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, 0);
		random = new Random();
	}

	/**
	 * 获取视频在数据源列表中的位置
	 * @param videoid
	 * @return
	 */
	public int getVideoPosition(int videoid){
		List<Video> list = getDataList();
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getId() != null && list.get(i).getId()==videoid){
				return i;
			}
		}
		return NOT_VIDEO;
	}
	
	public void setEditable(boolean isSetting){
		this.isEdit = isSetting;
		deleteMap.clear();
		notifyDataSetChanged();
	}
	
	public List<Video> syncVideoList(List<Video> resultVideos){
		List<Video> originalVideos = getDataList();
		// 检查本地视频是否有在服务器返回的列表中的，有的话把本地的视频路径设置给服务器返回的数据中。
		List<Video> commonVideos = commonFilter(resultVideos, originalVideos);
		if (!resultVideos.isEmpty()){
			// 把所有服务器返回的视频改为已经上传状态
			for (Video newVideo : resultVideos){
				newVideo.setStatus(Video.STATUS_UPLOAD_SUCCESS);
				newVideo.setUploadTime(newVideo.getCreatetime());
			}
			// 服务器返回的视频信息添加到列表中
			commonVideos.addAll(resultVideos);
		}
		DbHelper.syncVideoList(mActivity.getApplicationContext(), resultVideos, originalVideos);
		return commonVideos;
	}

	private List<Video> commonFilter(List<Video> resultVideos, List<Video> originalVideos){
		List<Video> commonVideos = new ArrayList<>();
		if (Utils.isCollectionEmpty(resultVideos) || Utils.isCollectionEmpty(originalVideos)){
			return commonVideos;
		}
		Iterator<Video> newIterator = resultVideos.iterator();
		while (newIterator.hasNext()){
			Video newVideo = newIterator.next();
			Iterator<Video> oldIterator = originalVideos.iterator();
			while (oldIterator.hasNext()){
				Video oldVideo = oldIterator.next();
				// 未上传的要保留下来
				if (oldVideo.getStatus() != Video.STATUS_UPLOAD_SUCCESS){
					oldIterator.remove();
					commonVideos.add(oldVideo);
					break;
				}
				// 服务器端和本地共有的要保存下来，除此之外的本地视频可认为是在其他机器上被删除掉的，本机也要删除。
				if (newVideo.getVideoid().equals(oldVideo.getVideoid())){
					newIterator.remove();
					oldIterator.remove();
					commonVideos.add(oldVideo);
					break;
				}
			}
		}
		return commonVideos;
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		Holder holder = new Holder();
		View layout = mInflater.inflate(R.layout.item_myvideo, parent, false);
		holder.mPreView 		= (ImageView) layout.findViewById(R.id.item_myself_video_preview);
		holder.mVideoNew		= (ImageView) layout.findViewById(R.id.item_myself_video_new);
		holder.mDescription 	= (TextView) layout.findViewById(R.id.item_myself_video_description);
		
		holder.mCreateTime 		= (TextView) layout.findViewById(R.id.item_myself_video_createtime);
		holder.mVideoignites	= (TextView) layout.findViewById(R.id.item_myself_video_ignites);
		holder.mVideoNum 		= (TextView) layout.findViewById(R.id.item_myself_video_num);
		holder.middleInfoLayout = (ViewGroup) layout.findViewById(R.id.item_myself_video_info_layout);
		holder.mUploadProgress	= (UploadVideoProgressBar) layout.findViewById(R.id.item_myself_video_upload_progressbar);
		holder.mProgressText 	= (TextView) layout.findViewById(R.id.item_myself_video_upload_progressbar_text);
		holder.mUpload			= (TextView) layout.findViewById(R.id.item_myself_video_upload);
		holder.mUploadComplete	= (TextView) layout.findViewById(R.id.item_myself_video_upload_complete);
		
		holder.mClick 			= (CheckBox) layout.findViewById(R.id.item_myself_video_click);
		holder.mClick.bringToFront();
		holder.mCover			= layout.findViewById(R.id.item_myself_video_cover);
		
		layout.setTag(holder);
		return layout;
	}

	@Override
	protected void setViewContent(int position, View convertView, Video item) {
		Holder holder = (Holder) convertView.getTag();
		convertView.setTag(R.id.item, item);
		convertView.setTag(R.id.position, position);
		
		//设置预览图
		if (item.getFilaPath()==null || !item.getFilaPath().equals(holder.mPreView.getTag())){
			holder.mPreView.setImageBitmap(null);
			holder.mPreView.setTag(item.getFilaPath());
			String color = "#"+UIHelper.ColorList.get(random.nextInt(UIHelper.ColorList.size()-1));
			holder.mPreView.setBackgroundColor(Color.parseColor(color));
			if (item.getFilaPath() != null)
				mImageLoader.displayImage("file:///"+item.getFilaPath().replace(".mp4", ".jpg"), holder.mPreView, mImageLoaderOption, mImageLoadingListener);
			else if (item.getBigpreviewurl() != null)
				mImageLoader.displayImage(item.getBigpreviewurl(), holder.mPreView, mImageLoaderOption, mImageLoadingListener);
			else
				mImageLoader.displayImage(item.getPreviewurl(), holder.mPreView, mImageLoaderOption, mImageLoadingListener);
		}
		
		setVideoInfo(holder, item);
		
		holder.reset();
		
		if (deleteMap.containsKey(position)) {
			holder.mClick.setChecked(true);
			holder.mCover.setBackgroundResource(R.drawable.item_myvideo_select_mock);
		} else {
			holder.mClick.setChecked(false);
			holder.mCover.setBackgroundResource(R.color.item_myvideo_cover);
		}
		
		if(isEdit){
			if(isUploadVideo(item.getId())){
				//处于上传状态，不能编辑
				setUploadingProgress(item.getProgress(), holder);
			}else{
				holder.mClick.setVisibility(View.VISIBLE);
				if(doAnimation)	ObjectAnimator.ofFloat(holder.mClick, "Alpha", 0, 1).setDuration(300).start();
				holder.setLeftBottomInfo(item);
			}
		} else {
			/*  编辑按钮没有被选中   */
			if (item.getStatus() == Video.STATUS_UPLOAD_SUCCESS){
				//已经上传成功的
				holder.setUploadCompleteStyle(item);
			}else{
				//没有上传成功的
				if (isUploadVideo(item.getId())){
					// 视频已经在上传中了，显示出进度条，隐藏重新上传和上传完成按钮。
					setUploadingProgress(item.getProgress(), holder);
				} else {
					holder.mUpload.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private void setVideoInfo(Holder holder,  Video item){
		setVideoTime(holder, item);
		holder.mVideoignites.setText(String.valueOf(item.getLikenum()));
		holder.mVideoNum.setText(String.valueOf(item.getLooknum()));
	}

	private void setVideoTime(Holder holder,  Video item){
		String time = null;
		if(item.getUploadTime()>0){
			time=(DateUtils.getTimeString(mActivity, item.getUploadTime()));
		}else{
			time=(DateUtils.getTimeString(mActivity, new File(item.getFilaPath()).lastModified() / 1000));
		}
		if (System.currentTimeMillis() / 1000 - item.getCreatetime() < 60 && !UIHelper.isZH()){
			holder.mCreateTime.setTextSize(7);
		} else {
			holder.mCreateTime.setTextSize(9);
		}
		holder.mCreateTime.setText(time);
	}

		
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			ImageLoader.getInstance().resume();
		} else {
			ImageLoader.getInstance().pause();
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 预览图加载
	 */
	private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
		
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			ObjectAnimator.ofFloat(view, "alpha", 0, 1).start();
		}
		
		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 
	 * @param videoProgress
	 */
	public void refreshProgress(VideoProgress videoProgress){

		int videoid = videoProgress.getId();
		final double progress = videoProgress.getProgress();
		final int position = getVideoPosition(videoid);
		if (position == NOT_VIDEO)
			return;
		Video video = getDataList().get(position);
		
		View view = null ;
		//这里不能做滑行判断，因为是多线程的，如果在滑动中不刷新的话
		//就要求在停止时使用notifyDataSetChanged()刷新界面，不然的话，会出现上传成功，但是由于界面滑动没有刷新而导致状态是成功，但是没有刷新进度条
		for (int i = 0 , n = gridView.getChildCount(); i < n; i++) {
			View childView = gridView.getChildAt(i);
			Video tagVideo = (Video) childView.getTag(R.id.item);
			if(tagVideo.getId() != null && tagVideo.getId()==videoid){
				view = childView ;
				break ;
			}
		}
		if (view == null) return;
		final Holder holder = (Holder) view.getTag();
		//改变上传状态
		if (progress == Constants.PROGRESS_FAILURE){
			Log.d(TAG, "  上传失败，改变进度条状态为重新上传  ");
			video.setStatus(Video.STATUS_UPLOAD_FAIL);
			video.setProgress(0);
			cacheBean.putInt(mActivity, CacheKeyConstants.KEY_VIDEO_UPLOAD_FAIL, Video.STATUS_UPLOAD_FAIL);
			holder.middleInfoLayout.setVisibility(View.GONE);
			if(view!=null)	notifyDataSetChanged();  //失败了，要刷新全部上传中视频的状态
		} else if (progress != Constants.PROGRESS_SUCCESSS){
			Log.d(TAG, "  上传中，刷新进度 ：  " + progress);
			video.setProgress(progress);
			if(view==null)	return;
			setUploadingProgress(progress, holder);
		} else {
			Log.d(TAG, "  上传成功，隐藏上传进度条");
			video.setStatus(Video.STATUS_UPLOAD_SUCCESS);
			video.setVideoid(videoProgress.getServerVideoId());
			//改变状态
			if(view==null) return;
			
			//其他进度显示都消失，显示上传完成提示
			holder.mUpload.setVisibility(View.GONE);
			holder.mUploadProgress.setVisibility(View.GONE);
			holder.mProgressText.setVisibility(View.INVISIBLE);
			
			Animation animation = makeUploadSucAnimation(holder, video.getDescription() , video.getTopics());
			holder.mUploadComplete.setAnimation(animation);
			holder.mUploadComplete.setVisibility(View.INVISIBLE);
			holder.middleInfoLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @param progress
	 * @param holder
	 */
	private void setUploadingProgress(double progress, Holder holder) {
		holder.mUploadProgress.setVisibility(View.VISIBLE);
		holder.mProgressText.setVisibility(View.VISIBLE);
		holder.mUploadProgress.setProgress((float) progress);
		
		holder.mUpload.setVisibility(View.GONE);
		holder.mUploadComplete.setVisibility(View.GONE);
		holder.middleInfoLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 设置上传完成后显示的动画
	 * @param holder
	 * @return
	 */
	private Animation makeUploadSucAnimation(final Holder holder , final String desc , final String topic) {
		Animation animation = new AlphaAnimation(90, 0);
		animation.setDuration(2000);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) { }
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
			
			@Override
			public void onAnimationEnd(Animation animation) { 
				holder.setUploadCompleteStyle(desc, topic);
			}
		});
		return animation;
	}
	
	private void uploadVideo(Video video, View v){
		// 视频有可能正在上传中，所以要检查到没有正在上传后再发送到上传的服务
		int position = (Integer) v.getTag(R.id.position);
		if (!isUploadVideo(video.getId())){
			Log.i(TAG, " put position : " + position+" into map ");
			Intent intent = new Intent(mActivity.getApplicationContext() , UploadBackgroundService.class);
			intent.putExtra(UploadBackgroundService.INTENT_UPLOAD_VIDEO, video);
			mActivity.startService(intent);
			Holder holder = (Holder)v.getTag();
			setUploadingProgress(0, holder);
		}
	}
	
	/**
	 * 返回视频创建视频时间的字符串
	 * @param time
	 * @return
	 */
	private String videoCreateTime(long time){
		return new SimpleDateFormat("MM-dd", Locale.CHINESE).format(new Date(time));
	}
	
	public int getSelectVideoCount(){
		return deleteMap.size();
	}
	
	public void deleteVideoWithType(int type){
		String deleteIds = "";
		List<String> deletePathList= new ArrayList<String>();
		List<Video> removeList = new ArrayList<Video>();
		//获取本地要删除和服务器要删除的视频
		for (Map.Entry<Integer, Integer>en:deleteMap.entrySet()){
			int position = en.getKey();
			Video video = getDataList().get(position);
			removeList.add(video);
			
			if(Video.STATUS_UPLOAD_SUCCESS != en.getValue()){
				Log.d(TAG , " delete local video = " + video);
				DbHelper.deleteVideo(mActivity, video);
				deleteVideoFile(video);
			} else {
				Log.d(TAG , " delete server video = " + video);
				deleteIds = deleteIds+video.getVideoid()+",";
				// 为空说明是APP重新安装后同步的视频信息，文件是在下载缓存目录
				if (TextUtils.isEmpty(video.getFilaPath())){
					deletePathList.add(VideoFileManager.getInstance().getLocalCache(mActivity, video.getVideouri()).getAbsolutePath());
				} else {
					deletePathList.add(video.getFilaPath());
				}
			}
			
			if(!TextUtils.isEmpty(video.getFilaPath()) && video.getFilaPath().equals(BaseMapFragment.getUserSendFilePath())){
				sendDeleteBroadCast(null, video.getFilaPath());
			}
		}
		// 删除当前列表的数据
		for (Video video : removeList) {
			getDataList().remove(video);
		}
		if(!StringUtils.isEmpty(deleteIds)){
			deleteIds = deleteIds.substring(0, deleteIds.length()-1);
			sendDeleteBroadCast(deleteIds, null);
			sendNodtificationMessage(deleteIds, type, deletePathList);
		}
		deleteMap.clear();
		notifyDataSetChanged();
		if(getDataList().isEmpty()){
			((MyVideoActivity)activity).showEmptyAnimation(true);
		}
	}
	
	private void deleteVideoFile(Video video){
		if(StringUtils.isEmpty(video.getFilaPath())) return;
		deleteFile(video.getFilaPath());
	}
	
	private void deleteFile(String path){
		new File(path).delete();
	}
	
	private void sendDeleteBroadCast(String deleteIds ,String deletePath){
		Intent intent = new Intent();
		intent.setAction(Constants.ACTION_DELETE_VIDEO);
		intent.putExtra(Constants.INTENT_DELETE_SEND, deletePath);
		intent.putExtra(Constants.INTENT_DELETE_VIDEOIDS, deleteIds);
		activity.sendBroadcast(intent);
	}
	
	private void sendNodtificationMessage(String videoId, int type , List<String> deletePathList){
		final ModifyVideoMessage message = new ModifyVideoMessage();
		message.setVideos(videoId);
		message.setModifyType(type);
		message.setDeletePathList(deletePathList);
		Volley.addToTagQueue(new MessageRequest(message, new MessageRequest.Listener<ProcessResult>() {
			@Override
			public void success(ProcessResult result) {
				String[] ids = message.getVideos().split(",");
				for (String id : ids) {
					DbHelper.deleteVideoByVideoId(mActivity.getApplicationContext(), id);
				}
				if (!Utils.isEmptyCollection(message.getDeletePathList())) {
					for (String path : message.getDeletePathList()) {
						deleteFile(path);
					}
				}
			}
		}));
	}
	
	/**
	 * 选中或取消要删除、失联的视频
	 * @param v
	 * @param position
	 */
	private void checkedItem(View v, Video video,int position){
		if (deleteMap.containsKey(position)){
			deleteMap.remove(position);
			((CheckBox)v.findViewById(R.id.item_myself_video_click)).setChecked(false);
			v.findViewById(R.id.item_myself_video_cover).setBackgroundResource(R.color.item_myvideo_cover);
		} else {
			deleteMap.put(position, video.getStatus());
			Log.d(TAG, " delete vidoe : " + video);
			((CheckBox)v.findViewById(R.id.item_myself_video_click)).setChecked(true);
			v.findViewById(R.id.item_myself_video_cover).setBackgroundResource(R.drawable.item_myvideo_select_mock);
		}
	}
	
	private void clickedItem(Video video, int position, View v){
		if(video.getStatus() != Video.STATUS_UPLOAD_SUCCESS){
			//这里就不更新数据库里面的经纬度了，因为这个经纬度没有其他地方用到了、如果要计算视频距离自己限制有多远就需要更新
			if(StringUtils.isEmpty(video.getLat())||StringUtils.isEmpty(video.getLon())){
				video.setLat(cacheBean.getString(activity, CacheKeyConstants.LOCATION_LAT));
				video.setLon(cacheBean.getString(activity, CacheKeyConstants.LOCATION_LON));
			}
			
			if(StringUtils.isEmpty(video.getLat())||StringUtils.isEmpty(video.getLon())){
				UiHelper.shortToast(activity, activity.getResources().getString(R.string.tips_no_location));
				return ;
			}
			// 如果视频是未上传完成的，被点击后继续上传
			uploadVideo(video, v);
			video.setStatus(Video.STATIS_NOT_COMPLETE);
			DbHelper.updateVideo(mActivity.getApplicationContext(), video);
			// 检查看是否还有上传失败的视频
			boolean isFail = false;
			for (Video videoData:getDataList()){
				if (videoData.getStatus() == Video.STATUS_UPLOAD_FAIL){
					isFail = true;
				}
			}
			cacheBean.putInt(mActivity, CacheKeyConstants.KEY_VIDEO_UPLOAD_FAIL, isFail ? Video.STATUS_UPLOAD_FAIL : Video.STATUS_UPLOAD_SUCCESS);
		} else {
			// 已经上传完成的被点击后跳转到视频播放页面
			Intent intent = new Intent(mActivity, MyVideoViewPagerActivity.class);
			ArrayList<Video> list = new ArrayList<Video>(getDataList());
			intent.putParcelableArrayListExtra(VideoViewPagerActivity.INTENT_VIDEO_LIST, list);
			intent.putExtra(VideoViewPagerActivity.INTENT_VIDEO_POSITION, position);
			mActivity.startActivityForResult(intent, MyVideoActivity.RESULT_MY);
			mActivity.overridePendingTransition(R.anim.activity_camera_in, 0);
		}
	}
	
	class Holder{
		ImageView mPreView;
		ImageView mVideoNew;
		/**视频描述**/
		TextView mDescription;
		/**创建时间**/
		TextView mCreateTime;
		/**点赞数**/
		TextView mVideoignites;
		/**观看数**/
		TextView mVideoNum;
		
		/* * 进度条相关布局***/
		
		/**上传进度图条**/
		UploadVideoProgressBar mUploadProgress;
		/**上传进度条文本提示**/
		TextView mProgressText;
		/** 重新上传的条***/
		TextView mUpload;
		/** 上传完成ui ***/
		TextView mUploadComplete;

		/**中间显示标题和描述的布局 , 主要用来显示的**/
		ViewGroup middleInfoLayout;
		
		/** 编辑时选中视频的按钮 **/
		CheckBox mClick;
		
		View mCover;
		
		private void reset(){
			
			mDescription.setVisibility(View.INVISIBLE);
			mVideoignites.setVisibility(View.INVISIBLE);
			mVideoNum.setVisibility(View.INVISIBLE);
			mCreateTime.setVisibility(View.INVISIBLE);
			
			mDescription.setText("");
			mVideoignites.setText("");
			mVideoNum.setText("");
			
			mUploadProgress.setVisibility(View.INVISIBLE);
			mProgressText.setVisibility(View.INVISIBLE);
			mUpload.setVisibility(View.INVISIBLE);
			
			Animation animation = mUploadComplete.getAnimation();
			if (animation != null){
				animation.cancel();
				mUploadComplete.clearAnimation();
			}
			mUploadComplete.setVisibility(View.INVISIBLE);
			
			mClick.setVisibility(View.GONE);
		}
		
		private void setUploadCompleteStyle(Video item,String desc ,String topic){
			mDescription.setVisibility(View.VISIBLE);
			mCreateTime.setVisibility(View.VISIBLE);
			setVideoTime(this, item);
			
			mDescription.setText(item!=null?item.getDescription():desc);
			setLeftBottomInfo(item);
		}
		
		/**
		 * 左下角显示的信息
		 */
		private void setLeftBottomInfo(Video item){
			mVideoignites.setVisibility(View.VISIBLE);
			mVideoNum.setVisibility(View.VISIBLE);
			mVideoignites.setText(item != null ? String.valueOf(item.getLikenum()):"0");
			mVideoNum.setText(item != null ? String.valueOf(item.getLooknum()):"0");
		}
		
		private void setUploadCompleteStyle(Video item){
			setUploadCompleteStyle(item , null , null);
		}
		
		private void setUploadCompleteStyle(String desc ,String topic){
			setUploadCompleteStyle(null, desc, topic);
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Video video = (Video) v.getTag(R.id.item);
		if (isUploadVideo(video.getId())){ //已经是上传状态的不允许操作了
			return ;
		}else if (isEdit){
			checkedItem(v,video, position);
		}else {
			clickedItem(video, position, v);
		}
	}

	public boolean isDoAnimation() {
		return doAnimation;
	}

	public void setDoAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	
}

