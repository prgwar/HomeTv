package sdmc.com.adapter;

import sdmc.com.hometv.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView.ScaleType;


/**
 * π„∏ÊÕº∆¨  ≈‰∆˜
 * @author fee
 *
 */
@SuppressWarnings("deprecation")
public class AdImageAdapter extends BaseAdapter {
	private Context _context;
	private int [] adImagesRes={
		R.drawable.img3,	
		R.drawable.img1,
		R.drawable.img2
	};
	public AdImageAdapter(Context context) {
	    _context = context;
	}

	public int getCount() {
	    return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {

	    return position;
	}

	public long getItemId(int position) {
	    return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			ImageView imageView = new ImageView(_context);
		    imageView.setAdjustViewBounds(true);
		    imageView.setScaleType(ScaleType.FIT_XY);
		    imageView.setLayoutParams(new Gallery.LayoutParams(
			    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		    convertView = imageView;
		    viewHolder.imageView = (ImageView)convertView; 
		    convertView.setTag(viewHolder);
			
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		int curResId=position % adImagesRes.length;
				 
	    viewHolder.imageView.setImageResource(adImagesRes[curResId]);
	    
	    return convertView;
	}
	
	  static class ViewHolder
	  {
	  	ImageView imageView;
	  }   
 
}

