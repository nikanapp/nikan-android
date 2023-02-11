/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.Video;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 *   地图热点视频的描述窗口
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-3-26 上午11:01:13
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class DescriptionLayoutView extends LinearLayout{
	
	private static final String TAG = DescriptionLayoutView.class.getSimpleName();
	
	private ImageView preview;
	
	private DescriptionTextView description;
	
	private TextView lookNum ,likeNum ;
	
	private ImageLoader imageLoader  = ImageLoader.getInstance();
	
	private DisplayImageOptions options ;
	
	private Random random ;

	public DescriptionLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.view_description_layout, this);
		description = (DescriptionTextView) view.findViewById(R.id.description);
		preview = (ImageView) view.findViewById(R.id.preview);
		lookNum = (TextView) view.findViewById(R.id.looknum);
		likeNum = (TextView) view.findViewById(R.id.likenum);
		// 设置字体
		Typeface helvetica = UIHelper.getHelveticaTh(context);
		lookNum.setTypeface(helvetica);
		likeNum.setTypeface(helvetica);
		
		options = ImageLoaderUtils.getDescPreviewImageOption(context);

		random = new Random();
	}

	
	public void setContent(Video video){
		String color = "#"+UIHelper.ColorList.get(random.nextInt(UIHelper.ColorList.size()-1));
		preview.setBackgroundColor(Color.parseColor(color));
		preview.setImageBitmap(null);
		imageLoader.displayImage(video.getPreviewurl(), preview ,options);
		description.setText(video.getDescription());
		lookNum.setText(String.valueOf(video.getLooknum()));
		likeNum.setText(String.valueOf(video.getLikenum()));
	}
}
