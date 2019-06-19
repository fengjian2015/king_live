package com.wewin.live.ui.activity.login;

import android.app.Activity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.jasonutil.keyboard.EmoticonsKeyboardUtils;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterLogin;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.PickerViewUtil;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.eye)
    ImageView mEye;
    @InjectView(R.id.tv_area_code)
    CheckBox tvAreaCode;

    //显示隐藏密码
    boolean isEye = false;
    //区号相关数据
    private PickerViewUtil pickerViewUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.login));
//        initCode();
        initHttp(false);
        LogUtil.log("高度:"+EmoticonsKeyboardUtils.getDefKeyboardHeight(this));
    }

    /**
     * 获取网络请求数据
     */
    private void initHttp(boolean isShow){
        PersenterLogin.getInstance().getCountryCode(isShow,new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                initCode();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 初始化区号弹窗
     */
    private void initCode() {
        pickerViewUtil = new PickerViewUtil(this)
                .initCountryCode()
                .setOnPickerReturnListener(new PickerViewUtil.OnPickerReturnListener() {
                    @Override
                    public void getData(int options1, int options2, int options3, String data) {
                        tvAreaCode.setText(data);
                    }

                    @Override
                    public void onDismiss() {
                        tvAreaCode.setChecked(false);
                    }
                });
    }

    @OnClick({R.id.tv_login, R.id.tv_registered,R.id.eye,R.id.tv_forget_password,R.id.tv_area_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                //登录
                login();
                break;
            case R.id.tv_registered:
                //注册
                IntentStart.star(LoginActivity.this, RegisteredActivity.class);
                break;
            case R.id.eye:
                //隐藏显示密码
                isEye = !isEye;
                showHidePassword();
                break;
            case R.id.tv_forget_password:
                //忘记密码
                IntentStart.star(LoginActivity.this,ForgetPasswordActivity.class);
                break;
            case R.id.tv_area_code:
                //显示区号弹窗
//                showCode();
                break;
            default:
                break;
        }
    }


    /**
     * 显示区号弹窗
     */
    private void showCode() {
        String jason= MySharedPreferences.getInstance().getString(MySharedConstants.COUNTRY_CODE);
        if(StringUtils.isEmpty(jason)){
            initHttp(true);
            return;
        }
        UtilTool.closeKeybord(this);
        if(tvAreaCode.isChecked()){
            pickerViewUtil.show();
        }
    }



    /**
     * 登录
     */
    private void login(){
        if (check()) {
            PersenterLogin.getInstance().login(etPhone.getText().toString(), etPassword.getText().toString(),new OnSuccess(this, new OnSuccess.OnSuccessListener() {
                @Override
                public void onSuccess(Object content) {
                    finish();
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
            ToastShow.showToast2(this, getString(R.string.phone_number_cannot_empty));
            return false;
        } else if (StringUtils.isEmpty(etPassword.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etPassword);
            ToastShow.showToast2(this, getString(R.string.password_cannot_empty));
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
}
