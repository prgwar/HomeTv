package sdmc.com.views;

import sdmc.com.hometv.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomLinearLayout extends LinearLayout {
	
	public CustomLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomLinearLayout);
		TextView moduleName=new TextView(context);
		ImageView moduleIcon=new ImageView(context);
		float textSize=a.getDimension(R.styleable.CustomLinearLayout_tvtextsize, 30);
		moduleName.setTextColor(0xffffffff);
		moduleName.setTextSize(textSize);
		int ivResId=a.getResourceId(R.styleable.CustomLinearLayout_ivresource, 0);
		if(ivResId > 0){
			moduleIcon.setImageResource(ivResId);
		}
		moduleIcon.setScaleType(ScaleType.FIT_CENTER);
		boolean isNeedIvHide=a.getBoolean(R.styleable.CustomLinearLayout_hideiv, false);
		if(isNeedIvHide){
			moduleIcon.setVisibility(View.GONE);
		}
		int textResId=a.getResourceId(R.styleable.CustomLinearLayout_tvtext, 0);
		if(textResId > 0){
			moduleName.setText(a.getResources().getText(textResId));
		}else{
			moduleName.setText(a.getText(R.styleable.CustomLinearLayout_tvtext));
		}
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.topMargin=4;
		layoutParams.gravity=Gravity.CENTER;
		moduleName.setSingleLine(true);
		moduleName.setGravity(Gravity.CENTER);
		LayoutParams paramsForIv=new LayoutParams(100, 100);
		addView(moduleIcon, paramsForIv);
		addView(moduleName, layoutParams);
		a.recycle();
	}

	@SuppressLint("NewApi")
	public CustomLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomLinearLayout(Context context) {
		super(context);
	}
	 
}
