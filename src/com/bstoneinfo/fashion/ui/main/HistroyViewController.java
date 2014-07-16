package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

public class HistroyViewController extends CategoriesViewController {

    public HistroyViewController(Context context) {
        super(context);
    }

    @Override
    WaterFallViewController createChildViewController(String categoryName) {
        return new HistroyWaterFallViewController(getContext(), categoryName);
    }

}
