package com.bloomlife.videoapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.android.view.SoftKeyboardLayout;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.CommentAdapter;
import com.bloomlife.videoapp.adapter.CommentTagAdapter;
import com.bloomlife.videoapp.adapter.CommentTextListAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.AnalyticUtil;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.common.VideoReportListener;
import com.bloomlife.videoapp.common.util.SoundPlay;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.CommentInfo;
import com.bloomlife.videoapp.model.Commenttags;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.DeleteAnonCommentTextMessage;
import com.bloomlife.videoapp.model.message.DeleteRealCommentTextMessage;
import com.bloomlife.videoapp.model.message.GetCommentTextMessage;
import com.bloomlife.videoapp.model.message.GetVideoMessage;
import com.bloomlife.videoapp.model.message.SendAnonCommentMessage;
import com.bloomlife.videoapp.model.message.SendCommentTextMessage;
import com.bloomlife.videoapp.model.message.SendRealCommentMessage;
import com.bloomlife.videoapp.model.message.SendStoryCommentText;
import com.bloomlife.videoapp.model.result.GetCommentTextResult;
import com.bloomlife.videoapp.model.result.GetVideoResult;
import com.bloomlife.videoapp.model.result.SendCommentTextResult;
import com.bloomlife.videoapp.model.result.SendStoryCommentTextResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.bloomlife.videoapp.view.ObservableScrollView;
import com.bloomlife.videoapp.view.ReportOptionView;
import com.bloomlife.videoapp.view.GlobalProgressBar;

import emojicon.EmojiconGridFragment;
import emojicon.EmojiconsFragment;
import emojicon.emoji.Emojicon;
/**
 * 视频评论的弹出窗
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年11月21日 上午10:30:55
 */
public class VideoCommentActivity extends FragmentActivity implements OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{
	public static final String TAG = VideoCommentActivity.class.getSimpleName();

	public static final String INTENT_INFO = "intentCommentInfo";

	public static final String RESULT_INFO = "resultCommentInfo";
	
	public static final int RESULT_COMMENT_OK    = 10001;
	public static final int RESULT_COMMENT_ERROR = 10002;
	
	public static final int PAGE_SIZE = 20;
	
	@ViewInject(id = R.id.dialog_video_comment_close, click = ViewInject.DEFAULT)
	private View mBtnClose;
	
	@ViewInject(id = R.id.video_comment_btn_comment_tag, click = ViewInject.DEFAULT)
	private ImageView mBtnCommentTag;
	
	@ViewInject(id = R.id.video_comment_btn_emoji, click = ViewInject.DEFAULT)
	private ViewSwitcher mBtnCommentEmoji;
	
	@ViewInject(id = R.id.video_comment_btn_send, click = ViewInject.DEFAULT)
	private TextView mBtnSend;
	
	@ViewInject(id = R.id.add_impression_text)
	private TextView mImpressionText;
	
	@ViewInject(id = R.id.dialog_video_comment_layout, click = ViewInject.DEFAULT)
	private ViewGroup mCommentLayout;
	
	@ViewInject(id = R.id.dialog_video_comment_scroll)
	private ObservableScrollView mCommentScroll;
	
	@ViewInject(id = R.id.input_layout)
	private ViewGroup mInputLayout;
	
	@ViewInject(id = R.id.select_tag_layout)
	private ViewGroup mSelectTagLayout;
	
	@ViewInject(id = R.id.dialog_video_comement_load)
	private GlobalProgressBar mLoadProgress;
	
	@ViewInject(id = R.id.dialog_video_comment_first_comment)
	private View mFirstCommentTip;
	
	@ViewInject(id = R.id.add_impression_btn_complete, click = ViewInject.DEFAULT)
	private View mBtnImpressionComplete;
	
	@ViewInject(id = R.id.add_impression, click = ViewInject.DEFAULT)
	private View mBtnAddImpression;
	
	@ViewInject(id = R.id.dialog_video_comment_grid)
	private GridView mTopCommentTagGrid;
	
	@ViewInject(id = R.id.dialog_video_comment_empty)
	private FooterTipsView mEmpty;
	
	@ViewInject(id = R.id.dialog_video_comment_text_moreload)
	private View mCommentTextMoreLoadView;
	
	@ViewInject(id = R.id.video_comment_layout)
	private SoftKeyboardLayout mContentLayout;
	
	@ViewInject(id = R.id.dialog_video_comment_tag_select)
	private GridView mBottomCommentTagGrid;
	
	@ViewInject(id = R.id.dialog_video_comment_text_list)
	private ListView mCommentTextList;
	
	@ViewInject(id = R.id.emojicons)
	private View mEmojiFragment;
	
	@ViewInject(id = R.id.video_comment_input)
	private EditText mCommentInput;
	
	private SoundPlay mSoundPlay = new SoundPlay(this);
	
	private CommentAdapter mCommentGridAdapter;
	private CommentTagAdapter mTopCommentTagAdapter;
	private CommentTextListAdapter mCommentTextListAdapter;
	private Video mVideo;
	private String mStoryId;
	private boolean mIsReal;
	
	private boolean mShowTag;
	private boolean mShowEmoji;
	private boolean mIsShowSoftKeyboar;
	/** 是否在加载更多评论 */
	private boolean mIsMoreLoad;
	/** 是否有更多评论 */
	private boolean mHasMoreData;
	
	private int mPageNum = 2;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_video_comment);
		FinalActivity.initInjectedView(this);
		
		mCommentScroll.setEnabled(false);
		mEmojiFragment.setVisibility(View.GONE);
		mContentLayout.setOnSoftKeyboardListener(softKeyboardListener);

		registerReceiver(mRefreshCommentReceiver, new IntentFilter(Constants.ACTION_COMMENT)); 
	}
	
	private void setCommentData(CommentInfo info){
		List<Commenttags> commentTagsList = AppContext.getSysCode().getCommenttags();
		mVideo 	 = info.getVideo();
		mStoryId = info.getStoryId();
		mIsReal  = !TextUtils.isEmpty(mStoryId);
		mHasMoreData = info.getCommentTextList().size() == PAGE_SIZE ? true : false;
		initLayout(info.getAllowComment());
		initCommentUI(info.getCommentTextList(), commentTagsList, info.getCommentList(), info.getSelectComment());
	}
	
	private void initCommentUI(final List<CommentText> commentTextList, final List<Commenttags> commenttagsList, final List<Comment> commentList, final String mSelectComment){
		initCommentTextList(commentTextList);
		initTopCommentTag(commentList, mSelectComment);
		initCommentScroll();
		initBottomCommentTag(commenttagsList, mSelectComment);
		isCommentEmpty();
	}
	
	private void isCommentEmpty(){
		boolean commentTagEmpty = Utils.isEmpty(mTopCommentTagAdapter.getDataList());
		boolean commentTextEmpty = Utils.isEmpty(mCommentTextListAdapter.getDataList());
		// 如果评论标签为空，要把评论标签视图隐藏掉
		mTopCommentTagGrid.setVisibility(commentTagEmpty ? View.GONE : View.VISIBLE);
		// 如果评论标签和评论文本都为空，要把无评论的提示语显示出来
		mEmpty.setVisibility(commentTagEmpty && commentTextEmpty ? View.VISIBLE : View.GONE);
	}
	
	private void initTopCommentTag(List<Comment> commentList, String mSelectComment){
		mTopCommentTagAdapter = new CommentTagAdapter(this, commentList);
		mTopCommentTagAdapter.setSelectComment(mSelectComment);
		mTopCommentTagGrid.setAdapter(mTopCommentTagAdapter);
		mTopCommentTagGrid.setOnItemClickListener(mTopCommentTagGridListener);
		mTopCommentTagGrid.setVisibility(Utils.isEmpty(commentList) ? View.GONE : View.VISIBLE);
	}
	
	private void initBottomCommentTag(List<Commenttags> commenttagsList, String mSelectComment){
		// 屏幕下方选择框的评论标签
		mCommentGridAdapter = new CommentAdapter(this, commenttagsList);
		mCommentGridAdapter.setSelectComment(mSelectComment);
		mBottomCommentTagGrid.setAdapter(mCommentGridAdapter);
		mBottomCommentTagGrid.setOnItemClickListener(itemClickListener);
		mBottomCommentTagGrid.setVerticalScrollBarEnabled(false);
	}
	
	private void initCommentTextList(List<CommentText> commentTexts){
		mCommentTextListAdapter = new CommentTextListAdapter(this, commentTexts, mVideo.getUid());
		mCommentTextList.setAdapter(mCommentTextListAdapter);
		mCommentTextList.setOnItemLongClickListener(commentTextLongClickListener);
		mCommentTextList.setOnItemClickListener(commentTextClickListener);
		mCommentTextList.setVisibility(View.VISIBLE);
		mCommentTextList.setDividerHeight(0);
	}
	
	private void initLayout(int allowcomment){
		if (Constants.COMMENT_OFF == allowcomment){
			if (CacheBean.getInstance().getInt(this, CacheKeyConstants.KEY_FIRST_COMMENT, 0) < 1){
				CacheBean.getInstance().putInt(this, CacheKeyConstants.KEY_FIRST_COMMENT, 1);
				mFirstCommentTip.setVisibility(View.VISIBLE);
			}
			mInputLayout.setVisibility(View.INVISIBLE);
			mSelectTagLayout.setVisibility(View.VISIBLE);
		} else {
			mInputLayout.setVisibility(View.VISIBLE);
			mSelectTagLayout.setVisibility(View.INVISIBLE);
		}
		mEmpty.setText(getString(R.string.activity_comment_empty));
		mImpressionText.setTypeface(UIHelper.getHelveticaTh(this));
	}
	
	private void initCommentScroll(){
		mCommentScroll.setEnabled(true);
		mCommentScroll.setVerticalScrollBarEnabled(false);
		// 设置评论页被下拉到90dp时关闭评论页
		mCommentScroll.setPullHeight(UiUtils.dip2px(this, 90));
		mCommentScroll.setOnScrollListener(mOnScrollListener);
		mCommentScroll.setOnSingleClickListener(mSingleClickListener);
	}
	
	private ObservableScrollView.OnScrollListener mOnScrollListener = new ObservableScrollView.OnScrollListener() {
		
		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			if (Math.abs(t-oldt) > 5 && mIsShowSoftKeyboar){
				hideInput();
			}
			// 加载更多评论
			if (mHasMoreData && !mIsMoreLoad && t >= (mCommentScroll.getChildAt(0).getMeasuredHeight()-mCommentScroll.getMeasuredHeight())){
				GetCommentTextMessage message =  new GetCommentTextMessage(mIsReal ? mStoryId : mVideo.getVideoid(), PAGE_SIZE, mPageNum);
				Volley.addToTagQueue(new MessageRequest(message, mGetCommentReqListener));
			}
		}

		@Override
		public void onPull() {
			finish();
		}
	};
	
	private ObservableScrollView.OnSingleClickListener mSingleClickListener = new ObservableScrollView.OnSingleClickListener() {
		
		@Override
		public void onClick() {
			hideInput();
		}
	};
	
	private void hideInput(){
		mEmojiFragment.setVisibility(View.GONE);
		mBtnImpressionComplete.setVisibility(View.GONE);
		mBottomCommentTagGrid.setVisibility(View.GONE);
		mBtnCommentEmoji.setDisplayedChild(0);
		hideSoftKeyboar();
	}
	
	private void showSoftKeyboar(){
		UIHelper.showSoftInput(this);
	}
	
	private void hideSoftKeyboar(){
		UIHelper.hideSoftInput(VideoCommentActivity.this, mCommentInput);
	}
	
	
	private SoftKeyboardLayout.OnSoftKeyboardListener softKeyboardListener = new SoftKeyboardLayout.OnSoftKeyboardListener() {
		
		@Override
		public void onShownEnd() {
			Log.i(TAG, "onShownEnd");
		}
		
		@Override
		public void onShown(int keyboardHeight) {
			Log.i(TAG, "onShown");
			mIsShowSoftKeyboar = true;
			mBottomCommentTagGrid.setVisibility(View.GONE);
			mEmojiFragment.setVisibility(View.GONE);
		}
		
		@Override
		public void onHiddenEnd() {
			Log.i(TAG, "onHiddenEnd");
		}
		
		@Override
		public void onHidden() {
			mIsShowSoftKeyboar = false;
			Log.i(TAG, "onHidden");
			if (mShowTag){
				mShowTag = false;
				mEmojiFragment.setVisibility(View.GONE);
				mBottomCommentTagGrid.setVisibility(View.VISIBLE);
			} else if(mShowEmoji){
				mShowEmoji = false;
				mBottomCommentTagGrid.setVisibility(View.GONE);
				mEmojiFragment.setVisibility(View.VISIBLE);
			}
		}
	};
	
	/**
	 * 位于屏幕下方的评论标签窗口的点击事件
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		
		private View oldCommentView;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// 检查用户是否登陆
			if (!isLogin())
				return;
			Commenttags commenttags = (Commenttags) parent.getAdapter().getItem(position);
			Comment comment = Comment.makeComment(commenttags, "1", position);
			sendTagComment(comment.getCommentid());
			setTopCommentTag(comment);
			setBottomCommentTag(parent, view, position);
		}
		
		private void setTopCommentTag(Comment comment){
			// 点击的是否是新的标签，如果是新标签就添加到屏幕上方的评论标签里，如果是跟上一次点击的一样则移除掉上一次点击的标签。
			if (mCommentGridAdapter.setSelectComment(comment.getContent())){
				mTopCommentTagAdapter.enabledNumAnimator(true); // 新添加的标签要触发右上角的数值动画
				mTopCommentTagAdapter.addComment(comment);
			} else {
				mTopCommentTagAdapter.removeLastComment();
				mTopCommentTagAdapter.removeSelectComment();
				mTopCommentTagAdapter.notifyDataSetChanged();
			}
			isCommentEmpty();
		}
		
		private void setBottomCommentTag(AdapterView<?> parent, View view, int position){
			if (oldCommentView != null){
				mCommentGridAdapter.getView(position, oldCommentView, parent);
				oldCommentView = mCommentGridAdapter.getView(position, view, parent);
			} else {
				mCommentGridAdapter.notifyDataSetChanged();
			}
			mSoundPlay.play(R.raw.comment);
		}
	};
	
	/**
	 * 屏幕上方的评论标签被点击
	 */
	private OnItemClickListener mTopCommentTagGridListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!isLogin())
				return;
			Comment comment = (Comment)parent.getAdapter().getItem(position);
			mTopCommentTagAdapter.enabledNumAnimator(true);
			mTopCommentTagAdapter.setComment(comment);
			sendTagComment(comment.getCommentid());
			mCommentGridAdapter.setSelectComment(comment.getContent());
			mCommentGridAdapter.notifyDataSetChanged();
			mSoundPlay.play(R.raw.comment);
			isCommentEmpty();
		}
	};
	
	/**
	 * 发送标签评论到服务器
	 * @param commentId
	 */
	private void sendTagComment(String commentId){
		if (!TextUtils.isEmpty(mVideo.getVideoid()) && !TextUtils.isEmpty(commentId)){
			BaseMessage message = null;
			if (mIsReal){
				message = new SendRealCommentMessage(mStoryId, commentId);
			} else {
				message = new SendAnonCommentMessage(mVideo.getVideoid(), commentId);
			}
			Volley.addToTagQueue(new MessageRequest(message, new MessageRequest.Listener<ProcessResult>() {
				@Override
				public void start() {
					//统计印象标签行为
					Map<String, String> map = new HashMap<>();
					map.put("videoid", mVideo.getVideoid());
					AnalyticUtil.sendAnalytisEvent(getApplicationContext(), AnalyticUtil.Event_Impression_Comment, map);
				}

				@Override
				public void success(ProcessResult result) {
					Logger.i(TAG, "评论发送成功");
				}
			}));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_video_comment_close:
			finish();
			break;
			
		case R.id.video_comment_btn_comment_tag:
			if (mBottomCommentTagGrid.getVisibility() == View.GONE){
				if (mIsShowSoftKeyboar){
					mShowTag = true;
					hideSoftKeyboar();
				} else {
					mEmojiFragment.setVisibility(View.GONE);
					mBottomCommentTagGrid.setVisibility(View.VISIBLE);
				}
				mBottomCommentTagGrid.setVisibility(View.VISIBLE);
			} else {
				mBottomCommentTagGrid.setVisibility(View.GONE);
			}
			mBtnCommentEmoji.setDisplayedChild(0);
			break;
			
		case R.id.video_comment_btn_emoji:
			if (mEmojiFragment.getVisibility() == View.GONE){
				if (mIsShowSoftKeyboar) {
					// 收起键盘和弹出表情
					mShowEmoji = true;
					hideSoftKeyboar();
				} else {
					// 表情和标签之间切换
					mBottomCommentTagGrid.setVisibility(View.GONE);
					mEmojiFragment.setVisibility(View.VISIBLE);
				}
			} else {
				// 收起表情和弹出键盘
				mEmojiFragment.setVisibility(View.GONE);
				showSoftKeyboar();
			}
			mBtnCommentEmoji.showNext();
			break;
			
		case R.id.video_comment_btn_send:
			// 检查是否登陆
			if (!isLogin()){
				break;
			}
			if (TextUtils.isEmpty(mCommentInput.getText().toString().trim())){
				Toast.makeText(this, getResources().getString(R.string.video_comment_input_first), Toast.LENGTH_SHORT).show();
			} else {
				mCommentScroll.smoothScrollTo(0, 0);  
				hideSoftKeyboar();
				mBottomCommentTagGrid.setVisibility(View.GONE);
				mEmojiFragment.setVisibility(View.GONE);
				sendCommentText(mCommentInput.getText().toString().trim());
				mCommentInput.setText("");
			}
			break;
			
		case R.id.add_impression_btn_complete:
			mBottomCommentTagGrid.setVisibility(View.GONE);
			mBtnImpressionComplete.setVisibility(View.GONE);
			mCommentScroll.smoothScrollTo(0, 0);
			break;
			
		case R.id.add_impression:
			mFirstCommentTip.setVisibility(View.GONE);
			mBottomCommentTagGrid.setVisibility(View.VISIBLE);
			mBtnImpressionComplete.setVisibility(View.VISIBLE);
			mCommentScroll.smoothScrollTo(0, 0);
			break;
			
		case R.id.dialog_video_comment_layout:
			hideInput();
			break;
			
		default:
			break;
		}
	}

	/**
	 * 发送文本评论
	 * @param content
	 */
	private void sendCommentText(String content){
		Account account = Utils.getAccount(this);
		CommentText commentText = CommentText.makeCommentText(content, account, mIsReal);
		if (mIsReal){
			// 实名世界的评论
			sendRealTextMessage(content, commentText);
		} else {
			// 匿名世界的评论
			sendAnonTextMessage(content, commentText);
		}
		mCommentTextListAdapter.getDataList().add(0, commentText);
		mCommentTextListAdapter.notifyDataSetChanged();
	}

	private void sendRealTextMessage(String content, CommentText commentText){
		Volley.addToTagQueue(
				new MessageRequest(
						new SendStoryCommentText(mStoryId, content),
						new SendRealCommentReqListener(commentText)
				)
		);
	}

	private void sendAnonTextMessage(String content, CommentText commentText){
		SendCommentTextMessage message = new SendCommentTextMessage();
		message.setVideoId(mVideo.getVideoid());
		message.setContent(content);
		Volley.addToTagQueue(
				new MessageRequest(
						message,
						new SendAnonCommentReqListener(mVideo.getVideoid(), commentText)
				)
		);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(mCommentInput);
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(mCommentInput, emojicon);
	}

	@Override
	public void finish() {
		if (mTopCommentTagAdapter != null && mCommentTextListAdapter != null){
			setResult(RESULT_COMMENT_OK, getResultIntent());
		} else {
			setResult(RESULT_COMMENT_ERROR);
		}
		super.finish();
		overridePendingTransition(0, R.anim.activity_bottom_out);
	}
	
	private Intent getResultIntent(){
		Intent intent = new Intent();
		if (mTopCommentTagAdapter == null) return intent;
		Comment comment = mTopCommentTagAdapter.getSelectComment();
		// 返回评论信息
		CommentInfo info = new CommentInfo();
		info.setCommentList(mTopCommentTagAdapter.getDataList());
		info.setCommentTextList(mCommentTextListAdapter.getDataList());
		info.setSelectComment(comment == null ? null :comment.getContent());
		info.setVideo(mVideo);
		intent.putExtra(RESULT_INFO, info);
		return intent;
	}
	
	private OnItemLongClickListener commentTextLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// 检查用户是否登陆
			if (!isLogin())
				return false;
			CommentText commentText = (CommentText) parent.getItemAtPosition(position);
			if (CacheBean.getInstance().getLoginUserId().equals(commentText.getUserid())){
				AlterDialog.showDialog(
						VideoCommentActivity.this,
						getString(R.string.activity_video_comment_delete),
						new DeleteCommentTextListener(mVideo.getVideoid(), commentText));
			} else {
				ReportOptionView.showDialog(
						VideoCommentActivity.this,
						new VideoReportListener(VideoCommentActivity.this, mVideo.getVideoid()));
			}
			return true;
		}
		
	};
	
	private OnItemClickListener commentTextClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CommentText commentText = (CommentText) parent.getItemAtPosition(position);
			// 检查当前评论是否自己发送的
			if (CacheBean.getInstance().getLoginUserId().equals(commentText.getUserid()))
				return;
			// 检查用户是否登陆
			if (!isLogin())
				return;
			Intent intent = null;
			if (TextUtils.isEmpty(commentText.getUsername())){
				intent = new Intent(VideoCommentActivity.this, AnonymousChatActivity.class);
				intent.putExtra(AnonymousChatActivity.INTENT_USERNAME, commentText.getUserid());
				intent.putExtra(AnonymousChatActivity.INTENT_CHAT_ID, mVideo.getVideoid());
				intent.putExtra(AnonymousChatActivity.INTENT_VIDEO, mVideo);
			} else {
				intent = new Intent(VideoCommentActivity.this, RealNameChatActivity.class);
				intent.putExtra(RealNameChatActivity.INTENT_USERNAME, commentText.getUserid());
			}
			startActivity(intent);
		}
		
	};

	private boolean isLogin(){
		return Utils.isLogin(VideoCommentActivity.this, true);
	}
	
	class DeleteCommentTextListener implements OnClickListener {
		
		private String mVideoId;
		private CommentText mCommentText;
		
		public DeleteCommentTextListener(String videoId, CommentText commentText){
			mVideoId = videoId;
			mCommentText = commentText;
		}
		
		@Override
		public void onClick(View v) {
			if (mIsReal){
				deleteRealCommentText();
			} else {
				deleteAnonCommentText();
			}
			mCommentTextListAdapter.getDataList().remove(mCommentText);
			mCommentTextListAdapter.notifyDataSetChanged();
			isCommentEmpty();
		}

		private void deleteAnonCommentText(){
			Volley.addToTagQueue(new MessageRequest(new DeleteAnonCommentTextMessage(mVideoId, mCommentText.getCid())));
		}

		private void deleteRealCommentText(){
			Volley.addToTagQueue(new MessageRequest(new DeleteRealCommentTextMessage(mStoryId, mCommentText.getId())));
		}
	}

	private class SendAnonCommentReqListener extends MessageRequest.Listener<SendCommentTextResult>{

		private String mVideoId;
		private CommentText mCommentText;

		SendAnonCommentReqListener(String videoId, CommentText commentText){
			mVideoId = videoId;
			mCommentText = commentText;
		}

		@Override
		public void start() {
			//统计评论行为
			Map<String, String> map = new HashMap<String, String>();
			map.put("videoid", mVideoId);
			AnalyticUtil.sendAnalytisEvent(getApplicationContext(), AnalyticUtil.Event_Comment, map);
		}

		@Override
	    public void success(SendCommentTextResult result) {
			mCommentText.setCid(result.getCid());
	    }
	}

	private class SendRealCommentReqListener extends MessageRequest.Listener<SendStoryCommentTextResult>{

		private CommentText commentText;

		SendRealCommentReqListener(CommentText commentText){
			this.commentText = commentText;
		}

		@Override
		public void success(SendStoryCommentTextResult result) {
			commentText.setId(result.getCid());
		}
	}

	private MessageRequest.Listener mGetCommentReqListener = new MessageRequest.Listener<GetCommentTextResult>(){
		@Override
		public void start() {
			mIsMoreLoad = true;
			mCommentTextMoreLoadView.setVisibility(View.VISIBLE);
		}

		@Override
	    public void success(GetCommentTextResult result) {
			List<CommentText> commentTests = result.getCommentTexts();
			mHasMoreData = commentTests.size() < PAGE_SIZE ? false : true;
			mCommentTextListAdapter.addDataList(commentTests);
			mCommentTextListAdapter.notifyDataSetChanged();
			mPageNum++;
	    }

		@Override
		public void finish() {
			mIsMoreLoad = false;
			mCommentTextMoreLoadView.setVisibility(View.GONE);
		}
	};
	
	private BroadcastReceiver mRefreshCommentReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mLoadProgress.getVisibility() == View.VISIBLE){
				CommentInfo info = intent.getParcelableExtra(INTENT_INFO);
				setCommentData(info);
				enabledButton();
			}
		}
	};
	
	private void enabledButton(){
		mBtnSend.setEnabled(true);
		mCommentInput.setEnabled(true);
		mBtnCommentTag.setEnabled(true);
		mBtnCommentEmoji.setEnabled(true);
		mLoadProgress.stopAnimator();
		mLoadProgress.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mRefreshCommentReceiver);
		mSoundPlay.release();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private boolean isInit = true;
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// 初始化UI
		if (hasFocus && isInit){
			isInit = false;
			// 评论信息是否加载完成
			CommentInfo info = getIntent().getParcelableExtra(INTENT_INFO);
			if (info != null){
				setCommentData(info);
			} else {
				mBtnSend.setEnabled(false);
				mCommentInput.setEnabled(false);
				mBtnCommentTag.setEnabled(false);
				mBtnCommentEmoji.setEnabled(false);
				mLoadProgress.setVisibility(View.VISIBLE);
				mLoadProgress.startAnimator();
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

}