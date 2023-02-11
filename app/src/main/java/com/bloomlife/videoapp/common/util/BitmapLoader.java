package com.bloomlife.videoapp.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LongSparseArray;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import com.bloomlife.videoapp.app.AppContext;

/**
 * Created by cjf on 2015/3/24.
 */
public class BitmapLoader {

    private LruCache<Long, Bitmap> mLruCache;
    private ContentResolver mResolver;
    private BitmapFactory.Options mOptions;
    private LongSparseArray<ImageView> mImageViewArray;
    private BitmapTaskHandler mHandler;

    public BitmapLoader(Context context){
        mLruCache = new LruCache<Long, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8));
        mResolver = context.getContentResolver();
        mOptions  = new BitmapFactory.Options();
        mOptions.inDither = false;
        mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mImageViewArray = new LongSparseArray<ImageView>();
        mHandler = new BitmapTaskHandler(this);
    }

    public void load(ImageView imageView, long id){
        Bitmap bitmap = mLruCache.get(id);
        if (bitmap == null){
            mImageViewArray.put(id, imageView);
            AppContext.EXECUTOR_SERVICE.execute(new Task(id, mOptions, mResolver));
        }
    }

    class Task implements Runnable{

        private ContentResolver mResolver;
        private BitmapFactory.Options mOptions;
        private long mId;

        public Task(long id, BitmapFactory.Options options, ContentResolver resolver){
            mId = id;
            mOptions = options;
            mResolver = resolver;
        }

        @Override
        public void run() {
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(mResolver, mId, MediaStore.Video.Thumbnails.MINI_KIND, mOptions);
            Message message = new Message();
            message.obj = new CallBack(mId, bitmap);
            mHandler.sendMessage(message);
        }
    }

    static class BitmapTaskHandler extends Handler {
    	
    	private WeakReference<BitmapLoader> mReference;
    	
    	public BitmapTaskHandler(BitmapLoader loader){
    		mReference = new WeakReference<BitmapLoader>(loader);
    	}

        @Override
        public void handleMessage(Message msg) {
        	BitmapLoader loader = mReference.get();
        	if (loader == null) return;
            CallBack c = (CallBack) msg.obj;
            ImageView iv = loader.mImageViewArray.get(c.id);
            iv.setImageBitmap(c.bitmap);
            super.handleMessage(msg);
        }
    }

    class CallBack {
        private long id;
        private Bitmap bitmap;

        public CallBack(long id, Bitmap bitmap){
            this.id = id;
            this.bitmap = bitmap;
        }
    }

}
