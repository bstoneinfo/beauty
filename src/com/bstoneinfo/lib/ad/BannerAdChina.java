package com.bstoneinfo.lib.ad;

import android.view.View;

import com.adchina.android.ads.AdManager;
import com.adchina.android.ads.api.AdBannerListener;
import com.adchina.android.ads.api.AdFsListener;
import com.adchina.android.ads.api.AdFullScreen;
import com.adchina.android.ads.api.AdView;

public class BannerAdChina extends BSAdBanner {

    final public static String tag = "adchina";

    private AdView adView;
    private AdFullScreen adFullScreen;

    BannerAdChina(BSAdManager adManager) {
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
        adView = new AdView(adManager.activity, BSAdConstant.AppId_AdChina_Banner, true, false);
        AdManager.setEnableLbs(true); // 是否开启lbs精确广告定位
        AdManager.setRelateScreenRotate(adManager.activity, false); // 是否关心屏幕旋转，详细请查看文档
        AdManager.setAnimation(false); // banner展示是否需要动画
        AdManager.setLogMode(true); // 显示调试日志，发布时请关闭
        AdManager.setCanHardWare(true); // 是否允许打开硬件加速
        AdManager.setExpandToolBar(true); // 设置拓展页初始时展开还是关闭状态
        adView.setAdBannerListener(new AdBannerListener() {
            @Override
            public void onReceiveAd(AdView arg0) {
                adManager.log("Adchina - onReceiveAd");
                adManager.adReceived(BannerAdChina.this);
            }

            @Override
            public void onFailedToReceiveAd(AdView arg0) {
                adManager.log("Adchina - onFailedToReceiveAd");
            }

            @Override
            public void onClickBanner(AdView arg0) {
                adManager.log("Adchina - onClickBanner");
            }
        });
        adView.start();
        adView.setVisibility(View.GONE);
    }

    @Override
    boolean startFullscreen() {
        adFullScreen = new AdFullScreen(adManager.activity, BSAdConstant.AppId_AdChina_FullScreen);
        adFullScreen.setAdFsListener(new AdFsListener() {

            @Override
            public void onStartFullScreenLandPage() {
                adManager.log("Adchina - onStartFullScreenLandPage");
            }

            @Override
            public void onReceiveFullScreenAd() {
                adManager.log("Adchina - onReceiveFullScreenAd");
                adFullScreen.showFs();
                adManager.fullscreenReceived();
            }

            @Override
            public void onFinishFullScreenAd() {
                adManager.log("Adchina - onFinishFullScreenAd");
            }

            @Override
            public void onFailedToReceiveFullScreenAd() {
                adManager.log("Adchina - onFailedToReceiveFullScreenAd");
                adManager.fullscreenFailed();
            }

            @Override
            public void onEndFullScreenLandpage() {
                adManager.log("Adchina - onEndFullScreenLandpage");
            }

            @Override
            public void onDisplayFullScreenAd() {
                adManager.log("Adchina - onDisplayFullScreenAd");
            }

            @Override
            public void onClickFullScreenAd() {
                adManager.log("Adchina - onClickFullScreenAd");
            }
        });
        adFullScreen.start();
        return true;
    }

}
