package net.callumtaylor.news;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.callumtaylor.controller.adapter.NameAdapter;

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
			"Imogen Brickman",
			"Martinos",
			"Matt Cheatham",
			"Marc Biles",
			"Pete Josling",
			"Phill Caudell",
			"Sam Houghton",
			"Simon Mitchell",
			"Sophie Hardiman",
			"Steve Adams",
			"Tom Bell"
		};

		ListView list = (ListView)findViewById(R.id.list_view);
		NameAdapter adapter = new NameAdapter(this, names);
		list.setAdapter(adapter);
	}
}
