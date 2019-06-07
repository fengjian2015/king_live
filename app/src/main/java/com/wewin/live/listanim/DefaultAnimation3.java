package com.wewin.live.listanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Author : zhouyx
 * Date   : 2017/8/2
 * Description : 默认礼物动画3
 */
public class DefaultAnimation3 implements ICustomerAnimation {

    @Override
    public AnimatorSet giftEnterAnimation(final GiftAnimationLayout layout, AbstractGiftViewHolder holder) {
        //礼物飞入
        ObjectAnimator animator = GiftAnimationUtils.createTranslationXAnimator(layout, -layout.getWidth(), 0, 800, new OvershootInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout.startNumberComboAnimation();
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animatorSet.start();
        return animatorSet;
    }

    @Override
    public AnimatorSet giftNumberComboAnimation(final GiftAnimationLayout layout, final AbstractGiftViewHolder holder, int comboNumber) {
        //数量增加
        ObjectAnimator animator = GiftAnimationUtils.createGiftNumberAnimator(holder.getGiftNumberView());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.getGiftNumberView().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.singleComboAnimationEnd();//这里一定要回调该方法，不然连击不会生效
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animator.start();
        return animatorSet;
    }

    @Override
    public AnimatorSet giftExitAnimation(final GiftAnimationLayout layout, AbstractGiftViewHolder holder) {
        ObjectAnimator animator = GiftAnimationUtils.createTranslationXAnimator(layout, 0, -layout.getWidth(), 800, new DecelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animatorSet.start();
        return animatorSet;
    }

}
