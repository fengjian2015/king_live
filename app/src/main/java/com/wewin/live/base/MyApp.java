package com.wewin.live.base;

import android.app.Application;
import android.webkit.CookieSyncManager;

import com.alivc.player.AliVcMediaPlayer;
import com.example.jasonutil.util.LogUtil;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.wewin.live.db.DatabaseManager;
import com.wewin.live.listener.CrashHandler;
import com.wewin.live.newtwork.MyHostnameVerifier;
import com.wewin.live.thirdparty.UMMessage;
import com.wewin.live.ui.widget.SmallViewLayout;
import com.wewin.live.utils.Constants;
import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.MyLifecycleHandler;
import com.example.jasonutil.util.MySharedPreferences;

/**
 * @author jsaon
 * @date 2019/2/28
 */
public class MyApp extends Application {
    public static MyApp instance = null;
    public SmallViewLayout mSmallViewLayout;
    //单例
    public static MyApp getInstance() {
        if (instance == null) {
            instance = new MyApp();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        new CrashHandler(this);
        initSharedPreferences();
        initCreateFile();
        initAli();
        initSmallViewLayout();
        LogUtil.setIsDebug(Constants.isDebug);
        //用于获取是否处于后台状态
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        //初始化数据库
        DatabaseManager.initializeInstance(getApplicationContext());
        //初始化微博
        WbSdk.install(this,new AuthInfo(this,Constants.WEI_BO_APP_KEY,Constants.WEI_BO_SCOPE,Constants.WEI_BO_REDIRECT_URL));
        //忽略host认证
        new MyHostnameVerifier();
        //初始化cookie
        CookieSyncManager.createInstance(this);
        //初始化友盟
        UMMessage.getInstance().init(this);

    }

    private void initSmallViewLayout() {
        mSmallViewLayout=new SmallViewLayout(this);
    }

    /**
     * 初始化阿里播放器
     */
    private void initAli() {
        //初始化播放器（只需调用一次即可，建议在application中初始化）
        AliVcMediaPlayer.init(getApplicationContext());
    }

    /**
     * 初始化preferences
     */
    public void initSharedPreferences() {
        //初始化sp
        MySharedPreferences.getInstance().init(this);
    }

    /**
     * 创建根目录
     */
    private void initCreateFile() {
        FileUtil.createFile(FileUtil.getStorageString(this));
    }

}
