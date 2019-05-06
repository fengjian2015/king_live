package com.wewin.live.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
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
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterLogin;
import com.wewin.live.ui.activity.HtmlActivity;
import com.wewin.live.utils.Constants;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.PickerViewUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 注册
 */
public class RegisteredActivity extends BaseActivity {

    @InjectView(R.id.tv_agree)
    TextView tvAgree;
    @InjectView(R.id.tv_verification_code)
    TextView tvVerificationCode;
    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.et_code)
    EditText etCode;
    @InjectView(R.id.et_nickName)
    EditText etNickName;
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
        return R.layout.activity_registered;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.registered));
        initAgree();
//        initCode();
        initHttp(false);
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
     * 初始化协议颜色
     */
    private void initAgree() {
        String str = getString(R.string.king_agree_register);
        changeTextColorAndClick(tvAgree,  str, 6, str.length(), R.color.red);
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

    /**
     * 获取验证码
     *
     * @param mobile
     */
    private void gethttpCode(String mobile) {
        PersenterLogin.getInstance().getCode(mobile, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(RegisteredActivity.this, RegisteredActivity.this.getString(R.string.send_success));
                //发送验证码
                timer.start();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 注册
     */
    private void reg() {
        PersenterLogin.getInstance().userReg(etPhone.getText().toString(), etPassword.getText().toString()
                , etNickName.getText().toString(), etCode.getText().toString(), new OnSuccess(this, new OnSuccess.OnSuccessListener() {
                    @Override
                    public void onSuccess(Object content) {
                        ToastShow.showToast2(RegisteredActivity.this, BaseMapInfo.getMsg((BaseMapInfo) content));
                        finish();
                    }

                    @Override
                    public void onFault(String error) {

                    }
                }));
    }


    @OnClick({R.id.tv_verification_code, R.id.tv_registered, R.id.eye, R.id.tv_area_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_verification_code:
                //发送验证码
                if (checkPhone())
                    gethttpCode(etPhone.getText().toString());
                break;
            case R.id.tv_registered:
                //注册
                if (check()) {
                    reg();
                }
                break;
            case R.id.eye:
                //隐藏显示密码
                isEye = !isEye;
                showHidePassword();
                break;
            case R.id.tv_area_code:
//                showCode();
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
        UtilTool.hideKeyBoard(this,tvAreaCode);
        if(tvAreaCode.isChecked()){
            pickerViewUtil.show();
        }
    }


    /**
     * 显示和隐藏密码
     */
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

    /**
     * 校验
     *
     * @return
     */
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

    /**
     * 发送验证码
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!ActivityUtil.isActivityOnTop(RegisteredActivity.this)) return;
            tvVerificationCode.setEnabled(false);
            String result = String.format(getString(R.string.has_been_sent), millisUntilFinished / 1000 + "");
            tvVerificationCode.setText(result);

        }

        @Override
        public void onFinish() {
            if (!ActivityUtil.isActivityOnTop(RegisteredActivity.this)) return;
            tvVerificationCode.setEnabled(true);
            tvVerificationCode.setText(getString(R.string.reacquire_code));

        }
    };


    /**
     * 改变部分字体颜色，并且可以点击
     *
     * @param view
     * @param content
     * @param star
     * @param end
     * @param color
     */
    public  void changeTextColorAndClick(TextView view, String content, int star, int end, final int color) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(content);
        // 在设置点击事件、同时设置字体颜色
        ClickableSpan clickableSpanTwo = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString(BaseInfoConstants.URL,Constants.USER_AGREEMENT);
                IntentStart.star(RegisteredActivity.this,HtmlActivity.class,bundle);
            }

            @Override
            public void updateDrawState(TextPaint paint) {
                paint.setColor(RegisteredActivity.this.getResources().getColor(color));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);
            }
        };

        spannableBuilder.setSpan(clickableSpanTwo, star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 不设置点击不生效
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spannableBuilder);
        // 去掉点击后文字的背景色
        view.setHighlightColor(RegisteredActivity.this.getResources().getColor(com.example.jasonutil.R.color.transparent));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}
