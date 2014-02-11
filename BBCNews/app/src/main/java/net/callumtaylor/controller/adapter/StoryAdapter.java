package net.callumtaylor.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.callumtaylor.model.Story;
import net.callumtaylor.news.R;
import net.callumtaylor.view.holder.NameHolder;

public class StoryAdapter extends BaseAdapter
{
	private Story[] objects;
	private Context context;

	public StoryAdapter(Context context, Story[] objects)
	{
		this.context = context;
		this.objects = objects;
	}

	@Override public int getCount()
	{
		return objects.length;
	}

	@Override public Story getItem(int position)
	{
		return objects[position];
	}

	@Override public long getItemId(int position)
	{
		return objects[position].id;
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

		holder.name.setText(getItem(position).title);
		holder.position.setText(String.valueOf(getItem(position).id));

		return convertView;
	}
}
