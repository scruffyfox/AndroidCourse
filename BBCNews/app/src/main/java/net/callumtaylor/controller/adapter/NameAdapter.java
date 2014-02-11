package net.callumtaylor.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.callumtaylor.news.R;
import net.callumtaylor.view.holder.NameHolder;

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
		NameHolder holder;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

			holder = new NameHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.position = (TextView)convertView.findViewById(R.id.position);

			convertView.setTag(holder);
		}
		else
		{
			holder = (NameHolder)convertView.getTag();
		}

		holder.name.setText(getItem(position));
		holder.position.setText(String.valueOf(position));

		return convertView;
	}
}
