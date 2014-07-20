package com.bstoneinfo.lib.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;

public class BSHttpUrlConnection {

    public enum ConnectionMethod {
        GET,
        POST
    }

    public enum ConnectionStatus {
        Init,
        Running,
        Finished,
        Failed,
        Canceled
    }

    public interface BSHttpUrlConnectionListener {
        void finished(byte[] response);

        void failed(Exception exception);
    }

    public interface ProgressListener {
        void progress(int downloadedBytes, int totalBytes);
    }

    protected final String url;
    private ConnectionStatus connectionStatus = ConnectionStatus.Init;
    private ConnectionMethod requestMethod = ConnectionMethod.GET;
    private final HashMap<String, String> parameters = new HashMap<String, String>();
    private final HashMap<String, String> properties = new HashMap<String, String>();
    private final ArrayList<BSHttpUrlConnection> equalConnections = new ArrayList<BSHttpUrlConnection>();
    private BSHttpUrlConnectionQueue connectionQueue;
    private BSHttpUrlConnectionListener conectionListener;
    private ProgressListener progressListener;
    private Handler handler;

    public BSHttpUrlConnection(String url) {
        this.url = url;
    }

    protected boolean equals(BSHttpUrlConnection connection) {
        return false;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setRequestMethod(ConnectionMethod method) {
        requestMethod = method;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public void setConnectionQueue(BSHttpUrlConnectionQueue queue) {
        connectionQueue = queue;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void start(BSHttpUrlConnectionListener listener) {
        handler = new Handler();
        if (connectionStatus == ConnectionStatus.Init) {
            if (connectionQueue != null) {
                connectionQueue.add(this, listener);
            } else {
                start(listener, this);
            }
            connectionStatus = ConnectionStatus.Running;
        }
    }

    void start(final BSHttpUrlConnectionListener listener, BSHttpUrlConnection entityConnection) {
        if (connectionStatus != ConnectionStatus.Init) {
            return;
        }
        this.conectionListener = new BSHttpUrlConnectionListener() {

            @Override
            public void finished(final byte[] response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionStatus == ConnectionStatus.Running) {
                            connectionStatus = ConnectionStatus.Finished;
                            listener.finished(response);
                        }
                    }
                });
            }

            @Override
            public void failed(final Exception exception) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionStatus == ConnectionStatus.Running) {
                            connectionStatus = ConnectionStatus.Failed;
                            listener.failed(exception);
                        }
                    }
                });
            }
        };
        entityConnection.run(this);
    }

    private void notifyProgress(final int downloadedBytes, final int totalBytes) {
        if (progressListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressListener.progress(downloadedBytes, totalBytes);
                }
            });
        }
    }

    private void run(BSHttpUrlConnection equalConnection) {
        equalConnections.add(equalConnection);
        if (equalConnections.size() == 1) {
            new Thread() {
                @Override
                public void run() {
                    String urlString = url;
                    if (requestMethod == ConnectionMethod.GET && parameters != null) {
                        StringBuffer param = new StringBuffer();
                        int i = 0;
                        for (String key : parameters.keySet()) {
                            if (i == 0) {
                                param.append("?");
                            } else {
                                param.append("&");
                            }
                            param.append(key).append("=").append(parameters.get(key));
                            i++;
                        }
                        urlString += param;
                    }

                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        urlConnection.setRequestMethod(requestMethod.toString());
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setUseCaches(false);

                        if (properties != null) {
                            for (String key : properties.keySet()) {
                                urlConnection.addRequestProperty(key, properties.get(key));
                            }
                        }

                        if (requestMethod == ConnectionMethod.POST && parameters != null) {
                            StringBuffer param = new StringBuffer();
                            for (String key : parameters.keySet()) {
                                param.append("&");
                                param.append(key).append("=").append(parameters.get(key));
                            }
                            urlConnection.getOutputStream().write(param.toString().getBytes());
                            urlConnection.getOutputStream().flush();
                            urlConnection.getOutputStream().close();
                        }

                        final ByteArrayOutputStream os = new ByteArrayOutputStream();
                        BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedOutputStream bos = new BufferedOutputStream(os);
                        byte[] buffer = new byte[1024 * 16]; //创建存放输入流的缓冲 

                        int totalBytes = 0;
                        try {
                            totalBytes = Integer.parseInt(urlConnection.getHeaderField("Content-Length"));
                        } catch (Exception e) {
                        }
                        int readBytes = 0;
                        for (BSHttpUrlConnection connection : equalConnections) {
                            connection.notifyProgress(readBytes, totalBytes);
                        }
                        int num = -1; //读入的字节数 
                        while (true) {
                            boolean bAllCanceled = true;
                            for (BSHttpUrlConnection connection : equalConnections) {
                                if (connection.connectionStatus == ConnectionStatus.Running) {
                                    bAllCanceled = false;
                                    break;
                                }
                            }
                            if (bAllCanceled) {
                                break;
                            }
                            num = bis.read(buffer); // 读入到缓冲区
                            if (num == -1) {
                                bos.flush();
                                break; //已经读完 
                            }
                            bos.flush();
                            bos.write(buffer, 0, num);
                            readBytes += num;
                            for (BSHttpUrlConnection connection : equalConnections) {
                                connection.notifyProgress(readBytes, totalBytes);
                            }
                        }
                        bos.close();
                        bis.close();
                        for (BSHttpUrlConnection connection : equalConnections) {
                            if (connection.conectionListener != null) {
                                connection.conectionListener.finished(os.toByteArray());
                            }
                        }
                    } catch (final Exception e) {
                        for (BSHttpUrlConnection connection : equalConnections) {
                            if (connection.conectionListener != null) {
                                connection.conectionListener.failed(e);
                            }
                        }
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }

                    if (connectionQueue != null) {
                        connectionQueue.runNext(BSHttpUrlConnection.this);
                    }
                }
            }.start();
        }
    }

    public void cancel() {
        if (connectionStatus == ConnectionStatus.Running) {
            connectionStatus = ConnectionStatus.Canceled;
        }
    }
}
