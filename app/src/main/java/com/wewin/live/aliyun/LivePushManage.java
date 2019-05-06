package com.wewin.live.aliyun;

import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alivc.component.custom.AlivcLivePushCustomDetect;
import com.alivc.component.custom.AlivcLivePushCustomFilter;
import com.alivc.live.detect.TaoFaceFilter;
import com.alivc.live.filter.TaoBeautyFilter;
import com.alivc.live.pusher.AlivcLivePushBGMListener;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushNetworkListener;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.SurfaceStatus;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.example.jasonutil.util.LogUtil;

/**
 * @author jsaon
 * @date 2019/3/15
 * 推流管理
 */
public class LivePushManage extends LivePushControl{

    //人脸
    private TaoFaceFilter taoFaceFilter;
    //美颜
    private TaoBeautyFilter taoBeautyFilter;

    private  SurfaceView surfaceView;
    private Activity context;

    private SurfaceStatus mSurfaceStatus = SurfaceStatus.UNINITED;

    public LivePushManage(Activity context, SurfaceView surfaceView){
        super(context);
        this.surfaceView=surfaceView;
        this.context=context;
        initSurfaceView();
    }

    private void initSurfaceView(){
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                LogUtil.Log("jason","创建成功");
                if(mSurfaceStatus == SurfaceStatus.UNINITED) {
                    mSurfaceStatus = SurfaceStatus.CREATED;
                    preview(context, surfaceView);
                }else if(mSurfaceStatus == SurfaceStatus.DESTROYED) {
                    mSurfaceStatus = SurfaceStatus.RECREATED;
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if(mSurfaceStatus == SurfaceStatus.RECREATED){
                    setMute(false);
                    resumeAsync();
                }
                mSurfaceStatus = SurfaceStatus.CHANGED;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mSurfaceStatus = SurfaceStatus.DESTROYED;
                setMute(true);
                LogUtil.Log("jason","销毁");
                pause();
            }
        });
    }

    @Override
    protected void setListener(){
        setBeautyFace();
        //推流相关信息回调接口
        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPreviewStarted:开始预览");
            }

            @Override
            public void onPreviewStoped(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPreviewStoped:停止预览");
            }

            @Override
            public void onPushStarted(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPushStarted:开始推流");
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onPushStarted();
                ToastShow.showToast(context,context.getString(R.string.push_start_push));
            }

            @Override
            public void onFirstAVFramePushed(AlivcLivePusher alivcLivePusher) {
            }

            @Override
            public void onPushPauesed(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPushPauesed:暂停");
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onPushPauesed();
            }

            @Override
            public void onPushResumed(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPushResumed:恢复");
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onPushResumed();
            }

            @Override
            public void onPushStoped(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPushStoped:停止推流");
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onPushStoped();
            }

            /**
             * 推流重启通知
             *
             * @param alivcLivePusher AlivcLivePusher实例
             */
            @Override
            public void onPushRestarted(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPushRestarted:重启成功");
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onPushRestarted();
                ToastShow.showToast(context,context.getString(R.string.push_restarted_push));
            }

            @Override
            public void onFirstFramePreviewed(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onFirstFramePreviewed:首帧渲染");
            }

            @Override
            public void onDropFrame(AlivcLivePusher alivcLivePusher, int countBef, int countAft) {
                LogUtil.Log("onDropFrame:丢帧, 丢帧前："+countBef+", 丢帧后："+countAft);
            }

            @Override
            public void onAdjustBitRate(AlivcLivePusher alivcLivePusher, int curBr, int targetBr) {
//                LogUtil.Log("onAdjustBitRate:调整码率, 当前码率："+curBr+"Kps, 目标码率："+targetBr+"Kps");
            }

            @Override
            public void onAdjustFps(AlivcLivePusher alivcLivePusher, int curFps, int targetFps) {
//                LogUtil.Log("onAdjustFps:调整帧率, 当前帧率："+curFps+", 目标帧率："+targetFps);
            }
        });
        //推流网络回调接口
        mAlivcLivePusher.setLivePushNetworkListener(new AlivcLivePushNetworkListener() {
            @Override
            public void onNetworkPoor(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onNetworkPoor:网络差，请退出或者重连");
            }

            @Override
            public void onNetworkRecovery(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onNetworkRecovery:网络恢复");
            }

            @Override
            public void onReconnectStart(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onReconnectStart:重连开始");
            }

            @Override
            public void onConnectionLost(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onConnectionLost:推流已断开");
            }

            @Override
            public void onReconnectFail(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onReconnectFail:重连失败");
            }

            @Override
            public void onReconnectSucceed(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onReconnectSucceed:重连成功");
            }

            @Override
            public void onSendDataTimeout(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onSendDataTimeout:发送数据超时");
            }

            @Override
            public void onConnectFail(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onConnectFail:连接失败");
            }

            @Override
            public String onPushURLAuthenticationOverdue(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("流即将过期，请更换url");
                ToastShow.showToast(context,"流即将过期，请更换url");
                return null;
            }

            @Override
            public void onSendMessage(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("alivcLivePusher:"+alivcLivePusher.toString());
            }

            @Override
            public void onPacketsLost(AlivcLivePusher alivcLivePusher) {
                LogUtil.Log("onPacketsLost:推流丢包通知");
            }
        });

        //背景音乐回调接口
        mAlivcLivePusher.setLivePushBGMListener(new AlivcLivePushBGMListener() {
            @Override
            public void onStarted() {
                LogUtil.Log("开始播放音乐");
            }

            @Override
            public void onStoped() {
                LogUtil.Log("停止播放音乐");
            }

            @Override
            public void onPaused() {
                LogUtil.Log("暂停播放音乐");
            }

            @Override
            public void onResumed() {
                LogUtil.Log("恢复播放音乐");
            }

            @Override
            public void onProgress(long progress, long duration) {
//                LogUtil.Log("播放进度   progress："+progress+"   duration:"+duration);
            }

            @Override
            public void onCompleted() {
                LogUtil.Log("播放结束");
            }

            @Override
            public void onDownloadTimeout() {
                LogUtil.Log("播放超时");
            }

            @Override
            public void onOpenFailed() {
                LogUtil.Log("播放失败");
            }
        });
        //推流报错回调接口
        mAlivcLivePusher.setLivePushErrorListener(new AlivcLivePushErrorListener() {
            @Override
            public void onSystemError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                //当出现onSystemError系统级错误时，您需要退出直播。
                LogUtil.Log("报错onSystemError:"+alivcLivePusher.toString()+"     "+alivcLivePushError.toString());
                ToastShow.showToast(context,context.getString(R.string.push_system_error));
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onSystemError();
            }

            @Override
            public void onSDKError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                //当出现onSDKError错误（SDK错误）时，有两种处理方式，选择其一即可：销毁当前直播重新创建、调用restartPush/restartPushAsync重启AlivcLivePusher。
                LogUtil.Log("报错onSDKError:"+alivcLivePusher.toString()+"     "+alivcLivePushError.toString());
                if(mOnLicePushListener!=null)
                    mOnLicePushListener.onSDKError();
                restartPush();
                ToastShow.showToast(context,context.getString(R.string.push_sdk_error));
            }
        });
    }


    /**
     * 美颜和人脸检测
     */
    public void setBeautyFace(){
        if(mAlivcLivePusher==null)return;
        //人脸识别回调（只接入标准版美颜可不需要调用此接口）
        mAlivcLivePusher.setCustomDetect(new AlivcLivePushCustomDetect()
        {
            @Override
            public void customDetectCreate() {
                taoFaceFilter = new TaoFaceFilter(mContext);
                taoFaceFilter.customDetectCreate();
            }
            @Override
            public long customDetectProcess(long data, int width, int height, int rotation, int format, long extra) {

                if(taoFaceFilter != null) {
                    return taoFaceFilter.customDetectProcess(data, width, height, rotation, format, extra);
                }
                return 0;
            }
            @Override
            public void customDetectDestroy() {
                if(taoFaceFilter != null) {
                    taoFaceFilter.customDetectDestroy();
                }
            }
        });


        //美颜回调
        mAlivcLivePusher.setCustomFilter(new AlivcLivePushCustomFilter() {
            @Override
            public void customFilterCreate() {
                taoBeautyFilter = new TaoBeautyFilter();
                taoBeautyFilter.customFilterCreate();
            }
            @Override
            public void customFilterUpdateParam(float fSkinSmooth, float fWhiten, float fWholeFacePink, float fThinFaceHorizontal, float fCheekPink, float fShortenFaceVertical, float fBigEye) {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter.customFilterUpdateParam(fSkinSmooth, fWhiten, fWholeFacePink, fThinFaceHorizontal, fCheekPink, fShortenFaceVertical, fBigEye);
                }
            }
            @Override
            public void customFilterSwitch(boolean on)
            {
                if(taoBeautyFilter != null) {
                    taoBeautyFilter.customFilterSwitch(on);
                }
            }
            @Override
            public int customFilterProcess(int inputTexture, int textureWidth, int textureHeight, long extra) {
                if (taoBeautyFilter != null) {
                    return taoBeautyFilter.customFilterProcess(inputTexture, textureWidth, textureHeight, extra);
                }
                return inputTexture;
            }
            @Override
            public void customFilterDestroy() {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter.customFilterDestroy();
                }
                taoBeautyFilter = null;
            }
        });

    }


}
