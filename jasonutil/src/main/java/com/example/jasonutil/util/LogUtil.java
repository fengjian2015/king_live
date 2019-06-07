package com.example.jasonutil.util;

import android.util.Log;

/**
 * @author jsaon
 * @date 2019/2/28
 * 日志管理
 */
public class LogUtil {
    public static final String JASON="jason-";
    private static boolean isDebug=false;
    public static void setIsDebug(boolean mIsDebug){
        isDebug=mIsDebug;
    }

    /**
     * 打印日志
     * @param clazzName
     * @param s
     */
    public static void log(String clazzName, String s) {
        if (!isDebug) {
            return;
        }
        String name = getFunctionName();
        if (name != null) {
            Log.e(clazzName, name + " - " + s);
        } else {
            Log.e(clazzName, s);
        }
    }

    /**
     * 打印日志
     * @param s
     */
    public static void log(String s) {
        if (!isDebug) {
            return;
        }
        String name = getFunctionName();
        if (name != null) {
            Log.e(JASON, name + " - " + s);
        } else {
            Log.e(JASON, s);
        }
    }


    /**
     * 打印日志
     * @param s
     */
    public static void socketLog(String s) {
        if (!isDebug) {
            return;
        }
        String name = getFunctionName();
        if (name != null) {
            Log.e(JASON+"Socket：", name + " - " + s);
        } else {
            Log.e(JASON+"Socket：", s);
        }
    }

    /**
     * 打印日志
     * @param s
     */
    public static void umlog(String s) {
        if (!isDebug) {
            return;
        }
        String name = getFunctionName();
        if (name != null) {
            Log.e(JASON+"UM：", name + " - " + s);
        } else {
            Log.e(JASON+"UM：", s);
        }
    }

    /**
     * Get The Current Function Name
     *
     * @return
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if ("com.example.jasonutil.util.LogUtil".equals(st.getClassName())) {
                continue;
            }
            return "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }
}
