package com.wewin.live.modle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.Map;

/**
 * @author jsaon
 * @date 2019/3/27
 * 第二种数据格式
 */
public class BaseMapInfo2 {
    private String status;
    private Object data;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 返回实体类
     * @param content
     * @return
     */
    public static BaseMapInfo2 getBaseMap(String content){
        try {
            return JSON.parseObject(content, BaseMapInfo2.class);
//            return JSONArray.parseObject(content, BaseMapInfo2.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回状态码是否成功
     * @param map
     * @return
     */
    public static boolean getSuccess(BaseMapInfo2 map){
        try {
            if(map==null) {
                return false;
            }
            if("200".equals(map.status)){
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
    public static boolean isTokenInvalid(BaseMapInfo2 map){
        try {
            if(map==null) {
                return false;
            }
            if("201".equals(map.status)){
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
    public static String getMsg(BaseMapInfo2 map){
        try {
            if(map==null) {
                return "数据异常";
            }
            return map.message;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "数据异常";
    }

}
