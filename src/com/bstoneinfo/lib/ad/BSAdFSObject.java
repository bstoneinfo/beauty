package com.bstoneinfo.lib.ad;

import android.app.Activity;

abstract class BSAdFSObject {

    protected String appKey;

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    abstract void start(Activity activity);

    abstract void adReceived();

    abstract void adFailed();
}
