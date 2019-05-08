package com.wewin.live.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.base.MyApp;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.activity.Live.VideoDetailsActivity;


/**
 * 小窗口弹窗显示
 */
public class SmallWindowView extends LinearLayout {
    private final int screenHeight;
    private final int screenWidth;
    private int statusHeight;
    //按下位置
    private float mTouchStartX;
    private float mTouchStartY;
    //移动位置
    private float mMoveStartX;
    private float mMoveStartY;
    private float x;
    private float y;
    boolean isRight = true;
    private int state = 0;//0是移动，1是关闭  2放大 3 暂停和播放
    private int[] location = new int[2]; // 小窗口位置坐标
    private float finish_x;
    private float finish_y;
    private float finish_w;
    private float finish_h;

    private float amplification_x;
    private float amplification_y;
    private float amplification_w;
    private float amplification_h;

    private float live_surfce_x;
    private float live_surfce_y;
    private float live_surfce_w;
    private float live_surfce_h;

    private WindowManager wm;
    public WindowManager.LayoutParams wmParams;
    private Context mContext;

    private VideoSurfceView live_surfce;
    private ImageView iv_finish;
    private ImageView iv_amp;
    //按下时间
    private long currentMS;
    //记录是否移动
    private int moveX;
    private int moveY;

    public SmallWindowView(Context context) {
        this(context, null);
        mContext = context;
    }

    public WindowManager getWm() {
        return wm;
    }

    public void setWm(WindowManager wm) {
        this.wm = wm;
    }

    public WindowManager.LayoutParams getWmParams() {
        return wmParams;
    }

    public void setWmParams(WindowManager.LayoutParams wmParams) {
        this.wmParams = wmParams;
        //wmParams.x表示的是布局中心的，屏幕原点在中心处
        this.wmParams.x = (screenWidth / 2) - wmParams.width + (wmParams.width / 2);
    }

    public SmallWindowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public SmallWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        statusHeight = getStatusHeight(context);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        mContext = context;
        init();

    }

    private void init() {
        View relativeLayout = View.inflate(mContext, R.layout.dialog_live_window, null);
        live_surfce = relativeLayout.findViewById(R.id.live_surfce);
        iv_finish = relativeLayout.findViewById(R.id.iv_finish);
        iv_amp = relativeLayout.findViewById(R.id.iv_amp);
        addView(relativeLayout);
    }

    public void setSmall() {
        live_surfce.setSmall();
    }

    public VideoSurfceView getSufceView() {
        return live_surfce;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    /**
     * 获取控件的位置和起始点
     */
    private void getData() {
        finish_x = iv_finish.getX();
        finish_y = iv_finish.getY();
        finish_w = iv_finish.getWidth() + iv_finish.getPaddingLeft() + iv_finish.getPaddingRight();
        finish_h = iv_finish.getHeight() + iv_finish.getPaddingTop() + iv_finish.getPaddingBottom();

        amplification_x = iv_amp.getX();
        amplification_y = iv_amp.getY();
        amplification_w = iv_amp.getWidth() + iv_amp.getPaddingLeft() + iv_amp.getPaddingRight();
        amplification_h = iv_amp.getHeight() + iv_amp.getPaddingTop() + iv_amp.getPaddingBottom();

        live_surfce_x = live_surfce.getX();
        live_surfce_y = live_surfce.getY();
        live_surfce_w = live_surfce.getWidth() + live_surfce.getPaddingLeft() + live_surfce.getPaddingRight();
        live_surfce_h = live_surfce.getHeight() + live_surfce.getPaddingTop() + live_surfce.getPaddingBottom();
    }

    /**
     * 判断范围
     */
    private void isRange() {
        if (mTouchStartX - location[0] >= finish_x && mTouchStartX - location[0] <= finish_x + finish_w
                && mTouchStartY - location[1] >= finish_y && mTouchStartY - location[1] <= finish_y + finish_h) {
            state = 1;
        } else if (mTouchStartX - location[0] >= amplification_x && mTouchStartX - location[0] <= amplification_x + amplification_w
                && mTouchStartY - location[1] >= amplification_y && mTouchStartY - location[1] <= amplification_y + amplification_h) {
            state = 2;
        } else if (mTouchStartX - location[0] >= live_surfce_x && mTouchStartX - location[0] <= live_surfce_x + live_surfce_w
                && mTouchStartY - location[1] >= live_surfce_y && mTouchStartY - location[1] <= live_surfce_y + live_surfce_h) {
            state = 3;
        } else {
            state = 0;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getLocationOnScreen(location);
        x = event.getRawX();
        y = event.getRawY() - statusHeight;
        getData();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (wmParams.x > 0) {
                    isRight = true;
                }
                if (wmParams.x < 0) {
                    isRight = false;
                }
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                isRange();
                oldY = wmParams.y;
                oldX = wmParams.x;
                moveX = 0;
                moveY = 0;
                currentMS = System.currentTimeMillis();//long currentMS     获取系统时间
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveStartX = event.getRawX();
                mMoveStartY = event.getRawY();
                moveX += Math.abs(event.getX() - mTouchStartX);//X轴距离
                moveY += Math.abs(event.getY() - mTouchStartY);//y轴距离
                if (state == 3||state==0) {
                    updateViewPosition();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state == 1) {
                    MyApp.getInstance().mSmallViewLayout.dismissWindow(true);
                } else if (state == 2) {
                    goBigView();
                } else {
                    long moveTime = System.currentTimeMillis() - currentMS;//移动时间
                    //判断是否继续传递信号
                    if(moveTime<=200&&moveX<20&&moveY<20){
                        live_surfce.resumeOrPause();
                    }

                    if (wmParams.x <= 0) {
                        wmParams.x=-(screenWidth / 2)+(wmParams.width / 2);
                    } else {
                        wmParams.x=(screenWidth / 2) - wmParams.width + (wmParams.width / 2);
                    }
                    wm.updateViewLayout(this, wmParams);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void goBigView() {
        Intent intent = new Intent(mContext, VideoDetailsActivity.class);
        Bundle bundle=LiveManage.getInstance().getBundle();
        bundle.putBoolean(BaseInfoConstants.IS_HORIZONTAL,true);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private float oldY;
    private float oldX;

    private void updateViewPosition() {
        wmParams.gravity = Gravity.NO_GRAVITY;
        float move_x=(oldX + (mMoveStartX - mTouchStartX));
        float move_y=(oldY + (mMoveStartY - mTouchStartY));
        //获取可移动范围
        int max_x = (screenWidth / 2) - wmParams.width + (wmParams.width / 2);
        int min_x=-(screenWidth / 2)+(wmParams.width / 2);
        int max_y= (screenHeight / 2) - wmParams.height + (wmParams.height / 2);
        int min_y=-(screenHeight / 2) +(wmParams.height / 2);

        if(move_x>max_x){
            wmParams.x=max_x;
        }else if(move_x<min_x){
            wmParams.x=min_x;
        }else{
            wmParams.x= (int) move_x;
        }

        if(move_y>max_y){
            wmParams.y=max_y;
        }else if(move_y<min_y){
            wmParams.y=min_y;
        }else{
            wmParams.y= (int) move_y;
        }

        wm.updateViewLayout(this, wmParams);
    }


}
