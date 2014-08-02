package com.bstoneinfo.fashion.ui.browse;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.favorite.FavoriteManager;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoadStatus;
import com.bstoneinfo.lib.common.BSImageLoader.StatusChangedListener;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.view.BSImageView;
import com.bstoneinfo.lib.widget.BSViewCell;

import custom.R;

public class PhotoBrowseViewCell extends BSViewCell {

    private BSImageView imageView;
    private ProgressBar progressBar;
    ImageView refreshView;
    private ImageView favoriteView;
    private CategoryItemData itemData;

    public PhotoBrowseViewCell(Context context) {
        super(context, R.layout.photo_browse_cell);
        imageView = (BSImageView) getRootView().findViewById(R.id.imageView);
        refreshView = (ImageView) getRootView().findViewById(R.id.refresh);
        progressBar = (ProgressBar) getRootView().findViewById(R.id.progressBar);
        favoriteView = (ImageView) getRootView().findViewById(R.id.favorite);
        BSApplication.defaultNotificationCenter.addObserver(this, NotificationEvent.CATEGORY_ITEM_DATA_FINISHED, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                setFavoriteView();
            }
        });
    }

    @Override
    public void loadContent(Object data) {
        BSLog.e("position=" + position + " data=" + String.valueOf(data));
        itemData = (CategoryItemData) data;
        if (data == null) {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            refreshView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            if (TextUtils.isEmpty(itemData.standardURL)) {
                refreshView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            } else {
                refreshView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setStatusChangedListener(new StatusChangedListener() {
                    @Override
                    public void statusChanged(BSImageLoadStatus status) {
                        BSLog.e("position=" + position + " status=" + status);
                        if (status == BSImageLoadStatus.REMOTE_LOADING) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        if (status == BSImageLoadStatus.LOADED || status == BSImageLoadStatus.FAILED) {
                            progressBar.setVisibility(View.GONE);
                            if (status == BSImageLoadStatus.FAILED) {
                                imageView.setStatusChangedListener(new StatusChangedListener() {
                                    @Override
                                    public void statusChanged(BSImageLoadStatus status) {
                                        BSLog.d("position=" + position + " status=" + status);
                                        if (status == BSImageLoadStatus.REMOTE_LOADING) {
                                            progressBar.setVisibility(View.VISIBLE);
                                        } else if (status == BSImageLoadStatus.LOADED) {
                                            progressBar.setVisibility(View.GONE);
                                        } else if (status == BSImageLoadStatus.FAILED) {
                                            progressBar.setVisibility(View.GONE);
                                            refreshView.setVisibility(View.VISIBLE);
                                            refreshView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    refreshView.setVisibility(View.GONE);
                                                    loadStandardPhoto(itemData);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                imageView.setStatusChangedListener(null);
                            }
                            loadStandardPhoto(itemData);
                        }
                    }
                });
                imageView.setUrl("http://" + MyUtils.getHost() + itemData.thumbURL);//加载本地的缩略图
                setFavoriteView();
                favoriteView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FavoriteManager.getInstance().isFavorite(itemData)) {
                            FavoriteManager.getInstance().favoriteRemove(itemData);
                        } else {
                            FavoriteManager.getInstance().favoriteAdd(itemData);

                        }
                    }
                });
            }
        }
    }

    private void setFavoriteView() {
        if (FavoriteManager.getInstance().isFavorite(itemData)) {
            favoriteView.setBackgroundResource(R.drawable.heart_red);
        } else {
            favoriteView.setBackgroundResource(R.drawable.heart_grey);
        }
    }

    private void loadStandardPhoto(final CategoryItemData itemData) {
        imageView.setUrl("http://" + MyUtils.getHost() + itemData.standardURL);
    }

    @Override
    public void destory() {
        BSApplication.defaultNotificationCenter.removeObservers(this);
        super.destory();
    }
}
