package com.wewin.live.utils;


import com.wewin.live.utils.down.DownloadService;


/**
 * Created by GA on 2017/11/22.
 * eventBus数据管理类
 */

public class MessageEvent {
    public static final int CUSTONM_SIDE=1;//打开侧栏个人信息
    public static final int MAIN_MENU_UPDATA=2;//更新导航栏
    public static final int MAIN_MENU_FAILURE=3;//获取导航栏失败
    public static final int MAIN_MENU_AGAIN=4;//重新获取导航栏
    public static final int START_UPDATA_APK=5;//开始下载apk
    public static final int LOGIN=6;//登录
    public static final int SIGN_OUT=7;//退出
    public static final int SHARE_SUCCESS=8;//分享成功
    public static final int SHARE_FAIL=9;//分享失败
    public static final int SHARE_CANCEL=10;//分享取消
    public static final int UPTADA_INFO=11;//修改个人信息
    public static final int AUTHORIZATION_SUCCESS=12;//分享成功
    public static final int SEARCH_CONTENT=13;//搜索内容
    public static final int UPDATA_SEARCH_OTHER=14;//刷新搜索其余类型
    public static final int WB_SCROLL_HIGHT_CHANGE=15;//webview滑动改变首页控件高度

    public MessageEvent(int msgId) {
        mMsgId = msgId;
    }


    private int mMsgId;
    private String url;
    private String error;
    private String code;
    private String versionName;
    private String content;
    private float starY;
    private float scrollY;
    private DownloadService.DownloadCallback mDownloadCallback;

    public float getStarY() {
        return starY;
    }

    public void setStarY(float starY) {
        this.starY = starY;
    }

    public float getScrollY() {
        return scrollY;
    }

    public void setScrollY(float scrollY) {
        this.scrollY = scrollY;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DownloadService.DownloadCallback getDownloadCallback() {
        return mDownloadCallback;
    }

    public void setDownloadCallback(DownloadService.DownloadCallback downloadCallback) {
        mDownloadCallback = downloadCallback;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMsgId() {
        return mMsgId;
    }


    public void setMsgId(int msgId) {
        mMsgId = msgId;
    }


}
