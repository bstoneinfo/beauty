package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class BSAdManager {

    Activity activity;
    private ViewGroup adbarLayout;

    private String adType, adFull;
    private ArrayList<String> adTypeList = new ArrayList<String>();
    private ArrayList<String> adFullList = new ArrayList<String>();
    private ArrayList<BSAdBanner> bannerArray = new ArrayList<BSAdBanner>();
    private ArrayList<BSAdBanner> adReceived = new ArrayList<BSAdBanner>();
    private View currentView = null;
    private int currentAd = -1;
    private int fullscreenAd = 0;

    public BSAdManager(Activity activity, ViewGroup parent) {
        this.activity = activity;
        this.adbarLayout = parent;
    }

    public void destory() {
        for (BSAdBanner banner : bannerArray) {
            banner.destroy();
        }
    }

    private BSAdBanner getBanner(String type) {
        for (BSAdBanner banner : bannerArray) {
            if (banner.getTag().equals(type)) {
                return banner;
            }
        }
        return null;
    }

    public void log(String s) {
        Log.d("ad", s);
    }

    public boolean isEmpty() {
        return bannerArray.isEmpty();
    }

    public boolean contains(String tag) {
        return adTypeList.contains(tag);
    }

    public void setAdType(String bannerType, String fullType) {
        if (TextUtils.equals(adType, bannerType) || TextUtils.equals(adFull, fullType)) {
            return;
        }
        adType = bannerType;
        ArrayList<String> typeList = new ArrayList<String>();
        String types[] = bannerType.split(",");
        for (String t : types) {
            if (!TextUtils.isEmpty(t)) {
                typeList.add(t);
            }
        }
        adTypeList = typeList;
        currentAd = -1;

        ArrayList<String> fullList = new ArrayList<String>();
        String fullTypes[] = fullType.split(",");
        for (String t : fullTypes) {
            if (!TextUtils.isEmpty(t)) {
                fullList.add(t);
            }
        }
        adFullList = fullList;

        new Thread() {

            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        BSAdConstant.createBanner(BSAdManager.this);
                        if (currentView == null) {
                            showAd();
                        }
                        fullscreenNext();
                    }
                });
            }

        }.start();

    }

    public void showAd() {
        if (adTypeList.isEmpty()) {
            return;
        }
        int nextAd = currentAd + 1;
        while (nextAd != (currentAd < 0 ? adTypeList.size() : currentAd)) {
            if (nextAd >= adTypeList.size()) {
                nextAd = 0;
            }
            String adType = adTypeList.get(nextAd);
            BSAdBanner banner = getBanner(adType);
            if (banner != null) {
                View view = banner.getView();
                if (view != null && adReceived.contains(banner)) {
                    currentAd = nextAd;
                    if (currentView != view) {
                        if (currentView != null) {
                            currentView.setVisibility(View.GONE);
                        }
                        currentView = view;
                        currentView.setVisibility(View.VISIBLE);
                        log("showAd - " + adType);
                    }
                    return;
                }
            }
            nextAd++;
        }
    }

    void addAdView(BSAdBanner banner) {
        log(banner.getTag() + " - addView");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        adbarLayout.addView(banner.getView(), lp);
        bannerArray.add(banner);
    }

    void adReceived(BSAdBanner banner) {
        View adView = banner.getView();
        if (currentView == null) {
            currentView = adView;
        }
        adbarLayout.setVisibility(View.VISIBLE);
        adView.setVisibility(currentView == adView ? View.VISIBLE : View.GONE);
        if (!adReceived.contains(banner)) {
            adReceived.add(banner);
        }
    }

    void adFailed(BSAdBanner banner) {
        adReceived.remove(banner);
        View adView = banner.getView();
        if (currentView == adView) {
            showAd();
        }
    }

    public void fullscreenReceived() {
        fullscreenAd = -1;
    }

    public void fullscreenFailed() {
        fullscreenNext();
    }

    private void fullscreenNext() {
        if (fullscreenAd < 0) {
            return;
        }
        while (fullscreenAd < adFullList.size()) {
            BSAdBanner banner = getBanner(adFullList.get(fullscreenAd++));
            if (banner != null && banner.startFullscreen()) {
                return;
            }
        }
        fullscreenAd = -1;
    }

}
