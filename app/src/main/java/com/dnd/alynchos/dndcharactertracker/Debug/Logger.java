package com.dnd.alynchos.dndcharactertracker.Debug;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class Logger {

        private String TAG;

        public Logger(String tag){
            TAG = tag;
        }

        public void debug(String mes){
            System.out.println("" + TAG + ":  " + mes);
        }

        public void error(String mes){
            System.err.println("" + TAG + ":  " + mes);
        }

}
