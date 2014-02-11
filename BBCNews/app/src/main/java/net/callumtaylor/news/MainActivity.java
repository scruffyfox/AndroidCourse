package net.callumtaylor.news;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import net.callumtaylor.controller.adapter.NameAdapter;

public class MainActivity extends Activity implements OnItemClickListener
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
		list.setOnItemClickListener(this);
	}

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Toast.makeText(this, String.format("%s was clicked!", position), Toast.LENGTH_SHORT).show();
	}
}
