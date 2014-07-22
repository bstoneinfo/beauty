package com.bstoneinfo.lib.ad;

import android.view.View;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;

public class BannerAdmob extends BSAdBanner {

    final public static String tag = "admob";

    private com.google.ads.AdView adView;

    BannerAdmob(BSAdManager adManager) {
        super(adManager);
    }

    @Override
    String getTag() {
        return tag;
    }

    @Override
    View getView() {
        return adView;
    }

    @Override
    void destroy() {
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    void create(final BSAdManager adManager) {
        if (adView != null) {
            return;
        }
        adView = new com.google.ads.AdView(adManager.activity, com.google.ads.AdSize.SMART_BANNER, BSAdConstant.AppId_Admob);
        adView.loadAd(new com.google.ads.AdRequest());
        adView.setAdListener(new com.google.ads.AdListener() {

            @Override
            public void onReceiveAd(com.google.ads.Ad arg0) {
                adManager.log("Admob - onReceiveAd");
                adManager.adReceived(BannerAdmob.this);
            }

            @Override
            public void onPresentScreen(com.google.ads.Ad arg0) {
                adManager.log("Admob - onPresentScreen");
            }

            @Override
            public void onLeaveApplication(com.google.ads.Ad arg0) {
                adManager.log("Admob - onLeaveApplication");
            }

            @Override
            public void onFailedToReceiveAd(com.google.ads.Ad arg0, com.google.ads.AdRequest.ErrorCode arg1) {
                adManager.log("Admob - onFailedToReceiveAd");
                adManager.adFailed(BannerAdmob.this);
            }

            @Override
            public void onDismissScreen(com.google.ads.Ad arg0) {
                adManager.log("Admob - onDismissScreen");
            }
        });
    }

    @Override
    boolean startFullscreen() {
        final InterstitialAd interstitial = new InterstitialAd(adManager.activity, BSAdConstant.AppId_Admob);
        AdRequest adRequest = new AdRequest();
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {

            @Override
            public void onReceiveAd(Ad arg0) {
                interstitial.show();
                adManager.fullscreenReceived();
            }

            @Override
            public void onPresentScreen(Ad arg0) {
            }

            @Override
            public void onLeaveApplication(Ad arg0) {
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
                adManager.fullscreenFailed();
            }

            @Override
            public void onDismissScreen(Ad arg0) {
            }
        });
        return true;
    }

}
