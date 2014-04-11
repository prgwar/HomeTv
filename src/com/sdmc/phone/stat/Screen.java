package com.sdmc.phone.stat;

import android.app.Activity;
import android.view.Display;

public class Screen {
	
	private Activity mActivity;
	private int width;
	private int hight;
	private final int TVWidth = 1280;
	private final int TVHight = 720;
	
	public Screen(Activity activity){
		this.mActivity = activity;
		getScreen();
	}
	
	private void getScreen(){
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		hight = display.getHeight() * 8 / 9;
	}
	
	public float castX(float x){
		return x * TVWidth / width;
	}
	
	public float castY(float y){
		return y * TVHight / hight;
	}
}
