package sdmc.com.adapter;

import java.util.ArrayList;

import sdmc.com.hometv.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdmc.dlna.service.DeviceItem;
/**
 * @author fee
 *
 */
public class DlnaDevicesAdapter extends BaseAdapter{

	private  ArrayList<DeviceItem> mDlanList;
	private Context mContext;
	private int mCurrentSelectDevice = -1;
	private String curSelectedDeviceUdn = null;
	private DlanDevicesItemLayout listItemView;
	public DlnaDevicesAdapter(Context context, ArrayList<DeviceItem> list ) {
		mContext = context;
		if(list !=null){
			mDlanList = list;
		}else{
			mDlanList = new ArrayList<DeviceItem>();
		}
	}
	
 
	/**
	 * 更新数据
	 * @param newData
	 */
	public void updateListData(ArrayList<DeviceItem> newData){
//			mDlanList.clear();
//			mDlanList.addAll(newData);
			mDlanList = newData;
//			Log . e("info","  ^^^ mDlanList size =  "+mDlanList.size());
			notifyDataSetChanged();
	}
	/**
	 * 设置当前播放的节目
	 * @param currentProgram
	 */
	public void setCurrentCheckedItem(int currentItem){
//		Log.i("info","^^^ DlnaDevicesAdapter setCurrentCheckedItem() currentItem = "+currentItem);
		mCurrentSelectDevice = currentItem;
		notifyDataSetChanged();
	}
	public void setCurrentCheckedItemBaseUdn(String udn){
		curSelectedDeviceUdn = udn;
		notifyDataSetChanged();
	}
	 
	@Override
	public int getCount() {
//		 Log.i("info","^^^ DlnaDevicesAdapter getCount() "+mDlanList.size());
		return mDlanList != null ? mDlanList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		if( mDlanList == null || mDlanList.size() ==0) return "";
		return mDlanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		if( mDlanList == null || mDlanList.size() == 0) return 0;
		return mDlanList.get(position).getDeviceID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.e("info","   DlnaDevicesAdapter getView()..");
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dlna_device_item_layout, null);
			listItemView = new DlanDevicesItemLayout();
			listItemView.dlnaDeviceName = (TextView) convertView.findViewById(R.id.dlna_device_name);
			listItemView.dlnaDeviceIp = (TextView )convertView.findViewById(R.id.dlna_device_ip);
			listItemView.dlnaDeviceCheck  =(ImageView) convertView.findViewById(R.id.dlna_device_checked);
//			listItemView.dlnaDeviceCheck  =(CheckBox) convertView.findViewById(R.id.dlna_device_checked);
			convertView.setTag(listItemView);
		}
		else {
			listItemView = (DlanDevicesItemLayout) convertView.getTag();
		}
		
		DeviceItem  dlnaDevice = mDlanList.get(position);
		listItemView.dlnaDeviceName .setText(dlnaDevice.getName());
		String dlnaDeviceIp = dlnaDevice.getIPAddress();
		if(dlnaDeviceIp == null || "".equals(dlnaDeviceIp)){
			listItemView.dlnaDeviceIp.setVisibility(View.GONE);
		}else{
			listItemView.dlnaDeviceIp.setVisibility(View.VISIBLE);
			listItemView.dlnaDeviceIp .setText(dlnaDevice.getDeviceID()+"");
		}
//		if(position == mCurrentSelectDevice){
//			listItemView.dlnaDeviceCheck.setBackgroundResource(R.drawable.check_box_pressed);
//		}
//		else{
//			listItemView.dlnaDeviceCheck.setBackgroundResource(R.drawable.check_box_default);
//		}
		String deviceUdn = dlnaDevice.getUdn();
		if(curSelectedDeviceUdn != null && curSelectedDeviceUdn.equals(deviceUdn)){
			listItemView.dlnaDeviceCheck.setBackgroundResource(R.drawable.check_box_pressed);
		}
		else{
			listItemView.dlnaDeviceCheck.setBackgroundResource(R.drawable.check_box_default);
		}
//	   listItemView.dlnaDeviceCheck.setChecked(position == mCurrentSelectDevice);
		return convertView;
	}
	 class DlanDevicesItemLayout{
		TextView dlnaDeviceName;
		TextView dlnaDeviceIp;
//		CheckBox  dlnaDeviceCheck; 
		ImageView dlnaDeviceCheck;
	}
}
