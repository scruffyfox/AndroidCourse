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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.callumtaylor.asynchttp.response.JsonResponseHandler;
import net.callumtaylor.controller.adapter.StoryAdapter;
import net.callumtaylor.lib.manager.APIManager;
import net.callumtaylor.lib.manager.CacheManager;
import net.callumtaylor.model.Story;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener
{
	private StoryAdapter adapter;
	private ListView list;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_view);

		list = (ListView)findViewById(R.id.list_view);
		adapter = new StoryAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		long lastModified = System.currentTimeMillis();
		if (CacheManager.getInstance().fileExists(getFilesDir().getAbsolutePath() + "/stories"))
		{
			lastModified = CacheManager.getInstance().fileModifiedDate(getFilesDir().getAbsolutePath() + "/stories");
			Story[] stories = (Story[])CacheManager.getInstance().load(getFilesDir().getAbsolutePath() + "/stories");
			adapter.setObjects(stories);
			adapter.notifyDataSetChanged();
		}

		if (System.currentTimeMillis() - lastModified < 60 * 1 * 1000)
		{
			APIManager.getInstance().getStories(new JsonResponseHandler()
			{
				private Story[] stories;

				@Override public void onSuccess()
				{
					JsonArray storiesArray = getContent().getAsJsonObject().get("stories").getAsJsonArray();
					stories = new Story[storiesArray.size()];

					for (int index = 0; index < stories.length; index++)
					{
						JsonObject storyObject = storiesArray.get(index).getAsJsonObject();
						stories[index] = new Story().createFrom(storyObject);
					}

					CacheManager.getInstance().save(getFilesDir().getAbsolutePath() + "/stories", stories);
				}

				@Override public void onFinish(boolean failed)
				{
					adapter.setObjects(stories);
					adapter.notifyDataSetChanged();
				}
			});
		}
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
