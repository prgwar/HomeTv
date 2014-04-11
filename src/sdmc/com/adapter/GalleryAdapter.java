package sdmc.com.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


public class GalleryAdapter extends BaseAdapter
{
	private Context mContext;
	 ArrayList<BitmapDrawable> mBitmaps;
	 
	 public GalleryAdapter(ArrayList<BitmapDrawable> bitMaps,Context curContex ) {
		 this.mContext = curContex;
		 if(bitMaps == null	){
			 mBitmaps = new ArrayList<BitmapDrawable>();
		 }
		 else{
			 mBitmaps = bitMaps;
		 }
	 }
    @Override
    public int getCount()
    {
        return mBitmaps.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }
    public void updataOneByOne(BitmapDrawable gotBitMap){
    	if(mBitmaps != null	){
    		mBitmaps.add(gotBitMap);
    		notifyDataSetChanged();
    	}
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (null == convertView)
        {
            convertView = new ImageView(mContext);
            convertView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        
        ImageView imageView = (ImageView)convertView;
        imageView.setImageDrawable(mBitmaps.get(position));
        imageView.setScaleType(ScaleType.FIT_XY);
        
        return imageView;
    }
//    private class MyImageView extends ImageView
//    {
//        public MyImageView(Context context)
//        {
//            this(context, null);
//        }
//        
//        public MyImageView(Context context, AttributeSet attrs)
//        {
//            super(context, attrs, 0);
//        }
//        
//        public MyImageView(Context context, AttributeSet attrs, int defStyle)
//        {
//            super(context, attrs, defStyle);
//        }
//        
//        protected void onDraw(Canvas canvas)
//        {
//            super.onDraw(canvas);
//        }
//    }
}
