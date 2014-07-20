package com.bstoneinfo.lib.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.bstoneinfo.lib.view.BSListView.BSListViewImpl;
import com.bstoneinfo.lib.view.BSListView.PullUpStates;
import com.bstoneinfo.lib.view.BSListView.PullUpWillLoadListener;
import com.bstoneinfo.lib.widget.BSBaseAdapter;
import com.bstoneinfo.lib.widget.BSGridAdapter;
import com.bstoneinfo.lib.widget.BSViewCell;

public class BSGridView extends ListView {

    private BSListViewImpl impl;
    private BSBaseAdapter adapter;

    public BSGridView(Context context) {
        super(context);
    }

    public BSGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(ArrayList<?> dataList, int numColumns, int itemWidth, int itemHeight, int horzSpacing, int vertSpacing) {
        init(dataList, numColumns, itemWidth, itemHeight, horzSpacing, vertSpacing, null, null);
    }

    public void init(ArrayList<?> dataList, int numColumns, int itemWidth, int itemHeight, int horzSpacing, int vertSpacing, ArrayList<View> headerViews,
            ArrayList<View> footerViews) {
        if (headerViews != null) {
            for (View headerView : headerViews) {
                addHeaderView(headerView, null, false);
            }
        }
        if (footerViews != null) {
            for (View footerView : footerViews) {
                addFooterView(footerView, null, false);
            }
        }
        adapter = new BSGridAdapter(getContext(), dataList, numColumns, itemWidth, itemHeight, horzSpacing, vertSpacing) {
            @Override
            public BSViewCell createCell() {
                return null;//createCellDelegate.createCell();
            }
        };
        setAdapter(adapter);
        impl = new BSListViewImpl(this);
    }

    public void setPullUpFooter(int normalResId, int loadingResId, int failedResId, int finishedResId) {
        impl.setPullUpFooter(normalResId, loadingResId, failedResId, finishedResId);
    }

    // 调用者必须设置listener 并在回调中load More
    public void setPullUpWillLoadListener(PullUpWillLoadListener listener) {
        impl.setPullUpWillLoadListener(listener);
    }

    public PullUpStates getPullUpStates() {
        return impl.getPullUpStates();
    }

    public void setPullUpState(PullUpStates state) {
        impl.setPullUpState(state);
    }
}
