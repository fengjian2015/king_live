package com.wewin.live.ui.widget.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.dialog.BettingDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterCommon;
import com.wewin.live.ui.activity.HtmlActivity;
import com.wewin.live.ui.activity.live.VideoDetailsActivity;
import com.wewin.live.ui.widget.ErrorView;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.SignOutUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import static com.wewin.live.utils.MessageEvent.SHARE_DATA;
import static com.wewin.live.utils.MessageEvent.START_ANIMATION;
import static com.wewin.live.utils.MessageEvent.START_GIF;
import static com.wewin.live.utils.MessageEvent.START_LIST_GIF;

/**
 * @author jsaon
 * @date 2019/2/28
 * H5适配控件
 */
public class HtmlWebView extends LinearLayout implements ErrorView.OnContinueListener {
    ProgressBar mProgressBar;
    ExtendedWebView mWebView;

    private Context mContext;

    private View view;
    private String html5Url;

    //错误界面
    private ErrorView mErrorView;

    //回调接口
    private OnHtmlListener mOnHtmlListener;
    //错误回调
    private OnErrorListener mOnErrorListener;
    //滚动回调
    private OnWbScrollChanged onWbScrollChanged;

    public HtmlWebView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public HtmlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_html, this);
        mProgressBar = view.findViewById(R.id.progressBar);
        mWebView = view.findViewById(R.id.web_view);
        mErrorView = new ErrorView(mContext, view);
        mErrorView.setTvError(mContext.getString(R.string.error));
        mErrorView.setOnContinueListener(this);

    }

    public void setHtml5Url(String url) {
        mProgressBar.setMax(100);
        html5Url = url;
        if (html5Url.startsWith("file:///android_asset/")) {

        } else if (!UtilTool.checkLinkedExe(html5Url)) {
            html5Url = "http://" + html5Url;
        }
        // TODO: 2019/4/24 测试用 
//        if (StringUtils.isEmpty(html5Url))
//            html5Url = "http://testzhibo.wewin18.net";
//        html5Url = "file:///android_asset/android_js.html";
        initWebView();
    }

    private void initWebView() {
        mWebView.initWebView();
        addJs();
        mWebView.loadUrl(html5Url);
        mWebView.setOnWebViewListeren(new ExtendedWebView.OnWebViewListeren() {
            @Override
            public void onPageFinished(String url) {
                if (mProgressBar == null) {
                    return;
                }
                if (mWebView.isError) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    hintError();
                    mProgressBar.setVisibility(View.GONE);
                    if (mWebView.canGoBack()) {
                        if (mOnHtmlListener != null) {
                            mOnHtmlListener.goBack();
                        }
                    }
                }
                mWebView.writeData();
            }

            @Override
            public void onError() {
                if (mWebView == null) {
                    return;
                }
                showError();
            }

            @Override
            public void onPageStarted() {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(int progress) {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(String title) {
                if (mOnHtmlListener != null) {
                    title = title.replace("王者体育直播-", "");
                    mOnHtmlListener.setTitle(title);
                }
            }
        });
    }

    public ExtendedWebView getWebView() {
        return mWebView;
    }

    @Override
    public void again() {
        mWebView.reloadLoad();
    }

    /**
     * 获取是否回退
     *
     * @return
     */
    public boolean getCanGoBack() {
        return mWebView.canGoBack();
    }

    /**
     * 回退
     */
    public void goBack() {
        mWebView.back();
        hintError();
    }

    /**
     * 显示错误提示
     */
    public void showError() {
        mWebView.setVisibility(View.GONE);
        if (mOnErrorListener != null) {
            mOnErrorListener.onShow();
        } else {
            mErrorView.errorShow(true);
        }
    }

    /**
     * 隐藏错误提示
     */
    public void hintError() {
        mWebView.setVisibility(View.VISIBLE);
        if (mOnErrorListener != null) {
            mOnErrorListener.onHint();
        } else {
            mErrorView.hint();
        }

    }


    /**
     * 设置回调
     *
     * @param onHtmlListener
     */
    public void setOnHtmlListener(OnHtmlListener onHtmlListener) {
        mOnHtmlListener = onHtmlListener;
    }

    public void setOnWbScrollChanged(OnWbScrollChanged onWbScrollChanged) {
        this.onWbScrollChanged = onWbScrollChanged;
    }

    public interface OnWbScrollChanged {
        void onScrollChanged(float starY, float scrollY);
    }

    /**
     * 设置错误回调,设置后将不使用本控件的错误界面
     *
     * @param mOnErrorListener
     */
    public void setmOnErrorListener(OnErrorListener mOnErrorListener) {
        this.mOnErrorListener = mOnErrorListener;
    }

    /**
     * 回调
     */
    public interface OnHtmlListener {
        void goBack();

        void setTitle(String title);

        void setShareView(boolean isShow);
    }

    /**
     * 错误回调，使用情况：不想使用本控件自带的错误提示界面时使用
     */
    public interface OnErrorListener {
        //显示错误提示
        void onShow();

        //隐藏错误提示
        void onHint();
    }

    @Override
    public void destroyDrawingCache() {
        try {
            if (mWebView != null) {
                mWebView.clearCache(true);
                mWebView.onPause();
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.destroyDrawingCache();
    }

    //获取用户信息
    private final int GET_USER_INFO = 1;
    //跳转到播放器
    private final int GO_VIDEO_PLAY = 2;
    //跳转到播放器需登录
    private final int GO_VIDEO_PLAY_LOGIN = 3;
    //弹窗提示
    private final int SHOW_DIALOG = 5;
    //弹窗提示,重新刷新
    private final int SHOW_DIALOG_RELOAD = 6;
    //判断是否登录
    private final int IS_LONG = 7;
    //是否显示分享按钮
    private final int IS_SHOW_SHARE = 8;
    //弹出投注窗
    private final int BETTING_DIALOG = 9;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case GET_USER_INFO:
                    getUserInfo();
                    break;
                case GO_VIDEO_PLAY:
                    goVideoPlay((String) msg.obj, false);
                    break;
                case GO_VIDEO_PLAY_LOGIN:
                    goVideoPlay((String) msg.obj, true);
                    break;
                case SHOW_DIALOG:
                case SHOW_DIALOG_RELOAD:
                    //弹窗提示
                    new ConfirmDialog(mContext)
                            .setTvTitle((String) msg.obj)
                            .setOnClickListener(new ConfirmDialog.OnClickListener() {
                                @Override
                                public void onClick() {
                                    if (what == SHOW_DIALOG_RELOAD) {
                                        again();
                                    }
                                }
                            }).showDialog();
                    break;
                case IS_LONG:
                    if (SignOutUtil.getIsLogin()) {
                        again();
                    } else {
                        IntentStart.starLogin(mContext);
                    }
                    break;
                case IS_SHOW_SHARE:
                    if (mOnHtmlListener == null) {
                        break;
                    }
                    int isShow = (int) msg.obj;
                    if (1 == isShow) {
                        mOnHtmlListener.setShareView(true);
                    } else {
                        mOnHtmlListener.setShareView(false);
                    }
                    break;
                case BETTING_DIALOG:
                    bettingDialog((String) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 竞猜投注
     * @param content
     */
    private void bettingDialog(String content) {
        if (!SignOutUtil.getIsLogin()){
            IntentStart.starLogin(mContext);
            return;
        }
        final Map dataMap=JSON.parseObject(content);
        String data = dataMap.get(BaseInfoConstants.DATA)+"";
        List<Map> mapList = null;
        if(data!=null) {
            mapList = JSON.parseArray(data, Map.class);
        }
        new BettingDialog(mContext)
                .setTypeList(mapList)
                .setOnClickListener(new BettingDialog.OnClickListener() {
                    @Override
                    public void onClick(String typeId, String amountSelect, String amount) {
                        LogUtil.log(typeId+"   "+amountSelect+"  "+amount);
                        if (StringUtils.isEmpty(amount)){
                            amount=amountSelect;
                        }
                        if (StringUtils.isEmpty(typeId)){
                            typeId=dataMap.get(BaseInfoConstants.TABID)+"";
                        }
                        userQuiz(amount,dataMap.get(BaseInfoConstants.QUIZID)+"",typeId);
                    }
                })
                .showDialog();
    }

    /**
     * 竞猜投注接口
     * @param amount
     * @param quizId
     * @param tabId
     */
    private void userQuiz(String amount,String quizId,String tabId){
        LogUtil.log(amount+"   "+quizId+"  "+tabId);
        PersenterCommon.getInstance().userQuiz(amount,quizId,tabId,new OnSuccess(mContext, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                getLotteryList();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * * 视频播放
     *
     * @param content
     * @param isNeesLogin true需要登录
     */
    private void goVideoPlay(String content, boolean isNeesLogin) {
        try {
            Map map = JSONObject.parseObject(content, Map.class);
            Bundle bundle = new Bundle();
            bundle.putString(BaseInfoConstants.PULL_URL, map.get(BaseInfoConstants.PULL_URL) + "");
            bundle.putString(BaseInfoConstants.URL, map.get(BaseInfoConstants.HTML_URL) + "");
            bundle.putString(BaseInfoConstants.WECHAT, map.get(BaseInfoConstants.WECHAT) + "");
            bundle.putString(BaseInfoConstants.ADSCONTENT, map.get(BaseInfoConstants.ADSCONTENT) + "");
            bundle.putString(BaseInfoConstants.WXIMAGE, map.get(BaseInfoConstants.WXIMAGE) + "");
            bundle.putString(BaseInfoConstants.TITLE, map.get(BaseInfoConstants.TITLE) + "");
            if (isNeesLogin) {
                IntentStart.starLogin(mContext, VideoDetailsActivity.class, bundle);
            } else {
                IntentStart.star(mContext, VideoDetailsActivity.class, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------------以下是java调用js-------------------------------------------------*/

    /**
     * 获取用户信息回调h5
     */
    private void getUserInfo() {
        UserInfo userInfo = UserInfoDao.queryUserInfo(SignOutUtil.getUserId());
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        if (mWebView == null) {
            return;
        }
        mWebView.loadUrl("javascript:appGetUserInfo('" + userInfo.getJson() + " ');");
    }

    /**
     * 键盘高度关闭
     */
    public void keyboardHeightHint() {
        mWebView.loadUrl("javascript:appKeyboardHeightHint();");
    }

    /**
     * 投注框回调
     */
    public void getLotteryList() {
        mWebView.loadUrl("javascript:getLotteryList();");
    }
    /*-----------------------------------以下是java调用js,并且返回值-------------------------------------------------*/
    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWebView.evaluateJavascript("javascript:appGetUserInfo('" + userInfo.getJson() + " ')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                LogUtil.log("返回值"+value);
            }
        });
    }*/



    /*------------------------------------以下是h5调用java方法--------------------------------------------*/
    @SuppressLint("JavascriptInterface")
    private void addJs() {
        mWebView.addJavascriptInterface(this, "app");
    }

    /**
     * 获取用户信息
     */
    @JavascriptInterface
    public void jsGetUserInfo() {
        handler.sendEmptyMessage(GET_USER_INFO);
    }

    /**
     * 跳转到新的activity进行展示H5内容
     */
    @JavascriptInterface
    public void jsNewActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseInfoConstants.URL, url);
        IntentStart.star(mContext, HtmlActivity.class, bundle);

    }

    /**
     * 跳转播放器
     */
    @JavascriptInterface
    public void jsPlay(String content) {
        Message message = new Message();
        message.what = GO_VIDEO_PLAY;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 跳转播放器需要登录才可以进入.
     */
    @JavascriptInterface
    public void jsPlayOrLogin(String content) {
        Message message = new Message();
        message.what = GO_VIDEO_PLAY_LOGIN;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 跳转到登录界面
     */
    @JavascriptInterface
    public void jsLogin() {
        IntentStart.starLogin(mContext);
    }

    /**
     * 判断是否登录，登录状态刷新界面，否则跳转登录
     */
    @JavascriptInterface
    public void jsIsLogin() {
        handler.sendEmptyMessage(IS_LONG);
    }

    /**
     * 退出登录
     */
    @JavascriptInterface
    public void jsSignOut() {
        SignOutUtil.signOut();
    }

    /**
     * 弹窗,不刷新
     */
    @JavascriptInterface
    public void jsShowDialog(String content) {
        Message message = new Message();
        message.what = SHOW_DIALOG;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 弹窗
     */
    @JavascriptInterface
    public void jsShowDialog(String content, String isReload) {
        Message message = new Message();
        message.what = SHOW_DIALOG_RELOAD;
        message.obj = content;
        handler.sendMessage(message);
    }

    /**
     * 获取页面滚动
     */
    @JavascriptInterface
    public void jsScrollChanged(String starY, String scrollY) {
        onWbScrollChanged.onScrollChanged(UtilTool.parseFloat(starY), UtilTool.parseFloat(scrollY));
    }


    /**
     * 获取动画，下载和展示动画
     */
    @JavascriptInterface
    public void jsAnimation(String url, String fileName) {
        MessageEvent messageEvent = new MessageEvent(START_ANIMATION);
        messageEvent.setUrl(url);
        messageEvent.setFileName(fileName);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取动画，下载和展示动画GIF
     */
    @JavascriptInterface
    public void jsAnimationGif(String url, String fileName) {
        MessageEvent messageEvent = new MessageEvent(START_GIF);
        messageEvent.setUrl(url);
        messageEvent.setFileName(fileName);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取列表动画
     */
    @JavascriptInterface
    public void jsAnimationList(String content) {
        MessageEvent messageEvent = new MessageEvent(START_LIST_GIF);
        messageEvent.setContent(content);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 是否显示分享按钮
     */
    @JavascriptInterface
    public void jsIsShowShare(int isShow, String shareData) {
        if (!StringUtils.isEmpty(shareData)) {
            MessageEvent messageEvent = new MessageEvent(SHARE_DATA);
            Map map = JSONObject.parseObject(shareData, Map.class);
            messageEvent.setMap(map);
            EventBus.getDefault().post(messageEvent);
            Message message = new Message();
            message.what = IS_SHOW_SHARE;
            message.obj = isShow;
            handler.sendMessage(message);
        }
    }


    /**
     * 是否显示分享按钮
     */
    @JavascriptInterface
    public void jsBetting(String content) {
        if (!StringUtils.isEmpty(content)) {
            Message message = new Message();
            message.what = BETTING_DIALOG;
            message.obj = content;
            handler.sendMessage(message);
        }
    }
}
