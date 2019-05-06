package com.wewin.live.listener;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.wewin.live.ui.activity.MainActivity;
import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.UtilTool;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 错误处理
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final Context mContext;
    private final Thread.UncaughtExceptionHandler defaultUncaught;
    File logFile ;

    public CrashHandler(Context context) {
        super();
        mContext = context;
        defaultUncaught = Thread.getDefaultUncaughtExceptionHandler();
        logFile = new File(FileUtil.getLogDirString(mContext) + "ExceptionLog"  + UtilTool.createtFileName() + ".txt");
        Thread.setDefaultUncaughtExceptionHandler(this); // 设置为当前线程默认的异常处理器
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
// 打印当前的异常信息
        ex.printStackTrace();
        try {
            // 如果我们没处理异常，并且系统默认的异常处理器不为空，则交给系统来处理
            if (!handlelException(ex) && defaultUncaught != null) {
                defaultUncaught.uncaughtException(thread, ex);
            } else {

                // 已经记录完log, 提交服务器
//            upLoadErrorFileToServer(logFile);

                Intent in = new Intent(mContext, MainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果设置了此标志，这个activity将成为一个新task的历史堆栈中的第一个activity
                mContext.startActivity(in);

                // 杀死我们的进程
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
                        android.os.Process.killProcess(android.os.Process.myPid()); // 杀死线程
//                    }
//                }, 2 * 1000);
            }
        }catch (InternalError error){
            error.printStackTrace();
        }
    }


    private boolean handlelException(Throwable ex) {
        // TODO Auto-generated method stub
        if (ex == null) {
            return false;
        }

        PrintWriter pw = null;
        try {
            File file = new File(FileUtil.getLogDirString(mContext));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            pw = new PrintWriter(logFile);

            // 收集手机及错误信息
            collectInfoToSDCard(pw, ex);
            pw.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return true;
    }


    private void collectInfoToSDCard(PrintWriter pw, Throwable ex) throws PackageManager.NameNotFoundException, IllegalAccessException, IllegalArgumentException, PackageManager.NameNotFoundException {
        // TODO Auto-generated method stub
        PackageManager pm = mContext.getPackageManager();
        PackageInfo mPackageInfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        pw.println("time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 记录错误发生的时间
        pw.println("versionCode: " + mPackageInfo.versionCode); // 版本号
        pw.println("versionName: " + mPackageInfo.versionName); // 版本名称

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            pw.print(field.getName() + " : ");
            pw.println(field.get(null).toString());
        }
        ex.printStackTrace(pw);
    }
}
