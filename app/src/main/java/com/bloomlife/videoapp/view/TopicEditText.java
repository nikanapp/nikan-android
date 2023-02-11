/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.List;
import java.util.regex.Pattern;

import com.bloomlife.videoapp.common.util.TopicHandler;
import com.bloomlife.videoapp.common.util.TopicHandler.TopicView;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 话题的输入框，能监听输入框里的话题变化和将话题文字变色
 * @date 2014年12月15日 下午3:24:55
 */
public class TopicEditText extends EditText implements TopicView {

	public static final String TAG = TopicEditText.class.getSimpleName();
	
	private TopicHandler mTopicHandler;
	private List<String> mSelectTopicList;

	public TopicEditText(Context context) {
		super(context);
		init(context);
	}

	public TopicEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TopicEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		setFocusable(true);
		setFilters(new InputFilter[] { new EditFilter() });
	}

	@Override
	protected void onTextChanged(CharSequence text, final int start, int lengthBefore, int lengthAfter) {
//		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (mTopicHandler == null)
			mTopicHandler = new TopicHandler(getContext());
		List<String> newTopicList = mTopicHandler.setTopicStyle(getText());
		// 判断话题列表里选中的话题是否被改动了。
		if (mSelectTopicList != null && mTopicChangeListener != null){
			for (String selectTopic:mSelectTopicList){
				if (!newTopicList.contains(selectTopic)){
					mTopicChangeListener.onTopicChange(selectTopic);
				}
			}
		}
	}
	
	/**
	 * 
	 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
	 * 
	 *         描述框的过滤器，限制描述为中文三十个，英文六十个字符。
	 * @date 2014年12月12日 下午5:30:50
	 */
	class EditFilter implements InputFilter {

		public static final int ZH_LENGHT = 30;
		public static final int EN_LENGHT = 60;
		private Pattern ZH_PATTERN = Pattern.compile("[\u4E00-\u9FA5]");

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			int lenght = getDescriptLenght(source.toString()+dest.toString());
			if (lenght >= 60) {
				return "";
			} else {
				return source.subSequence(0, lenght > source.length() ? source.length() : lenght);
			}
		}
		
		private int getDescriptLenght(String ch){
			char[] chars = ch.toCharArray();
			int lenght = 0;
			for (char c:chars){
				if (ZH_PATTERN.matcher(String.valueOf(c)).find()){
					lenght += 2;
				} else {
					lenght += 1;
				}
			}
			return lenght;
		}

	}
	
	/**
	 * 返回一个话题字符串，如：话题1,话题2,话题3,话题4
	 * @return
	 */
	public String getTopicString(){
		StringBuilder topicStr = new StringBuilder();
		List<String> topicList = getTopicList();
		for (int i = 0; i < topicList.size(); i++) {
			if (i == 0) {
				topicStr.append(topicList.get(i).replace("#", "").replace(" ", ""));
			} else {
				topicStr.append(",").append(topicList.get(i).replace("#", "").replace(" ", ""));
			}
		}
		return topicStr.toString();
	}
	
	/**
	 * 返回输入框里话题的列表
	 * @return
	 */
	@Override
	public List<String> getTopicList(){
		return mTopicHandler.getTopicList(getText());
	}
	
	/**
	 * 返回视频描述
	 * @return
	 */
	public String getDesciption() {
		String description = getText().toString();
//		if (description != null) {
//			// 去掉话题
//			for (String topic : getTopicList()) {
//				description = description.replace(topic, "");
//			}
//		}
		return description;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	private TopicChangeListener mTopicChangeListener;
	
	public void addTopicChangeListener(TopicChangeListener l, List<String> topicList){
		mTopicChangeListener = l;
		mSelectTopicList = topicList;
	}
	
	public interface TopicChangeListener{
		void onTopicChange(String topic);
	}

}
