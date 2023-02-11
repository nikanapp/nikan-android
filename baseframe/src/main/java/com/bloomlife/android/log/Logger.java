package com.bloomlife.android.log;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bloomlife.android.log.Setting.*;

/**
 * Created by cjf on 2015/7/1.
 * 自定义的日志类，能输出Html格式的日志
 */
public class Logger {

    private static LoggerManager mManager = LoggerManager.getInstance();

    private String mTag;

    protected Logger(){

    }

    public void setTag(String tag){
        mTag = tag;
    }

    public void v(String format, Object... args){
        println(VERBOSE, mTag, format, args);
    }

    public void d(String format, Object... args){
        println(DEBUG, mTag, format, args);
    }

    public void i(String format, Object... args){
        println(INFO, mTag, format, args);
    }

    public void w(String format, Object... args){
        println(WARN, mTag, format, args);
    }

    public void e(String format, Object... args){
        println(ERROR, mTag, format, args);
    }

    public void a(String format, Object... args){
        println(ASSERT, mTag, format, args);
    }

    public static void v(String tag, String format, Object... args){
        println(VERBOSE, tag, format, args);
    }

    public static void d(String tag, String format, Object... args){
        println(DEBUG, tag, format, args);
    }

    public static void i(String tag, String format, Object... args){
        println(INFO, tag, format, args);
    }

    public static void w(String tag, String format, Object... args){
        println(WARN, tag, format, args);
    }

    public static void e(String tag, String format, Object... args){
        println(ERROR, tag, format, args);
    }

    public static void a(String tag, String format, Object... args){
        println(ASSERT, tag, format, args);
    }

    private static String log(int level, String tag, String format, Object... args){
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("<p>").append("<font color=\"").append(color(level)).append("\">");
        logBuilder.append(mManager.getFormat().format(new Date())).append(" ");
        logBuilder.append("[").append(tag).append("]").append(" ");
        logBuilder.append("[").append(level(level)).append("]").append(" ");
        logBuilder.append((args.length == 0 || args == null) ? format : String.format(format, args));
        logBuilder.append("</font>").append("</p>");
        return logBuilder.toString();
    }

    private static String level(int level){
        switch(level){
            case VERBOSE:
                return "V";

            case DEBUG:
                return "D";

            case INFO:
                return "I";

            case WARN:
                return "W";

            case ERROR:
                return "E";

            case ASSERT:
                return "A";

            default:
                return "NULL";
        }
    }

    /**
     * 根据日志等级返回需要显示的字体颜色
     * @param level
     * @return
     */
    private static String color(int level){
        switch(level){
            case VERBOSE:
                return "black";

            case DEBUG:
                return "blue";

            case INFO:
                return "green";

            case WARN:
                return "#ffa500";

            case ERROR:
                return "red";

            case ASSERT:
                return "red";

            default:
                return "NULL";
        }
    }

    private static void printAndroidLog(int level, String tag, String msg){
        switch (level){
            case VERBOSE:
                Log.v(tag, msg);
                break;

            case DEBUG:
                Log.d(tag, msg);
                break;

            case INFO:
                Log.i(tag, msg);
                break;

            case WARN:
                Log.w(tag, msg);
                break;

            case ERROR:
                Log.e(tag, msg);
                break;

            case ASSERT:
                Log.e(tag, msg);
                break;

            default:
                break;
        }
    }

    private static void println(int level, String tag, String format, Object... args){
        if (mManager.getSetting().outputLevel <= level && mManager.getPrinter() != null){
            mManager.getPrinter().write(log(level, tag, format, args));
            printAndroidLog(level, tag, (args.length == 0 || args == null) ? format : String.format(format, args));
        }
    }

}
