package com.wewin.live.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.jasonutil.util.StringUtils;
import com.wewin.live.R;

/**
 * @author jsaon
 * @date 2019/3/8
 * 可设置hint的textview
 */
public class HintTextView extends AppCompatTextView {
    private Context mContext;
    /**
     * 未设置的字体颜色
     */
    private int hintColor;
    /**
     * 设置后的颜色
     */
    private int textColor;
    /**
     * 未设置的文本
     */
    private String hintText;
    private boolean isHint=true;

    public HintTextView(Context context) {
        super(context);
    }

    public HintTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HintText);
            hintColor = typedArray.getColor(R.styleable.HintText_hintText_color, mContext.getResources().getColor(R.color.gray1));
            hintText = typedArray.getString(R.styleable.HintText_hintText_hint);
            textColor= typedArray.getColor(R.styleable.HintText_text_color, mContext.getResources().getColor(R.color.black1));
            typedArray.recycle();
        }
        if(!StringUtils.isEmpty(hintText)){
            setHint();
        }
    }

    public String getTextContent(){
        if(isHint){
            return "";
        }else {
            return getText().toString();
        }
    }

    public void setHint(String content){
        if(StringUtils.isEmpty(content)) {
            return;
        }
        hintText=content;
        setText(hintText);
        setTextColor(hintColor);
        isHint=true;
    }

    public void setHint(){
        if(StringUtils.isEmpty(hintText)) {
            return;
        }
        setText(hintText);
        setTextColor(hintColor);
        isHint=true;
    }

    public void setMyText(String content){
        if(StringUtils.isEmpty(content)){
            setHint();
            isHint=true;
        }else{
            setText(content);
            setTextColor(textColor);
            isHint=false;
        }
    }
}
