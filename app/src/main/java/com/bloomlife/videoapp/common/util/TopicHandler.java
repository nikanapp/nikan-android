/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.bloomlife.videoapp.R;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 处理输入的文本，判断输入的文本是否为话题，是的话把文字颜色改变。
 * @date 2014年12月24日 下午12:25:10
 */
public class TopicHandler{
	
	public static final String TAG = TopicHandler.class.getSimpleName();
	
	private Resources mResources;
	
	private int topicColor=R.color.topic_select ; 
	
	public TopicHandler(Context context){
		mResources = context.getResources();
	}
	
	/**
	 * 话题的正则表达式，
	 */
	public static final Pattern TOPIC_PATTERN = Pattern.compile("#[^#\\s]+\\s");
	
	public List<String> setTopicStyle(Spannable text){
		removeSpan(text);
		Matcher matcher = TOPIC_PATTERN.matcher(text);
		List<String> topicList = new ArrayList<>();
		while (matcher.find()) {
			String topic = matcher.group();
			int topicStart = text.toString().indexOf(topic);
			ForegroundColorSpan span = getSpan(topicColor);
			text.setSpan(span, topicStart, topicStart + topic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			topicList.add(topic);
		}
		return topicList;
	}
	
	/**
	 * 根据输入的文本，返回其中的话题列表
	 * @param text
	 * @return
	 */
	public List<String> getTopicList(CharSequence text){
		List<String> topicList = new ArrayList<>();
		if (!TextUtils.isEmpty(text)){
			Matcher matcher = TopicHandler.TOPIC_PATTERN.matcher(text);
			while (matcher.find()) {
				topicList.add(matcher.group());
			}
		} 
		return topicList;
	}
	
	private void removeSpan(Spannable spannable){
		ForegroundColorSpan[] spans = spannable.getSpans(0, spannable.length(), ForegroundColorSpan.class);
		for (ForegroundColorSpan span:spans){
			spannable.removeSpan(span);
		}
	}
	
	private ForegroundColorSpan getSpan(int color) {
		return new ForegroundColorSpan(mResources.getColor(color));
	}
	
	public int getTopicColorRes() {
		return topicColor;
	}

	public void setTopicColorRes(int topicColor) {
		this.topicColor = topicColor;
	}

	public interface TopicView{
		List<String> getTopicList();
	}
}
