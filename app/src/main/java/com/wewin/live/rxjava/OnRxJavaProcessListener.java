package com.wewin.live.rxjava;

import io.reactivex.ObservableEmitter;

/**
 * Created by Darren on 2019/4/13
 **/
public interface OnRxJavaProcessListener {

    void process(ObservableEmitter<Object> emitter);

}
