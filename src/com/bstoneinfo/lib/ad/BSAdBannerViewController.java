package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.bstoneinfo.lib.ui.BSViewController;

public class BSAdBannerViewController extends BSViewController {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = 0;

    public BSAdBannerViewController(Context context) {
        super(context);
    }

    public void addAdObject(BSAdObject fsObj) {
        adObjectArray.add(fsObj);
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        MarginLayoutParams params = (MarginLayoutParams) getRootView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getRootView().setLayoutParams(params);
        startAd();
    }

    @Override
    protected void destroy() {
        for (BSAdObject adObject : adObjectArray) {
            adObject.destroy();
        }
        super.destroy();
    }

    private void startAd() {
        if (adIndex < 0 || adIndex >= adObjectArray.size()) {
            return;
        }
        final BSAdObject adObject = adObjectArray.get(adIndex);
        adObject.setAdListener(new BSAdListener() {
            @Override
            public void adReceived() {
                adObject.getAdView().setVisibility(View.VISIBLE);
            }

            @Override
            public void adFailed() {
                adIndex++;
                if (adIndex >= adObjectArray.size()) {
                    adIndex = 0;
                }
                startAd();
            }
        });
        adObject.start();
        if (adObject.getAdView().getParent() == null) {
            getRootView().addView(adObject.getAdView());
        }
        adObject.getAdView().setVisibility(View.GONE);
    }

}
