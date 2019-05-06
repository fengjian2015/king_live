package com.example.jasonutil.util;


import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

/**
 * Created by GA on 2017/10/25.
 * 公共动画
 */

public class AnimatorTool {

    private static AnimatorTool instance;

    public static AnimatorTool getInstance() {

        if (instance == null) {

            instance = new AnimatorTool();

        }

        return instance;
    }

    //抖动动画
    public void editTextAnimator(View view) {

        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -10, 10, 0);

        translationY.setDuration(400);

        translationY.start();

    }

    //抖动动画
    public void viewAnimator(View view) {

        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -20, 20, 0);

        translationY.setDuration(1000);
        translationY.setInterpolator(new AnticipateOvershootInterpolator());
        translationY.start();

    }

}
