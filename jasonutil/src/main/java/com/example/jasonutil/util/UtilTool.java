package com.example.jasonutil.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.widget.TextView;

import com.example.jasonutil.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jsaon
 * @date 2019/2/28
 * 其余工具管理类
 */
public class UtilTool {

    // 判断是否为网址
    public static boolean checkLinkedExe(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            return false;
        }
        resultString = resultString.toLowerCase();
        String pattern = "^(http://|https://|ftp://|mms://|rtsp://){1}.*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(resultString);
        return m.matches();
    }

    public static synchronized String createtFileName() {
        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = fmt.format(dt);
        return fileName;
    }

    /**
     * 去掉多余的0
     * @param sum
     * @return
     */
    public static String removeZero(String sum) {
        if (sum.contains(".")) {
            char[] chars = sum.toCharArray();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < chars.length; i++) {
                list.add(chars[i] + "");
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).equals("0")) {
                    list.remove(i);
                } else if (list.get(i).equals(".")) {
                    list.remove(i);
                    break;
                } else {
                    break;
                }
            }
            String str = "";
            for (String s : list) {
                str += s;
            }
            return str;
        } else {
            return sum;
        }
    }

    /**
     * 获取版本好code
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名
     *
     * @param mContext
     * @return
     */
    public static String getVersionName(Context mContext) {
        String versionCode = "";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 关闭键盘
     *
     * @param context
     * @param v
     */
    public static void hideKeyBoard(Activity context, View v) {
        try {
            if(!ActivityUtil.isActivityOnTop(context))return;
            if(v==null)return;
            ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 转Int
     * @param number
     * @return
     */
    public static int parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转Double
     * @param number
     * @return
     */
    public static double parseDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转Long
     * @param number
     * @return
     */
    public static long parseLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转Float
     * @param number
     * @return
     */
    public static float parseFloat(String number) {
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return 0;
        }
    }



    /**
     * inputStream转String
     *
     * @param is
     * @return
     */
    public static String decompress(InputStream is) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void install(Context context, File file) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        } else {
            String path = file.getAbsolutePath();
            if (!path.contains("file://")) {
                uri = Uri.parse("file://" + path);
            } else {
                uri = Uri.parse(path);
            }
            context.startActivity(install);
        }
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        try {
            context.startActivity(install);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否运行在用户前面
     */
    private boolean onFront(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null || appProcesses.isEmpty())
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName()) &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 启动下载服务
     */
    public static void startDownServer(Context context,Class cl,String name) {
        Intent intent = new Intent(context, cl);
        if (ActivityUtil.isServiceRunning(context, name)) {
            return;
        }
        context.startService(intent);
    }

    /**
     * biemap转byte[]
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     *
     * @param bitmap
     * @param maxkb
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }

    /**
     * 复制到粘贴板
     * @param context
     * @param text
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //clip.getText(); // 粘贴
        clip.setText(text); // 复制
        ToastShow.showToast2(context,context.getString(R.string.copy_success));
    }

    /**
     * 判断当前设备是否有摄像头
     * @return
     */
    public static boolean hasCamera(Context context){
        boolean hasCamera=false;
        PackageManager pm=context.getPackageManager();
        hasCamera=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)||pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)||
                Build.VERSION.SDK_INT<Build.VERSION_CODES.GINGERBREAD||Camera.getNumberOfCameras()>0;
        return hasCamera;
    }

    /**
     * 清楚web缓存
     */
    public static void clearWeb(){
        //清除h5缓存
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.removeAllCookie();
            cookieManager.flush();
        } else {
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
        WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
    }
}
