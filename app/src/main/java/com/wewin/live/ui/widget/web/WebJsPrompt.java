package com.wewin.live.ui.widget.web;

import android.content.Context;
import android.os.Message;
import android.webkit.JsPromptResult;

import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.keyboard.EmoticonsKeyboardUtils;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/*
 *   author:jason
 *   date:2019/5/1710:35
 */
public class WebJsPrompt {
    private final String MSG_PROMPT_HEADER = "king";
    private final String GET_VERSION_NAME = "get_version_name";//获取版本名
    private final String GET_KEYBOARD_HEIGHT = "get_keyboard_height";//获取键盘高度
    private final String SET_KEYBOARD_HEIGHT = "set_keyboard_height";//设置键盘高度
    private final String ADD_BARRAGE = "add_barrage";//添加弹幕
    private Context mContext;
    public WebJsPrompt(Context context){
        mContext=context;
    }

    /**
     * 拦截 Prompt 进行处理
     * 格式：king{"type":"","body":""}
     * 安全的
     * @param message
     * @param result
     * @return
     */
    public boolean handleJsInterface(String message, JsPromptResult result) {
        String prefix = MSG_PROMPT_HEADER;
        if (!message.startsWith(prefix)) {
            return false;
        }
        String jsonStr = message.substring(prefix.length());
        try {
            Map map = JSONObject.parseObject(jsonStr, Map.class);
            switch (map.get(BaseInfoConstants.TYPE) + "") {
                case GET_VERSION_NAME:
                    result.confirm(UtilTool.getVersionName(mContext));
                    break;
                case GET_KEYBOARD_HEIGHT:
                    result.confirm(EmoticonsKeyboardUtils.getDefKeyboardHeight(mContext)+"");
                    break;
                case SET_KEYBOARD_HEIGHT:
                    EmoticonsKeyboardUtils.setDefKeyboardHeight(mContext,UtilTool.parseInt(map.get(BaseInfoConstants.BODY)+""));
                    result.cancel();
                    break;
                case ADD_BARRAGE:
                    MessageEvent messageEvent=new MessageEvent(MessageEvent.ADD_BARRAGE);
                    messageEvent.setIsSelf(UtilTool.parseInt(map.get(BaseInfoConstants.IS_SELF)+""));
                    messageEvent.setContent(map.get(BaseInfoConstants.CONTENT)+"");
                    EventBus.getDefault().post(messageEvent);
                    result.cancel();
                    break;
                default:
                    result.cancel();
                    break;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.cancel();
        return false;
    }


}
