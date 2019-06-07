package com.wewin.live.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;
import com.example.jasonutil.util.ActivityManage;
import com.wewin.live.utils.IntentStart;
import com.example.jasonutil.util.ScreenTools;
import com.example.jasonutil.util.UtilTool;
import org.greenrobot.eventbus.EventBus;
import butterknife.ButterKnife;

/**
 * @author jsaon
 * @date 2019/2/26
 * activity基类
 */
public abstract class BaseActivity extends BaseLiveActivity {
    protected TextView mTvTitleTop;
    protected ImageView bark;
    protected boolean isTitleColor = false;
    protected ImageView mIvMore;
    /**
     * 右边文本
     */
    protected TextView mTvRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManage.addActivity(this);
        titleColor();
        //动画
        IntentStart.initAnimtor(this);
        setBarTranslucent();
        setContentView(getLayoutId());
        ButterKnife.inject(this);
        getIntentData();
        init();
        setListener();
    }


    private void setBarTranslucent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置顶部状态栏高度
     */
    public void setBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View rootView = findViewById(R.id.main_root);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootView.getLayoutParams();
            layoutParams.height = ScreenTools.getStateBar3(this);
        }
    }

    /**
     * 设置顶部状态栏高度
     */
    public void setBarLinearLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View rootView = findViewById(R.id.main_root);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rootView.getLayoutParams();
            layoutParams.height = ScreenTools.getStateBar3(this);
        }
    }


    /**
     * 获取布局id
     *
     * @return
     */
    protected abstract int getLayoutId();


    /**
     * 获取传递数据
     *
     * @return
     */
    protected void getIntentData(){}

    /**
     * 监听
     *
     * @return
     */
    protected void setListener(){}
    /**
     *
     */
    protected void titleColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isTitleColor) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.home_red));
        }
    }

    /**
     * 设置返回按钮
     */
    protected void setBark() {
        bark = findViewById(R.id.bark);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 设置右边图标，点击事件子类实现
     *
     * @param id 图片资源id
     */
    protected void setIvMore(int id) {
        mIvMore = findViewById(R.id.iv_more);
        mIvMore.setImageResource(id);
        mIvMore.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右边第二个图标，点击事件子类实现
     *
     * @param id 图片资源id
     */
    protected void setIvMoreTwo(int id) {
        mIvMore = findViewById(R.id.iv_more_two);
        mIvMore.setImageResource(id);
        mIvMore.setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题
     *
     * @param content
     */
    protected void setTvTitleTop(String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        mTvTitleTop = findViewById(R.id.tv_title_top);
        mTvTitleTop.setText(content);
    }

    /**
     * 设置标题且设置返回和头部
     *
     * @param content
     */
    protected void setTitle(String content) {
        mTvTitleTop = findViewById(R.id.tv_title_top);
        mTvTitleTop.setText(content);
        setBark();
        setBar();
    }

    /**
     * 设置标题头部
     *
     * @param content
     */
    protected void setTitleNoBack(String content) {
        mTvTitleTop = findViewById(R.id.tv_title_top);
        mTvTitleTop.setText(content);
        setBar();
    }

    /**
     * 设置右边文本内容
     *
     * @param content
     */
    protected void setTvRight(String content) {
        mTvRight = findViewById(R.id.tv_right);
        mTvRight.setText(content);
        mTvRight.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        UtilTool.closeKeybord(BaseActivity.this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            EventBus.getDefault().unregister(this);
            ButterKnife.reset(this);
            ActivityManage.removeActivity(this);
        }
    }

    protected abstract void init();
}
