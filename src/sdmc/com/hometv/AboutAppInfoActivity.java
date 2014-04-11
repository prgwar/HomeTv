package sdmc.com.hometv;

import com.sdmc.phone.util.VersionUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutAppInfoActivity extends Activity implements OnClickListener{
	private static final String TAG = "AboutAppInfoActivity";
	private static final boolean DEBGU = true;

	private TextView tvHeadBack;
	private TextView tvHeadTab;
	private TextView tvCurVertion;
	private TextView tvVersionState;
	private RelativeLayout upgradeVerLayout;
	private RelativeLayout functionIntroLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_app_layout);
		initViews();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	protected void onStop() {
		super.onStop();

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}
	private void initViews(){
		tvHeadBack = (TextView) findViewById(R.id.head_back);
		tvHeadTab = (TextView) findViewById(R.id.head_current_tab);
		tvCurVertion = (TextView) findViewById(R.id.tv_cur_appver);
		tvVersionState = (TextView) findViewById(R.id.versionInform);
		upgradeVerLayout = (RelativeLayout) findViewById(R.id.upgradeLayout);
		functionIntroLayout = (RelativeLayout) findViewById(R.id.introduction);
		tvHeadTab.setText(R.string.about_app);
		tvVersionState.setVisibility(View.VISIBLE);
		
		String curVerSionName = VersionUtil.getVersionName(); 
		if(curVerSionName != null)
		tvCurVertion.setText("V: "+curVerSionName);
		//set listeners
		tvHeadBack.setOnClickListener(this);
		upgradeVerLayout.setOnClickListener(this);
		functionIntroLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.head_back:
			finish();
			break;
		case R.id.upgradeLayout: //检查更新
			break;
		case R.id.introduction: //功能介绍
			break;
		default:
			break;
		}
	}
}
