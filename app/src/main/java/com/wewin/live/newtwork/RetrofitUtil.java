package com.wewin.live.newtwork;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wewin.live.cookie.ClearableCookieJar;
import com.wewin.live.cookie.PersistentCookieJar;
import com.wewin.live.cookie.cache.SetCookieCache;
import com.wewin.live.cookie.persistence.SharedPrefsCookiePersistor;
import com.wewin.live.utils.Constants;
import com.example.jasonutil.util.LogUtil;
import com.example.jasonutil.util.NetWorkUtil;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dada on 2017/6/14.
 * okhttp网络配置
 */

public class RetrofitUtil {

    private static final long DEFAULT_DIR_CACHE = 2000;
    private Context mContext;

    private static int timeOut = 20;
    private static String base_url=Constants.BASE_URL;

    GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitUtil instance = null;
    private Retrofit mRetrofit = null;
    private ClearableCookieJar cookieJar;


    public static RetrofitUtil getInstance(Context context) {
        if (instance == null || timeOut != 20||!Constants.BASE_URL.equals(base_url)) {
            timeOut = 20;
            base_url=Constants.BASE_URL;
            instance = new RetrofitUtil(context,timeOut,base_url);
        }
        return instance;
    }

    /**
     * 控制时长，配置一次
     * @param context
     * @param time
     * @return
     */
    public static RetrofitUtil getInstance(Context context, int time) {
        timeOut = time;
        base_url=Constants.BASE_URL;
        instance= new RetrofitUtil(context,timeOut,base_url);
        return instance;
    }

    /**
     * 配置URL
     * @param context
     * @return
     */
    public static RetrofitUtil getInstance(Context context,String url) {
        base_url=url;
        instance= new RetrofitUtil(context,timeOut,base_url);
        return instance;
    }


    public class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();//获取请求
            //这里就是说判读我们的网络条件，要是有网络的话我么就直接获取网络上面的数据，要是没有网络的话我么就去缓存里面取数据
            if (!NetWorkUtil.isNetworkAvailable(mContext)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.d("CacheInterceptor", "no network");
            }
            Response originalResponse = chain.proceed(request);
            //设置TOKEN
//            String authorization = originalResponse.header("Authorization123");
//            if (!StringUtils.isEmpty(authorization)) {
//                MySharedPreferences.getInstance().setString(TOKEN, authorization);
//                MySharedPreferences.getInstance().setLong(TOKEN_TIME, System.currentTimeMillis());
//            }
            int code = originalResponse.code();
            String message = originalResponse.message();
            if (code == 200) {
            } else if (code == 401) {
            } else if (code == 500) {
            } else if (code == 504) {
            } else if (code == 502) {
            } else {
            }
            LogUtil.Log(request.url() + "接口返回碼： " + code + "----接口返回消息： " + message);
            if (NetWorkUtil.isNetworkAvailable(mContext)) {
                //这里大家看点开源码看看.header .removeHeader做了什么操作很简答，就是的加字段和减字段的。
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        //这里设置的为0就是说不进行缓存，我们也可以设置缓存时间
                        .header("Cache-Control", "public, max-age=" + 10)
                        .removeHeader("Pragma")
                        .build();
            } else {
                int maxTime = 4 * 24 * 60 * 60;
                return originalResponse.newBuilder()
                        //这里的设置的是我们的没有网络的缓存时间，想设置多少就是多少。
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxTime)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    }

    /**
     * 配置信息
     * @param Context
     * @param time
     * @param url
     */
    private RetrofitUtil(Context Context, int time,String url) {
        mContext = Context;//设置缓存路径
        File cacheFile = new File(mContext.getCacheDir(), "caheData");
        //设置cookie,退出时清除
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));
        //设置缓存大小
        Cache cache = new Cache(cacheFile, DEFAULT_DIR_CACHE);
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//连接失败后是否重新连接
                .connectTimeout(time, TimeUnit.SECONDS)//超时时间20S
                .readTimeout(time, TimeUnit.SECONDS)//超时时间20S
                .writeTimeout(time, TimeUnit.SECONDS)//超时时间20S
                .addInterceptor(new CacheInterceptor())//也就这里不同
//                .addNetworkInterceptor(new CacheInterceptor())//也就这里不同
                .cache(cache)
                .cookieJar(cookieJar)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(factory)//json转换成JavaBean
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 清除cookie
     */
    public void clearCookie(){
        cookieJar.clear();
    }

    public MyService getServer() {
        return mRetrofit.create(MyService.class);
    }

    /**
     * 设置订阅 和 所在的线程环境
     */
    public <T> void toSubscribe(Observable<T> o, DisposableObserver<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);

    }
}
