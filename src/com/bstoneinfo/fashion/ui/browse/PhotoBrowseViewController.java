package com.bstoneinfo.fashion.ui.browse;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.ui.BSViewController;
import com.bstoneinfo.lib.view.BSPagerView;
import com.bstoneinfo.lib.widget.BSCellAdapter;
import com.bstoneinfo.lib.widget.BSViewCell;

public abstract class PhotoBrowseViewController extends BSViewController {

    final private BSPagerView pagerView;
    final private String dataEventName;
    final private ArrayList<CategoryItemData> itemDataList;
    private int position;
    private boolean bEnded = false;
    private boolean bLoadmoreFailed = false;

    public PhotoBrowseViewController(Context context, ArrayList<CategoryItemData> itemDataList, int position, String dataEventName) {
        super(context);
        this.dataEventName = dataEventName;
        this.position = position;
        this.itemDataList = (ArrayList<CategoryItemData>) itemDataList.clone();
        getRootView().setBackgroundColor(Color.BLACK);
        pagerView = new BSPagerView(getContext());
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();

        final BSCellAdapter adapter = new BSCellAdapter() {

            @Override
            public Object getData(int position) {
                if (position < itemDataList.size()) {
                    return itemDataList.get(position);
                }
                if (bLoadmoreFailed) {
                    return new CategoryItemData();
                }
                return null;
            }

            @Override
            public int getCount() {
                return itemDataList.size() + (bEnded ? 0 : 1);
            }

            @Override
            public BSViewCell createCell() {
                return new PhotoBrowseViewCell(getContext());
            }
        };
        pagerView.setAdapter(adapter);
        getRootView().addView(pagerView);

        pagerView.setCurrentItem(position);
        pagerView.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int page) {
                if (page >= itemDataList.size()) {
                    loadMore();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        addNotificationObserver(dataEventName, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                final int loadmorePosition = itemDataList.size();
                final PhotoBrowseViewCell lastCell = (PhotoBrowseViewCell) pagerView.getCell(loadmorePosition);
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList == null) {
                    bLoadmoreFailed = true;
                    lastCell.refreshView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bLoadmoreFailed = false;
                            loadMore();
                            if (lastCell != null) {
                                lastCell.loadContent(adapter.getData(loadmorePosition));
                            }
                        }
                    });
                } else {
                    bLoadmoreFailed = false;
                    if (dataList.isEmpty()) {
                        bEnded = true;
                    } else {
                        itemDataList.addAll(dataList);
                    }
                }
                if (lastCell != null) {
                    lastCell.loadContent(adapter.getData(loadmorePosition));
                }
                pagerView.notifyDataSetChanged();
            }
        });
    }

    abstract protected void loadMore();

}
