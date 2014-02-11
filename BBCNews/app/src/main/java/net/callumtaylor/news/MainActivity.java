package net.callumtaylor.news;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity
{
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_view);

		// simple adapter
		String[] names = {
			"Andrew Dong",
			"Callum Taylor",
			"Dan Gough",
			"Duncan Cook",
			"Dave Stockdale",
			"Martinos",
			"Matt Cheatham",
			"Pete Josling",
			"Phill Caudell",
			"Steve Adams",
			"Sam Houghton",
			"Tom Bell"
		};

		ListView list = (ListView)findViewById(R.id.list_view);
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
		list.setAdapter(adapter);
	}
}
