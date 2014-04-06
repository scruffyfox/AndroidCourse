package net.callumtaylor.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.response.BitmapResponseHandler;
import net.callumtaylor.model.Story;
import net.callumtaylor.news.R;
import net.callumtaylor.view.holder.StoryHolder;

public class StoryAdapter extends BaseAdapter
{
	private Story[] objects;
	private Context context;

	public StoryAdapter(Context context)
	{
		this.context = context;
		objects = new Story[0];
	}

	public void setObjects(Story[] stories)
	{
		this.objects = stories;
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
		return ((Object)objects[position]).hashCode();
	}

	@Override public View getView(int position, View convertView, ViewGroup parent)
	{
		final StoryHolder holder;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);

			holder = new StoryHolder();
			holder.thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.summary = (TextView)convertView.findViewById(R.id.summary);

			convertView.setTag(holder);
		}
		else
		{
			holder = (StoryHolder)convertView.getTag();
		}

		holder.thumbnail.setImageBitmap(null);

		if (holder.thumbnail.getTag() != null)
		{
			((AsyncHttpClient)holder.thumbnail.getTag()).cancel();
		}

		AsyncHttpClient loader = new AsyncHttpClient(getItem(position).getThumbnail());
		loader.get(new BitmapResponseHandler()
		{
			@Override public void onSuccess(){}

			@Override public void onFinish(boolean failed)
			{
				if (!failed)
				{
					holder.thumbnail.setTag(null);
					holder.thumbnail.setImageBitmap(getContent());
				}
			}
		});
		holder.thumbnail.setTag(loader);

		holder.name.setText(getItem(position).getTitle());
		holder.summary.setText(getItem(position).getDescription());

		return convertView;
	}
}
