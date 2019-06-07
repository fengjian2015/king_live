package com.wewin.live.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.activity.HtmlActivity;
import com.wewin.live.ui.activity.live.VideoDetailsActivity;
import com.wewin.live.utils.Constants;
import com.wewin.live.utils.NotificationUtil;
import com.wewin.live.utils.SignOutUtil;

import java.util.Map;

/*
 *   author:jason
 *   date:2019/5/112:35
 *   友盟推送
 */
public class UMMessage {
    public static UMMessage instance = null;
    private PushAgent mPushAgent;
    private NotificationUtil notificationUtil;

    //单例
    public static UMMessage getInstance() {
        if (instance == null) {
            instance = new UMMessage();
        }
        return instance;
    }

    /**
     * 初始化推送
     *
     * @param context
     */
    public void init(Context context) {
        notificationUtil = new NotificationUtil();
        notificationUtil.setNotification(context);
        UMConfigure.init(context, Constants.UM_ID, Constants.UM_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, Constants.UMENG_MESSAGE_SECRET);
        UMConfigure.setLogEnabled(false);
        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);

        mPushAgent = PushAgent.getInstance(context);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtil.umlog(s);
                MySharedPreferences.getInstance().setString(BaseInfoConstants.DEVICE_TOKEN,s);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.umlog(s + "   " + s1);
            }
        });
        setMessageHandler();
    }

    /**
     * 初始化透传消息
     */
    private void setMessageHandler() {
        if (mPushAgent == null) {
            return;
        }
        mPushAgent.setMessageHandler(umengMessageHandler);
    }

    /**
     * 添加别名绑定
     * 登录时需要绑定
     */
    public void setAlias() {
        mPushAgent.setAlias(SignOutUtil.getUserId(), "king", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.umlog("别名绑定：" + isSuccess + "  " + message);
            }
        });
    }

    /**
     * 移除别名
     * 退出登录时调用
     */
    public void deleteAlias() {
        //移除别名ID
        mPushAgent.deleteAlias(SignOutUtil.getUserId(), "king", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.umlog("别名移除：" + isSuccess + "  " + message);
            }
        });
    }

    /**
     * 消息透传
     */
    private UmengMessageHandler umengMessageHandler = new UmengMessageHandler() {
        String PLAY_LIVE = "playLive";
        String NEW_HTML = "newHtml";
        @Override
        public void dealWithCustomMessage(Context context, UMessage uMessage) {
            LogUtil.umlog(uMessage.custom);
            try {
                Map map = JSONObject.parseObject(uMessage.custom, Map.class);
                notificationUtil.setContent(map.get(BaseInfoConstants.TITLE) + "", map.get(BaseInfoConstants.CONTENT) + "");
                if (PLAY_LIVE.equals(map.get(BaseInfoConstants.TYPE))) {
                    //跳转到播放
                    Intent intentPlay = playLive(context, map);
                    notificationUtil.setIntent(intentPlay,ActivityUtil.isAppRunning(context));
                } else if (NEW_HTML.equals(map.get(BaseInfoConstants.TYPE))) {
                    //跳转到新的H5界面
                    Intent intents = newHtml(context, map);
                    notificationUtil.setIntent(intents,ActivityUtil.isAppRunning(context));
                } else {
                    //打开应用
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    if(intent!=null) {
                        intent.setPackage(null); // 加上这句代码
                    }
                    notificationUtil.setIntent(intent);
                }
                notificationUtil.notifyNot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    /**
     * 跳转到H5
     * @param context
     * @param map
     * @return
     */
    private Intent newHtml(Context context, Map map){
        Intent intent = new Intent(context,HtmlActivity.class);
        try {
            map=JSONObject.parseObject(map.get(BaseInfoConstants.BODY)+"", Map.class);
            intent = new Intent(context, HtmlActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString(BaseInfoConstants.URL,map.get(BaseInfoConstants.URL)+"");
            intent.putExtras(bundle);
        }catch (Exception e){
            e.printStackTrace();
        }
        return intent;
    }

    /**
     * 跳转到播放器界面
     * @param context
     * @param map
     * @return
     */
    private Intent playLive(Context context, Map map) {
        Intent intent=null;
        try {
            map=JSONObject.parseObject(map.get(BaseInfoConstants.BODY)+"", Map.class);
            intent = new Intent(context, VideoDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BaseInfoConstants.PULL_URL, map.get(BaseInfoConstants.PULL_URL) + "");
            bundle.putString(BaseInfoConstants.URL, map.get(BaseInfoConstants.HTML_URL) + "");
            bundle.putString(BaseInfoConstants.WECHAT, map.get(BaseInfoConstants.WECHAT) + "");
            bundle.putString(BaseInfoConstants.ADSCONTENT, map.get(BaseInfoConstants.ADSCONTENT) + "");
            bundle.putString(BaseInfoConstants.WXIMAGE, map.get(BaseInfoConstants.WXIMAGE) + "");
            bundle.putString(BaseInfoConstants.TITLE, map.get(BaseInfoConstants.TITLE) + "");
            intent.putExtras(bundle);
        }catch (Exception e){
            e.printStackTrace();
        }
        return intent;
    }
}
