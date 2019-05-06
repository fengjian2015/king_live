package com.wewin.live.utils;

import android.widget.Switch;

import com.example.jasonutil.util.MySharedPreferences;

/**
 * @author jsaon
 * @date 2019/3/1
 * ui公共处理方法类
 */
public class UiUtil {

    /**
     * 设置开关初始值
     * @param key
     * @param view
     */
    public static void setOnOf(String key, Switch view){
        view.setChecked(MySharedPreferences.getInstance().getBoolean(key));
    }

    /**
     * 修改开关
     * @param key
     * @param view
     */
    public static boolean changeOnOff(String key,Switch view) {
        if (MySharedPreferences.getInstance().getBoolean(key)) {
            view.setChecked(false);
            MySharedPreferences.getInstance().setBoolean(key,false);
            return false;
        }else {
            view.setChecked(true);
            MySharedPreferences.getInstance().setBoolean(key,true);
            return true;
        }
    }
}
