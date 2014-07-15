package com.bstoneinfo.fashion.ui;

import android.content.Context;

import com.bstoneinfo.fashion.data.CategoryDataSource;

public class ExploreWaterFallViewController extends WaterFallViewController {

    public ExploreWaterFallViewController(Context context, String categoryName) {
        super(context, categoryName, CategoryDataSource.CATEGORY_EXPLORE_FINISHED + categoryName);
    }

    @Override
    protected void loadMore() {
        getDataSource().exploreMore();
    }
}
