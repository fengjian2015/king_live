package com.wewin.live.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.MyLifecycleHandler;
import com.example.jasonutil.util.MySharedPreferences;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.wewin.live.utils.MySharedConstants;


/**
 * @author jsaon
 * @date 2019/3/13
 * 播放器全局窗口管理
 */
public class BaseLiveActivity extends AppCompatActivity {
    private boolean isRange = false;
    private int[] location = new int[2]; // 小窗口位置坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果是可显示状态改成显示
        if (MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW)) {
            MyApp.getInstance().mSmallViewLayout.alertWindow();
        }
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isRange = calcPointRange(event);
        }
        if (isRange) {
            if(MyApp.getInstance().mSmallViewLayout.getWindowView()==null) {
                super.dispatchTouchEvent(event);
            }
            MyApp.getInstance().mSmallViewLayout.getWindowView().dispatchTouchEvent(event);
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 计算当前点击事件坐标是否在小窗口内
     *
     * @param event
     * @return
     */
    public boolean calcPointRange(MotionEvent event) {
        if(MyApp.getInstance().mSmallViewLayout.getWindowView()==null) {
            return false;
        }
        MyApp.getInstance().mSmallViewLayout.getWindowView().getLocationOnScreen(location);
        int width = MyApp.getInstance().mSmallViewLayout.getWindowView().getMeasuredWidth();
        int height = MyApp.getInstance().mSmallViewLayout.getWindowView().getMeasuredHeight();
        float curX = event.getRawX();
        float curY = event.getRawY();
        if (curX >= location[0] && curX <= location[0] + width && curY >= location[1] && curY <= location[1] + height) {
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!MyLifecycleHandler.isApplicationVisible()) {
            dismissWindow(false);
        }
    }

    // 移除window
    public void dismissWindow(boolean isDestroyLive) {
        MyApp.getInstance().mSmallViewLayout.dismissWindow(isDestroyLive);
    }
}
