package com.wewin.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import com.alivc.player.VcPlayerLog;


/**
 * 屏幕方向监听类
 */
public class OrientationWatchDog {
    private static final String TAG = OrientationWatchDog.class.getSimpleName();

    private Context mContext;
    //系统的屏幕方向改变监听
    private OrientationEventListener mLandOrientationListener;
    //对外的设置的监听
    private OnOrientationListener mOrientationListener;
    //上次屏幕的方向
    private Orientation mLastOrientation = Orientation.Port;

    /**
     * 屏幕方向
     */
    private enum Orientation {
        /**
         * 竖屏
         */
        Port,
        /**
         * 横屏
         */
        Land
    }


    public OrientationWatchDog(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 开始监听
     */
    public void startWatch() {
        if (mLandOrientationListener == null) {
            mLandOrientationListener = new OrientationEventListener(mContext,
                    SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int orientation) {
                    //这里的|| 和&& 不能弄错！！
                    //根据手机的方向角度计算。在90和180度上下10度的时候认为横屏了。
                    //竖屏类似。
                    boolean isLand = (orientation < 100 && orientation > 80)
                            || (orientation < 280 && orientation > 260);

                    boolean isPort = (orientation < 10 || orientation > 350)
                            || (orientation < 190 && orientation > 170);

                    if (isLand) {
                        if (mOrientationListener != null) {
                            VcPlayerLog.d(TAG, "ToLand");
                            mOrientationListener.changedToLandScape(mLastOrientation == Orientation.Port);
                        }
                        mLastOrientation = Orientation.Land;
                    } else if (isPort) {
                        if (mOrientationListener != null) {
                            VcPlayerLog.d(TAG, "ToPort");
                            mOrientationListener.changedToPortrait(mLastOrientation == Orientation.Land);
                        }
                        mLastOrientation = Orientation.Port;
                    }

                }
            };
        }

        mLandOrientationListener.enable();
    }

    /**
     * 结束监听
     */
    public void stopWatch() {
        VcPlayerLog.e(TAG, "stopWatch");
        if (mLandOrientationListener != null) {
            mLandOrientationListener.disable();
        }
    }

    /**
     * 销毁监听
     */
    public void destroy() {
        VcPlayerLog.e(TAG, "onDestroy");
        stopWatch();
        mLandOrientationListener = null;
    }

    /**
     * 屏幕方向变化事件
     */
    public interface OnOrientationListener {
        /**
         * 变为Land
         *
         * @param fromPort 是否是从竖屏变过来的
         */
        void changedToLandScape(boolean fromPort);

        /**
         * 变为Port
         *
         * @param fromLand 是否是从横屏变过来的
         */
        void changedToPortrait(boolean fromLand);
    }

    /**
     * 设置屏幕方向变化事件
     *
     * @param l 事件监听
     */
    public void setOnOrientationListener(OnOrientationListener l) {
        mOrientationListener = l;
    }

    /**
     * 是不是横屏
     *
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context) {
        Configuration newConfig = context.getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

    /**
     * 是否是竖屏
     *
     * @param context
     * @return
     */
    public static boolean isPoriratt(Context context) {
        Configuration newConfig = context.getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }
        return false;
    }

    /**
     * 修改横竖屏
     *  希望Activity在横竖屏幕上显示，但是可以根据方向传感器指示的方向来进行改变
     * @param activity
     * @return true 横屏
     *
     */
    public static boolean changeHorizontalOrverticalScreen(Activity activity) {
        if (OrientationWatchDog.isPoriratt(activity)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return true;
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return false;
        }
    }


}
