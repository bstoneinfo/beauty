package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.app.NotificationEvent;

public class ExploreWaterFallViewController extends CategoryWaterFallViewController {

    public ExploreWaterFallViewController(Context context, String categoryName) {
        super(context, categoryName, NotificationEvent.CATEGORY_EXPLORE_FINISHED_ + categoryName);
    }

    @Override
    protected void loadMore() {
        getDataSource().exploreMore();
    }
}
