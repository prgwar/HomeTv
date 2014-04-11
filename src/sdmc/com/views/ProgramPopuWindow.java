package sdmc.com.views;

import sdmc.com.adapter.ProgramAdapter;
import sdmc.com.hometv.R;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 * 节目列表的布局
 * @author fee
 *
 */
public class ProgramPopuWindow extends PopupWindow {
	private Context curContext;
	private ListView lvProgramList;
 	private ProgramAdapter programAdapter;
 	private Button btnPreProgramType;
 	private Button btnNextProgramType;
 	/** 当前节目类型：电视、广播、喜爱 **/
 	private TextView curProgramType;
 	private Button btnCurTypeAllPrograms;
 	private Button btnCurTypeFree;
 	private Button btnCurTypeFind;
 	private LinearLayout mFindLayout;
 	private int curTypeIndex = 0;
 	private EditText mFindProgramEdit;
 	private String [] programTypeNames;
 	private TextWatcher textWatcher;
	public ProgramPopuWindow(Context mContext) {
		super(mContext);
		this.curContext = mContext;
		LayoutInflater layoutInflater = LayoutInflater.from(curContext);
		View programListLayout = (LinearLayout) layoutInflater.inflate(R.layout.layout_programlist, null);
		initProgramListViews(programListLayout); 
		this.setContentView(programListLayout);    
        this.setWidth(300);    
        this.setHeight(LayoutParams.FILL_PARENT); 
        Resources res = mContext.getResources();
        programTypeNames = res.getStringArray(R.array.program_types);
        this.setBackgroundDrawable(res.getDrawable(R.drawable.popwindow_for_program_list_bg));// 设置TabMenu菜单背景    
        this.setAnimationStyle(R.style.pop_animstyle);    
        this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应 
        setTouchable(true);
		setOutsideTouchable(true);
	}
 
	private void initProgramListViews(View viewParent){
 		lvProgramList = (ListView) viewParent.findViewById(R.id.list_programlist);
 		btnPreProgramType  =(Button) viewParent.findViewById(R.id.btn_left);
 		btnNextProgramType  =(Button)viewParent.findViewById(R.id.btn_right);
 		curProgramType = (TextView) viewParent.findViewById(R.id.cur_type_title);
 		btnCurTypeAllPrograms = (Button) viewParent.findViewById(R.id.btn_all);
 		btnCurTypeFree = (Button) viewParent.findViewById(R.id.btn_curtype_for_free);
 		btnCurTypeFind = (Button) viewParent.findViewById(R.id.btn_search);
 		mFindLayout = (LinearLayout) viewParent.findViewById(R.id.linear_search);
 		mFindProgramEdit = (EditText)viewParent. findViewById(R.id.edit_find);
 	}
	public void setOnclickListener(OnClickListener onClickListener){
		// set listenners
		 		btnPreProgramType .setOnClickListener(onClickListener);
		 		btnNextProgramType.setOnClickListener(onClickListener);
		 		btnCurTypeAllPrograms.setOnClickListener(onClickListener);
		 		btnCurTypeFree.setOnClickListener(onClickListener);
		 		btnCurTypeFind.setOnClickListener(onClickListener);
	}
	public void setLvOnItemClickListener(OnItemClickListener onItemClickListener){
		this.lvProgramList.setOnItemClickListener(onItemClickListener);
	}
	public void setLvOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
		this.lvProgramList.setOnItemLongClickListener(onItemLongClickListener);
	}
	public void setLvAdapter(ProgramAdapter adapter){
		this.lvProgramList.setAdapter(adapter);
	}
	public void changeListType(int type, boolean isAllBtnPressed){
		curProgramType.setText(programTypeNames[type]);
		if (!isAllBtnPressed) {
			btnCurTypeAllPrograms.setBackgroundResource(R.drawable.all);
			btnCurTypeFree.setBackgroundResource(R.drawable.fta_press);
			btnCurTypeFind.setBackgroundResource(R.drawable.search);
			mFindLayout.setVisibility(View.GONE);
		} else { //选中了ALL
			btnCurTypeAllPrograms.setBackgroundResource(R.drawable.all_press);
			btnCurTypeFree.setBackgroundResource(R.drawable.fta);
			btnCurTypeFind.setBackgroundResource(R.drawable.search);
			mFindLayout.setVisibility(View.GONE);
		}
		
		mFindProgramEdit.removeTextChangedListener(textWatcher);
		mFindProgramEdit.setText("");
//		mIsFind = false;
		this.lvProgramList.requestFocusFromTouch();
		mFindProgramEdit.addTextChangedListener(textWatcher);
	}
	public void setTextWatcherForEditText(TextWatcher srcTextWatcher){
		this.textWatcher = srcTextWatcher;
	}
	public EditText getMfindProgramEdit(){
		return this.mFindProgramEdit;
	}
	public void prepareToFind(){
		btnCurTypeAllPrograms.setBackgroundResource(R.drawable.all);
		btnCurTypeFree.setBackgroundResource(R.drawable.fta);
		btnCurTypeFind.setBackgroundResource(R.drawable.search_press);
		mFindLayout.setVisibility(View.VISIBLE);
	}
	public void setCurPlayingItemInList(int playingItem){
		this.lvProgramList.setSelectionFromTop(playingItem, 10);
	}
}
