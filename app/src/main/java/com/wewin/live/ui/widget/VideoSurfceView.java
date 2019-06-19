package com.wewin.live.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.listener.live.LiveListener;
import com.wewin.live.listener.live.LiveListenerManage;
import com.example.jasonutil.util.DateUtil;
import com.example.jasonutil.util.LogUtil;
import com.wewin.live.utils.BarrageUtil;
import com.wewin.live.utils.GlideUtil;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;

import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * @author jsaon
 * @date 2019/3/2
 * 播放界面类（可替换播放器）
 */
public class VideoSurfceView extends RelativeLayout implements View.OnClickListener, LiveListener, View.OnTouchListener {
    private Context mContext;
    private SurfaceView mSurfaceView;

    //更新时间和进度条
    private final int TIME = 1;
    //隐藏状态栏
    private final int HIDE_STATUS_BAR = 2;
    //隐藏音量
    private final int HIDE_STATUS_VOLUME = 3;

    //加载控件
    private LinearLayout mLlLoad;
    private TextView mTvLoad;
    //各种提示控件
    private TextView mTvPrompt;
    //状态栏控件
    private LinearLayout mRlTransparent;
    //进度条控件
    private SeekBar mSeekBar;
    //开始和暂停控件
    private ImageView mIvSwitch;
    //大屏幕上的点击开始控件
    private ImageView mIvStart;
    //时间控件
    private TextView mTvTime;
    //音量显示控件
    private TextView mTvVolume;
    //全屏和小屏
    private ImageView mIvAmplification;
    //音量显示控件
    private RelativeLayout mRlVoluem;
    //跑马灯效果
    private RelativeLayout rlMarquee;
    //跑马灯文本
    private ScrollTextView tvMarquee;
    //右上角提示内容
    private TextView tvRightPrompt;
    private LinearLayout llRightPrompt;
    private ImageView ivRightPrompt;
    //弹幕控件
    private DanmakuView mDanmakuView;
    //弹幕开关
    private ImageView mIvBarrage;
    //推荐直播
    private LinearLayout llRecommend;
    private ImageView ivRecommend;
    //----------------功能块参数
    //当前进度
    private long current = 0;
    //缓存进度
    private long buffering = 0;
    //状态栏显示时长
    private int SHOW_TRANSPARENT = 6 * 1000;
    //音量显示时长
    private int SHOW_VOLUME = 4 * 1000;
    //触摸数据
    private float x1, x2, y1, y2;
    //固定住上下或者左右滑动//0未固定/1左右/2上下
    private int LR_UD = 0;
    //记录滑动时音量初始值
    private int volume_slide = 0;
    //记录滑动时亮度初始值
    private int brightness_slide = 0;
    //判断是否继续滚动seekbar
    private boolean isSeekPlay = true;
    //弹幕工具类
    private BarrageUtil barrageUtil;
    //是否可以显示推荐直播
    private boolean recommendShow = false;

    //监听横竖屏切换点击
    private onSwitchScreenListener mSwitchScreenListener;
    //监听控制面板是否隐藏
    private OnControlShowOrHint mOnControlShowOrHint;
    //监听点击推荐直播
    private OnRecommendListener mOnRecommendListener;


    public VideoSurfceView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public VideoSurfceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        View relativeLayout = View.inflate(mContext, R.layout.item_video_surfce, null);
        mSurfaceView = relativeLayout.findViewById(R.id.surfceview);
        mLlLoad = relativeLayout.findViewById(R.id.ll_load);
        mTvLoad = relativeLayout.findViewById(R.id.tv_load);
        mTvPrompt = relativeLayout.findViewById(R.id.tv_prompt);
        mSeekBar = relativeLayout.findViewById(R.id.seekBar);
        mRlTransparent = relativeLayout.findViewById(R.id.rl_transparent);
        mIvSwitch = relativeLayout.findViewById(R.id.iv_switch);
        mTvTime = relativeLayout.findViewById(R.id.tv_time);
        mIvStart = relativeLayout.findViewById(R.id.iv_start);
        mTvVolume = relativeLayout.findViewById(R.id.tv_volume);
        mIvAmplification = relativeLayout.findViewById(R.id.iv_amplification);
        mRlVoluem = relativeLayout.findViewById(R.id.rl_voluem);
        rlMarquee = relativeLayout.findViewById(R.id.rl_marquee);
        tvMarquee = relativeLayout.findViewById(R.id.tv_marquee);
        tvRightPrompt = relativeLayout.findViewById(R.id.tv_right_prompt);
        llRightPrompt = relativeLayout.findViewById(R.id.ll_right_prompt);
        ivRightPrompt = relativeLayout.findViewById(R.id.iv_right_prompt);
        mDanmakuView = relativeLayout.findViewById(R.id.sv_danmaku);
        mIvBarrage = relativeLayout.findViewById(R.id.iv_barrage);
        ivRecommend = relativeLayout.findViewById(R.id.iv_recommend);
        llRecommend = relativeLayout.findViewById(R.id.ll_recommend);

        barrageUtil = new BarrageUtil(mContext, mDanmakuView);
        setListener();
        addView(relativeLayout);
        showOrHideTransparent(true);
        selectPlayOrPause(true);
        LiveListenerManage.getInstance().registerLiveListener(this);
        LogUtil.log("走到了播放器的注册监听");
    }


    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public BarrageUtil getBarrageUtil() {
        return barrageUtil;
    }

    public void setSwitchScreenListener(onSwitchScreenListener onSwitchScreenListener) {
        mSwitchScreenListener = onSwitchScreenListener;
    }

    /**
     * 设置控件面板监听
     *
     * @param onControlShowOrHint
     */
    public void setOnControlShowOrHint(OnControlShowOrHint onControlShowOrHint) {
        mOnControlShowOrHint = onControlShowOrHint;
    }

    public void setOnRecommendListener(OnRecommendListener onRecommendListener){
        mOnRecommendListener=onRecommendListener;
    }

    /**
     * 改变播放显示界面，默认隐藏加载框和进度条等。
     */
    public void changeSufaceView() {
        LiveManage.getInstance().changeSufaceView(getSurfaceView());
        mLlLoad.setVisibility(GONE);
        mRlTransparent.setVisibility(GONE);
        showOrHideMarqueeAndRightPrompt(true);
    }

    /**
     * 小屏需要做的处理
     */
    public void setSmall() {
        mTvTime.setVisibility(GONE);
        mSeekBar.setVisibility(INVISIBLE);
        mIvBarrage.setVisibility(GONE);
//        mSurfaceView.setZOrderOnTop(true);
    }

    /**
     * 设置是否允许显示推荐直播
     */
    public void setRecommendShow(boolean show,String cover) {
        recommendShow = show;
        GlideUtil.setImg(mContext,cover,ivRecommend,0);
    }

    /**
     * 是否显示加载框
     *
     * @param isShow
     */
    @Override
    public void showOrHideProgressBar(boolean isShow) {
        if (isShow && LiveManage.getInstance().getIsPlaying()) {
            if (mLlLoad.getVisibility() != VISIBLE) {
                mTvLoad.setText("");
                mLlLoad.setVisibility(VISIBLE);
                mTvPrompt.setVisibility(View.GONE);
                llRecommend.setVisibility(GONE);
            }
        } else {
            mLlLoad.setVisibility(GONE);
        }
    }

    /**
     * 设置当前加载比例
     *
     * @param proportion
     */
    @Override
    public void setLoadProgress(int proportion) {
        if (LiveManage.getInstance().getIsPlaying()) {
            mLlLoad.setVisibility(VISIBLE);
            mTvLoad.setText(proportion + "%");
            mTvPrompt.setVisibility(View.GONE);
            llRecommend.setVisibility(GONE);
        } else {
            mLlLoad.setVisibility(GONE);
        }
    }

    /**
     * 视频播放报错给予提示
     *
     * @param content
     */
    @Override
    public void setError(String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        if (recommendShow) {
            llRecommend.setVisibility(VISIBLE);
            mTvPrompt.setVisibility(GONE);
        }else {
            llRecommend.setVisibility(GONE);
            mTvPrompt.setVisibility(VISIBLE);
            mTvPrompt.setText(content);
        }
        mLlLoad.setVisibility(GONE);
    }

    /**
     * 初始化进度条
     */
    @Override
    public void initSeekBar(long duration) {
        if (duration <= 0) {
            mIvBarrage.setVisibility(VISIBLE);
            barrageUtil.start();
            mSeekBar.setVisibility(INVISIBLE);
            mTvTime.setVisibility(GONE);
            return;
        } else {
            mIvBarrage.setVisibility(GONE);
            mSeekBar.setVisibility(VISIBLE);
            mTvTime.setVisibility(VISIBLE);
        }
        mSeekBar.setMax((int) duration);
        mSeekBar.setProgress(0);
        if (duration <= 0) {
            mSeekBar.setOnTouchListener(null);
        } else {
            new seekThere().start();
        }
    }

    /**
     * 设置视频进度条
     *
     * @param current
     */
    public void setSeekBar(long current) {
        setTime(DateUtil.getRemaining(LiveManage.getInstance().getDuration()), DateUtil.getRemaining(current));
        mSeekBar.setProgress((int) current);
    }

    /**
     * 设置加载进度
     *
     * @param buffering
     */
    public void setSeekBuffering(long buffering) {
        mSeekBar.setSecondaryProgress((int) buffering);
    }

    /**
     * 设置时间
     *
     * @param totalTime
     * @param currentTime
     */
    public void setTime(String totalTime, String currentTime) {
        mTvTime.setText(currentTime + "/" + totalTime);
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

    /**
     * 暂停
     */
    @Override
    public void pause() {
        selectPlayOrPause(false);
        LiveManage.getInstance().pause();
    }

    /**
     * 停止播放
     */
    @Override
    public void stop() {
        LiveManage.getInstance().stop();
        selectPlayOrPause(false);
    }

    @Override
    public void resume() {

    }

    /**
     * 播放
     */
    @Override
    public void start() {
        if (LiveManage.getInstance().getIsNetwork() == LiveManage.MOBILE &&
                !MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_FOUR_G)) {
            LiveManage.getInstance().showfourG();
            return;
        }
        //如果缓存进度大于或等于当前进度，则表示可以正常播放，隐藏掉提示
        if (LiveManage.getInstance().getBufferingPosition() >= LiveManage.getInstance().getCurrentPosition()) {
            mTvPrompt.setVisibility(View.GONE);
        }
        selectPlayOrPause(true);
        LiveManage.getInstance().start();
    }

    /**
     * 暂停或者播放，改变图片显示
     *
     * @param isPaly
     */
    public void selectPlayOrPause(boolean isPaly) {
        if (isPaly) {
            mIvSwitch.setSelected(true);
            mIvStart.setVisibility(GONE);
        } else {
            mIvSwitch.setSelected(false);
            mIvStart.setVisibility(VISIBLE);
        }
    }

    /**
     * 全屏和小屏图片切换
     *
     * @param isAmplification true全屏状态
     */
    public void setIvAmplification(boolean isAmplification) {
        if (isAmplification) {
            mIvAmplification.setSelected(true);
        } else {
            mIvAmplification.setSelected(false);
        }
    }

    /**
     * 切换全屏和小屏
     */
    public void setSwitchScreen() {
        //全屏和小屏
        if (mSwitchScreenListener != null) {
            mSwitchScreenListener.amplificationOrNarrow();
        }
        showOrHideTransparent(true);
    }

    /**
     * 设置音量进度条
     *
     * @param progress
     */
    @Override
    public void setVolume(int progress) {
        mTvVolume.setText(mContext.getString(R.string.volume) + progress + "%");
    }

    /**
     * 设置亮度进度条
     *
     * @param progress
     */
    public void setBrightness(int progress) {
        mTvVolume.setText(mContext.getString(R.string.brightness) + progress + "%");
    }


    /**
     * 显示与隐藏音量或亮度控件
     *
     * @param isShow
     */
    public void showOrHideVolume(boolean isShow) {
        if (isShow) {
            mHandler.removeMessages(HIDE_STATUS_VOLUME);
            mRlVoluem.setVisibility(VISIBLE);
            mHandler.sendEmptyMessageDelayed(HIDE_STATUS_VOLUME, SHOW_VOLUME);
        } else {
            mRlVoluem.setVisibility(GONE);
        }
    }

    /**
     * 打开或者关闭弹幕
     */
    public void openOrCloseBarrage() {
        if (mIvBarrage.isSelected()) {
            //打开
            mIvBarrage.setSelected(false);
        } else {
            //关闭
            mIvBarrage.setSelected(true);
        }
        barrageUtil.showOrHide(!mIvBarrage.isSelected());
    }

    /**
     * 显示与隐藏状态栏
     *
     * @param isShow 显示
     */
    public void showOrHideTransparent(boolean isShow) {
        if (isShow) {
            mHandler.removeMessages(HIDE_STATUS_BAR);
            mRlTransparent.setVisibility(VISIBLE);
            mHandler.sendEmptyMessageDelayed(HIDE_STATUS_BAR, SHOW_TRANSPARENT);
        } else {
            mRlTransparent.setVisibility(GONE);
        }
        showOrHideMarqueeAndRightPrompt(!isShow);
        if (mOnControlShowOrHint != null) {
            mOnControlShowOrHint.control(isShow);
        }
    }

    public boolean getTransparentShow() {
        if (mRlTransparent.isShown()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 一直循环设置seekbar和时间
     */
    class seekThere extends Thread {
        @Override
        public void run() {
            try {
                while (isSeekPlay) {
                    // 如果正在播放，没0.5.毫秒更新一次进度条
                    current = LiveManage.getInstance().getCurrentPosition();
                    buffering = LiveManage.getInstance().getBufferingPosition();
                    mHandler.sendEmptyMessage(TIME);
                    sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 滑动设置音量
     *
     * @param x
     */

    private void setSlideVolume(float x) {
        float max = mSurfaceView.getMeasuredHeight() / 3;
        int seek = (int) ((100 / max) * x / 2);
        int last = volume_slide - seek;
        if (last > 100) {
            last = 100;
        }
        if (last < 0) {
            last = 0;
        }
        setVolume(last);
        LiveManage.getInstance().setVolume(last);
    }

    /**
     * 滑动设置亮度
     *
     * @param x
     */
    private void setSlideBrightness(float x) {
        float max = mSurfaceView.getMeasuredHeight() / 3;
        int seek = (int) ((100 / max) * x / 2);
        int last = brightness_slide - seek;
        if (last > 100) {
            last = 100;
        }
        if (last < 0) {
            last = 0;
        }
        setBrightness(last);
        LiveManage.getInstance().setScreenBrightness(last);
    }

    /**
     * 滑动设置进度
     *
     * @param x
     */
    private long seek_slide = 0;

    private void setSlideSeek(float x) {
        float max = mSurfaceView.getMeasuredWidth() / 3 * 2;
        int seek = (int) ((LiveManage.getInstance().getDuration() / max) * x);
        long last = seek_slide + seek;

        if (last > LiveManage.getInstance().getDuration()) {
            last = LiveManage.getInstance().getDuration();
        }
        if (last < 0) {
            last = 0;
        }
        LiveManage.getInstance().setSeek((int) last);
        setSeekBar(last);

    }

    /**
     * 设置右上角文本
     *
     * @param content
     */
    public void setRightPrompt(String content, String imageUrl) {
        LiveManage.getInstance().setRightPrompt(content);
        LiveManage.getInstance().setRightImage(imageUrl);
        if (StringUtils.isEmpty(content)) {
            llRightPrompt.setVisibility(GONE);
        } else {
            llRightPrompt.setVisibility(VISIBLE);
            tvRightPrompt.setText(LiveManage.getInstance().getRightPrompt());
            GlideUtil.setImg(mContext, LiveManage.getInstance().getRightImage(), ivRightPrompt, 0);
        }
    }

    /**
     * 设置跑马灯文本
     *
     * @param content
     */
    public void setMarqueeContent(String content) {
        LiveManage.getInstance().setMarqueeContent(content);
    }

    /**
     * 判断是否显示与隐藏跑马灯
     */
    private void showOrHideMarqueeAndRightPrompt(boolean isShow) {
        if (!isShow) {
            rlMarquee.setVisibility(GONE);
            tvMarquee.setVisibility(GONE);
        } else if (!StringUtils.isEmpty(LiveManage.getInstance().getMarqueeContent())) {
            rlMarquee.setVisibility(VISIBLE);
            tvMarquee.setVisibility(VISIBLE);
            tvMarquee.setText(LiveManage.getInstance().getMarqueeContent());
        } else {
            rlMarquee.setVisibility(GONE);
            tvMarquee.setVisibility(GONE);
        }
        if (!StringUtils.isEmpty(LiveManage.getInstance().getRightPrompt())) {
            tvRightPrompt.setText(LiveManage.getInstance().getRightPrompt());
            llRightPrompt.setVisibility(VISIBLE);
            GlideUtil.setImg(mContext, LiveManage.getInstance().getRightImage(), ivRightPrompt, 0);
        } else {
            llRightPrompt.setVisibility(GONE);
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME:
                    setSeekBar(current);
                    setSeekBuffering(buffering);
                    break;
                case HIDE_STATUS_BAR:
                    showOrHideTransparent(false);
                    break;
                case HIDE_STATUS_VOLUME:
                    showOrHideVolume(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                //播放暂停
                resumeOrPause();
                showOrHideTransparent(true);
                break;
            case R.id.surfceview:
                if (mRlTransparent.getVisibility() == VISIBLE) {
                    resumeOrPause();
                }
                showOrHideTransparent(true);
                break;
            case R.id.iv_amplification:
                setSwitchScreen();
                break;
            case R.id.iv_barrage:
                openOrCloseBarrage();
                break;
            case R.id.ll_recommend:
                mOnRecommendListener.onClick();
                break;
            default:
                break;
        }
    }

    private void setListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    LiveManage.getInstance().setSeek(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mIvBarrage.setOnClickListener(this);
        mSurfaceView.setOnClickListener(this);
        mIvSwitch.setOnClickListener(this);
        mIvAmplification.setOnClickListener(this);
        mSurfaceView.setOnTouchListener(this);
        llRecommend.setOnClickListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LR_UD = 0;
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                if (LR_UD == 0) {
                    if (y1 - y2 > 50) {
                        y1 = y2;
                        LR_UD = 2;
                        brightness_slide = LiveManage.getInstance().getScreenBrightness();
                        volume_slide = LiveManage.getInstance().getVolume();
//                        LogUtil.log("向上滑");
                    } else if (y2 - y1 > 50) {
                        y1 = y2;
                        LR_UD = 2;
                        brightness_slide = LiveManage.getInstance().getScreenBrightness();
                        volume_slide = LiveManage.getInstance().getVolume();
//                        LogUtil.log("向下滑");
                    } else if (x1 - x2 > 50) {
                        x1 = x2;
                        LR_UD = 1;
                        seek_slide = LiveManage.getInstance().getCurrentPosition();
//                        LogUtil.log("向左滑");
                    } else if (x2 - x1 > 50) {
                        x1 = x2;
                        LR_UD = 1;
                        seek_slide = LiveManage.getInstance().getCurrentPosition();
//                        LogUtil.log("向右滑");
                    }
                } else {
                    if (y1 - y2 > 1 && LR_UD == 2) {
                        if (x1 <= getMeasuredWidth() / 2) {
                            setSlideVolume(y2 - y1);
                        } else {
                            setSlideBrightness(y2 - y1);
                        }
                        showOrHideVolume(true);
//                        LogUtil.log("向上滑");
                    } else if (y2 - y1 > 1 && LR_UD == 2) {
                        if (x1 <= getMeasuredWidth() / 2) {
                            setSlideVolume(y2 - y1);
                        } else {
                            setSlideBrightness(y2 - y1);
                        }
                        showOrHideVolume(true);
//                        LogUtil.log("向下滑");
                    } else if (x1 - x2 > 1 && LR_UD == 1) {
                        setSlideSeek(x2 - x1);
                        showOrHideTransparent(true);
//                        LogUtil.log("向左滑");
                    } else if (x2 - x1 > 1 && LR_UD == 1) {
                        setSlideSeek(x2 - x1);
                        showOrHideTransparent(true);
//                        LogUtil.log("向右滑");
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if (LR_UD != 0) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void destroyDrawingCache() {
        super.destroyDrawingCache();
        LiveListenerManage.getInstance().unregisterLiveListener(this);
        isSeekPlay = false;
        LogUtil.log("走到了播放器的destroyDrawingCache");
    }


    public interface onSwitchScreenListener {
        void amplificationOrNarrow();
    }

    public interface OnControlShowOrHint {
        void control(boolean isShow);
    }

    public interface OnRecommendListener {
        void onClick();
    }
}
