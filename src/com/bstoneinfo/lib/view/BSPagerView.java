package com.bstoneinfo.lib.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.bstoneinfo.lib.widget.BSCellAdapter;
import com.bstoneinfo.lib.widget.BSViewCell;

public class BSPagerView extends ViewPager {

    private BSCellAdapter cellAdapter;
    private ViewPagerAdapter viewPagerAdapter;
    private SparseArray<BSViewCell> cellArray = new SparseArray<BSViewCell>();

    public BSPagerView(Context context) {
        super(context);
    }

    public BSPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(BSCellAdapter cellAdapter) {
        this.cellAdapter = cellAdapter;
        viewPagerAdapter = new ViewPagerAdapter();
        setAdapter(viewPagerAdapter);

    }

    public void notifyDataSetChanged() {
        viewPagerAdapter.notifyDataSetChanged();
    }

    public BSViewCell getCell(int position) {
        return cellArray.get(position);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return cellAdapter.getCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BSViewCell cell = cellAdapter.createCell();
            View view = null;
            if (cell != null) {
                cellArray.put(position, cell);
                view = cell.getRootView();
                cell.position = position;
                cell.loadContent(cellAdapter.getData(position));
            }
            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            BSViewCell cell = cellArray.get(position);
            if (cell != null) {
                cell.destory();
                cellArray.remove(position);
            }
            container.removeView((View) object);
        }
    }

}
