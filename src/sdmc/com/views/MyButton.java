package sdmc.com.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button {
	
	private int keyCode;
	
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode= keyCode;
	}

}
