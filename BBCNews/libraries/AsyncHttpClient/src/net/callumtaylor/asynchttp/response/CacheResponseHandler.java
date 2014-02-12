package net.callumtaylor.asynchttp.response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Caches the response directly to disk. Useful when downloading
 * large files. <b>note</b> This will delete any existing files
 * with the same file name
 */
public abstract class CacheResponseHandler extends AsyncHttpResponseHandler
{
	private final File mFile;
	private BufferedOutputStream fos;

	public CacheResponseHandler(String filePath)
	{
		mFile = new File(filePath);

		if (mFile.exists())
		{
			mFile.delete();
		}

		try
		{
			mFile.createNewFile();
			fos = new BufferedOutputStream(new FileOutputStream(mFile));
		}
		catch (Exception e){}
	}

	@Override public void onPublishedDownloadProgress(byte[] chunk, int chunkLength, long totalProcessed, long totalLength)
	{
		if (chunk != null && fos != null)
		{
			try
			{
				fos.write(chunk, 0, chunkLength);
			}
			catch (Exception e){}
		}
		else
		{
			try
			{
				fos.flush();
				fos.close();
			}
			catch (Exception e){}
		}
	}

	/**
	 * Processes the response from the stream.
	 * This is <b>not</b> ran on the UI thread
	 *
	 * @return The data represented as a file object
	 */
	@Override public File getContent()
	{
		return mFile;
	}
}