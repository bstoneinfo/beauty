package com.bstoneinfo.lib.common;

import android.os.Handler;
import android.os.HandlerThread;

public class BSLooperThread extends HandlerThread {

    private Handler handler;
    private boolean released = false;

    public BSLooperThread(String name) {
        super(name);
        start();
        handler = new Handler(getLooper());
    }

    @Override
    public boolean quit() {
        released = true;
        untilDone(new Runnable() {
            @Override
            public void run() {

            }
        });
        return super.quit();
    }

    public void run(Runnable runnable) {
        if (Thread.currentThread() == this) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }

    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    public void wait(final Runnable action) {
        untilDone(new Runnable() {
            @Override
            public void run() {
                if (!released && action != null) {
                    action.run();
                }
            }
        });
    }

    public void disable(final Runnable runnable) {
        released = true;
        untilDone(runnable);
    }

    public void enable(final Runnable action) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.run();
                }
                released = false;
            }
        };
        untilDone(runnable);
    }

    private void untilDone(final Runnable action) {
        if (Thread.currentThread() == this) {
            if (action != null) {
                action.run();
            }
            return;
        }
        final Object mLock = new Object();
        synchronized (mLock) {
            try {
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (action != null) {
                            action.run();
                        }
                        synchronized (mLock) {
                            mLock.notify();
                        }
                    }
                };
                handler.post(myRunnable);
                mLock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
