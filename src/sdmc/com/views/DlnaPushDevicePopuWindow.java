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
 * ��Ŀ�б�Ĳ���
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
        this.setBackgroundDrawable(res.getDrawable(R.drawable.dlna_push_bg));// ����TabMenu�˵�����    
        this.setAnimationStyle(R.style.short_epg_pop_animstyle);    
        this.setFocusable(true);// menu�˵���ý��� ���û�л�ý���menu�˵��еĿؼ��¼��޷���Ӧ 
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
