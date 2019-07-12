package com.willpower.editor;

import android.support.multidex.MultiDexApplication;

import com.willpower.log.JLog;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化JLog
        JLog.instance().init();
    }
}
