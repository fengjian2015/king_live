package com.wewin.live.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.jasonutil.util.LogUtil;
import com.wewin.live.R;
import java.util.Timer;
import java.util.TimerTask;

/*
 *   author:jason
 *   date:2019/4/1810:17
 *   确定按钮点击后动画
 */
public class ButtonAnimation extends View {
    private long animationInterval = 2000;
    private long animationFinishTime = 50;
    private static final int FINISHANIMATION = 1;

    private Context mContext;
    //动画进度
    private float fraction;

    private int textColor;
    private float textSize;
    private String text;
    private int bg_color;

    private int button_Width;
    private int button_Height;
    //是否正在进行动画
    private boolean isAnimation = false;
    //按下时间
    private long currentMS;
    //记录是否移动
    private int moveX;
    private int moveY;
    //点击位置
    private float iniEventX;
    private float iniEvnetY;
    private PathMeasure mPathMeasure;


    public ButtonAnimation(Context context) {
        super(context);
        init(context, null);
    }

    public ButtonAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ButtonAnimation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonAnimation);
            textColor = typedArray.getInt(R.styleable.ButtonAnimation_t_color, Color.parseColor("#FFFFFF"));
            textSize = typedArray.getDimension(R.styleable.ButtonAnimation_t_size, mContext.getResources().getDimension(R.dimen.d15dp));
            text =typedArray.getString(R.styleable.ButtonAnimation_text_data);
            bg_color = typedArray.getInt(R.styleable.ButtonAnimation_bg_color, Color.parseColor("#FE294E"));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        button_Width = measureWidth(getMeasuredWidth());
        button_Height = measureHeight(getMeasuredHeight());
        setMeasuredDimension(button_Width, button_Height);
    }

    private int measureWidth(int measureSpec) {
        int result = 200;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 200;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    /**
     * 设置完成的百分比
     *
     * @param fraction 百分比
     */
    private void setFraction(float fraction) {
        this.fraction = fraction;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAnimation) {
            drawAnimationButton(canvas, fraction);
        } else {
            drawButton(canvas);
        }
    }


    //未进行动画时绘制
    private void drawButton(Canvas canvas) {
        canvas.save();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(bg_color);
        paint.setStyle(Paint.Style.FILL);
        //绘制圆角矩形
        RectF r = new RectF(0, 0, button_Width, button_Height);
        canvas.drawRoundRect(r, 8, 8, paint);
        if(text!=null) {
            //绘制文本
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            canvas.drawText(text, button_Width / 2, button_Height / 2 + (textSize) / 2, paint);
        }
        canvas.restore();
    }

    //进行动画时绘制
    private void drawAnimationButton(Canvas canvas, float fraction) {
        fraction=fraction/0.5f;
        canvas.save();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(bg_color);
        paint.setStyle(Paint.Style.FILL);
        if (fraction>1)fraction=1;
        //逐渐显示圆形
        int radius = button_Height  / 2;
        int left= (int) (Math.max((fraction*button_Width/2-radius),0));
        int top=0;
        int right=(int) (Math.min((button_Width/2+(1-fraction)*button_Width/2+radius),button_Width));
        int bottom=button_Height;
        RectF r = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(r, Math.max(radius * fraction,8), Math.max(radius * fraction,8), paint);

        drawLine(canvas);
        canvas.restore();
    }

    /**
     * 绘制勾
     * @param canvas
     */
    private void drawLine(Canvas canvas){
        int radius = button_Height  / 2;
        //计算勾所在的正方形范围
        float lineSpacings=radius*2/4;
        RectF lineF = new RectF(button_Width/2-radius+lineSpacings, lineSpacings, button_Width/2+radius-lineSpacings,  button_Height-lineSpacings);
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(8);
        linePaint.setStyle(Paint.Style.STROKE);

        float lineRadius=radius-lineSpacings;
        Path linePath=new Path();
        mPathMeasure=new PathMeasure();
        linePath.moveTo(lineF.left,lineF.top+lineRadius);

        //这里计算下一个点距离上一个点的距离，然后通过fraction计算
        if(fraction<=0.4){
            //绘制勾的前一半
            float line1=fraction/0.4f;
            float x=line1*lineRadius+lineF.left;
            float y=line1*lineRadius+(lineF.top+lineRadius);
            linePath.lineTo(x,y);

        }else{
            //绘制勾的后一半
            linePath.lineTo(lineF.left+lineRadius,lineF.bottom);
            float line2=1-(1-fraction)/0.6f;
            float x=line2*lineRadius+lineF.left+lineRadius;
            float y=lineF.bottom-line2*(lineRadius*2-(lineSpacings/2));
            linePath.lineTo(x,y);
        }
        mPathMeasure.nextContour();
        mPathMeasure.setPath(linePath,false);
        canvas.drawPath(linePath, linePaint);
    }

    /**
     * 点击事件
     */
    private void onButtonClick() {
        isAnimation = true;
        show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iniEventX = event.getX();
                iniEvnetY = event.getY();
                currentMS = System.currentTimeMillis();//long currentMS     获取系统时间
                moveX = 0;
                moveY = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                moveX += Math.abs(event.getX() - iniEventX);//X轴距离
                moveY += Math.abs(event.getY() - iniEvnetY);//y轴距离
                iniEventX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                long moveTime = System.currentTimeMillis() - currentMS;//移动时间
                //判断是否继续传递信号
                if (moveTime <= 200 && moveX < 20 && moveY < 20&&!isAnimation) {
                    //点击事件
                    onButtonClick();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 显示动画
     */
    public void show() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "fraction", 0, 1);
        objectAnimator.setDuration(animationInterval - 50);
        objectAnimator.start();
        //处理动画定时
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FINISHANIMATION) {
                    moveAnimation();
                }
                return false;
            }
        });
        //动画定时器
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = FINISHANIMATION;
                handler.sendMessage(message);
            }
        }, animationInterval);
    }

    /**
     * 隐藏动画view
     */
    private void moveAnimation() {
        isAnimation = false;
        invalidate();
        this.animate()
                .withLayer()
                .setDuration(animationFinishTime);
    }

}
