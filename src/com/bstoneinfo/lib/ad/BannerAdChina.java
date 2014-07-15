package com.bstoneinfo.lib.ad;

import com.adchina.android.ads.views.AdView;

import android.graphics.Color;
import android.view.View;

public class BannerAdChina extends Banner {

	final public static String tag = "adchina";

	private AdView adView;
	private boolean fullscreenStarted = false;

	BannerAdChina(AdManager adManager) {
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
	void create(final AdManager adManager) {
		if (adView != null)
			return;
		adView = new com.adchina.android.ads.views.AdView(adManager.activity, AdConstant.AppId_AdChina_Banner, true, false);
		com.adchina.android.ads.AdEngine.initAdEngine(adManager.activity);
		com.adchina.android.ads.AdManager.setRefershinterval(30);
		com.adchina.android.ads.AdManager.setmVideoPlayer(true);
		com.adchina.android.ads.AdManager.setRelateScreenRotate(adManager.activity, true);
		com.adchina.android.ads.AdManager.setCloseImg(AdConstant.idDrawableAdchinaClose);
		com.adchina.android.ads.AdManager.setLoadingImg(AdConstant.idDrawableAdchinaLoading);
		com.adchina.android.ads.AdManager.setFullScreenAdspaceId(AdConstant.AppId_AdChina_FullScreen);
		com.adchina.android.ads.AdManager.setShowFullScreenTimer(true); // 设置是否显示倒计时
		com.adchina.android.ads.AdManager.setAdWindowBackgroundColor(Color.WHITE);// 设置全屏背景色
		com.adchina.android.ads.AdManager.setFullScreenTimerTextColor(Color.BLACK);// 设置全屏广告倒计时文字颜色
		com.adchina.android.ads.AdEngine.setAdListener(new com.adchina.android.ads.AdListener() {

			@Override
			public void onReceiveAd(com.adchina.android.ads.views.AdView arg0) {
				adManager.log("Adchina - onReceiveAd");
				adManager.adReceived(BannerAdChina.this);
			}

			@Override
			public void onFailedToReceiveAd(com.adchina.android.ads.views.AdView arg0) {
				adManager.log("Adchina - onFailedToReceiveAd");
			}

			@Override
			public void onRefreshAd(com.adchina.android.ads.views.AdView arg0) {
				adManager.log("Adchina - onRefreshAd");
				adManager.adReceived(BannerAdChina.this);
			}

			@Override
			public void onFailedToRefreshAd(com.adchina.android.ads.views.AdView arg0) {
				adManager.log("Adchina - onFailedToRefreshAd");
			}

			@Override
			public void onStartFullScreenLandPage() {
				adManager.log("Adchina - onStartFullScreenLandPage");
			}

			@Override
			public void onReceiveFullScreenAd() {
				adManager.log("Adchina - onReceiveFullScreenAd");
				com.adchina.android.ads.AdEngine.getAdEngine().showFullScreenAd(null);
				adManager.fullscreenReceived();
			}

			@Override
			public void onDisplayFullScreenAd() {
				adManager.log("Adchina - onDisplayFullScreenAd");
			}

			@Override
			public void onEndFullScreenLandpage() {
				adManager.log("Adchina - onEndFullScreenLandpage");
			}

			@Override
			public void onFailedToReceiveFullScreenAd() {
				adManager.log("Adchina - onFailedToReceiveFullScreenAd");
				adManager.fullscreenFailed();
			}

			@Override
			public void onReceiveVideoAd() {
				adManager.log("Adchina - onReceiveVideoAd");
			}

			@Override
			public void onPlayVideoAd() {
				adManager.log("Adchina - onPlayVideoAd");
			}

			@Override
			public void onFailedToReceiveVideoAd() {
				adManager.log("Adchina - onFailedToReceiveVideoAd");
			}

			@Override
			public void onFailedToPlayVideoAd() {
				adManager.log("Adchina - onFailedToPlayVideoAd");
			}

			@Override
			public void onClickBanner(com.adchina.android.ads.views.AdView arg0) {
				adManager.log("Adchina - onClickBanner");
			}

			@Override
			public boolean OnRecvSms(com.adchina.android.ads.views.AdView arg0, String arg1) {
				adManager.log("Adchina - OnRecvSms");
				return false;
			}

		});
		adView.setVisibility(View.GONE);
	}

	@Override
	boolean startFullscreen() {
		if (fullscreenStarted)
			return false;
		fullscreenStarted = true;
		com.adchina.android.ads.AdEngine.getAdEngine().startFullScreenAd();
		return true;
	}

}
