package com.wewin.live.aliyun;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alivc.player.MediaPlayer;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.example.jasonutil.dialog.ConfirmCancelDialog;
import com.example.jasonutil.dialog.ConfirmDialog;
import com.example.jasonutil.util.ActivityUtil;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.listener.live.LiveListenerManage;
import com.example.jasonutil.util.LogUtil;
import com.wewin.live.utils.MySharedConstants;
import com.example.jasonutil.util.MySharedPreferences;

/**
 * @author jsaon
 * @date 2019/3/2
 * 高级播放器
 */
public class LiveVodManage {
    public static LiveVodManage instance = null;
    //功能管理器
    private AliyunVodPlayer mPlayer;
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

    public static LiveVodManage getInstance() {
        if (instance == null) {
            instance = new LiveVodManage();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     * @param SurfaceView
     * @param url
     */
    public void setLiveSurfce(Context context, SurfaceView surfaceView, String url) {
        //关闭释放之前界面
        if(mPlayer!=null){
            release();
        }
        if(ActivityUtil.isActivityOnTop(mContext)&&mContext instanceof Activity){
            ((Activity) mContext).finish();
        }
        isPlay=true;
        this.mSurfaceView = surfaceView;
        this.mContext = context;
        this.videoUrl = url;
        mSurfaceView.getHolder().addCallback(mCallback);

    }

    /**
     * 初始化播放控制器
     */
    private void initPlayer() {
        mPlayer = new AliyunVodPlayer(mContext);
        setDisplay();
        //开启循环播放
        setListener();

    }

    private void initConfiguration() {
        LiveListenerManage.getInstance().initSeekBar(mPlayer.getDuration());
        mPlayer.setCirclePlay(true);
        setVolume(100);
        LiveListenerManage.getInstance().setVolume(getVolume());
        start();
    }


    /**
     * 改变视图,创建小窗口
     * 想要不被暂停，调用前必须先于mCallback回调销毁之前调用此方法
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
            AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            asb.setSource(videoUrl);
            AliyunLocalSource mLocalSource = asb.build();
            mPlayer.prepareAsync(mLocalSource);
        }else{
            initConfiguration();
            mPlayer.setSurface(mSurfaceView.getHolder().getSurface());
            if(isPlay!=true){
                LiveListenerManage.getInstance().pause();
                return;
            }
            start();
        }

        //设置缺省编码类型：0表示硬解；1表示软解；
        //如果缺省为硬解，在使用硬解时如果解码失败，会尝试使用软解
        //如果缺省为软解，则一直使用软解，软解较为耗电，建议移动设备尽量使用硬解
//        mPlayer.setDefaultDecoder(0);
        //MediaType.Live 表示直播；MediaType.Vod 表示点播
//        mPlayer.setMediaType(MediaPlayer.MediaType.Live);
        //如果从历史点开始播放
        //mPlayer.seekTo(position);
        //准备开始播放
//        mPlayer.prepareAndPlay(videoUrl);
    }

    /**
     * 开始播放
     */
    public void start(){
        if (mPlayer==null)return;
        isPlay=true;
        mPlayer.start();
    }

    /**
     * 暂停
     */
    public void pause(){
        if(mPlayer==null)return;
        isPlay=false;
        mPlayer.pause();
    }

    /**
     * 停止
     */
    public void stop(){
        LogUtil.Log("停止");
        if(mPlayer==null)return;
        isPlay=false;
        mPlayer.stop();
    }

    /**
     * 继续播放
     */
    public void resume(){
        if(mPlayer==null)return;
        isPlay=true;
        mPlayer.resume();
    }

    /**
     * 停止视频播放
     */
    public void stopPlay() {
        if (mPlayer.isPlaying()) {
            ToastShow.showToast2(mContext, "停止");
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        } else {
            ToastShow.showToast2(mContext, "从来没放过");
        }
    }

    /**
     * 释放播放器资源
     * 关闭记得释放
     */
    public void release(){
        if(mPlayer==null)return;
        if(mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(mCallback);
        }
        mPlayer.release();
        mPlayer=null;
        videoUrl=null;
    }

    /**
     * 设置视图
     * @param surfaceView
     */
    public void setSurface(SurfaceView surfaceView){
        if (mPlayer==null)return;
        this.mSurfaceView=surfaceView;
        mPlayer.setSurface(mSurfaceView.getHolder().getSurface());
    }

    /**
     * 设置holder
     */
    public void setDisplay(){
        mPlayer.setDisplay(mSurfaceView.getHolder());

        mPlayer.surfaceChanged();
    }



    /**
     * 重置播放器。当播放的过程中调用该函数，会先停止当前的播放行为，销毁当前的播放器，然后创建一个新的播放器。
     */
    public void reset(){
        if(mPlayer==null)return;
        mPlayer.reset();
    }

    /**
     * 获取是否正在播放
     * @return
     */
    public boolean getIsPlaying(){
        if(mPlayer==null)return false;
        if(IAliyunVodPlayer.PlayerState.Started==mPlayer.getPlayerState()){
            return true;
        }else {
            return false;
        }
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
        if(mPlayer==null)return 0;
        return mPlayer.getBufferingPosition();
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
        if(mPlayer==null)return;
        mPlayer.setVolume(volume);
    }

    /**
     * 获取当前音量
     */
    public int getVolume(){
        if(mPlayer==null)return 0;
        return mPlayer.getVolume();
    }


    /**
     * 单位毫秒
     * 当播放器超过设定时间没有下载到任何数据，会发送。ALIVC_ERR_LOADING_TIMEOUT 错误事件。系统默认 timeout 时间为 15000 毫秒。
     * @param timeout
     */
    public void setNetworkTimeout(int timeout){
        if(mPlayer==null)return;
        mPlayer.setNetworkTimeout(timeout);
    }


    /**
     * 设置网络目前状态
     * @param network
     */
    public void setIsNetwork(int network){
        this.network=network;
    }

    /**
     * 判断当前网络能否播放
     * @return
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
     * 当前状态4G不允许播放的情况
     */
    public void showfourG(){
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        if(getAllowPlay())return;
        if(network!=MOBILE)return;//不为移动网络的忽略
        if(!MySharedPreferences.getInstance().getBoolean(MySharedConstants.ON_OFF_FOUR_G))return;
        if(mConfirmCancelDialog!=null&&mConfirmCancelDialog.isShowing())return;
        LiveListenerManage.getInstance().pause();
        mConfirmCancelDialog=new ConfirmCancelDialog(mContext);
        mConfirmCancelDialog.showDialog().setTvTitle(mContext.getString(R.string.four_g_help))
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
            @Override
            public void onClick() {
                MySharedPreferences.getInstance().setBoolean(MySharedConstants.ON_OFF_FOUR_G,true);
                LiveListenerManage.getInstance().setStart();
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
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        if(getAllowPlay())return;
        if(mConfirmDialog!=null&&mConfirmDialog.isShowing())return;
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
            startPlay();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            oldCurrentPosition=getCurrentPosition();
            LiveListenerManage.getInstance().pause();
        }
    };

    private void setListener() {
        mPlayer.setOnInfoListener(new IAliyunVodPlayer.OnInfoListener() {
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
                    LiveListenerManage.getInstance().showOrHideProgressBar(true);
                    LiveListenerManage.getInstance().setLoadProgress(i1);
                } else if (MediaPlayer.MEDIA_INFO_BUFFERING_END == i) {
                    LiveListenerManage.getInstance().showOrHideProgressBar(false);
                    if(isPlay==false){
                        pause();
                    }
                }
            }
        });
        mPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                LogUtil.Log("准备完成"+mPlayer.getDuration());

                initConfiguration();
            }
        });
        mPlayer.setOnPcmDataListener(new IAliyunVodPlayer.OnPcmDataListener() {
            @Override
            public void onPcmData(byte[] bytes, int i) {
                //                LogUtil.Log("音频数据回调接口，在需要处理音频时使用，如拿到视频音频，然后绘制音柱");
            }
        });
        mPlayer.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {
                LogUtil.Log("首帧显示时触发");
                LiveListenerManage.getInstance().showOrHideProgressBar(false);
            }
        });
        mPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int i, int i1, String msg) {
                LogUtil.Log("//错误发生时触发，错误码见接口文档  msg:" + msg + "   i:" + i+"    i1:"+i1);
                LiveListenerManage.getInstance().setError(msg);
            }
        });
        mPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                LogUtil.Log("视频正常播放完成时触发");
            }
        });
        mPlayer.setOnSeekCompleteListener(new IAliyunVodPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                LogUtil.Log("视频seek完成时触发");
            }
        });
        mPlayer.setOnStoppedListner(new IAliyunVodPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                LogUtil.Log("使用stop接口时触发");
            }
        });
        mPlayer.setOnCircleStartListener(new IAliyunVodPlayer.OnCircleStartListener() {
            @Override
            public void onCircleStart() {
                LogUtil.Log("循环播放开始");
            }
        });
    }

}
