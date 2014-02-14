package net.callumtaylor.lib.manager;

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
}
