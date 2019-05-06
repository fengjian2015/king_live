
package com.example.jasonutil.util;

import android.content.Context;

/**
 * 单位计算
 */
public class ScreenTools {

    public static int dip2px(Context context,float f) {
        return (int) (0.5D + (double) (f * getDensity(context)));
    }

    public static int dip2px(Context context,int i) {
        return (int) (0.5D + (double) (getDensity(context) * (float) i));
    }

    public static int get480Height(Context context,int i) {
        return (i * getScreenWidth(context)) / 480;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getScal(Context context) {
        return (100 * getScreenWidth(context)) / 480;
    }

    public static int getScreenDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    public static float getXdpi(Context context) {
        return context.getResources().getDisplayMetrics().xdpi;
    }

    public static float getYdpi(Context context) {
        return context.getResources().getDisplayMetrics().ydpi;
    }

    public static int px2dip(Context context,float f) {
        float f1 = getDensity(context);
        return (int) (((double) f - 0.5D) / (double) f1);
    }

    public static int px2dip(Context context,int i) {
        float f = getDensity(context);
        return (int) (((double) i - 0.5D) / (double) f);
    }

    public static int getStateBar3(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
