package com.bstoneinfo.lib.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSUtils;

public class BSImageConnection extends BSHttpUrlConnection {

    public interface BSImageConnectionListener {
        void finished(String localPath);

        void failed(Exception exception);
    }

    private String localPath;

    public BSImageConnection(String url) {
        super(url);
        localPath = BSUtils.getCachePath(url);
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    protected boolean equals(BSHttpUrlConnection connection) {
        return TextUtils.equals(url, connection.url);
    }

    public void start(final BSImageConnectionListener listener) {
        start(new BSHttpUrlConnectionListener() {

            @Override
            public void finished(byte[] response) {
                String localPath;
                if (TextUtils.isEmpty(BSImageConnection.this.localPath)) {
                    localPath = BSUtils.getCachePath(url);
                } else {
                    localPath = BSImageConnection.this.localPath;
                }
                FileOutputStream fOut = null;
                try {
                    new File(localPath).getParentFile().mkdirs();
                    fOut = new FileOutputStream(localPath, false);
                    fOut.write(response);
                    fOut.flush();
                    fOut.close();
                    if (listener != null) {
                        listener.finished(localPath);
                    }
                } catch (Exception e) {
                    if (fOut != null) {
                        try {
                            fOut.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (listener != null) {
                        listener.failed(e);
                    }
                }
            }

            @Override
            public void failed(final Exception exception) {
                if (listener != null) {
                    listener.failed(exception);
                }
            }
        });
    }

}
