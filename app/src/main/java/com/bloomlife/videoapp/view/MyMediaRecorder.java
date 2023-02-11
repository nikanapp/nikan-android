///**
// * 
// */
//package com.bloomlife.videoapp.view;
//
//import java.io.IOException;
//
//import android.hardware.Camera;
//import android.media.MediaRecorder;
//import android.util.Log;
//
//import com.yixia.camera.AudioRecorder;
//import com.yixia.camera.MediaRecorderBase;
//import com.yixia.camera.model.MediaObject.MediaPart;
//import com.yixia.videoeditor.adapter.UtilityAdapter;
//
///**
// * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
// *
// * @date 2015年1月21日 下午6:00:46
// */
//public class MyMediaRecorder extends MediaRecorderBase implements android.media.MediaRecorder.OnErrorListener {
//	
//	/** 系统MediaRecorder对象 */
//	private android.media.MediaRecorder mMediaRecorder;
//
//	public MyMediaRecorder() {
//
//	}
//
//	/** 开始录制 */
//	
//	public MediaPart startSystemRecord() {
//		if (mMediaObject != null && mSurfaceHolder != null && !mRecording) {
//			MediaPart result = mMediaObject.buildMediaPart(mCameraId, ".mp4");
//
//			try {
//				if (mMediaRecorder == null) {
//					mMediaRecorder = new MediaRecorder();
//					mMediaRecorder.setOnErrorListener(this);
//				} else {
//					mMediaRecorder.reset();
//				}
//
//				// Step 1: Unlock and set camera to MediaRecorder
//				camera.unlock();
//				mMediaRecorder.setCamera(camera);
//				mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
//
//				// Step 2: Set sources
//				mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//before setOutputFormat()
//				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//before setOutputFormat()
//
//				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//
//				//设置视频输出的格式和编码
////				CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
//				//                mMediaRecorder.setProfile(mProfile);
//				mMediaRecorder.setVideoSize(mVideoWidth, mVideoHeight);//after setVideoSource(),after setOutFormat()
//				mMediaRecorder.setAudioEncodingBitRate(44100);
////				if (mProfile.videoBitRate > 2 * 1024 * 1024)
////					mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
////				else
////					mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
//				mMediaRecorder.setVideoFrameRate(MAX_FRAME_RATE);//after setVideoSource(),after setOutFormat()
//				mMediaRecorder.setVideoEncodingBitRate(2000);
//
//				mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//after setOutputFormat()
//				mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//after setOutputFormat()
//
//				//mMediaRecorder.setVideoEncodingBitRate(800);
//
//				// Step 4: Set output file
//				mMediaRecorder.setOutputFile(result.mediaPath);
//
//				// Step 5: Set the preview output
//				//				mMediaRecorder.setOrientationHint(90);//加了HTC的手机会有问题
//
//				Log.e("Yixia", "OutputFile:" + result.mediaPath);
//
//				mMediaRecorder.prepare();
//				mMediaRecorder.start();
//				mRecording = true;
//				return result;
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//				Log.e("Yixia", "startRecord", e);
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.e("Yixia", "startRecord", e);
//			} catch (Exception e) {
//				e.printStackTrace();
//				Log.e("Yixia", "startRecord", e);
//			}
//		}
//		return null;
//	}
//
//	/** 停止录制 */
//	public void stopSystemRecord() {
//		long endTime = System.currentTimeMillis();
//		if (mMediaRecorder != null) {
//			//设置后不会崩
//			mMediaRecorder.setOnErrorListener(null);
//			mMediaRecorder.setPreviewDisplay(null);
//			try {
//				mMediaRecorder.stop();
//			} catch (IllegalStateException e) {
//				Log.w("Yixia", "stopRecord", e);
//			} catch (RuntimeException e) {
//				Log.w("Yixia", "stopRecord", e);
//			} catch (Exception e) {
//				Log.w("Yixia", "stopRecord", e);
//			}
//		}
//
//		if (camera != null) {
//			try {
//				camera.lock();
//			} catch (RuntimeException e) {
//				Log.e("Yixia", "stopRecord", e);
//			}
//		}
//
//		// 判断数据是否处理完，处理完了关闭输出流
//		if (mMediaObject != null) {
//			MediaPart part = mMediaObject.getCurrentPart();
//			if (part != null && part.recording) {
//				part.recording = false;
//				part.endTime = endTime;
//				part.duration = (int) (part.endTime - part.startTime);
//				part.cutStartTime = 0;
//				part.cutEndTime = part.duration;
//			}
//		}
//		mRecording = false;
//	}
//
//	/** 释放资源 */
//	@Override
//	public void release() {
//		super.release();
//		if (mMediaRecorder != null) {
//			mMediaRecorder.setOnErrorListener(null);
//			try {
//				mMediaRecorder.release();
//			} catch (IllegalStateException e) {
//				Log.w("Yixia", "stopRecord", e);
//			} catch (Exception e) {
//				Log.w("Yixia", "stopRecord", e);
//			}
//		}
//		mMediaRecorder = null;
//	}
//
//	@Override
//	public void onError(MediaRecorder mr, int what, int extra) {
//		try {
//			if (mr != null)
//				mr.reset();
//		} catch (IllegalStateException e) {
//			Log.w("Yixia", "stopRecord", e);
//		} catch (Exception e) {
//			Log.w("Yixia", "stopRecord", e);
//		}
//		if (mOnErrorListener != null)
//			mOnErrorListener.onVideoError(what, extra);
//	}
//	
//	
//	/** 视频后缀 */
//	private static final String VIDEO_SUFFIX = ".ts";
//	
//	/** 开始录制 */
//	
//	public MediaPart startNativeRecord() {
//		//防止没有初始化的情况
//		if (!UtilityAdapter.isInitialized()) {
//			UtilityAdapter.initFilterParser();
//		}
//		
//		MediaPart result = null;
//		
//		if (mMediaObject != null) {
//			mRecording = true;
//			result = mMediaObject.buildMediaPart(mCameraId, VIDEO_SUFFIX);
//			String cmd = String.format("filename = \"%s\"; ", result.mediaPath);
//			//如果需要定制非480x480的视频，可以启用以下代码，其他vf参数参考ffmpeg的文档：
//			int	transpose = mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 3;
//			cmd += String.format("addcmd = %s; ","-vf \"transpose="+transpose+", crop="+mVideoHeight+":"+mVideoWidth+":0:0\" ");
//			UtilityAdapter.FilterParserAction(cmd, UtilityAdapter.PARSERACTION_START);
//			if (mAudioRecorder == null && result != null) {
//				mAudioRecorder = new AudioRecorder(this);
//				mAudioRecorder.start();
//			}
//		}
//		return result;
//	}
//
//	/** 停止录制 */
//	
//	public void stopNativeRecord() {
//		UtilityAdapter.FilterParserAction("", UtilityAdapter.PARSERACTION_STOP);
//		super.stopRecord();
//	}
//
//	/** 数据回调 */
//	@Override
//	public void onPreviewFrame(byte[] data, Camera camera) {
//		if (mRecording) {
//			//底层实时处理视频，将视频旋转好，并剪切成480x480
//			UtilityAdapter.RenderDataYuv(data);
//		}
//		super.onPreviewFrame(data, camera);
//	}
//
//	/** 预览成功，设置视频输入输出参数 */
//	@Override
//	protected void onStartPreviewSuccess() {
//		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//			UtilityAdapter.RenderInputSettings(mVideoWidth, mVideoHeight, 0, UtilityAdapter.FLIPTYPE_NORMAL);
//		} else {
//			UtilityAdapter.RenderInputSettings(mVideoWidth, mVideoHeight, 180, UtilityAdapter.FLIPTYPE_HORIZONTAL);
//		}
//		UtilityAdapter.RenderOutputSettings(mVideoWidth, mVideoHeight, mFrameRate, UtilityAdapter.OUTPUTFORMAT_YUV | UtilityAdapter.OUTPUTFORMAT_MASK_MP4 /*| UtilityAdapter.OUTPUTFORMAT_MASK_HARDWARE_ACC*/);
//	}
//
//	/** 接收音频数据，传递到底层 */
//	@Override
//	public void receiveAudioData(byte[] sampleBuffer, int len) {
//		if (mRecording && len > 0) {
//			UtilityAdapter.RenderDataPcm(sampleBuffer);
//		}
//	}
//	
//	public static final int MODE_SYSTEM = 0;
//	public static final int MODE_NATIVE = 1;
//	
//	private int mMode;
//	
//	public void setRecordMode(int mode){
//		mMode = mode;
//	}
//	
//	@Override
//	public MediaPart startRecord() {
//		if (MODE_SYSTEM == mMode){
//			return startSystemRecord();
//		} else {
//			return startNativeRecord();
//		}
//	}
//	
//	@Override
//	public void stopRecord(){
//		if (MODE_SYSTEM == mMode){
//			stopSystemRecord();
//		} else {
//			stopNativeRecord();
//		}
//	}
//}
