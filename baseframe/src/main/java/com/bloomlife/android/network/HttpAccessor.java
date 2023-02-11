/**
 * 
 */
package com.bloomlife.android.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.bean.AnalyseMessage;
import com.bloomlife.android.bean.AnalyseRecord;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.MessageException;
import com.bloomlife.android.common.util.AnalyseUtils;
import com.bloomlife.android.common.util.MessageUtil;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.framework.AnalyseDatabase;
import com.signutil.SignUtils;
import com.signutil.StringHashMap;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-13  下午3:01:47
 */
public class HttpAccessor implements NetworkAccessor {
	protected SoftReference<Context> ctx ;
	
	protected int connectCount = 0;
	
	public static final String TAG = "HttpAccessor";
	
	
	public static final String TAG_HTTP_ACCESSOR = "HttpAccessor";

	public HttpAccessor(Context ctx) {
		this.ctx = new SoftReference<Context>(ctx);
	}

	@Override
	public String call(BaseMessage message) throws HttpException {

		String result = null;
	
			connectCount += 1;
			if (message.files.size() > 0) result =  doMultipartRequest(message);
			else
				try {
					result = doTextHttpRequest(message);
				} catch (Exception e) {
					if(e instanceof HttpException) throw (HttpException)e ;
					else throw new HttpException(ProcessResult.Failure+"",e);
				}

//		if(log.isDebugEnabled())log.debug(" receive msg : " + result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T call(BaseMessage message,Class<? extends ProcessResult> clz) throws HttpException {
		String result = call(message);
		Log.d(TAG, " recevie message : " + result);
		if(StringUtils.isEmpty(result)) return null;
		return (T) JSON.parseObject(result, clz);
	}

	/**
	 * 	执行纯文本的http业务请求
	 * 
	 * @param message
	 * @throws MalformedURLException
	 * @throws MessageException 
	 * @throws HttpException 
	 */
	private String doTextHttpRequest(BaseMessage message) throws Exception {
		URL url = new URL(HttpProtocolEntry.URL);
		// 如果是短连接的接口，调用特定的url
//		String content = JSON.toJSONString(message);
		String content = message.getPostParamsStr();
		Log.d(TAG ,"发送的请求内容为："+content);
		
		if(!AnalyseUtils.isEnable(ctx.get())){
			return NetUtils.getPostInputStream(ctx, url, message.getMsgCode(), content);
		}
		
		return requestWithAnalyse(message, url, content);
		 
	}

	/**
	 * 	执行带有统计分析记录的请求
	 * 
	 * @param message
	 * @param url
	 * @param content
	 * @return
	 * @throws Exception
	 */
	private String requestWithAnalyse(BaseMessage message, URL url, String content) throws Exception {
		//发送请求并且记录请求数据 。后台的统计开关接口打开了，才进行统计
		AnalyseRecord record = new AnalyseRecord(message.getMsgCode(),ctx.get());
		long start = System.currentTimeMillis();
		try {
			return  NetUtils.getPostInputStream(ctx, url, content, "utf-8");
		} catch (Exception e) {
			if(e instanceof HttpException){
				HttpException he = (HttpException) e;
				if(he.getMessage().startsWith("resCode")){
					String[] codes= he.getMessage().split(":");
					record.setResultCode(codes[1]);
				}
			}
			//请求异常
			if(StringUtils.isEmpty(record.getResultCode()))record.setResultCode(AnalyseRecord.HTTP_ERROR);
			throw e;
		}finally{
			if(! AnalyseMessage.MsgCode.equals(record.getApiName())){  //统计请求本身不需要保存
				record.setReqTimeLength((int) (System.currentTimeMillis()-start));
				if(StringUtils.isEmpty(record.getResultCode()))record.setResultCode(HttpURLConnection.HTTP_OK+"");
				AnalyseDatabase.saveAnalyse(ctx.get(), record);
			}
		}
	}
	
	private static  String getRandomKey(){
		return (new Date()).getTime()+"";
	}
	
	/**
	 * 执行带有文本域的http多媒体请求
	 * @param messag
	 * @return
	 * @throws HttpException 
	 */
	private String doMultipartRequest(BaseMessage message) throws HttpException {
		Log.d(TAG, "请求中带有文件，进行文件上传请求");
		String rs = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(HttpProtocolEntry.URL);
		MultipartEntity reqEntity = new MultipartEntity();
		try {
			Map<String,String> params = MessageUtil.converToMap(message);
			StringHashMap sign  = MessageUtil.converToMapWithoutParent(message);
			sign.put("devicetype", message.getDevicetype()==null?"":message.getDevicetype());
			params.put("sign", SignUtils.getSign(message, sign));
			//文本请求参数
			if (params != null) {
				for (HashMap.Entry<String, String> entry : params.entrySet()) {
					if(entry!=null && entry.getValue()!=null){
						reqEntity.addPart(entry.getKey(),new StringBody(entry.getValue(), Charset.forName("UTF-8")));
//						if(log.isDebugEnabled()) log.debug(" add  http  text  request parameter  ：  [ "+entry.getKey()+" = "+entry.getValue()+" ]" ); 
					}
				}
			}
			//文件域请求参数
			setFilePart(message, reqEntity);
			
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10*1000); 
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30*1000); 

			//发送请求和获取结果
			httpPost.setEntity(reqEntity);
			httpPost.setHeader("charset", HTTP.UTF_8);
			//httpPost.setHeader("Accept-Encoding", "gzip, deflate");
			HttpResponse resp = client.execute(httpPost);
			InputStream is = resp.getEntity().getContent();

			Header contentEncoding = resp.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& StringUtils.isNotBlank(contentEncoding.getValue())
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				is = new GZIPInputStream(new BufferedInputStream(is));
			}
			rs = StringUtils.convertStreamToString(is, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			throw new HttpException(ProcessResult.ILLEAGE_PARAMETER+"",e);
		} finally {
			try{
				if (client != null && client.getConnectionManager() != null) {
					client.getConnectionManager().closeIdleConnections(0,TimeUnit.SECONDS);
				}
			}catch(Exception e){}
		}
		return rs;
	}

	/**
	 * @param message
	 * @param reqEntity
	 * @throws FileNotFoundException
	 */
	private void setFilePart(BaseMessage message, MultipartEntity reqEntity)
			throws FileNotFoundException {
		Map<String, File> files = new HashMap<String, File>();
		for (String key : message.files.keySet()) {
			if(message.files.get(key)!=null){
				String path = message.files.get(key);
				Log.d(TAG, "需要上传的文件域为：[key="+key+"]"+" [path="+path+"]");
				File file = new File(path);
				files.put(key, file);
			}
		}
		if (files != null) {
			for (HashMap.Entry<String, File> file : files.entrySet()) {
				String fname = file.getValue().getName();
				if(!fname.endsWith(".raw") && !fname.endsWith(".wav")){
					//ImageUtils.zipBitmap(file.getValue());
				}
				Log.d(TAG," 需要上传的文件大小为："+file.getValue().length()/1024+ "kb , "+file.getValue().getTotalSpace());
				reqEntity.addPart(file.getKey(), new InputStreamBody(new FileInputStream(file.getValue()), file.getValue().getName()));
			}
		}
	}


}
