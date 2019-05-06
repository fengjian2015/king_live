package com.wewin.live.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wewin.live.thirdparty.WXUtil;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author jsaon
 * @date 2019/3/18
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXUtil.getInstance().wxapi;
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
//        System.out.println(baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if(WXUtil.TYPE==WXUtil.SHARE){
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_SUCCESS));
                }else if(WXUtil.TYPE==WXUtil.AUTHORIZATION){
                    String code = ((SendAuth.Resp) baseResp).code;
                    MessageEvent messageEvent = new MessageEvent(MessageEvent.AUTHORIZATION_SUCCESS);
                    messageEvent.setCode(code);
                    EventBus.getDefault().post(messageEvent);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if(WXUtil.TYPE==WXUtil.SHARE){
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.SHARE_CANCEL));
                }else if(WXUtil.TYPE==WXUtil.AUTHORIZATION){

                }
                break;
            default:
                if(WXUtil.TYPE==WXUtil.SHARE){
                    MessageEvent messageEvent = new MessageEvent(MessageEvent.SHARE_FAIL);
                    messageEvent.setError(baseResp.errStr);
                    EventBus.getDefault().post(messageEvent);
                }else if(WXUtil.TYPE==WXUtil.AUTHORIZATION){

                }
                break;
        }
        finish();
    }
}
