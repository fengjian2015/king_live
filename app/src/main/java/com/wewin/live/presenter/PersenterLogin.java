package com.wewin.live.presenter;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.newtwork.OnPersenterListener;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.SignOutUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;


/**
 * @author jsaon
 * @date 2019/3/5
 * 登录相关网络请求类
 */
public class PersenterLogin {

    public static PersenterLogin instance = null;

    public static PersenterLogin getInstance() {
        if (instance == null) {
            instance = new PersenterLogin();
        }
        return instance;
    }

    /**
     * 获取顶部栏
     */
    public void getNavMenu(boolean isShow,OnSuccess onSuccess) {
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .setOnPersenterListener(new OnPersenterListener() {
                    @Override
                    public void onFault(String content) {
                        if (StringUtils.isEmpty(MySharedPreferences.getInstance().getString(MySharedConstants.MAIN_MENU))) {
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.MAIN_MENU_FAILURE));
                        }
                    }

                    @Override
                    public void onSuccess(Object content) {
                        BaseMapInfo2 baseMapInfo2 = (BaseMapInfo2) content;
                        MySharedPreferences.getInstance().setString(MySharedConstants.MAIN_MENU, JSON.toJSONString(baseMapInfo2.getData()));
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.MAIN_MENU_UPDATA));
                    }
                })
                .sendHttp(onSuccess.getMyServer().getNavMenu(),isShow);
    }

    /**
     * 登录
     */
    public void login(final String user_login, String user_pass, final OnSuccess onSuccess) {
        onSuccess.setOnPersenterListener(new OnPersenterListener() {
            @Override
            public void onFault(String content) {

            }

            @Override
            public void onSuccess(Object content) {
                UtilTool.clearWeb();
                Context context = onSuccess.getContext();
                BaseMapInfo map = (BaseMapInfo) content;
                List<Map> list = BaseMapInfo.getInfo(map);
                Map data = list.get(0);
                UserInfo userInfo = new UserInfo();
                userInfo.setUser(data.get(BaseInfoConstants.ID) + "");
                userInfo.setActualName(data.get(BaseInfoConstants.TRUENAME) + "");
                userInfo.setEmail(data.get(BaseInfoConstants.USER_EMAIL) + "");
                userInfo.setNickName(data.get(BaseInfoConstants.USER_NICENAME) + "");
                if ("0".equals(data.get(BaseInfoConstants.SEX) + "")) {
                    userInfo.setSex(context.getString(R.string.confidentiality));
                } else if ("1".equals(data.get(BaseInfoConstants.SEX) + "")) {
                    userInfo.setSex(context.getString(R.string.male));
                } else {
                    userInfo.setSex(context.getString(R.string.female));
                }
                userInfo.setSignature(data.get(BaseInfoConstants.SIGNATURE) + "");
                userInfo.setUser_id(data.get(BaseInfoConstants.ID) + "");
                userInfo.setBirth(data.get(BaseInfoConstants.BIRTHDAY) + "");
                userInfo.setAvatar(data.get(BaseInfoConstants.AVATAR) + "");
                userInfo.setAvatar_thumb(data.get(BaseInfoConstants.AVATAR_THUMB) + "");
                userInfo.setCoin(data.get(BaseInfoConstants.COIN) + "");
                userInfo.setWeixin(data.get(BaseInfoConstants.WEIXIN) + "");
                userInfo.setLevel(data.get(BaseInfoConstants.LEVEL) + "");
                userInfo.setConsumption(data.get(BaseInfoConstants.CONSUMPTION) + "");
                userInfo.setLevel_up(data.get(BaseInfoConstants.LEVEL_UP) + "");
                userInfo.setLevel_icon(data.get(BaseInfoConstants.LEVEL_ICON) + "");
                userInfo.setIsanchor(data.get(BaseInfoConstants.ISANCHOR)+"");
                userInfo.setJson(data.get(BaseInfoConstants.JSON)+"");
                UserInfoDao.addUser(userInfo);

                SignOutUtil.setLogin(true);
                SignOutUtil.setUserId(data.get(BaseInfoConstants.ID) + "");
                SignOutUtil.setToken(data.get(BaseInfoConstants.TOKEN) + "");

                EventBus.getDefault().post(new MessageEvent(MessageEvent.LOGIN));
//                IntentStart.starNoAnimtorFinishAll(context, MainActivity.class);
                ((Activity) context).finish();
            }
        }).sendHttp(onSuccess.getMyServer().sendlogin(user_login, user_pass));
    }

    /**
     * 获取注册验证码
     *
     * @param mobile
     * @param onSuccess
     */
    public void getCode(String mobile, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().getLoginCode(mobile));
    }


    /**
     * 获取忘记密码验证码
     *
     * @param mobile
     * @param onSuccess
     */
    public void getForGetCode(String mobile, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().getForGetCode(mobile));
    }

    /**
     * 注册
     *
     * @param mobile
     * @param password
     * @param user_nicename
     * @param code
     * @param onSuccess
     */
    public void userReg(String mobile, String password, String user_nicename, String code, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().registeren(mobile, password, user_nicename, code));
    }

    /**
     * 找回密码
     *
     * @param mobile
     * @param password
     * @param code
     * @param onSuccess
     */
    public void userFindPass(String mobile, String password, String code, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().userFindPass(mobile, password, code));
    }

    /**
     * 获取国家码
     *
     * @param onSuccess
     */
    public void getCountryCode(boolean isShow,final OnSuccess onSuccess) {
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().getCountryCode(),isShow)
                .setOnPersenterListener(new OnPersenterListener() {
                    @Override
                    public void onFault(String content) {

                    }

                    @Override
                    public void onSuccess(Object content) {
                        BaseMapInfo2 baseMapInfo2= (BaseMapInfo2) content;
                        MySharedPreferences.getInstance().setString(MySharedConstants.COUNTRY_CODE,baseMapInfo2.getData().toString());

                    }
                });
    }
}
