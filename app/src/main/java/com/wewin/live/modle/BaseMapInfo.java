package com.wewin.live.modle;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by fengjian on 2019/1/7.
 * 第一种数据格式
 */

public class BaseMapInfo {
    private int ret;
    private String msg;
    private Map data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    /**
     * 返回实体类
     * @param content
     * @return
     */
    public static BaseMapInfo getBaseMap(String content){
        try {
            return JSONObject.parseObject(content, BaseMapInfo.class);
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }

    /**
     * 返回状态码
     * @param map
     * @return
     */
    public static int getCode(BaseMapInfo map){
        try {
            if(map==null)return 100;
            return (int)map.getData().get(BaseInfoConstants.CODE);
        }catch (Exception e){
            e.printStackTrace();
        }
       return 100;
    }

    /**
     * 返回状态码是否成功
     * @param map
     * @return
     */
    public static boolean getSuccess(BaseMapInfo map){
        try {
            if (map==null)return false;
            if((int)map.getData().get(BaseInfoConstants.CODE)==0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       return false;
    }

    /**
     * 返回状态码是否token失效
     * @param map
     * @return
     */
    public static boolean isTokenInvalid(BaseMapInfo map){
        try {
            if(map==null)return false;
            if((int)map.getData().get(BaseInfoConstants.CODE)==700){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 返回信息提示
     * @param map
     * @return
     */
    public static String getMsg(BaseMapInfo map){
        try {
            if (map==null)return "数据异常";
            return map.getData().get(BaseInfoConstants.MSG)+"";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "数据异常";
    }

    /**
     * 返回数据集合
     * @param map
     * @return
     */
    public static List<Map> getInfo(BaseMapInfo map){
        List<Map> list = (List)map.getData().get(BaseInfoConstants.INFO);
        return list;
    }
}
