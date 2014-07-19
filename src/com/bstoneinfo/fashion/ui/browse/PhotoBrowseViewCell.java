package com.bstoneinfo.fashion.ui.browse;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoadStatus;
import com.bstoneinfo.lib.common.BSImageLoader.StatusChangedListener;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.view.BSImageView;
import com.bstoneinfo.lib.widget.BSViewCell;

import custom.R;

public class PhotoBrowseViewCell extends BSViewCell {

    private BSImageView imageView;
    private ProgressBar loadMoreProgressBar;
    //    private BSCircularProgressBar circularProgressBar;
    private CategoryItemData itemData;

    public PhotoBrowseViewCell(Context context) {
        super(context, R.layout.photo_browse_cell);
        imageView = (BSImageView) getRootView().findViewById(R.id.imageView);
        loadMoreProgressBar = (ProgressBar) getRootView().findViewById(R.id.progressBar);
        //        circularProgressBar = (BSCircularProgressBar) getRootView().findViewById(R.id.circularBar);
    }

    @Override
    public void loadContent(Object data) {
        BSLog.e("position=" + position + " data=" + String.valueOf(data));
        itemData = (CategoryItemData) data;
        if (data == null) {
            loadMoreProgressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            loadMoreProgressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setStatusChangedListener(new StatusChangedListener() {
                @Override
                public void statusChanged(BSImageLoadStatus status) {
                    BSLog.e("position=" + position + " status=" + status);
                    if (status == BSImageLoadStatus.LOADED || status == BSImageLoadStatus.FAILED) {
                        loadStandardPhoto(itemData);
                    }
                }
            });
            imageView.setUrl(itemData.thumbURL);//加载本地的缩略图
        }
        //        circularProgressBar.setVisibility(View.GONE);
    }

    private void loadStandardPhoto(CategoryItemData itemData) {
        //        imageView.setProgressListener(new ProgressListener() {
        //            @Override
        //            public void progress(int downloadedBytes, int totalBytes) {
        //                if (totalBytes > 0) {
        //                    circularProgressBar.setSweepAngle((float) downloadedBytes / totalBytes * 360);
        //                }
        //            }
        //        });
        //        imageView.setStatusChangedListener(new StatusChangedListener() {
        //            @Override
        //            public void statusChanged(BSImageLoadStatus status) {
        //                BSLog.d("position=" + position + " status=" + status);
        //                if (status == BSImageLoadStatus.REMOTE_LOADING) {
        //                    circularProgressBar.setVisibility(View.VISIBLE);
        //                } else if (status == BSImageLoadStatus.LOADED) {
        //                    circularProgressBar.setVisibility(View.GONE);
        //                } else if (status == BSImageLoadStatus.FAILED) {
        //                    circularProgressBar.setVisibility(View.GONE);
        //                }
        //            }
        //        });
        imageView.setStatusChangedListener(null);
        imageView.setUrl(itemData.standardURL);
    }

}
