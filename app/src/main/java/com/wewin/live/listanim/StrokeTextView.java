package com.wewin.live.listanim;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;
/*
 *   author:jason
 *   date:2019/5/2115:48
 */
public class StrokeTextView  extends AppCompatTextView {
    public StrokeTextView(Context context) {
        super(context);
        init(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        AssetManager aManager=context.getAssets();
        Typeface font=Typeface.createFromAsset(aManager, "font/bool.ttf");
        setTypeface(font);
    }

}
