/**
 * 
 */
package com.bloomlife.android.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.bean.AnalyseMessage;
import com.bloomlife.android.bean.AnalyseRecord;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.business.BusinessProcessor;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.framework.AnalyseDatabase;
import com.bloomlife.android.framework.AppContext;

/**
 * 团队的统计分析工具类。这个和友盟之类的不同，是团队为了进行应用性能收集和优化而做的。
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2015-6-15 下午2:40:58
 * 
 * @organization bloomlife
 * @version 1.0
 */
public class AnalyseUtils {

	private static final String TAG = AnalyseUtils.class.getSimpleName();

	/**
	 * 上传统计数据
	 * 
	 * @param context
	 */
	public static void uploadAnalyseData(final Context context) {
		if(! isEnable(context)) return ;
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				try {
					List<AnalyseRecord> result = AnalyseDatabase
							.findAnalyses(context.getApplicationContext());
					if (result != null && !result.isEmpty()) {
						String jsonStr = JSON.toJSONString(result);
						Log.d(TAG, " json length = " + jsonStr.length() + " content = " + jsonStr);
						
						String compressStr = compress(jsonStr);
						Log.d(TAG, " compress length = " + compressStr.length() + " content = " + compressStr);
						AnalyseMessage message = new AnalyseMessage(compress(jsonStr));
						BusinessProcessor businessProcessor = new BusinessProcessor( context);
						ProcessResult processResult = businessProcessor .doBusinessRequest(message, ProcessResult.class);
						if (ProcessResult.SUC == processResult.getCode()) {
							AnalyseDatabase.deleteUpload(context);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 这个开关控制是否需要进行统计分析，并且上传的。
	 * @param context
	 * @return
	 */
	public static boolean isEnable(Context context){
		return  "on".equals(CacheBean.getInstance().getString(context, CacheKeyConstants.Analyse_Switch, "off"));
	}

	// /**
	// *
	// * 使用gzip进行压缩
	// */
	// public static String gzip(String primStr) {
	// if (primStr == null || primStr.length() == 0) {
	// return primStr;
	// }
	//
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	//
	// GZIPOutputStream gzip = null;
	// try {
	// gzip = new GZIPOutputStream(out);
	// gzip.write(primStr.getBytes());
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// if (gzip != null) {
	// try {
	// gzip.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return new sun.misc.BASE64Encoder().encode(out.toByteArray());
	// }
	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

}
