package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.fashion.data.CategoryDataSource;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.net.BSHttpUrlConnectionQueue;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSWaterFallViewController;
import com.bstoneinfo.lib.view.BSImageView;

import custom.R;

public abstract class WaterFallViewController extends BSWaterFallViewController {

    private final static int COLUMN_COUNT = 3;
    private final static int COLUMN_INTERVAL_DP = 5;
    private final int columnWidth = (BSActivity.getDisplayMetrics().widthPixels - BSActivity.dip2px(COLUMN_INTERVAL_DP) * (COLUMN_COUNT + 1)) / COLUMN_COUNT;

    private final BSHttpUrlConnectionQueue connectionQueue = new BSHttpUrlConnectionQueue(10);
    private final ArrayList<CategoryItemData> itemDataList = new ArrayList<CategoryItemData>();

    protected final String categoryName;

    abstract protected void loadMore();

    public WaterFallViewController(Context context, String categoryName, String dataEventName) {
        super(context, COLUMN_COUNT, BSActivity.dip2px(COLUMN_INTERVAL_DP));
        this.categoryName = categoryName;
        setNotificationCenter(BSApplication.defaultNotificationCenter);

        View footerView = LayoutInflater.from(context).inflate(R.layout.loadmore, null);
        setFooterView(footerView, footerView.findViewById(R.id.loadmore_normal), footerView.findViewById(R.id.loadmore_loading), footerView.findViewById(R.id.loadmore_failed),
                null);
        View.OnClickListener loadmoreClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPullupState(PullUpState.LOADING);
                loadMore();
            }
        };
        footerView.findViewById(R.id.loadmore_button).setOnClickListener(loadmoreClickListener);
        footerView.findViewById(R.id.loadmore_refresh).setOnClickListener(loadmoreClickListener);

        addNotificationObserver(dataEventName, new Observer() {
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
                            addView(imageView, columnWidth, columnWidth * itemData.thumbHeight / itemData.thumbWidth);
                            //                            final int finalPosition = position;
                            //                            imageView.setOnClickListener(new View.OnClickListener() {
                            //                                @Override
                            //                                public void onClick(View v) {
                            //                                    BSImageLoadStatus status = imageView.getImageLoadStatus();
                            //                                    if (status == BSImageLoadStatus.LOADED) {
                            //                                        PhotoBrowseViewController photoBrowseViewController = new PhotoBrowseViewController(getContext(), itemDataList, finalPosition);
                            //                                        presentModalViewController(photoBrowseViewController, AnimationType.None);
                            //                                    } else if (status == BSImageLoadStatus.FAILED) {
                            //                                        imageView.setUrl(remoteUrl);
                            //                                    }
                            //                                }
                            //                            });
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
    protected void viewDidLoad() {
        super.viewDidLoad();
        loadMore();
        setPullupState(PullUpState.LOADING);
    }
}
