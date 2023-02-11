package com.bloomlife.android.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by cjf on 2015/7/1.
 * 日志文件写入磁盘的类
 */
public class LoggerPrinter {

    private static final String HTML_HEAD = "<html><body>";
    private static final String HTML_FOOT = "</body></html>";
    private static final String CHARSET = "UTF-8";

    private static final int QUEUE_SIZE = 20;
    private static final int BUFFER_LENGTH = 8 * 1024;
    private static final int POLL_TIME_OUT = 3000;
    private static final int MAX_TIME_OUT_COUNT = 5;
    private final char[] mBuffer = new char[BUFFER_LENGTH];
    private int mBufferCount;

    private ArrayBlockingQueue<String> mQueue;
    private Executor mExecutor;
    private File mLogFile;
    private Setting mSetting;

    private AtomicBoolean mWriterIsRunning = new AtomicBoolean(false);

    private volatile RandomAccessFile mWriter;

    protected LoggerPrinter(){

    }

    protected LoggerPrinter(File file, Setting setting){
        mLogFile = file;
        mSetting = setting;
        init();
    }

    public void init(){
        mQueue = new ArrayBlockingQueue<String>(QUEUE_SIZE);
        mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(mRunnable);
    }

    public void setLogFile(File file){
        mLogFile = file;
    }

    public void setSetting(Setting setting){
        mSetting = setting;
    }

    public void write(String log){
        if (mQueue != null)
            mQueue.offer(log);
        if (!mWriterIsRunning.get())
            mExecutor.execute(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            mWriterIsRunning.set(true);
            if (mWriter == null) {
                try {
                    mWriter = createWriter(mLogFile, mSetting.isAppend);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mWriter != null) {
                        close();
                        return;
                    }
                }
            }
            int timeOutCount = 0;
            while (!Thread.currentThread().isInterrupted()){
                try {
                    // 从日志队列里读取日志，设置堵塞超时时间。
                    String log = mQueue.poll(POLL_TIME_OUT, TimeUnit.MILLISECONDS);
                    if (log != null) {
                        writeLog(log);
                        timeOutCount = 0;
                    } else if (mBufferCount > 0){
                        // 等待超时后，当缓冲区里有内容时，刷新缓冲区。
                        ++timeOutCount;
                        flush();
                    } else if (++timeOutCount >= MAX_TIME_OUT_COUNT) {
                        // 当等待次数超过最大限制时，退出循环，然后关闭输出流，完成当前线程。
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            mWriterIsRunning.set(false);
            close();
        }
    };

    private void writeLog(String log) throws IOException{
        if (mSetting.enableLogBuffer){
            writeBuffer(log);
        } else {
            writeStorage(log);
        }
    }

    /**
     * 把log先写入缓冲区，满了以后再写入磁盘
     * @param log
     * @throws IOException
     */
    private void writeBuffer(String log) throws IOException{
        if (mBufferCount + log.length() < BUFFER_LENGTH) {
            // 缓冲区未满时，写入缓冲区
            log.getChars(0, log.length(), mBuffer, mBufferCount);
            mBufferCount += log.length();
        } else {
            // 超过缓冲区时，把缓冲区的日志和当前这条日志写入磁盘中
            StringBuilder builder = new StringBuilder();
            builder.append(getBuffer()).append(log);
            writeStorage(builder.toString());
        }
    }

    /**
     * 以字符串的形式返回缓冲区的内容
     * @return
     */
    private synchronized String getBuffer(){
        if (mBufferCount != 0) {
            String bs = new String(mBuffer, 0, mBufferCount);
            mBufferCount = 0;
            return bs;
        } else {
            return "";
        }
    }

    /**
     * 把log写入磁盘中
     * @param log
     * @throws IOException
     */
    private synchronized void writeStorage(String log) throws IOException{
        if (mWriter == null || log == null || log.isEmpty()) return;
        checkFileSize(mWriter.length() + log.length());                 // 检查日志文件是否超出大小
        mWriter.seek(mWriter.length() - HTML_FOOT.length());            // 把指针移到</body></html>之前
        mWriter.write(log.getBytes(CHARSET));                           // 写入日志
        mWriter.write(HTML_FOOT.getBytes(CHARSET));                     // 重新把</body></html>加到页面最后面
    }

    /**
     * 检查日志文件是否超出大小，超出了就删除重建新日志文件
     * @param fileSize
     * @throws IOException
     */
    private void checkFileSize(long fileSize) throws IOException{
        if (fileSize > mSetting.maxFileSize && mLogFile != null){
            mLogFile.delete();
            mWriter = createWriter(mLogFile, mSetting.isAppend);
        }
    }

    private String getHtmlPage(){
        return HTML_HEAD+HTML_FOOT;
    }

    private synchronized RandomAccessFile createWriter(File file, boolean append) throws IOException{
        String page = null;
        // 是否新增在原来的日志文件里
        if (!append){
            if (file.exists()) {
                file.delete();
            }
            page = createLogFile(file);
        } else if (!file.exists()){
            page = createLogFile(file);
        }
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(file, "rwd");
        if (page != null)
            raf.writeBytes(page);
        return raf;
    }

    /**
     * 创建文件，同时返回一个基础页面
     * @param file
     * @return
     * @throws IOException
     */
    private String createLogFile(File file) throws IOException{
        file.createNewFile();
        LoggerManager.deleteOverShootLogFile(new File(file.getParent()));
        return getHtmlPage();
    }

    public void close(){
        if (mWriter != null){
            try {
                mWriter.close();
                mWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新缓冲区
     */
    public void flush(){
        try {
            writeStorage(getBuffer());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
