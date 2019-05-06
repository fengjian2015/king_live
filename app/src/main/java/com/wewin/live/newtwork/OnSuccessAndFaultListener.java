package com.wewin.live.newtwork;

/**
 * @author jsaon
 * @date 2019/3/5
 */
public interface OnSuccessAndFaultListener {
    void onFault(String content);
    void onSuccess(String content);
}
