package com.wewin.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.util.ActivityUtil;
import com.wewin.live.R;
import com.wewin.live.ui.activity.login.RegisteredActivity;
import com.wewin.live.utils.IntentStart;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by GIjia on 2018/7/30.
 * 快速注册界面
 */

public class RegisteredDialog extends Dialog {

    @InjectView(R.id.bt_registration)
    Button btRegistration;
    @InjectView(R.id.tv_login)
    TextView tvLogin;
    @InjectView(R.id.iv_finish)
    ImageView ivFinish;

    private Context mContext;

    public RegisteredDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    public RegisteredDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_registered);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        btRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                IntentStart.star(mContext, RegisteredActivity.class);
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                IntentStart.starLogin(mContext);
            }
        });
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCanceledOnTouchOutside(false);
    }

    public RegisteredDialog showDialog(){
        if (ActivityUtil.isActivityOnTop(mContext)) {
            setCanceledOnTouchOutside(false);
            show();
        }
        return this;
    }

}
