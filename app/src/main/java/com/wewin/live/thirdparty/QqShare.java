package com.wewin.live.thirdparty;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wewin.live.R;
import com.wewin.live.base.MyApp;
import com.wewin.live.utils.Constants;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/3/18
 * QQ分享
 */
public class QqShare {
    private Tencent mTencent;

    public static QqShare instance = null;

    public static QqShare getInstance() {
        if (instance == null) {
            instance = new QqShare();
        }
        return instance;
    }

    public QqShare(){
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, MyApp.getInstance());
    }

    /**
     * 分享到好友
     */
    public void shareFriendImageAndText(Activity activity,String title,String content,String url,String imageUrl){
        if(!isQQClientAvailable(activity)) {
            return;
        }
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//分享类型图文
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//最长30个字符。
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  content);//最长40个字符。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);//点击跳转的url
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);//图片url
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  activity.getString(R.string.app_name));
        mTencent.shareToQQ(activity, params, mIUiListener);

    }

    /**
     * 分享到朋友圈
     */
    public void shareToQzone(Activity activity,String title,String content,String url,String imageUrl) {
        if(!isQQClientAvailable(activity)) {
            return;
        }
        //图片地址
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(imageUrl);
        //分享类型
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        //PUBLISH_TO_QZONE_IMAGE_URL	选填	ArrayList	说说的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  activity.getString(R.string.app_name));
        mTencent.shareToQzone(activity, params, mIUiListener);
    }

    public  IUiListener mIUiListener= new IUiListener(){
        @Override
        public void onComplete(Object o) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_SUCCESS));
        }

        @Override
        public void onError(UiError uiError) {
            MessageEvent messageEvent = new MessageEvent(MessageEvent.SHARE_FAIL);
            messageEvent.setError(uiError.errorMessage);
            EventBus.getDefault().post(messageEvent);
        }

        @Override
        public void onCancel() {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_SUCCESS));
        }
    };

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if ("com.tencent.qqlite".equalsIgnoreCase(pn) ||"com.tencent.mobileqq".equalsIgnoreCase(pn)) {
                    return true;
                }
            }
        }
        MessageEvent messageEvent = new MessageEvent(MessageEvent.SHARE_FAIL);
        messageEvent.setError(context.getString(R.string.no_install_qq));
        EventBus.getDefault().post(messageEvent);
        return false;
    }
}
