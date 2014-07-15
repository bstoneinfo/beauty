package com.bstoneinfo.lib.ad;

import android.view.View;

public abstract class Banner {

	protected AdManager adManager;

	abstract String getTag();

	abstract View getView();

	abstract void create(AdManager adManager);

	Banner(AdManager adManager) {
		this.adManager = adManager;
		create(adManager);
	}

	void destroy() {
	}

	boolean startFullscreen() {
		return false;
	}

}
