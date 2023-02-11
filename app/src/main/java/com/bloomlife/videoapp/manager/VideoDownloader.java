/**
 * 
 */
package com.bloomlife.videoapp.manager;

/**
 * 	视频下载器
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-12-10 下午4:34:56
 */
//public abstract class VideoDownloader<T> extends AsyncTask<Void, Integer, String> {
//
//	private static final String TAG = "VideoDownloadManager";
//
//	private String videoUrl ;
//
//	private File file ;
//
//	protected String videoId;
//
//	protected WeakReference<T> videoFgtRf;
//
//	public VideoDownloader(Context context, String videoUrl, T obj){
//		this.videoUrl = videoUrl;
//		file = getLocalCache(context,videoUrl);
//		this.videoFgtRf = new WeakReference<>(obj);
//	}
//
//	/**
//	 * 如果不支持外部文件读取，会返回空。
//	 * @param context
//	 * @param videoUri
//	 * @return	返回的file不为空，通过判断文件是否存在决定有没有缓存
//	 */
//	public static File getLocalCache(Context context , String videoUri){
//		int hashCode = videoUri.hashCode();
//		File dirFile = CacheUtils.getCacheFileDirectory(context, Constants.CACHE_FOLDER_NAME);
//		if(dirFile==null){
//			Log.e(TAG, "不支持外部文件读取");
//			return null ;
//		}
//		File file = new File(dirFile,hashCode+".mp4");
//		return file ;
//	}
//
//
//	@Override
//	protected void onPreExecute() {
//		if(videoUrl==null){
//			Log.e(TAG, " 取消执行下载任务，url为空");
//			cancel(true);
//		}
//		super.onPreExecute();
//	}
//
//	@Override
//	protected String doInBackground(Void... ctx) {
//		if(file==null) return null;
//		try {
//			URL url = new URL(videoUrl);
//			URLConnection connection = url.openConnection();
//			connection.connect();
//
//			// Detect the file lenght
//			int fileLength = connection.getContentLength();
//			Log.d(TAG, " fileLength : " +fileLength);
//
//			// Download the file
//			InputStream input = new BufferedInputStream(url.openStream());
//
////			File file = new File(filePath);
//			if(!file.exists())file.createNewFile();
//			// Save the downloaded file
//			OutputStream output = new FileOutputStream(file);
//
//			byte data[] = new byte[1024];  //最大8M
//			long total = 0;
//			int count;
//			while ((count = input.read(data)) != -1) {
//				total += count;
//				// Publish the progress
//				publishProgress((int) (total * 100 / fileLength));
//				output.write(data, 0, count);
//			}
//
//			// Close connection
//			output.flush();
//			output.close();
//			input.close();
//			Log.d(TAG, " 视频 ： " +videoUrl+" 下载完成");
//			return file.getAbsolutePath();
//		} catch (Exception e) {
//			Log.e("Error", "下载视频出错",e);
//			if(file!=null){
//				if(file.delete()) Log.e(TAG, " 视频下载失败，删除失败文件成功");
//				else Log.e(TAG, " 视频下载失败，删除失败文件失败");
//			}
//		}
//		return null;
//	}
//
//	@Override
//	protected final void onPostExecute(String result) {
//		super.onPostExecute(result);
//		if(videoFgtRf.get()==null){
//			Log.w(TAG, " fragment 已经被回收， 不用播放视频 ");
//			return;
//		}
//		onFinishDownload(result);
//	}
//
//	protected void onFinishDownload(String result){
//
//	}
//}
