package com.wewin.live.ui.widget;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.example.jasonutil.util.ScreenTools;
import com.wewin.live.R;

/**
 * 侧滑栏
 */
public class CustomSideMenu extends RelativeLayout{
	
	public final static double SIDEMENUSCAL = 0.75;
	private Scroller mScroller;
	private int mWidthOfSideMenu;
	private int mEdge;
	private Point mInitPoint = new Point();
	private float iniEventX;
	private float iniEvnetY;
	private OnSideMenuListen mOnSideMenuListen;
	//按下时间
	private long currentMS;
	//记录是否移动
	private int moveX;
	private int moveY;
	//判断是否正在关闭中
	private boolean isBeingClose=false;

	Handler handler=new Handler();

	public CustomSideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onFinishInflate() {
		mScroller = new Scroller(getContext());
		super.onFinishInflate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		View child = (View) getChildAt(0);
		LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
		layoutParams.width = mWidthOfSideMenu = (int) (ScreenTools.getScreenWidth(getContext()) * SIDEMENUSCAL);
		layoutParams.height = ScreenTools.getScreenHeight(getContext());
		child.setLayoutParams(layoutParams);
		mEdge = ScreenTools.dip2px(getContext(),15);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		View child = (View) getChildAt(0);
		child.layout(-child.getMeasuredWidth(), 0, 0, child.getMeasuredHeight());
		mInitPoint.x = child.getLeft();
		mInitPoint.y = child.getTop();
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), 0);
			if(mOnSideMenuListen!=null)
		    mOnSideMenuListen.setShadowAlpha(getShadowAlpha());
			invalidate();
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getX() < mEdge || getScrollX() < 0) {
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
		    	float tmp = event.getX() - iniEventX;
				iniEventX = event.getX();
				if(getScrollX()-tmp < -mWidthOfSideMenu)
					scrollTo((int) (-mWidthOfSideMenu), 0);
				else 
				    scrollTo((int) (getScrollX()-tmp), 0);
				if(mOnSideMenuListen!=null)
				mOnSideMenuListen.setShadowAlpha(getShadowAlpha());
				break;
			case MotionEvent.ACTION_UP:
				int endX = (int) event.getX();
				int endY = (int) event.getY();
				if(endX == iniEventX && endY == iniEvnetY && iniEventX > mWidthOfSideMenu) {
					sideMenuScroll(false);
					break;
				} else {
					if(getScrollX() < (-mWidthOfSideMenu/2)) {
						mScroller.startScroll(getScrollX(), 0, (int) (-mWidthOfSideMenu - getScrollX()), 0, 500);
					} else {
						mScroller.startScroll(getScrollX(), 0, (int) (0 - getScrollX()), 0, 500);
					}
					long moveTime = System.currentTimeMillis() - currentMS;//移动时间
					//判断是否继续传递信号
					if(moveTime<=200&&moveX<20&&moveY<20&&!isBeingClose&&iniEventX> mEdge){
						sideMenuScroll(false);
					}
					break;
				}
			default:
				break;
			}
			invalidate();
			return true;
		}
		return false;
	}

	/**
	 * 设置初始背景色和透明度
	 */
	public void setBackgroundOrAlpha(){
		setBackgroundColor(getResources().getColor(R.color.black4));
		getBackground().setAlpha(0);
	}

	/**
	 * 关闭打开 带过度时间
	 * @param isLeftToRight  true打开
	 */
	public void sideMenuScroll(boolean isLeftToRight) {
		if(isLeftToRight) {
			mScroller.startScroll(0, 0, 0-mWidthOfSideMenu, 0, 500);
		} else {
			isBeingClose=true;
			mScroller.startScroll(0 - mWidthOfSideMenu, 0, mWidthOfSideMenu, 0, 500);
			handler.postDelayed(new MyRunnable(),500);
		}
		invalidate();
	}

	/**
	 * 关闭打开
	 * @param isLeftToRight  true打开
	 */
	public void sideMenuScrollNoDuration(boolean isLeftToRight) {
		if(isLeftToRight) {
			mScroller.startScroll(0, 0, 0-mWidthOfSideMenu, 0, 0);
		} else {
			mScroller.startScroll(0 - mWidthOfSideMenu, 0, mWidthOfSideMenu, 0, 0);
		}
		invalidate();
	}

	class MyRunnable implements Runnable {

		@Override
		public void run() {
			isBeingClose=false;
		}

	}



	public void doScroll(int sx, int sy, int dx, int dy) {
		mScroller.startScroll(sx, sy, dx, dy, 500);
		invalidate();
	}
	
	public int getShadowAlpha() {
		int currentScroll = getScrollX();
		if(currentScroll > 0)
			currentScroll = 0;
		if(-currentScroll > mWidthOfSideMenu)
			currentScroll = -mWidthOfSideMenu;
		int alpha = (int) (-currentScroll*1.0/mWidthOfSideMenu * 100);
		return alpha;
	}
	
	public void setSideMenuListen(OnSideMenuListen onSideMenuListen){
		mOnSideMenuListen = onSideMenuListen;
	}
	
	public interface OnSideMenuListen{
		public void setShadowAlpha(int alpha);
	}
}
