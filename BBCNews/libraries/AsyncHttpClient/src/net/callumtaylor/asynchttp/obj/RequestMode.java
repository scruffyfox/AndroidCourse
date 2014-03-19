package net.callumtaylor.asynchttp.obj;

public enum RequestMode
{
	/**
	 * Gets data from the server as String
	 */
	GET("GET"),
	/**
	 * Posts to a server
	 */
	POST("POST"),
	/**
	 * Puts data to the server (equivilant to POST with relevant headers)
	 */
	PUT("PUT"),
	/**
	 * Deletes data from the server (equivilant to GET with relevant
	 * headers)
	 */
	DELETE("DELETE");

	private String canonicalStr = "";
	private RequestMode(String canonicalStr)
	{
		this.canonicalStr = canonicalStr;
	}

	public String getCanonical()
	{
		return this.canonicalStr;
	}
}