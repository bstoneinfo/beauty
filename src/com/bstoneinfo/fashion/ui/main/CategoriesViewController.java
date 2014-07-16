package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSPagerViewController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.R;

public abstract class CategoriesViewController extends BSViewController {

    public CategoriesViewController(Context context) {
        super(context);
    }

    abstract WaterFallViewController createChildViewController(String categoryName);

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();

        ArrayList<String> titles = new ArrayList<String>();
        titles.add(getContext().getString(R.string.tab_51));
        titles.add(getContext().getString(R.string.tab_52));

        ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
        childViewControllers.add(createChildViewController("51"));
        childViewControllers.add(createChildViewController("52"));

        BSPagerViewController pagerViewController = new BSPagerViewController(getContext(), childViewControllers, titles);
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
    }
}
