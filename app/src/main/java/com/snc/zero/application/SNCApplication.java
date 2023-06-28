package com.snc.zero.application;

import android.content.Context;

import com.snc.sample.webview.BuildConfig;

import androidx.multidex.MultiDexApplication;
import timber.log.Timber;

/**
 * Application
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class SNCApplication extends MultiDexApplication {
    private static final String TAG = SNCApplication.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        Timber.plant(new Timber.DebugTree());
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        Timber.i(TAG, ">>>>>>>>>> onCreate <<<<<<<<<<");
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        Timber.i(">>>>>>>>>> onLowMemory <<<<<<<<<<");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.i(">>>>>>>>>> onTrimMemory(" + level + ") <<<<<<<<<<");
        super.onTrimMemory(level);
    }

}
