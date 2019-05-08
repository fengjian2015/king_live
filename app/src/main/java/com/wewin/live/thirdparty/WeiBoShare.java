package com.wewin.live.thirdparty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.jasonutil.util.UtilTool;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.wewin.live.R;
import com.wewin.live.utils.MessageEvent;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author jsaon
 * @date 2019/3/18
 * 新浪微博分享
 * 在分享页实现WbShareCallback
 * 处理回调重新
 */
public class WeiBoShare {
    public static WeiBoShare instance = null;
    public WbShareHandler wbShareHandler;

    public static WeiBoShare getInstance() {
        if (instance == null) {
            instance = new WeiBoShare();
        }
        return instance;
    }

    /**
     * 初始化之后再调用分享方法
     * @param activity
     */
    public void init(Activity activity){
        wbShareHandler = new WbShareHandler(activity);
        wbShareHandler.registerApp();
    }

    public void doResultIntent(Intent intent){
        if (wbShareHandler==null)return;
        wbShareHandler.doResultIntent(intent,mWbShareCallback);
    }


    public void share(final Activity activity, final String title, final String desc, final String url, final String imageUrl){
        if(!isSinaInstalled(activity))return;
        new Thread(){
            @Override
            public void run() {
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
                weiboMessage.textObject = getTextObj(title,desc,url);//文本内容
                weiboMessage.imageObject=getImageObj(activity,imageUrl);//图片
                wbShareHandler.shareMessage(weiboMessage,false);
            }
        }.start();
    }

    /**
     * 创建文本消息对象
     * @return
     * @param title
     * @param desc
     * @param url
     */
    private  TextObject getTextObj(String title, String desc, String url) {
        TextObject textObject = new TextObject();
        textObject.text = desc;
        textObject.title = title;
        textObject.actionUrl = url;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * @return 图片消息对象。
     */
    private  ImageObject getImageObj(Context context,String imageUrl) {
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = UtilTool.GetLocalOrNetBitmap(imageUrl);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    public WbShareCallback mWbShareCallback=new WbShareCallback() {
        @Override
        public void onWbShareSuccess() {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_SUCCESS));
        }

        @Override
        public void onWbShareCancel() {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_CANCEL));
        }

        @Override
        public void onWbShareFail() {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_FAIL));
        }
    };

    /**
     * sina
     * 判断是否安装新浪微博
     */
    public static boolean isSinaInstalled(Context context){
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        MessageEvent messageEvent = new MessageEvent(MessageEvent.SHARE_FAIL);
        messageEvent.setError(context.getString(R.string.no_install_wb));
        EventBus.getDefault().post(messageEvent);
        return false;
    }
}
