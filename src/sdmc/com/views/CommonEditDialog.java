package sdmc.com.views;

import sdmc.com.hometv.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class CommonEditDialog {
	public static final int DIALOG_TYPE_NO_WIFI = 0;
	public static final int DIALOG_TYPE_ENTER_PARENT_UNLOCK = 1;
	public static final int DIALOG_TYPE_PARENT_UNLOCK_PW_ERROR = 2;
	
	private Context mContext;
	private View dialogView;
	private TextView tvTitle;
	private TextView tvHint;
	private TextView tvBtnSure;
	private TextView tvBtnCancel;
	private EditText edtContent;
	private Dialog curDialog;
	private int width,height;
	Window window;
	public CommonEditDialog(Context curContext,int width,int height) {
		this.mContext = curContext;
		this.width = width;
		this.height = height;
		initDialogView();
	}
	private void initDialogView(){
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		dialogView = layoutInflater.inflate(R.layout.common_dialog_layout, null);
		tvTitle = (TextView) dialogView.findViewById(R.id.comm_dialog_title);
		tvHint = (TextView) dialogView.findViewById(R.id.comm_dialog_hint);
		edtContent = (EditText) dialogView.findViewById(R.id.comm_dialog_edit);
		tvBtnCancel = (TextView) dialogView.findViewById(R.id.comm_dialog_tvbtn_cancel);
		tvBtnSure =(TextView) dialogView.findViewById(R.id.comm_dialog_tvbtn_sure);
		tvBtnCancel.setOnClickListener(canCelOnclickListener);
		curDialog = new Dialog(mContext, R.style.MyDialog);
		curDialog.setCanceledOnTouchOutside(false);
		LayoutParams params = new LayoutParams(width, height);
		curDialog.setContentView(dialogView,params);
//		AlertDialog.Builder builder = new Builder(mContext);
//		curDialog = builder.create();
//		curDialog.setCanceledOnTouchOutside(false);
//		window= curDialog.getWindow();
	}
	public void setOnclickListener(OnClickListener clickListener){
				tvBtnSure.setOnClickListener(clickListener);
	}
	public void show(int dialogType){
		curDialog.show();
//		window.setLayout(width, height);
//		window.setContentView(dialogView);
		switch (dialogType) {
		case DIALOG_TYPE_NO_WIFI:
			tvTitle.setText(R.string.nonet);
			edtContent.setVisibility(View.GONE);
			tvHint.setVisibility(View.GONE);
			tvBtnSure.setText(R.string.to_set_net);
			break;
		case DIALOG_TYPE_ENTER_PARENT_UNLOCK:
			// ‰»ÎΩ‚À¯√‹¬Î
			tvTitle.setText(R.string.please_enter_unlock_pw);
			tvTitle.setVisibility(View.VISIBLE);
			edtContent.setVisibility(View.VISIBLE);
			tvHint.setVisibility(View.GONE);
			tvBtnSure.setText(R.string.sure);
			break;
		case DIALOG_TYPE_PARENT_UNLOCK_PW_ERROR: //√‹¬Î¥ÌŒÛ
			tvTitle.setText(R.string.the_lock_password_error);
			tvTitle.setVisibility(View.VISIBLE);
			edtContent.setVisibility(View.VISIBLE);
			tvHint.setVisibility(View.GONE);
			tvBtnSure.setText(R.string.sure);
			break;
		}//end switch
		 
	}
	private OnClickListener canCelOnclickListener =new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(curDialog != null){
				curDialog.dismiss();
			}
		}
	};
	public void dismiss(){
		curDialog.dismiss();
	}
	public boolean isShowing(){
		return curDialog !=null ? curDialog.isShowing():false;
	}
	public EditText getTheEditTextView(){
		return edtContent;
	}
	public void dissMiss(){
		if(curDialog !=null)curDialog.dismiss();
	}
}
