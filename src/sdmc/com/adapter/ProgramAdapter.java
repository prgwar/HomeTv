package sdmc.com.adapter;

import java.util.ArrayList;

import sdmc.com.hometv.R;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdmc.dtv.programsend.Program;
/**
 * @author fee
 *
 */
public class ProgramAdapter extends BaseAdapter{

	private ArrayList<Program> mList;
	private Context mContext;
	private int mCurrentPlayingProgram;
	private String mEpgString;
	private ProgramListItem listItemView;
	public ProgramAdapter(Context context, ArrayList<Program> list,
			int currentPlayingProgram, String epgString) {
		mContext = context;
		mCurrentPlayingProgram = currentPlayingProgram;
		mEpgString = epgString;
		mList = list;
	}
	
 
	/**
	 * 更新数据
	 * @param newData
	 */
	public void updateListData(ArrayList<Program> newData){
		if(newData == null ){
			if(mList ==null)
				mList = new ArrayList<Program>();
		}else {
			mList = newData;
			notifyDataSetChanged();
		}
	}
	/**
	 * 设置当前播放的节目
	 * @param currentProgram
	 */
	public void setCurrentPlayingProgram(int currentProgram){
		mCurrentPlayingProgram = currentProgram;
		notifyDataSetChanged();
	}
	
	public void setEpgString(String epgString){
		mEpgString = epgString;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		if(mList == null || mList.size() ==0) return 0;
		return mList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.program_list_item_layout, null);
			listItemView = new ProgramListItem();
			listItemView.tvProgramIndex = (TextView) convertView.findViewById(R.id.tv_program_index);
			listItemView.tvProgramName = (TextView )convertView.findViewById(R.id.tv_program_name);
			listItemView.ivIsFavorite = (ImageView) convertView.findViewById(R.id.iv_like);
			listItemView.ivIsLock = (ImageView) convertView.findViewById(R.id.iv_lock);
			listItemView.ivIsPaid = (ImageView) convertView.findViewById(R.id.iv_paid);
			convertView.setTag(listItemView);
		}
		else {
			listItemView = (ProgramListItem) convertView.getTag();
		}
		
		Program program = mList.get(position);
		listItemView.tvProgramIndex .setText(String.format("%04d", position + 1));
		listItemView.tvProgramName.setText(program.getName());
		
		listItemView.ivIsPaid.setVisibility(program.isScrambler() ? View.VISIBLE : View.INVISIBLE);
		listItemView.ivIsFavorite.setVisibility(program.isFavor() ? View.VISIBLE : View.INVISIBLE);
		listItemView.ivIsLock.setVisibility(program.isLock() ? View.VISIBLE : View.INVISIBLE);
		//当前播放 的节目
		int programId = program.getId(); 
		if (programId == mCurrentPlayingProgram){ 
			listItemView.tvProgramIndex.setTextColor(Color.BLUE);
			listItemView.tvProgramName.setTextColor(Color.BLUE);
		 
		} else {
			listItemView.tvProgramIndex.setTextColor(Color.rgb(0xAA, 0xAA, 0xAA));
			listItemView.tvProgramName.setTextColor(Color.rgb(0xAA, 0xAA, 0xAA));
		}
		
		return convertView;
	}
	public static class ProgramListItem{
		TextView tvProgramIndex;
		TextView tvProgramName;
		ImageView ivIsLock;
		ImageView ivIsFavorite;
		//是否付费
		ImageView ivIsPaid;
	}
}
