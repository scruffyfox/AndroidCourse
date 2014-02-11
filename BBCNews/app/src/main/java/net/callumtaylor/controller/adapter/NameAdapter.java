package net.callumtaylor.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.callumtaylor.news.R;

public class NameAdapter extends BaseAdapter
{
	private String[] objects;
	private Context context;

	public NameAdapter(Context context, String[] objects)
	{
		this.context = context;
		this.objects = objects;
	}

	@Override public int getCount()
	{
		return objects.length;
	}

	@Override public String getItem(int position)
	{
		return objects[position];
	}

	@Override public long getItemId(int position)
	{
		return objects[position].hashCode();
	}

	@Override public View getView(int position, View convertView, ViewGroup parent)
	{
		TextView name;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
			name = (TextView)convertView.findViewById(R.id.name);
			convertView.setTag(name);
		}
		else
		{
			name = (TextView)convertView.getTag();
		}

		name.setText(getItem(position));

		return convertView;
	}
}
