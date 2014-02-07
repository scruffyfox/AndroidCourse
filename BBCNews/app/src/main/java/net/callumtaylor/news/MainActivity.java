package net.callumtaylor.news;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity
{
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_view);

		TextView text = (TextView)findViewById(R.id.text_view);
		text.setText("Hello World!");
	}
}
