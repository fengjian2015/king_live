package com.wewin.live.ui.activity.Live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.ui.widget.VideoSurfceView;
import com.example.jasonutil.util.ActivityManage;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.NetWorkUtil;

import butterknife.InjectView;

/**
 * 播放器大屏
 */
public class VideoBigActivity extends BaseActivity {

    @InjectView(R.id.live_surfce)
    VideoSurfceView liveSurfce;
    private String url = "";//视频链接
    private NetWorkUtil netWorkUtil;//网络监听


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //先移除上次打开的播放界面，不移除会播放不了
        ActivityManage.exitActivity(getClass().getName());
        //再调用父类,之前设置，不然会开启后再关闭
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, false);
        //关闭小窗口
        dismissWindow(false);
        super.onCreate(savedInstanceState);
        //常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live;
    }

    @Override
    protected void init() {
        setBar();
        initNetWork();
        initVideo();
    }

    /**
     * 初始化播放器
     */
    private void initVideo() {
//        if (!StringUtils.isEmpty(LiveManage.getInstance().getUrl()) && LiveManage.getInstance().getUrl().equals(url)) {
            liveSurfce.changeSufaceView();
            liveSurfce.start();
//        } else {
//            LiveManage.getInstance().setLiveSurfce(this, liveSurfce.getSurfaceView(), url);
//        }
        liveSurfce.setIvAmplification(true);
        liveSurfce.setSwitchScreenListener(new VideoSurfceView.onSwitchScreenListener() {
            @Override
            public void amplificationOrNarrow() {
                VideoBigActivity.this.finish();
            }
        });
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
    protected void onDestroy() {
        //设置可以显示小窗口界面

        super.onDestroy();
        netWorkUtil.stopWatch();
    }
}
