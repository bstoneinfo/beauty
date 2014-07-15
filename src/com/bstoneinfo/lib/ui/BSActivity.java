package com.bstoneinfo.lib.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent;

public abstract class BSActivity extends Activity {

    private BSViewController mainViewController;
    private static DisplayMetrics displayMetrics;
    private ArrayList<Dialog> autoDestroyDialogs = new ArrayList<Dialog>();
    final ArrayList<BSViewController> presentViewControllers = new ArrayList<BSViewController>();
    ViewGroup mainView;

    public static DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * displayMetrics.density + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / displayMetrics.density + 0.5f);
    }

    public BSViewController getMainViewController() {
        return mainViewController;
    }

    public void setMainViewController(BSViewController viewController) {
        mainViewController = viewController;
        mainView.addView(mainViewController.getRootView());
        mainViewController.viewDidLoad();
        mainViewController.viewWillAppear();
        mainViewController.asyncRun(new Runnable() {
            @Override
            public void run() {
                mainViewController.viewDidAppear();
            }
        });
    }

    private void initDisplayMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDisplayMetrics();
        mainView = new FrameLayout(this);
        setContentView(mainView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDisplayMetrics();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BSApplication.defaultNotificationCenter.notifyOnUIThread(BSNotificationEvent.APP_ENTER_FOREGROUND);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BSApplication.defaultNotificationCenter.notifyOnUIThread(BSNotificationEvent.APP_ENTER_BACKGROUND);
        if (isFinishing()) {// 关闭附属于本Activity的Dialog
            autoDestroyDialogs();
        }
    }

    @Override
    protected void onDestroy() {
        mainViewController.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!presentViewControllers.isEmpty()) {
            BSViewController presentViewController = presentViewControllers.get(presentViewControllers.size() - 1);
            if (!presentViewController.back()) {
                presentViewController.dismiss();
            }
        } else if (!mainViewController.back()) {
            super.onBackPressed();
        }
    }

    @Override
    final public void finish() {
        super.finish();
    }

    void addPresentViewController(BSViewController presentViewController) {
        mainView.addView(presentViewController.getRootView());
        presentViewControllers.add(presentViewController);
    }

    void removePresentViewController(BSViewController presentViewController) {
        mainView.removeView(presentViewController.getRootView());
        presentViewControllers.remove(presentViewController);
    }

    public void autoDestroyDialogs() {
        for (Dialog dialog : autoDestroyDialogs) {
            dialog.dismiss();
        }
        autoDestroyDialogs.clear();
    }

    public void addToAutoDestroyDialogList(AlertDialog alertDialog) {
        if (alertDialog != null) {
            autoDestroyDialogs.add(alertDialog);
        }
    }

    public void removeFromAutoDestroyDialogList(AlertDialog alertDialog) {
        autoDestroyDialogs.remove(alertDialog);
    }

    /*
     * 显示信息框
     */
    public AlertDialog alert(int titleResId, int alertTextResId, int buttonTextResId, final Runnable callback) {
        return alert(titleResId == 0 ? null : getString(titleResId), getString(alertTextResId), getString(buttonTextResId), callback);
    }

    public AlertDialog alert(String title, String text, String buttonText, final Runnable callback) {

        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(text);

        builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (callback != null) {
                    callback.run();
                }
            }
        });

        AlertDialog alert = builder.create();
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        autoDestroyDialogs.add(alert);
        alert.show();
        return alert;
    }

    public AlertDialog confirm(int titleResId, int alertTextResId, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2,
            final OnCancelListener onCancelListener) {
        return confirm(titleResId, getString(alertTextResId), btnTxtResId1, btnTxtResId2, btnCallback1, btnCallback2, onCancelListener);
    }

    public AlertDialog confirm(final int titleResId, String alertText, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2,
            final OnCancelListener onCancelListener) {

        AlertDialog.Builder builder = new Builder(this);
        if (titleResId != 0) {
            builder.setTitle(titleResId);
        }
        builder.setMessage(alertText);

        builder.setPositiveButton(btnTxtResId1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (btnCallback1 != null) {
                    btnCallback1.run();
                }
            }
        });

        builder.setNegativeButton(btnTxtResId2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (btnCallback2 != null) {
                    btnCallback2.run();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onCancel(dialog);
                }
            }
        });
        autoDestroyDialogs.add(alert);
        alert.show();
        return alert;
    }
}
