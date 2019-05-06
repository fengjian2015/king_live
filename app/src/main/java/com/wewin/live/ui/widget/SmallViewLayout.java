package com.wewin.live.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.example.jasonutil.permission.AuthorizationCheck;
import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.base.MyApp;
import com.wewin.live.ui.activity.person.SettingsActivity;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/3/13
 * 小窗口管理
 */
public class SmallViewLayout {
    private SmallWindowView windowView;
    private WindowManager wm;
    private WindowManager.LayoutParams mLayoutParams;
    private Context mContext;

    private List<View> mViewList=new ArrayList<>();

    public SmallViewLayout(Context context){
        this.mContext=context;
    }

    public SmallWindowView getWindowView() {
        return windowView;
    }
    public WindowManager getWm() {
        return wm;
    }
    public WindowManager.LayoutParams getmLayoutParams() {
        return mLayoutParams;
    }
    /**
     * 初始化窗口
     *
     */
    public void initSmallViewLayout() {
        windowView = (SmallWindowView) LayoutInflater.from(mContext).inflate(R.layout.dialog_window, null);
        wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams(
                mContext.getResources().getDimensionPixelOffset(R.dimen.d220dp), mContext.getResources().getDimensionPixelOffset(R.dimen.d150dp),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.UNKNOWN);

        mLayoutParams.gravity = Gravity.NO_GRAVITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        //使用非CENTER时，可以通过设置XY的值来改变View的位置
        windowView.setWm(wm);
        windowView.setWmParams(mLayoutParams);
        windowView.setSmall();
    }

    public void setSV(){
        windowView.getSufceView().changeSufaceView();
    }

    public void alertWindow(Activity activity) {
        try {
            if(mViewList.size()>0)return;
            if(!MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW)
                    ||!MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_LITTLE_WINDOW))
                return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(mContext)) {
                    show();
                }
            }else {
                show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void show(){
        //这里重新创建，不然再次进入时无法控制里面的子view，这里导致，以后启动新的播放界面时，必须在弹出之前调用关闭
        initSmallViewLayout();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (wm != null&& windowView.getWm() != null&&windowView.getWindowId()==null) {
                wm.addView(windowView, mLayoutParams);
                MyApp.getInstance().mSmallViewLayout.setSV();
                mViewList.add(windowView);
            }
        }else {
            if (wm != null && windowView.getWm() != null) {
                wm.addView(windowView, mLayoutParams);
                mViewList.add(windowView);
            }
        }
    }


    // 移除window
    public void dismissWindow(boolean isDestroyLive) {
        try {
            if(isDestroyLive) MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, false);
            if(windowView==null)return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (wm != null && windowView != null&&windowView.getWindowId() != null) {
                    wm.removeView(windowView);
                    mViewList.remove(windowView);
                }
            }else {
                if (wm != null && windowView != null) {
                    wm.removeView(windowView);
                    mViewList.remove(windowView);
                }
            }
            if(isDestroyLive){
                LiveManage.getInstance().release();
            }
            windowView=null;
            LogUtil.Log("弹窗数量："+mViewList.size());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
