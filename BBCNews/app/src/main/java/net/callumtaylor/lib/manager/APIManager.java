package net.callumtaylor.lib.manager;

import android.util.Log;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.response.JsonResponseHandler;

public class APIManager
{
	private static APIManager INSTANCE;

	public static APIManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new APIManager();
		}

		return INSTANCE;
	}

	public AsyncHttpClient getStories()
	{
		AsyncHttpClient client = new AsyncHttpClient("http://android.3sidedcube.com/");
		client.get("bbcnews?type=topics", new JsonResponseHandler()
		{
			@Override public void onSuccess()
			{
				Log.e("BBCNews", getContent().toString());
			}
		});

		return client;
	}
}
