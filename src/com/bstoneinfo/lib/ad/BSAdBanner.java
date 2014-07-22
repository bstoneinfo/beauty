package com.bstoneinfo.lib.ad;

import android.view.View;

public abstract class BSAdBanner {

	protected BSAdManager adManager;

	abstract String getTag();

	abstract View getView();

	abstract void create(BSAdManager adManager);

	BSAdBanner(BSAdManager adManager) {
		this.adManager = adManager;
		create(adManager);
	}

	void destroy() {
	}

	boolean startFullscreen() {
		return false;
	}

}
