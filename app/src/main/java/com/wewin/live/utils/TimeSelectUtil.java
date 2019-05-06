package com.wewin.live.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.jasonutil.util.DateUtil;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by jason on 2019/1/2.
 * 时间选择管理类
 */

public class TimeSelectUtil {
    //时间选择
    private OptionsPickerView pvOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String[] mDateArr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private SimpleDateFormat mSimpleDateFormat;

    private Activity mContext;
    private String mDate;
    private OnTimeReturnListener mOnTimeReturnListener;

    private int number=80;
    //初始化默认年月日的位置
    private int y;
    private int m;
    private int d;

    /**
     * @param context
     */
    public TimeSelectUtil(Activity context) {
        mContext = context;
        getOptionData();
    }

    public void setDefault(String date){
        if(StringUtils.isEmpty(date))return;
        try {
            String[] split = date.split("-");
            y=options1Items.indexOf(split[0]);
            m=options2Items.get(y).indexOf(split[1]);
            d=options3Items.get(y).get(m).indexOf(split[2]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setOnTimeReturnListener(OnTimeReturnListener onTimeReturnListener) {
        mOnTimeReturnListener = onTimeReturnListener;
    }

    public void initOptionPicker() {
        try {
            if (pvOptions == null) {
                pvOptions = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        mDate= options1Items.get(options1)
                                + "-" + options2Items.get(options1).get(options2)
                                + "-" + options3Items.get(options1).get(options2).get(options3);
                        if (mOnTimeReturnListener == null) return;
                        mOnTimeReturnListener.getTime(mDate);
                    }
                })
                        .setContentTextSize(20)//设置滚轮文字大小
                        .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                        .setSelectOptions(y,m,d)//默认选中项
                        .setBgColor(mContext.getResources().getColor(R.color.white_bg))
                        .setTitleBgColor(mContext.getResources().getColor(R.color.white))
                        .setCancelColor(mContext.getResources().getColor(R.color.black1))
                        .setSubmitColor(mContext.getResources().getColor(R.color.black1))
                        .setTextColorCenter(Color.BLACK)
                        .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                        .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .setLabels(mContext.getResources().getString(R.string.year), mContext.getResources().getString(R.string.month), mContext.getResources().getString(R.string.day))
                        .setBackgroundId(0x00000000) //设置外部遮罩颜色
                        .setDecorView((ViewGroup)mContext.getWindow().getDecorView().findViewById(android.R.id.content))
                        .build();
                pvOptions.setPicker(options1Items, options2Items,options3Items);//二级选择器
            }
            pvOptions.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getDate() {
        return mDate;
    }
    int year;
    int day;
    int month;
    private void getOptionData() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        mDate = mSimpleDateFormat.format(date);

        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DATE);

        for (int i = 0; i < number; i++) {
            //选项1
            options1Items.add(year - i + "");
            ArrayList<String> options2Items_01 = new ArrayList<>();
            ArrayList<ArrayList<String>> options3Items_01 = new ArrayList<>();
            for(String s : mDateArr){
                //选项3

                //选项2
                if (i != 0) {
                    options2Items_01.add(s);
                    options3Items_01.add(getDay(year-i,UtilTool.parseInt(s)));
                } else {
                    if (month >= Integer.parseInt(s)) {
                        options2Items_01.add(s);
                        options3Items_01.add(getDay(year-i,UtilTool.parseInt(s)));
                    }
                }
            }
            Collections.reverse(options2Items_01);
            Collections.reverse(options3Items_01);
            options2Items.add(options2Items_01);
            options3Items.add(options3Items_01);
        }
    }

    public ArrayList<String> getDay(int y,int m){
        ArrayList<String> options3Items_02 = new ArrayList<>();
        int num=DateUtil.getMonthOfDay(y,m);
        if(y==year&&m==month){
            for (int i=1;i<day+1;i++){
                if(i<10){
                    options3Items_02.add("0"+i);
                }else{
                    options3Items_02.add(i+"");
                }
            }
        }else {
            for(int i=1;i<num+1;i++){
                if(i<10){
                    options3Items_02.add("0"+i);
                }else{
                    options3Items_02.add(i+"");
                }

            }
        }
        Collections.reverse(options3Items_02);
        return options3Items_02;
    }




    public interface OnTimeReturnListener {
        void getTime(String time);
    }

    public void dismiss() {
        if (pvOptions != null)
            pvOptions.dismiss();
    }
}
