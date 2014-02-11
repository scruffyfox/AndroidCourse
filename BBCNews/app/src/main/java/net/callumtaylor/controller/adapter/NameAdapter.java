package net.callumtaylor.controller.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

public class NameAdapter extends ArrayAdapter
{
	public NameAdapter(Context context, Object[] objects)
	{
		super(context, android.R.layout.simple_list_item_1, objects);
	}
}
