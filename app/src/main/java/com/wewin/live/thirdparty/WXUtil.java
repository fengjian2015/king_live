package com.wewin.live.thirdparty;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wewin.live.R;
import com.wewin.live.base.MyApp;
import com.wewin.live.newtwork.OnSuccessAndFaultListener;
import com.wewin.live.presenter.PersenterWX;
import com.wewin.live.utils.Constants;
import com.wewin.live.utils.MessageEvent;
import com.example.jasonutil.util.UtilTool;

import org.greenrobot.eventbus.EventBus;

/**
 * @author jsaon
 * @date 2019/3/18
 * 微信分享,授权
 */
public class WXUtil {

    public static WXUtil instance = null;
    public IWXAPI wxapi;
    public static int TYPE;//用于区分发送的什么请求
    public static final int SHARE=1;//分享
    public static final int AUTHORIZATION=2;//授权

    public static WXUtil getInstance() {
        if (instance == null) {
            instance = new WXUtil();
        }
        return instance;
    }

    public WXUtil(){
        wxapi =WXAPIFactory.createWXAPI(MyApp.getInstance(),null);
        // 将该app注册到微信
        wxapi.registerApp(Constants.WX_APP_ID);
    }


    /**
     *  判断是否安装微信
     * @param context
     * @return true 安装
     */
    public boolean isWeiXinAppInstall(Context context) {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(MyApp.getInstance(), Constants.WX_APP_ID);
        if (wxapi.isWXAppInstalled()) {
            return true;
        } else {
            MessageEvent messageEvent = new MessageEvent(MessageEvent.SHARE_FAIL);
            messageEvent.setError(context.getString(R.string.no_install_wx));
            EventBus.getDefault().post(messageEvent);
            return false;
        }
    }
    /*------------------------------------------授权--------------------------------------------------*/

    /**
     * 发送授权请求获取code
     */
    public void sendAuthorization(){
        TYPE=AUTHORIZATION;
        SendAuth.Req req = new SendAuth.Req();
        //应用授权作用域，如获取用户个人信息则填写snsapi_userinfo
        req.scope = "snsapi_userinfo";
        //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        req.state = "wechat_sdk_demo_test";
        wxapi.sendReq(req);
    }

    /**
     * 回调成功后调用发送请求获取accesstoken
     * @param context
     * @param code
     * 用户的unionid是唯一的
     * 最好保存unionID信息，以便以后在不同应用之间进行用户信息互通。
     */
    public void getAccessToken(Context context,String code, OnSuccessAndFaultListener onSuccessAndFaultListener) {
        PersenterWX.getInstance().getAccessToken(context,code,onSuccessAndFaultListener);
    }

    /**
     * 获取用户信息以及唯一标识
     * @param context
     * @param access
     * @param oppenid
     */
    public void getWXUserInfo(Context context,String access,String oppenid, OnSuccessAndFaultListener onSuccessAndFaultListener){
        PersenterWX.getInstance().getWXUserInfo(context,access,oppenid,onSuccessAndFaultListener);
    }

    /*------------------------------------------分享--------------------------------------------------*/
    /**
     * 分享文本类型
     *
     * @param text 文本内容
     * @param type 微信会话或者朋友圈等
     * 分享到对话:
     * SendMessageToWX.Req.WXSceneSession
     * 分享到朋友圈:
     * SendMessageToWX.Req.WXSceneTimeline ;
     * 分享到收藏:
     * SendMessageToWX.Req.WXSceneFavorite
     */
    public void shareTextToWx(Context context,String text, int type) {
        TYPE=SHARE;
        if(!isWeiXinAppInstall(context)){
            return;
        }
        if (text == null || text.length() == 0) {
            return;
        }

        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = type;

        wxapi.sendReq(req);

    }

    /**
     * 分享url地址
     *
     * @param url            地址
     * @param title          标题
     * @param desc           描述
     * @param type 类型
     */
    public void shareUrlToWx(Context context,String url, String title, String desc, final int type) {
        TYPE=SHARE;
        if(!isWeiXinAppInstall(context)){
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = desc;

        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo);
        msg.thumbData =UtilTool.bitmap2Bytes(thumbBmp,30);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message =msg;
        req.scene =type;
        //调用api接口，发送数据到微信
        wxapi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
