package com.wewin.live.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.dialog.LoadingProgressDialog;
import com.wewin.live.dialog.ShareDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.thirdparty.WeiBoShare;
import com.wewin.live.ui.widget.web.HtmlWebView;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 单个H5界面
 */
public class HtmlActivity extends BaseActivity implements HtmlWebView.OnHtmlListener {

    HtmlWebView htmlWebview;
    @InjectView(R.id.iv_finish)
    ImageView ivFinish;
    @InjectView(R.id.fl_web)
    FrameLayout flWeb;

    //网页链接
    private String html5Url;
    private LoadingProgressDialog mLoadingProgressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_html;
    }

    @Override
    protected void init() {
        initIntent();
        initView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void initIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        html5Url = bundle.getString(BaseInfoConstants.URL);
    }

    private void initView() {
        setBar();
        htmlWebview = new HtmlWebView(this);
        flWeb.addView(htmlWebview);
        htmlWebview.setOnHtmlListener(this);
        htmlWebview.setHtml5Url(html5Url);
//        setIvMore(R.mipmap.icon_collection);
//        setIvMore(R.mipmap.icon_share);
    }

    @OnClick({R.id.iv_finish, R.id.bark, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.bark:
                //可返回可关闭
                if (htmlWebview.getCanGoBack()) {
                    htmlWebview.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.iv_more:
                share();
                break;
            default:
                break;
        }
    }

    /**
     * 分享弹窗
     */
    private void share() {
        mLoadingProgressDialog = LoadingProgressDialog.createDialog(this);
        WeiBoShare.getInstance().init(this);
        final ShareDialog shareDialog = new ShareDialog(HtmlActivity.this, new ArrayList<HashMap>());
        shareDialog
                .initData()
                .showAtLocation()
                .setListOnClick(new ShareDialog.ListOnClick() {
                    @Override
                    public void onClickItem(int position) {
                        if (position != 5) {
                            mLoadingProgressDialog.showDialog();
                        }
                        shareDialog.goShare(HtmlActivity.this, "http://testzhibo.wewin18.net", "标题", "描述", "图片地址", position);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.SHARE_SUCCESS) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(HtmlActivity.this, getString(R.string.share_success));
        } else if (msgId == MessageEvent.SHARE_FAIL) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(HtmlActivity.this, "" + event.getError());
        } else if (msgId == MessageEvent.SHARE_CANCEL) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(HtmlActivity.this, getString(R.string.share_cancel));
        }
    }

    @Override
    public void goBack() {
        if (htmlWebview.getCanGoBack()) {
            ivFinish.setVisibility(View.VISIBLE);
        } else {
            ivFinish.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(String title) {
        setTvTitleTop(title);
    }

    @Override
    public void setShareView(boolean isShow) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
