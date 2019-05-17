package com.wewin.live.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasonutil.util.LogUtil;
import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.ui.widget.web.HtmlWebView;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jsaon
 * @date 2019/2/28
 * 网页
 */
public class HtmlFragment extends LazyFragment {

    @InjectView(R.id.html_webview)
    HtmlWebView htmlWebview;

    private String html5Url;
    private String type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_html, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    protected void lazyLoad() {
        init();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void init() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            html5Url = arguments.getString(BaseInfoConstants.URL);
            type=arguments.getString(BaseInfoConstants.TYPE);
            LogUtil.Log("html:"+html5Url+"   type:"+type);
        }
        htmlWebview.setHtml5Url(html5Url);
        if(getString(R.string.home).equals(type)) {
            htmlWebview.setOnWbScrollChanged(new HtmlWebView.OnWbScrollChanged() {
                @Override
                public void onScrollChanged(float starY, float scrollY) {
                    MessageEvent messageEvent=new MessageEvent(MessageEvent.WB_SCROLL_HIGHT_CHANGE);
                    messageEvent.setStarY(starY);
                    messageEvent.setScrollY(scrollY);
                    EventBus.getDefault().post(messageEvent);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.LOGIN||msgId == MessageEvent.SIGN_OUT) {
            htmlWebview.again();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.reset(this);
    }


}
