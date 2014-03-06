package net.callumtaylor.lib.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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

	public long fileModifiedDate(String filename)
	{
		return new File(filename).lastModified();
	}

	public boolean fileExists(String filename)
	{
		return new File(filename).exists();
	}

	public void save(String filename, Serializable data)
	{
		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename), 8192);
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(data);
			os.flush();
			os.close();
			bos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Object load(String filename)
	{
		try
		{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename), 8192);
			ObjectInputStream is = new ObjectInputStream(bis);
			Object data = is.readObject();
			is.close();
			bis.close();

			return data;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
