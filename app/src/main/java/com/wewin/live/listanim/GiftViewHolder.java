package com.wewin.live.listanim;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wewin.live.R;
import com.wewin.live.ui.widget.ScrollTextView;


/*
 *   author:jason
 *   date:2019/5/2114:05
 *   Description : 继承礼物布局抽象类自定义UI
 */
public class GiftViewHolder extends AbstractGiftViewHolder {
    private View rootView;
    private ImageView  ivGift;
    private ScrollTextView tvNickname, tvInfo;
    private StrokeTextView tvGiftNumber;

    @Override
    public void initGiftView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.item_animation_gift, null);
        tvNickname = (ScrollTextView) rootView.findViewById(R.id.tv_nickname);
        tvInfo = (ScrollTextView) rootView.findViewById(R.id.tv_info);
        ivGift = (ImageView) rootView.findViewById(R.id.iv_gift);
        tvGiftNumber = (StrokeTextView) rootView.findViewById(R.id.tv_gift_number);
    }

    @Override
    public View getGiftView() {
        return rootView;
    }

    @Override
    public ImageView getGiftImageView() {
        return ivGift;
    }

    @Override
    public StrokeTextView getGiftNumberView() {
        return tvGiftNumber;
    }

    @Override
    public void loadGiftModelToView(Context context, AbstractGiftModel giftModel) {
        MyGiftModel model = (MyGiftModel) giftModel;
        if (!TextUtils.isEmpty(model.getSenderName())) {
            tvNickname.setText(model.getSenderName());
        }
        if (!TextUtils.isEmpty(model.getGiftName())) {
            tvInfo.setText(model.getGiftName());
        }
//        Glide.with(context.getApplicationContext())
//                .load(model.getSenderAvatar())
//                .into(ivAvatar);
        Glide.with(context.getApplicationContext())
                .load(model.getGiftPic())
                .into(ivGift);
    }
}
