package sdmc.com.adapter;

import java.util.ArrayList;

import sdmc.com.hometv.R;

import com.sdmc.phone.stat.ScanData;
import com.sdmc.phone.stat.ScanData.ScanInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 扫描到的设备适配器
 * @author fee
 *
 */
public class ScannedDeviceAdapter extends BaseAdapter {
	private ArrayList<ScanData.ScanInfo> devices = null;
	private LayoutInflater layoutInflater;
	ViewHolder viewHolder;
	public ScannedDeviceAdapter(Context mContext,ArrayList<ScanData.ScanInfo> deviceData) {
		this.layoutInflater=LayoutInflater.from(mContext);
		if(deviceData !=null ){
			this.devices=deviceData;
		}
		this.devices=new ArrayList<ScanData.ScanInfo>();
	}
	@Override
	public int getCount() {
		return devices !=null ? devices.size(): 0;
	}

	@Override
	public Object getItem(int position) {
		 
		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.lv_item_layout, null);
			viewHolder.tvServerName = (TextView) convertView.findViewById(R.id.lv_item_server_name);
			viewHolder.tvServerIP = (TextView) convertView.findViewById(R.id.lv_item_server_ip);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ScanInfo curScanInfo = devices.get(position);
		viewHolder.tvServerName.setText(curScanInfo.getName());
		viewHolder.tvServerIP.setText("IP:"+curScanInfo.getIp());
		return convertView;
	}
	public void updateDevices(ArrayList<ScanData.ScanInfo> curDevices){
		if(curDevices !=null){
			this.devices = curDevices;
		}
		notifyDataSetChanged();
	}
	private int curConnectedPosion = -1;
	public void setCurConnectedPositon(int position){
		curConnectedPosion = position;
	}
	static class ViewHolder{
		TextView tvServerName;
		TextView tvServerIP;
	}
}
