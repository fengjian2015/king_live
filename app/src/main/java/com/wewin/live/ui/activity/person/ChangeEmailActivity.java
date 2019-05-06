package com.wewin.live.ui.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.utils.SignOutUtil;

import butterknife.InjectView;
import butterknife.OnClick;

public class ChangeEmailActivity extends BaseActivity {

    @InjectView(R.id.et_emali)
    EditText etEmail;
    @InjectView(R.id.et_code)
    EditText etCode;
    @InjectView(R.id.tv_verification_code)
    TextView tvVerificationCode;

    private String email;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_email;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.bind_email));
        initData();
    }

    private void initData(){
        Bundle bundle=getIntent().getExtras();
        email = bundle.getString(BaseInfoConstants.USER_EMAIL);
        if(StringUtils.isEmpty(email))return;
        etEmail.setText(email);
        etEmail.setSelection(email.length());
    }

    @OnClick({R.id.tv_verification_code, R.id.tv_registered})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_verification_code:
                //发送验证码
                if (checkEmail())
                    timer.start();
                break;
            case R.id.tv_registered:
                if(check()){
                    //绑定
                    bind();
                }
                break;

        }
    }

    private void bind(){
        PersenterPersonal.getInstance().bindEmail(etCode.getText().toString(),etEmail.getText().toString(),new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                UserInfoDao.updateEmail(etEmail.getText().toString(),SignOutUtil.getUserId());
                Bundle bundle=new Bundle();
                bundle.putString(BaseInfoConstants.USER_EMAIL,etEmail.getText().toString());
                Intent intent=new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 检查邮箱
     * @return
     */
    private boolean checkEmail(){
        if (StringUtils.isEmpty(etEmail.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etEmail);
            ToastShow.showToast2(this,getString(R.string.input_email));
            return false;
        }
        return true;
    }

    private boolean check() {
        if (!checkEmail()) {
            return false;
        }else if(StringUtils.isEmpty(etCode.getText().toString())){
            AnimatorTool.getInstance().editTextAnimator(etCode);
            ToastShow.showToast2(this,getString(R.string.verification_code_cannot_empty));
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
            if (!ActivityUtil.isActivityOnTop(ChangeEmailActivity.this)) return;
            tvVerificationCode.setEnabled(false);
            String result = String.format(getString(R.string.has_been_sent), millisUntilFinished / 1000 + "");
            tvVerificationCode.setText(result);

        }

        @Override
        public void onFinish() {
            if (!ActivityUtil.isActivityOnTop(ChangeEmailActivity.this)) return;
            tvVerificationCode.setEnabled(true);
            tvVerificationCode.setText(getString(R.string.reacquire_code));

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}
