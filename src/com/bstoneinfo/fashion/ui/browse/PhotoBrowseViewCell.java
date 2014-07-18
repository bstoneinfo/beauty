package com.bstoneinfo.fashion.ui.browse;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.common.BSImageLoader;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoaderListener;
import com.bstoneinfo.lib.view.BSCircularProgressBar;
import com.bstoneinfo.lib.widget.BSViewCell;

import custom.R;

public class PhotoBrowseViewCell extends BSViewCell {

    private ImageView imageView;
    private ProgressBar loadMoreProgressBar;
    private BSCircularProgressBar circularProgressBar;

    public PhotoBrowseViewCell(Context context) {
        super(context, R.layout.photo_browse_cell);
        imageView = (ImageView) getRootView().findViewById(R.id.imageView);
        loadMoreProgressBar = (ProgressBar) getRootView().findViewById(R.id.progressBar);
        circularProgressBar = (BSCircularProgressBar) getRootView().findViewById(R.id.circularBar);
    }

    @Override
    public void loadContent(Object data) {
        CategoryItemData itemData = (CategoryItemData) data;
        BSImageLoader imageLoader = new BSImageLoader();
        Bitmap bitmap = imageLoader.getBitampFromMemoryCache(itemData.thumbURL);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageLoader.loadImage(itemData.standardURL, new BSImageLoaderListener() {
                @Override
                public void finished(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void failed(Throwable throwable) {
                }
            });
        }

    }

}
