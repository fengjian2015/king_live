package com.wewin.live.ui.activity;

import android.os.Bundle;

import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.base.BaseMainAcitvity;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterLogin;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.thirdparty.UMMessage;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.SignOutUtil;
import com.wewin.live.utils.down.DownloadService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseMainAcitvity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动下载服务
        UtilTool.startDownServer(this, DownloadService.class, DownloadService.DOWN_LOAD_SERVICE_NAME);
        init();
    }

    private void init() {
        //获取状态栏
        String menuString=MySharedPreferences.getInstance().getString(MySharedConstants.MAIN_MENU);
        if(StringUtils.isEmpty(menuString)){
            PersenterLogin.getInstance().getNavMenu(true,new OnSuccess(this));
        }else {
            PersenterLogin.getInstance().getNavMenu(false,new OnSuccess(this));
        }

        checkVersionHttp(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取用户信息配置
        PersenterPersonal.getInstance().getBaseInfo(new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                if(SignOutUtil.getIsLogin()){
                    UMMessage.getInstance().setAlias();
                }
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 获取版本信息
     *
     * @param isShow
     */
    private void checkVersionHttp(boolean isShow) {
        PersenterPersonal.getInstance().getConfig(isShow, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                setVersionView();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.CUSTONM_SIDE) {
            //打开侧栏
            mCustomSideMenu.sideMenuScroll(true);
        } else if (msgId == MessageEvent.MAIN_MENU_AGAIN) {
            //再次获取
            init();
        } else if (msgId == MessageEvent.LOGIN) {
            //登录
            loginOrOut();
            initData();
        } else if (msgId == MessageEvent.SIGN_OUT) {
            //退出登录
            mCustomSideMenu.sideMenuScroll(false);
            loginOrOut();
            initData();
        } else if (msgId == MessageEvent.UPTADA_INFO) {
            initData();
        }
    }


}
