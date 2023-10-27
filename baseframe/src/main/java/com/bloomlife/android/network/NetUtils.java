package com.bloomlife.android.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.Base64;
import com.bloomlife.android.common.util.StringUtils;

public class NetUtils {

	private static final String TAG = NetUtils.class.getSimpleName();

	/**
	 * 无网�?
	 */
	public final static int NONE = -1;
	/**
	 * wifi网络
	 */
	public final static int WIFI = 1111;
	/**
	 * gprs网络
	 */
	public final static int MOBILE = 2;

	public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

	private static void setProxy(Context ctx) {
		try {
			Cursor c = ctx.getContentResolver().query(PREFERRED_APN_URI, null,
					null, null, null);
			String proxy = "";
			if(c!=null && c.getCount()>0){
				c.moveToFirst();
				proxy = c.getString(c.getColumnIndex("proxy"));
			}
			if (!"".equals(proxy) && proxy != null) {
				Properties prop = System.getProperties();
				System.getProperties().put("proxySet", "true");
				prop.setProperty("http.proxyHost",
						c.getString(c.getColumnIndex("proxy")));
				prop.setProperty("http.proxyPort",
						c.getString(c.getColumnIndex("port")));
			}
			if(c!=null){
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 设置连接代理
	 * @param context
	 * @param uc
	 */
	private static void setConnectProxy(Context context, HttpURLConnection uc) {
		if(context==null) return ;
		Cursor c = null;
		try {
			c = context.getContentResolver().query(PREFERRED_APN_URI,null, null, null, null);
			String proxy = "";
			if (c.getCount() > 0) {
				c.moveToFirst();
				proxy = c.getString(c.getColumnIndex("proxy"));
			}
			if (!"".equals(proxy) && proxy != null) {
				Properties prop = System.getProperties();
				System.getProperties().put("proxySet", "true");
				prop.setProperty("http.proxyHost",
						c.getString(c.getColumnIndex("proxy")));
				prop.setProperty("http.proxyPort",
						c.getString(c.getColumnIndex("port")));
				String authentication = c.getString(c.getColumnIndex("user"))
						+ ":" + c.getString(c.getColumnIndex("password"));
				String encodedLogin = Base64.encode(authentication);
				uc.setRequestProperty("Proxy-Authorization", " Basic "
						+ encodedLogin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(c!=null){
				c.close();
			}
		}
	}
	/**
	 * 获取当前上网方式是否�?��设置代理 如果不是WIFI方式连网则需要设置代�?
	 * 旧版本wap网络�?��
	 * @param context
	 * @return
	 */
	private static boolean isProxy(Context context) {
		if(context==null) return false ;
		boolean isProxy = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null) {
				if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
					isProxy = true;
				}
			}
		}
		return isProxy;
	}

	/**
	 * 获取网络状�?
	 * @param context
	 * @return 0不可�?1wifi网络,2gprs网络
	 */
	public static int getNetState(Context context){
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo=connManager.getActiveNetworkInfo();
		if(networkInfo==null) return NONE;

		if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
			return WIFI;
		}else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
			return networkInfo.getSubtype();
		}
		return NONE;
	}

	/**
	 * 通过HttpURLConnection获取网络数据
	 *
	 * @param url
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static InputStream getInputStream(Context ctx, URL url)
			throws Exception {
		InputStream result = null;
		if (url != null) {
			HttpURLConnection conn = null;
			try {
				if (isProxy(ctx)) {
					// 获取默认代理主机ip
					String host = android.net.Proxy.getDefaultHost();
					// 获取端口
					int port = android.net.Proxy.getDefaultPort();
					if (host != null && port != -1) {
						// 封装代理连接主机IP与端口号
						InetSocketAddress inetAddress = new InetSocketAddress(
								host, port);
						// 根据URL链接获取代理类型，本链接适用于TYPE.HTTP
						java.net.Proxy.Type proxyType = java.net.Proxy.Type
								.valueOf(url.getProtocol().toUpperCase(Locale.getDefault()));
						java.net.Proxy javaProxy = new java.net.Proxy(
								proxyType, inetAddress);
						conn = (HttpURLConnection) url.openConnection(javaProxy);
					} else {
						conn = (HttpURLConnection) url.openConnection();
					}
				} else {
					conn = (HttpURLConnection) url.openConnection();
				}
				if (conn != null) {
					conn.setConnectTimeout(CONNECT_TIMEOUT);
					conn.setReadTimeout(CONNECT_TIMEOUT * 2);
					conn.connect();
					result = conn.getInputStream();
				}
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
		return result;
	}

	public final static int CONNECT_TIMEOUT=20*1000;
	public final static int READ_TIMEOUT=30*1000;


	public static String getPostInputStream(SoftReference<Context> ctx, String msgCode,URL url,  String param) throws HttpException {

		return null ;


	}


	/**
	 * 	 * 通过HttpURLConnection获取网络返回的字符串
	 * @param ctx
	 * @param url
	 * @param msgCode
	 * @param param
	 * @param encode
	 * @return 返回json格式数据
	 * @throws HttpException
	 */
	public static String getPostInputStream(SoftReference<Context> ctx, URL url,String msgCode , String param) throws HttpException {
		String result = "";
		InputStream in = null;
		HttpURLConnection conn = null;
		final 	String encode =  "utf-8";
		final String analyseUrl = HttpProtocolEntry.URL+"/"+msgCode;
		long start = System.currentTimeMillis();
		try {
			if (url != null) {
				conn = (HttpURLConnection) url.openConnection();
				if(ctx!=null && ctx.get()!=null){
					if (isProxy(ctx.get())) {
						setConnectProxy(ctx.get(), conn);
					}
				}
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				conn.setReadTimeout(READ_TIMEOUT);
				conn.setDoOutput(true);
				conn.setDoInput(true);
				// 设置请求方式，默认为GET
				conn.setRequestMethod("POST");
				// Post 请求不能使用缓存
				conn.setUseCaches(false);
				conn.setInstanceFollowRedirects(true);
				// 维持长连�?
				conn.setRequestProperty("Connection", "Keep-Alive");
				//conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				// 设置浏览器编�?
				conn.setRequestProperty("Charset", "UTF-8");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.connect();
				OutputStream out = conn.getOutputStream();
				// 正文内容其实跟get的URL ?后的参数字符串一�?
				out.write(param.getBytes());
				out.flush();
				out.close();
				final int statusCode = conn.getResponseCode();
				if(statusCode!= HttpURLConnection.HTTP_OK){
					sendBlueWareHttpAnalyse(analyseUrl, statusCode, start, param.getBytes().length, 0, null);
					throw new HttpException("resCode:"+conn.getResponseCode());
				}

				String content_encode = conn.getContentEncoding();
				if(StringUtils.isNotBlank(content_encode) && content_encode.equalsIgnoreCase("gzip")){
					result = StringUtils.convertStreamToString(new GZIPInputStream(conn.getInputStream()), encode);
				}else{
					result = StringUtils.convertStreamToString(conn.getInputStream(), encode);
				}
				sendBlueWareHttpAnalyse(analyseUrl, statusCode, start, param.getBytes().length, result.getBytes().length, null);
			}
		}catch(Exception e){
			e.printStackTrace();
			if(e instanceof HttpException) throw (HttpException)e;
			else{
				sendBlueWareHttpAnalyse(analyseUrl, null, start, 0, 0, e);
				throw new HttpException(ProcessResult.Failure+"",e);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}


	private static void sendBlueWareHttpAnalyse(String url ,Integer statusCode,  long start , long bytesSend ,long byteRec ,Exception e){
	}

	/**
	 * �?��网络是否可用
	 * @param ctx
	 */
	public static boolean checkNet(final Context ctx) {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) { // 当前网络不可�?
			new AlertDialog.Builder(ctx)
					.setMessage("网络连接不可用")
					.setPositiveButton("前往设置",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									ComponentName cn = new ComponentName(
											"com.android.settings",
											"com.android.settings.Settings");
									Intent intent = new Intent();
									intent.setComponent(cn);
									intent.setAction("android.intent.action.VIEW");
									ctx.startActivity(intent);
								}
							}).setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			return false ;
		}
		return true ;
	}
}