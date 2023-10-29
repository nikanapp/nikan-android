/**
 * 
 */
package com.bloomlife.videoapp.view;

import pl.droidsonroids.gif.GifImageView;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.PictureActivity;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.easemob.chat.EMChat;

import emojicon.EmojiconGridFragment;
import emojicon.EmojiconsFragment;
import emojicon.emoji.Emojicon;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 私信页面下方的文字发送条
 * @date 2015年4月29日 下午4:10:52
 */
public class ChatSendToolController extends RelativeLayout implements OnClickListener {

	/**
	 * @param context
	 */
	public ChatSendToolController(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ChatSendToolController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ChatSendToolController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	@ViewInject(id = R.id.switcher)
	private ViewSwitcher switcher;
	
	
	@ViewInject(id = R.id.audio_text_tips)
	private GifImageView mAudioTextTips;
	
	@ViewInject(id = R.id.message, click = ViewInject.DEFAULT)
	private EditText editText;
	
	@ViewInject(id = R.id.btn_keyboard, click = ViewInject.DEFAULT)
	private ImageView keyboardBtn;
	
	@ViewInject(id = R.id.btn_emoji, click = ViewInject.DEFAULT)
	private ImageView emojibtn;
	
	@ViewInject(id = R.id.btn_picture, click = ViewInject.DEFAULT)
	private ImageView mBtnPicture;

	@ViewInject(id = R.id.send, click = ViewInject.DEFAULT)
	private ImageView send;
	
	private void init(Context context){
		View layout = inflate(context, R.layout.view_comment_send, this);
		FinalActivity.initInjectedView(this, layout);
		
		// 清除焦点
		editText.clearFocus();
		editText.addTextChangedListener(mTextWatcher);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			mAudioTextTips.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_keyboard:
			switcher.showPrevious();															// 切换成表情按钮
			if (mListener != null){
				mListener.onKeyboard();
			}
			break;
		case R.id.btn_emoji:
			switcher.showNext();																// 切换成键盘按钮
			if (mListener != null){
				mListener.onEmoji();
			}
			break;
		case R.id.send:
			if (mListener != null){
				mListener.onSend(editText.getText().toString());
			}
			break;
			
		case R.id.message:
			if (switcher.getCurrentView().getId() != R.id.btn_emoji){
				switcher.showPrevious();		
			}
			break;
			
		case R.id.btn_picture:
			if (mListener != null){
				mListener.onPicture();
			}
			break;

		default:
			break;
		}
	}

	private SendViewListener mListener;
	
	public void setSendViewListener(SendViewListener l){
		mListener = l;
	}
	
	public interface SendViewListener{
		void onKeyboard();
		void onEmoji();
		void onSend(String text);
		void onPicture();
	}
	
	public void showKeyBoardIcon(){
		if (switcher.getCurrentView().getId()!=R.id.btn_emoji){
			switcher.showPrevious();
		}
	}
	
	public void clearInputEditText(){
		editText.setText("");
	}
	
	public IBinder getEditWindowToken(){
		return editText.getWindowToken();
	}
	
	public EditText getEditText(){
		return editText;
	}

}
