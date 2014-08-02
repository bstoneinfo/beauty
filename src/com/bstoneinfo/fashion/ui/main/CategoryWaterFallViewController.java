package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.data.CategoryDataSource;
import com.bstoneinfo.fashion.data.CategoryManager;

public abstract class CategoryWaterFallViewController extends ImageWaterFallViewController {

    protected final String categoryName;

    public CategoryWaterFallViewController(Context context, String category, final String dataEventName) {
        super(context, dataEventName);
        this.categoryName = category;
    }

    protected CategoryDataSource getDataSource() {
        return CategoryManager.getInstance().getDataSource(categoryName);
    }

}
