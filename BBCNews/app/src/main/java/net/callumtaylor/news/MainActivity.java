package net.callumtaylor.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import net.callumtaylor.controller.adapter.StoryAdapter;
import net.callumtaylor.model.Story;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener
{
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

		ListView list = (ListView)findViewById(R.id.list_view);
		StoryAdapter adapter = new StoryAdapter(this, stories);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
	}

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Intent storyDetails = new Intent(this, StoryActivity.class);
		startActivity(storyDetails);
	}

	@Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		Toast.makeText(this, String.format("%s was long clicked!", position), Toast.LENGTH_SHORT).show();
		return true;
	}
}
