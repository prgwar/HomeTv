package sdmc.com.views;

import hometv.remote.socketUtil.Constant;

import com.sdmc.dlna.service.NativeAccess;
import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;

import sdmc.com.hometv.R;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
/**
 *  
 * @author fee
 *
 */
public class DlnaSettingPopuWindow extends PopupWindow {
	private Context curContext;
	private Button btnCommit; 
	private CheckBox cbSharePic;
	private CheckBox cbShareVideo;
	private CheckBox cbShareMusic;
	private EditText edtDlnaName;
	
	public DlnaSettingPopuWindow(Context mContext) {
		super(mContext);
		 visiter  = PreferencesVisiter.getVisiter();
		this.curContext = mContext;
		LayoutInflater layoutInflater = LayoutInflater.from(curContext);
		View dlnaSettingView = layoutInflater.inflate(R.layout.dlna_setting_layout, null);
		initDlnaSetLayout(dlnaSettingView);
		this.setContentView(dlnaSettingView);    
        this.setWidth(LayoutParams.WRAP_CONTENT);    
//        this.setHeight(200); 
        this.setHeight(LayoutParams.WRAP_CONTENT); 
        Resources res = mContext.getResources();
        this.setBackgroundDrawable(res.getDrawable(R.drawable.dlna_push_bg));// 设置TabMenu菜单背景    
        this.setAnimationStyle(R.style.more_menu_pop_animstyle);    
       
        this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应 
        setTouchable(true);
		setOutsideTouchable(true);
		
//		setInputMethodMode(INPUT_METHOD_NEEDED);
//		setInputMethodMode(
////				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
////                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
////				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
//				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//				INPUT_METHOD_NEEDED
//                );
		 
	}
	private void initDlnaSetLayout(View container){
		btnCommit = (Button) container.findViewById(R.id.btn_dlna_setting_commit);
		cbShareMusic = (CheckBox) container.findViewById(R.id.checkbox_share_music);
		cbSharePic = (CheckBox) container.findViewById(R.id.checkbox_share_pic);
		cbShareVideo = (CheckBox) container.findViewById(R.id.checkbox_share_video);
		edtDlnaName = (EditText) container.findViewById(R.id.edit_dlna_name);
		btnCommit.setOnClickListener(clickListener);
		boolean [] mediaShares = visiter.getLocalShareMediaPrefers();
		cbShareVideo.setChecked(mediaShares[0]);
		cbShareMusic.setChecked(mediaShares[1]);
		cbSharePic.setChecked(mediaShares[2]);
	}
	PreferencesVisiter visiter;
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int viewId = v.getId();
			switch (viewId) {
			case R.id.btn_dlna_setting_commit:
				 
				boolean isShareVideo = cbShareVideo.isChecked();
				boolean isShareMusic = cbShareMusic.isChecked();
				boolean isSharePic =  cbSharePic.isChecked();
				visiter.savePrefer(Constant.DLNA_SHARE_IMAGE,isSharePic);
				visiter.savePrefer(Constant.DLNA_SHARE_MUSIC, isShareMusic);
				visiter.savePrefer(Constant.DLNA_SHARE_VIDEO, isShareVideo);
				String newDlnaName = edtDlnaName.getText().toString();
				String theOldDLnaName = visiter.getPreferInfo(Constant.LOCAL_DLNA_NAME, "");
				NativeAccess.setShare(
						isShareVideo ? 1 :0,
						isShareMusic ? 1: 0,
						isSharePic ? 1 : 0		
						);
				if(null == newDlnaName || newDlnaName.length() == 0 || newDlnaName .equals(theOldDLnaName)){
					dismiss();
					break;
				}
					NativeAccess.setUserName(newDlnaName+" : "+NetUtil.IP, NetUtil.MAC);
					visiter.savePrefer(Constant.LOCAL_DLNA_NAME, newDlnaName);
					dismiss();
				break;

			default:
				break;
			}
		}
	};
}
