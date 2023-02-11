package com.bloomlife.android.log;

import android.util.Log;

import com.bloomlife.android.BuildConfig;
import com.bloomlife.android.common.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cjf on 2015/7/1.
 * 日志管理者，必须调用init方法后日志才可使用。
 */
public class LoggerManager {

    private static final String LOG_DATE_FORMAT = "yy/MM/dd HH:mm";

    private static final String LOG_FILE_NAME_FORMAT = "yy-MM-dd'.html'";

    private static final String LOG_DIRE_NAME = "mylog";

    private static final int MAX_LOG_FILE_SIZE = 2;

    private static volatile LoggerManager mLoggerManager;

    private File mLogFile;

    private LoggerPrinter mPrinter;

    private SimpleDateFormat mFormat;

    private Setting mSetting;

    private void LoggerManager(){

    }

    public static LoggerManager getInstance(){
        if (mLoggerManager == null){
            synchronized (LoggerManager.class){
                if (mLoggerManager == null)
                    mLoggerManager = new LoggerManager();
            }
        }
        return mLoggerManager;
    }

    public void init(String filePath, Setting setting){
        File logFilePath = new File(filePath+"/"+LOG_DIRE_NAME);
        if (logFilePath.exists()){
            deleteOverShootLogFile(logFilePath);
        } else {
            logFilePath.mkdir();
        }
        String fileName = new SimpleDateFormat(LOG_FILE_NAME_FORMAT, Locale.getDefault()).format(new Date(System.currentTimeMillis()));
        mLogFile = new File(logFilePath, fileName);
        mPrinter = new LoggerPrinter(mLogFile, setting);
        mFormat = new SimpleDateFormat(LOG_DATE_FORMAT, Locale.getDefault());
        mSetting = setting;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    /**
     * 删除超出规定个数的日志文件
     * @param logFilePath 日志文件文件夹路径
     */
    public static void deleteOverShootLogFile(File logFilePath){
        File[] logFiles = logFilePath.listFiles();
        Arrays.sort(logFiles, new FileComparator());
        for (int i=0; i<logFiles.length-MAX_LOG_FILE_SIZE; i++){
            logFiles[i].delete();
        }
    }

    public LoggerPrinter getPrinter(){
        return mPrinter;
    }

    public SimpleDateFormat getFormat() {
        return mFormat;
    }

    public static class DefaultSettingBuilder extends Setting.SettingBuilder {
        public DefaultSettingBuilder(){
            setAppend(true);
            setMaxFileSize(1 * 1024 * 1024);
            setOutputLevel(Setting.VERBOSE);
            setOutputDate(true);
            setEnableLogBuffer(true);
            setDebug(BuildConfig.DEBUG);
        }
    }

    static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs == null && rhs == null) return 0;
            if (lhs != null && rhs == null) return 1;
            if (lhs == null && rhs != null) return -1;
            if (lhs.lastModified() > rhs.lastModified()) return 1;
            if (lhs.lastModified() < rhs.lastModified()) return -1;
            return 0;
        }
    }

    public Setting getSetting(){
        return mSetting;
    }

    public void uploadLog(){
        if (mLogFile != null && mLogFile.exists()){

        }
    }

    /**
     * 异常处理者，当遇到崩溃时把在缓冲区的日志写入到本地磁盘中，防止日志丢失
     */
    private class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

        private ExceptionHandler(){
            Logger.v("LoggerManager", "init ExceptionHandler");
            mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Logger.e("LoggerManager", "App Crash");
            mPrinter.flush();
            if (mDefaultExceptionHandler != null){
                mDefaultExceptionHandler.uncaughtException(thread, ex);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

}
