package com.bstoneinfo.fashion.ui.browse;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.ui.BSViewController;
import com.bstoneinfo.lib.view.BSPagerView;
import com.bstoneinfo.lib.view.BSPagerView.CreateCellDelegate;
import com.bstoneinfo.lib.widget.BSViewCell;

public class PhotoBrowseViewController extends BSViewController {

    final BSPagerView pagerView;

    public PhotoBrowseViewController(Context context, ArrayList<CategoryItemData> itemDataList, int position) {
        super(context);
        getRootView().setBackgroundColor(Color.BLACK);
        pagerView = new BSPagerView(context);
        pagerView.setCreateCellDelegate(new CreateCellDelegate() {
            @Override
            public BSViewCell createCell() {
                return new PhotoBrowseViewCell(getContext());
            }
        });
        getRootView().addView(pagerView);
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
    }

}
