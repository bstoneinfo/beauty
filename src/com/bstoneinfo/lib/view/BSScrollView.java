package com.bstoneinfo.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class BSScrollView extends ScrollView {

    public interface OnScrollChangedListener {
        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener onScrollChangedListener;

    public BSScrollView(Context context) {
        super(context);
    }

    public BSScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BSScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null && (l != oldl || t != oldt)) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

}
