package com.bstoneinfo.lib.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class BSTabBarController extends BSViewController {

    public interface OnSelectListener {
        void onSelect(int currentSelectIndex, int lastSelectIndex);
    }

    final ViewGroup tabbarView;
    private FrameLayout containerView;
    private int currentSelected = -1;
    private final int defaultSelected;
    private final ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();
    private OnSelectListener onSelectListener;

    public BSTabBarController(Context context, int tabLayout, ArrayList<BSViewController> childViewControllers, int defaultSelected) {
        super(new RelativeLayout(context));
        this.defaultSelected = defaultSelected;
        getChildViewControllers().addAll(childViewControllers);
        RelativeLayout rootView = (RelativeLayout) getRootView();
        containerView = new FrameLayout(context);
        rootView.addView(containerView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        tabbarView = (ViewGroup) ((ViewGroup) LayoutInflater.from(context).inflate(tabLayout, rootView)).getChildAt(rootView.getChildCount() - 1);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @Override
    protected void viewDidLoad() {
        for (BSViewController viewController : getChildViewControllers()) {
            viewController.hideView();
            containerView.addView(viewController.getRootView());
            viewController.parentViewController = this;
            viewController.setMargin();
            viewController.viewDidLoad();
        }

        for (int j = 0; j < tabbarView.getChildCount(); j++) {
            View childView = tabbarView.getChildAt(j);
            if (childView instanceof RadioButton) {
                radioButtons.add((RadioButton) childView);
                ((RadioButton) childView).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            for (int index = radioButtons.size() - 1; index >= 0; index--) {
                                if (radioButtons.get(index) == buttonView) {
                                    int lastSelectIndex = currentSelected;
                                    int newSelectIndex = index;
                                    final BSViewController oldViewController = currentSelected < 0 ? null : getChildViewControllers().get(currentSelected);
                                    final BSViewController newViewController = index < 0 ? null : getChildViewControllers().get(index);
                                    currentSelected = index;
                                    newViewController.showViewController(oldViewController, null, null);
                                    if (oldViewController != null) {
                                        oldViewController.hideView();
                                    }
                                    if (newViewController != null) {
                                        newViewController.showView();
                                    }
                                    if (newViewController.isHideBottomBar()) {
                                        tabbarView.setVisibility(View.GONE);
                                    } else {
                                        tabbarView.setVisibility(View.VISIBLE);
                                    }
                                    if (onSelectListener != null) {
                                        onSelectListener.onSelect(newSelectIndex, lastSelectIndex);
                                    }
                                    return;
                                }
                            }
                        }
                    }

                });
            }
        }
        select(defaultSelected);
    }

    public int getSelectedIndex() {
        return currentSelected;
    }

    public BSViewController getSelectedViewController() {
        if (currentSelected < 0) {
            return null;
        } else {
            return getChildViewControllers().get(currentSelected);
        }
    }

    protected void setCheck(int index, boolean bCheck) {
        if (index >= 0 && index < radioButtons.size()) {
            radioButtons.get(index).setChecked(bCheck);
        }
    }

    public boolean select(int index) {
        if (!isEnabled()) {
            return false;
        }
        if (index < 0 || index >= getChildViewControllers().size()) {
            index = -1;
        }
        if (currentSelected == index) {
            return false;
        }
        if (currentSelected >= 0 && currentSelected < getChildViewControllers().size()) {
            setCheck(currentSelected, false);
        }
        if (index >= 0 && index < getChildViewControllers().size()) {
            setCheck(index, true);
        }
        return true;
    }

    @Override
    protected void viewWillAppear() {
        viewStatus = ViewStatus.Appearing;
        BSViewController selectedViewController = getSelectedViewController();
        if (selectedViewController != null) {
            selectedViewController.viewWillAppear();
        }
    }

    @Override
    protected void viewDidAppear() {
        viewStatus = ViewStatus.Appeared;
        BSViewController selectedViewController = getSelectedViewController();
        if (selectedViewController != null) {
            selectedViewController.viewDidAppear();
        }
    }

    @Override
    protected void viewWillDisappear() {
        viewStatus = ViewStatus.Disappearing;
        BSViewController selectedViewController = getSelectedViewController();
        if (selectedViewController != null) {
            selectedViewController.viewWillDisappear();
        }
    }

    @Override
    protected void viewDidDisappear() {
        viewStatus = ViewStatus.Disappeared;
        BSViewController selectedViewController = getSelectedViewController();
        if (selectedViewController != null) {
            selectedViewController.viewDidDisappear();
        }
    }

    @Override
    BSViewController findViewControllerNeedDisableWhenAnimating() {
        return this;
    }

    @Override
    public boolean back() {
        BSViewController selectedViewController = getSelectedViewController();
        if (selectedViewController != null) {
            return selectedViewController.back();
        }
        return backAction == BackAction.Disable;
    }

    @Override
    public void restore() {
        super.restore();
        BSViewController selectedViewController = getSelectedViewController();
        if (selectedViewController != null) {
            selectedViewController.restore();
        }
    }

}
