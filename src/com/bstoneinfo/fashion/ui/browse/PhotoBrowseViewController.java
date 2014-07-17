package com.bstoneinfo.fashion.ui.browse;

import android.content.Context;

import com.bstoneinfo.lib.ui.BSViewController;
import com.bstoneinfo.lib.view.BSPagerView;
import com.bstoneinfo.lib.view.BSPagerView.CreateCellDelegate;
import com.bstoneinfo.lib.widget.BSViewCell;

import custom.R;

public class PhotoBrowseViewController extends BSViewController {

    final BSPagerView pagerView;

    public PhotoBrowseViewController(Context context) {
        super(context);
        pagerView = new BSPagerView(context);
        pagerView.setCreateCellDelegate(new CreateCellDelegate() {
            @Override
            public BSViewCell createCell() {
                return new BSViewCell(getContext(), R.layout.photo_browse_cell) {
                    @Override
                    public void loadContent(Object data) {

                    }
                };
            }
        });
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
    }

}
