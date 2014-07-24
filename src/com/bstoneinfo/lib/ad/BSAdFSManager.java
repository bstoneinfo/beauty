package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import android.app.Activity;

public class BSAdFSManager {

    private final static BSAdFSManager instance = new BSAdFSManager();

    private final ArrayList<BSAdFSObject> fsObjectArray = new ArrayList<BSAdFSObject>();

    public BSAdFSManager getInstance() {
        return instance;
    }

    public void addFSObject(BSAdFSObject fsObj) {
        fsObjectArray.add(fsObj);
    }

    public void start(Activity activity) {

    }

}
