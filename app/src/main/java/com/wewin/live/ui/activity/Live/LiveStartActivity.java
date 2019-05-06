package com.wewin.live.ui.activity.Live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.NetWorkUtil;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.aliyun.LivePushControl;
import com.wewin.live.aliyun.LivePushManage;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.dialog.BeautyDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.widget.LiveSurfceView;
import com.wewin.live.utils.MySharedConstants;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 直播界面和播放界面
 * 由于直播播放界面和视频播放界面不同，所以使用两个界面编写
 */
public class LiveStartActivity extends BaseActivity implements LivePushControl.OnLicePushListener {


    @InjectView(R.id.surfceview)
    LiveSurfceView surfceview;
    @InjectView(R.id.bt_start)
    Button btStart;
    @InjectView(R.id.ll_live)
    LinearLayout llLive;
    @InjectView(R.id.seekbar_bgm)
    SeekBar seekbarBgm;
    @InjectView(R.id.seekbar_capture)
    SeekBar seekbarCapture;


    //直播工具类
    private LivePushManage livePushManage;
    //网络监听
    private NetWorkUtil netWorkUtil;
    //判断进入是直播还是观众，true直播
    private boolean isLive = true;
    //直播推流地址或者播放推流地址
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //关闭小弹窗
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, false);
        dismissWindow(true);
        super.onCreate(savedInstanceState);
        //常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_start;
    }

    @Override
    protected void init() {
        Bundle extras = getIntent().getExtras();
        isLive = extras.getBoolean(BaseInfoConstants.IS_LIVE);
        url = extras.getString(BaseInfoConstants.URL);
        setBar();
        if (isLive) {
            initLive();
        } else {
            audience();
        }
    }

    /**
     * 初始化live界面
     */
    private void initLive() {
        llLive.setVisibility(View.VISIBLE);
        surfceview.setIsLive(true);
        livePushManage = new LivePushManage(this, surfceview.getSurfaceView());
        livePushManage.setOnLicePushListener(this);
        livePushManage.setPushUrl(url);
        seekbarBgm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    livePushManage.setBGMVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbarCapture.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    livePushManage.setCaptureVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 初始化观众界面
     */
    private void audience() {
        surfceview.setIsLive(false);
        llLive.setVisibility(View.GONE);
        LiveManage.getInstance().setLiveSurfce(this, surfceview.getSurfaceView(), url);
        initNetWork();
    }

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


    @OnClick({R.id.bt_change, R.id.bt_setting, R.id.bt_start, R.id.bt_startMusic, R.id.bt_pauseMusic, R.id.bt_resumeMusic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_change:
                //切换摄像头
                livePushManage.switchCamera();
                break;
            case R.id.bt_setting:
                showBeautyDialog();
                break;
            case R.id.bt_start:
                if (!livePushManage.isPushing()) {
                    livePushManage.push("rtmp://by.j5349h.cn/5showcam/8198_2?auth_key=1554230820-0-0-82bd7d8e1c69f65bdfb773b0c5179998");
                } else {
                    livePushManage.stopPush();

                }
                break;
            case R.id.bt_startMusic:
                //打开背景音乐
                livePushManage.startBGMAsync(FileUtil.getAssetsDirString(LiveStartActivity.this) + "test.mp3");
//                livePushManage.startBGMAsync("/storage/emulated/0/alivc_resource/Pas de Deux.mp3");
                break;
            case R.id.bt_pauseMusic:
                //暂停
                livePushManage.pauseBGM();
                break;
            case R.id.bt_resumeMusic:
                //恢复
                livePushManage.resumeBGM();
                break;
        }
    }


    /**
     * 显示美颜设置
     */
    private void showBeautyDialog() {
        BeautyDialog beautyDialog = new BeautyDialog(this, livePushManage);
        beautyDialog.showAtLocation();
    }

    /**
     * 修改文本
     *
     * @param content
     */
    public void updatebtStart(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btStart.setText(content);
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        try {
            LiveManage.getInstance().release();
            if (netWorkUtil != null)
                netWorkUtil.stopWatch();
            //销毁
            if (livePushManage != null)
                livePushManage.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPushStarted() {
        updatebtStart("暂停推流");

    }

    @Override
    public void onPushStoped() {
        updatebtStart("开始推流");
    }

    @Override
    public void onPushPauesed() {
        updatebtStart("开始推流");
    }

    @Override
    public void onPushResumed() {
        updatebtStart("暂停推流");
    }

    @Override
    public void onSystemError() {
        updatebtStart("开始推流");
        LiveStartActivity.this.finish();
    }

    @Override
    public void onSDKError() {
        updatebtStart("开始推流");
    }

    @Override
    public void onPushRestarted() {
        updatebtStart("暂停推流");
    }
}
