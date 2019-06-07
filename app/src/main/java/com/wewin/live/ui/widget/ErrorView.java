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
    private TextView tvError;
    private ImageView ivLoading;
    private Button btnConfirm;

    private Context context;
    private OnContinueListener mOnContinueListener;

    private String errorContent;
    private String notDataContent;

    public ErrorView(Context context, View view) {
        this.context = context;
        mRlLoadError = view.findViewById(R.id.rl_error);
        tvError = view.findViewById(R.id.tv_error);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        ivLoading = view.findViewById(R.id.iv_loading);


        btnConfirm.setOnClickListener(this);
        animationDrawable = (AnimationDrawable) ivLoading.getBackground();
    }

    public ErrorView(Activity activity) {
        this.context = activity;
        mRlLoadError = activity.findViewById(R.id.rl_error);
        tvError = activity.findViewById(R.id.tv_error);
        btnConfirm = activity.findViewById(R.id.btn_confirm);
        ivLoading = activity.findViewById(R.id.iv_loading);


        btnConfirm.setOnClickListener(this);
        animationDrawable = (AnimationDrawable) ivLoading.getBackground();
    }


    public void setOnContinueListener(OnContinueListener mOnContinueListener) {
        this.mOnContinueListener = mOnContinueListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (mOnContinueListener != null) {
                    mOnContinueListener.again();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置错误提示时，显示按钮
     *
     * @param error
     */
    public void setTvError(String error) {
        if (tvError == null) {
            return;
        }
        errorContent = error;
    }

    /**
     * 设置无数据提示时，隐藏按钮
     *
     * @param noData
     */
    public void setTvNoData(String noData) {
        if (tvError == null) {
            return;
        }
        notDataContent = noData;
        tvError.setText(notDataContent);
    }

    /**
     * 显示错误提示
     * @param isShowButton
     */
    public void errorShow(boolean isShowButton) {
        mRlLoadError.setVisibility(View.VISIBLE);
        animationDrawable.start();
        tvError.setText(errorContent);
        if (isShowButton) {
            btnConfirm.setVisibility(View.VISIBLE);
        } else {
            btnConfirm.setVisibility(View.GONE);
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
        tvError.setText(notDataContent);
        if (isShowButton) {
            btnConfirm.setVisibility(View.VISIBLE);
        } else {
            btnConfirm.setVisibility(View.GONE);
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
