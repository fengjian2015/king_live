package com.wewin.live.utils;

import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.base.MyApp;
import com.wewin.live.thirdparty.UMMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * @author jsaon
 * @date 2019/3/13
 * 个人信息sharedPreferences保存
 */
public class SignOutUtil {
    /**
     * 退出登录调用
     */
    public static void signOut(){
        UMMessage.getInstance().deleteAlias();
        setLogin(false);
        MySharedPreferences.getInstance().setString(MySharedConstants.USER_ID,"");
        MySharedPreferences.getInstance().setString(MySharedConstants.TOKEN,"");
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW,false);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.SIGN_OUT));
        MyApp.getInstance().mSmallViewLayout.dismissWindow(true);
        UtilTool.clearWeb();
    }

    /**
     * 获取id
     * @return
     */
    public static String getUserId(){
        return MySharedPreferences.getInstance().getString(MySharedConstants.USER_ID);
    }

    /**
     * 设置唯一id
     * @param user_id
     */
    public static void setUserId(String user_id){
        MySharedPreferences.getInstance().setString(MySharedConstants.USER_ID,user_id);
    }

    /**
     * 获取token
     */
    public static String getToken(){
        return MySharedPreferences.getInstance().getString(MySharedConstants.TOKEN);
    }


    /**
     * 设置token
     * @param token
     */
    public static void setToken(String token){
        MySharedPreferences.getInstance().setString(MySharedConstants.TOKEN,token);
    }

    /**
     * 设置是否登录
     * @param isLogin
     */
    public static void setLogin(boolean isLogin){
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.IS_LOGIN,isLogin);
    }

    /**
     * 获取是否登录
     * @return
     */
    public static boolean getIsLogin(){
        return MySharedPreferences.getInstance().getBoolean(MySharedConstants.IS_LOGIN);
    }
}
