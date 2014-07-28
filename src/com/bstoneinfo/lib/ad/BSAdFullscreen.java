package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

public class BSAdFullscreen {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = 0;

    public void addAdObject(BSAdObject fsObj) {
        adObjectArray.add(fsObj);
    }

    public void start() {
        if (adIndex < 0 || adIndex >= adObjectArray.size()) {
            return;
        }
        BSAdObject adObject = adObjectArray.get(adIndex);
        adObject.setAdListener(new BSAdListener() {
            @Override
            public void adReceived() {
                adIndex = -1;
            }

            @Override
            public void adFailed() {
                adIndex++;
                start();
            }
        });
        adObject.start();
    }
}
