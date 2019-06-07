package com.example.jasonutil.util;

import java.util.Calendar;

/**
 * @author jsaon
 * @date 2019/3/2
 * 日期计算类
 */
public class DateUtil {
    /**
     * 计算剩余时间
     * @param time 毫秒单位
     * @return
     */
    public static String getRemaining(long time){
        if(time <= 1000){
            return "00:00";
        }
        time/=1000;
        long minute = time/60;
        long hour = minute/60;
        long second = time%60;
        minute %= 60;
        String timeStr;
        if(hour == 0){
            timeStr = String.format("%02d:%02d", minute,second);
        }else{
            timeStr = String.format("%02d:%02d:%02d", hour, minute,second);
        }
        return timeStr;
    }

    /**
     * 计算这个月有多少天
     * @param year
     * @param month
     * @return
     */
    public static int getMonthOfDay(int year,int month){
        int day = 0;
        if(year%4==0&&year%100!=0||year%400==0){
            day = 29;
        }else{
            day = 28;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;
            default:
                break;
        }
        return 0;
    }

    /**
     * 获取当前年份
     * @return
     */
    public static int getYear(){
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取当前月份
     * @return
     */
    public static String getMonth(){
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        int month = calendar.get(Calendar.MONTH)+1;
        if(month<10){
            return "0"+month;
        }else {
            return month+"";
        }
    }

    /**
     * 获取当前日
     * @return
     */
    public static String getDay(){
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        int  day = calendar.get(Calendar.DATE);
        if(day<10){
            return "0"+day;
        }else {
            return day+"";
        }
    }

    /**
     * 获取当前星期几
     * @return
     */
    public static String getWeek(){
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        String  mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return "周"+mWay;
    }
}
