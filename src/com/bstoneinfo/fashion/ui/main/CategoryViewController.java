package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBannerAdmob;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSPagerBarViewController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.R;

public class CategoryViewController extends BSViewController {

    protected final String categoryName;
    private final BSAdBannerAdmob admob;

    public CategoryViewController(Context context, String categoryName) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        this.categoryName = categoryName;
        admob = new BSAdBannerAdmob(getActivity());
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        ArrayList<String> titles = new ArrayList<String>();
        titles.add(getContext().getString(R.string.tab_explore));
        titles.add(getContext().getString(R.string.tab_history));

        ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
        childViewControllers.add(new ExploreWaterFallViewController(getContext(), categoryName));
        childViewControllers.add(new HistroyWaterFallViewController(getContext(), categoryName));

        BSPagerBarViewController pagerViewController = new BSPagerBarViewController(getContext(), childViewControllers, titles);
        pagerViewController.setAllCaps(false);
        pagerViewController.setShouldExpand(true);
        pagerViewController.setIndicatorHeight(BSActivity.dip2px(4));
        pagerViewController.setTypeface(null, Typeface.NORMAL);
        pagerViewController.setTabBackground(R.drawable.pager_sliding_tab_bg);
        pagerViewController.setIndicatorColor(getContext().getResources().getColor(R.color.pager_sliding_tab_indicator_color));
        pagerViewController.setTextColor(getContext().getResources().getColor(R.color.pager_sliding_tab_text_color));
        pagerViewController.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        addChildViewController(pagerViewController);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pagerViewController.getRootView().getLayoutParams();
        params.weight = 1;
        params.height = 0;
        pagerViewController.getRootView().setLayoutParams(params);
        admob.start();
        getRootView().addView(admob.getAdView());
    }

    @Override
    protected void destroy() {
        admob.destroy();
        super.destroy();
    }

}
