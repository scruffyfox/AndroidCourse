package net.callumtaylor.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Story implements Serializable
{
	private String title;
	private String description;
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
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
			setDescription(story.get("description").getAsString());
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
