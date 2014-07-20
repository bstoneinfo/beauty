package com.bstoneinfo.lib.common;

import android.text.TextUtils;
import android.util.Log;

public class BSLog {

    public static void d(String tag, String msg) {
        log(Log.ERROR, tag, msg);
    }

    public static void d(String msg) {
        log(Log.ERROR, null, msg);
    }

    public static void e(String tag, String msg) {
        log(Log.ERROR, tag, msg);
    }

    public static void e(String msg) {
        log(Log.ERROR, null, msg);
    }

    public static void i(String tag, String msg) {
        log(Log.INFO, tag, msg);
    }

    public static void i(String msg) {
        log(Log.INFO, null, msg);
    }

    public static void v(String tag, String msg) {
        log(Log.VERBOSE, tag, msg);
    }

    public static void v(String msg) {
        log(Log.VERBOSE, null, msg);
    }

    public static void w(String tag, String msg) {
        log(Log.WARN, tag, msg);
    }

    public static void w(String msg) {
        log(Log.WARN, null, msg);
    }

    private static void log(int priority, String tag, String msg) {
        if (!BSUtils.isDebug()) {
            return;
        }
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 2;
        final StackTraceElement ste = stack[i];
        String[] className = ste.getClassName().split("\\.");
        if (TextUtils.isEmpty(tag)) {
            tag = className[className.length - 1];
        } else {
            tag = tag + "-" + className[className.length - 1];
        }
        msg = String.format("%s(Line:%s) - %s", ste.getMethodName(), ste.getLineNumber(), msg);
        Log.println(priority, tag, msg);
    }
}
