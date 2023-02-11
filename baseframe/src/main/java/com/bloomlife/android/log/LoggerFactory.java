package com.bloomlife.android.log;

/**
 * Created by cjf on 2015/7/1.
 */
public class LoggerFactory {

    public static Logger getLogger(String tag){
        Logger log = new Logger();
        log.setTag(tag);
        return log;
    }

}
