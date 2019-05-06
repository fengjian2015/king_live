package com.wewin.live.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonutil.dialog.ConfirmCancelDialog;
import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.DateUtil;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.ui.activity.Live.LiveConfigActivity;
import com.wewin.live.ui.activity.Live.LiveStartActivity;
import com.wewin.live.ui.activity.Live.VideoDetailsActivity;
import com.wewin.live.ui.activity.person.TaskCenterActivity;
import com.wewin.live.ui.activity.video.VideoSelectActivity;
import com.wewin.live.utils.IntentStart;

import java.util.HashMap;

/**
 * @author jsaon
 * @date 2019/3/7
 * 弹出功能盘
 */
public class FeaturesListDialog extends Dialog implements View.OnClickListener {
    private Activity context;

    private TextView tv_day;
    private TextView week;
    private TextView tv_year_month;
    private LinearLayout ll_live;
    private LinearLayout ll_video;
    private LinearLayout ll_dynamic;
    private LinearLayout ll_check_in;
    private ImageView iv_finish;
    private RelativeLayout rl_finish;
    //判断权限有几个已申请

    public FeaturesListDialog(Activity context) {
        super(context, R.style.BottomDialog2);
        View view = View.inflate(context, R.layout.dialog_features_list, null);
        this.context = context;
        initView(view);
        //获得dialog的window窗口
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        if (!ActivityUtil.isActivityOnTop(context)) return;
        window.setWindowAnimations(R.style.BottomDialog);
        setContentView(view);

    }

    private void initView(View view) {
        tv_day = view.findViewById(R.id.tv_day);
        week = view.findViewById(R.id.week);
        tv_year_month = view.findViewById(R.id.tv_year_month);
        ll_live = view.findViewById(R.id.ll_live);
        ll_video = view.findViewById(R.id.ll_video);
        ll_dynamic = view.findViewById(R.id.ll_dynamic);
        ll_check_in = view.findViewById(R.id.ll_check_in);
        iv_finish = view.findViewById(R.id.iv_finish);
        rl_finish = view.findViewById(R.id.rl_finish);
        ll_live.setOnClickListener(this);
        ll_video.setOnClickListener(this);
        ll_dynamic.setOnClickListener(this);
        ll_check_in.setOnClickListener(this);

        iv_finish.setOnClickListener(this);
        rl_finish.setOnClickListener(this);

        tv_day.setText(DateUtil.getDay());
        week.setText(DateUtil.getWeek());
        tv_year_month.setText(DateUtil.getMonth() + "/" + DateUtil.getYear());

    }

    public void showAtLocation() {
        if (!ActivityUtil.isActivityOnTop(context)) return;
        show();
        mHandler.sendEmptyMessageDelayed(1, 200);
        mHandler.sendEmptyMessageDelayed(2, 300);
        mHandler.sendEmptyMessageDelayed(3, 400);
        mHandler.sendEmptyMessageDelayed(4, 500);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AnimatorTool.getInstance().viewAnimator(ll_live);
                    break;
                case 2:
                    AnimatorTool.getInstance().viewAnimator(ll_video);
                    break;
                case 3:
                    AnimatorTool.getInstance().viewAnimator(ll_dynamic);
                    break;
                case 4:
                    AnimatorTool.getInstance().viewAnimator(ll_check_in);
                    break;
            }

        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rl_finish:
            case R.id.iv_finish:
                dismiss();
                break;
            case R.id.ll_live:
                // 直播
                dismiss();
                goLive();
                break;
            case R.id.ll_video:
                //上传视频
                dismiss();
                IntentStart.starLogin(context,VideoSelectActivity.class);
                break;
            case R.id.ll_dynamic:
                dismiss();
                // TODO: 2019/3/7 发布动态  隐藏
                Bundle bundle1 = new Bundle();
                bundle1.putString(BaseInfoConstants.PULL_URL,"https://qqjs.j5349h.cn/5showcam/8198_2.flv?auth_key=1554230909-0-0-b7f122e0a0e6489dd31a4d79111f02f8&weight=0");
                IntentStart.starLogin(context, VideoDetailsActivity.class, bundle1);
                break;
            case R.id.ll_check_in:
                dismiss();
                IntentStart.starLogin(context, TaskCenterActivity.class);
                break;
        }
    }

    /**
     * 去直播
     */
    private void goLive(){
        String isanchor = UserInfoDao.findisanchor();
        if ("true".equals(isanchor)) {
            IntentStart.starLogin(context, LiveConfigActivity.class);
        }else{
            new ConfirmCancelDialog(context)
                    .setTvTitle(context.getString(R.string.not_anchor_prompt))
                    .showDialog()
                    .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                        @Override
                        public void onClick() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
