package com.dnd.alynchos.dndcharactertracker.Debug;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 * Logging class for debugging
 */
public class Logger {

    private String TAG;
    private static short LOG_LEVEL = 4; // 4 is the highest log level
    public static final short DEBUG = 4, INFO = 3, WARN = 2, ERROR = 1, FATAL = 0;

    public Logger(String tag){
        TAG = tag;
    }

    public void debug(String mes){
        if(LOG_LEVEL >= DEBUG) System.out.println("" + TAG + "[DEBUG]: " + mes);
    }

    public void info(String mes){
        if(LOG_LEVEL >= INFO) System.err.println("" + TAG + "[INFO]:  " + mes);
    }

    public void error(String mes){
        if(LOG_LEVEL >= ERROR) System.err.println("" + TAG + "[ERROR]:  " + mes);
    }

    public void warn(String mes){
        if(LOG_LEVEL >= WARN) System.out.println("" + TAG + "[WARN]: " + mes);
    }

    public static void setLogLevel(short val){ LOG_LEVEL = val;}

}
