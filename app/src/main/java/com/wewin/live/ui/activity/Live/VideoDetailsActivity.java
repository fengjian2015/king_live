package com.wewin.live.ui.activity.Live;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonutil.util.ScreenTools;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.wewin.live.R;
import com.wewin.live.dialog.LoadingProgressDialog;
import com.wewin.live.dialog.ShareDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.rxjava.OnRxJavaProcessListener;
import com.wewin.live.rxjava.RxJavaObserver;
import com.wewin.live.rxjava.RxJavaScheduler;
import com.wewin.live.thirdparty.QqShare;
import com.wewin.live.thirdparty.WeiBoShare;
import com.wewin.live.ui.widget.web.HtmlWebView;
import com.wewin.live.ui.widget.VideoSurfceView;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.OrientationWatchDog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;

import static com.wewin.live.utils.MessageEvent.DOWN_ANIMATION;
import static com.wewin.live.utils.MessageEvent.DOWN_GIF;

/**
 * 播放器详情
 */
public class VideoDetailsActivity extends BaseVideoPlayActivity {

    @InjectView(R.id.html_webview)
    HtmlWebView htmlWebview;
    @InjectView(R.id.rl_title)
    LinearLayout rl_title;
    @InjectView(R.id.ll_data)
    LinearLayout llData;
    @InjectView(R.id.iv_more_two)
    ImageView ivMoreTwo;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.bark)
    ImageView bark;


    private String html5Url;//网页链接
    private boolean is_horizontal = false;//进入界面时是否横屏
    private Map shareMap;//分享的内容
    private boolean isShowShare = false;//是否需要显示分享
    private String title;//标题

    private LoadingProgressDialog mLoadingProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setLayout(R.layout.activity_live_details);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) return;
        WeiBoShare.getInstance().doResultIntent(intent);
    }

    @Override
    protected void initChildData() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        setBar();
        initIntent();
        initHtml();
        initWindow();
        setTitle(true);
    }

    @Override
    protected void setListener() {
        liveSurfce.setOnControlShowOrHint(new VideoSurfceView.OnControlShowOrHint() {
            @Override
            public void control(boolean isShow) {
                setIvMoreTwo(isShow);
                setTitle(false);
                setBack(isShow);
            }
        });
    }


    /**
     * 获取传递过来数据
     */
    private void initIntent() {
        if (bundle != null) {
            html5Url = bundle.getString(BaseInfoConstants.URL);
            title = bundle.getString(BaseInfoConstants.TITLE);
            is_horizontal = bundle.getBoolean(BaseInfoConstants.IS_HORIZONTAL);
        }
    }

    private void initWindow() {
        htmlWebview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                htmlWebview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (is_horizontal) {
                    changeView(is_horizontal);
                    OrientationWatchDog.changeHorizontalOrverticalScreen(VideoDetailsActivity.this);
                }
            }
        });

    }


    private void initHtml() {
        htmlWebview.setHtml5Url(html5Url);
        htmlWebview.setOnHtmlListener(new HtmlWebView.OnHtmlListener() {
            @Override
            public void goBack() {

            }

            @Override
            public void setTitle(String title) {

            }

            @Override
            public void setShareView(boolean isShow) {
                if (isShow) {
                    isShowShare = true;
                } else {
                    isShowShare = false;
                }
                setIvMoreTwo(liveSurfce.getTransparentShow());

            }
        });
    }


    @OnClick({R.id.bark, R.id.iv_more_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                if (OrientationWatchDog.isLandscape(this)) {
                    liveSurfce.setSwitchScreen();
                    return;
                } else {
                    finish();
                }
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
        if (shareMap == null) return;
        mLoadingProgressDialog = LoadingProgressDialog.createDialog(this);
        WeiBoShare.getInstance().init(this);
        final ShareDialog shareDialog = new ShareDialog(VideoDetailsActivity.this, new ArrayList<HashMap>());
        shareDialog
                .initData()
                .showAtLocation()
                .setListOnClick(new ShareDialog.ListOnClick() {
                    @Override
                    public void onclickitem(int position) {
                        if (position != 5)
                            mLoadingProgressDialog.showDialog();
                        shareDialog.goShare(VideoDetailsActivity.this,
                                shareMap.get(BaseInfoConstants.SHARE_URL) + "", shareMap.get(BaseInfoConstants.SHARE_TITLE) + ""
                                , shareMap.get(BaseInfoConstants.SHARE_CONTENT) + "", shareMap.get(BaseInfoConstants.SHARE_IMAGE) + "", position);
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
        } else if (msgId == MessageEvent.START_ANIMATION) {
            event.setMsgId(DOWN_ANIMATION);
            event.setDownloadCallback(mDownloadCallback);
            EventBus.getDefault().post(event);
        } else if (msgId == MessageEvent.START_GIF) {
            event.setMsgId(DOWN_GIF);
            event.setDownloadCallback(mDownloadCallback);
            EventBus.getDefault().post(event);
        } else if (msgId == MessageEvent.SHARE_DATA) {
            shareMap = event.getMap();
        }else if (msgId==MessageEvent.ADD_BARRAGE){
            addBarrage(event.getIsSelf(),event.getContent());
        }
    }

    /**
     * 添加弹幕
     * @param isSelf
     * @param content
     */
    private void addBarrage(final int isSelf, final String content){
        RxJavaScheduler.execute(new OnRxJavaProcessListener() {
            @Override
            public void process(ObservableEmitter<Object> emitter) {
                if(isSelf==0){
                    //对方
                    barrageUtil.addBarrage(false,content);
                }else {
                    //自己
                    barrageUtil.addBarrage(true,content);
                }
            }
        },new RxJavaObserver<Object>());
    }

    /**
     * 界面改变
     *
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
        } else if (OrientationWatchDog.isPoriratt(this)) {
            changeView(false);
            params.height = getResources().getDimensionPixelSize(R.dimen.d200dp);
        }
        liveSurfce.setLayoutParams(params);
    }

    /**
     * 横竖屏切换后界面更改
     *
     * @param isHorizontal
     */
    private void changeView(boolean isHorizontal) {
        if (isHorizontal) {
            liveSurfce.setIvAmplification(true);
//            rl_title.setVisibility(View.GONE);
            llData.setVisibility(View.GONE);
        } else {
            liveSurfce.setIvAmplification(false);
            rl_title.setVisibility(View.VISIBLE);
            llData.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否显示分享按钮
     *
     * @param isShow
     */
    private void setIvMoreTwo(boolean isShow) {
        if (ivMoreTwo == null) return;
        if (isShowShare && isShow) {
            ivMoreTwo.setVisibility(View.VISIBLE);
        } else {
            ivMoreTwo.setVisibility(View.GONE);
        }
    }

    private void setBack(boolean isShow) {
        if (bark == null) return;
        if (isShow) {
            bark.setVisibility(View.VISIBLE);
        } else {
            bark.setVisibility(View.GONE);
        }
    }


    private void setTitle(boolean isFist) {
        if (tvTitle == null) return;
        if (liveSurfce.getTransparentShow()) {
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
        }
        if (isFist && !StringUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
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
