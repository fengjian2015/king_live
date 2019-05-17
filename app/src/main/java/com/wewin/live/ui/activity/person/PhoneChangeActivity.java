package com.wewin.live.ui.activity.person;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterLogin;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.PickerViewUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 修改手机号
 */
public class PhoneChangeActivity extends BaseActivity {
    @InjectView(R.id.tv_area_code)
    CheckBox tvAreaCode;
    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.et_code)
    EditText etCode;
    @InjectView(R.id.tv_verification_code)
    TextView tvVerificationCode;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.eye)
    ImageView mEye;

    //显示隐藏密码
    boolean isEye = false;
    //区号相关数据
    private PickerViewUtil pickerViewUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_change;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.modify_phone_number));
//        initCode();
        initHttp(false);
    }

    /**
     * 获取网络请求数据
     */
    private void initHttp(boolean isShow) {
        PersenterLogin.getInstance().getCountryCode(isShow, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
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


    @OnClick({R.id.tv_verification_code, R.id.tv_registered, R.id.tv_area_code,R.id.eye})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_verification_code:
                //发送验证码
                if (checkPhone())
                    gethttpCode(etPhone.getText().toString());
                break;
            case R.id.tv_registered:
                //修改手机号
                if (check()) {
                    nodifyPhone();
                }
                break;
            case R.id.tv_area_code:
//                showCode();
                break;
            case R.id.eye:
                //隐藏显示密码
                isEye = !isEye;
                showHidePassword();
                break;
        }
    }

    /**
     * 显示区号弹窗
     */
    private void showCode() {
        String jason = MySharedPreferences.getInstance().getString(MySharedConstants.COUNTRY_CODE);
        if (StringUtils.isEmpty(jason)) {
            initHttp(true);
            return;
        }
        UtilTool.closeKeybord(this);
        if (tvAreaCode.isChecked()) {
            pickerViewUtil.show();
        }
    }

    /**
     * 修改手机号
     */
    private void nodifyPhone() {
        PersenterPersonal.getInstance().modifyMobile(etPhone.getText().toString(), etCode.getText().toString(),etPassword.getText().toString()
                 ,new OnSuccess(this, new OnSuccess.OnSuccessListener() {
                    @Override
                    public void onSuccess(Object content) {
                        ToastShow.showToast2(PhoneChangeActivity.this, PhoneChangeActivity.this.getString(R.string.change_success));
                        finish();
                    }

                    @Override
                    public void onFault(String error) {

                    }
                }));
    }

    /**
     * 获取验证码
     *
     * @param mobile
     */
    private void gethttpCode(String mobile) {
        PersenterPersonal.getInstance().getBindCode(mobile, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(PhoneChangeActivity.this, PhoneChangeActivity.this.getString(R.string.send_success));
                //发送验证码
                timer.start();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 发送验证码
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!ActivityUtil.isActivityOnTop(PhoneChangeActivity.this)) return;
            tvVerificationCode.setEnabled(false);
            String result = String.format(getString(R.string.has_been_sent), millisUntilFinished / 1000 + "");
            tvVerificationCode.setText(result);

        }

        @Override
        public void onFinish() {
            if (!ActivityUtil.isActivityOnTop(PhoneChangeActivity.this)) return;
            tvVerificationCode.setEnabled(true);
            tvVerificationCode.setText(getString(R.string.reacquire_code));

        }
    };

    /**
     * 检查手机号
     *
     * @return
     */
    private boolean checkPhone() {
        if (StringUtils.isEmpty(etPhone.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etPhone);
            ToastShow.showToast2(this, getString(R.string.phone_number_cannot_empty));
            return false;
        }
        return true;
    }


    private boolean check() {
        if (StringUtils.isEmpty(etPhone.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etPhone);
            ToastShow.showToast2(this, getString(R.string.phone_number_cannot_empty));
            return false;
        } else if (StringUtils.isEmpty(etCode.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etCode);
            ToastShow.showToast2(this, getString(R.string.verification_code_cannot_empty));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}
