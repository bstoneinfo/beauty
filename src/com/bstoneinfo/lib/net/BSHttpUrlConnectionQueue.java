package com.bstoneinfo.lib.net;

import java.util.LinkedList;

import com.bstoneinfo.lib.net.BSHttpUrlConnection.BSHttpUrlConnectionListener;

public class BSHttpUrlConnectionQueue {

    private final LinkedList<BSHttpUrlConnection> runningConnections = new LinkedList<BSHttpUrlConnection>();
    private final LinkedList<BSHttpUrlConnection> waitingConnections = new LinkedList<BSHttpUrlConnection>();
    private final LinkedList<BSHttpUrlConnectionListener> waitingListeners = new LinkedList<BSHttpUrlConnectionListener>();
    private final int queueSize;

    public BSHttpUrlConnectionQueue(int size) {
        queueSize = size;
    }

    public void clear() {
        for (BSHttpUrlConnection connection : runningConnections) {
            connection.cancel();
        }
        runningConnections.clear();
        waitingConnections.clear();
    }

    void add(BSHttpUrlConnection connection, BSHttpUrlConnectionListener listener) {
        synchronized (runningConnections) {
            for (BSHttpUrlConnection runningConnection : runningConnections) {
                if (runningConnection.equals(connection)) {
                    connection.start(listener, runningConnection);
                    return;
                }
            }
            if (runningConnections.size() < queueSize) {
                start(connection, listener);
            } else {
                waitingConnections.add(connection);
                waitingListeners.add(listener);
            }
        }
    }

    private void start(final BSHttpUrlConnection connection, final BSHttpUrlConnectionListener listener) {
        runningConnections.add(connection);
        connection.start(new BSHttpUrlConnectionListener() {

            @Override
            public void finished(byte[] response) {
                if (listener != null) {
                    listener.finished(response);
                }
                runNext();
            }

            @Override
            public void failed(Exception exception) {
                if (listener != null) {
                    listener.failed(exception);
                }
                runNext();
            }

            private void runNext() {
                synchronized (runningConnections) {
                    runningConnections.remove(connection);
                    if (!waitingConnections.isEmpty()) {
                        start(waitingConnections.removeFirst(), waitingListeners.removeFirst());
                    }
                }
            }
        });
    }

}
