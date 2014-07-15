package com.bstoneinfo.lib.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

public class BSNavigationController extends BSViewController {

    public BSNavigationBar defaultNavBar;

    public BSNavigationController(Context context, BSViewController rootViewController) {
        super(context);
        defaultNavBar = new BSNavigationBar(context);
        addChildViewController(rootViewController);
    }

    public void setDefaultNavigationBar(BSNavigationBar navBar) {
        defaultNavBar = navBar;
    }

    public BSViewController getRootViewController() {
        return getChildViewControllers().get(0);
    }

    public BSViewController getTopViewController() {
        return getChildViewControllers().get(getChildViewControllers().size() - 1);
    }

    private void setChildViewControllerMargin(BSViewController childViewController) {// 返回tabbarView
        // 根据tabbar的显示或隐藏 设置panelLayout的bottomMargin以控制panelLayout的高度
        ViewGroup tabbarView = getTabbarView();
        MarginLayoutParams lp = (MarginLayoutParams) childViewController.getRootView().getLayoutParams();
        if (tabbarView == null || childViewController.isHideBottomBar()) {
            lp.bottomMargin = 0;
        } else {
            lp.bottomMargin = tabbarView.getLayoutParams().height;
        }
        childViewController.getRootView().setLayoutParams(lp);
    }

    @Override
    void setMargin() {
    }

    @Override
    public void addChildViewController(final BSViewController childViewController) {
        getChildViewControllers().add(childViewController);
        childViewController.parentViewController = this;
        getRootView().addView(childViewController.getRootView());
        getRootView().addView(childViewController.getNavigationBar().rootView);
    }

    private void removeChildFramePanel(BSViewController childViewController) {
        getChildViewControllers().remove(childViewController);
        getRootView().removeView(childViewController.getNavigationBar().rootView);
        getRootView().removeView(childViewController.getRootView());
        childViewController.destroy();
    }

    private ViewGroup getTabbarView() {
        if (getParentViewController() instanceof BSTabBarController) {
            return ((BSTabBarController) getParentViewController()).tabbarView;
        } else {
            return null;
        }
    }

    @Override
    boolean isHideBottomBar() {
        return getTopViewController().isHideBottomBar();
    }

    public boolean push(final BSViewController panel, boolean animated) {

        //        final BSViewController lastFramePanel = getTopChildFramePanel();
        //        final BSViewController newFramePanel = addChildFramePanel(panel);
        //
        //        setChildViewControllerMargin(newFramePanel);
        //        panel.getNavigationBar().setBackButton();// 加入back按钮
        //        panel.didLoad();
        //
        //        if (getPanelStatus() == ViewStatus.HIDING || getPanelStatus() == ViewStatus.HIDDEN) {
        //            return true;
        //        }
        //
        //        final ViewGroup tabbarView = getTabbarView();
        //        if (animated) {// 有动画
        //            if (tabbarView != null) {//tabbar动画
        //                if (tabbarView.getVisibility() == View.VISIBLE && panel.isHideBottomBar()) {// 需要隐藏tabbar，将tabbar从右向左移出
        //                    Animation animation = PanelUtils.createHorzSlideAnimation(0, -getRootView().getWidth());
        //                    animation.setAnimationListener(new AnimationListener() {
        //                        @Override
        //                        public void onAnimationStart(Animation animation) {
        //                        }
        //
        //                        @Override
        //                        public void onAnimationRepeat(Animation animation) {
        //                        }
        //
        //                        @Override
        //                        public void onAnimationEnd(Animation animation) {
        //                            tabbarView.setVisibility(View.GONE);
        //                        }
        //                    });
        //                    tabbarView.startAnimation(animation);
        //                } else if (tabbarView.getVisibility() == View.GONE && !panel.isHideBottomBar()) {// 需要显示tabbar，将tabbar从右向左移入
        //                    tabbarView.setVisibility(View.VISIBLE);
        //                    tabbarView.startAnimation(PanelUtils.createHorzSlideAnimation(getRootView().getWidth(), 0));
        //                }
        //            }
        //            // 原panel从右向左移出
        //            if (lastFramePanel != null) {
        //                lastFramePanel.rootView.startAnimation(PanelUtils.createHorzSlideAnimation(0, -getRootView().getWidth()));
        //            }
        //            // 新panel从右向左移入
        //            Animation pushAnimation = PanelUtils.createHorzSlideAnimation(getRootView().getWidth(), 0);
        //            panel.showPanel(lastFramePanel.corePanel, pushAnimation, null);
        //            newFramePanel.rootView.startAnimation(pushAnimation);
        //        } else {
        //            if (tabbarView != null) {
        //                if (panel.isHideBottomBar()) {
        //                    tabbarView.setVisibility(View.GONE);// 直接隐藏tabbar
        //                } else {
        //                    tabbarView.setVisibility(View.VISIBLE);// 直接显示tabbar
        //                }
        //            }
        //            panel.showPanel(lastFramePanel.corePanel, null, null);
        //        }
        return true;
    }

    public boolean pop(final BSViewController toPanel, final boolean animated) {

        //        BSViewController toFramePanel = null;
        //        int toIndex = -1, index = 0;
        //        for (BSViewController childFrame : childFramePanels) {
        //            if (toPanel == childFrame.corePanel) {
        //                toIndex = index;
        //                toFramePanel = childFrame;
        //                break;
        //            }
        //            index++;
        //        }
        //        if (toIndex < 0 || toIndex == childFramePanels.size() - 1) {
        //            return false;
        //        }
        //
        //        final int finalToIndex = toIndex;
        //        final Runnable endCallback = new Runnable() {
        //            @Override
        //            public void run() {
        //                for (int index = childFramePanels.size() - 1; index > finalToIndex; index--) {
        //                    removeChildFramePanel(childFramePanels.get(index));
        //                }
        //            }
        //        };
        //
        //        if (getPanelStatus() == ViewStatus.HIDING || getPanelStatus() == ViewStatus.HIDDEN) {
        //            endCallback.run();
        //            return true;
        //        }
        //
        //        final BSViewController popFramePanel = getTopChildFramePanel();
        //        final ViewGroup tabbarView = getTabbarView();
        //        if (animated) {// 有动画
        //            if (tabbarView.getVisibility() == View.VISIBLE && toPanel.isHideBottomBar()) {// 需要隐藏tabbar，从左向右移出
        //                Animation animation = PanelUtils.createHorzSlideAnimation(0, getRootView().getWidth());
        //                animation.setAnimationListener(new AnimationListener() {
        //                    @Override
        //                    public void onAnimationStart(Animation animation) {
        //                    }
        //
        //                    @Override
        //                    public void onAnimationRepeat(Animation animation) {
        //                    }
        //
        //                    @Override
        //                    public void onAnimationEnd(Animation animation) {
        //                        tabbarView.setVisibility(View.GONE);
        //                    }
        //                });
        //                tabbarView.startAnimation(animation);
        //            } else if (tabbarView.getVisibility() == View.GONE && !toPanel.isHideBottomBar()) {// 需要显示tabbar，从左向右移入
        //                tabbarView.setVisibility(View.VISIBLE);
        //                tabbarView.startAnimation(PanelUtils.createHorzSlideAnimation(-getRootView().getWidth(), 0));
        //            }
        //            toFramePanel.rootView.startAnimation(PanelUtils.createHorzSlideAnimation(-getRootView().getWidth(), 0));
        //            Animation popAnimation = PanelUtils.createHorzSlideAnimation(0, getRootView().getWidth());// 将pop出的panel从左向右移出
        //            popFramePanel.corePanel.hidePanel(toPanel, popAnimation, endCallback);
        //            popFramePanel.rootView.startAnimation(popAnimation);
        //
        //        } else {
        //            if (tabbarView != null) {
        //                if (toPanel.isHideBottomBar()) {
        //                    tabbarView.setVisibility(View.GONE);// 直接隐藏tabbar
        //                } else {
        //                    tabbarView.setVisibility(View.VISIBLE);// 直接显示tabbar
        //                }
        //            }
        //            toPanel.showPanel(popFramePanel.corePanel, null, null);
        //            endCallback.run();
        //        }
        return true;
    }

    public boolean pop(boolean animated) {
        if (getChildViewControllers().size() >= 2) {
            return pop(getChildViewControllers().get(getChildViewControllers().size() - 2), animated);
        }
        return false;
    }

    public boolean popToRoot(final boolean animated) {
        if (getChildViewControllers().size() >= 2) {
            return pop(getRootViewController(), animated);
        }
        return false;
    }

    @Override
    void removeChildViewController(BSViewController viewController, boolean animated) {
        if (getChildViewControllers().size() > 1) {
            BSViewController topViewController = getTopViewController();
            if (viewController == topViewController) {
                pop(animated);
            }
        }
    }

    @Override
    BSViewController findViewControllerNeedDisableWhenAnimating() {
        if (getTabbarView() != null) {
            return getParentViewController().findViewControllerNeedDisableWhenAnimating();
        } else {
            return this;
        }
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        setChildViewControllerMargin(getTopViewController());
        getRootViewController().viewDidLoad();
    }

    @Override
    protected void viewWillAppear() {
        viewStatus = ViewStatus.Appearing;
        getTopViewController().viewWillAppear();
    }

    @Override
    protected void viewDidAppear() {
        viewStatus = ViewStatus.Appeared;
        getTopViewController().viewDidAppear();
    }

    @Override
    protected void viewWillDisappear() {
        viewStatus = ViewStatus.Disappearing;
        getTopViewController().viewWillDisappear();
    }

    @Override
    protected void viewDidDisappear() {
        viewStatus = ViewStatus.Disappeared;
        getTopViewController().viewDidDisappear();
    }

    @Override
    public void restore() {
        getTopViewController().restore();
        super.restore();
    }

    @Override
    public boolean back() {
        if (getChildViewControllers().size() <= 1) {
            return false;
        }
        if (!getTopViewController().back()) {
            pop(true);
        }
        return true;
    }

}
