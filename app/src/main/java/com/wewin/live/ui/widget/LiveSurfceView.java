package com.wewin.live.ui.widget;

/**
 * 作者：created by jason on 2019/4/3 10
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.listener.live.LiveListener;
import com.wewin.live.listener.live.LiveListenerManage;
import com.example.jasonutil.util.LogUtil;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;

/**
 * 作者：created by jason on ${DATA} 10
 * 直播观众
 */
public class LiveSurfceView extends RelativeLayout implements LiveListener {
    private Context mContext;
    private SurfaceView mSurfaceView;
    //各种提示控件
    private TextView mTvPrompt;
    //加载控件
    private LinearLayout mLlLoad;
    private ProgressBar mPbLoad;
    private TextView mTvLoad;

    public LiveSurfceView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LiveSurfceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public LiveSurfceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        View relativeLayout = View.inflate(mContext, R.layout.item_live_surfce, null);
        mSurfaceView = relativeLayout.findViewById(R.id.surfceview);
        mTvPrompt = relativeLayout.findViewById(R.id.tv_prompt);
        mLlLoad = relativeLayout.findViewById(R.id.ll_load);
        mPbLoad = relativeLayout.findViewById(R.id.pb_load);
        mTvLoad = relativeLayout.findViewById(R.id.tv_load);

        addView(relativeLayout);
        LiveListenerManage.getInstance().registerLiveListener(this);
        LogUtil.Log("走到了播放器的注册监听");

    }

    /**
     * 判断是直播还是观众
     */
    public void setIsLive(boolean isLive){
        if(isLive){
            mLlLoad.setVisibility(GONE);
        }
    }

    /**
     * 获取SurfaceView
     * @return
     */
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    /**
     * 改变播放显示界面。
     */
    public void changeSufaceView(){
        LiveManage.getInstance().changeSufaceView(getSurfaceView());
    }

    /**
     * 暂停或者播放触发
     */
    public void resumeOrPause() {
        if (LiveManage.getInstance().getIsPlaying()) {
            pause();
        } else {
            start();
        }
    }


    @Override
    public void showOrHideProgressBar(boolean isShow) {
        if (isShow && LiveManage.getInstance().getIsPlaying()) {
            if (mLlLoad.getVisibility() != VISIBLE) {
                mTvLoad.setText("");
                mLlLoad.setVisibility(VISIBLE);
                mTvPrompt.setVisibility(View.GONE);
            }
        } else {
            mLlLoad.setVisibility(GONE);
        }
    }

    @Override
    public void setLoadProgress(int proportion) {
        if(LiveManage.getInstance().getIsPlaying()) {
            mLlLoad.setVisibility(VISIBLE);
            mTvLoad.setText(proportion + "%");
            mTvPrompt.setVisibility(View.GONE);
        }else{
            mLlLoad.setVisibility(GONE);
        }
    }

    @Override
    public void initSeekBar(long duration) {

    }

    @Override
    public void setError(String content) {
        if (StringUtils.isEmpty(content)) return;
        mTvPrompt.setVisibility(VISIBLE);
        mTvPrompt.setText(content);
        mLlLoad.setVisibility(GONE);
    }

    @Override
    public void setVolume(int progress) {

    }

    @Override
    public void start() {
        if(LiveManage.getInstance().getIsNetwork()==LiveManage.MOBILE&&
                !MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_FOUR_G)) {
            LiveManage.getInstance().showfourG();
            return;
        }
        //如果缓存进度大于或等于当前进度，则表示可以正常播放，隐藏掉提示
        if(LiveManage.getInstance().getBufferingPosition()>=LiveManage.getInstance().getCurrentPosition()){
            mTvPrompt.setVisibility(View.GONE);
        }
        LiveManage.getInstance().start();
    }

    @Override
    public void pause() {
        LiveManage.getInstance().pause();
    }

    @Override
    public void stop() {
        LiveManage.getInstance().stop();
    }

    @Override
    public void resume() {
        LiveManage.getInstance().resume();
    }


    @Override
    public void destroyDrawingCache() {
        super.destroyDrawingCache();
        LiveListenerManage.getInstance().unregisterLiveListener(this);
        LogUtil.Log("走到了播放器的destroyDrawingCache");
    }
}

