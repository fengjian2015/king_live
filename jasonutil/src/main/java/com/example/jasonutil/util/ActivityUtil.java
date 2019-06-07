package com.example.jasonutil.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * 判断activity是否存在
 */
public class ActivityUtil {
	public static boolean isActivityOnTop(Activity act) {
		if (act != null) {
			if (Build.VERSION.SDK_INT >= 17) {
				if (act.isDestroyed() || act.isFinishing()) {
					return false;
				}
			} else {
				if (act.isFinishing()) {
					return false;
				}
			}
		}else {
			return false;
		}
		return true;
	}

	public static boolean isActivityOnTop(Context context) {
		if(context!=null){
			if(context instanceof Activity){
				if (Build.VERSION.SDK_INT >= 17) {
					if (((Activity)context).isDestroyed() || ((Activity)context).isFinishing()) {
						return false;
					}
				} else {
					if (((Activity)context).isFinishing()) {
						return false;
					}
				}
			}else if(context instanceof Service && !isServiceRunning(context,context.getClass().getName()) ){
				return false;
			}
		}else{
			return false;
		}
		return true;
	}


	/**
	 * 当前app是否还在运行
	 * @param context
	 * @return
	 */
	public static boolean isAppRunning(Context context){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String pkgName = context.getPackageName();
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(pkgName) || info.baseActivity.getPackageName().equals(pkgName)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * 判断视图是否存在
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(150);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}


}
