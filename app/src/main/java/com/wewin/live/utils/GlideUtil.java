package com.wewin.live.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.jasonutil.util.ActivityUtil;
import com.wewin.live.R;

/**
 * @author jsaon
 * @date 2019/3/6
 * 图片加载类
 */
public class GlideUtil {
    public static void setCircleImg(Context context, Object url, ImageView imageView) {
        if (!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.icon_avatar)).into(imageView);
    }

    public static void setImg(Context context, Object url, ImageView imageView,int id) {
        if (!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        Glide.with(context).load(url).apply(new RequestOptions().placeholder(id)).into(imageView);
    }
}
