package com.wewin.live.utils;

/**
 * @author jsaon
 * @date 2019/3/1
 * sharedPreferences的key值管理类
 */
public class MySharedConstants {
    //个人信息类
    public static final String USER_ID="user_id";//个人id
    public static final String TOKEN="token";//个人token
    public static final String IS_LOGIN="is_login";//是否登录
    public static final String ON_OFF_Y=SignOutUtil.getUserId()+"on_off_y";//是否开启硬件编码
    public static final String ON_OFF_FOUR_G=SignOutUtil.getUserId()+"on_off_four_g";//是否允许3G/4G下观看
    public static final String ON_OFF_LITTLE_WINDOW=SignOutUtil.getUserId()+"on_off_little_window";//控制是否允许小窗口播放
    public static final String ON_OFF_ALL_REMINDERS=SignOutUtil.getUserId()+"on_off_all_reminders";//是否开启全部体系
    public static final String ON_OFF_SHOW_WINDOW=SignOutUtil.getUserId()+"on_off_show_window";//当前是否显示小窗口播放


    public static final String SEARCH_DATA=SignOutUtil.getUserId()+"search_data";//搜索记录
    public static final String MAIN_MENU="main_menu";//首页导航
    public static final String COUNTRY_CODE="country_code";//国家码
    public static final String VERSION_NAME="version_name";//版本名
    public static final String APK_DES="apk_des";//更新内容
    public static final String APK_URL="apk_url";//apk下载地址
    public static final String SEARCH_CONTENT="search_content";//搜索内容
    public static final String SEARCH_HOT="search_hot";//热门搜索
    public static final String NO_LOGIN_DIALOG="no_login_dialog";//未登录时是否弹窗
}
