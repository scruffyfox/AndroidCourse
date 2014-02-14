package net.callumtaylor.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Story implements Serializable
{
	private String title;
	private String summary;
	private String link;
	private String thumbnail;
	private long published;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getThumbnail()
	{
		return thumbnail;
	}

	public void setThumbnail(String thumbnail)
	{
		this.thumbnail = thumbnail;
	}

	public long getPublished()
	{
		return published;
	}

	public void setPublished(long published)
	{
		this.published = published;
	}

	public Story createFrom(JsonObject story)
	{
		try
		{
			setTitle(story.get("title").getAsString());
			setSummary(story.get("summary").getAsString());
			setLink(story.get("link").getAsString());
			setThumbnail(story.get("thumbnail").getAsString());
			setPublished(story.get("published").getAsInt() * 1000L);

			return this;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
