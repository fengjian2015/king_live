package com.example.jasonutil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasonutil.R;
import com.example.jasonutil.util.StringUtils;

/**
 * Created by GIjia on 2018/7/30.
 *确认取消框
 */

public class ConfirmCancelDialog extends Dialog {

    TextView mTvTitle;
    TextView mTvContent;
    Button mBtnConfirm;
    ImageView mBtnCancel;

    private OnClickListener onClickListener;
    private View view;

    public ConfirmCancelDialog( Context context) {
        super(context, R.style.dialog);
        createDialog(context);
    }

    public ConfirmCancelDialog( Context context, int themeResId) {
        super(context, themeResId);
        createDialog(context);
    }

    private void createDialog(Context context) {
        view = View.inflate(context, R.layout.dialog_confirm_cancel, null);
        mTvTitle=view.findViewById(R.id.tv_title);
        mTvContent=view.findViewById(R.id.tv_content);
        mBtnConfirm=view.findViewById(R.id.btn_confirm);
        mBtnCancel=view.findViewById(R.id.btn_cancel);

        setContentView(view);
        init();
    }



    /**
     * 先show再设置其他控件
     * @return
     */
    public ConfirmCancelDialog showDialog(){
        setCanceledOnTouchOutside(false);
        show();
        return this;
    }

    /**
     * 设置标题
     * @param title
     * @return
     */
    public ConfirmCancelDialog setTvTitle(String title) {
        if (!StringUtils.isEmpty(title)) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
        return this;
    }

    /**
     * 设置内容
     * @param content
     * @return
     */
    public ConfirmCancelDialog setTvContent(String content) {
        if (!StringUtils.isEmpty(content)) {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
        }
        return this;
    }

    /**
     * 设置确认控件文本
     * @param confirm
     * @return
     */
    public ConfirmCancelDialog setBtnConfirm(String confirm) {
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

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onCancel();
                }
            }
        });
    }

    public ConfirmCancelDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        void onClick();

        void onCancel();
    }
}
