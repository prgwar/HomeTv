package sdmc.com.hometv;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.stat.KeyCode;
import com.sdmc.phone.util.BytesMaker;
import com.sdmc.phone.util.MenuUtil;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
/*
 * 鼠标界面
 */
public class MouseTouch extends FatherActivity{
	
	private byte[] mBytes;
	private int mDownX;
	private int mDownY;
	private int mTouchX;
	private int mTouchY;
	private int mSendX;
	private int mSendY;
	
	private boolean mIsTouch = false;
	private boolean isUp = true;
	private boolean mIsDown = false;
	
	private Button startDrag;
	private Button endDrag;
	private Button btn_left;
	private Button btn_right;
	private Button btn_move;
	private Button btn_touch;
	private Button btn_middle;
	private int toDown = 0;
	private int toUp = 0;
	
	private long mDownTime;
	
	private int mLastAction = -1;
	private final int SENSITIVE = 300;  //鼠标双击的灵敏度，可以提供给用户设置
	private final int XYOFDOUBLE = 40; 
	private final int SEND_TOUCH = 65;
	private float middle_Y = 0;
	
	private boolean mIsHanding = false;
	
	private Handler mHandler;
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			move(mSendX, mSendY);
			mIsHanding = false;
		}
	};
	
	@Override
	protected void setlayout() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.layout_mouse);
		this.item_current = MenuUtil.ITEM_MOUSE;
		this.mLayoutID = R.id.linear_mouse;
		this.mID = R.id.btn_ChangeToMouse;
	}
	@Override
	protected void init() {
		mHandler = new Handler();
		btn_left = (Button) findViewById(R.id.btn_mouse_left);
		btn_right = (Button) findViewById(R.id.btn_mouse_right);
		btn_middle = (Button) findViewById(R.id.btn_mouse_middle);
		btn_move = (Button) findViewById(R.id.btn_move);
		btn_touch = (Button) findViewById(R.id.btn_touch);
		startDrag = (Button) findViewById(R.id.btn_startDrag);
		endDrag = (Button) findViewById(R.id.btn_endDrag);
		//鼠标移动 或拖动
		btn_move.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					checkDoubleClick((int) event.getX(), (int) event.getY());
					mDownTime = System.currentTimeMillis();
					mDownX = (int) event.getX();
					mDownY = (int) event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					int addX = (int) event.getX() - mDownX;
					int addY = (int) event.getY() - mDownY;
					mDownX = (int) event.getX();
					mDownY = (int) event.getY();
					if(mLastAction == MotionEvent.ACTION_DOWN && 
							(System.currentTimeMillis() - mDownTime) >= 1000){
						mIsTouch = true;
						btn_move.setVisibility(View.GONE);
						btn_touch.setVisibility(View.VISIBLE);
						leftClick(MotionEvent.ACTION_DOWN);
					}
					if(mIsTouch){
						mTouchX += addX;
						mTouchY += addY;
						if(!mIsHanding){
							mIsHanding = true;
							mSendX = mTouchX;
							mSendY = mTouchY;
							mHandler.postDelayed(mRunnable, SEND_TOUCH);
							mTouchX = 0;
							mTouchY = 0;
						}
					} else {
						move(addX, addY);
					}
					if(Math.abs(addX) <= 3 && Math.abs(addY) <= 3){
						return false;
					}
					break;	
				case MotionEvent.ACTION_UP:
					if(mIsTouch){
						mHandler.removeCallbacks(mRunnable);
						move(mTouchX, mTouchY);
						mIsHanding = false;
						mIsTouch = false;
						btn_move.setVisibility(View.VISIBLE);
						btn_touch.setVisibility(View.GONE);
						leftClick(MotionEvent.ACTION_UP);
					}
					break;	
				default:
					break;
				}
				mLastAction = event.getAction();
				return false;
			}
		});
		
		//鼠标拖动
		btn_touch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						mIsDown = true;
						mDownX = (int) event.getX();
						mDownY = (int) event.getY();
						leftClick(MotionEvent.ACTION_DOWN);
						break;
					case MotionEvent.ACTION_UP:
						mIsDown = false;
						mHandler.removeCallbacks(mRunnable);
						move(mTouchX, mTouchY);
						leftClick(MotionEvent.ACTION_UP);
						mIsHanding = false;
						break;
					case MotionEvent.ACTION_MOVE:
						if(!mIsDown){
							leftClick(MotionEvent.ACTION_DOWN);
							mIsDown = true;
						}
						int addX = (int) event.getX() - mDownX;
						int addY = (int) event.getY() - mDownY;
						mDownX = (int) event.getX();
						mDownY = (int) event.getY();
						mTouchX += addX;
						mTouchY += addY;
						if(!mIsHanding){
							mIsHanding = true;
							mSendX = mTouchX;
							mSendY = mTouchY;
							mHandler.postDelayed(mRunnable, SEND_TOUCH);
							mTouchX = 0;
							mTouchY = 0;
						}
						break;
					default:
						break;
				}
				return false;
			}
		});
		//鼠标左键点击
		btn_left.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN ){
					leftClick(MotionEvent.ACTION_DOWN);
					return false;
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					leftClick(MotionEvent.ACTION_UP);
					return false;
				}
				return false;
			}
		});
		//鼠标右键点击
		btn_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mWriteUitl.write(new byte[]{EventType.EVENT_MOUSE_RIGHT , 1 , 0});
			}
		});
		//开始鼠标拖动
		startDrag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mIsTouch = true;
				startDrag.setVisibility(View.GONE);
				btn_move.setVisibility(View.GONE);
				btn_touch.setVisibility(View.VISIBLE);
			}
		});
		//结束鼠标拖动
		endDrag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mIsTouch = false;
				startDrag.setVisibility(View.VISIBLE);
				btn_move.setVisibility(View.VISIBLE);
				btn_touch.setVisibility(View.GONE);
			}
		});
		//鼠标滚轮
		btn_middle.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						if(isUp){
							middle_Y = event.getY();
							isUp = false;
						}
						break;
					case MotionEvent.ACTION_UP:
						toDown = 0;
						toUp = 0;
						isUp = true;
						break;
					case MotionEvent.ACTION_MOVE:
						if(event.getY() > middle_Y){
							if(toDown >= 4){
								toDown = 0;
							}
							if(toDown == 0){
								mWriteUitl.write(new byte[]{EventType.EVENT_KEYDOWN, 2,
										MotionEvent.ACTION_DOWN, KeyCode.KEYCODE_DOWN});
								mWriteUitl.write(new byte[]{EventType.EVENT_KEYDOWN, 2,
										MotionEvent.ACTION_UP , KeyCode.KEYCODE_DOWN});
							}
							toDown ++;
						}
						if(event.getY() < middle_Y){
							if(toUp >= 4){
								toUp = 0;
							}
							if(toUp == 0){
								mWriteUitl.write(new byte[]{EventType.EVENT_KEYDOWN, 2,
										MotionEvent.ACTION_DOWN, KeyCode.KEYCODE_UP});
								mWriteUitl.write(new byte[]{EventType.EVENT_KEYDOWN, 2,
										MotionEvent.ACTION_UP , KeyCode.KEYCODE_UP});
							}
							toUp ++;
						}
						middle_Y = event.getY();
						break;	
					default:
						break;
				}
				return false;
			}
		});
	}
	
	//鼠标左键点击动作生成byte数组
	private void leftClick(int action){
		mBytes = new byte[3];
		mBytes[0] = EventType.EVENT_MOUSE_LEFT;
		mBytes[1] = 1;
		mBytes[2] = (byte) action;
		mWriteUitl.write(mBytes);
	}
	//鼠标移动动作生成的byte数组
	private void move(int x, int y){
		mBytes = new byte[11];
		mBytes[1] = 9;
		mBytes[2] = MotionEvent.ACTION_MOVE;
		BytesMaker.int2bytes(x, mBytes, 3);
		BytesMaker.int2bytes(y, mBytes, 7);
		if(mIsTouch){
			mBytes[0] = EventType.EVENT_MOUSE_TOUCH;
			Log.i("MouseTouch:", "Touch");
		}else{
			mBytes[0] = EventType.EVENT_MOUSE_MOVE;
		}
		mWriteUitl.write(mBytes);
	}
	//检测是否为双击事件
	private void checkDoubleClick(int x, int y){
		if(!mIsTouch && mLastAction == MotionEvent.ACTION_UP
				&& (System.currentTimeMillis() - mDownTime) <= SENSITIVE
				&& Math.abs(x - mDownX) <= XYOFDOUBLE
				&& Math.abs(y - mDownY) <= XYOFDOUBLE ){
			leftClick(MotionEvent.ACTION_DOWN);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			leftClick(MotionEvent.ACTION_UP);
			mDownTime = 0;
		}
	}
}
