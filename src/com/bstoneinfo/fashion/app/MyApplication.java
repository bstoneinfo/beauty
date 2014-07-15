package com.bstoneinfo.fashion.app;

import com.bstoneinfo.lib.common.BSApplication;

import custom.Constant;

public class MyApplication extends BSApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setRemoteConfigURL(Constant.remoteConfigURL);
    }
}
