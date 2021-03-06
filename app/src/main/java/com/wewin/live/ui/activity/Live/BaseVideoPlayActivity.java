package com.wewin.live.ui.activity.live;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.jasonutil.util.ActivityManage;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.NetWorkUtil;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.listanim.DefaultAnimation3;
import com.wewin.live.listanim.GiftAnimationLayout;
import com.wewin.live.listanim.GiftController;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.widget.GifImageView;
import com.wewin.live.listanim.GiftViewHolder;
import com.wewin.live.ui.widget.VideoSurfceView;
import com.wewin.live.utils.BarrageUtil;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.OrientationWatchDog;
import com.wewin.live.utils.down.DownloadService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.InjectView;

/**
 *  author:jason
 *  date:2019/5/1019:13
 *   播放器界面
 */
public abstract class BaseVideoPlayActivity extends BaseActivity {

    @InjectView(R.id.live_surfce)
    public VideoSurfceView liveSurfce;
    @InjectView(R.id.animation_view)
    LottieAnimationView animationView;
    @InjectView(R.id.gif_view)
    GifImageView gifView;
    @InjectView(R.id.layout_gift_1)
    GiftAnimationLayout layoutGift1;
    @InjectView(R.id.layout_gift_2)
    GiftAnimationLayout layoutGift2;
    @InjectView(R.id.layout_gift_3)
    GiftAnimationLayout layoutGift3;

    /**
     * 网络监听
     */
    protected NetWorkUtil netWorkUtil;
    /**
     * 视频链接
     */
    protected String pullUrl = "";
    /**
     * 跑马灯文本
     */
    protected String marqueeText;
    /**
     * 微信号码
     */
    protected String wxNumber;
    /**
     * 微信图标
     */
    protected String wxImage;

    private int layoutId;
    protected Bundle bundle;
    protected BarrageUtil barrageUtil;
    protected GiftController giftController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //先移除上次打开的播放界面，不移除会播放不了
        ActivityManage.exitActivity(getClass().getName());
        //再调用父类之前设置，不然会开启后再关闭
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, false);
        //关闭小窗口
        dismissWindow(false);
        super.onCreate(savedInstanceState);
        //常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected int getLayoutId() {
        return layoutId;
    }

    protected void setLayout(int layout) {
        layoutId = layout;
    }

    @Override
    protected void init() {
        initServer();
        initAnimationView();
        initGifView();
        initNetWork();
        initVideo();
        initDanmaku();
        initLiveGiftView();
        initChildData();
    }

    protected abstract void initChildData();

    /**
     * 初始化弹幕
     */
    private void initDanmaku() {
        barrageUtil = liveSurfce.getBarrageUtil();
    }



    @Override
    protected void getIntentData() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            pullUrl = getIntent().getExtras().getString(BaseInfoConstants.PULL_URL);
            marqueeText = getIntent().getExtras().getString(BaseInfoConstants.ADSCONTENT);
            wxNumber = getIntent().getExtras().getString(BaseInfoConstants.WECHAT);
            wxImage = getIntent().getExtras().getString(BaseInfoConstants.WXIMAGE);
        }
    }

    /**
     * 设置网络监听
     */
    private void initNetWork() {
        netWorkUtil = new NetWorkUtil(this);
        netWorkUtil.startWatch();
        netWorkUtil.setNetChangeListener(new NetWorkUtil.NetChangeListener() {
            @Override
            public void onWifiTo4G() {
                LiveManage.getInstance().setIsNetwork(LiveManage.MOBILE);
                LiveManage.getInstance().showfourG();
            }

            @Override
            public void on4GToWifi() {
                LiveManage.getInstance().setIsNetwork(LiveManage.WIFI);
            }

            @Override
            public void onNetDisconnected() {
                LiveManage.getInstance().setIsNetwork(LiveManage.NET_DISCONNECTED);
            }
        });
        if (NetWorkUtil.hasNet(this)) {
            if (NetWorkUtil.is4GConnected(this)) {
                LiveManage.getInstance().setIsNetwork(LiveManage.MOBILE);
            } else {
                LiveManage.getInstance().setIsNetwork(LiveManage.WIFI);
            }
        } else {
            LiveManage.getInstance().setIsNetwork(LiveManage.NET_DISCONNECTED);
        }
    }

    /**
     * 初始化播放器
     */
    protected void initVideo() {
        LiveManage.getInstance().setBundle(getIntent().getExtras());
        liveSurfce.setRightPrompt(wxNumber, wxImage);
        liveSurfce.setMarqueeContent(marqueeText);
        if (!StringUtils.isEmpty(LiveManage.getInstance().getUrl()) && LiveManage.getInstance().getUrl().equals(pullUrl)) {
            liveSurfce.changeSufaceView();
            liveSurfce.start();

        } else {
            LiveManage.getInstance().setLiveSurfce(this, liveSurfce.getSurfaceView(), pullUrl);
        }
        liveSurfce.setSwitchScreenListener(new VideoSurfceView.onSwitchScreenListener() {
            @Override
            public void amplificationOrNarrow() {
                //切换横竖屏
                OrientationWatchDog.changeHorizontalOrverticalScreen(BaseVideoPlayActivity.this);
            }
        });

    }


    private void initServer() {
        //不存在就启动服务
        if (!ActivityUtil.isServiceRunning(this, DownloadService.DOWN_LOAD_SERVICE_NAME)) {
            UtilTool.startDownServer(this, DownloadService.class, DownloadService.DOWN_LOAD_SERVICE_NAME);
        }
    }


    /**
     * 初始化连击礼物
     */
    private void initLiveGiftView(){
        giftController = new GiftController();
        giftController.append(layoutGift1, new GiftViewHolder(), new DefaultAnimation3(), true)
                .append(layoutGift2, new GiftViewHolder(), new DefaultAnimation3(), true)
                .append(layoutGift3, new GiftViewHolder(), new DefaultAnimation3(), true);
    }

    /**
     * 初始化json动画控件
     */
    private void initAnimationView() {
        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if ((int) animation.getAnimatedFraction() * 100 == 100) {
                    animationView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 初始化gif动画控件
     */
    private void initGifView() {
        gifView.setOnPlayListener(new GifImageView.OnPlayListener() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onPlaying(float percent) {

            }

            @Override
            public void onPlayPause(boolean pauseSuccess) {

            }

            @Override
            public void onPlayRestart() {

            }

            @Override
            public void onPlayEnd() {
                gifView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 下载回调
     */
    DownloadService.DownloadCallback mDownloadCallback = new DownloadService.DownloadCallback() {
        @Override
        public void onPrepare() {

        }

        @Override
        public void onProgress(int progress) {
            LogUtil.log("下载进度：" + progress);
        }

        @Override
        public void onComplete(final File file) {
            LogUtil.log("下载的文件：" + file.getAbsolutePath() + Thread.currentThread());
            if (file.getAbsolutePath().contains(FileUtil.FILE_JSON)) {
                animationView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream is = new FileInputStream(file);
                            JsonReader reader = new JsonReader(new InputStreamReader(is));
                            animationView.cancelAnimation();
                            animationView.setVisibility(View.VISIBLE);
                            animationView.setAnimation(reader);
                            animationView.playAnimation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                gifView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            gifView.setVisibility(View.VISIBLE);
                            gifView.setGifResource(file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onFail(String msg) {
            LogUtil.log("下载失败：" + msg);
        }
    };


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && OrientationWatchDog.isLandscape(this)) {
            liveSurfce.setSwitchScreen();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        //如果打开了返回打开小窗口播放则不杀死播放器
        if (MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_LITTLE_WINDOW)) {
            MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, true);
        } else {
            LiveManage.getInstance().release();
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barrageUtil != null) {
            barrageUtil.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barrageUtil != null) {
            barrageUtil.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animationView == null) {
            return;
        }
        animationView.cancelAnimation();
        if(isFinishing()){
            if(giftController!=null) {
                giftController.destroy();
            }
            //设置可以显示小窗口界面
            if (barrageUtil != null) {
                barrageUtil.onDestroy();
            }
            if (netWorkUtil != null) {
                netWorkUtil.stopWatch();
            }
        }
    }
}
