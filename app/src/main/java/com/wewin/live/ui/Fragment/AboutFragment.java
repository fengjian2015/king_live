package com.wewin.live.ui.Fragment;

import com.wewin.live.R;
import com.wewin.live.base.BaseMainFragment;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.utils.MessageEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author jsaon
 * @date 2019/2/27
 * 关注
 */
public class AboutFragment extends BaseMainFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected String type() {
        return BaseInfoConstants.ATTENTION;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.MAIN_MENU_UPDATA && getIsVisible()) {
            updata();
        } else if (msgId == MessageEvent.MAIN_MENU_FAILURE) {
            failure();
        }
    }
}
