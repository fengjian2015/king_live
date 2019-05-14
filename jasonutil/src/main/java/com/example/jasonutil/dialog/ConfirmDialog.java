package com.example.jasonutil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jasonutil.R;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.StringUtils;

/**
 * Created by GIjia on 2018/7/30.
 * 确认框，无取消
 */

public class ConfirmDialog extends Dialog {

    TextView mTvTitle;
    TextView mTvContent;
    Button mBtnConfirm;

    private OnClickListener onClickListener;
    private View view;
    private Context context;

    public ConfirmDialog( Context context) {
        super(context, R.style.dialog);
        this.context=context;
        createDialog(context);
    }

    public ConfirmDialog( Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
        createDialog(context);
    }

    private void createDialog(Context context) {
        view = View.inflate(context, R.layout.dialog_confirm, null);
        mTvTitle=view.findViewById(R.id.tv_title);
        mTvContent=view.findViewById(R.id.tv_content);
        mBtnConfirm=view.findViewById(R.id.btn_confirm);
        setContentView(view);
        init();
    }

    public ConfirmDialog showDialog(){
        if (ActivityUtil.isActivityOnTop(context)) {
            setCanceledOnTouchOutside(false);
            show();
        }
        return this;
    }

    /**
     * 设置标题颜色
     * @param color
     * @return
     */
    public ConfirmDialog setTvTitleColor(int color) {
        mTvTitle.setTextColor(color);
        return this;
    }

    /**
     * 设置标题
     * @param title
     * @return
     */
    public ConfirmDialog setTvTitle(String title) {
        if (!StringUtils.isEmpty(title)) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
        return this;
    }

    /**
     * 设置内容颜色
     * @param color
     * @return
     */
    public ConfirmDialog setTvContentColor(int color) {
        mTvContent.setTextColor(color);
        return this;
    }

    public ConfirmDialog setTvContent(String content) {
        if (!StringUtils.isEmpty(content)) {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
        }
        return this;
    }

    /**
     * 设置内容
     * @param content
     * @return
     */
    public ConfirmDialog setTvContent(SpannableStringBuilder content) {
        if (!StringUtils.isEmpty(content.toString())) {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
        }
        return this;
    }

    /**
     * 设置确认按钮文本
     * @param confirm
     * @return
     */
    public ConfirmDialog setBtnConfirm(String confirm) {
        if (!StringUtils.isEmpty(confirm)) {
            mBtnConfirm.setText(confirm);
        }
        return this;
    }

    private void init() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });
    }

    public ConfirmDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        void onClick();
    }
}
