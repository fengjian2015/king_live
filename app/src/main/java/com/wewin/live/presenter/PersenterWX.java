package com.wewin.live.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.newtwork.OnSuccessAndFaultListener;
import com.wewin.live.newtwork.OnSuccessAndFaultSub;
import com.wewin.live.newtwork.RetrofitUtil;
import com.wewin.live.thirdparty.WXUtil;
import com.wewin.live.utils.Constants;

import java.util.HashMap;

import io.reactivex.Observable;

/**
 * @author jsaon
 * @date 2019/3/22
 * 微信相关网络请求
 */
public class PersenterWX {
    public static PersenterWX instance = null;

    public static PersenterWX getInstance() {
        if (instance == null) {
            instance = new PersenterWX();
        }
        return instance;
    }

    /**
     * 获取微信token
     */
    public void getAccessToken(final Context context, String code, final OnSuccessAndFaultListener onSuccessAndFaultListener) {
        OnSuccessAndFaultListener l = new OnSuccessAndFaultListener() {
            @Override
            public void onSuccess(String result) {//成功回调
                 HashMap map=JSONObject.parseObject(result,HashMap.class);
                if(StringUtils.isEmpty(map.get("errcode")+"")){
                    WXUtil.getInstance().getWXUserInfo(context,map.get("access_token")+"",map.get("openid")+"",onSuccessAndFaultListener);
                }else {
                    ToastShow.showToast2(context,map.get("errmsg")+"");
                }
            }

            @Override
            public void onFault(String errorMsg) {//失败的回调
                if(!ActivityUtil.isActivityOnTop(context))return;
                ToastShow.showToast2(context,errorMsg);
            }
        };
        Observable observable = RetrofitUtil.getInstance(context,Constants.BASE_WX_URL).getServer().getAccessToken(Constants.WX_APP_ID,Constants.WX_SECRET,code,"authorization_code"); //在HttpServer中
        RetrofitUtil.getInstance(context).toSubscribe(observable, new OnSuccessAndFaultSub(l,context,true));
    }

    /**
     * 获取微信token
     * unionid 当且仅当该移动应用已获得该用户的userinfo授权时，才会出现该字段
     */
    public void getWXUserInfo(final Context context, String access,String openId,final OnSuccessAndFaultListener onSuccessAndFaultListener) {
        OnSuccessAndFaultListener l = new OnSuccessAndFaultListener() {
            @Override
            public void onSuccess(String result) {//成功回调
                HashMap map=JSONObject.parseObject(result,HashMap.class);
                String unionid=map.get("unionid")+"";
                onSuccessAndFaultListener.onSuccess(unionid);
            }

            @Override
            public void onFault(String errorMsg) {//失败的回调
                if(!ActivityUtil.isActivityOnTop(context))return;
                ToastShow.showToast2(context,errorMsg);
            }
        };
        Observable observable = RetrofitUtil.getInstance(context,Constants.BASE_WX_URL).getServer().getWXUserInfo(access,openId); //在HttpServer中
        RetrofitUtil.getInstance(context).toSubscribe(observable, new OnSuccessAndFaultSub(l,context,true));
    }

}
