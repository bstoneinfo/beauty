package com.bstoneinfo.lib.ad;

import org.json.JSONObject;

import android.app.Activity;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.bstoneinfo.lib.common.BSLog;

public class BSAdBannerBaidu extends BSAdObject {

    public BSAdBannerBaidu(Activity activity) {
        super(activity, "AppKey_Baidu");
    }

    @Override
    public void start() {
        AdView.setAppSid(activity, appKey);
        AdView.setAppSec(activity, appKey);
        adView = new AdView(activity);
        ((AdView) adView).setListener(new AdViewListener() {

            @Override
            public void onVideoStart() {
                BSLog.d("Baidu - onVideoStart");
            }

            @Override
            public void onVideoFinish() {
                BSLog.d("Baidu - onVideoFinish");
            }

            @Override
            public void onAdSwitch() {
                BSLog.d("Baidu - onAdSwitch");
                adReceived();
            }

            @Override
            public void onAdShow(JSONObject info) {
                BSLog.d("Baidu - onAdShow " + info.toString());
            }

            @Override
            public void onAdReady(AdView adView) {
                BSLog.d("Baidu - onAdReady ");
                adReceived();
            }

            @Override
            public void onAdFailed(String reason) {
                BSLog.d("Baidu - onAdFailed " + reason);
                adFailed();
            }

            @Override
            public void onAdClick(JSONObject info) {
                BSLog.d("Baidu - onAdClick " + info.toString());
            }

            @Override
            public void onVideoClickAd() {
            }

            @Override
            public void onVideoClickClose() {
            }

            @Override
            public void onVideoClickReplay() {
            }

            @Override
            public void onVideoError() {
            }
        });
    }

}
