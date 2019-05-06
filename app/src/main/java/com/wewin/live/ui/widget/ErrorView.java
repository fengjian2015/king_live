package com.wewin.live.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wewin.live.R;

/**
 * @author jsaon
 * @date 2019/3/11
 * 错误界面
 */
public class ErrorView implements View.OnClickListener {
    private AnimationDrawable animationDrawable;
    private RelativeLayout mRlLoadError;
    private TextView tv_error;
    private ImageView iv_loading;
    private Button btn_confirm;

    private Context context;
    private OnContinueListener mOnContinueListener;

    private String errorContent;
    private String notDataContent;

    public ErrorView(Context context, View view) {
        this.context = context;
        mRlLoadError = view.findViewById(R.id.rl_error);
        tv_error = view.findViewById(R.id.tv_error);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        iv_loading = view.findViewById(R.id.iv_loading);


        btn_confirm.setOnClickListener(this);
        animationDrawable = (AnimationDrawable) iv_loading.getBackground();
    }

    public ErrorView(Activity activity) {
        this.context = activity;
        mRlLoadError = activity.findViewById(R.id.rl_error);
        tv_error = activity.findViewById(R.id.tv_error);
        btn_confirm = activity.findViewById(R.id.btn_confirm);
        iv_loading = activity.findViewById(R.id.iv_loading);


        btn_confirm.setOnClickListener(this);
        animationDrawable = (AnimationDrawable) iv_loading.getBackground();
    }


    public void setOnContinueListener(OnContinueListener mOnContinueListener) {
        this.mOnContinueListener = mOnContinueListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (mOnContinueListener != null)
                    mOnContinueListener.again();
                break;
        }
    }

    /**
     * 设置错误提示时，显示按钮
     *
     * @param error
     */
    public void setTvError(String error) {
        if (tv_error == null) return;
        errorContent = error;
    }

    /**
     * 设置无数据提示时，隐藏按钮
     *
     * @param noData
     */
    public void setTvNoData(String noData) {
        if (tv_error == null) return;
        notDataContent = noData;
        tv_error.setText(notDataContent);
    }

    /**
     * 显示错误提示
     * @param isShowButton
     */
    public void errorShow(boolean isShowButton) {
        mRlLoadError.setVisibility(View.VISIBLE);
        animationDrawable.start();
        tv_error.setText(errorContent);
        if (isShowButton) {
            btn_confirm.setVisibility(View.VISIBLE);
        } else {
            btn_confirm.setVisibility(View.GONE);
        }

    }

    /**
     * 显示无数据提示提示
     *
     * @param isShowButton
     */
    public void notDataShow(boolean isShowButton) {
        mRlLoadError.setVisibility(View.VISIBLE);
        animationDrawable.start();
        tv_error.setText(notDataContent);
        if (isShowButton) {
            btn_confirm.setVisibility(View.VISIBLE);
        } else {
            btn_confirm.setVisibility(View.GONE);
        }

    }

    /**
     * 隐藏
     */
    public void hint() {
        mRlLoadError.setVisibility(View.GONE);
        animationDrawable.stop();
    }

    public interface OnContinueListener {
        void again();
    }
}
