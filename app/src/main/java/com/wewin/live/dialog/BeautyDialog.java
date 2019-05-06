package com.wewin.live.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.example.jasonutil.util.ActivityUtil;
import com.wewin.live.R;
import com.wewin.live.aliyun.LivePushManage;

/**
 * Created by xingyun on 2016/7/19.
 * 美颜配置弹窗
 */
public class BeautyDialog extends Dialog implements SeekBar.OnSeekBarChangeListener{

    SeekBar seekBarWhite;
    SeekBar seekBarBuffing;
    SeekBar seekBarRuddy;
    SeekBar seekBarBigEye;
    SeekBar seekBarThinFace;
    SeekBar seekBarShortenFace;
    SeekBar seekBarCheekPink;

    private Activity context;
    private View view;

    private LivePushManage livePushManage;

    private int[] beautyInt;

    public BeautyDialog(Activity context, LivePushManage livePushManage) {
        super(context, R.style.BottomDialog3);
        this.livePushManage=livePushManage;
        view = View.inflate(context, R.layout.dialog_live_setting, null);
        this.context = context;
        initView();
        initDialog();
    }

    private void initDialog(){
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

    private void initView() {
        beautyInt=livePushManage.getBeautyInt();
        seekBarWhite=view.findViewById(R.id.seekBar_white);
        seekBarBuffing=view.findViewById(R.id.seekBar_buffing);
        seekBarRuddy=view.findViewById(R.id.seekBar_ruddy);
        seekBarBigEye=view.findViewById(R.id.seekBar_bigEye);
        seekBarThinFace=view.findViewById(R.id.seekBar_thinFace);
        seekBarShortenFace=view.findViewById(R.id.seekBar_shortenFace);
        seekBarCheekPink=view.findViewById(R.id.seekBar_cheekPink);

        seekBarWhite.setProgress(beautyInt[0]);
        seekBarBuffing.setProgress(beautyInt[1]);
        seekBarRuddy.setProgress(beautyInt[2]);
        seekBarBigEye.setProgress(beautyInt[3]);
        seekBarThinFace.setProgress(beautyInt[4]);
        seekBarShortenFace.setProgress(beautyInt[5]);
        seekBarCheekPink.setProgress(beautyInt[6]);

        seekBarWhite.setOnSeekBarChangeListener(this);
        seekBarBuffing.setOnSeekBarChangeListener(this);
        seekBarRuddy.setOnSeekBarChangeListener(this);
        seekBarBigEye.setOnSeekBarChangeListener(this);
        seekBarThinFace.setOnSeekBarChangeListener(this);
        seekBarShortenFace.setOnSeekBarChangeListener(this);
        seekBarCheekPink.setOnSeekBarChangeListener(this);
    }


    @Override
    public void dismiss() {
        livePushManage.setBeautyInt(beautyInt);
        super.dismiss();
    }


    /**
     * 显示
     */
    public BeautyDialog showAtLocation() {
        if (!ActivityUtil.isActivityOnTop(context)) return this;
        show();
        return this;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            switch (seekBar.getId()) {
                case R.id.seekBar_white:
                    beautyInt[0]=i;
                    livePushManage.setBeautyWhite(i);
                    break;
                case R.id.seekBar_buffing:
                    beautyInt[1]=i;
                    livePushManage.setBeautyBuffing(i);
                    break;
                case R.id.seekBar_ruddy:
                    beautyInt[2]=i;
                    livePushManage.setBeautyRuddy(i);
                    break;
                case R.id.seekBar_bigEye:
                    beautyInt[3]=i;
                    livePushManage.setBeautyBigEye(i);
                    break;
                case R.id.seekBar_thinFace:
                    beautyInt[4]=i;
                    livePushManage.setBeautyThinFace(i);
                    break;
                case R.id.seekBar_shortenFace:
                    beautyInt[5]=i;
                    livePushManage.setBeautyShortenFace(i);
                    break;
                case R.id.seekBar_cheekPink:
                    beautyInt[6]=i;
                    livePushManage.setBeautyCheekPink(i);
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
