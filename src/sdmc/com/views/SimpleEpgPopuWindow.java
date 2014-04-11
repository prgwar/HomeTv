package sdmc.com.views;

import com.sdmc.dtv.programsend.ShortEPG;

import sdmc.com.hometv.R;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 * 节目列表的布局
 * @author fee
 *
 */
public class SimpleEpgPopuWindow extends PopupWindow {
	private Context curContext;
	/** 当前播放的节目EPG **/
	private TextView pEPG_Name; 
	private TextView pEPG_Time;
	/** 下一个播放的节目EPG **/
	private TextView fEPG_Name;
	private TextView fEPG_Time;
	
	public SimpleEpgPopuWindow(Context mContext) {
		super(mContext);
		this.curContext = mContext;
		LayoutInflater layoutInflater = LayoutInflater.from(curContext);
		View simpleEpgLayout = (LinearLayout) layoutInflater.inflate(R.layout.simple_epg_layout, null);
		initSimpleEpgViews(simpleEpgLayout); 
		this.setContentView(simpleEpgLayout);    
        this.setWidth(LayoutParams.WRAP_CONTENT);    
        this.setHeight(200); 
//        this.setHeight(LayoutParams.WRAP_CONTENT); 
        Resources res = mContext.getResources();
        this.setBackgroundDrawable(res.getDrawable(R.drawable.dialog_bg));// 设置TabMenu菜单背景    
        this.setAnimationStyle(R.style.short_epg_pop_animstyle);    
//        this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应 
//        setTouchable(true);
		setOutsideTouchable(true);
	}
 
	private void initSimpleEpgViews(View viewParent){
 		 pEPG_Name = (TextView) viewParent.findViewById(R.id.simple_pepg_name);
 		 pEPG_Time = (TextView) viewParent.findViewById(R.id.simple_pepg_time);
 		 fEPG_Name = (TextView) viewParent.findViewById(R.id.simple_fepg_name);
 		 fEPG_Time = (TextView) viewParent.findViewById(R.id.simple_fepg_time);
 	}
	
	public void setCurProgramShortEpg(ShortEPG shortEPG){
		if(shortEPG == null) return;
		pEPG_Name.setText(shortEPG.getPEpgName());
		pEPG_Time.setText(shortEPG.getPEpgTime());
		fEPG_Name.setText(shortEPG.getFEpgName());
		fEPG_Time.setText(shortEPG.getFEpgTime());
	}
	/**
	 * 显示后，自动隐藏
	 * @param parent
	 * @param gravity
	 * @param x
	 * @param y
	 */
	public void showAtLocationAndAutoDismiss(View parent,int gravity,int x,int y){
		showAtLocation(parent, gravity, x, y);
		mHandler.sendEmptyMessageDelayed(AUTO_DISSMISS, DEFAULT_TIME_OUT);
	}
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
	}
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int msgWhat  = msg.what;
			switch (msgWhat) {
			case AUTO_DISSMISS:
				dismiss();
				break;
			}
		};
	};
	private static final int AUTO_DISSMISS = 0;
	private static final int DEFAULT_TIME_OUT = 6000;
}
