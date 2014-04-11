package hometv.remote.socketUtil;

import sdmc.com.hometv.R;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	static LayoutInflater layoutInflater = null;
	static Context mContext;
	static int marginBottomH = 0;
	public static void setContext(Context context) {
		mContext = context;
		//marginBottomH = (int) mContext.getResources().getDimension(R.dimen.tabwidget_height);
		if (layoutInflater == null) {
			layoutInflater = LayoutInflater.from(mContext);
		}
	}

	public static void showToast(String str) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		//TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		//messageInfo.setText(str);
		mToast.setView(v);
		mToast.setGravity(Gravity.BOTTOM, 0, 8);
		mToast.setDuration(300);
		mToast.show();
	}

	public static void showToast(int strID) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		messageInfo.setText(strID);
		mToast.setView(v);
		mToast.setGravity(Gravity.BOTTOM, 0, 8);
		mToast.setDuration(300);
		mToast.show();
	}

	public static void showToast(int strID, int gravity) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		messageInfo.setText(strID);
		mToast.setView(v);
		if(gravity==Gravity.TOP){
			mToast.setGravity(gravity, -100, 0);
		}else{
			mToast.setGravity(gravity, 0, marginBottomH + 20);
		}
		mToast.setDuration(300);
		mToast.show();
	}
	public static void showToast(String message, int gravity) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		messageInfo.setText(message);
		mToast.setView(v);
		if(gravity==Gravity.TOP){
			mToast.setGravity(gravity, -100, 0);
		}else{
			mToast.setGravity(gravity, 0, marginBottomH + 20);
		}
		mToast.setDuration(300);
		mToast.show();
	}
	public static void show(String msg,int gravity){
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.toast_base, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_msg);
		messageInfo.setText(msg);
		mToast.setView(v);
		mToast.setGravity(gravity, 0, 20);
		mToast.setDuration(300);
		mToast.show();
	}
	public static void topShow(int  msgId ){
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.toast_base, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_msg);
		messageInfo.setText(msgId);
		mToast.setView(v);
		mToast.setGravity(Gravity.TOP, 0, 20);
		mToast.setDuration(300);
		mToast.show();
	}
	public static void topShow(String  msg ){
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.toast_base, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_msg);
		messageInfo.setText(msg);
		mToast.setView(v);
		mToast.setGravity(Gravity.TOP, 0, 20);
		mToast.setDuration(300);
		mToast.show();
	}
}
