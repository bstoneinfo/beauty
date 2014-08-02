package com.bstoneinfo.fashion.favorite;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.text.TextUtils;

import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.ui.main.ImageWaterFallViewController;
import com.bstoneinfo.lib.common.BSApplication;

public class FavoriteViewController extends ImageWaterFallViewController {

    private boolean dataChanged = false;

    public FavoriteViewController(Context context) {
        super(context, NotificationEvent.FAVORITE_QUERYLIST_FINISHED);
    }

    @Override
    protected void loadMore() {
        FavoriteManager.getInstance().favoriteMore();
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        BSApplication.defaultNotificationCenter.addObserver(this, NotificationEvent.CATEGORY_ITEM_DATA_FINISHED, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                CategoryItemData itemData = (CategoryItemData) data;
                boolean isFavorite = FavoriteManager.getInstance().isFavorite(itemData);
                Iterator<CategoryItemData> iterator = itemDataList.iterator();
                while (iterator.hasNext()) {
                    CategoryItemData i = iterator.next();
                    if (TextUtils.equals(itemData.getFavoriteKey(), i.getFavoriteKey())) {
                        if (!isFavorite) {
                            iterator.remove();
                            dataChanged = true;
                        }
                        return;
                    }
                }
                if (isFavorite) {
                    itemDataList.add(0, itemData);
                    dataChanged = true;
                }
            }
        });
    }

    @Override
    protected void viewWillAppear() {
        super.viewWillAppear();
        if (dataChanged) {
            dataChanged = false;
            removeAllViews();
            for (CategoryItemData itemData : itemDataList) {
                addView(itemData);
            }
        }
    }
}
