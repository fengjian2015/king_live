package com.wewin.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.ui.activity.login.LoginActivity;

import java.util.HashMap;

/**
 * @author jsaon
 * @date 2019/2/28
 * 跳转类
 */
public class IntentStart {
    public static final String FILE_URL = "file_url";
    public static final String BEAUTY = "beauty";

    /**
     * 直接跳转,不带动画
     *
     * @param context
     * @param cl
     */
    public static void starNoAnimtor(Context context, Class cl) {
        Intent intent = new Intent(context, cl);
        context.startActivity(intent);
    }

    /**
     * 直接跳转
     *
     * @param context
     * @param cl
     */
    public static void star(Context context, Class cl) {
        Intent intent = new Intent(context, cl);
        staActivity(context, intent);
    }

    /**
     * 跳转传递参数
     *
     * @param context
     * @param cl
     * @param bundle
     */
    public static void star(Context context, Class cl, Bundle bundle) {
        Intent intent = new Intent(context, cl);
        intent.putExtras(bundle);
        staActivity(context, intent);
    }

    /**
     * 判断是否登录，未登录跳转到登录界面
     *
     * @param context
     * @return true 跳转到登录页面
     */
    public static boolean starLogin(Context context) {
        if (!SignOutUtil.getIsLogin()) {
            Intent intent = new Intent(context, LoginActivity.class);
            staActivity(context, intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否登录，未登录跳转到登录界面 否则跳转到指定页面
     *
     * @param context
     * @return true 跳转到登录页面
     */
    public static void starLogin(Context context, Class cl) {
        if (!starLogin(context)) {
            star(context, cl);
        }
    }

    /**
     * 判断是否登录，未登录跳转到登录界面 否则跳转到指定页面
     *
     * @param context
     * @return true 跳转到登录页面
     */
    public static void starLogin(Context context, Class cl, Bundle bundle) {
        if (!starLogin(context)) {
            star(context, cl, bundle);
        }
    }

    /**
     * 跳转清除
     *
     * @param context
     * @return true 跳转到登录页面
     */
    public static void starNoAnimtorFinishAll(Context context, Class cl) {
        Intent intent = new Intent(context, cl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 跳转清除
     *
     * @param context
     * @return true 跳转到登录页面
     */
    public static void starFinishAll(Context context, Class cl) {
        Intent intent = new Intent(context, cl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        staActivity(context, intent);
    }

    /**
     * 需要动画的跳转
     *
     * @param activity
     * @param intent
     */
    private static void staActivity(Context activity, Intent intent) {
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP&&activity instanceof Activity) {
            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) activity);
            activity.startActivity(intent,transitionActivityOptions.toBundle());
            return;
        }*/
        activity.startActivity(intent);
    }

    public static void starForResult(Activity context, Class cl, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cl);
        intent.putExtras(bundle);//将Bundle添加到Intent,也可以在Bundle中添加相应数据传递给下个页面,例如：bundle.putString("abc", "bbb");
        context.startActivityForResult(intent, requestCode);
    }


    /**
     * 拍摄视频
     */
    public static void takeVideo(final Activity context, final int request) {
        if (!UtilTool.hasCamera(context)) return;
        Rigger.on(context)
                .permissions(Permission.CAMERA,Permission.WRITE_EXTERNAL_STORAGE)
                .isShowDialog(true)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        // 录制视频最大时长15s
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                        context.startActivityForResult(intent, request);
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });
    }


    /**
     * 初始化动画
     *
     * @param activity setExitTransition() ：当A startB时，使A中的View退出场景的transition    在A中设置
     *                 setEnterTransition() ：当A startB时，使B中的View进入场景的transition    在B中设置
     *                 <p>
     *                 setReenterTransition() ： 当B 返回A时，使A中的View进入场景的transition   在A中设置
     *                 setReturnTransition() ：当B 返回A时，使B中的View退出场景的transition  在B中设置
     *                 setAllowEnterTransitionOverlap()：是否运行布局显示时重叠
     *                 setAllowReturnTransitionOverlap()：是否运行布局显示时重叠
     *                 需要配合使用才有对应的效果
     */
    public static void initAnimtor(Activity activity) {
        /*activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Fade fade = new Fade();//渐隐
            fade.setDuration(500);
            Slide slide = new Slide(Gravity.END);//右边平移
            slide.setDuration(500);
            Explode explode = new Explode();//展开回收
            explode.setDuration(500);
            //A
            activity.getWindow().setExitTransition(fade);
            activity.getWindow().setReenterTransition(slide);

            //B
            Slide slide_b = new Slide();
            slide_b.setDuration(500);
            slide_b.setSlideEdge(Gravity.START);
            activity.getWindow().setEnterTransition(slide_b);
            activity.getWindow().setReturnTransition(fade);
        }*/
    }
}
