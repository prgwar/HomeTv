package sdmc.com.views;

import sdmc.com.adapter.DlnaDevicesAdapter;
import sdmc.com.hometv.R;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
/**
 * 节目列表的布局
 * @author fee
 *
 */
public class DlnaPushDevicePopuWindow extends PopupWindow {
	private Context curContext;
	private ListView lvDlnaPushDevices; 
	
	public DlnaPushDevicePopuWindow(Context mContext) {
		super(mContext);
		this.curContext = mContext;
		LayoutInflater layoutInflater = LayoutInflater.from(curContext);
		View dlnaPushView = (LinearLayout) layoutInflater.inflate(R.layout.dlna_push_devices_layout, null);
		lvDlnaPushDevices = (ListView) dlnaPushView.findViewById(R.id.lv_dlna_push_devices);
		this.setContentView(dlnaPushView);    
        this.setWidth(LayoutParams.WRAP_CONTENT);    
//        this.setHeight(200); 
        this.setHeight(LayoutParams.WRAP_CONTENT); 
        Resources res = mContext.getResources();
        this.setBackgroundDrawable(res.getDrawable(R.drawable.dlna_push_bg));// 设置TabMenu菜单背景    
        this.setAnimationStyle(R.style.short_epg_pop_animstyle);    
        this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应 
        setTouchable(true);
		setOutsideTouchable(true);
	}
	public void setLvAdapter(DlnaDevicesAdapter adapter){
		this.lvDlnaPushDevices.setAdapter(adapter);
	}
	public void setLvOnitemClickListener(OnItemClickListener itemClickListener){
		this.lvDlnaPushDevices.setOnItemClickListener(itemClickListener);
	}
}
