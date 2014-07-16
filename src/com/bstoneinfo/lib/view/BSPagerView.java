package com.bstoneinfo.lib.view;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.bstoneinfo.lib.widget.BSViewCell;

public class BSPagerView extends ViewPager {

    public interface CreateCellDelegate {
        BSViewCell createCell(int position);
    }

    private ArrayList<?> dataList;
    private ViewPagerAdapter adapter;
    private CreateCellDelegate createCellDelegate;
    private SparseArray<BSViewCell> cellArray = new SparseArray<BSViewCell>();

    public BSPagerView(Context context) {
        super(context);
    }

    public BSPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(ArrayList<?> dataList) {
        this.dataList = dataList;
        adapter = new ViewPagerAdapter();
        setAdapter(adapter);

    }

    public void setCreateCellDelegate(CreateCellDelegate createCellDelegate) {
        this.createCellDelegate = createCellDelegate;
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (createCellDelegate == null) {
                return null;
            }
            BSViewCell cell = createCellDelegate.createCell(position);
            View view = null;
            if (cell != null) {
                cellArray.put(position, cell);
                view = cell.getRootView();
                cell.loadContent(dataList.get(position));
            }
            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            BSViewCell cell = cellArray.get(position);
            cell.destory();
            cellArray.remove(position);
            container.removeView((View) object);
        }
    }

}
