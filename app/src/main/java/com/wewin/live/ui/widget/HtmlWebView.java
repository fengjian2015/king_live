package com.wewin.live.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.NetWorkUtil;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.ui.activity.HtmlActivity;
import com.wewin.live.ui.activity.Live.VideoDetailsActivity;
import com.wewin.live.utils.AndroidBug5497Workaround;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.SignOutUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import static com.wewin.live.utils.MessageEvent.DOWN_ANIMATION;
import static com.wewin.live.utils.MessageEvent.START_ANIMATION;

/**
 * @author jsaon
 * @date 2019/2/28
 * H5适配控件
 */
public class HtmlWebView extends LinearLayout implements ErrorView.OnContinueListener {
    ProgressBar mProgressBar;
    ExtendedWebView mWebView;

    private Context mContext;
    private boolean isError = false;
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
//        html5Url="file:///android_asset/android_js.html";
        initWebView();
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + "zhibo18.app");
        mWebView.getSettings().setTextZoom(100);
        AndroidBug5497Workaround.assistActivity((Activity) mContext);
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (NetWorkUtil.isNetworkAvailable(getContext())) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // 把图片加载放在最后来加载渲染
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 支持多窗口
        mWebView.getSettings().setSupportMultipleWindows(true);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);


        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        addJs();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.Log("网页链接：" + url);
                isError = false;
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressBar == null) {
                    return;
                }
                if (isError) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    hintError();
                    mProgressBar.setVisibility(View.GONE);
                    if (mWebView.canGoBack()) {
                        if (mOnHtmlListener != null)
                            mOnHtmlListener.goBack();

                    }
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                LogUtil.Log(errorResponse.getStatusCode() + "" + errorResponse.getData() + "   " + request.getUrl() + "  isForMainFrame: " + request.isForMainFrame());
                super.onReceivedHttpError(view, request, errorResponse);
                if (request.isForMainFrame()) {
                    setError();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return;
                if (mWebView == null) return;
                setError();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.isForMainFrame()) {// 在这里加上个判断
                        if (mWebView == null) return;
                        setError();
                    }
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }


        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (view == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("502 Bad Gateway") || title.contains("异常捕获")) {
                        //报错
                        setError();
                    }
                }

                if (mOnHtmlListener != null) {
                    title = title.replace("王者体育直播-", "");
                    mOnHtmlListener.setTitle(title);
                }
            }

        });
        mWebView.loadUrl(html5Url);
    }

    public ExtendedWebView getWebView() {
        return mWebView;
    }

    /**
     * 设置报错
     */
    private void setError() {
        isError = true;
        showError();

    }


    /**
     * 重新加载
     */
    private void againLoad() {
        isError = false;
        mWebView.reload();
    }

    @Override
    public void again() {
        againLoad();
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
        mWebView.goBack();
        hintError();
        isError = false;
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

    private final int GET_USER_INFO = 1;//获取用户信息
    private final int GO_VIDEO_PLAY = 2;//跳转到播放器
    private final int GO_VIDEO_PLAY_LOGIN = 3;//跳转到播放器需登录
    private final int SHOW_DIALOG = 5;//弹窗提示
    private final int SHOW_DIALOG_RELOAD = 6;//弹窗提示,重新刷新
    private final int IS_LONG = 7;//判断是否登录
    private final int IS_SHOW_SHARE = 8;//是否显示分享按钮
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
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
                                    if (what == SHOW_DIALOG_RELOAD)
                                        againLoad();
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
                    if (mOnHtmlListener==null)return;
                    int isShow= (int) msg.obj;
                    if(1==isShow){
                        mOnHtmlListener.setShareView(true);
                    }else {
                        mOnHtmlListener.setShareView(false);
                    }
                    break;
            }
        }
    };

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
            bundle.putString(BaseInfoConstants.SHARE_URL, map.get(BaseInfoConstants.SHARE_URL) + "");
            bundle.putString(BaseInfoConstants.SHARE_TITLE, map.get(BaseInfoConstants.SHARE_TITLE) + "");
            bundle.putString(BaseInfoConstants.SHARE_CONTENT, map.get(BaseInfoConstants.SHARE_CONTENT) + "");
            bundle.putString(BaseInfoConstants.SHARE_IMAGE, map.get(BaseInfoConstants.SHARE_IMAGE) + "");
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
//        userInfo.setToken(SignOutUtil.getToken());
//        HashMap hashMap=new HashMap();
//        hashMap.put("uid",SignOutUtil.getUserId());
//        hashMap.put("token",SignOutUtil.getToken());
//        String content=JSONObject.toJSONString(userInfo);
        mWebView.loadUrl("javascript:appGetUserInfo('" + userInfo.getJson() + " ');");
    }


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
    public void jsScrollChanged(String starY,String scrollY ) {
        onWbScrollChanged.onScrollChanged(UtilTool.parseFloat(starY),UtilTool.parseFloat(scrollY));
    }


    /**
     * 获取动画，下载和展示动画
     */
    @JavascriptInterface
    public void jsAnimation(String url,String fileName) {
        MessageEvent messageEvent=new MessageEvent(START_ANIMATION);
        messageEvent.setUrl(url);
        messageEvent.setFileName(fileName);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取动画，下载和展示动画
     */
    @JavascriptInterface
    public void jsIsShowShare(int isShow) {
        Message message = new Message();
        message.what = IS_SHOW_SHARE;
        message.obj = isShow;
        handler.sendMessage(message);
    }
}
