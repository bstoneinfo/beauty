package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.app.NotificationEvent;

public class HistroyWaterFallViewController extends WaterFallViewController {

    public HistroyWaterFallViewController(Context context, String categoryName) {
        super(context, categoryName, NotificationEvent.CATEGORY_HISTORY_FINISHED_ + categoryName);
    }

    @Override
    protected void loadMore() {
        getDataSource().histroyMore();
    }

}
