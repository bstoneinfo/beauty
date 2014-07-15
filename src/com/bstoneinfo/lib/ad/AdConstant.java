package com.bstoneinfo.lib.ad;

import custom.R;

public class AdConstant {
    final static String AppId_Admob = "a150cc2cec89398";
    final static String AppId_AdChina_Banner = "81322";
    final static String AppId_AdChina_FullScreen = "81323";
    final static String AppId_Mobisage = "244648fdc1f14616aa865780c50891f7";
    final static String AppId_Baidu = "bc1ce592";
    //	final static  String AppId_Baidu = "debug";

    public static int idDrawableAdchinaClose = R.drawable.adchina_close;
    public static int idDrawableAdchinaLoading = R.drawable.loading;

    public static void createBanner(AdManager adManager) {
        if (!adManager.isEmpty()) {
            return;
        }
        if (adManager.contains(BannerAdChina.tag)) {
            adManager.addAdView(new BannerAdChina(adManager));
        }
        if (adManager.contains(BannerAdmob.tag)) {
            adManager.addAdView(new BannerAdmob(adManager));
        }
    }
}
