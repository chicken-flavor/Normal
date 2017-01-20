package com.ayyj.normaltemp.application;

import android.app.Application;

/**
 * Created by ayyj on 2017/1/4.
 */

public class MineApp extends Application {

    private static MineApp sInstance;

    public static MineApp getsInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
