package sdmc.com.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class AutoFlipGallery extends Gallery {
	/**
	 * �������ֵ��������ÿ�ι�������󳤶ȣ�ͼƬ���Ϊ480PX����������600Ч����һЩ�� 
	 * ���ֵԽ�󣬹����ĳ��Ⱦ�Խ��
	 * Ҳ���ǻ����һ�ι�������Image���������Ƴ��Ⱥ�ÿ�ι���ֻ�ܿ�һ��Image
	 */
	private static final int timerAnimation = 1;
	private final Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case timerAnimation:
				int position = getSelectedItemPosition();
//				Log.i("msg", "position:"+position);
				if (position >= (getCount() - 1))
				{
					onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				} else
				{
					onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}
				break;
			}
		};
	};

	private final Timer timer = new Timer();
	private final TimerTask task = new TimerTask()
	{
		public void run()
		{
			if(isOnFling){
				isOnFling=false;
				return;
			}
			mHandler.sendEmptyMessage(timerAnimation);
		}
	};

	public AutoFlipGallery(Context paramContext)
	{
		super(paramContext);
		timer.schedule(task, 3000, 3000);
	}

	public AutoFlipGallery(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		timer.schedule(task, 3000, 3000);

	}

	public AutoFlipGallery(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
		timer.schedule(task, 3000, 3000);

	}
	/**
	 *  �ж��Ƿ�Ҫ�������
	 *  ��---------->��    ���һ���   ���������
	 *  x1    <     x2
	 *  
	 * @param paramMotionEvent1
	 * @param paramMotionEvent2
	 * @return
	 */
	private boolean isScrollingLeft(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2)
	{
		float f2 = paramMotionEvent2.getX();
		float f1 = paramMotionEvent1.getX();
		if (f2 > f1)
			return true;
		return false;
	}
	private boolean isOnFling=false;
	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
	{
		isOnFling=true;
		int keyCode;
		if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2))
		{
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else
		{
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return true;
	}

	public void destroy()
	{
		timer.cancel();
	}
}
