package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.fashion.data.CategoryDataSource;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.fashion.ui.browse.PhotoBrowseViewController;
import com.bstoneinfo.lib.ad.BSAdBannerAdChina;
import com.bstoneinfo.lib.ad.BSAdBannerBaidu;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoadStatus;
import com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent;
import com.bstoneinfo.lib.net.BSHttpUrlConnectionQueue;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSWaterFallViewController;
import com.bstoneinfo.lib.view.BSImageView;
import com.bstoneinfo.lib.view.BSScrollView.OnScrollChangedListener;

import custom.R;

public abstract class WaterFallViewController extends BSWaterFallViewController {

    private final static int COLUMN_COUNT = 3;
    private final static int COLUMN_INTERVAL_DP = 5;
    private final int columnWidth = (BSActivity.getDisplayMetrics().widthPixels - BSActivity.dip2px(COLUMN_INTERVAL_DP) * (COLUMN_COUNT + 1)) / COLUMN_COUNT;

    private final BSHttpUrlConnectionQueue connectionQueue = new BSHttpUrlConnectionQueue(10);
    private final ArrayList<CategoryItemData> itemDataList = new ArrayList<CategoryItemData>();
    private final ArrayList<BSImageView> imageViewList = new ArrayList<BSImageView>();
    private boolean memoryWaringReceived = false;

    protected final String categoryName;

    private final BSAdBannerAdChina adchina;
    private final BSAdBannerBaidu adBaidu;
    private final LinearLayout footerView;

    abstract protected void loadMore();

    public WaterFallViewController(Context context, String category, final String dataEventName) {
        super(context, COLUMN_COUNT, BSActivity.dip2px(COLUMN_INTERVAL_DP));
        this.categoryName = category;

        adchina = new BSAdBannerAdChina(getActivity());
        adBaidu = new BSAdBannerBaidu(getActivity());

        footerView = new LinearLayout(getContext());
        footerView.setOrientation(LinearLayout.VERTICAL);

        View loadmoreView = LayoutInflater.from(context).inflate(R.layout.loadmore, null);
        footerView.addView(loadmoreView);

        setFooterView(footerView, loadmoreView.findViewById(R.id.loadmore_normal), loadmoreView.findViewById(R.id.loadmore_loading),
                loadmoreView.findViewById(R.id.loadmore_failed), null);
        View.OnClickListener loadmoreClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPullupState(PullUpState.LOADING);
                loadMore();
            }
        };
        loadmoreView.findViewById(R.id.loadmore_button).setOnClickListener(loadmoreClickListener);
        loadmoreView.findViewById(R.id.loadmore_refresh).setOnClickListener(loadmoreClickListener);

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
                            addView(imageView, columnWidth, columnWidth * itemData.thumbHeight / itemData.thumbWidth);
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
                                                WaterFallViewController.this.loadMore();
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
    protected void viewDidLoad() {
        super.viewDidLoad();

        adchina.start();
        adBaidu.start();
        footerView.addView(adchina.getAdView(), 0);
        footerView.addView(adBaidu.getAdView(), 0);

        loadMore();
        setPullupState(PullUpState.LOADING);
        getScrollView().setOnScrollChangedListener(new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (t != oldt) {
                    checkVisible();
                }
            }
        });
        BSApplication.defaultNotificationCenter.addObserver(this, BSNotificationEvent.LOW_MEMORY_WARNING, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                memoryWaringReceived = true;
                checkVisible();
            }
        });
    }

    @Override
    protected void viewWillAppear() {
        super.viewWillAppear();
        checkVisible();
    }

    @Override
    protected void viewWillDisappear() {
        super.viewWillDisappear();
        checkVisible();
    }

    private void checkVisible() {
        if (getViewStatus() == ViewStatus.Appearing || getViewStatus() == ViewStatus.Appeared) {
            int y1 = getScrollView().getScrollY();
            int y2 = y1 + getRootView().getHeight();
            for (BSImageView imageView : imageViewList) {
                int top = imageView.getTop();
                int bottom = imageView.getBottom();
                if (bottom >= y1 && top <= y2) {
                    if (memoryWaringReceived) {
                        setImageViewVisible(imageView, true);
                    } else if (imageView.getImageLoadStatus() == BSImageLoadStatus.FAILED) {
                        imageView.setUrl(imageView.getUrl());
                    }
                } else {
                    if (memoryWaringReceived) {
                        setImageViewVisible(imageView, false);
                    }
                }
            }
        } else {
            if (memoryWaringReceived) {
                for (BSImageView imageView : imageViewList) {
                    setImageViewVisible(imageView, false);
                }
            }
        }
    }

    private void setImageViewVisible(BSImageView imageView, boolean bVisible) {
        imageView.setVisible(bVisible);
    }

    @Override
    public void addView(View childView, int width, int height) {
        BSImageView imageView = (BSImageView) childView;
        imageViewList.add(imageView);
        if (memoryWaringReceived) {
            setImageViewVisible(imageView, getViewStatus() == ViewStatus.Appearing || getViewStatus() == ViewStatus.Appeared);
        }
        super.addView(childView, width, height);
    }

    @Override
    protected void destroy() {
        adchina.destroy();
        adBaidu.destroy();
        super.destroy();
    }
}
