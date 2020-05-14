package com.developer.base.utils.demo;

import android.app.Application;

import com.developer.base.utils.lib.tool.BaseDevice;

public class MyApp extends Application {
    public MyApp() {
        super();
        BaseDevice.getApp(this);
    }
}
