package com.wewin.live.listanim;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * Author : zhouyx
 * Date   : 2017/8/2
 * Description : 礼物布局抽象类
 */
public abstract class AbstractGiftViewHolder {

    /**
     * 初始化布局
     */
    public abstract void initGiftView(Context context);

    /**
     * 礼物布局
     */
    public abstract View getGiftView();

    /**
     * 礼物图片控件
     */
    public abstract ImageView getGiftImageView();

    /**
     * 礼物数量控件
     */
    public abstract StrokeTextView getGiftNumberView();

    /**
     * 设置布局信息
     * @param context
     * @param giftModel
     */
    public abstract void loadGiftModelToView(Context context, AbstractGiftModel giftModel);

}
