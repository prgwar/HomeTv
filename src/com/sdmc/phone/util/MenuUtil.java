package com.sdmc.phone.util;

import sdmc.com.hometv.FatherActivity;
import sdmc.com.hometv.MouseTouch;
import sdmc.com.hometv.MyKeyboard;
import sdmc.com.hometv.MySensorActivity;
import sdmc.com.hometv.R;
import sdmc.com.hometv.SmallController;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

//显示Menu菜单的工具类
public class MenuUtil {
	
	private final int ITEM_SETTING = 0x110;
	private final int ITEM_CLOSE = 0x130;
	public static final int ITEM_CONTROLLER = 0x121;
	public static final int ITEM_CONTROLLER_SMALL = 0x122;
	public static final int ITEM_MOUSE = 0x123;
	public static final int ITEM_KEYBOARD = 0x124;
	public static final int ITEM_SENSOR = 0x125;
	public static final int ITEM_PROGRAMLIST = 0x127;
	
	public static boolean check_ACCELEROMETER = false;
	public static boolean check_ORIENTATION = true;
	public static boolean check_help = true;
	
	private PreferencesVisiter setting = null;
	private FatherActivity mActivity = null;
	private int item_current;
	private boolean[] bools;
	
	public MenuUtil(Activity activity){
		this.mActivity = (FatherActivity) activity;
		setting = PreferencesVisiter.getVisiter();
	}
	
	//创造menu菜单
	public void onCreateOptionsMenu(Menu menu , int item_current){
		this.item_current = item_current;
//		menu.add(0, ITEM_SETTING, 0, R.string.setting);
		SubMenu change = menu.addSubMenu(R.string.change);
		menu.add(0, ITEM_CLOSE, 0, R.string.close);
		change.setHeaderTitle(R.string.changeActivity);
//		if (NetConnect.isBindDtv()) {
//			change.add(0, ITEM_PROGRAMLIST, 0, R.string.programlist);
//		}
		change.add(0, ITEM_CONTROLLER_SMALL, 0, R.string.controller_small);
		change.add(0, ITEM_KEYBOARD, 0, R.string.keyboard);
		change.add(0, ITEM_MOUSE, 0, R.string.mouse);
		change.add(0, ITEM_SENSOR, 0, R.string.sensor);
		change.removeItem(item_current);
	}
	//菜单项被单击后的回调方法
	public void onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		    case ITEM_SETTING:
		    	intentToSetting();
		    	break;
		    case ITEM_PROGRAMLIST:
//		    	mActivity.intentTo(ProgramListActivity.class);
		    	break;
		    case ITEM_CONTROLLER:
//		    	mActivity.intentTo(MainController.class);
		    	break;
		    case ITEM_CONTROLLER_SMALL:
		    	mActivity.intentTo(SmallController.class);
		    	break;
		    case ITEM_MOUSE:
		    	mActivity.intentTo(MouseTouch.class);
		    	break;
		    case ITEM_KEYBOARD:
		    	mActivity.intentTo(MyKeyboard.class);
		    	break;	
		    case ITEM_SENSOR:
		    	mActivity.intentTo(MySensorActivity.class);
		    	break;
		    case ITEM_CLOSE:
		    	close();
		    	break;
		    default:
		    	break;
		}
	}
	
	private void intentToSetting(){
//		Intent intent = new Intent(mActivity, SettingActivity.class);
//		mActivity.startActivity(intent);
	}
	
	private void close(){
		NetConnect.sendEnd();
    	NetConnect.close();
    	mActivity.finish();
    	System.exit(0);
	}
	
	/*private void showSettingDialog(){
		if(mIsDLNA){
	    	mDLNAActivity.showNameDialog();
		} else {
			settingDialog().show();
		}
		settingDialog().show();
	}*/
	
	//生成设置对话框
	private AlertDialog settingDialog(){
		Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(R.string.setting);
		String[] names;
		if (item_current == ITEM_SENSOR){
			names = new String[]{mActivity.getResources().getText(R.string.accelerometer).toString(),
					mActivity.getResources().getText(R.string.orientaion).toString(),
					mActivity.getResources().getText(R.string.help).toString()};
			bools = new boolean[]{check_ACCELEROMETER, check_ORIENTATION, check_help}; 
		} else {
			names = new String[]{mActivity.getResources().getText(R.string.remenberIp).toString(),
					mActivity.getResources().getText(R.string.autologin).toString()};
			bools = new boolean[]{setting.readRecord(), setting.readAuto()}; 
		}
		
		builder.setMultiChoiceItems(names, bools, new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (item_current == ITEM_SENSOR){
					bools[which] = isChecked;
				} else {
					switch (which){ 
						case 0:
							setting.writeRecord(isChecked);
							break;
						case 1:
							setting.writeAuto(isChecked);
							break;
					}
				}
			}
		});
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				if(item_current == ITEM_SENSOR){
					check_ACCELEROMETER = bools[0];
					check_ORIENTATION = bools[1];
					check_help = bools[2];
				} else {
					setting.commit();
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	
	/*//确认退出程序对话框
	private AlertDialog backDialogMake(){
		Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(R.string.close);
		builder.setMessage(R.string.close_info);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				NetConnect.sendEnd();
				NetConnect.close();
				mActivity.finish();
			}
		});
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}*/
}
