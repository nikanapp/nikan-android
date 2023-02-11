package com.bloomlife.android.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * 
 * @author zhang
 *
 */
public class HttpUtil {
	
	public static String doGet(String req){
		String rs = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(req); 
			HttpResponse response = client.execute(httpGet);
			rs = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static String doPost(String req,HashMap<String,String> params){
		String resp = "";
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost postMethod = new HttpPost(req);
			if(params!=null){
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				Set key = params.keySet();
				Iterator iter = key.iterator();
				while(iter.hasNext()){
					String obj = (String)iter.next();
					paramList.add(new BasicNameValuePair(obj, params.get(obj).toString()));
				}
				postMethod.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
				HttpResponse response = client.execute(postMethod);
				resp = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resp;
	}
	
}
