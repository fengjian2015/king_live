package com.wewin.live.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonutil.dialog.ConfirmCancelDialog;
import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.permission.AuthorizationCheck;
import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.example.jasonutil.util.ActivityManage;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.MySharedPreferences;
import com.example.jasonutil.util.ScreenTools;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.umeng.analytics.MobclickAgent;
import com.wewin.live.R;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterLogin;
import com.wewin.live.ui.activity.HtmlActivity;
import com.wewin.live.ui.activity.SearchActivity;
import com.wewin.live.ui.fragment.HomeFragment;
import com.wewin.live.ui.fragment.LiveFragment;
import com.wewin.live.ui.activity.person.AboutActivity;
import com.wewin.live.ui.activity.person.AccountSettingsActivity;
import com.wewin.live.ui.activity.person.AssetRecordActivity;
import com.wewin.live.ui.activity.person.FeedbackActivity;
import com.wewin.live.ui.activity.person.PersonalActivity;
import com.wewin.live.ui.activity.person.PhoneChangeActivity;
import com.wewin.live.ui.activity.person.ReleaseRecordActivity;
import com.wewin.live.ui.activity.person.SettingsActivity;
import com.wewin.live.ui.activity.person.TaskCenterActivity;
import com.wewin.live.ui.adapter.HomeMainAdapter;
import com.wewin.live.ui.widget.CustomSideMenu;
import com.wewin.live.ui.widget.NoScrollViewPager;
import com.wewin.live.utils.Constants;
import com.wewin.live.utils.GlideUtil;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.MobclickAgentUtil;
import com.wewin.live.utils.MySharedConstants;
import com.wewin.live.utils.NotificationUtil;
import com.wewin.live.utils.SignOutUtil;
import com.wewin.live.utils.down.DownloadService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author jsaon
 * @date 2019/2/27
 * 专用于页面处理等（第三方或者别的处理放到MainActivity）
 */
public abstract class BaseMainAcitvity extends BaseLiveActivity {
    @InjectView(R.id.viewpage)
    NoScrollViewPager viewpage;
    @InjectView(R.id.main_bottom_menu)
    LinearLayout mMainBottomMenu;
    @InjectView(R.id.side_menu_layout)
    protected CustomSideMenu mCustomSideMenu;
    @InjectView(R.id.iv_avatar)
    ImageView ivAvatar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.iv_grade)
    ImageView ivGrade;
    @InjectView(R.id.tv_grade)
    TextView tvGrade;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.tv_gem)
    TextView tvGem;
    @InjectView(R.id.rl_personal)
    RelativeLayout rlPersonal;
    @InjectView(R.id.view_red_point)
    View viewRedPoint;
    @InjectView(R.id.rl_my_message)
    RelativeLayout rlMyMessage;
    @InjectView(R.id.rl_account_settings)
    RelativeLayout rlAccountSettings;
    @InjectView(R.id.rl_task_center)
    RelativeLayout rlTaskCenter;
    @InjectView(R.id.rl_withdrawal_application)
    RelativeLayout rlWithdrawalApplication;
    @InjectView(R.id.rl_fund_record)
    RelativeLayout rlFundRecord;
    @InjectView(R.id.rl_release_record)
    RelativeLayout rlReleaseRecord;
    @InjectView(R.id.rl_setting)
    RelativeLayout rlSetting;
    @InjectView(R.id.view_sign_out)
    View viewSignOut;
    @InjectView(R.id.rl_sign_out)
    RelativeLayout rlSignOut;
    @InjectView(R.id.tv_check_updata)
    TextView tvCheckUpdata;

    private HomeMainAdapter adapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    //双击退出函数
    private static Boolean isExit = false;
    //加载框
    protected ConfirmDialog mConfirmDialog;
    //是否正在下载中
    private boolean isDownload=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //再调用父类之前设置，不然会开启后再关闭
        MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_SHOW_WINDOW, false);
        super.onCreate(savedInstanceState);
        setBarTranslucent();
        ActivityManage.addActivity(this);
        //动画
        IntentStart.initAnimtor(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        init();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        NotificationUtil.clearNotification(this);
    }


    /**
     * 初始化操作
     */
    private void init() {
        setBar();
        initFragment();
        initMenu();
        initAuthorization();
        initData();
        loginOrOut();
        setVersionView();
    }

    private void setBarTranslucent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    /**
     * 初始化侧栏
     */
    private void initMenu(){
        mCustomSideMenu.bringToFront();
        mCustomSideMenu.setBackgroundOrAlpha();
        mCustomSideMenu.setSideMenuListen(new CustomSideMenu.OnSideMenuListen() {
            @Override
            public void setShadowAlpha(int alpha) {
                mCustomSideMenu.getBackground().setAlpha(alpha);
            }
        });
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        if (SignOutUtil.getIsLogin()) {
            UserInfo userInfo=UserInfoDao.queryUserInfo(SignOutUtil.getUserId());
            GlideUtil.setCircleImg(this, userInfo.getAvatar(), ivAvatar);
            tvName.setText(userInfo.getNickName());
            tvGem.setText(getString(R.string.gem)+" "+userInfo.getCoin());
            progressBar.setMax(UtilTool.parseInt(userInfo.getLevel_up()));
            progressBar.setProgress(UtilTool.parseInt(userInfo.getConsumption()));
            tvGrade.setText(getString(R.string.grade)+" "+userInfo.getConsumption()+"/"+userInfo.getLevel_up());
            if(StringUtils.isEmpty(userInfo.getLevel_icon())){
                ivGrade.setVisibility(View.GONE);
            }else {
                ivGrade.setVisibility(View.VISIBLE);
                GlideUtil.setImg(this, userInfo.getLevel_icon(), ivGrade, 0);
            }
        } else {
            progressBar.setProgress(0);
            GlideUtil.setCircleImg(this, null, ivAvatar);
            tvName.setText(getString(R.string.not_logged_in));
            tvGem.setText(getString(R.string.gem)+" 0");
            tvGrade.setText("");
            ivGrade.setVisibility(View.GONE);
        }
    }

    /**
     * 设置版本信息
     */
    protected void setVersionView() {
        String newVersionName = MySharedPreferences.getInstance().getString(MySharedConstants.VERSION_NAME);
        if (StringUtils.isEmpty(newVersionName) || UtilTool.getVersionName(BaseMainAcitvity.this).equals(newVersionName)) {
            tvCheckUpdata.setText(getString(R.string.already_new_version));
            tvCheckUpdata.setTextColor(getResources().getColor(R.color.gray1));
        } else {
            tvCheckUpdata.setText(getString(R.string.discover_new_version));
            tvCheckUpdata.setTextColor(getResources().getColor(R.color.red2));
        }
    }


    /**
     * 申请权限
     */
    private void initAuthorization() {
        Rigger.on(this)
                .permissions(Permission.WRITE_EXTERNAL_STORAGE,Permission.RECORD_AUDIO,Permission.CAMERA)
                .start();
    }


    /**
     * 设置状态栏高度
     */
    public void setBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View rootView = findViewById(R.id.main_root);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootView.getLayoutParams();
            layoutParams.height = ScreenTools.getStateBar3(this);

            View menuView = findViewById(R.id.main_menu);
            LinearLayout.LayoutParams layoutParamsMenu = (LinearLayout.LayoutParams) menuView.getLayoutParams();
            layoutParamsMenu.height = ScreenTools.getStateBar3(this);
        }
    }

    /**
     * 初始化5大模块
     */
    private void initFragment() {
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new LiveFragment());
//        mFragmentList.add(new AboutFragment());
//        mFragmentList.add(new ReviewFragment());
//        mFragmentList.add(new MyFragment());
        initViewPager();
        initBottomMenu();
    }

    private void initViewPager() {
        adapter = new HomeMainAdapter(getSupportFragmentManager(), mFragmentList);
        viewpage.setOffscreenPageLimit(3);
        viewpage.setAdapter(adapter);

        viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelector(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        changeFragment(0);
    }

    //初始化底部菜单栏
    private void initBottomMenu() {
        for (int i = 0; i < mMainBottomMenu.getChildCount(); i++) {
            final View childAt = mMainBottomMenu.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = mMainBottomMenu.indexOfChild(childAt);
                    changeFragment(index);
                    setSelector(index);
                }
            });
        }
        setSelector(0);
    }

    private void changeFragment(int index) {
        viewpage.setCurrentItem(index, false);
    }

    //菜单选项选中处理
    private void setSelector(int index) {
        for (int i = 0; i < mMainBottomMenu.getChildCount(); i++) {
            if (i == index) {
                mMainBottomMenu.getChildAt(i).setSelected(true);
            } else {
                mMainBottomMenu.getChildAt(i).setSelected(false);
            }
        }
    }

    @OnClick({R.id.rl_setting, R.id.rl_task_center, R.id.rl_fund_record, R.id.rl_personal, R.id.rl_account_settings, R.id.rl_release_record, R.id.rl_sign_out
            ,R.id.tv_get_gem,R.id.rl_change_phone,R.id.rl_suggestion_feedback,R.id.rl_check_updata,R.id.rl_about_me,R.id.iv_avatar,R.id.rl_quiz,R.id.ll_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_setting:
                //设置
                IntentStart.starLogin(BaseMainAcitvity.this, SettingsActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_task_center:
                //任务中心
                IntentStart.starLogin(BaseMainAcitvity.this, TaskCenterActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_fund_record:
                //资金记录
                IntentStart.starLogin(BaseMainAcitvity.this, AssetRecordActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_personal:
                //个人资料
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                IntentStart.starLogin(BaseMainAcitvity.this, PersonalActivity.class);
                break;
            case R.id.rl_account_settings:
                //账号设置
                IntentStart.starLogin(BaseMainAcitvity.this, AccountSettingsActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_release_record:
                //发布记录
                IntentStart.starLogin(BaseMainAcitvity.this, ReleaseRecordActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_sign_out:
                //退出登录
                showSignOut();
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.tv_get_gem:
                //获取宝石
                IntentStart.starLogin(BaseMainAcitvity.this, TaskCenterActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_change_phone:
                //手机号
                IntentStart.starLogin(BaseMainAcitvity.this,PhoneChangeActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_suggestion_feedback:
                //意见反馈
                IntentStart.starLogin(BaseMainAcitvity.this, FeedbackActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_check_updata:
                //检查更新
                checkVersion();
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_about_me:
                //关于我们
                IntentStart.star(BaseMainAcitvity.this, AboutActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.iv_avatar:
            case R.id.ll_name:
                //点击头像
                IntentStart.starLogin(BaseMainAcitvity.this, AccountSettingsActivity.class);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            case R.id.rl_quiz:
                //我的竞猜
                Bundle bundle=new Bundle();
                bundle.putString(BaseInfoConstants.URL,Constants.USER_BETTING);
                IntentStart.starLogin(BaseMainAcitvity.this, HtmlActivity.class,bundle);
                mCustomSideMenu.sideMenuScrollNoDuration(false);
                break;
            default:
                break;
        }
    }

    /**
     * 退出或者登录操作
     */
    public void loginOrOut(){
        if(SignOutUtil.getIsLogin()){
            viewSignOut.setVisibility(View.VISIBLE);
            rlSignOut.setVisibility(View.VISIBLE);
        }else {
            viewSignOut.setVisibility(View.GONE);
            rlSignOut.setVisibility(View.GONE);
        }
    }

    /**
     * 退出弹窗
     */
    private void showSignOut() {
        if (IntentStart.starLogin(this)) {
            return;
        }
        new ConfirmCancelDialog(this)
                .showDialog()
                .setTvTitle(getString(R.string.ok_to_log_out))
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        PersenterLogin.getInstance().userLogout(new OnSuccess(BaseMainAcitvity.this));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!mCustomSideMenu.getIsClose()){
                if (!mCustomSideMenu.isBeingClose()) {
                    mCustomSideMenu.sideMenuScroll(false);
                }
                return false;
            }
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    /**
     * 返回键控制
     */
    private void exitBy2Click() {
        if (isExit == false) {
            // 准备退出
            isExit = true;
            ToastShow.showToast2(this, getString(R.string.click_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 取消退出
                    isExit = false;
                }
            },3000);
        } else {
            try {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查版本
     */
    private void checkVersion() {
        MobclickAgent.onEvent(this,MobclickAgentUtil.CLICK_DOWNLOAD);
        if(isDownload){
            ToastShow.showToast2(this,getString(R.string.download_now));
            return;
        }
        String newVersionName = MySharedPreferences.getInstance().getString(MySharedConstants.VERSION_NAME);
        String apkDes = MySharedPreferences.getInstance().getString(MySharedConstants.APK_DES);
        //如果是最新版本则忽略
        if (StringUtils.isEmpty(newVersionName) || UtilTool.getVersionName(this).equals(newVersionName)) {
            ToastShow.showToast2(this, getString(R.string.already_new_version));
            return;
        }
        //判断当前版本apk是否存在，存在则直接安装
        File file = new File(FileUtil.getApkLoc(this), FileUtil.getAPKFileName(newVersionName));
        if (file.exists()) {
            file.delete();
        }
        new ConfirmCancelDialog(this)
                .setTvTitle(getString(R.string.new_now_version) + newVersionName)
                .setBtnConfirm(getString(R.string.start_down))
                .setTvContent(apkDes)
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        if (Build.VERSION.SDK_INT >= 26) {
                            boolean b = getPackageManager().canRequestPackageInstalls();
                            if (b) {
                                installs();
                            } else {
                                Rigger.on(BaseMainAcitvity.this)
                                        .isShowDialog(true)
                                        .permissions(Permission.REQUEST_INSTALL_PACKAGES, Permission.WRITE_EXTERNAL_STORAGE)
                                        .start(new PermissionCallback() {
                                            @Override
                                            public void onGranted() {
                                                installs();
                                            }

                                            @Override
                                            public void onDenied(HashMap<String, Boolean> permissions) {
                                                AuthorizationCheck.authorizationPrompt(BaseMainAcitvity.this, AuthorizationCheck.REQUEST_INSTALL_PACKAGES, true, true);
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
        //不存在就启动服务
        if (!ActivityUtil.isServiceRunning(this, DownloadService.DOWN_LOAD_SERVICE_NAME)) {
            UtilTool.startDownServer(this, DownloadService.class, DownloadService.DOWN_LOAD_SERVICE_NAME);
        }
        String url = MySharedPreferences.getInstance().getString(MySharedConstants.APK_URL);
        String newVersionName = MySharedPreferences.getInstance().getString(MySharedConstants.VERSION_NAME);
        if (mConfirmDialog == null) {
            mConfirmDialog = new ConfirmDialog(BaseMainAcitvity.this);
        }
        if (mConfirmDialog.isShowing()) {
            return;
        }
        mConfirmDialog.showDialog().setTvTitle(getString(R.string.start_down));
        MessageEvent messageEvent = new MessageEvent(MessageEvent.START_UPDATA_APK);
        messageEvent.setDownloadCallback(mDownloadCallback);
        messageEvent.setUrl(url);
        messageEvent.setVersionName(newVersionName);
        EventBus.getDefault().post(messageEvent);

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
            isDownload=true;
            if (!ActivityUtil.isActivityOnTop(BaseMainAcitvity.this)
                    || mConfirmDialog == null
                    || !mConfirmDialog.isShowing()) {
                return;
            }
            mConfirmDialog.setTvTitle(progress + "%");
        }

        @Override
        public void onComplete(File file) {
            isDownload=false;
            if (!ActivityUtil.isActivityOnTop(BaseMainAcitvity.this)
                    || mConfirmDialog == null) {
                return;
            }
            mConfirmDialog.dismiss();
        }

        @Override
        public void onFail(String msg) {
            isDownload=false;
            if (!ActivityUtil.isActivityOnTop(BaseMainAcitvity.this)
                    || mConfirmDialog == null
                    || !mConfirmDialog.isShowing()) {
                return;
            }
            mConfirmDialog.setTvTitle(msg);
        }
    };


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isFinishing()){
            //设置小窗口不显示，当前界面销毁
            dismissWindow(true);
            ActivityManage.removeActivity(this);
            EventBus.getDefault().unregister(this);
            UtilTool.clearWeb();
        }
    }
}
