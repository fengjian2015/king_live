package com.wewin.live.ui.activity.person;


import android.view.View;
import android.widget.EditText;

import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterPersonal;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity {

    @InjectView(R.id.et_content)
    EditText etContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }


    @Override
    protected void init() {
        setTitle(getString(R.string.suggestion_feedback));
    }

    /**
     * 提交反馈
     */
    private void submit(){
        if (StringUtils.isEmpty(etContent.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etContent);
            ToastShow.showToast2(this, getString(R.string.input_feedback));
            return;
        }
        PersenterPersonal.getInstance().feedback(etContent.getText().toString(),UtilTool.getVersionName(this),new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(FeedbackActivity.this, getString(R.string.submit_success));
                finish();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    @OnClick({R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                submit();
                break;

        }
    }
}
