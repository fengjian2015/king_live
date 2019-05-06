package com.wewin.live.presenter;

import com.example.jasonutil.util.UtilTool;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.utils.SignOutUtil;

/*
 *   author:jason
 *   date:2019/4/2216:15
 *   公共的请求
 */
public class PersenterCommon {
    public static PersenterCommon instance = null;

    public static PersenterCommon getInstance() {
        if (instance == null) {
            instance = new PersenterCommon();
        }
        return instance;
    }

    /**
     * 搜索全部
     * @param onSuccess
     */
    public void searchAll(String keyword,final OnSuccess onSuccess) {
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().searchAll(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),keyword));
    }

    /**
     * 搜索单个
     * @param onSuccess
     */
    public void searchType(String keyword,String name,int page,final OnSuccess onSuccess) {
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().searchType(UtilTool.parseInt(SignOutUtil.getUserId()),SignOutUtil.getToken(),keyword,name,page,10),false);
    }

    /**
     * 获取热门搜索
     * @param onSuccess
     */
    public void getHotKeywords(final OnSuccess onSuccess) {
        onSuccess.setInfoType(OnSuccess.BASEMAPINFO2)
                .sendHttp(onSuccess.getMyServer().getHotKeywords(10),false);
    }
}
