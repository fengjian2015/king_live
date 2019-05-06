package com.wewin.live.ui.activity.person;

import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jasonutil.dialog.ConfirmCancelDialog;
import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.permission.AuthorizationCheck;
import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.base.MyApp;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.UiUtil;
import com.wewin.live.utils.down.DownloadService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 设置界面
 */
public class SettingsActivity extends BaseActivity {

    @InjectView(R.id.rl_all_reminders)
    RelativeLayout rlAllReminders;
    @InjectView(R.id.rl_security_privacy)
    RelativeLayout rlSecurityPrivacy;
    @InjectView(R.id.on_off_y)
    Switch onOffY;
    @InjectView(R.id.rl_y)
    RelativeLayout rlY;
    @InjectView(R.id.on_off_fourg)
    Switch onOffFourg;
    @InjectView(R.id.rl_network_fourg)
    RelativeLayout rlNetworkFourg;
    @InjectView(R.id.on_off_little_window)
    Switch onOffLittleWindow;
    @InjectView(R.id.rl_little_window)
    RelativeLayout rlLittleWindow;
    @InjectView(R.id.tv_cach)
    TextView tvCach;
    @InjectView(R.id.rl_clear_cach)
    RelativeLayout rlClearCach;
    @InjectView(R.id.rl_suggestion_feedback)
    RelativeLayout rlSuggestionFeedback;
    @InjectView(R.id.tv_check_updata)
    TextView tvCheckUpdata;
    @InjectView(R.id.rl_check_updata)
    RelativeLayout rlCheckUpdata;
    @InjectView(R.id.rl_about_me)
    RelativeLayout rlAboutMe;

    private ConfirmDialog mConfirmDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.setting));
        checkVersionHttp(false);
        UiUtil.setOnOf(MySharedConstants.ON_OFF_Y, onOffY);
        UiUtil.setOnOf(MySharedConstants.ON_OFF_FOUR_G, onOffFourg);
        UiUtil.setOnOf(MySharedConstants.ON_OFF_LITTLE_WINDOW, onOffLittleWindow);
        String fileSize = FileUtil.FormetFileSize(FileUtil.getFolderSize(new File(FileUtil.getStorageString(this))));
        tvCach.setText(fileSize);
        setVersionView();
    }

    /**
     * 获取版本信息
     *
     * @param isShow
     */
    private void checkVersionHttp(boolean isShow) {
        PersenterPersonal.getInstance().getConfig(isShow, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                setVersionView();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    @OnClick({R.id.on_off_y, R.id.on_off_fourg, R.id.on_off_little_window, R.id.rl_clear_cach, R.id.rl_security_privacy, R.id.rl_all_reminders
            , R.id.rl_suggestion_feedback, R.id.rl_about_me, R.id.rl_check_updata})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.on_off_y:
                //是否开启硬件编码
                UiUtil.changeOnOff(MySharedConstants.ON_OFF_Y, onOffY);
                break;
            case R.id.on_off_fourg:
                //是否在移动网下播放
                UiUtil.changeOnOff(MySharedConstants.ON_OFF_FOUR_G, onOffFourg);
                break;
            case R.id.on_off_little_window:
                //是否开启小窗口播放
                checkAlertWindow();
                break;
            case R.id.rl_clear_cach:
                //清除缓存
                clearCach();
                break;
            case R.id.rl_security_privacy:
                //安全隐私
                IntentStart.star(this, SecurityPrivacyActivity.class);
                break;
            case R.id.rl_all_reminders:
                //全部提醒
                IntentStart.star(this, AllRemindersActivity.class);
                break;
            case R.id.rl_suggestion_feedback:
                //意见反馈
                IntentStart.star(this, FeedbackActivity.class);
                break;
            case R.id.rl_about_me:
                //关于我们
                IntentStart.star(this, AboutActivity.class);
                break;
            case R.id.rl_check_updata:
                //检查更新
                checkVersion();
                break;
        }
    }

    /**
     * 设置版本信息
     */
    private void setVersionView() {
        String newVersionName = MySharedPreferences.getInstance().getString(MySharedConstants.VERSION_NAME);
        if (StringUtils.isEmpty(newVersionName) || UtilTool.getVersionName(SettingsActivity.this).equals(newVersionName)) {
            tvCheckUpdata.setText(getString(R.string.already_new_version));
            tvCheckUpdata.setTextColor(getResources().getColor(R.color.gray1));
        } else {
            tvCheckUpdata.setText(getString(R.string.discover_new_version));
            tvCheckUpdata.setTextColor(getResources().getColor(R.color.red2));
        }
    }

    /**
     * 小窗口开关
     */
    private void checkAlertWindow() {
        //检查是否已经授予权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                //若未授权则请求权限
                AuthorizationCheck.authorizationPrompt(SettingsActivity.this, AuthorizationCheck.SYSTEM_ALERT_WINDOW, true, true);
                onOffLittleWindow.setChecked(false);
            }else{
                if (!UiUtil.changeOnOff(MySharedConstants.ON_OFF_LITTLE_WINDOW, onOffLittleWindow)) {
                    MyApp.getInstance().mSmallViewLayout.dismissWindow(true);
                }
            }
        }else {
            if (!UiUtil.changeOnOff(MySharedConstants.ON_OFF_LITTLE_WINDOW, onOffLittleWindow)) {
                MyApp.getInstance().mSmallViewLayout.dismissWindow(true);
            }
        }

    }


    /**
     * 检查版本
     */
    private void checkVersion() {
        String newVersionName = MySharedPreferences.getInstance().getString(MySharedConstants.VERSION_NAME);
        String apk_des = MySharedPreferences.getInstance().getString(MySharedConstants.APK_DES);
        //如果是最新版本则忽略
        if (StringUtils.isEmpty(newVersionName) || UtilTool.getVersionName(SettingsActivity.this).equals(newVersionName)) {
            ToastShow.showToast2(this, getString(R.string.already_new_version));
            return;
        }
        //判断当前版本apk是否存在，存在则直接安装
        File file = new File(FileUtil.getApkLoc(this), FileUtil.getAPKFileName(newVersionName));
        if (file.exists()) {
            UtilTool.install(this, file);
            return;
        }
        new ConfirmCancelDialog(this)
                .setTvTitle(getString(R.string.new_now_version) + newVersionName)
                .setBtnConfirm(getString(R.string.start_down))
                .setTvContent(apk_des)
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (Build.VERSION.SDK_INT >= 26) {
                            boolean b = getPackageManager().canRequestPackageInstalls();
                            if (b) {
                                installs();
                            } else {
                                Rigger.on(SettingsActivity.this)
                                        .isShowDialog(true)
                                        .permissions(Permission.REQUEST_INSTALL_PACKAGES, Permission.WRITE_EXTERNAL_STORAGE)
                                        .start(new PermissionCallback() {
                                            @Override
                                            public void onGranted() {
                                                installs();
                                            }

                                            @Override
                                            public void onDenied(HashMap<String, Boolean> permissions) {
                                                AuthorizationCheck.authorizationPrompt(SettingsActivity.this, AuthorizationCheck.REQUEST_INSTALL_PACKAGES, true, true);
                                            }
                                        });
                            }
                        } else {
                            installs();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .showDialog();
    }

    /**
     * 下载或者直接安装
     */
    private void installs() {
        String url = MySharedPreferences.getInstance().getString(MySharedConstants.APK_URL);
        String newVersionName = MySharedPreferences.getInstance().getString(MySharedConstants.VERSION_NAME);
        if (mConfirmDialog == null) {
            mConfirmDialog = new ConfirmDialog(SettingsActivity.this);
        }
        if (mConfirmDialog.isShowing()) return;
        mConfirmDialog.showDialog().setTvTitle(getString(R.string.start_down));
        MessageEvent messageEvent = new MessageEvent(MessageEvent.START_UPDATA_APK);
        messageEvent.setDownloadCallback(mDownloadCallback);
        messageEvent.setUrl(url);
        messageEvent.setVersionName(newVersionName);
        EventBus.getDefault().post(messageEvent);

    }


    /**
     * 清除缓存
     */
    private void clearCach() {
        new ConfirmCancelDialog(this)
                .showDialog()
                .setTvTitle(getString(R.string.clear_wait))
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (FileUtil.deleteFolderFile(FileUtil.getStorageString(SettingsActivity.this), false)) {
                            tvCach.setText("0B");
                            ToastShow.showToast2(SettingsActivity.this, getString(R.string.clear_success));
                        } else {
                            ToastShow.showToast2(SettingsActivity.this, getString(R.string.clear_error));
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    /**
     * 下载回调
     */
    DownloadService.DownloadCallback mDownloadCallback = new DownloadService.DownloadCallback() {
        @Override
        public void onPrepare() {

        }

        @Override
        public void onProgress(int progress) {
            if (!ActivityUtil.isActivityOnTop(SettingsActivity.this)
                    || mConfirmDialog == null
                    || !mConfirmDialog.isShowing())
                return;
            mConfirmDialog.setTvTitle(progress + "%");
        }

        @Override
        public void onComplete(File file) {
            if (!ActivityUtil.isActivityOnTop(SettingsActivity.this)
                    || mConfirmDialog == null)
                return;
            mConfirmDialog.dismiss();
        }

        @Override
        public void onFail(String msg) {
            if (!ActivityUtil.isActivityOnTop(SettingsActivity.this)
                    || mConfirmDialog == null
                    || !mConfirmDialog.isShowing())
                return;
            mConfirmDialog.setTvTitle(msg);
        }
    };
}
