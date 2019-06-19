package com.wewin.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterLogin;
import com.wewin.live.ui.activity.login.ForgetPasswordActivity;
import com.wewin.live.ui.activity.login.RegisteredActivity;
import com.wewin.live.utils.IntentStart;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by GIjia on 2018/7/30.
 * 快速注册界面
 */

public class LoginDialog extends Dialog {

    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.eye)
    ImageView mEye;


    private OnClickListener onClickListener;
    //显示隐藏密码
    boolean isEye = false;
    private Context mContext;

    public LoginDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    public LoginDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        ButterKnife.inject(this);
    }


    @OnClick({R.id.tv_login, R.id.tv_registered,R.id.eye,R.id.tv_forget_password,R.id.iv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                //登录
                login();
                break;
            case R.id.tv_registered:
                //注册
                IntentStart.star(mContext, RegisteredActivity.class);
                break;
            case R.id.eye:
                //隐藏显示密码
                isEye = !isEye;
                showHidePassword();
                break;
            case R.id.tv_forget_password:
                //忘记密码
                IntentStart.star(mContext,ForgetPasswordActivity.class);
                break;
            case R.id.iv_finish:
                dismiss();
                break;
            default:
                break;
        }
    }

    public LoginDialog showDialog(){
        if (ActivityUtil.isActivityOnTop(mContext)) {
            setCanceledOnTouchOutside(false);
            show();
        }
        return this;
    }

    /**
     * 登录
     */
    private void login(){
        if (check()) {
            PersenterLogin.getInstance().login(etPhone.getText().toString(), etPassword.getText().toString(),new OnSuccess(mContext, new OnSuccess.OnSuccessListener() {
                @Override
                public void onSuccess(Object content) {
                    if (ActivityUtil.isActivityOnTop(mContext)){
                        dismiss();
                        if (onClickListener!=null){
                            onClickListener.onClick();
                        }
                    }
                }

                @Override
                public void onFault(String error) {

                }
            }));
        }
    }

    private boolean check() {
        if (StringUtils.isEmpty(etPhone.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etPhone);
            ToastShow.showToast2(mContext, mContext.getString(R.string.phone_number_cannot_empty));
            return false;
        } else if (StringUtils.isEmpty(etPassword.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etPassword);
            ToastShow.showToast2(mContext, mContext.getString(R.string.password_cannot_empty));
            return false;
        }
        return true;
    }


    private void showHidePassword() {
        if (isEye) {
            mEye.setSelected(true);
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPassword.setSelection(etPassword.length());
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPassword.setSelection(etPassword.length());
            mEye.setSelected(false);
        }
    }


    public LoginDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        void onClick();
    }
}
