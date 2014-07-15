package com.bstoneinfo.lib.net;

import org.json.JSONException;
import org.json.JSONObject;

public class BSJsonConnection extends BSHttpUrlConnection {

    public interface BSJsonConnectionListener {
        void finished(JSONObject jsonObject);

        void failed(Exception exception);
    }

    public BSJsonConnection(String url) {
        super(url);
    }

    public void start(final BSJsonConnectionListener listener) {
        start(new BSHttpUrlConnectionListener() {

            @Override
            public void finished(byte[] response) {
                if (listener != null) {
                    String content = new String(response);
                    JSONObject json;
                    try {
                        json = new JSONObject(content);
                    } catch (JSONException e) {
                        listener.failed(e);
                        return;
                    }
                    listener.finished(json);
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
