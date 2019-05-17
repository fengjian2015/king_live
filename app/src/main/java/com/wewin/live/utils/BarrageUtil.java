package com.wewin.live.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

import com.example.jasonutil.util.LogUtil;
import java.util.HashMap;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/*
 *   author:jason
 *   date:2019/5/1613:28
 *   弹幕工具类
 */
public class BarrageUtil {
    private IDanmakuView mDanmakuView;
    private Context mContext;
    private DanmakuContext mDanmakuContext;
    //是否准备完毕
    private boolean isPrepared=false;
    public boolean isShow=false;

    public BarrageUtil(Context context, DanmakuView danmakuView){
        mDanmakuView=danmakuView;
        mContext=context;
        mDanmakuContext=DanmakuContext.create();

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
//        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
//        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, false);
//        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, false);

        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(null).setDanmakuMargin(0);
        initDanmakuView();
    }

    public void initDanmakuView(){
        if (mDanmakuView != null) {
            mDanmakuView.showFPS(false);
            mDanmakuView.enableDanmakuDrawingCache(true);
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void prepared() {
                    LogUtil.Log("弹幕准备完成"+isShow);
                    isPrepared=true;
                    if(isShow)
                    mDanmakuView.start();
                }
            });
            mDanmakuView.prepare(parser, mDanmakuContext);
        }
    }

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };


    public void addBarrage(boolean isSelf,String content) {
        if(TextUtils.isEmpty(content)||!isPrepared)return;
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        SpannableString spanStr = new SpannableString(content);
        if (EmoticonUtil.isContainEmotion(content)) {
            spanStr = EmoticonUtil.getEmoSpanStr(mContext, spanStr);
        }
        danmaku.text = spanStr;
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = true;

        danmaku.setTime(mDanmakuView.getCurrentTime()+1200);
        danmaku.textSize = 20f * (parser.getDisplayer().getDensity() - 0.6f);
        danmaku.textShadowColor = 0;
        // danmaku.underlineColor = Color.GREEN;
        if (isSelf){
            danmaku.borderColor = 0;
            danmaku.textColor = Color.GREEN;
        }else {
            danmaku.borderColor = 0;
            danmaku.textColor = Color.WHITE;
        }

        mDanmakuView.addDanmaku(danmaku);

    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
           /* if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if(drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if(mDanmakuView != null) {
                                mDanmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }*/
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };

    public void start(){
        isShow=true;
        if (mDanmakuView!=null&&isPrepared){
            mDanmakuView.start();
        }
    }

    public void showOrHide(boolean isShow){
        if (isShow){
            show();
        }else {
            hide();
        }
    }

    public void show(){
        if (mDanmakuView!=null){
            mDanmakuView.show();
        }
    }

    public void hide(){
        if (mDanmakuView!=null){
            mDanmakuView.hide();
        }
    }

    public void onPause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
            LogUtil.Log("弹幕暂停");
        }
    }

    public void onResume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
            LogUtil.Log("弹幕重新开始");
        }
    }

    public void onDestroy() {
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
            LogUtil.Log("弹幕销毁");
        }
    }

    public static int dip2px(Context context,int i) {
        return (int) (0.5D + (double) (getDensity(context) * (float) i));
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
