package com.example.jasonutil.util;

import android.util.Log;

/**
 * @author jsaon
 * @date 2019/2/28
 * 日志管理
 */
public class LogUtil {
    public static final String Jason="jason-";
    private static boolean isDebug=false;
    public static void setIsDebug(boolean isdebug){
        isDebug=isdebug;
    }

    //打印日志
    public static void Log(String clazzName, String s) {
        if (!isDebug) return;
        String name = getFunctionName();
        if (name != null) {
            Log.e(clazzName, name + " - " + s);
        } else {
            Log.e(clazzName, s);
        }
    }

    //打印日志
    public static void Log(String s) {
        if (!isDebug) return;
        String name = getFunctionName();
        if (name != null) {
            Log.e(Jason, name + " - " + s);
        } else {
            Log.e(Jason, s);
        }
    }


    //打印日志
    public static void SocketLog(String s) {
        if (!isDebug) return;
        String name = getFunctionName();
        if (name != null) {
            Log.e(Jason+"Socket：", name + " - " + s);
        } else {
            Log.e(Jason+"Socket：", s);
        }
    }

    //打印日志
    public static void UMLog(String s) {
        if (!isDebug) return;
        String name = getFunctionName();
        if (name != null) {
            Log.e(Jason+"UM：", name + " - " + s);
        } else {
            Log.e(Jason+"UM：", s);
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
            if (st.getClassName().equals("com.example.jasonutil.util.LogUtil")) {
                continue;
            }
            return "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }
}
