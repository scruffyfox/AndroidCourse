package net.callumtaylor.news;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import net.callumtaylor.model.Story;

public class StoryActivity extends Activity
{
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.story_view);

		if (getIntent().getExtras() != null)
		{
			Story story = (Story)getIntent().getExtras().get("story");

			TextView title = (TextView)findViewById(R.id.title);
			TextView summary = (TextView)findViewById(R.id.summary);
			title.setText(story.getTitle());
			summary.setText(story.getDescription());
		}
	}
}
