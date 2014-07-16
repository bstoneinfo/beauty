package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

public class ExploreViewController extends CategoriesViewController {

    public ExploreViewController(Context context) {
        super(context);
    }

    @Override
    WaterFallViewController createChildViewController(String categoryName) {
        return new ExploreWaterFallViewController(getContext(), categoryName);
    }

}
