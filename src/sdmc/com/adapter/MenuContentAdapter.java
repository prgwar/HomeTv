package sdmc.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class MenuContentAdapter extends ArrayAdapter<String> {

	public MenuContentAdapter(Context context, int resource) {
		super(context, resource);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return super.getView(position, convertView, parent);
	}
}
