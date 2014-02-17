package net.callumtaylor.lib.manager;

public class CacheManager
{
	private static CacheManager INSTANCE;

	public static CacheManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new CacheManager();
		}

		return INSTANCE;
	}
}
