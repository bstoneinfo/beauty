package com.bstoneinfo.lib.ad;

import android.app.Activity;

import com.bstoneinfo.lib.common.BSLog;
import com.google.ads.AdView;

public class BSAdBannerAdmob extends BSAdObject {

    public BSAdBannerAdmob(Activity activity) {
        super(activity, "AppKey_Admob");
    }

    @Override
    public void start() {
        if (adView != null) {
            return;
        }
        adView = new AdView(activity, com.google.ads.AdSize.SMART_BANNER, appKey);
        ((AdView) adView).loadAd(new com.google.ads.AdRequest());
        ((AdView) adView).setAdListener(new com.google.ads.AdListener() {

            @Override
            public void onReceiveAd(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onReceiveAd");
                adReceived();
            }

            @Override
            public void onPresentScreen(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onPresentScreen");
            }

            @Override
            public void onLeaveApplication(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onLeaveApplication");
            }

            @Override
            public void onFailedToReceiveAd(com.google.ads.Ad arg0, com.google.ads.AdRequest.ErrorCode arg1) {
                BSLog.d("Admob - onFailedToReceiveAd");
                adFailed();
            }

            @Override
            public void onDismissScreen(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onDismissScreen");
            }
        });
    }

    @Override
    public void destroy() {
        if (adView != null) {
            ((AdView) adView).destroy();
        }
    }
}
