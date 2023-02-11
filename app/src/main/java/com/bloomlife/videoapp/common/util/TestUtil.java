package com.bloomlife.videoapp.common.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.Commenttags;
import com.bloomlife.videoapp.model.More;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.GetVideoListMessage;
import com.bloomlife.videoapp.model.result.GetVideoResult;

public class TestUtil {
	private static Random mRandom = new Random();
	public static final String[] COMMENTS = new String[]{
			"点32个赞",
			"真的假的", 
			"不明觉厉", 
			"口水一地", 
			"帅到没朋友", 
			"内牛满面", 
			"节操碎一地", 
			"震惊了！",
			"萌蠢贱",
			"我湿了",
			"膝盖跪到碎",
			"路过",
			"怒！",
			"我爱你！",
			"我勒个去",
			"滚粗",
			"拽P啊",
			"好忧桑"
	};
	
	public static final String[] COLORS = new String[]{
		"ce9d40",
		"ed9d60",
		"bb9e76"
	};
	
	public static List<Video> createTestVideoList(GetVideoListMessage message, int length){
		List<Video> list = new ArrayList<Video>();
		for (int i = 0; i < length; i++) {
			int randomLat = randomLat(message.getLowerleftlat(), message.getUpperrightlat());
			int randomLon = randomLon(message.getLowerleftlon(), message.getUpperrightlon());
			list.add(createTestVideo(randomLat, randomLon, i));
		}
		return list;
	}
	
	public static Video createTestVideo(int lat, int lon, int i){
		Video video = new Video();
		video.setLat(LocationUtil.intToDouble(lat)+"");
		video.setLon(LocationUtil.intToDouble(lon)+"");
		video.setDescription("测试视频"+i);
		return video;
	}
	
	public static List<CommentText> createTestCommentTexts(int length){
		List<CommentText> list = new ArrayList<CommentText>();
		for (int i = 0; i < length; i++){
			CommentText text = new CommentText();
			text.setContent("前段时间前段时间前段时间，前段时间前段时间。");
			if (i % 2 == 0)
				text.setUserid(CacheBean.getInstance().getLoginUserId());
			list.add(text);
		}
		return list;
	}
	
	public static List<More> createTestMoreList(GetVideoListMessage message, int length){
		List<More> list = new ArrayList<More>();
		for (int i = 0; i < length; i++) {
//			int randomLat = randomLat(message.getMinlat(), message.getMaxlat());
//			int randomLon = randomLon(message.getMinlon(), message.getMaxlon());
//			list.add(createTestMore(randomLat, randomLon));
		}
		return list;
	}
	
	public static More createTestMore(int lat, int lon){
		More more = new More();
		more.setLat(lat);
		more.setLon(lon);
		return more;
	}
	
	public static int randomLat(double minLat, double maxLat){
		int intminlat = LocationUtil.doubleToInt(minLat);
		int intmaxLat = LocationUtil.doubleToInt(maxLat);
		return intminlat + new Random().nextInt(Math.abs(intmaxLat - intminlat));
	}
	
	public static int randomLon(double minLon, double maxLon){
		int intminLon = LocationUtil.doubleToInt(minLon);
		int intmaxLon = LocationUtil.doubleToInt(maxLon);
		return intminLon + new Random().nextInt(Math.abs(intmaxLon - intminLon));
	}
	
	public static List<GetVideoResult> createVideoList(int num){
		List<GetVideoResult> list = new ArrayList<GetVideoResult>();
		for (int i = 0; i < num; i++) {
			list.add(new GetVideoResult());
		}
		return list;
	}
	
	public static void writeFile(String str){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CameraDemoVideo/upload.txt");
			try {
				fos.write(str.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<String> createTestTopicList(int size){
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			list.add("#话题"+i+" ");
		}
		return list;
	}
	
	public static ArrayList<Commenttags> createTestCommentList(){
		ArrayList<Commenttags> list = new ArrayList<Commenttags>();
		for (String str:COMMENTS){
			Commenttags comment = new Commenttags();
			comment.setContent(str);
			comment.setColor(COLORS[mRandom.nextInt(COLORS.length)%COLORS.length]);
			list.add(comment);
		}
		return list;
	}
	
	public static List<Comment> createTestCommentList(int size){
		List<Comment> list = new LinkedList<Comment>();
		for (int i = 0; i < size; i++) {
			Comment comment = getRandomCommnet(i);
			list.add(comment);
		}
		return list;
	}
	
	private static Comment getRandomCommnet(int i){
		Comment comment = new Comment();
		comment.setContent(COMMENTS[i%COMMENTS.length]);
		comment.setCount(String.valueOf(mRandom.nextInt(100)));
		comment.setColor(COLORS[mRandom.nextInt(COLORS.length)%COLORS.length]);
		return comment;
	}
	
	public static void TestLoaction(Activity activity){
		LocationManager manager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(true);
		criteria.setCostAllowed(true);//允许产生开销
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//消耗大的话，获取的频率高
        criteria.setSpeedRequired(true);//手机位置移动
        criteria.setAltitudeRequired(true);//海拔
		String bestProvider = manager.getBestProvider(criteria, true);
		manager.requestLocationUpdates(bestProvider, 60000, 100, new MyLocationListener(activity));
	}
	
	static class MyLocationListener implements LocationListener{

		public static final String TAG = MyLocationListener.class.getSimpleName();
		
		private Activity mActivity;
		
		public MyLocationListener(Activity activity){
			mActivity = activity;
		}
		
		@Override
		public void onLocationChanged(Location location) {
			String message = "location Lat "+location.getLatitude()+" Lon "+location.getLongitude();
			Toast.makeText(mActivity, message , Toast.LENGTH_LONG).show();
			Log.e(TAG, TAG + message);
			TestUtil.writeFile(message);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
