package com.bstoneinfo.lib.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.bstoneinfo.lib.net.BSHttpUrlConnection;
import com.bstoneinfo.lib.net.BSHttpUrlConnection.ProgressListener;
import com.bstoneinfo.lib.net.BSHttpUrlConnectionQueue;
import com.bstoneinfo.lib.net.BSImageConnection;
import com.bstoneinfo.lib.net.BSImageConnection.BSImageConnectionListener;

public class BSImageLoader {

    public enum BSImageLoadStatus {
        INIT,
        LOCAL_LOADING,
        REMOTE_LOADING,
        LOADED,
        FAILED,
        CANCELED
    }

    public interface BSImageLoaderListener {
        public void finished(Bitmap bitmap);

        public void failed(Throwable throwable);
    }

    public interface StatusChangedListener {
        void statusChanged(BSImageLoadStatus status);
    }

    private static final LruCache<String, Bitmap> imageCache;
    private static final BSLooperThread loadThread;
    private StatusChangedListener statusChangedListener;
    private ProgressListener progressListener;
    private BSImageLoadStatus loadStatus = BSImageLoadStatus.INIT;

    static {
        int percent = BSApplication.getApplication().getRemoteConfig().optInt("ImageCachePercent", 15);
        if (percent <= 0) {
            imageCache = null;
        } else {
            int memorySize = Math.round(Runtime.getRuntime().maxMemory() / 1024 * percent / 100);
            imageCache = new LruCache<String, Bitmap>(memorySize) {

                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    int bitmapSize = getBitmapSize(bitmap) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                }

                private int getBitmapSize(Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }
        loadThread = new BSLooperThread("BSImageLoader Thread");
    }

    public static void clearMemoryCache() {
        if (imageCache != null) {
            synchronized (imageCache) {
                imageCache.evictAll();
            }
        }
    }

    private final ArrayList<BSHttpUrlConnection> connections = new ArrayList<BSHttpUrlConnection>();
    private BSHttpUrlConnectionQueue connectionQueue;

    public BSImageLoader() {
    }

    public void setConnectionQueue(BSHttpUrlConnectionQueue queue) {
        connectionQueue = queue;
    }

    public BSImageLoadStatus getImageLoadStatus() {
        return loadStatus;
    }

    public boolean isLoading() {
        return loadStatus == BSImageLoadStatus.LOCAL_LOADING || loadStatus == BSImageLoadStatus.REMOTE_LOADING;
    }

    private void setImageLoadStatus(BSImageLoadStatus status) {
        loadStatus = status;
        if (statusChangedListener != null) {
            statusChangedListener.statusChanged(status);
        }
    }

    public void setStatusChangedListener(StatusChangedListener statusChangedListener) {
        this.statusChangedListener = statusChangedListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void loadImage(final String imageUrl, final BSImageLoaderListener listener) {
        String localPath = getDiskPath(imageUrl);
        if (TextUtils.isEmpty(localPath)) {
            return;
        }
        if (getImageLoadStatus() != BSImageLoadStatus.INIT) {
            return;
        }
        final Handler handler = new Handler();
        if (new File(localPath).exists()) {
            setImageLoadStatus(BSImageLoadStatus.LOCAL_LOADING);
            loadBitampFromLocalFile(handler, localPath, listener);
        } else if (isHttpUrl(imageUrl)) {
            setImageLoadStatus(BSImageLoadStatus.REMOTE_LOADING);
            loadThread.run(new Runnable() {
                @Override
                public void run() {
                    BSImageConnection connection = new BSImageConnection(imageUrl);
                    connection.setConnectionQueue(connectionQueue);
                    connection.start(new BSImageConnectionListener() {
                        @Override
                        public void finished(String localPath) {
                            loadBitampFromLocalFile(handler, localPath, listener);
                        }

                        @Override
                        public void failed(Exception e) {
                            notifyFailed(handler, listener, e);
                        }
                    });
                }
            });
        } else {
            notifyFailed(handler, listener, new FileNotFoundException(imageUrl + " not found."));
        }
    }

    public void cancel() {
        for (BSHttpUrlConnection connection : connections) {
            connection.cancel();
        }
        connections.clear();
        setImageLoadStatus(BSImageLoadStatus.CANCELED);
    }

    public static Bitmap getBitampFromMemoryCache(String imageUrl) {
        if (imageCache == null) {
            return null;
        }
        synchronized (imageCache) {
            return imageCache.get(getKey(imageUrl));
        }
    }

    private void addBitampToMemoryCache(String imageUrl, Bitmap bitmap) {
        synchronized (imageCache) {
            imageCache.put(getKey(imageUrl), bitmap);
        }
    }

    private void loadBitampFromLocalFile(final Handler handler, final String localPath, final BSImageLoaderListener listener) {
        loadThread.run(new Runnable() {
            @Override
            public void run() {
                if (getImageLoadStatus() == BSImageLoadStatus.CANCELED) {
                    return;
                }
                InputStream stream = null;
                try {
                    stream = new FileInputStream(localPath);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream, null, null);
                    addBitampToMemoryCache("file://" + localPath, bitmap);
                    notifyFinished(handler, listener, bitmap);
                } catch (final Throwable e) {
                    notifyFailed(handler, listener, e);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            // do nothing here
                        }
                    }
                }
            }
        });
    }

    private void notifyFinished(Handler handler, final BSImageLoaderListener listener, final Bitmap bm) {
        if (isLoading()) {
            setImageLoadStatus(BSImageLoadStatus.LOADED);
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.finished(bm);
                    }
                });
            }
        }
    }

    private void notifyFailed(Handler handler, final BSImageLoaderListener listener, final Throwable e) {
        if (isLoading()) {
            setImageLoadStatus(BSImageLoadStatus.FAILED);
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.failed(e);
                    }
                });
            }
        }
    }

    private static boolean isHttpUrl(String imageUrl) {
        return imageUrl.startsWith("http://") || imageUrl.startsWith("https://");
    }

    private static boolean isFileUrl(String imageUrl) {
        return imageUrl.startsWith("file://");
    }

    public static String getDiskPath(String imageUrl) {
        if (isHttpUrl(imageUrl)) {
            return BSUtils.getCachePath(imageUrl);
        }
        if (isFileUrl(imageUrl)) {
            return imageUrl.substring(6);
        }
        return imageUrl;
    }

    private static String getKey(String imageUrl) {
        return getDiskPath(imageUrl);
    }

}
