package com.bloomlife.android.log;

/**
 * Created by cjf on 2015/7/1.
 * 日志功能的一些设置
 */
public class Setting {

    public static final int VERBOSE = 1;
    public static final int DEBUG   = 2;
    public static final int INFO    = 3;
    public static final int WARN    = 4;
    public static final int ERROR   = 5;
    public static final int ASSERT  = 6;

    public final int outputLevel;
    public final long maxFileSize;
    public final boolean isAppend;
    public final boolean isOutputDate;
    public final boolean enableLogBuffer;
    public final boolean isDebug;

    public Setting(int outputLevel, long maxFileSize, boolean isAppend, boolean isOutputDate, boolean enableLogBuffer, boolean isDebug){
        this.outputLevel = outputLevel;
        this.enableLogBuffer = enableLogBuffer;
        this.maxFileSize = maxFileSize;
        this.isAppend = isAppend;
        this.isOutputDate = isOutputDate;
        this.isDebug = isDebug;
    }

    public static class SettingBuilder {
        private int outputLevel;
        private long maxFileSize;
        private boolean append;
        private boolean outputDate;
        private boolean enableLogBuffer;
        private boolean debug;


        public long getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public boolean isAppend() {
            return append;
        }

        public void setAppend(boolean append) {
            this.append = append;
        }

        public boolean isOutputDate() {
            return outputDate;
        }

        public void setOutputDate(boolean outputDate) {
            this.outputDate = outputDate;
        }

        public boolean isEnableLogBuffer() {
            return enableLogBuffer;
        }

        public void setEnableLogBuffer(boolean enableLogBuffer) {
            this.enableLogBuffer = enableLogBuffer;
        }

        public Setting create(){
            if (!debug)
                outputLevel = outputLevel > INFO ? outputLevel : INFO;
            return new Setting(outputLevel, maxFileSize, append, outputDate, enableLogBuffer, debug);
        }

        public int getOutputLevel() {
            return outputLevel;
        }

        public void setOutputLevel(int outputLevel) {
            this.outputLevel = outputLevel;
        }

        public boolean isDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }
    }

}
