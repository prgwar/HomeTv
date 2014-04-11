package sdmc.com.hometv;

import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.PreferencesVisiter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * ң�ؿ��Ʋ˵�����
 * @author fee
 *
 */
public class RemoteControlerGuideActivity extends Activity {
public final static String IS_BIND_DTV = "isBindDtv";
	
	private final static int ITEM_SETTING = 0x101;
	private final static int ITEM_CLOSE = 0x102;
	
	private final static int INDEX_CONTROL = 0;
	private final static int INDEX_KEYBOARD = 1;
	private final static int INDEX_MOUSE = 2;
	private final static int INDEX_SENSOR = 3;
	
	private final static int MAX_BUTTONS = 4;
	
	private final static int[] LAYOUT_IDS = new int[]{R.id.linear_1, R.id.linear_2, 
			R.id.linear_3, R.id.linear_4, R.id.linear_5, R.id.linear_6};
	/**
	 * ��ţ�
	 * 0 �ֻ�ң�أ�1 ���̣�2 ��ꣻ3 ��Ϸ���ƣ�
	 */
	private final static int[] BG_IDS = new int[]{  
		R.drawable.press_guide_controller, R.drawable.press_guide_keyboard, 
		R.drawable.press_guide_mouse, R.drawable.press_guide_game};
	private final static int[] TEXT_IDS = new int[]{ R.string.controller, 
		R.string.keyboard, R.string.mouse, R.string.sensor};
	
	private IntentView[] mIntentViews;
	private ButtonIntent[] mButtonIntents;
	
	private TextView tvHeadBack;
	private TextView tvHeadTab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_controller_layout);
		init();
	}
	
	private void init(){
		mIntentViews = new IntentView[MAX_BUTTONS];
		mButtonIntents = new ButtonIntent[MAX_BUTTONS];
		for (int i = 0; i < MAX_BUTTONS; i ++) {
			mIntentViews[i] = new IntentView((LinearLayout) findViewById(LAYOUT_IDS[i]));
			mButtonIntents[i] = new ButtonIntent(BG_IDS[i], TEXT_IDS[i]);
		}
	 
		mButtonIntents[INDEX_CONTROL].setButtonAction(new ButtonAction() {
			@Override
			public void Action() {
				intentTo(SmallController.class);
			}
		});
		mButtonIntents[INDEX_KEYBOARD].setButtonAction(new ButtonAction() {
	
			@Override
			public void Action() {
				intentTo(MyKeyboard.class);
			}
		});
		mButtonIntents[INDEX_MOUSE].setButtonAction(new ButtonAction() {
			
			@Override
			public void Action() {
				intentTo(MouseTouch.class);
			}
		});
		mButtonIntents[INDEX_SENSOR].setButtonAction(new ButtonAction() {
			
			@Override
			public void Action() {
				intentTo(MySensorActivity.class);
			}
		});
		for (int i = 0; i < MAX_BUTTONS; i++) {
			mIntentViews[i].setIntent(mButtonIntents[i]);
		}
//		if (NetConnect.isBindDtv()) {
//			for (int i = 0; i < MAX_BUTTONS; i++) {
//				mIntentViews[i].setIntent(mButtonIntents[i]);
//			}
//		} else {
//			for (int i = 0; i < MAX_BUTTONS - 1; i++) {
//				mIntentViews[i].setIntent(mButtonIntents[i + 1]);
//			}
//			mIntentViews[mIntentViews.length - 1].mLayout.setVisibility(View.INVISIBLE);
//		}
		tvHeadBack = (TextView) findViewById(R.id.head_back);
		tvHeadBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RemoteControlerGuideActivity.this.finish();
			}
		});
		tvHeadTab = (TextView) findViewById(R.id.head_current_tab);
		tvHeadTab .setText(R.string.remote_control_guide);
	}
	
	private void intentTo(Class<?> target){
		Intent intent = new Intent(this, target);
		startActivityForResult(intent, 800);
	}
	
	//�����ֻ��ϵ�MENU���󵯳��Ĳ˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, ITEM_SETTING, 0, R.string.setting);
//		menu.add(0, ITEM_CLOSE, 0, R.string.close);
		return super.onCreateOptionsMenu(menu);
	}
	//���²˵��е�Ԫ�غ��Ӧ�Ĳ���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case ITEM_SETTING:
				settingDialog().show();
				break;
		    case ITEM_CLOSE:
		    	NetConnect.close();
		    	this.finish();
		    	System.exit(0);
		    	break;
		    default:
		    	break;
		}
		return super.onOptionsItemSelected(item);
	}
	//�������öԻ���
	private AlertDialog settingDialog(){
		final PreferencesVisiter setting = PreferencesVisiter.getVisiter();
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.setting);
		String[] names = new String[]{getResources().getText(R.string.remenberIp).toString(),
					getResources().getText(R.string.autologin).toString()};
		boolean[] bools = new boolean[]{setting.readRecord(), setting.readAuto()};
		
		builder.setMultiChoiceItems(names, bools, new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				switch (which){ 
					case 0:
						setting.writeRecord(isChecked);
						break;
					case 1:
						setting.writeAuto(isChecked);
						break;
				}
			}
		});
		
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				setting.commit();
			}
		});
		
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	@Override
	public void onBackPressed() {
		 finish();
	}
	//ȷ���˳�����Ի���
	private AlertDialog backDialogMake(){
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.close);
		builder.setMessage(R.string.close_info);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				NetConnect.sendEnd();
				NetConnect.close();
//				GuideActivity.this.finish();
//				System.exit(0);
			}
		});
        builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}
	/**
	 * ��ʼ��ÿ��Linearlayout��Button��TextView
	 *
	 */
	private class IntentView {
		
		private static final int CHILD_BUTTON = 0;
		private static final int CHILD_TEXTVIEW = 1;
		
		private LinearLayout mLayout;
		private Button mBtn;
		private TextView mTextView;
		private ButtonIntent mButtonIntent;
		IntentView(LinearLayout layout) {
			mLayout = layout;
			mBtn = (Button) mLayout.getChildAt(CHILD_BUTTON);
			mTextView = (TextView) mLayout.getChildAt(CHILD_TEXTVIEW);
		}
		/**
		 * ��Button���ð�������--> ���°���Ȼ�� ���� ButtonAction.Actionʵ����ת
		 * �Լ���ButtonIntent�ж�Ӧ����ı������ַ�����Դ���ø���ť��TextView
		 * @param buttonIntent
		 */
		private void setIntent(ButtonIntent buttonIntent) {
			mButtonIntent = buttonIntent;
			mBtn.setBackgroundResource(mButtonIntent.mBgId);
			mTextView.setText(mButtonIntent.mTextId);
			mBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mButtonIntent.mButtonAction.Action();
				}
			});
		}
	}
	/**
	 * ���水ť�ı�����TextView���ַ�����Դ����
	 *
	 */
	private class ButtonIntent {
		private int mBgId;
		private int mTextId;
		private ButtonAction mButtonAction;
		ButtonIntent(int bgId, int textId) {
			mBgId = bgId;
			mTextId = textId;
		}
		
		void setButtonAction(ButtonAction buttonAction) {
			mButtonAction = buttonAction;
		}
	}
	
	private interface ButtonAction {
		void Action();
	}
}
