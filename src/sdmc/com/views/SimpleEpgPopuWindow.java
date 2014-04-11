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
 * ��Ŀ�б�Ĳ���
 * @author fee
 *
 */
public class SimpleEpgPopuWindow extends PopupWindow {
	private Context curContext;
	/** ��ǰ���ŵĽ�ĿEPG **/
	private TextView pEPG_Name; 
	private TextView pEPG_Time;
	/** ��һ�����ŵĽ�ĿEPG **/
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
        this.setBackgroundDrawable(res.getDrawable(R.drawable.dialog_bg));// ����TabMenu�˵�����    
        this.setAnimationStyle(R.style.short_epg_pop_animstyle);    
//        this.setFocusable(true);// menu�˵���ý��� ���û�л�ý���menu�˵��еĿؼ��¼��޷���Ӧ 
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
	 * ��ʾ���Զ�����
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
