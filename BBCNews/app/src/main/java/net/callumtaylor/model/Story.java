package net.callumtaylor.model;

import java.io.Serializable;

public class Story implements Serializable
{
	private int id;
	private String title;
	private String summary;
	private String link;
	private long published;

	public Story(int id, String title, String summary)
	{
		this.id = id;
		this.title = title;
		this.summary = summary;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

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

	public long getPublished()
	{
		return published;
	}

	public void setPublished(long published)
	{
		this.published = published;
	}
}
