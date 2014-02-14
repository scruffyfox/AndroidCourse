package net.callumtaylor.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import net.callumtaylor.asynchttp.response.JsonResponseHandler;
import net.callumtaylor.controller.adapter.StoryAdapter;
import net.callumtaylor.lib.manager.APIManager;
import net.callumtaylor.model.Story;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener
{
	private StoryAdapter adapter;
	private ListView list;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_view);

		// simple adapter
		Story[] stories = {
			new Story(1, "Story 1", "This is a test story"),
			new Story(2, "Story 2", "This is a test story"),
			new Story(3, "Story 3", "This is a test story"),
			new Story(4, "Story 4", "This is a test story")
		};

		list = (ListView)findViewById(R.id.list_view);
		adapter = new StoryAdapter(this, stories);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		APIManager.getInstance().getStories(new JsonResponseHandler()
		{
			@Override public void onSuccess()
			{
				Log.e("BBCNews", getContent().toString());
			}
		});
	}

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Story story = adapter.getItem(position);

		Intent storyDetails = new Intent(this, StoryActivity.class);
		storyDetails.putExtra("story", story);
		startActivity(storyDetails);
	}

	@Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		Toast.makeText(this, String.format("%s was long clicked!", position), Toast.LENGTH_SHORT).show();
		return true;
	}
}
