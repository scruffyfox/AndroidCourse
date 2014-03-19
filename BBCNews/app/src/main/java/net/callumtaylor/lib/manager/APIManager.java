package net.callumtaylor.lib.manager;

import android.util.Log;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.response.AsyncHttpResponseHandler;
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

	public AsyncHttpClient getStories(AsyncHttpResponseHandler response)
	{
		AsyncHttpClient client = new AsyncHttpClient("http://android.3sidedcube.com/");
		client.get("bbcnews?type=stories", response);

		return client;
	}
}
