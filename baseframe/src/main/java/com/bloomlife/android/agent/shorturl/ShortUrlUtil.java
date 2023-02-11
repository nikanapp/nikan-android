package com.bloomlife.android.agent.shorturl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSON;

import android.os.AsyncTask;


public class ShortUrlUtil extends AsyncTask<Object, Void, String> {
	
	private Object userInfo;
	private String urlsource;
	private String urlstring;
	private String result = "";
	private static final String apiurl = "https://api.weibo.com/2/short_url/shorten.json?source=2554198391&url_long=";
	private ShortUrlInterface shortUrlInterface;
	public static String charSet = HTTP.UTF_8;
	
	public Object getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Object userInfo) {
		this.userInfo = userInfo;
	}
	
	public String getUrlsource() {
		return urlsource;
	}

	public void setUrlsource(String urlsource) {
		this.urlsource = urlsource;
	}

	public ShortUrlInterface getShortUrlInterface() {
		return shortUrlInterface;
	}

	public void setShortUrlInterface(ShortUrlInterface shortUrlInterface) {
		this.shortUrlInterface = shortUrlInterface;
	}

	public ShortUrlUtil(String url) {
		try {
			this.urlstring = apiurl + URLEncoder.encode(url, charSet);
			this.urlsource = url;
			System.out.println("share url " + this.urlstring);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		
		BufferedReader in = null;
		
		try {
			
			URL realUrl = new URL(this.urlstring);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.connect();
			
			in = new BufferedReader( new InputStreamReader(conn.getInputStream()));
			String line;
			
			while ( (line = in.readLine()) != null ) {
				this.result += line;
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.result;
	}
	
	@Override
	protected void onPostExecute(final String result) {
		
		//{"urls":[{"object_type":"","result":true,"url_short":"http://t.cn/h5mwx","object_id":"","url_long":"http://www.baidu.com","type":0}]}
		
		System.out.println("short url result json:" + result);
		try{
			ResultEntity resultEntity = JSON.parseObject(result, ResultEntity.class);
			if ( resultEntity.getUrls().size() > 0 ) {
				UrlObject urlobj = resultEntity.getUrls().get(0);
				this.shortUrlInterface.handleResult(this, urlobj);
			} else {
				this.shortUrlInterface.handleResult(this, null);
			}
		}catch(Exception e){
			e.printStackTrace();
			this.shortUrlInterface.handleResult(this, null);
		}
		super.onPostExecute(result);
	}
	
}
