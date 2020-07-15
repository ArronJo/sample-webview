package com.snc.zero.application;

import android.app.Application;

import com.snc.zero.log.Logger;

/**
 * Application
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class SNCApplication extends Application {
    private static final String TAG = SNCApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        Logger.i(TAG, ">>>>>>>>>> onCreate <<<<<<<<<<");
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        Logger.i(TAG, ">>>>>>>>>> onLowMemory <<<<<<<<<<");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Logger.i(TAG, ">>>>>>>>>> onTrimMemory(" + level + ") <<<<<<<<<<");
        super.onTrimMemory(level);
    }

}
