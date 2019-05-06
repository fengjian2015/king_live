package com.wewin.live.listener.live;

/**
 * @author jsaon
 * @date 2019/3/2
 */
public interface LiveListener {
    //是否显示加载框
    void showOrHideProgressBar(boolean isShow);
    //设置当前加载比例
    void setLoadProgress(int proportion);
    //初始化进度条
    void initSeekBar(long duration);
    //视频播放报错给予提示
    void setError(String content);
    //设置音量
    void setVolume(int progress);
    //播放
    void start();
    //暂停
    void pause();
    //停止播放
    void stop();
    //继续播放
    void resume();
}
