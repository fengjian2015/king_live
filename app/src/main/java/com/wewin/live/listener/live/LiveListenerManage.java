package com.wewin.live.listener.live;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/3/4
 * 播放器监听
 */
public class LiveListenerManage {
    public static LiveListenerManage mInstance;
    private List<LiveListener> listeners;// 设置状态改变的监听
    private static Object lock = new Object();

    private LiveListenerManage() {
        listeners = new ArrayList<LiveListener>();
    }

    public static LiveListenerManage getInstance() {
        if(mInstance==null){
            mInstance=new LiveListenerManage();
        }
        return mInstance;
    }

    public void registerLiveListener(LiveListener listener) {
        synchronized (listeners) {
            if(!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void unregisterLiveListener(LiveListener listener) {
        if(listener==null) {
            return;
        }
        synchronized (listeners) {
            if(listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    public void showOrHideProgressBar(boolean isShow) {
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.showOrHideProgressBar(isShow);
            }
        }
    }

    public void setLoadProgress(int proportion) {
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.setLoadProgress(proportion);
            }
        }
    }

    public void initSeekBar(long duration) {
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.initSeekBar(duration);
            }
        }
    }

    public void setError(String content){
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.setError(content);
            }
        }
    }

    public void setVolume(int progress){
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.setVolume(progress);
            }
        }
    }

    public void setStart(){
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.start();
            }
        }
    }


    public void pause(){
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.pause();
            }
        }
    }

    public void stop(){
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.stop();
            }
        }
    }

    public void resume(){
        synchronized (lock) {
            for (LiveListener lis : listeners) {
                lis.resume();
            }
        }
    }
}
