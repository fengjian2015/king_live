package com.example.jasonutil.util;

import android.app.Activity;

import java.util.Stack;

/**
 * @author jsaon
 * @date 2019/2/28
 * activity管理
 */
public class ActivityManage {
    public static Stack mActivityList = new Stack<Activity>();//储存打开的Activity

    // 添加Activity到容器中
    public static void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    // 添加Activity到容器中
    public static void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    // 遍历所有Activity并finish
    public static void exit() {
        for (int i = 0; i < mActivityList.size(); i++) {
            Activity activity = (Activity) mActivityList.get(i);
            activity.finish();
        }
    }

    // 遍历所有Activity并finish指定activity
    public static void exitActivity(String activityName) {
        for (int i = 0; i < mActivityList.size(); i++) {
            Activity activity = (Activity) mActivityList.get(i);
            if (activity.getClass().getName().equals(activityName)) {
                activity.finish();
            }
        }
    }

    /**
     * @return 获取栈顶的Activity
     */
    public  Activity getTopActivity() {
        Activity pop = (Activity) mActivityList.pop();
        return pop;
    }
}
