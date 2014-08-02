package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.fashion.data.CategoryDataSource;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.fashion.ui.browse.PhotoBrowseViewController;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoadStatus;
import com.bstoneinfo.lib.view.BSImageView;

public class NetworkWaterFallViewController extends ImageWaterFallViewController {

    protected final String categoryName;

    public NetworkWaterFallViewController(Context context, String category, final String dataEventName) {
        super(context);
        this.categoryName = category;
        BSApplication.defaultNotificationCenter.addObserver(this, dataEventName, new Observer() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(Observable observable, Object data) {
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList == null) {//fail
                    setPullupState(PullUpState.FAILED);
                } else {//success
                    if (dataList.isEmpty()) {//结束
                        setPullupState(PullUpState.FINISHED);
                    } else {
                        setPullupState(PullUpState.NORMAL);
                        int position = itemDataList.size();
                        itemDataList.addAll(dataList);
                        for (CategoryItemData itemData : dataList) {
                            final String remoteUrl = "http://" + MyUtils.getHost() + itemData.thumbURL;
                            final BSImageView imageView = new BSImageView(getContext());
                            imageView.setBackgroundColor(0xFFD0D0D0);
                            imageView.setConnectionQueue(connectionQueue);
                            imageView.setScaleType(ScaleType.FIT_CENTER);
                            imageView.setUrl(remoteUrl);
                            addView(imageView, itemData);
                            final int finalPosition = position;
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BSImageLoadStatus status = imageView.getImageLoadStatus();
                                    if (status == BSImageLoadStatus.LOADED) {
                                        PhotoBrowseViewController photoBrowseViewController = new PhotoBrowseViewController(getContext(), categoryName, itemDataList,
                                                dataEventName, finalPosition, getPullUpState() == PullUpState.FINISHED) {
                                            @Override
                                            protected void loadMore() {
                                                NetworkWaterFallViewController.this.loadMore();
                                            }
                                        };
                                        presentModalViewController(photoBrowseViewController, AnimationType.None);
                                    } else if (status == BSImageLoadStatus.FAILED) {
                                        imageView.setUrl(remoteUrl);
                                    }
                                }
                            });
                            position++;
                        }
                    }
                }
            }
        });
    }

    protected CategoryDataSource getDataSource() {
        return CategoryManager.getInstance().getDataSource(categoryName);
    }

    @Override
    protected void loadMore() {

    }

}
