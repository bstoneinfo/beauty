package com.bstoneinfo.lib.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BSViewCell {

    public int position = -1;
    private final View rootView;

    public BSViewCell(View rootView) {
        this.rootView = rootView;
    }

    public BSViewCell(Context context, int layout) {
        this.rootView = LayoutInflater.from(context).inflate(layout, null);
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public View getRootView() {
        return rootView;
    }

    abstract public void loadContent(Object data);

    public void updateContent(Object data) {
    }

    public void destory() {
    }

}
