package com.wewin.live.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.jasonutil.util.MySharedPreferences;
import com.wewin.live.R;

import java.util.ArrayList;

/**
 * 作者：created by jason on 2019/4/10 10
 */
public class PickerViewUtil {
    private OnPickerReturnListener onPickerReturnListener;
    private Activity mContext;

    private OptionsPickerView<Object> pvOptions;
    private ArrayList<Object> options1Items = new ArrayList<>();

    public PickerViewUtil(Activity activity){
        mContext=activity;
    }

    /**
     * 单条数据
     */
    public PickerViewUtil initCountryCode(){
        try {
            options1Items.clear();
            String jason= MySharedPreferences.getInstance().getString(MySharedConstants.COUNTRY_CODE);
            final JSONArray jsonArray= JSONArray.parseArray(jason);
            if(jsonArray!=null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONArray jsonArray1 = JSONArray.parseArray(jsonArray.getString(i));
                    options1Items.add(jsonArray1.getString(0) +"("+ jsonArray1.getString(1)+")");
                }
            }
            if (pvOptions == null) {
                pvOptions = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        if (onPickerReturnListener == null) {
                            return;
                        }
                        onPickerReturnListener.getData(options1,options2,options3, JSONArray.parseArray(jsonArray.getString(options1)).getString(1));
                    }
                })
                        .setContentTextSize(20)//设置滚轮文字大小
                        .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                        .setSelectOptions(0,0,0)//默认选中项
                        .setBgColor(mContext.getResources().getColor(R.color.white_bg))
                        .setTitleBgColor(mContext.getResources().getColor(R.color.white))
                        .setCancelColor(mContext.getResources().getColor(R.color.gray1))
                        .setSubmitColor(mContext.getResources().getColor(R.color.blue))
                        .setTextColorCenter(Color.BLACK)
                        .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                        .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .setBackgroundId(0x00000000) //设置外部遮罩颜色
                        .setDecorView((ViewGroup)mContext.getWindow().getDecorView().findViewById(android.R.id.content))
                        .build();
                pvOptions.setPicker(options1Items);//二级选择器
            }
            pvOptions.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(Object o) {
                    if (onPickerReturnListener == null) {
                        return;
                    }
                    onPickerReturnListener.onDismiss();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public PickerViewUtil setOnPickerReturnListener(OnPickerReturnListener onPickerReturnListener) {
        this.onPickerReturnListener = onPickerReturnListener;
        return this;
    }



    public interface OnPickerReturnListener {
        void getData(int options1, int options2, int options3,String data);
        void onDismiss();
    }

    public void show(){
        if (pvOptions != null) {
            pvOptions.show();
        }
    }

    public void dismiss() {
        if (pvOptions != null) {
            pvOptions.dismiss();
        }
    }
}
