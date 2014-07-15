package com.bstoneinfo.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.bstoneinfo.lib.view.BSListView.BSListViewImpl;
import com.bstoneinfo.lib.view.BSListView.PullUpWillLoadListener;
import com.bstoneinfo.lib.view.BSListView.PullUpStates;

public class BSExListView extends ExpandableListView {

    private BSListViewImpl impl;

    public BSExListView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public void showPullUpBar() {
        impl.showPullUpBar();
    }

    public void hidePullUpBar() {
        impl.hidePullUpBar();
    }
}
