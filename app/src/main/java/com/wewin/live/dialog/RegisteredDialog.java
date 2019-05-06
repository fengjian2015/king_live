package com.wewin.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wewin.live.R;
import com.wewin.live.utils.IntentStart;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by GIjia on 2018/7/30.
 * 快速注册界面
 */

public class RegisteredDialog extends Dialog {


    @InjectView(R.id.iv_finish)
    ImageView ivFinish;
    @InjectView(R.id.ll_message)
    LinearLayout llMessage;
    @InjectView(R.id.ll_live_more)
    LinearLayout llLiveMore;
    @InjectView(R.id.ll_focus_anchor)
    LinearLayout llFocusAnchor;
    @InjectView(R.id.bt_registration)
    Button btRegistration;

    private Context mContext;

    public RegisteredDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mContext=context;
    }

    public RegisteredDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_registered);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                IntentStart.starLogin(mContext);
            }
        });
    }

}
