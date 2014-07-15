package com.bstoneinfo.lib.widget;

import android.content.Context;
import android.view.View;

public class BSViewCell {

    int position = -1;
    private Object itemData;
    private View rootView;

    public BSViewCell(View rootView) {
        this.rootView = rootView;
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public Object getItemData() {
        return itemData;
    }

    public View getRootView() {
        return rootView;
    }

    public void loadContent(Object data) {
        itemData = data;
    }

    public void updateContent(Object data) {
    }

    public void destory() {
    }

}
