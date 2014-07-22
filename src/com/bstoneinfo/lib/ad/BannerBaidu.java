package com.bstoneinfo.lib.ad;

import org.json.JSONObject;

import android.view.View;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;

public class BannerBaidu extends BSAdBanner {

    final public static String tag = "baidu";

    private AdView adView;

    BannerBaidu(BSAdManager adManager) {
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
    void create(final BSAdManager adManager) {
        if (adView != null) {
            return;
        }
        AdView.setAppSid(adManager.activity, BSAdConstant.AppId_Baidu);
        AdView.setAppSec(adManager.activity, BSAdConstant.AppId_Baidu);
        adView = new AdView(adManager.activity);
        adView.setListener(new AdViewListener() {

            @Override
            public void onVideoStart() {
                adManager.log("Baidu - onVideoStart");
            }

            @Override
            public void onVideoFinish() {
                adManager.log("Baidu - onVideoFinish");
            }

            @Override
            public void onAdSwitch() {
                adManager.log("Baidu - onAdSwitch");
                adManager.adReceived(BannerBaidu.this);
            }

            @Override
            public void onAdShow(JSONObject info) {
                adManager.log("Baidu - onAdShow " + info.toString());
            }

            @Override
            public void onAdReady(AdView adView) {
                adManager.log("Baidu - onAdReady ");
                adManager.adReceived(BannerBaidu.this);
            }

            @Override
            public void onAdFailed(String reason) {
                adManager.log("Baidu - onAdFailed " + reason);
                adManager.adFailed(BannerBaidu.this);
            }

            @Override
            public void onAdClick(JSONObject info) {
                adManager.log("Baidu - onAdClick " + info.toString());
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

        adView.setVisibility(View.GONE);
    }

    @Override
    boolean startFullscreen() {
        //        final ViewGroup activityContent = (ViewGroup) adManager.activity.findViewById(R.id.activity_content);
        //        if (activityContent != null) {
        //            final RelativeLayout adsParent = new RelativeLayout(adManager.activity);
        //            activityContent.addView(adsParent, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //            SplashAd splashAd = new SplashAd(adManager.activity, adsParent, new SplashAdListener() {
        //
        //                int seconds = 0;
        //                BSTimer bsTimer;
        //
        //                @Override
        //                public void onAdDismissed() {
        //                    Log.i("adBaidu", "onAdDismissed");
        //                    dismiss();
        //                }
        //
        //                @Override
        //                public void onAdFailed(String arg0) {
        //                    Log.i("adBaidu", "onAdFailed");
        //                    adManager.fullscreenFailed();
        //                    dismiss();
        //                }
        //
        //                @Override
        //                public void onAdPresent() {
        //                    Log.i("adBaidu", "onAdPresent");
        //                    adManager.fullscreenReceived();
        //                    bsTimer = BSTimer.schedule(1000, new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            seconds++;
        //                            if (seconds == 5) {
        //                                dismiss();
        //                            }
        //                        }
        //                    }, 0);
        //                }
        //
        //                private void dismiss() {
        //                    activityContent.removeView(adsParent);
        //                    if (bsTimer != null) {
        //                        bsTimer.cancel();
        //                        bsTimer = null;
        //                    }
        //                }
        //            });
        //
        //        }
        return true;
    }
}
