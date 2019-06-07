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
import com.wewin.live.ui.activity.live.VideoDetailsActivity;


/**
 * 小窗口弹窗显示
 */
public class SmallWindowView extends LinearLayout {
    private final int screenHeight;
    private final int screenWidth;
    private int statusHeight;
    /**
     * 按下位置
     */
    private float mTouchStartX;
    private float mTouchStartY;
    /**
     * 移动位置
     */
    private float mMoveStartX;
    private float mMoveStartY;
    private float x;
    private float y;
    boolean isRight = true;
    /**
     * 0是移动，1是关闭  2放大 3 暂停和播放
     */
    private int state = 0;
    /**
     * 小窗口位置坐标
     */
    private int[] location = new int[2];
    private float finishX;
    private float finishY;
    private float finishW;
    private float finishH;

    private float amplificationX;
    private float amplificationY;
    private float amplificationW;
    private float amplificationH;

    private float liveSurfceX;
    private float liveSurfceY;
    private float liveSurfceW;
    private float liveSurfceH;

    private WindowManager wm;
    public WindowManager.LayoutParams wmParams;
    private Context mContext;

    private VideoSurfceView liveSurfce;
    private ImageView ivFinish;
    private ImageView ivAmp;
    /**
     * 按下时间
     */
    private long currentMS;
    /**
     * 记录是否移动
     */
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
        liveSurfce = relativeLayout.findViewById(R.id.live_surfce);
        ivFinish = relativeLayout.findViewById(R.id.iv_finish);
        ivAmp = relativeLayout.findViewById(R.id.iv_amp);
        addView(relativeLayout);
    }

    public void setSmall() {
        liveSurfce.setSmall();
    }

    public VideoSurfceView getSurfceView() {
        return liveSurfce;
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
        finishX = ivFinish.getX();
        finishY = ivFinish.getY();
        finishW = ivFinish.getWidth() + ivFinish.getPaddingLeft() + ivFinish.getPaddingRight();
        finishH = ivFinish.getHeight() + ivFinish.getPaddingTop() + ivFinish.getPaddingBottom();

        amplificationX = ivAmp.getX();
        amplificationY = ivAmp.getY();
        amplificationW = ivAmp.getWidth() + ivAmp.getPaddingLeft() + ivAmp.getPaddingRight();
        amplificationH = ivAmp.getHeight() + ivAmp.getPaddingTop() + ivAmp.getPaddingBottom();

        liveSurfceX = liveSurfce.getX();
        liveSurfceY = liveSurfce.getY();
        liveSurfceW = liveSurfce.getWidth() + liveSurfce.getPaddingLeft() + liveSurfce.getPaddingRight();
        liveSurfceH = liveSurfce.getHeight() + liveSurfce.getPaddingTop() + liveSurfce.getPaddingBottom();
    }

    /**
     * 判断范围
     */
    private void isRange() {
        if (mTouchStartX - location[0] >= finishX && mTouchStartX - location[0] <= finishX + finishW
                && mTouchStartY - location[1] >= finishY && mTouchStartY - location[1] <= finishY + finishH) {
            state = 1;
        } else if (mTouchStartX - location[0] >= amplificationX && mTouchStartX - location[0] <= amplificationX + amplificationW
                && mTouchStartY - location[1] >= amplificationY && mTouchStartY - location[1] <= amplificationY + amplificationH) {
            state = 2;
        } else if (mTouchStartX - location[0] >= liveSurfceX && mTouchStartX - location[0] <= liveSurfceX + liveSurfceW
                && mTouchStartY - location[1] >= liveSurfceY && mTouchStartY - location[1] <= liveSurfceY + liveSurfceH) {
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
                    //移动时间
                    long moveTime = System.currentTimeMillis() - currentMS;
                    //判断是否继续传递信号
                    if(moveTime<=200&&moveX<20&&moveY<20){
                        liveSurfce.resumeOrPause();
                    }

                    if (wmParams.x <= 0) {
                        wmParams.x=-(screenWidth / 2)+(wmParams.width / 2);
                    } else {
                        wmParams.x=(screenWidth / 2) - wmParams.width + (wmParams.width / 2);
                    }
                    wm.updateViewLayout(this, wmParams);
                }
                break;
            default:
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
        float moveX=(oldX + (mMoveStartX - mTouchStartX));
        float moveY=(oldY + (mMoveStartY - mTouchStartY));
        //获取可移动范围
        int maxX = (screenWidth / 2) - wmParams.width + (wmParams.width / 2);
        int minX=-(screenWidth / 2)+(wmParams.width / 2);
        int maxY= (screenHeight / 2) - wmParams.height + (wmParams.height / 2);
        int minY=-(screenHeight / 2) +(wmParams.height / 2);

        if(moveX>maxX){
            wmParams.x=maxX;
        }else if(moveX<minX){
            wmParams.x=minX;
        }else{
            wmParams.x= (int) moveX;
        }

        if(moveY>maxY){
            wmParams.y=maxY;
        }else if(moveY<minY){
            wmParams.y=minY;
        }else{
            wmParams.y= (int) moveY;
        }

        wm.updateViewLayout(this, wmParams);
    }


}
