package net.callumtaylor.asynchttp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import net.callumtaylor.asynchttp.obj.ConnectionInfo;
import net.callumtaylor.asynchttp.obj.HttpDeleteWithBody;
import net.callumtaylor.asynchttp.obj.HttpsFactory;
import net.callumtaylor.asynchttp.obj.HttpsFactory.EasySSLSocketFactory;
import net.callumtaylor.asynchttp.obj.Packet;
import net.callumtaylor.asynchttp.obj.RequestMode;
import net.callumtaylor.asynchttp.obj.RequestUtil;
import net.callumtaylor.asynchttp.obj.entity.ProgressEntityWrapper;
import net.callumtaylor.asynchttp.obj.entity.ProgressEntityWrapper.ProgressListener;
import net.callumtaylor.asynchttp.response.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.text.TextUtils;

/**
 * @mainpage
 *
 * The client class used for initiating HTTP requests using an AsyncTask. It
 * follows a RESTful paradigm for the connections with the 4 possible methods,
 * GET, POST, PUT and DELETE.
 *
 * <b>Note:</b> Because AsyncHttpClient uses
 * AsyncTask, only one instance can be created at a time. If one client makes 2
 * requests, the first request is canceled for the new request. You can either
 * wait for the first to finish before making the second, or you can create two
 * seperate instances.
 *
 * <b>Depends on</b>
 * <ul>
 * <li>{@link AsyncHttpResponseHandler}</li>
 * <li>{@link HttpEntity}</li>
 * <li>{@link NameValuePair}</li>
 * <li>{@link ConnectionInfo}</li>
 * <li>{@link Packet}</li>
 * <li>{@link RequestMode}</li>
 * <li>{@link RequestUtil}</li>
 * <li>{@link HttpsFactory}</li>
 * </ul>
 * <h1>Example GET</h1>
 *
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient(&quot;http://example.com&quot;);
 * List&lt;NameValuePair&gt; params = new ArrayList&lt;NameValuePair&gt;();
 * params.add(new BasicNameValuePair(&quot;key&quot;, &quot;value&quot;));
 *
 * List&lt;Header&gt; headers = new ArrayList&lt;Header&gt;();
 * headers.add(new BasicHeader(&quot;1&quot;, &quot;2&quot;));
 *
 * client.get(&quot;api/v1/&quot;, params, headers, new JsonResponseHandler()
 * {
 * 	&#064;Override public void onSuccess()
 * 	{
 * 		JsonElement result = getContent();
 * 	}
 * });
 * </pre>
 *
 * <h1>Example DELETE</h1>
 *
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient(&quot;http://example.com&quot;);
 * List&lt;NameValuePair&gt; params = new ArrayList&lt;NameValuePair&gt;();
 * params.add(new BasicNameValuePair(&quot;key&quot;, &quot;value&quot;));
 *
 * List&lt;Header&gt; headers = new ArrayList&lt;Header&gt;();
 * headers.add(new BasicHeader(&quot;1&quot;, &quot;2&quot;));
 *
 * client.delete(&quot;api/v1/&quot;, params, headers, new JsonResponseHandler()
 * {
 * 	&#064;Override public void onSuccess()
 * 	{
 * 		JsonElement result = getContent();
 * 	}
 * });
 * </pre>
 *
 * <h1>Example POST - Single Entity</h1>
 *
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient(&quot;http://example.com&quot;);
 * List&lt;NameValuePair&gt; params = new ArrayList&lt;NameValuePair&gt;();
 * params.add(new BasicNameValuePair(&quot;key&quot;, &quot;value&quot;));
 *
 * List&lt;Header&gt; headers = new ArrayList&lt;Header&gt;();
 * headers.add(new BasicHeader(&quot;1&quot;, &quot;2&quot;));
 *
 * JsonEntity data = new JsonEntity(&quot;{\&quot;key\&quot;:\&quot;value\&quot;}&quot;);
 * GzippedEntity entity = new GzippedEntity(data);
 *
 * client.post(&quot;api/v1/&quot;, params, entity, headers, new JsonResponseHandler()
 * {
 * 	&#064;Override public void onSuccess()
 * 	{
 * 		JsonElement result = getContent();
 * 	}
 * });
 * </pre>
 *
 * <h1>Example POST - Multiple Entity + file</h1>
 *
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient(&quot;http://example.com&quot;);
 * List&lt;NameValuePair&gt; params = new ArrayList&lt;NameValuePair&gt;();
 * params.add(new BasicNameValuePair(&quot;key&quot;, &quot;value&quot;));
 *
 * List&lt;Header&gt; headers = new ArrayList&lt;Header&gt;();
 * headers.add(new BasicHeader(&quot;1&quot;, &quot;2&quot;));
 *
 * MultiPartEntity entity = new MultiPartEntity();
 * FileEntity data1 = new FileEntity(new File(&quot;/IMG_6614.JPG&quot;), &quot;image/jpeg&quot;);
 * JsonEntity data2 = new JsonEntity(&quot;{\&quot;key\&quot;:\&quot;value\&quot;}&quot;);
 * entity.addFilePart(&quot;image1.jpg&quot;, data1);
 * entity.addPart(&quot;content1&quot;, data2);
 *
 * client.post(&quot;api/v1/&quot;, params, entity, headers, new JsonResponseHandler()
 * {
 * 	&#064;Override public void onSuccess()
 * 	{
 * 		JsonElement result = getContent();
 * 	}
 * });
 * </pre>
 *
 * <h1>Example PUT</h1>
 *
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient(&quot;http://example.com&quot;);
 * List&lt;NameValuePair&gt; params = new ArrayList&lt;NameValuePair&gt;();
 * params.add(new BasicNameValuePair(&quot;key&quot;, &quot;value&quot;));
 *
 * List&lt;Header&gt; headers = new ArrayList&lt;Header&gt;();
 * headers.add(new BasicHeader(&quot;1&quot;, &quot;2&quot;));
 *
 * JsonEntity data = new JsonEntity(&quot;{\&quot;key\&quot;:\&quot;value\&quot;}&quot;);
 * GzippedEntity entity = new GzippedEntity(data);
 *
 * client.post(&quot;api/v1/&quot;, params, entity, headers, new JsonResponseHandler()
 * {
 * 	&#064;Override public void onSuccess()
 * 	{
 * 		JsonElement result = getContent();
 * 	}
 * });
 * </pre>
 *
 * Because of the nature of REST, GET and DELETE requests behave in the same
 * way, POST and PUT requests also behave in the same way.
 *
 * @author Callum Taylor &lt;callumtaylor.net&gt; &#064;scruffyfox
 */
public class AsyncHttpClient
{
	private ClientExecutorTask executorTask;
	private Uri requestUri;
	private long requestTimeout = 0L;
	private boolean allowAllSsl = false;

	/**
	 * Creates a new client using a base Url without a timeout
	 * @param baseUrl The base connection url
	 */
	public AsyncHttpClient(String baseUrl)
	{
		this(baseUrl, 0);
	}

	/**
	 * Creates a new client using a base Uri without a timeout
	 * @param baseUrl The base connection uri
	 */
	public AsyncHttpClient(Uri baseUri)
	{
		this(baseUri, 0);
	}

	/**
	 * Creates a new client using a base Url with a timeout in MS
	 * @param baseUrl The base connection url
	 * @param timeout The timeout in MS
	 */
	public AsyncHttpClient(String baseUrl, long timeout)
	{
		this(Uri.parse(baseUrl), timeout);
	}

	/**
	 * Creates a new client using a base Uri with a timeout in MS
	 * @param baseUrl The base connection uri
	 * @param timeout The timeout in MS
	 */
	public AsyncHttpClient(Uri baseUri, long timeout)
	{
		requestUri = baseUri;
		requestTimeout = timeout;
	}

	/**
	 * Cancels a request if it's running
	 */
	public void cancel()
	{
		if (executorTask != null && executorTask.getStatus() == Status.RUNNING)
		{
			executorTask.cancel(true);
		}
	}

	/**
	 * Performs a GET request on the baseUri
	 * @param response The response handler for the request
	 */
	public void get(AsyncHttpResponseHandler response)
	{
		get("", null, null, response);
	}

	/**
	 * Performs a GET request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param response The response handler for the request
	 */
	public void get(String path, AsyncHttpResponseHandler response)
	{
		get(path, null, null, response);
	}

	/**
	 * Performs a GET request on the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void get(List<Header> headers, AsyncHttpResponseHandler response)
	{
		get("", null, headers, response);
	}

	/**
	 * Performs a GET request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void get(List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		get("", params, headers, response);
	}

	/**
	 * Performs a GET request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void get(String path, List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		get(path, params, null, response);
	}

	/**
	 * Performs a GET request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void get(String path, List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		if (!TextUtils.isEmpty(path))
		{
			requestUri = Uri.withAppendedPath(requestUri, path);
		}

		requestUri = RequestUtil.appendParams(requestUri, params);
		executeTask(RequestMode.GET, requestUri, headers, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param response The response handler for the request
	 */
	public void delete(AsyncHttpResponseHandler response)
	{
		delete("", null, null, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param response The response handler for the request
	 */
	public void delete(String path, AsyncHttpResponseHandler response)
	{
		delete(path, null, null, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param response The response handler for the request
	 */
	public void delete(List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		delete("", params, null, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void delete(List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		delete("", params, null, headers, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void delete(HttpEntity postData, AsyncHttpResponseHandler response)
	{
		delete("", null, postData, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void delete(HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		delete("", null, postData, headers, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void delete(List<NameValuePair> params, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		delete("", params, postData, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param response The response handler for the request
	 */
	public void delete(String path, List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		delete(path, params, null, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void delete(String path, List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		delete(path, params, null, headers, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void delete(String path, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		delete(path, null, postData, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void delete(String path, HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		delete(path, null, postData, headers, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void delete(String path, List<NameValuePair> params, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		delete(path, params, postData, null, response);
	}

	/**
	 * Performs a DELETE request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void delete(String path, List<NameValuePair> params, HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		if (!TextUtils.isEmpty(path))
		{
			requestUri = Uri.withAppendedPath(requestUri, path);
		}

		requestUri = RequestUtil.appendParams(requestUri, params);
		executeTask(RequestMode.DELETE, requestUri, headers, postData, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param response The response handler for the request
	 */
	public void post(AsyncHttpResponseHandler response)
	{
		post("", null, null, null, response);
	}

	/**
	 * Performs a POST request on the baseUr
	 * @param path The path extended from the baseUri
	 * @param response The response handler for the request
	 */
	public void post(String path, AsyncHttpResponseHandler response)
	{
		post(path, null, null, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param response The response handler for the request
	 */
	public void post(List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		post("", params, null, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void post(List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		post("", params, null, headers, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void post(HttpEntity postData, AsyncHttpResponseHandler response)
	{
		post("", null, postData, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void post(HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		post("", null, postData, headers, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void post(List<NameValuePair> params, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		post("", params, postData, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param response The response handler for the request
	 */
	public void post(String path, List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		post(path, params, null, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void post(String path, List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		post(path, params, null, headers, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void post(String path, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		post(path, null, postData, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void post(String path, HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		post(path, null, postData, headers, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void post(String path, List<NameValuePair> params, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		post(path, params, postData, null, response);
	}

	/**
	 * Performs a POST request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void post(String path, List<NameValuePair> params, HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		if (!TextUtils.isEmpty(path))
		{
			requestUri = Uri.withAppendedPath(requestUri, path);
		}

		requestUri = RequestUtil.appendParams(requestUri, params);
		executeTask(RequestMode.POST, requestUri, headers, postData, response);
	}

	/**
	 * Performs a PUT request on the baseUr
	 * @param response The response handler for the request
	 */
	public void put(AsyncHttpResponseHandler response)
	{
		put("", null, null, null, response);
	}

	/**
	 * Performs a PUT request on the baseUr
	 * @param path The path extended from the baseUri
	 * @param response The response handler for the request
	 */
	public void put(String path, AsyncHttpResponseHandler response)
	{
		put(path, null, null, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param response The response handler for the request
	 */
	public void put(List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		put("", params, null, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void put(List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		put("", params, null, headers, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void put(HttpEntity postData, AsyncHttpResponseHandler response)
	{
		put("", null, postData, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void put(HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		put("", null, postData, headers, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void put(List<NameValuePair> params, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		put("", params, postData, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param response The response handler for the request
	 */
	public void put(String path, List<NameValuePair> params, AsyncHttpResponseHandler response)
	{
		put(path, params, null, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void put(String path, List<NameValuePair> params, List<Header> headers, AsyncHttpResponseHandler response)
	{
		put(path, params, null, headers, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void put(String path, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		put(path, null, postData, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void put(String path, HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		put(path, null, postData, headers, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param response The response handler for the request
	 */
	public void put(String path, List<NameValuePair> params, HttpEntity postData, AsyncHttpResponseHandler response)
	{
		put(path, params, postData, null, response);
	}

	/**
	 * Performs a PUT request on the baseUri
	 * @param path The path extended from the baseUri
	 * @param params The Query params to append to the baseUri
	 * @param postData The post data entity to post to the server
	 * @param headers The request headers for the connection
	 * @param response The response handler for the request
	 */
	public void put(String path, List<NameValuePair> params, HttpEntity postData, List<Header> headers, AsyncHttpResponseHandler response)
	{
		if (!TextUtils.isEmpty(path))
		{
			requestUri = Uri.withAppendedPath(requestUri, path);
		}

		requestUri = RequestUtil.appendParams(requestUri, params);
		executeTask(RequestMode.PUT, requestUri, headers, postData, response);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void executeTask(RequestMode mode, Uri uri, List<Header> headers, HttpEntity sendData, AsyncHttpResponseHandler response)
	{
		if (executorTask != null || (executorTask != null && (executorTask.getStatus() == Status.RUNNING || executorTask.getStatus() == Status.PENDING)))
		{
			executorTask.cancel(true);
			executorTask = null;
		}

		executorTask = new ClientExecutorTask(mode, uri, headers, sendData, response);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			executorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			executorTask.execute();
		}
	}

	public class ClientExecutorTask extends AsyncTask<Void, Packet, Void>
	{
		private static final int BUFFER_SIZE = 1 * 1024 * 8;

		private final AsyncHttpResponseHandler response;
		private final Uri requestUri;
		private final List<Header> requestHeaders;
		private final HttpEntity postData;
		private final RequestMode requestMode;

		public ClientExecutorTask(RequestMode mode, Uri request, List<Header> headers, HttpEntity postData, AsyncHttpResponseHandler response)
		{
			this.response = response;
			this.requestUri = request;
			this.requestHeaders = headers;
			this.postData = postData;
			this.requestMode = mode;
		}

		@Override protected void onPreExecute()
		{
			super.onPreExecute();
			if (this.response != null)
			{
				this.response.getConnectionInfo().connectionUrl = requestUri.toString();
				this.response.getConnectionInfo().connectionTime = System.currentTimeMillis();
				this.response.getConnectionInfo().requestMethod = requestMode;
				this.response.onSend();
			}
		}

		@Override protected Void doInBackground(Void... params)
		{
			HttpClient httpClient;

			if (allowAllSsl)
			{
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

				HttpParams httpParams = new BasicHttpParams();
				httpParams.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
				httpParams.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
				httpParams.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
				HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

				ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
				httpClient = new DefaultHttpClient(cm, httpParams);
			}
			else
			{
				httpClient = new DefaultHttpClient();
			}

			HttpContext httpContext = new BasicHttpContext();
			HttpRequestBase request = null;

			try
			{
				System.setProperty("http.keepAlive", "false");

				if (requestMode == RequestMode.GET)
				{
					request = new HttpGet(requestUri.toString());
				}
				else if (requestMode == RequestMode.POST)
				{
					request = new HttpPost(requestUri.toString());
				}
				else if (requestMode == RequestMode.PUT)
				{
					request = new HttpPut(requestUri.toString());
				}
				else if (requestMode == RequestMode.DELETE)
				{
					request = new HttpDeleteWithBody(requestUri.toString());
				}

				HttpParams p = httpClient.getParams();
				HttpConnectionParams.setConnectionTimeout(p, (int)requestTimeout);
				HttpConnectionParams.setSoTimeout(p, (int)requestTimeout);
				request.setHeader("Connection", "close");

				if (postData != null)
				{
					request.addHeader(postData.getContentType().getName(), postData.getContentType().getValue());
				}

				if (requestHeaders != null)
				{
					for (Header header : requestHeaders)
					{
						request.addHeader(header.getName(), header.getValue());
					}
				}

				if ((requestMode == RequestMode.POST || requestMode == RequestMode.PUT || requestMode == RequestMode.DELETE) && postData != null)
				{
					final long contentLength = postData.getContentLength();
					if (this.response != null && !isCancelled())
					{
						this.response.getConnectionInfo().connectionLength = contentLength;
					}

					((HttpEntityEnclosingRequestBase)request).setEntity(new ProgressEntityWrapper(postData, new ProgressListener()
					{
						@Override public void onBytesTransferred(byte[] buffer, int len, long transferred)
						{
							if (response != null)
							{
								response.onPublishedUploadProgress(buffer, len, contentLength);
								response.onPublishedUploadProgress(buffer, len, transferred, contentLength);

								publishProgress(new Packet(transferred, contentLength, false));
							}
						}
					}));
				}

				// Get the response
				HttpResponse response = httpClient.execute(request, httpContext);
				int responseCode = response.getStatusLine().getStatusCode();

				if (response.getAllHeaders() != null && this.response != null)
				{
					this.response.getConnectionInfo().responseHeaders = new LinkedHashMap<String, String>();
					for (Header header : response.getAllHeaders())
					{
						this.response.getConnectionInfo().responseHeaders.put(header.getName(), header.getValue());
					}
				}

				if (response.getEntity() != null)
				{
					String encoding = response.getEntity().getContentEncoding() == null ? "" : response.getEntity().getContentEncoding().getValue();
					long contentLength = response.getEntity().getContentLength();
					InputStream responseStream;
					InputStream stream = response.getEntity().getContent();

					if ("gzip".equals(encoding))
					{
						responseStream = new GZIPInputStream(new BufferedInputStream(stream, BUFFER_SIZE));
					}
					else
					{
						responseStream = new BufferedInputStream(stream, BUFFER_SIZE);
					}

					if (this.response != null && !isCancelled())
					{
						this.response.getConnectionInfo().responseCode = responseCode;
					}

					try
					{
						if (this.response != null && contentLength != 0)
						{
							this.response.onBeginPublishedDownloadProgress(responseStream, this, contentLength);
						}
					}
					catch (SocketTimeoutException timeout)
					{
						responseCode = 0;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				if (this.response != null && !isCancelled())
				{
					this.response.getConnectionInfo().responseTime = System.currentTimeMillis();
					this.response.getConnectionInfo().responseCode = responseCode;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (this.response != null && !isCancelled())
			{
				if (this.response.getConnectionInfo().responseCode < 400 && this.response.getConnectionInfo().responseCode > 100)
				{
					this.response.onSuccess();
				}
				else
				{
					this.response.onFailure();
				}
			}

			return null;
		}

		@Override protected void onProgressUpdate(Packet... values)
		{
			super.onProgressUpdate(values);

			if (this.response != null && !isCancelled())
			{
				if (values[0].isDownload)
				{
					this.response.onPublishedDownloadProgressUI(values[0].length, values[0].total);
				}
				else
				{
					this.response.onPublishedUploadProgressUI(values[0].length, values[0].total);
				}
			}
		}

		@Override protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			if (this.response != null && !isCancelled())
			{
				this.response.beforeCallback();
				this.response.beforeFinish();
				this.response.onFinish();
				this.response.onFinish(this.response.getConnectionInfo().responseCode >= 400 || this.response.getConnectionInfo().responseCode == 0);
			}
		}

		public void postPublishProgress(Packet... values)
		{
			publishProgress(values);
		}
	}

	public void setAllowAllSsl(boolean allow)
	{
		this.allowAllSsl = allow;
	}
}