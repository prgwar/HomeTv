package sdmc.com.adapter;

import java.util.ArrayList;

import sdmc.com.hometv.R;

import com.sdmc.dlna.filebrowser.Const;
import com.sdmc.dlna.filebrowser.FileItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	private ArrayList<FileItem> mFileList;
	private Context mContext;
	private DlnaResItemViews dlnaResItemViews;
	public FileAdapter(Context context, ArrayList<FileItem> list) {
		mContext = context;
		if (list == null) {
			mFileList = new ArrayList<FileItem>();
		} else {
			mFileList = list;
		}
	}

	@Override
	public int getCount() {
		return mFileList != null ? mFileList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mFileList.get(position).getId();
	}
	public void updateFileData(ArrayList<FileItem> newData){
		if( newData != null){
			mFileList = newData;
		}
		notifyDataSetChanged();
	}
	public void addSingleItem(FileItem newFileItem){
		if(newFileItem != null){
			mFileList.add(newFileItem);
		}
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dlna_res_item_layout,null);
			dlnaResItemViews = new DlnaResItemViews();
			dlnaResItemViews .mDlnaResTypeFlag = (ImageView) convertView.findViewById(R.id.dlna_res_type_flag);
			dlnaResItemViews.mResName = (TextView) convertView.findViewById(R.id.dlna_res_name);
			dlnaResItemViews.mResSize = (TextView) convertView.findViewById(R.id.dlna_res_size);
			 
			dlnaResItemViews.mResType = (TextView) convertView.findViewById(R.id.dlna_res_type);
			convertView.setTag(dlnaResItemViews);
		} else {
			 dlnaResItemViews = (DlnaResItemViews) convertView.getTag();
		}
		FileItem fi = mFileList.get(position);
		
		// set icon
		int index = fi.getIndexOfIcon();
		if (index >= 0 && index < Const.DEFAULT_ICONs.length) {
			dlnaResItemViews.mDlnaResTypeFlag.setBackgroundResource(Const.DEFAULT_ICONs[index]);
		} else {
			dlnaResItemViews.mDlnaResTypeFlag.setBackgroundResource(R.drawable.item_type_file);
		}
		// set name
		String resName = fi.getName();
		dlnaResItemViews.mResName.setText(resName);
//		String resEndFileType = resName.substring(resName.lastIndexOf("."));
//		// set res type
//		dlnaResItemViews.mResType.setText(resEndFileType);
		// set size
		 
		return convertView;
	}

	/**
	 * 每个快捷方式显示的内容，包括图标、名称和背景
	 */
	static class DlnaResItemViews {
		ImageView mDlnaResTypeFlag;
		TextView mResName;
		TextView mResSize;
		TextView mResType;
 
	}
}
