package sdmc.com.hometv;

import java.util.Timer;
import java.util.TimerTask;

import sdmc.com.views.ChangeButton;
import sdmc.com.views.MyButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.util.MenuUtil;
import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.WriteUitl;

public abstract class FatherActivity extends Activity {
	 
	ChangeButton btn_ChangeToController = null;
	ChangeButton btn_ChangeToController_small = null;
	ChangeButton btn_ChangeToKeyboard = null;
	ChangeButton btn_ChangeToMouse = null;
	ChangeButton btn_ChangeToSensor = null;
	
	private Button mShowMenuBtn;
	private PopupWindow mPopupWindow;
	private boolean mIsPopupCanShow = true;
	private boolean mIsHasMenu = true;
	
	protected MenuUtil mMenuUtil;
	protected WriteUitl mWriteUitl;
	protected int item_current;
	protected byte[] mBytes;;
	private Timer timer;
	
	protected boolean isShiftPressed = false;
	protected boolean isALTPressed = false;
	
	protected int mID;
	protected int mLayoutID;
	
	//初始化
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuUtil = new MenuUtil(this);
		mWriteUitl = new WriteUitl(this);
        setlayout();
        init();
        fatherInit();
	}
	//两个抽象方法
	//设置布局
	protected abstract void setlayout();
	//注册控件
	protected abstract void init();
	
	//确定键的监听
	protected void buttonOKRegister(int id, int keyCode){
		MyButton myButton = (MyButton) findViewById(id);
		myButton.setKeyCode(keyCode);
		myButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN 
						|| event.getAction() == MotionEvent.ACTION_UP){
					MyButton mbn = (MyButton) v;
					byte[] bytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
							(byte) event.getAction(), (byte) mbn.getKeyCode()};
					mWriteUitl.write(bytes);
				}
				return false;
			}
		});
	}
	
	//确定键的监听
	protected void buttonPowerRegister(int id, int keyCode){
		MyButton myButton = (MyButton) findViewById(id);
		myButton.setKeyCode(keyCode);
		myButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final MyButton mbn = (MyButton) v;
				AlertDialog.Builder builder = new AlertDialog.Builder(FatherActivity.this);
				builder.setMessage(R.string.power_notice);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mBytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
								MotionEvent.ACTION_DOWN, (byte) mbn.getKeyCode()};
						mWriteUitl.write(mBytes);
						
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						mBytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
								MotionEvent.ACTION_UP, (byte) mbn.getKeyCode()};
						mWriteUitl.write(mBytes);
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.create().show();
			}
		});
	}
	
	//添加按键监听
	protected MyButton buttonRegister(int id, int keyCode){
		MyButton myButton = (MyButton) findViewById(id);
		myButton.setKeyCode(keyCode);
		
		myButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					MyButton mbn = (MyButton) v;
					mBytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
							(byte) event.getAction(), (byte) mbn.getKeyCode()};
					mWriteUitl.write(mBytes);
					timer = new Timer();
					timer.schedule(new TimerTask(){
						
						@Override
						public void run() {
							mWriteUitl.write(mBytes);
						}
					}, 200, 200);
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					try{
						timer.cancel();
					}catch(IllegalStateException e){
						e.printStackTrace();
					}
					MyButton mbn = (MyButton) v;
					byte[] bytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
							(byte) event.getAction(), (byte) mbn.getKeyCode()};
					mWriteUitl.write(bytes);
				}
				return false;
			}
		});
		return myButton;
	}
	//添加跳转按钮的监听
	protected void changeButtonRegister(ChangeButton changeButton, int id, Class<?> target){
		changeButton = (ChangeButton) findViewById(id);
		changeButton.setTarget(target);
		changeButton.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
				ChangeButton cButon = (ChangeButton) v; 
				intentTo(cButon.getTarget());
			}
		});
	}
	//跳转
	public void intentTo(Class<?> target){
		mWriteUitl.intentTo(target);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN){
			if (mIsHasMenu && mPopupWindow.isShowing()){
				mIsPopupCanShow = false;
				mPopupWindow.dismiss();
			} else {
				mIsPopupCanShow = true;
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mIsHasMenu && mPopupWindow.isShowing()){
			mPopupWindow.dismiss();
			if (keyCode == KeyEvent.KEYCODE_BACK){
				return true;
			} 
		}
		return super.onKeyDown(keyCode, event);
	}
//	//按下退出键
//	@Override
//	public void onBackPressed() {
//		
//		mWriteUitl.intentTo(RemoteControlerGuideActivity.class);
//	}
	//按下手机上的MENU键后弹出的菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenuUtil.onCreateOptionsMenu(menu, item_current);
//		return super.onCreateOptionsMenu(menu);
		return false;
	}
	//按下菜单中的元素后对应的操作
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mMenuUtil.onOptionsItemSelected(item);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		if (mIsHasMenu && mPopupWindow.isShowing()){
			mPopupWindow.dismiss();
		}
		super.onDestroy();
	}
	
	private void fatherInit(){
		if (mIsHasMenu) {
			mShowMenuBtn = (Button) findViewById(R.id.btn_showmenu);
			mShowMenuBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mIsPopupCanShow){
						mPopupWindow.setAnimationStyle(R.style.AnimBottom);
						mPopupWindow.showAtLocation(findViewById(mLayoutID), 
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
						mShowMenuBtn.setBackgroundResource(R.drawable.menu_show);
					}
				}
			});
			initPopupWindow();
		}
	}
	
	private void initPopupWindow(){
		View layout = getLayoutInflater().inflate(R.layout.popup_change, null);
		mPopupWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				mShowMenuBtn.setBackgroundResource(R.drawable.menu_show);
			}
		});
//		popupWindowButtonRegister(layout, R.id.btn_ChangeToProgramList, ProgramListActivity.class);
		popupWindowButtonRegister(layout, R.id.btn_ChangeToController_small, SmallController.class);
		popupWindowButtonRegister(layout, R.id.btn_ChangeToKeyboard, MyKeyboard.class);
		popupWindowButtonRegister(layout, R.id.btn_ChangeToMouse, MouseTouch.class);
		popupWindowButtonRegister(layout, R.id.btn_ChangeToSensor, MySensorActivity.class);
		Button showButton = (Button) layout.findViewById(R.id.btn_pophide);
		showButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
//		if (!NetConnect.isBindDtv()) {
//			ChangeButton changeButton = (ChangeButton) layout.findViewById(R.id.btn_ChangeToProgramList);
//			changeButton.setVisibility(View.GONE);
//		}
	}
	
	private void popupWindowButtonRegister(View layout, int id, Class<?> target){
		
		ChangeButton changeButton = (ChangeButton) layout.findViewById(id);
		changeButton.setTarget(target);
		changeButton.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if (v.getId() == mID){
					mPopupWindow.dismiss();
				} else {
					ChangeButton cButon = (ChangeButton) v; 
					intentTo(cButon.getTarget());
					mPopupWindow.dismiss();
				}
			}
		});
	}
	
	public void setIsHasMenu(boolean isHasMenu) {
		mIsHasMenu = isHasMenu;
	}
}
