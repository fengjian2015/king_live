package com.wewin.live.ui.activity.person;


import android.view.View;
import android.widget.TextView;

import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.SignOutUtil;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 安全与隐私
 */
public class SecurityPrivacyActivity extends BaseActivity {

    @InjectView(R.id.tv_phone)
    TextView tvPhone;
    @InjectView(R.id.tv_wx)
    TextView tvWx;
    @InjectView(R.id.tv_emil)
    TextView tvEmil;
    private UserInfo mUserInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_security_privacy;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.security_privacy));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData(){
        mUserInfo=UserInfoDao.queryUserInfo(SignOutUtil.getUserId());
        if (StringUtils.isEmpty(mUserInfo.getEmail())){
            tvEmil.setText(getString(R.string.bind));
        }else{
            tvEmil.setText(getString(R.string.replace));
        }
        if(StringUtils.isEmpty(mUserInfo.getWeixin())){
            tvWx.setText(getString(R.string.bind));
        }else{
            tvWx.setText(getString(R.string.replace));
        }
    }

    @OnClick({R.id.rl_phone, R.id.rl_wx, R.id.rl_emil})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_phone:
                IntentStart.star(SecurityPrivacyActivity.this,PhoneChangeActivity.class);
                break;
            case R.id.rl_wx:
                IntentStart.star(SecurityPrivacyActivity.this,AccountSettingsActivity.class);
                break;
            case R.id.rl_emil:
                IntentStart.star(SecurityPrivacyActivity.this,AccountSettingsActivity.class);
                break;
            default:
                break;
        }
    }
}
