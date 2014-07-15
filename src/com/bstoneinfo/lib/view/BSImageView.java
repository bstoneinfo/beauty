package com.bstoneinfo.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bstoneinfo.lib.common.BSImageLoader;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoaderListener;
import com.bstoneinfo.lib.net.BSHttpUrlConnectionQueue;

public class BSImageView extends ImageView {

    private String url;
    private BSHttpUrlConnectionQueue connectionQueue;

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

    public void setUrl(String url) {
        this.url = url;
        BSImageLoader imageLoader = new BSImageLoader();
        String localPath = imageLoader.getDiskPath(url);
        Bitmap bitmap = imageLoader.getBitampFromMemoryCache("file://" + localPath);
        if (bitmap != null) {
            setImageBitmap(bitmap);
        } else {
            imageLoader.loadImage(url, new BSImageLoaderListener() {
                @Override
                public void finished(Bitmap bitmap) {
                    setImageBitmap(bitmap);
                }

                @Override
                public void failed(Throwable throwable) {
                    setImageBitmap(null);
                }
            });
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

}
