package com.bstoneinfo.lib.common;

import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.app.Application;
import android.content.SharedPreferences;

import com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent;

public class BSApplication extends Application {

    private static BSApplication instance;
    public static final BSNotificationCenter defaultNotificationCenter = new BSNotificationCenter();
    public static final BSLooperThread fileThread = new BSLooperThread("FileThread");
    public static final BSLooperThread databaseThread = new BSLooperThread("DatabaseThread");
    private BSRemoteConfig mRemoteConfig;
    private boolean bRunningForeground;

    public BSApplication() {
        super();
        instance = this;
    }

    public static BSApplication getApplication() {
        return instance;
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return instance.getSharedPreferences("default", 0);
    }

    public JSONObject getRemoteConfig() {
        return mRemoteConfig.mConfigJSON;
    }

    public void setRemoteConfigURL(String url) {
        mRemoteConfig.mRemoteConfigUrl = url;
    }

    /*
     * 返回程序是否在前台运行
     */
    public boolean isRunningForeground() {
        return bRunningForeground;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        defaultNotificationCenter.addObserver(this, BSNotificationEvent.APP_ENTER_BACKGROUND, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                bRunningForeground = false;
            }
        });
        defaultNotificationCenter.addObserver(this, BSNotificationEvent.APP_ENTER_FOREGROUND, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                bRunningForeground = true;
            }
        });
        mRemoteConfig = new BSRemoteConfig();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
