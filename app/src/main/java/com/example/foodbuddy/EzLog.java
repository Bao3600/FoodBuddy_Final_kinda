package com.example.foodbuddy;

import android.util.Log;


public class EzLog {

    private static final String TAG = "okhttp";

    public static void d(String s) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "--->" + s);
        }
    }

    public static void e(String s) {
        if(BuildConfig.DEBUG) {
            Log.e(TAG, "--->" + s);
        }
    }
}