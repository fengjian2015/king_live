package com.wewin.live.ui.activity.person;

import android.widget.TextView;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.example.jasonutil.util.UtilTool;

import butterknife.InjectView;

/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {

    @InjectView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.about1));
        String result= String.format(getString(R.string.version_name),UtilTool.getVersionName(this));
        tvVersion.setText(result);
    }
}
