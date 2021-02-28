package com.snc.zero.log;

import android.util.Log;

/**
 * Log Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class Logger {
    private static String sPrefix = "";

    public static void setPrefix(String prefix) {
        sPrefix = prefix;
    }

    public static void d(String tag, Object...msg) {
        try {
            Log.d(tag, getMessage(msg));
        } catch (Exception e) {
            systemOut(getMessage(msg));
        }
    }

    public static void i(String tag, Object...msg) {
        try {
            Log.i(tag, getMessage(msg));
        } catch (Exception e) {
            systemOut(getMessage(msg));
        }
    }

    public static void v(String tag, Object...msg) {
        try {
            Log.v(tag, getMessage(msg));
        } catch (Exception e) {
            systemOut(getMessage(msg));
        }
    }

    public static void w(String tag, Object...msg) {
        try {
            Log.w(tag, getMessage(msg));
        } catch (Exception e) {
            systemOut(getMessage(msg));
        }
    }

    public static void e(String tag, Object...msg) {
        try {
            Log.e(tag, getMessage(msg));
        } catch (Exception e) {
            systemOut(getMessage(msg));
        }
    }

    private static void systemOut(String msg) {
        System.out.println(getMessage(msg));
    }

    private static String getMessage(Object...msg) {
        StringBuilder sb = new StringBuilder();
        for (Object o : msg) {
            sb.append(getMessage(o)).append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String getMessage(Object msg) {
        if (null == msg) {
            return "";
        }
        if (msg instanceof Throwable) {
            return Log.getStackTraceString((Throwable) msg);
        }
        return sPrefix + msg.toString();
    }

}
