package com.wewin.live.utils;

/**
 * @author jsaon
 * @date 2019/2/28
 */
public class Constants {
    //全局控制开关
    public static final boolean isDebug = true;
    //本app
//    public static final String BASE_URL=isDebug ? "http://testzhibo.wewin18.net/" : "https://www.zhibo16.live/";//测试
//    public static final String BASE_URL=isDebug ? "http://dev.zhibo18.live/" : "https://www.zhibo16.live/";//tong
    public static final String BASE_URL=isDebug ? "http://tong.wewin18.net/" : "https://www.zhibo16.live/";//tong
//    public static final String BASE_URL=isDebug ? "http://wzzb.wewin18.net/" : "https://www.zhibo16.live/";//callan
//    public static final String BASE_URL = isDebug ? "http://tim.wewin18.net/" : "https://www.zhibo16.live/";//tim
//    public static final String BASE_URL = isDebug ? "http://jeffrey.wewin18.net:8081/" : "https://www.zhibo16.live/";//tim
    //微信的前缀
    public static final String BASE_WX_URL = "https://api.weixin.qq.com/sns/";

    //------------------第三方id---------------------
    public static final String UM_CHANNEL = isDebug ? "测试" : "生产";//友盟渠道
    public static final String UM_ID = "5cc80a494ca3572c820007cd";
    public static final String UMENG_MESSAGE_SECRET = "618ecbd1149fdbdbd51486d22c62bdf1";

    public static final String QQ_APP_ID = "101566272";
    public static final String WEI_BO_APP_KEY = "3431441065";
    public static final String WEI_BO_SCOPE = "statuses/share";
    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    public static final String WEI_BO_REDIRECT_URL = "http://www.sina.com";
    //微信
    public static final String WX_APP_ID = "wxaf69886b3e6ea0ea";
    public static final String WX_SECRET = "db31c3dede14cae348b89cf3c9e24d40";
    //---------------------------------------------
    public static final String USER_AGREEMENT = BASE_URL + "license.html";
}
