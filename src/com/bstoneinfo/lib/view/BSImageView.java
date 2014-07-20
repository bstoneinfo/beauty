package com.bstoneinfo.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bstoneinfo.lib.common.BSImageLoader;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoadStatus;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoaderListener;
import com.bstoneinfo.lib.common.BSImageLoader.StatusChangedListener;
import com.bstoneinfo.lib.net.BSHttpUrlConnection.ProgressListener;
import com.bstoneinfo.lib.net.BSHttpUrlConnectionQueue;

public class BSImageView extends ImageView {

    private String url;
    private BSHttpUrlConnectionQueue connectionQueue;
    private StatusChangedListener statusChangedListener;
    private ProgressListener progressListener;
    private BSImageLoadStatus imageLoadStatus = BSImageLoadStatus.INIT;
    private BSImageLoader imageLoader;
    private boolean bVisible = true;

    public BSImageView(Context context) {
        super(context);
    }

    public BSImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BSImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setConnectionQueue(BSHttpUrlConnectionQueue connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setStatusChangedListener(StatusChangedListener listener) {
        statusChangedListener = listener;
    }

    public BSImageLoadStatus getImageLoadStatus() {
        return imageLoadStatus;
    }

    public boolean isLoading() {
        return imageLoadStatus == BSImageLoadStatus.LOCAL_LOADING || imageLoadStatus == BSImageLoadStatus.REMOTE_LOADING;
    }

    private void setImageLoadStatus(BSImageLoadStatus status) {
        this.imageLoadStatus = status;
        if (statusChangedListener != null) {
            statusChangedListener.statusChanged(status);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (bVisible) {
            String localPath = BSImageLoader.getDiskPath(url);
            Bitmap bitmap = BSImageLoader.getBitampFromMemoryCache(localPath);
            if (bitmap != null) {
                if (imageLoader != null) {
                    imageLoader.cancel();
                    imageLoader = null;
                }
                setImageBitmap(bitmap);
                setImageLoadStatus(BSImageLoadStatus.LOADED);
            } else {
                if (imageLoader != null) {
                    if (imageLoader.isLoading()) {
                        if (TextUtils.equals(this.url, url)) {
                            return;
                        }
                        imageLoader.cancel();
                    }
                }
                imageLoader = new BSImageLoader();
                imageLoader.setStatusChangedListener(new StatusChangedListener() {
                    @Override
                    public void statusChanged(BSImageLoadStatus status) {
                        setImageLoadStatus(status);
                    }
                });
                imageLoader.setProgressListener(progressListener);
                //            imageLoader.setConnectionQueue(connectionQueue);
                imageLoader.loadImage(url, new BSImageLoaderListener() {
                    @Override
                    public void finished(Bitmap bitmap) {
                        setImageBitmap(bitmap);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                    }
                });
            }
        }
        this.url = url;
    }

    public void setVisible(boolean bVisible) {
        if (this.bVisible == bVisible) {
            return;
        }
        this.bVisible = bVisible;
        if (!bVisible) {
            if (imageLoader != null) {
                imageLoader.cancel();
                imageLoader = null;
            }
            setImageBitmap(null);
        } else {
            setUrl(url);
        }
    }
}
