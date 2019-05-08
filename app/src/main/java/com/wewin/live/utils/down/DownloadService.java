package com.wewin.live.utils.down;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.example.jasonutil.util.LogUtil;
import com.wewin.live.R;
import com.example.jasonutil.util.FileUtil;
import com.wewin.live.utils.MessageEvent;
import com.example.jasonutil.util.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author jsaon
 * @date 2019/3/12
 */
public class DownloadService extends Service {
    public final static String DOWN_LOAD_SERVICE_NAME = "com.wewin.live.utils.down.DownloadService";
    //定义notify的id，避免与其它的notification的处理冲突
    private static final int NOTIFY_ID = 0;
    private static final String CHANNEL = "update";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private DownloadCallback callback;

    //定义个更新速率，避免更新通知栏过于频繁导致卡顿
    private float rate = .0f;

    private String versionName;
    final String CHANNEL_ID = "king_live";
    final String CHANNEL_NAME = "king_live_name";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
        mNotificationManager = null;
        mBuilder = null;
    }


    /**
     * 销毁时清空一下对notify对象的持有
     */
    @Override
    public void onDestroy() {
        mNotificationManager = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.START_UPDATA_APK) {
            //开始下载apk
            versionName = event.getVersionName();
            downApk(event.getUrl(), event.getDownloadCallback());
        } else if (msgId == MessageEvent.DOWN_ANIMATION) {
            //开始下载apk
            downAnimation(event.getUrl(), event.getFileName(), event.getDownloadCallback());
        }
    }

    /**
     * 创建通知栏
     */
    private void setNotification() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this, CHANNEL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID)
                    .setContentTitle("正在连接服务器")
                    .setContentText("开始下载")
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.icon_logo_r);
        } else {
            mBuilder.setContentTitle("开始下载")
                    .setContentText("正在连接服务器")
                    .setSmallIcon(R.mipmap.icon_logo_r)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo_r))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis());
        }
        if (mNotificationManager != null)
            mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    /**
     * 下载完成
     */
    private void complete(String msg) {
        if (mBuilder != null) {
            mBuilder.setContentTitle("新版本").setContentText(msg);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            if (mNotificationManager != null)
                mNotificationManager.notify(NOTIFY_ID, notification);
        }
        stopSelf();
    }

    /**
     * 开始下载apk
     */
    public void downApk(String url, DownloadCallback callback) {
        this.callback = callback;
        if (TextUtils.isEmpty(url)) {
            complete("下载路径错误");
            return;
        }
        setNotification();
        handler.sendEmptyMessage(0);
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = "下载错误";
                    handler.sendMessage(message);
                    return;
                }
                InputStream is = null;
                byte[] buff = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = FileUtil.createAPKFile(DownloadService.this, versionName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (rate != progress) {
                            Message message = Message.obtain();
                            message.what = 2;
                            message.obj = progress;
                            handler.sendMessage(message);
                            rate = progress;
                        }
                    }
                    fos.flush();
                    Message message = Message.obtain();
                    message.what = 3;
                    message.obj = file;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 开始下载动画
     */
    public void downAnimation(String url, final String fileName, final DownloadCallback callback) {
        this.callback = callback;
        if (TextUtils.isEmpty(url)) {
            complete("下载路径错误");
            return;
        }
        String root =FileUtil.getAnimationLoc(this);
        File file = new File(root,fileName+".json" );
        if (file.exists() && callback != null) {
            LogUtil.Log("动画文件已存在");
            callback.onComplete(file);
            return;
        }
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            File file = FileUtil.createAnimationFile(DownloadService.this, fileName);
            @Override
            public void onFailure(Call call, IOException e) {
                file.delete();
                if (callback != null)
                    callback.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    if (callback != null)
                        callback.onFail("下载错误");
                    return;
                }
                InputStream is = null;
                byte[] buff = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (rate != progress) {
                            rate = progress;
                            if (callback != null)
                                callback.onProgress(progress);
                        }
                    }
                    fos.flush();
                    if (callback != null)
                        callback.onComplete(file);
                } catch (Exception e) {
                    file.delete();
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 把处理结果放回ui线程
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (callback != null)
                        callback.onPrepare();
                    break;

                case 1:
                    if (mNotificationManager != null)
                        mNotificationManager.cancel(NOTIFY_ID);
                    if (callback != null)
                        callback.onFail((String) msg.obj);
                    stopSelf();
                    break;

                case 2: {
                    int progress = (int) msg.obj;
                    if (callback != null)
                        callback.onProgress(progress);
                    mBuilder.setContentTitle("正在下载：新版本...")
                            .setContentText(String.format(Locale.CHINESE, "%d%%", progress))
                            .setProgress(100, progress, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    if (mNotificationManager != null)
                        mNotificationManager.notify(NOTIFY_ID, notification);
                }
                break;

                case 3: {
                    if (callback != null)
                        callback.onComplete((File) msg.obj);
                    //app运行在界面,直接安装
                    //否则运行在后台则通知形式告知完成
                    if (mNotificationManager != null)
                        mNotificationManager.cancel(NOTIFY_ID);
                    UtilTool.install(DownloadService.this, (File) msg.obj);
                }
                break;
            }
            return false;
        }
    });


    /**
     * 定义一下回调方法
     */
    public interface DownloadCallback {
        void onPrepare();

        void onProgress(int progress);

        void onComplete(File file);

        void onFail(String msg);
    }
}
