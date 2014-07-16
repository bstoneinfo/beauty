package com.bstoneinfo.lib.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bstoneinfo.lib.view.BSPagerSlidingTabView;

import custom.R;

public class BSPagerBarViewController extends BSViewController {

    private final ArrayList<String> titles;
    private final BSPagerSlidingTabView pagerSlidingTabStrip;
    private final ViewPager tabPagers;
    private BSPagerAdapter pagerAdapter;
    private OnPageChangeListener onPageChangeListener;
    private int currentSelectedPosition = -1;

    public BSPagerBarViewController(Context context, ArrayList<BSViewController> childViewControllers, ArrayList<String> titles) {
        super(context, R.layout.bs_pager_view_controller);
        getChildViewControllers().addAll(childViewControllers);
        this.titles = titles;
        tabPagers = (ViewPager) getRootView().findViewById(R.id.bs_tab_pagers);
        pagerSlidingTabStrip = (BSPagerSlidingTabView) getRootView().findViewById(R.id.bs_pagerSlidingTabStrip);
        pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getChildViewControllers().get(position)
                        .showViewController(currentSelectedPosition >= 0 ? getChildViewControllers().get(currentSelectedPosition) : null, null, null);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    public void setAllCaps(boolean textAllCaps) {
        pagerSlidingTabStrip.setAllCaps(textAllCaps);
    }

    public void setShouldExpand(boolean shouldExpand) {
        pagerSlidingTabStrip.setShouldExpand(shouldExpand);
    }

    public void setTextSize(int textSizePx) {
        pagerSlidingTabStrip.setTextSize(textSizePx);
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        pagerSlidingTabStrip.setIndicatorHeight(indicatorLineHeightPx);
    }

    public void setTypeface(Typeface typeface, int style) {
        pagerSlidingTabStrip.setTypeface(typeface, style);
    }

    public void setTabBackground(int resId) {
        pagerSlidingTabStrip.setTabBackground(resId);
    }

    public void setIndicatorColor(int indicatorColor) {
        pagerSlidingTabStrip.setIndicatorColor(indicatorColor);
    }

    public void setIndicatorColorResource(int resId) {
        pagerSlidingTabStrip.setIndicatorColorResource(resId);
    }

    public void setTextColor(int textColor) {
        pagerSlidingTabStrip.setTextColor(textColor);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        onPageChangeListener = listener;
    }

    @Override
    protected void viewDidLoad() {
        pagerAdapter = new BSPagerAdapter();
        tabPagers.setAdapter(pagerAdapter);
        pagerSlidingTabStrip.setViewPager(tabPagers);
    }

    private class BSPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return getChildViewControllers().size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Log.d("BSPagerAdapter", "instantiateItem position=" + position);
            BSViewController viewController = getChildViewControllers().get(position);
            View rootView = viewController.getRootView();
            if (rootView.getParent() == null) {
                container.addView(rootView);
                viewController.viewDidLoad();
            }
            return rootView;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
