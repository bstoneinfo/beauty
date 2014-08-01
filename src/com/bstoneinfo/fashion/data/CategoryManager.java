package com.bstoneinfo.fashion.data;

import java.util.HashMap;

public class CategoryManager {

    private static final CategoryManager instance = new CategoryManager();

    private HashMap<String, CategoryDataSource> dataSourceMap = new HashMap<String, CategoryDataSource>();

    public static CategoryManager getInstance() {
        return instance;
    }

    public CategoryDataSource getDataSource(String categoryName) {
        CategoryDataSource ds = dataSourceMap.get(categoryName);
        if (ds == null) {
            ds = new CategoryDataSource(categoryName);
            dataSourceMap.put(categoryName, ds);
        }
        return ds;
    }

    public void reset() {
        for (CategoryDataSource ds : dataSourceMap.values()) {
            ds.destroy();
        }
        dataSourceMap.clear();
    }
}
