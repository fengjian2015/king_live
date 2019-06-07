package com.wewin.live.aliyun;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.example.jasonutil.dialog.ConfirmCancelDialog;
import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.util.ActivityUtil;
import com.wewin.live.R;
import com.wewin.live.listener.live.LiveListenerManage;
import com.example.jasonutil.util.LogUtil;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;

/**
 * @author jsaon
 * @date 2019/3/2
 * 普通播放器
 */
public class LiveManage {
    public static LiveManage instance = null;
    //功能管理器
    private AliVcMediaPlayer mPlayer;
    //视图
    private SurfaceView mSurfaceView;
    private Context mContext;
    //视频或直播地址
    private String videoUrl;

    private int network=0;//判断当前网络是否能播放或者当前有没有网络
    public static final int WIFI=1;
    public static final int MOBILE=2;
    public static final int NET_DISCONNECTED=3;
    //弹窗提醒
    private ConfirmDialog mConfirmDialog;
    private ConfirmCancelDialog mConfirmCancelDialog;
    //是否播放，无网络暂停时无效
    private boolean isPlay=true;
    //记录失去视图后的位置，方便下次直接加载当前位置
    private long oldCurrentPosition;
    //跑马灯文本记录
    private String marqueeContent;
    //右上角提示文本记录
    private String rightPrompt;
    //右上角图标
    private String rightImage;
    //记录播放器界面的数据
    private Bundle mBundle;

    public static LiveManage getInstance() {
        if (instance == null) {
            instance = new LiveManage();
        }
        return instance;
    }

    /**
     * 初始化
     * @param context
     * @param SurfaceView
     * @param url
     */
    public void setLiveSurfce(Context context, SurfaceView surfaceView, String url) {
        if(ActivityUtil.isActivityOnTop(mContext)&&mContext instanceof Activity){
            ((Activity) mContext).finish();
        }
        //关闭释放之前界面
        if(mPlayer!=null){
            release();
        }
        isPlay=true;
        this.mSurfaceView = surfaceView;
        this.mContext = context;
        this.videoUrl = url;
        setDisplay();
    }

    /**
     * 初始化播放控制器
     */
    private void initPlayer() {
        mPlayer = new AliVcMediaPlayer(mContext,mSurfaceView);
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        //设置缺省编码类型：0表示硬解；1表示软解；
        //如果缺省为硬解，在使用硬解时如果解码失败，会尝试使用软解
        //如果缺省为软解，则一直使用软解，软解较为耗电，建议移动设备尽量使用硬解
        if(MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_Y)) {
            mPlayer.setDefaultDecoder(0);
        }
        //MediaType.live 表示直播；MediaType.Vod 表示点播
//        mPlayer.setMediaType(MediaPlayer.MediaType.live);
        //如果从历史点开始播放
        //mPlayer.seekTo(position);
        //准备开始播放
        //开启循环播放
        mPlayer.setCirclePlay(true);
        setListener();
    }

    private void initConfiguration() {
        if (mPlayer==null) {
            return;
        }
        LiveListenerManage.getInstance().initSeekBar((long) mPlayer.getDuration());
        mPlayer.setCirclePlay(true);
        LiveListenerManage.getInstance().setVolume(getVolume());
    }

    /**
     * 改变视图
     * @param surfaceView
     */
    public void changeSufaceView(SurfaceView surfaceView){
        if(mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(mCallback);
        }
        mSurfaceView=surfaceView;
        mSurfaceView.getHolder().addCallback(mCallback);
    }

    /**
     * 返回链接
     * @return
     */
    public String getUrl(){
        return videoUrl;
    }

    /**
     * 开始
     * 准备结束后自动开始
     */
    public void startPlay() {
        if (mPlayer == null) {
            initPlayer();
            mPlayer.prepareAndPlay(videoUrl);
            return;
        }else{
            initConfiguration();
            mPlayer.setVideoSurface(mSurfaceView.getHolder().getSurface());
            mPlayer.setSurfaceChanged();
//            if(isPlay!=true){
//                LiveListenerManage.getInstance().pause();
//                LiveListenerManage.getInstance().resume();
//                return;
//            }
            LiveListenerManage.getInstance().setStart();
        }
    }

    /**
     * 开始播放
     */
    public void start(){
        LogUtil.log("开始播放");
        if (mPlayer==null) {
            return;
        }
        isPlay=true;
        mPlayer.play();
    }

    /**
     * 暂停
     */
    public void pause(){
        LogUtil.log("暂停");
        if(mPlayer==null) {
            return;
        }
        isPlay=false;
        mPlayer.pause();
    }

    /**
     * 停止
     */
    public void stop(){
        LogUtil.log("停止");
        if(mPlayer==null) {
            return;
        }
        isPlay=false;
        mPlayer.stop();
    }

    /**
     * 继续播放
     */
    public void resume(){
        LogUtil.log("继续播放");
        if(mPlayer==null) {
            return;
        }
        isPlay=true;
        mPlayer.resume();
    }

    /**
     * 销毁
     */
    public void release(){
        if(mPlayer==null) {
            return;
        }
        if(mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(mCallback);
        }
        mPlayer.destroy();
        mPlayer=null;
        videoUrl=null;
        LogUtil.log("销毁播放管理类");
    }


    public void setDisplay(){
        mSurfaceView.getHolder().addCallback(mCallback);
    }

    /**
     * 重置播放器。当播放的过程中调用该函数，会先停止当前的播放行为，销毁当前的播放器，然后创建一个新的播放器。
     */
    public void reset(){
        if(mPlayer==null) {
            return;
        }
        mPlayer.reset();
    }

    /**
     * 获取是否正在播放
     * @return
     */
    public boolean getIsPlaying(){
        if(mPlayer==null) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    /**
     * 获取当前播放状态
     * @return 用与强制控制是否播放
     */
    public boolean getPlay(){
        return isPlay;
    }

    /**
     * 获取当前播放位置
     * @return
     */
    public long getCurrentPosition(){
        if(mPlayer!=null){
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取当前缓冲进度
     * @return
     */
    public long getBufferingPosition(){
        if(mPlayer==null) {
            return 0;
        }
        return mPlayer.getBufferPosition();
    }

    /**
     * 获取视频总时长
     * @return
     */
    public long getDuration(){
        if(mPlayer!=null){
            return mPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 进度选择
     * @param i
     */
    public void setSeek(int i){
        if(mPlayer!=null){
            mPlayer.seekTo(i);
        }
    }

    /**
     * 设置播放器音量
     * 音量大小，范围为 0-100，100 为最大，0 为最小。
     */
    public void setVolume(int volume){
        if(mPlayer==null) {
            return;
        }
        mPlayer.setVolume(volume);
    }

    /**
     * 获取当前音量
     */
    public int getVolume(){
        if(mPlayer==null) {
            return 0;
        }
        return mPlayer.getVolume();
    }

    /**
     * 设置亮度
     * @param brightness
     */
    public void setScreenBrightness(int brightness){
        if (mPlayer==null) {
            return;
        }
        mPlayer.setScreenBrightness(brightness);
    }

    /**
     * 获取当前亮度
     * @return
     */
    public int getScreenBrightness(){
        if (mPlayer==null) {
            return 0;
        }
        return mPlayer.getScreenBrightness();
    }





    /**
     * 单位毫秒
     * 当播放器超过设定时间没有下载到任何数据，会发送。ALIVC_ERR_LOADING_TIMEOUT 错误事件。系统默认 timeout 时间为 15000 毫秒。
     * @param timeout
     */
    public void setNetworkTimeout(int timeout){
        if(mPlayer==null) {
            return;
        }
        mPlayer.setTimeout(timeout);
    }

    /**
     * 设置网络目前状态
     * @param network
     */
    public void setIsNetwork(int network){
        this.network=network;
    }

    /**
     * 返回网络状态
     * @return
     */
    public int getIsNetwork(){
        return network;
    }

    /**
     * 判断当前网络能否播放
     * @return true正常
     */
    public boolean getAllowPlay(){
        if(network==WIFI){
            //无线网
            return true;
        }else if(network==MOBILE){
            //手机网络
            if(MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_FOUR_G)){
                return true;
            }else{
                return false;
            }
        }else {
            //无网络
            return false;
        }
    }

    /**
     * 获取右上角图标
     * @return
     */
    public String getRightImage() {
        return rightImage;
    }

    /**
     * 设置右上角图标
     * @param rightImage
     */
    public void setRightImage(String rightImage) {
        this.rightImage = rightImage;
    }

    /**
     * 设置跑马灯文本
     * @param content
     */
    public void setMarqueeContent(String content){
        marqueeContent=content;
    }

    /**
     * 设置右上角提示内容
     * @param content
     */
    public void setRightPrompt(String content){
        rightPrompt=content;
    }

    /**
     * 返回跑马灯文本
     */
    public String getMarqueeContent(){
        return marqueeContent;
    }

    /**
     * 返回右上角提示内容
     */
    public String getRightPrompt(){
        return rightPrompt;
    }

    /**
     * 设置播放器界面的参数
     * @param bundle
     */
    public void setBundle(Bundle bundle){
        mBundle=bundle;
    }

    /**
     * 返回播放器的数据
     * @return
     */
    public Bundle getBundle(){
        return mBundle;
    }

    /**
     * 当前状态4G不允许播放的情况
     */
    public void showfourG(){
        if(!ActivityUtil.isActivityOnTop(mContext)) {
            return;
        }
        if(getAllowPlay()) {
            return;
        }
        if(network!=MOBILE) {
            return;//不为移动网络的忽略
        }
        if(MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_FOUR_G)) {
            return;
        }
        if(mConfirmCancelDialog!=null&&mConfirmCancelDialog.isShowing()) {
            return;
        }
        LiveListenerManage.getInstance().pause();
        LiveListenerManage.getInstance().showOrHideProgressBar(false);
        mConfirmCancelDialog=new ConfirmCancelDialog(mContext);
        mConfirmCancelDialog.showDialog().setTvTitle(mContext.getString(R.string.four_g_help))
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
            @Override
            public void onClick() {
                MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_FOUR_G,true);
                LiveListenerManage.getInstance().setStart();
                LiveListenerManage.getInstance().showOrHideProgressBar(true);
            }

            @Override
            public void onCancel() {
                LiveListenerManage.getInstance().setError(mContext.getString(R.string.four_g_error));
            }
        });
    }

    /**
     * 当前没网络状态
     */
    public void notNetWork(){
        if(!ActivityUtil.isActivityOnTop(mContext)) {
            return;
        }
        if(getAllowPlay()) {
            return;
        }
        if(mConfirmDialog!=null&&mConfirmDialog.isShowing()) {
            return;
        }
        mConfirmDialog=new ConfirmDialog(mContext)
                .showDialog()
                .setTvTitle(mContext.getString(R.string.not_network));
    }

    /**
     * 获取上个位置的点
     * @return
     */
    public long getOldCurrentPosition(){
        return oldCurrentPosition;
    }

    SurfaceHolder.Callback mCallback=new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            LogUtil.log("surfaceCreated创建完成"+videoUrl);
            startPlay();

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            LogUtil.log("surfaceChanged改变");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogUtil.log("surfaceDestroyed销毁");
            oldCurrentPosition=getCurrentPosition();
            LiveListenerManage.getInstance().pause();
        }
    };

    private void setListener() {
        mPlayer.setInfoListener(new MediaPlayer.MediaPlayerInfoListener() {
            @Override
            public void onInfo(int i, int i1) {
                //MediaPlayer.MEDIA_INFO_BUFFERING_PROGRESS 加载中
                //MEDIA_INFO_BUFFERING_END   加载结束
                // MEDIA_INFO_BUFFERING_START 加载开始
                if (MediaPlayer.MEDIA_INFO_BUFFERING_START == i) {
                    notNetWork();
                    showfourG();
                    LiveListenerManage.getInstance().showOrHideProgressBar(true);
                } else if (MediaPlayer.MEDIA_INFO_BUFFERING_PROGRESS == i) {
                    if(!getAllowPlay()) {
                        return;
                    }
                    LiveListenerManage.getInstance().setLoadProgress(i1);
                } else if (MediaPlayer.MEDIA_INFO_BUFFERING_END == i) {
                    LiveListenerManage.getInstance().showOrHideProgressBar(false);
                    if(isPlay==false){
                        pause();
                    }
                }
            }
        });
        mPlayer.setPreparedListener(new MediaPlayer.MediaPlayerPreparedListener() {
            @Override
            public void onPrepared() {
                LogUtil.log("准备完成"+mPlayer.getDuration());
                initConfiguration();
            }
        });
//        mPlayer.setOnPcmDataListener(new IAliyunVodPlayer.OnPcmDataListener() {
//            @Override
//            public void onPcmData(byte[] bytes, int i) {
//                //                LogUtil.log("音频数据回调接口，在需要处理音频时使用，如拿到视频音频，然后绘制音柱");
//            }
//        });

        mPlayer.setFrameInfoListener(new MediaPlayer.MediaPlayerFrameInfoListener() {
            @Override
            public void onFrameInfoListener() {
                LogUtil.log("首帧显示时触发");
                LiveListenerManage.getInstance().showOrHideProgressBar(false);
            }
        });
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
                LogUtil.log("//错误发生时触发，错误码见接口文档  msg:" + msg + "   i:" + i);
                LiveListenerManage.getInstance().setError(msg);
            }
        });
        mPlayer.setCompletedListener(new MediaPlayer.MediaPlayerCompletedListener() {
            @Override
            public void onCompleted() {
                LogUtil.log("视频正常播放完成时触发");
            }
        });

        mPlayer.setSeekCompleteListener(new MediaPlayer.MediaPlayerSeekCompleteListener() {
            @Override
            public void onSeekCompleted() {
                LogUtil.log("视频seek完成时触发");
            }
        });
        mPlayer.setStoppedListener(new MediaPlayer.MediaPlayerStoppedListener() {
            @Override
            public void onStopped() {
                LogUtil.log("使用stop接口时触发");
            }
        });
        mPlayer.setCircleStartListener(new MediaPlayer.MediaPlayerCircleStartListener() {
            @Override
            public void onCircleStart() {
                LogUtil.log("循环播放开始");
            }
        });
    }
}
