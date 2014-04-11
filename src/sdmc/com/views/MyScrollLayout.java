package sdmc.com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollLayout extends ViewGroup {

	private static final String TAG = "MyScrollLayout";
	private static final boolean DEBUG = true;
	private VelocityTracker mVelocityTracker; // 用于判断甩动手势
	private static final int SNAP_VELOCITY = 600;   //X轴�?度基值，大于该�?时进行切�?
	private Scroller mScroller; // 滑动控制�?
	private int mCurScreen;   //当前页面为第几屏
	private int mDefaultScreen = 0;
	private float mLastMotionX;


	private OnViewChangeListener mOnViewChangeListener;

	public MyScrollLayout(Context context) {
		super(context);
		init(context);
	}

	public MyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mCurScreen = mDefaultScreen;
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(DEBUG)
		Log.e(TAG, "---- > onLayout()");
//		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();
			Log.e("count", childCount+"");
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
//		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(DEBUG)
		Log.e(TAG, "---- > onMeasure()");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		
		scrollTo(mCurScreen * width, 0);
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(DEBUG)
		Log.e(TAG, "---- > onDraw()");
	}
	public void snapToDestination() {
		final int screenWidth = getWidth();

		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	//使屏幕移动到第whichScreen+1�?
	public void snapToScreen(int whichScreen) {

		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); 

			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);
			}
		}
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
 
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		int action = ev.getAction();
//		final float x = ev.getX();
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			 Log.w("info",TAG +" --> --> 拦截事件  < 按下 >:   down down down ");
////			 if (mVelocityTracker == null) {
////					mVelocityTracker = VelocityTracker.obtain();
////					mVelocityTracker.addMovement(ev);
////				}
////				if (!mScroller.isFinished()) {
////					mScroller.abortAnimation();
////				}
////				mLastMotionX = x;
//				break;
////			return true;
//			 
//		case MotionEvent.ACTION_MOVE:
//			 Log.w("info",TAG +" --> --> 拦截事件  < 移动 >:  move move move ");
//			break;
////			 return true;
//		case MotionEvent.ACTION_UP:
//			 Log.w("info",TAG +" --> --> 拦截事件   < 抬起 > : up up up ");
//			break;
//		}
//		return false;
//		return true; // 则 子View 接收不到Touch事件，则Touch事件自己消费，执行onTouchEvent
//		return super.onInterceptTouchEvent(ev);
//	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		
		
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(DEBUG)
			Log.i("info", TAG+" --> 拦截事件  < 按下 >:  ∨ ∨ ∨ ");
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			if(DEBUG)
			Log.i("info", TAG+" --> 拦截事件  < 移动 >:   ---> --> ");
			int deltaX = (int) (mLastMotionX - x);
			if (isCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}
				mLastMotionX = x;
				//正向或�?负向移动，屏幕跟随手指移�?
				scrollBy(deltaX, 0);
			}
			break;

		case MotionEvent.ACTION_UP:
			if(DEBUG)
			Log.i("info", TAG+" --> 拦截事件  < 抬起 > : ∧ ∧ ∧");
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				//得到X轴方向手指移动�?�?
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			//velocityX为正值说明手指向右滑动，为负值说明手指向左滑�?
			// 右滑动----->    左翻屏
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} 
			// 左滑动 <----  右翻屏
			else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} 
			else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			break;
		}
		return false;
	}
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		
//		
//		final int action = event.getAction();
//		final float x = event.getX();
//		final float y = event.getY();
//
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			if(DEBUG)
//			Log.i("info", TAG+" --> 触摸事件  < 按下 >:  ∨ ∨ ∨ ");
//			if (mVelocityTracker == null) {
//				mVelocityTracker = VelocityTracker.obtain();
//				mVelocityTracker.addMovement(event);
//			}
//			if (!mScroller.isFinished()) {
//				mScroller.abortAnimation();
//			}
//			mLastMotionX = x;
//			break;
//
//		case MotionEvent.ACTION_MOVE:
//			if(DEBUG)
//			Log.i("info", TAG+" --> 触摸事件  < 移动 >:   ---> --> ");
//			int deltaX = (int) (mLastMotionX - x);
//			if (isCanMove(deltaX)) {
//				if (mVelocityTracker != null) {
//					mVelocityTracker.addMovement(event);
//				}
//				mLastMotionX = x;
//				//正向或�?负向移动，屏幕跟随手指移�?
//				scrollBy(deltaX, 0);
//			}
//			break;
//
//		case MotionEvent.ACTION_UP:
//			if(DEBUG)
//			Log.i("info", TAG+" --> 触摸事件  < 抬起 > : ∧ ∧ ∧");
//			int velocityX = 0;
//			if (mVelocityTracker != null) {
//				mVelocityTracker.addMovement(event);
//				mVelocityTracker.computeCurrentVelocity(1000);
//				//得到X轴方向手指移动�?�?
//				velocityX = (int) mVelocityTracker.getXVelocity();
//			}
//			//velocityX为正值说明手指向右滑动，为负值说明手指向左滑�?
//			// 右滑动----->    左翻屏
//			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
//				// Fling enough to move left
//				Log.e(TAG, "snap left");
//				snapToScreen(mCurScreen - 1);
//			} 
//			// 左滑动 <----  右翻屏
//			else if (velocityX < -SNAP_VELOCITY
//					&& mCurScreen < getChildCount() - 1) {
//				// Fling enough to move right
//				Log.e(TAG, "snap right");
//				snapToScreen(mCurScreen + 1);
//			} 
//			else {
//				snapToDestination();
//			}
//
//			if (mVelocityTracker != null) {
//				mVelocityTracker.recycle();
//				mVelocityTracker = null;
//			}
//
//			break;
//		}
//		return true;
//	}

	/**
	 * 
	 * @param deltaX   = mLastX - x;
	 * @return
	 */
	private boolean isCanMove(int deltaX) {
		//deltaX<0说明手指向右�?
		if (getScrollX() <= 0 && deltaX < 0 ) {
			return false;
		}
		if(deltaX < 0 && deltaX > -100){
			return false;
		}
		//deltaX>0说明手指向左�?
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}
		if(deltaX > 0 && deltaX < 100){
			return false;
		}
		return true;
	}

	public void setOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

}
