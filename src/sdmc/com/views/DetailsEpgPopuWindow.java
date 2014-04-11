package sdmc.com.views;

import hometv.remote.bean.NetModeManager;
import hometv.remote.bean.NetProtecolInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import sdmc.com.hometv.R;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
/**
 * 详细EPG布局
 * @author fee
 *
 */
public class DetailsEpgPopuWindow extends PopupWindow {
	private Context curContext;
	private static final String TAG = "DetailsEpgPopuWindow";
	
	private static final int TIME_START = 1000;
	private static final int TIME_PERIOD = 2000;
	private static final int TIME_EPG_TIME_WAIT = 10; 
	private static final int TIME_EPG_LIST_WAIT = 30; 
	
	private static final String ITEM_TEXT = "item text";
	
	private static final int[] BUTTON_IDS = new int[]{
		R.id.btn_week1, 
		R.id.btn_week2, 
		R.id.btn_week3,
		R.id.btn_week4, 
		R.id.btn_week5,
		R.id.btn_week6,
		R.id.btn_week7 };
	private static final int[] WEEK_IDS = new int[]{
		R.string.sunday_s, 
		R.string.monday_s,
		R.string.tuesday_s,
		R.string.wednesday_s,
		R.string.thursday_s, 
		R.string.friday_s,
		R.string.saturday_s };
	private NetProtecolInterface curImp;
	private int mDay;
	private int mEPGtimeCount = 0;
	private int mEPGListCount = 0;
	private Button[] mWeekBtns;
	private ListView mListView;
	private ArrayList<String> mEPGList = new ArrayList<String>();
	private ProgressBar mProgressBar;
	private TextView tvNoDetailEpg;
	private Timer mTimer;
	/** 每两秒钟执行一次 **/
	private TimerTask getDetailEpgTask = new TimerTask() {
		@Override
		public void run() {
//			Log.i(TAG, "mTimerTask run");
			mEPGtimeCount ++;
			mEPGListCount ++;
			if (mEPGtimeCount >= TIME_EPG_TIME_WAIT) {
				mEPGtimeCount = 0;
				Log.i(TAG, "getEPGTime");
				if(curImp !=null)
					curImp.getEPGTime();
			} 
			if (mEPGListCount >= TIME_EPG_LIST_WAIT) {
				mEPGListCount = 0;
				Log.i(TAG, "getMoreEPG");
				if(curImp !=null)
					curImp.getMoreEPG(mDay);
			}
		}
	};
	
 
 

 
	
	public void release() {
		mTimer.cancel();
		mTimer.purge();
		getDetailEpgTask.cancel();
	}
	
	 
	private void setWeekButton(int weekday) {
		for (int i = 0; i < 7; i ++) {
			int day = (i + weekday) % 7;
			mWeekBtns[i].setText(WEEK_IDS[day]);
		}
	}
	
	private void changeSelectButton(int select) {
		for (int i = 0; i < 7; i ++) {
			if (select == i) {
				mWeekBtns[i].setBackgroundResource(R.drawable.btn_listtype_press);
			} else {
				mWeekBtns[i].setBackgroundResource(R.drawable.btn_listtype);
			}
		}
	}
	
 
	public void updateEpgList(ArrayList<String> epgInfos){
		if(epgInfos == null || epgInfos.size() ==0){
			// 没有详细EPG
			mListView .setVisibility(View.INVISIBLE);
			tvNoDetailEpg .setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			return;
		}
			  mEPGList = epgInfos;
			  refreshList();
	}
	private void refreshList() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		int epgListSize = mEPGList.size();
		for (int i = 0; i < epgListSize; i++){
			HashMap<String, Object> map = new HashMap <String, Object> ();
			map.put(ITEM_TEXT, mEPGList.get(i));
			list.add(map);
		}
		
		mListView.setAdapter(new SimpleAdapter(curContext, 
				list, R.layout.item_program_edit, 
				new String[] {ITEM_TEXT},
				new int[] {R.id.text_item}));
		mListView .setVisibility(View.VISIBLE);
		tvNoDetailEpg .setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
	}
	
	public  void refreshEPGTime(String time) {
		Log.i(TAG, "time " + time);
		String dateStr;
		String timeStr;
		int weekday;
		if (!time.equals("")) {
			int index = time.indexOf(":");
			dateStr = time.substring(0, index);
			timeStr = time.substring(index + 1, time.length() - 2);
			weekday = Integer.parseInt(time.substring(time.length() - 1, time.length()));
		} else {
			Date curDate = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			dateStr = dateFormatter.format(curDate);
			SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
			timeStr = timeFormatter.format(curDate);
			weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		}
		setWeekButton(weekday);
	}
	
	 
	
	public DetailsEpgPopuWindow(Context mContext) {
		super(mContext);
		this.curContext = mContext;
		LayoutInflater layoutInflater = LayoutInflater.from(curContext);
		View detailsEpgLayout = (LinearLayout) layoutInflater.inflate(R.layout.layout_epg, null);
		initDetailsEpgViews(detailsEpgLayout); 
		this.setContentView(detailsEpgLayout);    
//        this.setWidth(LayoutParams.WRAP_CONTENT);    
        this.setWidth(600);    
        this.setHeight(260); 
//        this.setHeight(LayoutParams.WRAP_CONTENT); 
        Resources res = mContext.getResources();
        this.setBackgroundDrawable(res.getDrawable(R.drawable.dialog_bg));// 设置TabMenu菜单背景    
        this.setAnimationStyle(R.style.short_epg_pop_animstyle);    
//        this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应 
        setTouchable(true);
		setOutsideTouchable(true);
	}
 
	private void initDetailsEpgViews(View viewParent){
		curImp = NetModeManager .getNetModeManager().getCurNetMode();
		mListView = (ListView)viewParent. findViewById(R.id.list_epg);
		tvNoDetailEpg = (TextView) viewParent.findViewById(R.id.tv_detail_no_epg);
		mProgressBar = (ProgressBar) viewParent. findViewById(R.id.loading_detail_epg);
		mWeekBtns = new Button[BUTTON_IDS.length];
		for(int i = 0; i < 7; i ++) {
			mWeekBtns[i] = (Button)viewParent. findViewById(BUTTON_IDS[i]);
			mWeekBtns[i].setTag(Integer.valueOf(i));
			mWeekBtns[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mEPGList.clear();
					refreshList();
					mDay = (Integer) v.getTag();
					changeSelectButton(mDay);
					mListView .setVisibility(View.INVISIBLE);
					tvNoDetailEpg .setVisibility(View.INVISIBLE);
					mProgressBar.setVisibility(View.VISIBLE);
					mEPGListCount = TIME_EPG_LIST_WAIT;
				}
			});
		}
		mWeekBtns[0].setBackgroundResource(R.drawable.btn_listtype_press);
		setWeekButton(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
		
		mTimer = new Timer();
		mTimer.schedule(getDetailEpgTask, TIME_START, TIME_PERIOD);
		if(curImp !=null){
			curImp.getEPGTime();
			curImp.getMoreEPG(mDay);
		}
	}
	
	public void setEPGtimeCount(int count){
		this.mEPGListCount = count;
	}
	/**
	 * @param parent
	 * @param gravity
	 * @param x
	 * @param y
	 */
	public void show(View parent,int gravity,int x,int y){
		showAtLocation(parent, gravity, x, y);
	}
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
	}
	 
}
