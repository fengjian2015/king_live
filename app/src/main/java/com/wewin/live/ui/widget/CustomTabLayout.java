package com.wewin.live.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wewin.live.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义 TabLayout
 * 可固定个数，可自动适配
 */

public class CustomTabLayout extends HorizontalScrollView {

    private Context mContext;
    private List<String> mTitleList = new ArrayList<>();
    private LinearLayout mTvContainerLl;
    private View mUnderlineView;
    private float mTextSize;
    private int mDefaultTextColor;
    private int mTvBackground;
    private float mUnderlineWidth;
    private float mUnderlineHeight;
    private int mUnderlineCol;
    private int mCheckedTextCol;
    private int mUnderlineDuration;
    private int mHorizontalSpace;
    private float mUnderlinePaddingBottom;
    private float mCtlCheckedTextSize;
    private int initPosition = 0;
    private int currentPosition = initPosition;
    private int oldPosition=initPosition;

    private final String TEXT_STYLE_NORMAL = "0";
    private final String TEXT_STYLE_BOLD = "1";
    private String mTextStyle;
    private OnTabClickListener mTabClickListener;
    private OnTabScrollListener mTabScrollListener;
    private boolean isCheckedTextSet = false;
    //一排显示几个 如果设置isAutomatic=true则无效
    private int LineNumber=4;
    private  int width;
    private int[] location;
    private float difference;
    //是否自适应
    private boolean isAutomatic=true;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public interface OnTabClickListener {
        void tabClick(int position, String str);
    }

    /**
     * 点击事件
     */
    public void setOnTabClickListener(OnTabClickListener mTabClickListener) {
        this.mTabClickListener = mTabClickListener;
    }

    public interface OnTabScrollListener {
        void scrollChange(int position, String text);
    }

    /**
     * 滑动监听
     */
    public void setOnTabScrollListener(OnTabScrollListener scrollListener) {
        this.mTabScrollListener = scrollListener;
    }

    /**
     * 如果初始下划线position不为0，则调该方法调整初始position
     */
    public void initUnderlinePosition(int position) {
        if (position > mTvContainerLl.getChildCount() - 1) {
            return;
        }
        this.initPosition = position;
        this.currentPosition = position;
        resetTextColor(position);
    }

    /**
     * 移动下划线
     */
    public void moveToPosition(int position) {
        if (position < 0) {
            return;
        }
        this.currentPosition = position;
        moveLine(position);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        reacquireWidth();
    }

    /**
     * 重新获取宽度，避免初始化时界面Gone状态，获取的width为0
     */
    public void reacquireWidth(){
        if(width!=0)return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = getWidth();
                location=new int[]{0,width};
                setChildLayout();
            }
        });

    }

    /**
     * 设置tab标签
     */
    public void setTitleArr(String[] titleArr) {
        if (titleArr == null || titleArr.length == 0) {
            return;
        }
        setTitleList(Arrays.asList(titleArr));
    }

    /**
     * 设置tab标签
     */
    public void setTitleList(List<String> titleList) {
        if (titleList == null || titleList.isEmpty()) {
            return;
        }
        mTitleList.clear();
        this.mTitleList.addAll(titleList);
        setChildLayout();
    }

    /**
     * 获取当前下划线position
     */
    public int getUnderlinePosition() {
        return currentPosition;
    }

    /**
     * 获取titleList
     */
    public List<String> getTitleList() {
        return mTitleList;
    }

    /**
     * 获取当前被选中的title
     */
    public String getCheckedText() {
        return mTitleList.get(currentPosition);
    }

    /**
     * 获取当前被选中的TextView
     */
    public TextView getCheckedTextView() {
        return (TextView) mTvContainerLl.getChildAt(currentPosition);
    }

    /**
     * 设置下划线颜色
     */
    public void setUnderlineColor(int colorId) {
        this.mUnderlineCol = colorId;
        mUnderlineView.setBackgroundColor(mUnderlineCol);
        resetTextColor(currentPosition);
    }

    /**
     * 设置选中字体颜色
     */
    public void setCheckedTextColor(int colorId) {
        this.mCheckedTextCol = colorId;
        this.isCheckedTextSet = true;
        resetTextColor(currentPosition);
    }

    private void moveLine(int clickPosition){
        resetTextColor(clickPosition);
        Message message=new Message();
        message.what=1;
        message.obj=clickPosition;
        mHandler.sendMessage(message);
    }

    private void startAnim(int clickPosition) {
        TextView clickTv = (TextView) mTvContainerLl.getChildAt(clickPosition);
        difference=clickTv.getMeasuredWidth()+mUnderlineWidth;
        //设置线的X轴
        float clickEndX = 0;
           if(isAutomatic){
               clickEndX = clickTv.getX();
           }else{
               clickEndX = clickTv.getX()+clickTv.getMeasuredWidth() / 4;
           }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mUnderlineView, "x", clickEndX);
        objectAnimator.setDuration(200).start();
        if(location[0]>clickEndX){
            scrollTo((int) clickTv.getX(),0);
        }
        if(location[1]-difference<clickEndX){
            scrollTo((int) (clickTv.getX()+clickTv.getMeasuredWidth()-location[1]+location[0]),0);
        }
        for (int i = 0; i < mTvContainerLl.getChildCount(); i++) {
            ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mDefaultTextColor);
        }
        clickTv.setTextColor(getCheckedTextCol());
        mUnderlineView.setBackgroundColor(mUnderlineCol);
        if (mTabScrollListener != null) {
            mTabScrollListener.scrollChange(clickPosition, clickTv.getText().toString());
        }
        oldPosition=currentPosition;
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    int click= (int) msg.obj;
                    startAnim(click);
                    break;
            }
        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        location[0]=l;
        location[1]=width+l;
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTabLayout);
            mTextSize = typedArray.getDimension(R.styleable.CustomTabLayout_ctlTextSize, 15);
            mDefaultTextColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTextColor, Color.parseColor("#333333"));
            mTvBackground = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvBackground, Color.parseColor("#00000000"));
            mUnderlineWidth = typedArray.getDimension(R.styleable.CustomTabLayout_ctlUnderlineWidth, 20);
            mUnderlineHeight = typedArray.getDimension(R.styleable.CustomTabLayout_ctlUnderlineHeight, 2);
            mUnderlineCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineColor, Color.parseColor("#FFFFFF"));
            mUnderlinePaddingBottom=typedArray.getDimension(R.styleable.CustomTabLayout_ctlUnderlinePaddingBottom,mContext.getResources().getDimension(R.dimen.d10dp));
            mCheckedTextCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlCheckedTextColor, mUnderlineCol);
            mUnderlineDuration = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineDuration, 150);
            mHorizontalSpace = typedArray.getInt(R.styleable.CustomTabLayout_ctlHorizontalSpace, 20);
            mCtlCheckedTextSize=typedArray.getDimension(R.styleable.CustomTabLayout_ctlCheckedTextSize, 18);
            mTextStyle = typedArray.getString(R.styleable.CustomTabLayout_ctlTextStyle);
            LineNumber=typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineNumber,4);
            isAutomatic=typedArray.getBoolean(R.styleable.CustomTabLayout_ctlIsAutomatic,true);

            typedArray.recycle();
        }

        if (TextUtils.isEmpty(mTextStyle)) {
            mTextStyle = TEXT_STYLE_NORMAL;
        }

        LayoutInflater.from(context).inflate(R.layout.layout_custom_tab, this, true);
        mTvContainerLl = findViewById(R.id.ll_text_view_container);
        mUnderlineView = findViewById(R.id.view_underline);
        mUnderlineView.setBackgroundColor(mUnderlineCol);


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        TextView firstTv = (TextView) mTvContainerLl.getChildAt(initPosition);
        TextView currentTv = (TextView) mTvContainerLl.getChildAt(currentPosition);

        if (firstTv == null) {
            return;
        }
        if(isAutomatic) {
            mUnderlineView.layout(getPaddingLeft() + firstTv.getLeft(),
                    (int) (getMeasuredHeight() - mUnderlineHeight - mUnderlinePaddingBottom),
                    getPaddingLeft() + firstTv.getLeft() + currentTv.getMeasuredWidth(),
                    (int) (getMeasuredHeight() - mUnderlinePaddingBottom));
        }else{
            mUnderlineView.layout(getPaddingLeft() + firstTv.getLeft()+currentTv.getMeasuredWidth() / 4,
                    (int) (getMeasuredHeight() - mUnderlineHeight - mUnderlinePaddingBottom),
                    getPaddingLeft() + firstTv.getLeft() + currentTv.getMeasuredWidth()-currentTv.getMeasuredWidth() / 4,
                    (int) (getMeasuredHeight() - mUnderlinePaddingBottom));
        }
    }

    private void setChildLayout() {

        mTvContainerLl.removeAllViews();
        for (int i = 0; i < mTitleList.size(); i++) {
            final String title = mTitleList.get(i);
            final TextView textView = new TextView(mContext);
            textView.setSingleLine();
            textView.setText(title);
            LinearLayout.LayoutParams params;
            if(isAutomatic) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.d15dp),0,getResources().getDimensionPixelSize(R.dimen.d15dp),0);
            }else{
                params = new LinearLayout.LayoutParams((int) getTextViewWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setPadding(getResources().getDimensionPixelSize(R.dimen.d15dp),0,getResources().getDimensionPixelSize(R.dimen.d15dp),0);
            }
            if (i == initPosition) {
                textView.getPaint().setTextSize(mCtlCheckedTextSize);
                textView.setTextColor(mCheckedTextCol);
            } else {
                textView.getPaint().setTextSize(mTextSize);
                textView.setTextColor(mDefaultTextColor);
            }

            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(mTvBackground);
            TextPaint textPaint = textView.getPaint();
            switch (mTextStyle) {
                case TEXT_STYLE_NORMAL:
                    textPaint.setFakeBoldText(false);
                    break;

                case TEXT_STYLE_BOLD:
                    textPaint.setFakeBoldText(true);
                    break;

                default:
                    break;
            }

            mTvContainerLl.addView(textView, params);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = mTitleList.indexOf(title);
                    if (mTabClickListener != null) {
                        mTabClickListener.tabClick(currentPosition, title);
                    }
                    moveLine(currentPosition);
                }
            });
        }
    }

    private float getTextViewWidth(){
        float myWidth;
        if(LineNumber>mTitleList.size()){
            myWidth=width/mTitleList.size();
        }else{
            myWidth= width/LineNumber;
        }
        return myWidth;
    }

    private void resetTextColor(int position) {
        for (int i = 0; i < mTvContainerLl.getChildCount(); i++) {
            if (i == position) {

                ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(getCheckedTextCol());
                ((TextView) mTvContainerLl.getChildAt(i)).getPaint().setTextSize(mCtlCheckedTextSize);
            } else {
                ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mDefaultTextColor);
                ((TextView) mTvContainerLl.getChildAt(i)).getPaint().setTextSize(mTextSize);
            }
            if(isAutomatic)
                ((TextView) mTvContainerLl.getChildAt(i)).setText(((TextView) mTvContainerLl.getChildAt(i)).getText().toString());
        }
    }


    private int getCheckedTextCol() {
        if (!isCheckedTextSet) {
            return mUnderlineCol;
        } else {
            return mCheckedTextCol;
        }
    }


}
