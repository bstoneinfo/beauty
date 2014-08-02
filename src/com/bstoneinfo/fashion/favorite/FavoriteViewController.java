package com.bstoneinfo.fashion.favorite;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.favorite.FavoriteAgent.FavoriteQueryListener;
import com.bstoneinfo.fashion.ui.main.ImageWaterFallViewController;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSWaterFallViewController;

import custom.R;

public class FavoriteViewController extends BSWaterFallViewController {

    private final int columnWidth = (BSActivity.getDisplayMetrics().widthPixels - BSActivity.dip2px(ImageWaterFallViewController.COLUMN_INTERVAL_DP)
            * (ImageWaterFallViewController.COLUMN_COUNT + 1))
            / ImageWaterFallViewController.COLUMN_COUNT;
    private final LinearLayout footerView;
    private int nextFavoriteID = 0;
    private final FavoriteAgent favoriteAgent = new FavoriteAgent();

    public FavoriteViewController(Context context) {
        super(context, ImageWaterFallViewController.COLUMN_COUNT, BSActivity.dip2px(ImageWaterFallViewController.COLUMN_INTERVAL_DP));
        footerView = new LinearLayout(getContext());
        footerView.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();

        View loadmoreView = LayoutInflater.from(getContext()).inflate(R.layout.loadmore, null);
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

        BSApplication.defaultNotificationCenter.addObserver(this, NotificationEvent.FAVORITE_CHANGED, new Observer() {
            @Override
            public void update(Observable observable, Object data) {

            }
        });
        loadMore();
    }

    private void loadMore() {
        favoriteAgent.favoriteQuery(10, nextFavoriteID, new FavoriteQueryListener() {
            @Override
            public void finished(ArrayList<CategoryItemData> itemList) {

            }
        });
    }

    @Override
    protected void destroy() {
        favoriteAgent.cancel();
        super.destroy();
    }

}
