package com.wewin.live.newtwork;

import android.content.Context;

import com.example.jasonutil.util.ActivityUtil;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.wewin.live.dialog.LoadingProgressDialog;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.UtilTool;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * @author jsaon
 * @date 2019/3/5
 * 成功错误回调
 */
public class OnSuccessAndFaultSub extends DisposableObserver<ResponseBody> implements LoadingProgressDialog.ProgressCancelListener {
    /**
     * 是否需要显示默认Loading
     */
    private boolean showProgress = true;
    private OnSuccessAndFaultListener mOnSuccessAndFaultListener;

    private Context context;
    private LoadingProgressDialog progressDialog;

    /**
     * @param mOnSuccessAndFaultListener 成功回调监听
     */
    public OnSuccessAndFaultSub(OnSuccessAndFaultListener mOnSuccessAndFaultListener) {
        this.mOnSuccessAndFaultListener = mOnSuccessAndFaultListener;
    }

    /**
     * @param mOnSuccessAndFaultListener 成功回调监听
     * @param context                    上下文
     */
    public OnSuccessAndFaultSub(OnSuccessAndFaultListener mOnSuccessAndFaultListener, Context context) {
        this.mOnSuccessAndFaultListener = mOnSuccessAndFaultListener;
        this.context = context;
        progressDialog = LoadingProgressDialog.createDialog(context);
        progressDialog.setProgressCancelListener(this);
    }

    /**
     * @param mOnSuccessAndFaultListener 成功回调监听
     * @param context                    上下文
     * @param showProgress               是否需要显示默认Loading
     */
    public OnSuccessAndFaultSub(OnSuccessAndFaultListener mOnSuccessAndFaultListener, Context context, boolean showProgress) {
        this.mOnSuccessAndFaultListener = mOnSuccessAndFaultListener;
        this.context = context;
        progressDialog = LoadingProgressDialog.createDialog(context);
        progressDialog.setProgressCancelListener(this);
        this.showProgress = showProgress;
    }

    private void showProgressDialog() {
        if (showProgress && null != progressDialog && ActivityUtil.isActivityOnTop(context)) {
            progressDialog.setMessage("Loding...");
            progressDialog.showDialog();
        }
    }

    private void dismissProgressDialog() {
        if (showProgress && null != progressDialog) {
            progressDialog.hideDialog();
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        dismissProgressDialog();
        progressDialog = null;
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     */
    @Override
    public void onError(Throwable e) {
        try {
            if (e instanceof SocketTimeoutException) {//请求超时
                mOnSuccessAndFaultListener.onFault("网络连接超时");
            } else if (e instanceof ConnectException) {//网络连接超时
                mOnSuccessAndFaultListener.onFault("网络连接超时");
            } else if (e instanceof SSLHandshakeException) {//安全证书异常
                mOnSuccessAndFaultListener.onFault("安全证书异常");
            } else if (e instanceof HttpException) {//请求的地址不存在
                int code = ((HttpException) e).code();
                if (code == 504) {
                    mOnSuccessAndFaultListener.onFault("网络异常，请检查您的网络状态");
                } else if (code == 404) {
                    mOnSuccessAndFaultListener.onFault("请求的地址不存在");
                } else {
                    mOnSuccessAndFaultListener.onFault("请求失败");
                }
            } else if (e instanceof UnknownHostException) {//域名解析失败
                mOnSuccessAndFaultListener.onFault("解析失败");
            } else {
                mOnSuccessAndFaultListener.onFault("error:" + e.getMessage());
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        } finally {
            dismissProgressDialog();
            progressDialog = null;

        }

    }

    /**
     * 当result等于1回调给调用者，否则自动显示错误信息，若错误信息为401跳转登录页面。
     * ResponseBody  body = response.body();//获取响应体
     * InputStream inputStream = body.byteStream();//获取输入流
     * byte[] bytes = body.bytes();//获取字节数组
     * String str = body.string();//获取字符串数据
     */
    @Override
    public void onNext(ResponseBody body) {
        String result = "";
        try {
            result = UtilTool.decompress(body.byteStream());
            LogUtil.log(result);
            mOnSuccessAndFaultListener.onSuccess(result);
        } catch (Exception e) {
            LogUtil.log("数据异常：" + result);
            mOnSuccessAndFaultListener.onFault("数据异常");
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isDisposed()) {
            this.dispose();
        }
    }
}