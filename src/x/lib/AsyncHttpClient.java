/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 *
 * @author Callum Taylor
 **/
package x.lib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import x.type.ConnectionInfo;
import x.type.FileHttpParams;
import x.type.HttpParams;
import x.type.ItemList;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * @brief The client class used for initiating HTTP requests
 *
 * POST/PUT
 * When using POST/PUT the object data that gets sent must be either a byte array of serialized data, or a derivative of the String class.
 * Sending as a byte array should be used with the correct headers of Content-Type: multipart/form-data. See {@link getMultipartFormHeader}
 * When sending as a String, the data should be interpreted by the server as RAW input.
 *
 * You can use the method {@link getFormPostDataWithFiles} to convert a HttpParam object to serialized data. You can also send files by using
 * the {@link AsyncHttpClient.FileHttpParams} params.
 *
 * <b>Depends on</b>
 * <ul>
 * 	<li>{@link AsyncHttpResponse}</li>
 * 	<li>{@link HttpParams}</li>
 * 	<li>{@link FileHttpParams}</li>
 * 	<li>{@link ConnectionInfo}</li>
 * 	<li>{@link ItemList}</li>
 * </ul>
 *
 * GET
 * When using the GET request, the response is recieved as a STRING. If you are expecting a binary file such as an image or a database, use DOWNLOAD
 *
 * DOWNLOAD
 * Use this method when you want to recieve a binary file from a server such as an image or database. The response is received as a byte[].
 *
 * The response life cycle:
 *  - OnSend
 *  - OnBytesProcessed (continuous)
 *  - BeforeFinished
 *  - OnSuccess / OnFailure
 *  - OnFinish
 *
 * Example GET:
 * @code
 * AsyncHttpClient APIDownloader = new AsyncHttpClient();
 * APIDownloader.get
 * (
 * 	"http://url.com/api.json",
 * 	new HttpParams
 * 	(
 * 		new String[]{"id", 15}
 * 	),
 * 	new AsyncHttpResponse()
 * 	{
 * 		public void onSuccess(Object response)
 * 		{
 * 			//	do stuff
 * 		}
 * 	}
 * );
 * @endcode
 */
public class AsyncHttpClient
{
	private String mUrl;
	private HttpParams mHeaders, mRequestParams;
	private Object mPost;
	private RequestMode mRequestMode;
	private AsyncHttpResponse mResponse;
	private static final String BOUNDARY = "----------XLibraryAsyncRequest35146";
	public static boolean followRedirect = true;
	public static HttpParams globalHeaders = new HttpParams();

	/**
	 * @brief The request mode enumerator for making AsyncHttp requests
	 */
	public enum RequestMode
	{
		/**
		 * Posts to a server
		 */
		POST,
		/**
		 * Gets data from the server as String
		 */
		GET,
		/**
		 * Puts data to the server (equivilant to POST with relevant headers)
		 */
		PUT,
		/**
		 * Deletes data from the server (equivilant to GET with relevant headers)
		 */
		DELETE,
		/**
		 * Downloads binary data from the server
		 */
		DOWNLOAD
	}

	private HttpLoader mHttpLoader;
	private AsyncHttpResponse mAsyncHttpResponse;

	/**
	 * Default constructor
	 */
	public AsyncHttpClient()
	{
		mHttpLoader = new HttpLoader(0);
	}

	/**
	 * Default constructor
	 * @param timeout The timeout delay for the request
	 */
	public AsyncHttpClient(int timeout)
	{
		mHttpLoader = new HttpLoader(timeout);
	}

	/**
	 * Gets the boundary string for posting headers
	 * @return The boundary string
	 */
	public static String getBoundary()
	{
		return BOUNDARY;
	}

	/**
	 * Gets the multipart form header used with Posting data to a server
	 * @return The header as a String[] which can be used with HttpParams.addParam(header)
	 */
	public static String[] getMultipartFormHeader()
	{
		return new String[]{"Content-Type", "multipart/form-data; boundary=" + getBoundary()};
	}

	/**
	 * Gets the binary equivilant of a HttpParam object
	 * @param values The values to convert
	 * @return The binary data or null if failed
	 */
	public static byte[] getFormPostData(HttpParams values)
	{
		try
		{
			return getFormPostDataWithFiles(values, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the binary equivilant of a HttpParam object and FileHttpParam files object
	 * @param values The values and files to convert
	 * @return The binary data or null if failed
	 */
	public static byte[] getFormPostDataWithFiles(HttpParams values, FileHttpParams files)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			StringBuffer res = new StringBuffer("\r\n").append("--").append(getBoundary()).append("\r\n");

			ItemList<String[]> headers = values.getHeaders();
			int size = headers.size();

			for (int index = 0; index < size; index++)
			{
				String key = headers.get(index)[0];
				String val = headers.get(index)[1];

				res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n").append("\r\n").append(val).append("\r\n").append("--").append(getBoundary()).append("\r\n");
			}

			bos.write(res.toString().getBytes());

			if (files != null && files.size() > 0)
			{
				int count = files.size();
				for (int index = 0; index < count; index++)
				{
					StringBuffer fileRes = new StringBuffer();
					fileRes.append("Content-Disposition: form-data; name=\"").append(files.getFieldName(index))
					.append("\"; filename=\"").append(files.getFileName(index)).append("\"\r\n")
					.append("Content-Type: ").append(files.getFileType(index)).append("\r\n\r\n");

					bos.write(fileRes.toString().getBytes());
					bos.write(files.getFileContents(index));
				}
			}

			StringBuffer end = new StringBuffer("\r\n--" + getBoundary() + "--\r\n");
			bos.write(end.toString().getBytes());

			return bos.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Cancels the request
	 */
	public void cancel()
	{
		mHttpLoader.cancel(true);
	}

	/**
	 * Creates a AsyncHttpClient for later use
	 * @param requestMode The request mode
	 * @param urlStr The URL
	 * @return The newly created async client
	 */
	public static AsyncHttpClient createClient(RequestMode requestMode, String urlStr)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.mUrl = urlStr;
		client.mRequestMode = requestMode;

		return client;
	}

	/**
	 * Creates a AsyncHttpClient for later use
	 * @param requestMode The request mode
	 * @param urlStr The URL
	 * @param response The response for the request
	 * @return The newly created async client
	 */
	public static AsyncHttpClient createClient(RequestMode requestMode, String urlStr, AsyncHttpResponse response)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.mUrl = urlStr;
		client.mRequestMode = requestMode;
		client.mResponse = response;

		return client;
	}

	/**
	 * Creates a AsyncHttpClient for later use
	 * @param requestMode The request mode
	 * @param urlStr The URL
	 * @param postData The post data
	 * @return The newly created async client
	 */
	public static AsyncHttpClient createClient(RequestMode requestMode, String urlStr, Object postData)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.mUrl = urlStr;
		client.mPost = postData;
		client.mRequestMode = requestMode;

		return client;
	}

	/**
	 * Creates a AsyncHttpClient for later use
	 * @param requestMode The request mode
	 * @param urlStr The URL
	 * @param requestParameters The request params for the url
	 * @param postData The post data
	 * @return The newly created async client
	 */
	public static AsyncHttpClient createClient(RequestMode requestMode, String urlStr, HttpParams requestParameters, Object postData)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.mUrl = urlStr;
		client.mPost = postData;
		client.mRequestParams = requestParameters;
		client.mRequestMode = requestMode;

		return client;
	}

	/**
	 * Creates a AsyncHttpClient for later use
	 * @param requestMode The request mode
	 * @param urlStr The URL
	 * @param requestParameters The request params for the url
	 * @param postData The post data
	 * @param httpHeaders The HTTP Headers to send to the server
	 * @return The newly created async client
	 */
	public static AsyncHttpClient createClient(RequestMode requestMode, String urlStr, HttpParams requestParameters, Object postData, HttpParams httpHeaders)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.mUrl = urlStr;
		client.mHeaders = httpHeaders;
		client.mPost = postData;
		client.mRequestParams = requestParameters;
		client.mRequestMode = requestMode;

		return client;
	}

	/**
	 * Creates a AsyncHttpClient for later use
	 * @param requestMode The request mode
	 * @param urlStr The URL
	 * @param requestParameters The request params for the url
	 * @param postData The post data
	 * @param httpHeaders The HTTP Headers to send to the server
	 * @param response The response
	 * @return The newly created async client
	 */
	public static AsyncHttpClient createClient(RequestMode requestMode, String urlStr, HttpParams requestParameters, Object postData, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.mUrl = urlStr;
		client.mHeaders = httpHeaders;
		client.mPost = postData;
		client.mRequestParams = requestParameters;
		client.mRequestMode = requestMode;
		client.mResponse = response;

		return client;
	}

	/**
	 * Sets the response of a client
	 * @param response The response to set
	 */
	public void setResponse(AsyncHttpResponse response)
	{
		mResponse = response;
	}

	/**
	 * Gets the response for the client
	 * @return The response for the client
	 */
	public AsyncHttpResponse getResponse()
	{
		return mResponse;
	}

	/**
	 * Executes a pre-created request built by {@link AsyncHttpClient.createClient}
	 */
	public void execute()
	{
		if (mUrl != null)
		{
			request(mRequestMode, mUrl, mPost, mRequestParams, mHeaders, mResponse);
		}
	}

	/**
	 * Executes a pre-created request built by {@link AsyncHttpClient.createClient}
	 */
	protected void execute(AsyncHttpResponse response)
	{
		if (mUrl != null)
		{
			request(mRequestMode, mUrl, mPost, mRequestParams, mHeaders, response);
		}
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 */
	public void request(RequestMode requestMode, String urlStr)
	{
		request(requestMode, urlStr, null, null);
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 * @param response The response interface for the request call back
	 */
	public void request(RequestMode requestMode, String urlStr, AsyncHttpResponse response)
	{
		request(requestMode, urlStr, null, response);
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param response The response interface for the request call back
	 */
	public void request(RequestMode requestMode, String urlStr, Object postData, AsyncHttpResponse response)
	{
		request(requestMode, urlStr, postData, response);
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 * @param httpHeaders The headers to be sent to the server
	 * @param response The response interface for the request call back
	 */
	public void request(RequestMode requestMode, String urlStr, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		request(requestMode, urlStr, null, httpHeaders, response);
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 * @param requestParameters The request params
	 * @param httpHeaders The headers to be sent to the server
	 * @param response The response interface for the request call back
	 */
	public void request(RequestMode requestMode, String urlStr, HttpParams requestParameters, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		request(requestMode, urlStr, null, requestParameters, httpHeaders, response);
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param httpHeaders The request headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void request(RequestMode requestMode, String urlStr, Object postData, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		request(requestMode, urlStr, postData, null, httpHeaders, response);
	}

	/**
	 * Initiates a request with a server
	 * @param requestMode The request mode for the connection
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param requestParameters The request parameters for the URL
	 * @param httpHeaders The request headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void request(RequestMode requestMode, String urlStr, Object postData, HttpParams requestParameters, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;

		switch (requestMode)
		{
			case POST:
			{
				post(urlStr, postData, requestParameters, httpHeaders, response);
				break;
			}

			case GET:
			{
				get(urlStr, requestParameters, httpHeaders, response);
				break;
			}

			case PUT:
			{
				put(urlStr, postData, requestParameters, httpHeaders, response);
				break;
			}

			case DELETE:
			{
				delete(urlStr, requestParameters, httpHeaders, response);
				break;
			}

			case DOWNLOAD:
			{
				download(urlStr, requestParameters, httpHeaders, response);
				break;
			}
		}
	}

	/**
	 * Downloads a file from a url as an instance of a byte array
	 * @param url The url to download
	 * @param response The response
	 */
	public void download(String url, AsyncHttpResponse response)
	{
		download(url, null, null, response);
	}

	/**
	 * Downloads a file from a url as an instance of a byte array
	 * @param url The url to download
	 * @param requestParameters The request parameters
	 * @param response The response
	 */
	public void download(String url, HttpParams requestParameters, AsyncHttpResponse response)
	{
		download(url, requestParameters, null, response);
	}

	/**
	 * Downloads a file from a url as an instance of a byte array
	 * @param url The url to download
	 * @param requestParameters The request parameters
	 * @param params The header parameters
	 * @param response The response
	 */
	public void download(String url, HttpParams requestParameters, HttpParams params, AsyncHttpResponse response)
	{
		String urlStr = url;

		if (requestParameters != null)
		{
			requestParameters.URLEncode();
			urlStr += requestParameters.toString();
		}

		mHttpLoader.download(urlStr, params, response);
	}

	/**
	 * Initiates a get request with a server
	 * @param endpoint The URL to the server
	 * @param requestParameters The request parameters in the format ?<b>param</b>=value&<b>param</b>=value
	 * @param response The response interface for the request call back
	 */
	public void get(String endpoint, String requestParameters, AsyncHttpResponse response)
	{
		HttpParams params = HttpParams.parseUrl(endpoint);

		if (endpoint.indexOf("?") > -1)
		{
			endpoint = endpoint.substring(0, endpoint.indexOf("?"));
		}

		get(endpoint, params, null, response);
	}

	/**
	 * Initiates a get request with a server
	 * @param endpoint The URL to the server
	 * @param response The response interface for the request call back
	 */
	public void get(String endpoint, AsyncHttpResponse response)
	{
		HttpParams params = HttpParams.parseUrl(endpoint);

		if (endpoint.indexOf("?") > -1)
		{
			endpoint = endpoint.substring(0, endpoint.indexOf("?"));
		}

		get(endpoint, params, null, response);
	}

	/**
	 * Initiates a get request with a server
	 * @param endpoint The URL to the server
	 * @param requestParameters The request parameters for the URL
	 * @param response The response interface for the request call back
	 */
	public void get(String endpoint, HttpParams requestParameters, AsyncHttpResponse response)
	{
		get(endpoint, requestParameters, null, response);
	}

	/**
	 * Initiates a get request with a server
	 * @param endpoint The URL to the server
	 * @param requestParameters The request parameters for the URL
	 * @param headers The headers to be sent to the server
	 * @param response The response interface for the request call back
	 */
	public void get(String endpoint, HttpParams requestParameters, HttpParams headers, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;
		String urlStr = endpoint;

		if (requestParameters != null)
		{
			requestParameters.URLEncode();
			urlStr += requestParameters.toString();
		}

		mHttpLoader.get(urlStr, headers, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, AsyncHttpResponse response)
	{
		delete(endpoint, "", null, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param requestParameters The request parameters for the URL
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, String requestParameters, AsyncHttpResponse response)
	{
		delete(endpoint, new HttpParams(requestParameters), null, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param requestParameters The request parameters for the URL
	 * @param headers The headers to be esnt to the server
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, String requestParameters, HttpParams headers, AsyncHttpResponse response)
	{
		delete(endpoint, new HttpParams(requestParameters), headers, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param requestParameters The request parameters for the URL
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, HttpParams requestParameters, AsyncHttpResponse response)
	{
		delete(endpoint, requestParameters, null, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param requestParameters The request parameters for the URL
	 * @param headers The headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, HttpParams requestParameters, HttpParams headers, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;
		String urlStr = endpoint;

		if (requestParameters != null)
		{
			requestParameters.URLEncode();
			urlStr += requestParameters.toString();
		}

		mHttpLoader.delete(urlStr, headers, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param response The response interface for the request call back
	 */
	public void post(String urlStr, Object postData, AsyncHttpResponse response)
	{
		post(urlStr, postData, null, null, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param httpHeaders The request headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void post(String urlStr, Object postData, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		post(urlStr, postData, null, httpHeaders, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param requestParameters The request parameters for the URL
	 * @param httpHeaders The request headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void post(String urlStr, Object postData, HttpParams requestParameters, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;
		String url = urlStr;

		if (requestParameters != null)
		{
			requestParameters.URLEncode();
			url += requestParameters.toString();
		}

		mHttpLoader.post(url, postData, httpHeaders, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param response The response interface for the request call back
	 */
	public void put(String urlStr, Object postData, AsyncHttpResponse response)
	{
		put(urlStr, postData, null, null, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param httpHeaders The request headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void put(String urlStr, Object postData, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		put(urlStr, postData, null, httpHeaders, response);
	}

	/**
	 * Initiates a delete request with a server
	 * @param urlStr The URLs to the server
	 * @param postData The data to be sent to the server
	 * @param requestParameters The request parameters for the URL
	 * @param httpHeaders The request headers for the URL
	 * @param response The response interface for the request call back
	 */
	public void put(String urlStr, Object postData, HttpParams requestParameters, HttpParams httpHeaders, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;
		String url = urlStr;

		if (requestParameters != null)
		{
			requestParameters.URLEncode();
			url += requestParameters.toString();
		}

		mHttpLoader.put(url, postData, httpHeaders, response);
	}

	public void setCookie(HttpParams cookie)
	{
		mHeaders.setParam("Cookie", cookie.toString());
	}

	/**
	 * @brief This class is the main AsyncTask loader for the requests
	 */
	private class HttpLoader extends AsyncTask<String, Void, Object>
	{
		public ConnectionInfo mConnectionInfo = new ConnectionInfo();

		private final int DOWNLOAD = 0x00;
		private final int GET = 0x01;
		private final int GET_IMAGE = 0x11;
		private final int POST = 0x02;
		private final int PUT = 0x03;
		private final int DELETE = 0x04;

		private long mLoadTime = 0;
		private AsyncHttpResponse mAsyncHttpResponse;
		private int type = 0;
		private Handler mTimeoutHandler;
		private int mTimeout = 10000;
		private Object mSendData;
		private HttpParams mHttpParams;
		private String mUrl;

		/**
		 * Default Constructor
		 */
		public HttpLoader()
		{
		}

		/**
		 * Default Constructor
		 * @param timeout Sets the timeout for the request
		 */
		public HttpLoader(int timeout)
		{
			this.mTimeout = timeout;
		}

		/**
		 * Initiates a download request on the urlStr
		 * @param urlStr The URL for the request
		 * @param responseHandler The response handler
		 */
		public void download(String urlStr, HttpParams headers, AsyncHttpResponse responseHandler)
		{
			this.mAsyncHttpResponse = responseHandler;
			this.mHttpParams = headers;
			type = DOWNLOAD;
			this.mUrl = urlStr;

			this.execute(urlStr);
		}

		/**
		 * Initiates a GET request on the urlStr
		 * @param urlStr The URL for the request
		 * @param headers The headers to be sent to the server
		 * @param responseHandler The response handler
		 */
		public void get(String urlStr, HttpParams headers, AsyncHttpResponse responseHandler)
		{
			this.mAsyncHttpResponse = responseHandler;
			this.mHttpParams = headers;
			type = GET;
			this.mUrl = urlStr;

			this.execute(urlStr);
		}

		/**
		 * Initiates a DELETE request on the urlStr
		 * @param urlStr The URL for the request
		 * @param headers The headers to be sent to the server
		 * @param responseHandler The response handler
		 */
		public void delete(String urlStr, HttpParams headers, AsyncHttpResponse responseHandler)
		{
			this.mAsyncHttpResponse = responseHandler;
			this.mHttpParams = headers;
			type = DELETE;
			this.mUrl = urlStr;

			this.execute(urlStr);
		}

		/**
		 * Initiates a GET request on the urlStr for an image
		 * @param urlStr The URL for the request
		 * @param headers The headers to be sent to the server
		 * @param responseHandler The response handler
		 */
		public void getImage(String urlStr, AsyncHttpResponse responseHandler)
		{
			this.mAsyncHttpResponse = responseHandler;
			type = GET_IMAGE;
			this.mUrl = urlStr;

			this.execute(urlStr);
		}

		/**
		 * Initiates a POST request on the urlStr
		 * @param urlStr The URL for the request
		 * @param data The data to be sent to the server
		 * @param headers The headers to be sent to the server
		 * @param responseHandler The response handler
		 */
		public void post(String urlStr, Object data, HttpParams headers, AsyncHttpResponse response)
		{
			this.mAsyncHttpResponse = response;
			this.mSendData = data;
			this.mHttpParams = headers;
			type = POST;
			this.mUrl = urlStr;

			this.execute(urlStr);
		}

		/**
		 * Initiates a PUT request on the urlStr
		 * @param urlStr The URL for the request
		 * @param data The data to be sent to the server
		 * @param headers The headers to be sent to the server
		 * @param responseHandler The response handler
		 */
		public void put(String urlStr, Object data, HttpParams header, AsyncHttpResponse response)
		{
			this.mAsyncHttpResponse = response;
			this.mSendData = data;
			this.mHttpParams = header;
			type = PUT;
			this.mUrl = urlStr;

			this.execute(urlStr);
		}

		/**
		 * The timeout runnable for the request
		 */
		private Runnable timeoutRunnable = new Runnable()
		{
			public void run()
			{
				cancel(true);

				if (mAsyncHttpResponse != null)
				{
					mAsyncHttpResponse.onFailure();
					mAsyncHttpResponse.beforeFinish();
					mAsyncHttpResponse.onFinish();
				}
			}
		};


		@Override protected void onPreExecute()
		{
			mConnectionInfo.connectionHeaders = mHttpParams;
			mConnectionInfo.connectionSentData = mSendData;
			mConnectionInfo.connectionResponseHeaders = new HttpParams();
			mConnectionInfo.connectionInitiationTime = System.currentTimeMillis();
			mConnectionInfo.connectionResponseTime = mLoadTime;
			mConnectionInfo.connectionUrl = mUrl;

			if (mAsyncHttpResponse != null)
			{
				mAsyncHttpResponse.setConnectionInfo(mConnectionInfo);
				mAsyncHttpResponse.onSend();
			}

			mTimeoutHandler = new Handler(Looper.getMainLooper());

			if (this.mTimeout > 0)
			{
				mTimeoutHandler.postDelayed(timeoutRunnable, this.mTimeout);
			}
		}

		@Override protected Object doInBackground(String... url)
		{
			mLoadTime = System.currentTimeMillis();

			switch (type)
			{
				case DOWNLOAD:
				{
					try
					{
						// Send data
						URL murl = new URL(url[0]);
						System.setProperty("http.keepAlive", "false");

						HttpURLConnection conn;

						if (murl.getHost().equals("https"))
						{
							conn = (HttpsURLConnection)murl.openConnection();
						}
						else
						{
							conn = (HttpURLConnection)murl.openConnection();
						}

						conn.setDoInput(true);
						conn.setUseCaches(false);

						for (String[] s : globalHeaders.getHeaders())
						{
							conn.setRequestProperty(s[0], s[1]);
						}

						conn.setRequestProperty("Connection", "close");
						conn.setRequestMethod("GET");

						mConnectionInfo.connectionResponseCode = conn.getResponseCode();
						mConnectionInfo.connectionResponseMessage = conn.getResponseMessage();

						// Get the response
						PatchInputStream i = new PatchInputStream(conn.getInputStream());
						if ((mConnectionInfo.connectionResponseCode / 100) != 2)
						{
							i = new PatchInputStream(conn.getErrorStream());
						}
						else
						{
							i = new PatchInputStream(conn.getInputStream());
						}

						String loc;
						if ((loc = conn.getHeaderField("Location")) != null)
						{
							mConnectionInfo.connectionResponseHeaders.addParam("Location", loc);
						}

						InputStream is = new BufferedInputStream(i, 1024);

						ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
						int bufferSize = 1024;
						byte[] buffer = new byte[bufferSize];

						int len = 0;
						int readCount = 0;
						while ((len = is.read(buffer)) > 0)
						{
							if (mAsyncHttpResponse != null)
							{
								mAsyncHttpResponse.onBytesProcessed(readCount, conn.getContentLength());
							}

							byteBuffer.write(buffer, 0, len);
							readCount += len;
						}

						if (mAsyncHttpResponse != null)
						{
							mAsyncHttpResponse.onBytesProcessed(readCount, conn.getContentLength());
						}

						is.close();
						i.close();
						conn.disconnect();

						return byteBuffer.toByteArray();
					}
					catch (EOFException e)
					{
						e.printStackTrace();
						return null;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return null;
					}
				}

				case DELETE:
				case GET:
				{
					String result = null;
					try
					{
						StringBuffer data = new StringBuffer();

						// Send data
						URL murl = new URL(url[0]);
						System.setProperty("http.keepAlive", "false");

						HttpURLConnection conn;

						if (murl.getHost().equals("https"))
						{
							conn = (HttpsURLConnection)murl.openConnection();
						}
						else
						{
							conn = (HttpURLConnection)murl.openConnection();
						}

						for (String[] s : globalHeaders.getHeaders())
						{
							conn.setRequestProperty(s[0], s[1]);
						}

						conn.setInstanceFollowRedirects(followRedirect);
						if (mHttpParams != null && mHttpParams.getParam("FollowRedirect") != null)
						{
							conn.setInstanceFollowRedirects(Boolean.parseBoolean(mHeaders.getParam("FollowRedirect")));
						}

						conn.setDoInput(true);
						conn.setUseCaches(false);
						conn.setRequestProperty("Connection", "close");

						if (type == DELETE)
						{
							conn.setRequestMethod("DELETE");
						}
						else
						{
							conn.setRequestMethod("GET");
						}

						if (mHttpParams != null)
						{
							ArrayList<String[]> mHeaders = mHttpParams.getHeaders();
							int headerSize = mHeaders.size();

							for (int headerIndex = 0; headerIndex < headerSize; headerIndex++)
							{
								conn.setRequestProperty(mHeaders.get(headerIndex)[0], mHeaders.get(headerIndex)[1]);
							}
						}

						mConnectionInfo.connectionResponseCode = conn.getResponseCode();

						PatchInputStream i;
						if ((mConnectionInfo.connectionResponseCode / 100) != 2)
						{
							i = new PatchInputStream(conn.getErrorStream());
						}
						else
						{
							i = new PatchInputStream(conn.getInputStream());
						}

						String loc;
						if ((loc = conn.getHeaderField("Location")) != null)
						{
							mConnectionInfo.connectionResponseHeaders.addParam("Location", loc);
						}

						// Get the response
						InputStream is = new BufferedInputStream(i);

						InputStreamReader reader = new InputStreamReader(is);
						BufferedReader rd = new BufferedReader(reader);
						String line;
						StringBuilder sb = new StringBuilder();

						while ((line = rd.readLine()) != null)
						{
							sb.append(line);
						}

						mConnectionInfo.connectionResponseMessage = sb.toString();

						reader.close();
						is.close();
						i.close();
						rd.close();
						conn.disconnect();
						result = sb.toString();

						return result;
					}
					catch (EOFException e)
					{
						e.printStackTrace();
						return null;
					}
					catch (UnknownHostException e)
					{
						e.printStackTrace();
						mConnectionInfo.connectionResponseCode = 0;
						mConnectionInfo.connectionResponseMessage = "";

						return null;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return null;
					}
				}

				case PUT:
				case POST:
				{
					try
					{
						// Send data
						URL murl = new URL(url[0]);

						System.setProperty("http.keepAlive", "false");

						HttpURLConnection conn;

						if (murl.getHost().equals("https"))
						{
							conn = (HttpsURLConnection)murl.openConnection();
						}
						else
						{
							conn = (HttpURLConnection)murl.openConnection();
						}

						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setUseCaches(false);

						for (String[] s : globalHeaders.getHeaders())
						{
							conn.setRequestProperty(s[0], s[1]);
						}

						if (type == PUT)
						{
							conn.setRequestMethod("PUT");
						}
						else
						{
							conn.setRequestMethod("POST");
						}

						conn.setInstanceFollowRedirects(followRedirect);
						if (mHttpParams != null && mHttpParams.getParam("FollowRedirect") != null)
						{
							conn.setInstanceFollowRedirects(Boolean.parseBoolean(mHttpParams.getParam("FollowRedirect")));
						}

						if (mHttpParams != null)
						{
							ArrayList<String[]> mHeaders = mHttpParams.getHeaders();
							int headerSize = mHeaders.size();

							for (int headerIndex = 0; headerIndex < headerSize; headerIndex++)
							{
								conn.setRequestProperty(mHeaders.get(headerIndex)[0], mHeaders.get(headerIndex)[1]);
							}
						}

						//	Send as binary if its a byte array
						if (mSendData.getClass().equals(byte[].class))
						{
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ObjectOutput out = new ObjectOutputStream(bos);
							out.writeObject(mSendData);
							byte[] yourBytes = bos.toByteArray();

							OutputStream wr = conn.getOutputStream();
							wr.write(yourBytes);

							int index = 0;
							int size = 1024;

							while (index < yourBytes.length)
							{
								if ((index + size) > yourBytes.length)
								{
				                    size = yourBytes.length - index;
				                }

								if (mAsyncHttpResponse != null)
								{
									mAsyncHttpResponse.onBytesProcessed(index, yourBytes.length);
								}

								wr.write(yourBytes, index, size);
								index += size;
							}

							wr.flush();
							wr.close();

							if (mAsyncHttpResponse != null)
							{
								mAsyncHttpResponse.onBytesProcessed(index, yourBytes.length);
							}
						}
						else
						{
						    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				            wr.write(mSendData.toString());

				            if (mAsyncHttpResponse != null)
							{
				            	mAsyncHttpResponse.onBytesProcessed(mSendData.toString().length(), mSendData.toString().length());
							}

				            wr.flush();
							wr.close();
						}

						mConnectionInfo.connectionResponseCode = conn.getResponseCode();
						mConnectionInfo.connectionSentData = mSendData.toString();

						String loc;
						if ((loc = conn.getHeaderField("Location")) != null)
						{
							mConnectionInfo.connectionResponseHeaders.addParam("Location", loc);
						}

						PatchInputStream i;
						if ((mConnectionInfo.connectionResponseCode / 100) != 2)
						{
							i = new PatchInputStream(conn.getErrorStream());
						}
						else
						{
							i = new PatchInputStream(conn.getInputStream());
						}

						// Get the response
						InputStream is = new BufferedInputStream(i);

						InputStreamReader reader = new InputStreamReader(is);
						BufferedReader rd = new BufferedReader(reader);
						String line;
						StringBuilder sb = new StringBuilder();

						while ((line = rd.readLine()) != null)
						{
							sb.append(line);
						}

						mConnectionInfo.connectionResponseMessage = sb.toString();

						rd.close();
						is.close();
						i.close();
						reader.close();
						conn.disconnect();

						return sb.toString();
					}
					catch (EOFException e)
					{
						e.printStackTrace();

						return "";
					}
					catch (Exception e)
					{
						e.printStackTrace();

						return null;
					}
				}

				default:
				{
					return "";
				}
			}
		}

		@Override protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			mTimeoutHandler.removeCallbacks(timeoutRunnable);

			mConnectionInfo.connectionResponseTime = System.currentTimeMillis();

			if (mAsyncHttpResponse != null)
			{
				mAsyncHttpResponse.beforeFinish();

				if (((mConnectionInfo.connectionResponseCode / 100) == 2))
				{
					if (type == DOWNLOAD)
					{
						mAsyncHttpResponse.onSuccess(result == null ? new byte[]{} : (byte[])result);
					}
					else
					{
						mAsyncHttpResponse.onSuccess(result == null ? "" : result);
					}
				}
				else
				{
					mAsyncHttpResponse.onFailure();

					if (result != null)
					{
						mAsyncHttpResponse.onFailure(result);
					}

					mAsyncHttpResponse.onFailure(mConnectionInfo.connectionResponseCode, mConnectionInfo.connectionResponseMessage);
				}

				mAsyncHttpResponse.onFinish();
			}
		}
	}

	private class PatchInputStream extends FilterInputStream
	{
		public PatchInputStream(InputStream in)
		{
			super(in);
		}

		public long skip(long amount) throws IOException
		{
			long skipCount = 0L;
			while (skipCount < amount)
			{
				long totalSkipped = in.skip(amount - skipCount);
				if (totalSkipped == 0L)
				{
					break;
				}

				skipCount += totalSkipped;
			}

			return skipCount;
		}
	}
}