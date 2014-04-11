package sdmc.com.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ChangeButton extends Button {

	private Class<?> target;
	
	public ChangeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public Class<?> getTarget() {
		return target;
	}

	public void setTarget(Class<?> target) {
		this.target = target;
	}
}
