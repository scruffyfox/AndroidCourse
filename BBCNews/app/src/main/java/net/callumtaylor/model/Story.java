package net.callumtaylor.model;

public class Story
{
	public int id;
	public String title;
	public String summary;
	public String link;
	public long published;

	public Story(int id, String title, String summary)
	{
		this.id = id;
		this.title = title;
		this.summary = summary;
	}
}
