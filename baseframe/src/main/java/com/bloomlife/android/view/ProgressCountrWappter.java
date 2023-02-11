/**
 * 
 */
package com.bloomlife.android.view;

import java.util.concurrent.ExecutorService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bloomlife.android.common.util.StringUtils;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-8-8  下午12:43:10
 */
public class ProgressCountrWappter {

	public static final String TAG = "ProgressCountrWappter";
	
	private ProgressBar progressBar;
	
	private Processer processer ;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressBar.incrementProgressBy(1);
		}
		
	};
	
	public ProgressCountrWappter(ProgressBar progressBar ){
		this.progressBar = progressBar;
	}
	
	public void process(String  times , ExecutorService executorService){
		if(StringUtils.isEmpty(times)) return ;
		if(processer!=null)processer.controller = false ;
		try {
			processer =new Processer(Integer.parseInt(times));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ;
		}
		
		progressBar.setProgress(0);
		progressBar.setMax(processer.getCount());
		
		progressBar.setVisibility(View.VISIBLE);
		executorService.execute(processer);
	}
	
	public void reset(){
		progressBar.setVisibility(View.INVISIBLE);
		if(processer!=null)	processer.controller=false ;
	}
	
	private class Processer implements Runnable{

		private boolean controller = true ;
		
		private int count ;
		
		private static final int INTERVAL = 200 ;
		
		public Processer(int count){
			this.count = count*(1000/INTERVAL) ;
			Log.d(TAG, " audiolength ："+count+"   and  count  ："+this.count);
		}
		
		@Override
		public void run() {
			while (controller&&count >0) {
				count--;
				Log.d(TAG, " count  ："+this.count);
				handler.sendEmptyMessage(0);
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		public int getCount(){
			return count;
		}
	}
}
