package com.wewin.live.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.example.jasonutil.util.MySharedPreferences;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.ui.widget.OpeningStartAnimation;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MySharedConstants;

import butterknife.InjectView;

/**
 * 启动页
 */
public class StartActivity extends BaseActivity {

    @InjectView(R.id.rl_data)
    RelativeLayout rlData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
        initAnimtor();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentStart.starNoAnimtor(StartActivity.this, MainActivity.class);
                StartActivity.this.finish();
            }
        }, 2000);
    }


    private void initAnimtor() {
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation(this);
        openingStartAnimation.setImage(R.mipmap.start_logo);
        openingStartAnimation.show(rlData);
    }

}
