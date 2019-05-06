package com.wewin.live.presenter;

import android.app.Activity;
import android.content.Context;

import com.example.jasonutil.util.MySharedPreferences;
import com.wewin.live.R;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.newtwork.OnPersenterListener;
import com.wewin.live.newtwork.OnSuccess;
import com.example.jasonutil.util.FileUtil;
import com.wewin.live.ui.activity.MainActivity;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.SignOutUtil;
import com.example.jasonutil.util.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author jsaon
 * @date 2019/3/26
 * 个人信息相关接口
 */
public class PersenterPersonal {
    public static PersenterPersonal instance = null;

    public static PersenterPersonal getInstance() {
        if (instance == null) {
            instance = new PersenterPersonal();
        }
        return instance;
    }

    /**
     * 上传头像
     * @param file
     * @param onSuccess
     */
    public void upLoadImage( File file, OnSuccess onSuccess) {
        //文件配置
        RequestBody uidBody = RequestBody.create(MediaType.parse("multipart/form-data"), SignOutUtil.getUserId());
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), SignOutUtil.getToken());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(FileUtil.DATA_TYPE_IMAGE_PNG), file));
        onSuccess.sendHttp(onSuccess.getMyServer(60).uploadImage(uidBody, tokenBody, filePart));
    }


    /**
     * 修改个人信息
     * @param fields                    jason字符串
     * @param onSuccess
     */
    public void updateFields( String fields, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().updateFields(UtilTool.parseInt(SignOutUtil.getUserId()), SignOutUtil.getToken(), fields));
    }


    /**
     * 获取个人资料界面信息
     * @param onSuccess
     */
    public void getPersonal(OnSuccess onSuccess){
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().getPersonl(UtilTool.parseInt(SignOutUtil.getUserId()), SignOutUtil.getToken()));
    }


    /**
     * 获取任务中心
     * @param onSuccess
     */
    public void taskCenter(boolean isShow,OnSuccess onSuccess){
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().taskCenter(UtilTool.parseInt(SignOutUtil.getUserId()), SignOutUtil.getToken()),isShow);
    }

    /**
     * 获取任务中心
     * @param onSuccess
     */
    public void taskCenterClaim(String action,OnSuccess onSuccess){
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().taskCenterClaim(UtilTool.parseInt(SignOutUtil.getUserId()), SignOutUtil.getToken(),action));
    }

    /**
     * 意见反馈
     * @param onSuccess
     */
    public void feedback(String content,String version,OnSuccess onSuccess){
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().feedback(UtilTool.parseInt(SignOutUtil.getUserId()),content,version));
    }

    /**
     * 签到
     * @param onSuccess
     */
    public void signin(int day,OnSuccess onSuccess){
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().signin(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),day));
    }

    /**
     *  绑定邮箱
     * @param code
     * @param email
     * @param onSuccess
     */
    public void bindEmail(String code,String email,OnSuccess onSuccess){
        onSuccess
                .sendHttp(onSuccess.getMyServer().bindEmail(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),code,email));
    }

    /**
     * 检查版本
     * @param onSuccess
     */
    public void getConfig(boolean isShow,OnSuccess onSuccess){
        onSuccess.sendHttp(onSuccess.getMyServer().getConfig(),isShow).setOnPersenterListener(new OnPersenterListener() {
            @Override
            public void onSuccess(Object content) {
                Map map_info = BaseMapInfo.getInfo((BaseMapInfo) content).get(0);
                MySharedPreferences.getInstance().setString(MySharedConstants.VERSION_NAME,map_info.get(BaseInfoConstants.APK_VER)+"");
                MySharedPreferences.getInstance().setString(MySharedConstants.APK_DES,map_info.get(BaseInfoConstants.APK_DES)+"");
                MySharedPreferences.getInstance().setString(MySharedConstants.APK_URL,map_info.get(BaseInfoConstants.APK_URL)+"");
            }

            @Override
            public void onFault(String content) {

            }
        });
    }

    /**
     * 获取个人信息
     */
    public void getBaseInfo( final OnSuccess onSuccess) {
        if(!SignOutUtil.getIsLogin())return;
        onSuccess.sendHttp(onSuccess.getMyServer().getBaseInfo(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken()),false)
                .setOnPersenterListener(new OnPersenterListener() {
            @Override
            public void onFault(String content) {

            }

            @Override
            public void onSuccess(Object content) {
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
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPTADA_INFO));
            }
        });
    }

    /**
     * 修改手机号

     */
    public void modifyMobile( String newMobile, String code,String user_pass, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().modifyMobile(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),newMobile, code, user_pass));
    }

    /**
     * 获取修改手机验证码
     *
     * @param mobile
     * @param onSuccess
     */
    public void getBindCode(String mobile, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().getBindCode(mobile));
    }
}
