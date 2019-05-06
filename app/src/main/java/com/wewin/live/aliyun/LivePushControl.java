package com.wewin.live.aliyun;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceView;
import com.alivc.live.pusher.AlivcBeautyLevelEnum;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePusher;
import com.example.jasonutil.util.FileUtil;
import com.example.jasonutil.util.LogUtil;

/**
 * @author jsaon
 * @date 2019/3/15
 * 直播控制类
 */
public abstract class LivePushControl {
    protected Context mContext;
    //推流初始配置
    protected final AlivcLivePushConfig mAlivcLivePushConfig;
    //推流功能类
    protected AlivcLivePusher mAlivcLivePusher;
    //是否异步
    protected boolean isAsync = false;

    //推流地址
    protected String pushUrl = "";

    //美颜参数五个固定档次,阿里配置参考  默认3档
    protected static int[] beautyInt = new int[]{0, 0, 0, 0, 0, 0, 0};
    protected static final int[] beautyIntOne = new int[]{35, 40, 10, 0, 0, 0, 0};
    protected static final int[] beautyIntTwo = new int[]{80, 60, 20, 0, 0, 0, 0};
    protected static final int[] beautyIntThree = new int[]{100, 50, 20, 0, 0, 0, 0};
    protected static final int[] beautyIntFour = new int[]{70, 40, 40, 30, 40, 50, 15};
    protected static final int[] beautyIntFive = new int[]{100, 70, 10, 30, 40, 50, 0};

    //自定义回调监听
    protected OnLicePushListener mOnLicePushListener;

    public LivePushControl(Context context) {
        mContext = context;
        mAlivcLivePushConfig = new AlivcLivePushConfig();
        initAlivcLivePusher(mContext);
    }

    public void setPushUrl(String url) {
        pushUrl = url;
    }

    /**
     * 初始化直播功能类添加监听
     *
     * @param context
     */
    public void initAlivcLivePusher(Context context) {
        mAlivcLivePusher = new AlivcLivePusher();
        //设置离开提示图片和网络错误提示图片
        FileUtil.copyAssetsSingleFile(context, "background_push.png");
        FileUtil.copyAssetsSingleFile(context, "logo.png");
        FileUtil.copyAssetsSingleFile(context, "poor_network.png");
        FileUtil.copyAssetsSingleFile(context, "test.mp3");
        //设置后台图片
        mAlivcLivePushConfig.setPausePushImage(FileUtil.getAssetsDirString(context) + "background_push.png");
        //配置网络不好时推的图片
        mAlivcLivePushConfig.setNetworkPoorPushImage(FileUtil.getAssetsDirString(context) + "poor_network.png");
        //配置水印
        mAlivcLivePushConfig.addWaterMark(FileUtil.getAssetsDirString(context) + "logo.png",0.7f,0.9f,0.3f);
        try {
            mAlivcLivePusher.init(context, mAlivcLivePushConfig);
            setListener();
            initBeanty();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    protected abstract void setListener();


    /**
     * 初始化美颜配置，也可以单独设置
     */
    private void initBeanty() {
        beautyInt = beautyIntFour;
        mAlivcLivePushConfig.setBeautyOn(true);// 开启美颜
        mAlivcLivePushConfig.setBeautyLevel(AlivcBeautyLevelEnum.BEAUTY_Professional);//设定为高级美颜.
        mAlivcLivePushConfig.setBeautyWhite(beautyInt[0]);// 美白范围0-100
        mAlivcLivePushConfig.setBeautyBuffing(beautyInt[1]);// 磨皮范围0-100
        mAlivcLivePushConfig.setBeautyRuddy(beautyInt[2]);// 红润设置范围0-100
        mAlivcLivePushConfig.setBeautyBigEye(beautyInt[3]);// 大眼设置范围0-100
        mAlivcLivePushConfig.setBeautyThinFace(beautyInt[4]);// 瘦脸设置范围0-100
        mAlivcLivePushConfig.setBeautyShortenFace(beautyInt[5]);// 收下巴设置范围0-100
        mAlivcLivePushConfig.setBeautyCheekPink(beautyInt[6]);// 腮红设置范围0-100
    }

    /**
     * 预览
     */
    public void preview(Activity activity, SurfaceView surfaceView) {
        try {
            if (mAlivcLivePusher == null) return;
            if (isAsync) {
                mAlivcLivePusher.startPreviewAysnc(surfaceView);
            } else {
                mAlivcLivePusher.startPreview(surfaceView);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.stopPreview();
    }

    /**
     * 开始推流
     *
     * @param url 推流测试地址(rtmp://......)
     */
    public void push(String url) {
        try {
            if (mAlivcLivePusher == null) return;
            mAlivcLivePusher.startPushAysnc(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 停止推流
     */
    public void stopPush() {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.stopPush();
    }

    /**
     * 重新推流
     */
    public void restartPush() {
        try {
            if (mAlivcLivePusher == null) return;
            mAlivcLivePusher.restartPushAync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 是否推流
     *
     * @return
     */
    public boolean isPushing() {
        try {
            if (mAlivcLivePusher == null) return false;
            return mAlivcLivePusher.isPushing();
        }catch (Exception e){
            e.printStackTrace();
        }
     return false;
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.pause();
    }

    /**
     * 恢复
     */
    public void resume() {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.resume();
    }

    /**
     * 恢复
     */
    public void resumeAsync() {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.resumeAsync();
    }


    /**
     * 切换相机
     */
    public void switchCamera() {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.switchCamera();
    }

    /**
     * 设置手电筒
     *
     * @param isFlash true open
     */
    public void setFlash(boolean isFlash) {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.setFlash(isFlash);
    }

    /**
     * 是否自动对焦
     *
     * @param isAutoFocus
     */
    public void setAutoFocus(boolean isAutoFocus) {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.setAutoFocus(isAutoFocus);
    }

    /**
     * 是否静音
     *
     * @param isMute
     */
    public void setMute(boolean isMute) {
        if (mAlivcLivePusher == null) return;
        mAlivcLivePusher.setMute(isMute);
    }

    /**
     * 开始背景音乐
     *
     * @param music
     */
    public void startBGMAsync(String music) {
        if (mAlivcLivePusher == null) return;
        try {
            mAlivcLivePusher.startBGMAsync(music);
        } catch (Exception e) {
            LogUtil.Log(e.getMessage());
        }
    }

    /**
     * 停止播放
     */
    public void stopBGMAsync() {
        if (mAlivcLivePusher == null) return;
        try {
            mAlivcLivePusher.stopBGMAsync();
        } catch (Exception e) {
            LogUtil.Log(e.getMessage());
        }
    }

    /**
     * 暂停音乐
     */
    public void pauseBGM() {
        if (mAlivcLivePusher == null) return;
        try {
            mAlivcLivePusher.pauseBGM();
        } catch (Exception e) {
            LogUtil.Log(e.getMessage());
        }
    }

    /**
     * 恢复音乐
     */
    public void resumeBGM() {
        if (mAlivcLivePusher == null) return;
        try {
            mAlivcLivePusher.resumeBGM();
        } catch (Exception e) {
            LogUtil.Log(e.getMessage());
        }
    }

    /**
     * 设置伴奏音量
     * @param number
     */
    public void setBGMVolume(int number){
        if(mAlivcLivePusher==null)return;
        try {
            mAlivcLivePusher.setBGMVolume(number);
        } catch (Exception e) {
            LogUtil.Log(e.getMessage());
        }
    }

    /**
     * 设置人声音量
     * @param number
     */
    public void setCaptureVolume(int number){
        if(mAlivcLivePusher==null)return;
        try {
            mAlivcLivePusher.setCaptureVolume(number);
        } catch (Exception e) {
            LogUtil.Log(e.getMessage());
        }
    }


    /**
     * 获取美颜参数
     *
     * @return
     */
    public int[] getBeautyInt() {
        return beautyInt;
    }

    /**
     * 设置美颜参数
     *
     * @return
     */
    public void setBeautyInt(int[] beautyInt) {
        this.beautyInt = beautyInt;
    }

    /**
     * 美白范围
     *
     * @param number
     */
    public void setBeautyWhite(int number) {
        mAlivcLivePusher.setBeautyWhite(number);// 美白范围0-100
    }


    /**
     * 磨皮范围
     *
     * @param number
     */
    public void setBeautyBuffing(int number) {
        mAlivcLivePusher.setBeautyBuffing(number);// 磨皮范围0-100
    }

    /**
     * 红润范围
     *
     * @param number
     */
    public void setBeautyRuddy(int number) {
        mAlivcLivePusher.setBeautyRuddy(number);// 红润设置范围0-100
    }

    /**
     * 大眼范围
     *
     * @param number
     */
    public void setBeautyBigEye(int number) {
        mAlivcLivePusher.setBeautyBigEye(number);// 大眼设置范围0-100
    }

    /**
     * 瘦脸范围
     *
     * @param number
     */
    public void setBeautyThinFace(int number) {
        mAlivcLivePusher.setBeautySlimFace(number);// 瘦脸设置范围0-100
    }


    /**
     * 收下巴范围
     *
     * @param number
     */
    public void setBeautyShortenFace(int number) {
        mAlivcLivePusher.setBeautyShortenFace(number);// 收下巴设置范围0-100
    }

    /**
     * 腮红范围
     *
     * @param number
     */
    public void setBeautyCheekPink(int number) {
        mAlivcLivePusher.setBeautyCheekPink(number);// 腮红设置范围0-100
    }

    /**
     * 设置美颜所有参数
     *
     * @param ints
     */
    public void setBeautyAllConfig(int[] ints) {
        beautyInt = ints;
        mAlivcLivePusher.setBeautyWhite(ints[0]);// 美白范围0-100
        mAlivcLivePusher.setBeautyBuffing(ints[1]);// 磨皮范围0-100
        mAlivcLivePusher.setBeautyRuddy(ints[2]);// 红润设置范围0-100
        mAlivcLivePusher.setBeautyBigEye(ints[3]);// 大眼设置范围0-100
        mAlivcLivePusher.setBeautySlimFace(ints[4]);// 瘦脸设置范围0-100
        mAlivcLivePusher.setBeautyShortenFace(ints[5]);// 收下巴设置范围0-100
        mAlivcLivePusher.setBeautyCheekPink(ints[6]);// 腮红设置范围0-100
    }

    /**
     * 销毁
     * 正常推流模式下
     */
    public void release() {
        if (mAlivcLivePusher != null) {
            //停止推流
            try {
                mAlivcLivePusher.stopPush();
            } catch (Exception e) {
            }

            //停止预览
            try {
                mAlivcLivePusher.stopPreview();
            } catch (Exception e) {
            }

            //释放推流
            mAlivcLivePusher.destroy();
            mAlivcLivePusher.setLivePushInfoListener(null);
            mAlivcLivePusher = null;
        }
    }


    /**
     * 设置监听
     * @param mOnLicePushListener
     */
    public void setOnLicePushListener(OnLicePushListener mOnLicePushListener){
        this.mOnLicePushListener=mOnLicePushListener;
    }

    /**
     * 自定义接口回调，挑选需要的回调返回
     */
    public interface OnLicePushListener{
        //开始推流
        void onPushStarted();
        //停止推流
        void onPushStoped();
        //暂停推流
        void onPushPauesed();
        //恢复推流
        void onPushResumed();
        //系统错误
        void onSystemError();
        //sdk错误
        void onSDKError();
        //重启成功
        void onPushRestarted();
    }

}
