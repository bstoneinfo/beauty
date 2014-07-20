package com.bstoneinfo.lib.widget;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bstoneinfo.lib.common.BSImageLoader;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoaderListener;

public abstract class BSImageCell extends BSViewCell {

    protected final ImageView imageView;
    protected final int defaultImageResId;
    private BSImageLoader imageLoader;

    public BSImageCell(View rootView, ImageView imageView, int defaultImageResId) {
        super(rootView);
        this.imageView = imageView;
        this.defaultImageResId = defaultImageResId;
    }

    abstract protected String getImageUrl();

    @Override
    public void loadContent(Object data) {
        if (imageLoader != null) {
            imageLoader.cancel();
        }
        String imageUrl = getImageUrl();
        imageLoader = new BSImageLoader();
        Bitmap bitmap = imageLoader.getBitampFromMemoryCache(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageBitmapDidSet();
        } else {
            imageView.setImageResource(defaultImageResId);
            asyncLoadWillStart();
            imageLoader.loadImage(imageUrl, new BSImageLoaderListener() {
                @Override
                public void finished(Bitmap bitmap) {
                    imageLoader = null;
                    asyncLoadDidFinish();
                    imageView.setImageBitmap(bitmap);
                    imageBitmapDidSet();
                }

                @Override
                public void failed(Throwable throwable) {
                    imageLoader = null;
                    asyncLoadDidFail();
                }
            });
        }
    }

    protected void asyncLoadWillStart() {
    }

    protected void asyncLoadDidFinish() {
    }

    protected void asyncLoadDidFail() {
    }

    protected void imageBitmapDidSet() {
    }

    public void destroy() {
        if (imageLoader != null) {
            imageLoader.cancel();
        }
    }

}
