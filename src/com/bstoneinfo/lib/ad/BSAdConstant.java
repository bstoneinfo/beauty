package com.bstoneinfo.lib.ad;

public class BSAdConstant {
    //    final static String AppId_Admob = "a150cc2cec89398";
    final static String AppId_Admob = "ca-app-pub-3897310542142538/8654485008";
    final static String AppId_AdChina_Banner = "81322";
    final static String AppId_AdChina_FullScreen = "81323";
    final static String AppId_Mobisage = "244648fdc1f14616aa865780c50891f7";
    final static String AppId_Baidu = "bc1ce592";

    public static void createBanner(BSAdManager adManager) {
        if (!adManager.isEmpty()) {
            return;
        }
        if (adManager.contains(BannerAdChina.tag)) {
            adManager.addAdView(new BannerAdChina(adManager));
        }
        if (adManager.contains(BannerAdmob.tag)) {
            adManager.addAdView(new BannerAdmob(adManager));
        }
        if (adManager.contains(BannerBaidu.tag)) {
            adManager.addAdView(new BannerBaidu(adManager));
        }
        //        if (adManager.contains(BannerMobisage.tag)) {
        //            adManager.addAdView(new BannerMobisage(adManager));
        //        }
    }
}
