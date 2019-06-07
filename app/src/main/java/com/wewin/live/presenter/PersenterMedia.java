package com.wewin.live.presenter;

import com.alibaba.fastjson.JSONArray;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.newtwork.OnPersenterListener;
import com.wewin.live.newtwork.OnSuccess;
import com.example.jasonutil.util.FileUtil;
import com.wewin.live.newtwork.ProgressRequestBody;
import com.wewin.live.utils.SignOutUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author jsaon
 * @date 2019/3/29
 * 视频直播相关
 */
public class PersenterMedia {
    public static PersenterMedia instance = null;

    public static PersenterMedia getInstance() {
        if (instance == null) {
            instance = new PersenterMedia();
        }
        return instance;
    }

    /**
     * 获取视频分类
     *
     * @param onSuccess
     */
    public void getCategories(final OnSuccess onSuccess) {
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2).sendHttp(onSuccess.getMyServer().getCategories());
    }

    /**
     * 上传视频
     *
     * @param file
     * @param onSuccess
     */
    public void uploadVideo(File file, OnSuccess onSuccess) {
        //文件配置
        RequestBody uidBody = RequestBody.create(MediaType.parse("multipart/form-data"), SignOutUtil.getUserId());
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), SignOutUtil.getToken());
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(FileUtil.DATA_TYPE_VIDEO), file));

        MultipartBody.Part filePart= MultipartBody.Part.createFormData("file", file.getName(), new ProgressRequestBody( RequestBody.create(MediaType.parse(FileUtil.DATA_TYPE_VIDEO), file),
                new ProgressRequestBody.UploadProgressListener() {
                    @Override
                    public void onProgress(long currentBytesCount, long totalBytesCount) {
                        LogUtil.log("当前："+currentBytesCount+"   总长度："+totalBytesCount);
                    }
                }));

        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                 .sendHttp(onSuccess.getMyServer(60)
                 .uploadVideo(uidBody, tokenBody, filePart));
    }


    /**
     * 投稿
     * @param catid
     * @param title
     * @param videoUrlLocal
     * @param onSuccess
     */
    public void contribute(int catid,String title,String videoUrlLocal,File file, final OnSuccess onSuccess) {
        RequestBody uidBody = RequestBody.create(MediaType.parse("multipart/form-data"), SignOutUtil.getUserId());
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), SignOutUtil.getToken());
        RequestBody catidBody = RequestBody.create(MediaType.parse("multipart/form-data"), catid+"");
        RequestBody titleBody = RequestBody.create(MediaType.parse("multipart/form-data"),title );
        RequestBody videoUrlLocalBody = RequestBody.create(MediaType.parse("multipart/form-data"),videoUrlLocal );

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("cover", file.getName(), RequestBody.create(MediaType.parse(FileUtil.DATA_TYPE_IMAGE_PNG), file));
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer()
                .contribute(uidBody,tokenBody,catidBody,titleBody,videoUrlLocalBody,filePart));
    }

    /**
     * 直播配置信息
     * @param mRoomMapList
     * @param mLiveMapList
     * @param mCoinMapList
     * @param mCoinList
     * @param mTimeCoinMapList
     * @param onSuccess
     */
    public void getConfig(final List<Map> mRoomMapList, final List<Map> mLiveMapList, final List<Map> mCoinMapList, final List<ArrayList<String>> mCoinList, final List<Map> mTimeCoinMapList, final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().getConfig())
                .setOnPersenterListener(new OnPersenterListener() {
                    @Override
                    public void onSuccess(Object content) {
                        mRoomMapList.clear();
                        mLiveMapList.clear();
                        mCoinMapList.clear();
                        mCoinList.clear();

                        Map mapInfo = BaseMapInfo.getInfo((BaseMapInfo) content).get(0);
                        //房间类型
                        List<ArrayList> roomList =  JSONArray.parseArray(mapInfo.get(BaseInfoConstants.LIVE_TYPE) + "", ArrayList.class);
                        for (int i=0;i<roomList.size();i++) {
                            ArrayList<String> list=roomList.get(i);
                            HashMap roomMap=new HashMap();
                            roomMap.put(BaseInfoConstants.CONTENT,list.get(1));
                            roomMap.put(BaseInfoConstants.ID,list.get(0));
                            mRoomMapList.add(roomMap);
                        }
                        //直播类型
                        List<Map> liveList =  JSONArray.parseArray(mapInfo.get(BaseInfoConstants.ZHIBO_TYPE) + "", Map.class);
                        for (int i=0;i<liveList.size();i++) {
                            Map liveMap=liveList.get(i);
                            liveMap.put(BaseInfoConstants.CONTENT,liveMap.get(BaseInfoConstants.CATEGORY));
                            liveMap.put(BaseInfoConstants.ID,liveMap.get(BaseInfoConstants.ID));
                            mLiveMapList.add(liveMap);
                            //分类类型
                            List<String> subCategoriesList =JSONArray.parseArray(liveMap.get(BaseInfoConstants.SUBCATEGORIES) + "", String.class);
                            mCoinList.add((ArrayList) subCategoriesList);
                        }
                        //计费列表
                        List<String> timeCoinList =  JSONArray.parseArray(mapInfo.get(BaseInfoConstants.LIVE_TIME_COIN) + "", String.class);
                        for (int i=0;i<timeCoinList.size();i++) {
                            HashMap timeCoinMap=new HashMap(16);
                            timeCoinMap.put(BaseInfoConstants.CONTENT,timeCoinList.get(i)+onSuccess.getContext().getString(R.string.time_coin));
                            timeCoinMap.put(BaseInfoConstants.ID,timeCoinList.get(i));
                            mTimeCoinMapList.add(timeCoinMap);
                        }
                    }
                    @Override
                    public void onFault(String content) {
                    }
                });
    }

    /**
     * 创建直播
     * @param onSuccess
     */
    public void createLive(Map hashMap,final OnSuccess onSuccess) {
        if(hashMap==null) {
            return;
        }
        onSuccess.sendHttp(onSuccess.getMyServer().createLive(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken()
                ,hashMap.get(BaseInfoConstants.TITLE)+"",UtilTool.parseInt(hashMap.get(BaseInfoConstants.TYPE)+""),hashMap.get(BaseInfoConstants.TYPE_VAL)+""
                ,UtilTool.parseInt(hashMap.get(BaseInfoConstants.ZBTYPE)+""),hashMap.get(BaseInfoConstants.ZBTYPE_DETAIL)+""));
    }

    /**
     * 是否关注
     * @param onSuccess
     */
    public void isAttent(String touid,final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().isAttent(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),touid),false);
    }

    /**
     * 设置关注或取关
     * @param onSuccess
     */
    public void setAttent(String touid,final OnSuccess onSuccess) {
        onSuccess.sendHttp(onSuccess.getMyServer().setAttent(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),touid));
    }
}
