package com.wewin.live.newtwork;

import android.content.Context;

import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.utils.SignOutUtil;

import io.reactivex.Observable;

/**
 * @author jsaon
 * @date 2019/3/27
 */
public class OnSuccess implements OnSuccessAndFaultListener{
    //第一种数据map类型
    public static final int BASEMAPINFO=0;
    //第二种数据map类型
    public static final int BASEMAPINFO2=1;

    private Context mContext;
    //界面处理使用的回调
    private OnSuccessListener mOnSuccessListener;
    //业务处理类使用
    private OnPersenterListener mOnPersenterListener;
    //两种类型
    private int infoType=0;


    public OnSuccess(Context context,OnSuccessListener listener){
        this.mContext=context;
        mOnSuccessListener=listener;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * 界面不需要回调
     * @param context
     */
    public OnSuccess(Context context){
        this.mContext=context;
    }

    /**
     * 获取接口
     * @return
     */
    public MyService getMyServer(){
        MyService myService=RetrofitUtil.getInstance(mContext).getServer();
        return myService;
    }

    /**
     * 获取设置时间接口
     * @return
     */
    public MyService getMyServer(int time){
        MyService myService= RetrofitUtil.getInstance(mContext, time).getServer();
        return myService;
    }

    /**
     * 发送请求 默认弹窗
     * @param observable
     */
    public OnSuccess sendHttp(Observable observable){
        RetrofitUtil.getInstance(mContext).toSubscribe(observable, new OnSuccessAndFaultSub(this,mContext,true));
        return this;
    }

    /**
     * 发送请求 设置是否弹窗
     * @param observable
     *  @param isShow
     */
    public OnSuccess sendHttp(Observable observable,boolean isShow){
        RetrofitUtil.getInstance(mContext).toSubscribe(observable, new OnSuccessAndFaultSub(this,mContext,isShow));
        return this;
    }

    /**
     * 是否返回map
     * @param infoType
     */
    public OnSuccess setInfoType(int infoType){
        this.infoType=infoType;
        return this;
    }

    /**
     * 设置业务类回调
     * @param mOnPersenterListener
     */
    public OnSuccess setOnPersenterListener(OnPersenterListener mOnPersenterListener){
        this.mOnPersenterListener=mOnPersenterListener;
        return this;
    }

    @Override
    public void onFault(String content) {
        if (!ActivityUtil.isActivityOnTop(mContext)) return;
        ToastShow.showToast2(mContext, content);
        //业务处理类使用
        if(mOnPersenterListener!=null)
            mOnPersenterListener.onFault(content);
        if(mOnSuccessListener!=null)
            mOnSuccessListener.onFault(content);
    }

    @Override
    public void onSuccess(String content) {
        if (!ActivityUtil.isActivityOnTop(mContext)) return;
        if(infoType==BASEMAPINFO2){
            baseMapInfo2(content);
            return;
        }else if(infoType==BASEMAPINFO){
            baseMapInfo(content);
            return;
        }

    }

    /**
     * 第一种数据处理
     * @param content
     */
    private void baseMapInfo(String content){
        BaseMapInfo map = BaseMapInfo.getBaseMap(content);
        if (BaseMapInfo.getSuccess(map)) {
            //业务处理类使用
            if(mOnPersenterListener!=null)
                mOnPersenterListener.onSuccess(map);

            if(mOnSuccessListener!=null)
                mOnSuccessListener.onSuccess(map);
        } else {
            if (BaseMapInfo.isTokenInvalid(map)){
                SignOutUtil.signOut();
            }
            ToastShow.showToast2(mContext, BaseMapInfo.getMsg(map));
            //业务处理类使用
            if(mOnPersenterListener!=null)
                mOnPersenterListener.onFault(BaseMapInfo.getMsg(map));
            if(mOnSuccessListener!=null)
                mOnSuccessListener.onFault(BaseMapInfo.getMsg(map));
        }
    }

    /**
     * 第二种数据处理
     * @param content
     */
    private void baseMapInfo2(String content){
        BaseMapInfo2 map = BaseMapInfo2.getBaseMap(content);
        if (BaseMapInfo2.getSuccess(map)) {
            //业务处理类使用
            if(mOnPersenterListener!=null)
                mOnPersenterListener.onSuccess(map);

            if(mOnSuccessListener!=null)
                mOnSuccessListener.onSuccess(map);
        } else {
            if (BaseMapInfo2.isTokenInvalid(map)){
                SignOutUtil.signOut();
            }
            ToastShow.showToast2(mContext, BaseMapInfo2.getMsg(map));
            //业务处理类使用
            if(mOnPersenterListener!=null)
                mOnPersenterListener.onFault(BaseMapInfo2.getMsg(map));
            if(mOnSuccessListener!=null)
                mOnSuccessListener.onFault(BaseMapInfo2.getMsg(map));
        }
    }


    public interface OnSuccessListener {
        void onSuccess(Object content);
        void onFault(String error);
    }
}
