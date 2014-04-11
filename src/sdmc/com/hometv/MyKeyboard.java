package sdmc.com.hometv;

import java.util.Timer;
import java.util.TimerTask;

import sdmc.com.views.MyButton;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.stat.KeyCode;
import com.sdmc.phone.util.MenuUtil;

import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
/*
 * ¼üÅÌ½çÃæ
 */
public class MyKeyboard extends FatherActivity{
	
	//×ÖÄ¸¼ü
	private MyButton btn_A;
	private MyButton btn_B;
	private MyButton btn_C;
	private MyButton btn_D;
	private MyButton btn_E;
	private MyButton btn_F;
	private MyButton btn_G;
	private MyButton btn_H;
	private MyButton btn_I;
	private MyButton btn_J;
	private MyButton btn_K;
	private MyButton btn_L;
	private MyButton btn_M;
	private MyButton btn_N;
	private MyButton btn_O;
	private MyButton btn_P;
	private MyButton btn_Q;
	private MyButton btn_R;
	private MyButton btn_S;
	private MyButton btn_T;
	private MyButton btn_U;
	private MyButton btn_V;
	private MyButton btn_W;
	private MyButton btn_X;
	private MyButton btn_Y;
	private MyButton btn_Z;
	//Êý×Ö¼ü
	private MyButton btn_1;
	private MyButton btn_2;
	private MyButton btn_3;
	private MyButton btn_4;
	private MyButton btn_5;
	private MyButton btn_6;
	private MyButton btn_7;
	private MyButton btn_8;
	private MyButton btn_9;
	private MyButton btn_0;
	//ÌØÊâ×Ö·û°´¼ü
	private MyButton btn_GRAVE;
	private MyButton btn_LEFT_BRACKET;
	private MyButton btn_RIGHT_BRACKET;
	private MyButton btn_SEMICOLON;
	private MyButton btn_APOSTROPHE;
	private MyButton btn_SLASH;
	private MyButton btn_MINUS;
	private MyButton btn_EQUALS;
	private MyButton btn_BACKSLASH;
	private MyButton btn_COMMA;	
	private MyButton btn_PERIOD;
	
	
	private LinearLayout mLetterLayout1;
	private LinearLayout mLetterLayout2;
	private LinearLayout mLetterLayout3;
	private LinearLayout mNumLayout3;
	
	private MyButton btn_LETTER;
	private MyButton btn_NUMBER;
	
	private boolean mIsShiftOn;
	private Timer timer;
	
	@Override
	protected void setlayout() {
		//ÉèÖÃÎªºáÆÁ
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.layout_keyboard);
		this.item_current = MenuUtil.ITEM_KEYBOARD;
		this.mLayoutID = R.id.linear_keyboard;
		this.mID = R.id.btn_ChangeToKeyboard;
	}
	@Override
	protected void init() {
		buttonOKRegister(R.id.btn_OK, KeyCode.KEYCODE_OK);
		buttonShiftRegister(R.id.btn_SHIFT, KeyCode.KEYCODE_SHIFT);
		buttonDelRegister(R.id.btn_DEL, KeyCode.KEYCODE_DEL);
		
		btn_A = buttonRegister(R.id.btn_A, KeyCode.KEYCODE_A);
		btn_B = buttonRegister(R.id.btn_B, KeyCode.KEYCODE_B);
		btn_C = buttonRegister(R.id.btn_C, KeyCode.KEYCODE_C);
		btn_D = buttonRegister(R.id.btn_D, KeyCode.KEYCODE_D);
		btn_E = buttonRegister(R.id.btn_E, KeyCode.KEYCODE_E);
		btn_F = buttonRegister(R.id.btn_F, KeyCode.KEYCODE_F);
		btn_G = buttonRegister(R.id.btn_G, KeyCode.KEYCODE_G);
		btn_H = buttonRegister(R.id.btn_H, KeyCode.KEYCODE_H);
		btn_I = buttonRegister(R.id.btn_I, KeyCode.KEYCODE_I);
		btn_J = buttonRegister(R.id.btn_J, KeyCode.KEYCODE_J);
		btn_K = buttonRegister(R.id.btn_K, KeyCode.KEYCODE_K);
		btn_L = buttonRegister(R.id.btn_L, KeyCode.KEYCODE_L);
		btn_M = buttonRegister(R.id.btn_M, KeyCode.KEYCODE_M);
		btn_N = buttonRegister(R.id.btn_N, KeyCode.KEYCODE_N);
		btn_O = buttonRegister(R.id.btn_O, KeyCode.KEYCODE_O);
		btn_P = buttonRegister(R.id.btn_P, KeyCode.KEYCODE_P);
		btn_Q = buttonRegister(R.id.btn_Q, KeyCode.KEYCODE_Q);
		btn_R = buttonRegister(R.id.btn_R, KeyCode.KEYCODE_R);
		btn_S = buttonRegister(R.id.btn_S, KeyCode.KEYCODE_S);
		btn_T = buttonRegister(R.id.btn_T, KeyCode.KEYCODE_T);
		btn_U = buttonRegister(R.id.btn_U, KeyCode.KEYCODE_U);
		btn_V = buttonRegister(R.id.btn_V, KeyCode.KEYCODE_V);
		btn_W = buttonRegister(R.id.btn_W, KeyCode.KEYCODE_W);
		btn_X = buttonRegister(R.id.btn_X, KeyCode.KEYCODE_X);
		btn_Y = buttonRegister(R.id.btn_Y, KeyCode.KEYCODE_Y);
		btn_Z = buttonRegister(R.id.btn_Z, KeyCode.KEYCODE_Z);
		btn_1 = buttonRegister(R.id.btn_1, KeyCode.KEYCODE_NUM_1);
		btn_2 = buttonRegister(R.id.btn_2, KeyCode.KEYCODE_NUM_2);
		btn_3 = buttonRegister(R.id.btn_3, KeyCode.KEYCODE_NUM_3);
		btn_4 = buttonRegister(R.id.btn_4, KeyCode.KEYCODE_NUM_4);
		btn_5 = buttonRegister(R.id.btn_5, KeyCode.KEYCODE_NUM_5);
		btn_6 = buttonRegister(R.id.btn_6, KeyCode.KEYCODE_NUM_6);
		btn_7 = buttonRegister(R.id.btn_7, KeyCode.KEYCODE_NUM_7);
		btn_8 = buttonRegister(R.id.btn_8, KeyCode.KEYCODE_NUM_8);
		btn_9 = buttonRegister(R.id.btn_9, KeyCode.KEYCODE_NUM_9);
		btn_0 = buttonRegister(R.id.btn_0, KeyCode.KEYCODE_NUM_0);
		btn_COMMA = buttonRegister(R.id.btn_COMMA, KeyCode.KEYCODE_COMMA);
		btn_PERIOD = buttonRegister(R.id.btn_PERIOD, KeyCode.KEYCODE_PERIOD);
		btn_GRAVE = buttonRegister(R.id.btn_GRAVE, KeyCode.KEYCODE_GRAVE);
		btn_MINUS = buttonRegister(R.id.btn_MINUS, KeyCode.KEYCODE_MINUS);
		btn_EQUALS = buttonRegister(R.id.btn_EQUALS, KeyCode.KEYCODE_EQUALS);
		btn_BACKSLASH = buttonRegister(R.id.btn_BACKSLASH, KeyCode.KEYCODE_BACKSLASH);
		btn_LEFT_BRACKET = buttonRegister(R.id.btn_LEFT_BRACKET, KeyCode.KEYCODE_LEFT_BRACKET);
		btn_RIGHT_BRACKET = buttonRegister(R.id.btn_RIGHT_BRACKET, KeyCode.KEYCODE_RIGHT_BRACKET);
		btn_SEMICOLON = buttonRegister(R.id.btn_SEMICOLON, KeyCode.KEYCODE_SEMICOLON);
		btn_APOSTROPHE = buttonRegister(R.id.btn_APOSTROPHE, KeyCode.KEYCODE_APOSTROPHE);
		btn_SLASH = buttonRegister(R.id.btn_SLASH , KeyCode.KEYCODE_SLASH);
		
		buttonRegister(R.id.btn_AT, KeyCode.KEYCODE_AT);
		buttonRegister(R.id.btn_PLUS, KeyCode.KEYCODE_PLUS);
		buttonRegister(R.id.btn_POUND, KeyCode.KEYCODE_POUND);
		buttonRegister(R.id.btn_STAR, KeyCode.KEYCODE_STAR);
		
		buttonRegister(R.id.btn_EXIT, KeyCode.KEYCODE_EXIT);
		buttonRegister(R.id.btn_ALT, KeyCode.KEYCODE_ALT);
		buttonRegister(R.id.btn_LEFT, KeyCode.KEYCODE_LEFT);
		buttonRegister(R.id.btn_RIGHT, KeyCode.KEYCODE_RIGHT);
		buttonRegister(R.id.btn_UP, KeyCode.KEYCODE_UP);
		buttonRegister(R.id.btn_DOWN, KeyCode.KEYCODE_DOWN);
		buttonRegister(R.id.btn_SPACE, KeyCode.KEYCODE_SPACE);
		buttonRegister(R.id.btn_ENTER, KeyCode.KEYCODE_ENTER);
		buttonRegister(R.id.btn_SYM, KeyCode.KEYCODE_SYM);
		buttonRegister(R.id.btn_TAB, KeyCode.KEYCODE_TAB);
		
		letterToNumber();
	}
	//¼üÅÌÔÚ×ÖÄ¸Ãæ°åºÍÊý×ÖÃæ°å¼äÇÐ»»
	private void letterToNumber(){
		mLetterLayout1 = (LinearLayout) findViewById(R.id.row_letter1);
		mLetterLayout2 = (LinearLayout) findViewById(R.id.row_letter2);
		mLetterLayout3 = (LinearLayout) findViewById(R.id.row_letter3);
		
		mNumLayout3 = (LinearLayout) findViewById(R.id.row_num3);
		
		btn_LETTER = (MyButton) findViewById(R.id.btn_LETTER);
		btn_NUMBER = (MyButton) findViewById(R.id.btn_NUMBER);
		btn_LETTER.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setVisibility(View.VISIBLE);
				mNumLayout3.setVisibility(View.GONE);
			}
		});
		btn_NUMBER.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setVisibility(View.GONE);
				mNumLayout3.setVisibility(View.VISIBLE);
			}
		});
	}
	
	private void setVisibility(int visible){
		mLetterLayout1.setVisibility(visible);
		mLetterLayout2.setVisibility(visible);
		mLetterLayout3.setVisibility(visible);
		btn_NUMBER.setVisibility(visible);
	}
	
	private MyButton buttonDelRegister(int id, int keyCode){
		MyButton myButton = (MyButton) findViewById(id);
		myButton.setKeyCode(keyCode);
		
		myButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					MyButton mbn = (MyButton) v;
					if(mIsShiftOn){
						sendShiftOff();
					}
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
				if (event.getAction() == MotionEvent.ACTION_UP){
					try {
						timer.cancel();
					} catch (IllegalStateException e){
						e.printStackTrace();
					}
					MyButton mbn = (MyButton) v;
					byte[] bytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
							(byte) event.getAction(), (byte) mbn.getKeyCode()};
					mWriteUitl.write(bytes);
					if(mIsShiftOn){
						sendShiftOn();
					}
				}
				return false;
			}
		});
		
		return myButton;
	}
	
	private void sendShiftOn(){
		byte[] bytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
				MotionEvent.ACTION_DOWN, KeyCode.KEYCODE_SHIFT};
		mWriteUitl.write(bytes);
		bytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
				MotionEvent.ACTION_MOVE, KeyCode.KEYCODE_SHIFT};
		mWriteUitl.write(bytes);
	}
	
	private void sendShiftOff(){
		byte[] bytes = new byte[]{EventType.EVENT_KEYDOWN, 2 , 
				MotionEvent.ACTION_UP, KeyCode.KEYCODE_SHIFT};
		mWriteUitl.write(bytes);
	}
	
	private void buttonShiftRegister(int id, int keyCode){
		MyButton myButton = (MyButton) findViewById(id);
		myButton.setKeyCode(keyCode);
		myButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyButton mbn = (MyButton) v;
				if (mIsShiftOn){
					mIsShiftOn = false;
					mbn.setBackgroundResource(R.drawable.shift_off);
					sendShiftOff();
				} else {
					mIsShiftOn = true;
					mbn.setBackgroundResource(R.drawable.shift_on);
					sendShiftOn();
				}
				changeButtonText(mIsShiftOn);
			}
		});
	}
	
	private void changeButtonText(boolean isShiftOn){
		if (isShiftOn){
			btn_A.setText("A");
			btn_B.setText("B");
			btn_C.setText("C");
			btn_D.setText("D");
			btn_E.setText("E");
			btn_F.setText("F");
			btn_G.setText("G");
			btn_H.setText("H");
			btn_I.setText("I");
			btn_J.setText("J");
			btn_K.setText("K");
			btn_L.setText("L");
			btn_M.setText("M");
			btn_N.setText("N");
			btn_O.setText("O");
			btn_P.setText("P");
			btn_Q.setText("Q");
			btn_R.setText("R");
			btn_S.setText("S");
			btn_T.setText("T");
			btn_U.setText("U");
			btn_V.setText("V");
			btn_W.setText("W");
			btn_X.setText("X");
			btn_Y.setText("Y");
			btn_Z.setText("Z");
			btn_1.setText("!");
			btn_2.setText("@");
			btn_3.setText("#");
			btn_4.setText("$");
			btn_5.setText("%");
			btn_6.setText("^");
			btn_7.setText("&");
			btn_8.setText("*");
			btn_9.setText("(");
			btn_0.setText(")");
			btn_COMMA.setText("<");
			btn_PERIOD.setText(">");
			btn_GRAVE.setText("~");
			btn_MINUS.setText("_");
			btn_EQUALS.setText("+");
			btn_BACKSLASH.setText("|");
			btn_LEFT_BRACKET.setText("{");
			btn_RIGHT_BRACKET.setText("}");
			btn_SEMICOLON.setText(":");
			btn_APOSTROPHE.setText("\"");
			btn_SLASH.setText("?");
			
		} else {
			btn_A.setText("a");
			btn_B.setText("b");
			btn_C.setText("c");
			btn_D.setText("d");
			btn_E.setText("e");
			btn_F.setText("f");
			btn_G.setText("g");
			btn_H.setText("h");
			btn_I.setText("i");
			btn_J.setText("j");
			btn_K.setText("k");
			btn_L.setText("l");
			btn_M.setText("m");
			btn_N.setText("n");
			btn_O.setText("o");
			btn_P.setText("p");
			btn_Q.setText("q");
			btn_R.setText("r");
			btn_S.setText("s");
			btn_T.setText("t");
			btn_U.setText("u");
			btn_V.setText("v");
			btn_W.setText("w");
			btn_X.setText("x");
			btn_Y.setText("y");
			btn_Z.setText("z");
			btn_1.setText("1");
			btn_2.setText("2");
			btn_3.setText("3");
			btn_4.setText("4");
			btn_5.setText("5");
			btn_6.setText("6");
			btn_7.setText("7");
			btn_8.setText("8");
			btn_9.setText("9");
			btn_0.setText("0");
			btn_COMMA.setText(",");
			btn_PERIOD.setText(".");
			btn_GRAVE.setText("`");
			btn_MINUS.setText("-");
			btn_EQUALS.setText("=");
			btn_BACKSLASH.setText("\\");
			btn_LEFT_BRACKET.setText("[");
			btn_RIGHT_BRACKET.setText("]");
			btn_SEMICOLON.setText(";");
			btn_APOSTROPHE.setText("'");
			btn_SLASH.setText("/");
		}
	}
	@Override
	protected void onDestroy() {
		if (mIsShiftOn){
			sendShiftOff();
		}
		super.onDestroy();
	}
}
