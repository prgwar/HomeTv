package sdmc.com.hometv;

import com.sdmc.phone.stat.KeyCode;
import com.sdmc.phone.util.MenuUtil;

import android.content.pm.ActivityInfo;
import android.view.KeyEvent;

/*
 * ¼òÒ×Ò£¿ØÆ÷½çÃæ
 */

public class SmallController extends FatherActivity{
	
	@Override
	protected void setlayout() {
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.layout_controller);
		this.item_current = MenuUtil.ITEM_CONTROLLER_SMALL;
		this.mLayoutID = R.id.linear_controller;
		this.mID = R.id.btn_ChangeToController_small;
	}
	@Override
	protected void init() {
		
		buttonOKRegister(R.id.btn_OK, KeyCode.KEYCODE_OK);
		buttonPowerRegister(R.id.btn_POWER, KeyCode.KEYCODE_POWER);
		buttonRegister(R.id.btn_MUTE, KeyCode.KEYCODE_MUTE);
		buttonRegister(R.id.btn_HOME, KeyCode.KEYCODE_HOME);
		buttonRegister(R.id.btn_MENU, KeyCode.KEYCODE_MENU);
		buttonRegister(R.id.btn_EXIT, KeyCode.KEYCODE_EXIT);
		buttonRegister(R.id.btn_LEFT, KeyCode.KEYCODE_LEFT);
		buttonRegister(R.id.btn_RIGHT, KeyCode.KEYCODE_RIGHT);
		buttonRegister(R.id.btn_UP, KeyCode.KEYCODE_UP);
		buttonRegister(R.id.btn_DOWN, KeyCode.KEYCODE_DOWN);
		buttonRegister(R.id.btn_volUp, KeyCode.KEYCODE_VOL_UP);
		buttonRegister(R.id.btn_volDown, KeyCode.KEYCODE_VOL_DOWN);
		buttonRegister(R.id.btn_playOrpause, KeyCode.KEYCODE_PAUSE);
		buttonRegister(R.id.btn_FB, KeyCode.KEYCODE_FB);
		buttonRegister(R.id.btn_FF, KeyCode.KEYCODE_FF);
	}
}
