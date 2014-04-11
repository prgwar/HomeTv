package sdmc.com.hometv;

import hometv.remote.socketUtil.Constant;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.stat.Screen;
import com.sdmc.phone.util.BytesMaker;
import com.sdmc.phone.util.MenuUtil;
import com.sdmc.phone.util.PreferencesVisiter;

public class MySensorActivity extends FatherActivity implements SensorEventListener{
	
	private SensorManager mSensorManager;
	private Button btn_screen;
	private Screen mScreen;
	private byte[] bytes = new byte[11];
	private boolean isfirst = true;
	private int motionCount = 0;
	private float[] motionsX = new float[3];
	private float[] motionsY = new float[3];
	
	private TextView mTextAccelerometer;
	private TextView mTextOrientaion;
//	private LinearLayout mHelpLayout;
	
	private boolean mAccelerometer;
	private boolean mOrientaion;

	@Override
	protected void setlayout() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.layout_sensor);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mScreen = new Screen(this);
		this.item_current = MenuUtil.ITEM_SENSOR;
		this.mLayoutID = R.id.linear_sensor;
		this.mID = R.id.btn_ChangeToSensor;
	}

	@Override
	protected void init() {
		btn_screen = (Button) findViewById(R.id.btn_sensor_screen);
		btn_screen.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				
				float x = mScreen.castX(event.getX());
				float y = mScreen.castY(event.getY());
				
				switch(event.getAction()){
					
					case MotionEvent.ACTION_DOWN:
						if(isfirst){
							sendTouch(event.getAction(), x, y);
							isfirst = false;
						}
						break;
					case MotionEvent.ACTION_MOVE:
						motionsX[motionCount] = x;
						motionsY[motionCount] = y;
						if(motionCount == 2){
							motionCount = 0;
							break;
						}
						if(motionCount == 0){
							sendTouch(event.getAction(), x, y);
						}
						motionCount ++;
						break;
					case MotionEvent.ACTION_UP:
						if(motionCount != 0){
							sendTouch(MotionEvent.ACTION_MOVE, motionsX[motionCount], motionsY[motionCount]);
						}
						sendTouch(event.getAction(), x, y);
						isfirst = true;
						motionCount = 0;
						break;
					default:
						break;
				}
				return false;
			}
		});
//		mHelpLayout = (LinearLayout) findViewById(R.id.layout_sensor_help);
		mTextAccelerometer = (TextView) findViewById(R.id.text_sensor_accelerometer);
		mTextOrientaion = (TextView) findViewById(R.id.text_sensor_orientaion);
	}

	private void sendTouch(int action, float x, float y){
		bytes[0] = EventType.EVENT_GRAVITY_TOUCH;
		bytes[1] = 9;
		bytes[2] = (byte) action;
		BytesMaker.float2bytes(x ,bytes ,3);
		BytesMaker.float2bytes(y ,bytes ,7);
		mWriteUitl.write(bytes);
	}
	
	private void registerSensor(){
		//注册加速度传感器
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		//注册方向传感器
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onStart() {
		registerSensor();
		PreferencesVisiter setting = PreferencesVisiter.getVisiter();
		mAccelerometer = setting.getEnableState(Constant.ENABLE_SENSOR_SPEED);

		mOrientaion = setting.readOrientaion();
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		event.accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
		int sensorType = event.sensor.getType();
		Log.i("info"," onSensorChanged -- > sensorType： "+sensorType +" mAccelerometer= "+mAccelerometer);
		switch(sensorType){
			//加速度传感器
			case Sensor.TYPE_ACCELEROMETER:
//				if(mAccelerometer){
					send(sensorType, event.values);
//				}
				break;
			//方向传感器	
				 
			case Sensor.TYPE_ORIENTATION: //3
				if(mOrientaion){
					send(sensorType, event.values);
				}
				break;
		}
	}
	//[数] 精确度，准确性
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		 
		
	}
	//发送信息
	private void send(int type, float[] value){
		
		mBytes = new byte[18];
		mBytes[0] = EventType.EVENT_GRAVITY;
		mBytes[1] = 16;
		BytesMaker.int2bytes_sensor(type ,mBytes ,5);
		float xValue = value[0];
		float yValue = value[1];
		float zValue = value[2];
//		Log.w("info","  sensor x -- "+ xValue +" y-- "+yValue +" z--"+zValue);
		BytesMaker.float2bytes(xValue ,mBytes ,6);
		BytesMaker.float2bytes(yValue ,mBytes ,10);
		BytesMaker.float2bytes(zValue ,mBytes ,14);
		mWriteUitl.write(mBytes);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
//		mHelpLayout.setVisibility(View.VISIBLE);
		if(mAccelerometer){
			mTextAccelerometer.setText(R.string.on);
		} else {
			mTextAccelerometer.setText(R.string.off);
		}
		if(mOrientaion){
			mTextOrientaion.setText(R.string.on);
		} else {
			mTextOrientaion.setText(R.string.off);
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		mHelpLayout.setVisibility(View.VISIBLE);
		if(mAccelerometer){
			mTextAccelerometer.setText(R.string.on);
		} else {
			mTextAccelerometer.setText(R.string.off);
		}
		if(mOrientaion){
			mTextOrientaion.setText(R.string.on);
		} else {
			mTextOrientaion.setText(R.string.off);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
//		mHelpLayout.setVisibility(View.GONE);
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus){
//			mHelpLayout.setVisibility(View.GONE);
		}
		super.onWindowFocusChanged(hasFocus);
	}
}
