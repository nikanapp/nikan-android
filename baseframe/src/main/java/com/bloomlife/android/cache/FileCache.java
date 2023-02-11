package com.bloomlife.android.cache;

import java.io.File;

import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    private FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"gaoshou/cache");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    private static FileCache fileCache;
    public synchronized static FileCache getInstance(Context ctx){
    	if(fileCache==null) fileCache = new FileCache(ctx);
    	return fileCache;
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public File getFile(String url,String filename){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public String getFileLocalPath(String netPath){
    	String filename=String.valueOf(netPath.hashCode());
    	String path = cacheDir.getAbsolutePath();
    	return path+"/"+filename;
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}