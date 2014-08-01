package com.bstoneinfo.lib.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.bstoneinfo.lib.common.BSAnimation;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSNotificationCenter;
import com.bstoneinfo.lib.common.BSTimer;
import com.bstoneinfo.lib.common.BSUtils;

public class BSViewController {

    public enum ViewStatus {
        Init,
        Appearing,
        Appeared,
        Disappearing,
        Disappeared,
        Released
    }

    public enum BackAction {
        Dismiss,
        Disable,
    }

    public enum AnimationType {
        None,
        CoverVertical,
        ZoomOut,
    }

    BSViewController parentViewController;
    FrameLayout disableMaskLayout;
    boolean dismissed = false;
    ViewStatus viewStatus = ViewStatus.Init;
    BackAction backAction;

    private final ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
    private final ViewGroup rootView;
    private BSNavigationBar navBar;
    private BSTimer showHideAsyncRun;
    private boolean viewEnabled = true;
    private boolean hideBottomBar = false;
    boolean overTopBar = false;
    private BSViewController presentingViewController, presentedViewController;
    private AnimationType presentAnimationType;
    private BSNotificationCenter notificationCenter = BSApplication.defaultNotificationCenter;
    private final ArrayList<BSTimer> asyncRunArrayList = new ArrayList<BSTimer>();
    private final ArrayList<BSAnimation> animationList = new ArrayList<BSAnimation>();

    public BSViewController(Context context, int layout) {
        rootView = (ViewGroup) LayoutInflater.from(context).inflate(layout, null);
        rootView.setClickable(true);
    }

    public BSViewController(ViewGroup parentView, int layout) {
        LayoutInflater.from(parentView.getContext()).inflate(layout, parentView);
        rootView = (ViewGroup) parentView.getChildAt(parentView.getChildCount() - 1);
        rootView.setClickable(true);
    }

    public BSViewController(ViewGroup view) {
        rootView = view;
        rootView.setClickable(true);
    }

    public BSViewController(Context context) {
        rootView = new FrameLayout(context);
        rootView.setClickable(true);
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    public BSApplication getApplication() {
        return (BSApplication) getContext().getApplicationContext();
    }

    public BSActivity getActivity() {
        return (BSActivity) getContext();
    }

    public BSViewController getMainPanel() {
        return getActivity().getMainViewController();
    }

    public BSNavigationController getNavigationPanel() {
        BSViewController viewController = parentViewController;
        while (viewController != null) {
            if (viewController instanceof BSNavigationController) {
                return (BSNavigationController) viewController;
            }
            viewController = viewController.parentViewController;
        }
        return null;
    }

    public BSTabBarController getTabBarController() {
        BSViewController viewController = parentViewController;
        while (viewController != null) {
            if (viewController instanceof BSTabBarController) {
                return (BSTabBarController) viewController;
            }
            viewController = viewController.parentViewController;
        }
        return null;
    }

    public BSViewController getParentViewController() {
        return parentViewController;
    }

    public List<BSViewController> getChildViewControllers() {
        return childViewControllers;
    }

    public ViewStatus getViewStatus() {
        return viewStatus;
    }

    protected void restore() {
    }

    protected void destroy() {
        viewStatus = ViewStatus.Released;
        BSApplication.defaultNotificationCenter.removeObservers(this);
        if (notificationCenter != null) {
            notificationCenter.removeObservers(this);
        }

        for (BSTimer asyncRun : asyncRunArrayList) {
            asyncRun.cancel();
        }
        asyncRunArrayList.clear();

        for (BSAnimation animation : animationList) {
            animation.cancel();
        }
        animationList.clear();

        if (showHideAsyncRun != null) {
            showHideAsyncRun.cancel();
            showHideAsyncRun = null;
        }

        for (int i = childViewControllers.size() - 1; i >= 0; i--) {
            childViewControllers.get(i).destroy();
        }

        ViewGroup parentView = (ViewGroup) rootView.getParent();
        if (parentView != null) {
            parentView.removeView(rootView);
        }
        parentViewController = null;
    }

    void setNavigationBar(BSNavigationBar navbar) {
        this.navBar = navbar;
    }

    public BSNavigationBar getNavigationBar() {
        BSViewController viewController = this;
        do {
            if (viewController.navBar != null) {
                return viewController.navBar;
            }
            viewController = viewController.parentViewController;
        } while (viewController != null);
        return null;
    }

    public void setOverTopBar(boolean bOver) {
        overTopBar = bOver;
    }

    public void setHideBottomBar(boolean bHide) {
        hideBottomBar = bHide;
    }

    boolean isHideBottomBar() {
        return hideBottomBar;
    }

    void setMargin() {
        // 根据tabbar的显示或隐藏 设置panelLayout的bottomMargin以控制panelLayout的高度
        ViewGroup tabbarView = null;
        if (getParentViewController() instanceof BSTabBarController) {
            tabbarView = ((BSTabBarController) getParentViewController()).tabbarView;
        }
        MarginLayoutParams lp = (MarginLayoutParams) getRootView().getLayoutParams();
        if (tabbarView == null || isHideBottomBar()) {
            lp.bottomMargin = 0;
        } else {
            lp.bottomMargin = tabbarView.getLayoutParams().height;
        }
        getRootView().setLayoutParams(lp);
    }

    public void setBackAction(BackAction backAction) {
        this.backAction = backAction;
    }

    void showView() {
        getRootView().setVisibility(View.VISIBLE);
        if (navBar != null) {
            navBar.rootView.setVisibility(View.VISIBLE);
        }
    }

    void hideView() {
        getRootView().setVisibility(View.GONE);
        if (navBar != null) {
            navBar.rootView.setVisibility(View.GONE);
        }
    }

    BSViewController findViewControllerNeedDisableWhenAnimating() {
        if (getNavigationBar() == null) {
            return this;
        } else {
            return getParentViewController().findViewControllerNeedDisableWhenAnimating();
        }
    }

    void showViewController(final BSViewController viewControllerToHide, final Animation showAnimation, final Runnable didShowCallback) {
        if (showHideAsyncRun != null) {
            showHideAsyncRun.cancel();
            showHideAsyncRun = null;
        }

        if (viewControllerToHide != null) {
            viewControllerToHide.viewWillDisappear();
        }
        viewWillAppear();

        final BSViewController panelNeedDisable;
        if (showAnimation == null) {
            panelNeedDisable = null;
        } else {
            panelNeedDisable = findViewControllerNeedDisableWhenAnimating();
            if (panelNeedDisable != null) {
                panelNeedDisable.setEnabled(false);
            }
        }

        AnimationListener animationListener = new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showHideAsyncRun = BSTimer.asyncRun(new Runnable() {
                    @Override
                    public void run() {
                        if (panelNeedDisable != null) {
                            panelNeedDisable.setEnabled(true);
                        }
                        if (viewControllerToHide != null) {
                            viewControllerToHide.viewDidDisappear();
                        }
                        viewDidAppear();
                        if (didShowCallback != null) {
                            didShowCallback.run();
                        }
                        showHideAsyncRun = null;
                    }
                });
            }
        };

        if (showAnimation != null) {
            showAnimation.setAnimationListener(animationListener);
        } else {
            animationListener.onAnimationEnd(null);
        }
    }

    void hideViewController(final BSViewController viewControllerToShow, final Animation hideAnimation, final Runnable didHideCallback) {
        if (showHideAsyncRun != null) {
            showHideAsyncRun.cancel();
            showHideAsyncRun = null;
        }

        viewWillDisappear();
        if (viewControllerToShow != null) {
            viewControllerToShow.viewWillAppear();
        }

        final BSViewController panelNeedDisable;
        if (hideAnimation == null) {
            panelNeedDisable = null;
        } else {
            panelNeedDisable = findViewControllerNeedDisableWhenAnimating();
            if (panelNeedDisable != null) {
                panelNeedDisable.setEnabled(false);
            }
        }

        AnimationListener animationListener = new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewDidDisappear();
                if (didHideCallback != null) {
                    didHideCallback.run();
                }
                showHideAsyncRun = BSTimer.asyncRun(new Runnable() {
                    @Override
                    public void run() {
                        if (panelNeedDisable != null) {
                            panelNeedDisable.setEnabled(true);
                        }
                        if (viewControllerToShow != null) {
                            viewControllerToShow.viewDidAppear();
                        }
                        showHideAsyncRun = null;
                    }
                });
            }
        };

        if (hideAnimation != null) {
            hideAnimation.setAnimationListener(animationListener);
        } else {
            animationListener.onAnimationEnd(null);
        }
    }

    public void addChildViewController(final BSViewController childViewController) {
        addChildViewController(childViewController, rootView);
    }

    public void addChildViewController(final BSViewController childViewController, final ViewGroup parentView) {
        if (childViewController.parentViewController != null) {
            BSUtils.debugAssert("The subpanel(" + childViewController + ") already has a parent viewController(" + childViewController.parentViewController + ")");
            return;
        }
        childViewControllers.add(childViewController);
        childViewController.parentViewController = this;
        if (childViewController.rootView.getParent() == null) {
            parentView.addView(childViewController.rootView);
        }
        childViewController.setNotificationCenter(notificationCenter);
        childViewController.viewDidLoad();
        if (viewStatus == ViewStatus.Appearing) {
            childViewController.viewWillAppear();
        }
        if (viewStatus == ViewStatus.Appeared) {
            childViewController.viewWillAppear();
            childViewController.asyncRun(new Runnable() {
                @Override
                public void run() {
                    childViewController.viewDidAppear();
                }
            });
        }
        if (viewStatus == ViewStatus.Disappearing) {
            childViewController.viewWillDisappear();
        }
        if (viewStatus == ViewStatus.Disappeared) {
            childViewController.viewWillDisappear();
            childViewController.asyncRun(new Runnable() {
                @Override
                public void run() {
                    childViewController.viewDidDisappear();
                }
            });
        }
    }

    public void removeChildViewController(BSViewController viewController) {
        if (viewController.parentViewController != this) {
            BSUtils.debugAssert("The subpanel(" + viewController + ") has not added to this viewController(" + this + ")");
            return;
        }
        viewController.hideViewController(null, null, null);
        childViewControllers.remove(viewController);
        viewController.destroy();
    }

    void removeChildViewController(BSViewController viewController, boolean animated) {
        removeChildViewController(viewController);
    }

    public boolean back() {//back只负责半闭子Panel，不负责自己的关闭
        for (int i = childViewControllers.size() - 1; i >= 0; i--) {
            BSViewController viewController = childViewControllers.get(i);
            if (viewController.back()) {
                return true;
            }
            if (viewController.backAction == BackAction.Dismiss) {
                removeChildViewController(viewController);
                return true;
            }
        }
        return backAction == BackAction.Disable;
    }

    public boolean isEnabled() {
        return viewEnabled;
    }

    public void setEnabled(final boolean bEnable) {
        if (viewEnabled == bEnable) {
            return;
        }
        viewEnabled = bEnable;
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (bEnable == false) {
                    // 在panelView上加一层maskView，以防止用户点击
                    if (disableMaskLayout == null) {
                        disableMaskLayout = new FrameLayout(getContext());
                        disableMaskLayout.setClickable(true);
                        rootView.addView(disableMaskLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    }
                } else {
                    //将maskView移除
                    if (disableMaskLayout != null) {
                        rootView.removeView(disableMaskLayout);
                        disableMaskLayout = null;
                    }
                }
            }
        });
    }

    protected void viewDidLoad() {
    }

    protected void viewWillAppear() {
        viewStatus = ViewStatus.Appearing;
        for (BSViewController viewController : childViewControllers) {
            viewController.viewWillAppear();
        }
    }

    protected void viewDidAppear() {
        viewStatus = ViewStatus.Appeared;
        for (BSViewController viewController : childViewControllers) {
            viewController.viewDidAppear();
        }
    }

    protected void viewWillDisappear() {
        viewStatus = ViewStatus.Disappearing;
        for (BSViewController viewController : childViewControllers) {
            viewController.viewWillDisappear();
        }
    }

    protected void viewDidDisappear() {
        viewStatus = ViewStatus.Disappeared;

        for (BSAnimation animation : animationList) {
            animation.finish();
        }
        animationList.clear();

        for (BSViewController viewController : childViewControllers) {
            viewController.viewDidDisappear();
        }
    }

    public void presentModalViewController(BSViewController modalViewController, AnimationType animationType) {
        if (presentingViewController != null) {
            BSUtils.debugAssert("Current presentPanel (" + presentingViewController.toString() + ") has not dismissed, can not present new.");
            return;
        }
        BSViewController presentTo = getActivity().presentViewControllers.isEmpty() ? getActivity().getMainViewController() : getActivity().presentViewControllers
                .get(getActivity().presentViewControllers.size() - 1);
        presentingViewController = modalViewController;
        presentingViewController.presentAnimationType = animationType;
        presentingViewController.parentViewController = getActivity().getMainViewController();
        presentingViewController.presentedViewController = this;
        modalViewController.setNotificationCenter(notificationCenter);
        getActivity().addPresentViewController(presentingViewController);
        presentingViewController.viewDidLoad();

        Animation animation = null;
        if (animationType == AnimationType.ZoomOut) {
            animation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(400);
        } else if (animationType == AnimationType.CoverVertical) {
            animation = new TranslateAnimation(0, 0, getActivity().mainView.getHeight(), 0);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(400);
        }
        presentingViewController.showViewController(presentTo, animation, null);
        if (animation != null) {
            presentingViewController.rootView.startAnimation(animation);
        }
    }

    public void dismiss() {
        dismiss(parentViewController != null && parentViewController.presentingViewController == this);
    }

    public void dismiss(boolean animated) {
        if (dismissed || parentViewController == null) {//已经dismiss,防重入
            return;
        }
        dismissed = true;
        if (presentedViewController != null) {
            presentedViewController.removePresentViewController(animated);
        } else {
            parentViewController.removeChildViewController(this, animated);
        }
    }

    private void removePresentViewController(boolean animated) {
        if (presentingViewController == null) {
            return;
        }
        final Runnable endCallback = new Runnable() {
            @Override
            public void run() {
                getActivity().removePresentViewController(presentingViewController);
                presentingViewController.destroy();
                presentingViewController = null;
            }
        };

        Animation animation = null;
        if (animated && presentAnimationType == AnimationType.CoverVertical) {
            animation = new TranslateAnimation(0, 0, 0, getActivity().mainView.getHeight());
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(400);
        }
        BSViewController panelPresentTo = getActivity().presentViewControllers.size() >= 2 ? getActivity().presentViewControllers
                .get(getActivity().presentViewControllers.size() - 2) : getActivity().getMainViewController();
        presentingViewController.hideViewController(panelPresentTo, animation, endCallback);
        if (animation != null) {
            presentingViewController.rootView.startAnimation(animation);
        }
    }

    public void setNotificationCenter(BSNotificationCenter notificationCenter) {
        this.notificationCenter = notificationCenter;
    }

    public void addNotificationObserver(String event, Observer observer) {
        if (notificationCenter != null) {
            notificationCenter.addObserver(this, event, observer);
        }
    }

    public void removeNotificationObserver(Observer observer) {
        if (notificationCenter != null) {
            notificationCenter.removeObserver(observer);
        }
    }

    public void removeNotificationObservers(String event) {
        if (notificationCenter != null) {
            notificationCenter.removeObservers(this, event);
        }
    }

    public void removeNotificationObservers() {
        if (notificationCenter != null) {
            notificationCenter.removeObservers(this);
        }
    }

    public void notifyNotificationCenterOnUIThread(String event) {
        if (notificationCenter != null) {
            notificationCenter.notifyOnUIThread(event);
        }
    }

    public void notifyNotificationCenterOnUIThread(String event, final Object data) {
        if (notificationCenter != null) {
            notificationCenter.notifyOnUIThread(event, data);
        }
    }

    public void asyncRun(final Runnable runnable) {
        asyncRun(runnable, 0);
    }

    public void asyncRun(final Runnable runnable, int delayMillis) {
        BSTimer asyncRun = BSTimer.asyncRun(new Runnable() {
            @Override
            public void run() {
                asyncRunArrayList.remove(runnable);
                runnable.run();
            }
        }, delayMillis);
        asyncRunArrayList.add(asyncRun);
    }

    public void startAnimation(final BSAnimation paramAnimation, final Runnable endListener) {
        if (getViewStatus() == ViewStatus.Disappearing || getViewStatus() == ViewStatus.Disappeared) {
            paramAnimation.setEndListener(endListener);
            paramAnimation.finish();
            return;
        }
        paramAnimation.setEndListener(new Runnable() {
            @Override
            public void run() {
                if (endListener != null) {
                    endListener.run();
                }
                animationList.remove(paramAnimation);
            }
        });
        animationList.add(paramAnimation);
        paramAnimation.start();
    }

    public void cancelAnimation(BSAnimation animation) {
        animation.cancel();
        animationList.remove(animation);
    }

    public void finishAnimation(BSAnimation animation) {
        animation.finish();
    }

}
