package com.wewin.live.ui.activity.Live;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.wewin.live.R;
import com.wewin.live.aliyun.LiveManage;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.dialog.LoadingProgressDialog;
import com.wewin.live.dialog.ShareDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.thirdparty.QqShare;
import com.wewin.live.thirdparty.WeiBoShare;
import com.wewin.live.ui.widget.HtmlWebView;
import com.wewin.live.ui.widget.VideoSurfceView;
import com.example.jasonutil.util.ActivityManage;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.NetWorkUtil;
import com.wewin.live.utils.OrientationWatchDog;
import com.example.jasonutil.util.ScreenTools;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 播放器详情
 */
public class VideoDetailsActivity extends BaseActivity {
    @InjectView(R.id.html_webview)
    HtmlWebView htmlWebview;
    @InjectView(R.id.rl_title_top)
    RelativeLayout rlTitleTop;
    @InjectView(R.id.ll_data)
    LinearLayout llData;
    @InjectView(R.id.live_surfce)
    VideoSurfceView liveSurfce;

    private String pull_url ="";//视频链接
    private String marquee_text;//跑马灯文本
    private String wx_number;//微信号码
    private String wx_image;//微信图标
    private String html5Url;//网页链接


    private NetWorkUtil netWorkUtil;//网络监听
    private LoadingProgressDialog mLoadingProgressDialog;


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
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent==null)return;
        WeiBoShare.getInstance().doResultIntent(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_details;
    }

    @Override
    protected void init() {
        initIntent();
        setBar();
        initNetWork();
        initVideo();
        initHtml();
    }

    /**
     * 获取传递过来数据
     */
    private void initIntent() {
        if(getIntent()!=null&&getIntent().getExtras()!=null) {
            pull_url = getIntent().getExtras().getString(BaseInfoConstants.PULL_URL);
            html5Url=getIntent().getExtras().getString(BaseInfoConstants.URL);
            marquee_text = getIntent().getExtras().getString(BaseInfoConstants.ADSCONTENT);
            wx_number=getIntent().getExtras().getString(BaseInfoConstants.WECHAT);
            wx_image=getIntent().getExtras().getString(BaseInfoConstants.WXIMAGE);
        }
    }


    private void initHtml() {
        htmlWebview.setHtml5Url(html5Url);
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
    private void initVideo() {
        liveSurfce.setRightPrompt(wx_number,wx_image);
        liveSurfce.setMarqueeContent(marquee_text);
        if (!StringUtils.isEmpty(LiveManage.getInstance().getUrl()) && LiveManage.getInstance().getUrl().equals(pull_url)) {
            liveSurfce.changeSufaceView();
            liveSurfce.start();

        } else {
            LiveManage.getInstance().setLiveSurfce(this, liveSurfce.getSurfaceView(), pull_url);
        }
        liveSurfce.setSwitchScreenListener(new VideoSurfceView.onSwitchScreenListener() {
            @Override
            public void amplificationOrNarrow() {
                //切换横竖屏
                OrientationWatchDog.changeHorizontalOrverticalScreen(VideoDetailsActivity.this);
            }
        });
    }

    @OnClick({R.id.bark, R.id.iv_more, R.id.iv_more_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                //收藏
                break;
            case R.id.iv_more_two:
                share();
                break;
        }
    }

    /**
     * 分享弹窗
     */
    private void share() {
        mLoadingProgressDialog = LoadingProgressDialog.createDialog(this);
        WeiBoShare.getInstance().init(this);
        final ShareDialog shareDialog = new ShareDialog(VideoDetailsActivity.this, new ArrayList<HashMap>());
        shareDialog
                .initData()
                .showAtLocation()
                .setListOnClick(new ShareDialog.ListOnClick() {
                    @Override
                    public void onclickitem(int position) {
                        if(position!=5)
                        mLoadingProgressDialog.showDialog();
                        shareDialog.goShare(VideoDetailsActivity.this, "http://testzhibo.wewin18.net", "标题", "描述","图片地址", position);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.SHARE_SUCCESS) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(VideoDetailsActivity.this, getString(R.string.share_success));
        } else if (msgId == MessageEvent.SHARE_FAIL) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(VideoDetailsActivity.this, "" + event.getError());
        } else if (msgId == MessageEvent.SHARE_CANCEL) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(VideoDetailsActivity.this, getString(R.string.share_cancel));
        }
    }


    /**
     * 界面改变
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = ScreenTools.getScreenWidth(this);
        int height = ScreenTools.getScreenHeight(this);
        //设置宽高，首先要找到外部包裹视频界面的布局
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) liveSurfce.getLayoutParams();
        params.width = width;
        //横屏
        if (OrientationWatchDog.isLandscape(this)) {
            params.height = height - ScreenTools.getStateBar3(this);
            changeView(true);
        }else if (OrientationWatchDog.isPoriratt(this)) {
            changeView(false);
            params.height = getResources().getDimensionPixelSize(R.dimen.d200dp);
        }
        liveSurfce.setLayoutParams(params);
    }

    /**
     * 横竖屏切换后界面更改
     * @param isHorizontal
     */
    private void changeView(boolean isHorizontal){
        if(isHorizontal){
            liveSurfce.setIvAmplification(true);
            rlTitleTop.setVisibility(View.GONE);
            llData.setVisibility(View.GONE);
        }else {
            liveSurfce.setIvAmplification(false);
            rlTitleTop.setVisibility(View.VISIBLE);
            llData.setVisibility(View.VISIBLE);
        }
    }

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
    protected void onDestroy() {
        //设置可以显示小窗口界面

        super.onDestroy();
        netWorkUtil.stopWatch();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, QqShare.getInstance().mIUiListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, QqShare.getInstance().mIUiListener);
            }
        }
    }
}
