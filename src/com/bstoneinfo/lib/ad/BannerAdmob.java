package com.bstoneinfo.lib.ad;

import android.view.View;

public class BannerAdmob extends Banner {

	final public static String tag = "admob";

	private com.google.ads.AdView adView;

	BannerAdmob(AdManager adManager) {
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
		if (adView != null)
			adView.destroy();
	}

	@Override
	void create(final AdManager adManager) {
		if (adView != null)
			return;
		adView = new com.google.ads.AdView(adManager.activity, com.google.ads.AdSize.SMART_BANNER,
				AdConstant.AppId_Admob);
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

}
