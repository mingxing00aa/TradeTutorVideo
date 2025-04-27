package com.app.yourvideoschannelapps.a;

import android.util.Log;


public class LogUtils {
    private static final String TAG = "log";

    public static void i(String msg) {
//        if (debug)
            Log.i(TAG, msg);
    }
}
