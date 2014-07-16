package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.data.CategoryDataSource;

public class HistroyWaterFallViewController extends WaterFallViewController {

    public HistroyWaterFallViewController(Context context, String categoryName) {
        super(context, categoryName, CategoryDataSource.CATEGORY_HISTORY_FINISHED + categoryName);
    }

    @Override
    protected void loadMore() {
        getDataSource().histroyMore();
    }

}
