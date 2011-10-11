/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;
 
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @brief The client class used for initiating HTTP requests
 */
public class AsyncHttpClient
{	
	public enum RequestMode
	{
		POST,
		GET,
		PUT,
		DELETE
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
	 * Cancels the request
	 */
	public void cancel() 
	{
		mHttpLoader.cancel(true); 
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
		String url = urlStr;
		
		if (requestParameters != null)
		{
			url += requestParameters.toString();
		}
		
		switch (requestMode)
		{
			case POST:
			{
				post(url, postData, requestParameters, httpHeaders, response);	
				break;
			}
			
			case GET:
			{
				get(urlStr, requestParameters, httpHeaders, response);
				break;
			}
			
			case PUT:
			{
				put(url, postData, requestParameters, httpHeaders, response);	
				break;
			}
			
			case DELETE:
			{
				delete(urlStr, requestParameters, httpHeaders, response);
				break;
			}
		}	
	}
	
	/**
	 * Initiates a get request with a server
	 * @param endpoint The URL to the server
	 * @param requestParameters The request parameters in the format ?<b>param</b>=value&<b>param</b>=value
	 * @param response The response interface for the request call back
	 */
	public void get(String endpoint, String requestParameters, AsyncHttpResponse response)
	{		
		String urlStr = endpoint;
		if (requestParameters != null && requestParameters.length() > 0)
		{
			urlStr += urlStr.charAt(urlStr.length() - 1) == '?' ? "" : "?";
			urlStr += requestParameters;
		}
		
		get(urlStr, null, null, response);	
	}
	
	/**
	 * Initiates a get request with a server
	 * @param endpoint The URL to the server
	 * @param response The response interface for the request call back
	 */
	public void get(String endpoint, AsyncHttpResponse response)
	{
		get(endpoint, null, null, response);	
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
			urlStr += requestParameters.toString();
		}
		
		mHttpLoader.get(urlStr, headers, response);		
	}
	
	/**
	 * Initiates a get request with a server for an image
	 * @param urlStr The URL to the server
	 * @param response The response interface for the request call back
	 */
	public void getImage(String urlStr, AsyncHttpResponse response)
	{
		mHttpLoader.getImage(urlStr, response);		
	}		
	
	/**
	 * Initiates a get request with a server
	 * @param urlStr The URLs to the server
	 * @param response The response interface for the request call back
	 */
	public void getImages(String[] urlStr, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;
		final int count = urlStr.length;
		final Object[] images = new Object[count];
		final ArrayList<Integer> counter = new ArrayList<Integer>();		
								
		response.onSend();
		
		for (int index = 0; index < count; index++)
		{
			HttpLoader loader = new HttpLoader(); 
			Bundle b = new Bundle(); 
			b.putInt("index", index);
			
			AsyncHttpResponse tempResponse = new AsyncHttpResponse(b)
			{
				@Override
				public void onSuccess(Object response)
				{
					Bundle b = getExtras();
					images[b.getInt("index")] = response;
					counter.add(1);					
				}
				
				@Override
				public void onFailure()
				{
					Bundle b = getExtras();
					images[b.getInt("index")] = null;
					counter.add(1);
					
					super.onFailure();
				}
				
				@Override
				public void onFinish()
				{
					if (count == counter.size())
					{
						mAsyncHttpResponse.beforeFinish();
						mAsyncHttpResponse.onSuccess(images);
					}
					
					super.onFinish();
				}
			};
			
			loader.getImage(urlStr[index], tempResponse);
		}
	}
	
	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param requestParameters The request parameters for the URL
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, String requestParameters, AsyncHttpResponse response)
	{
		String urlStr = endpoint;
		if (requestParameters != null && requestParameters.length() > 0)
		{
			urlStr += urlStr.charAt(urlStr.length() - 1) == '?' ? "" : "?";
			urlStr += requestParameters;
		}
		
		delete(urlStr, null, null, response);	
	}
	
	/**
	 * Initiates a delete request with a server
	 * @param endpoint The URLs to the server
	 * @param response The response interface for the request call back
	 */
	public void delete(String endpoint, AsyncHttpResponse response)
	{
		delete(endpoint, null, null, response);	
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
	 * @param headers The request headers for the URL 
	 * @param response The response interface for the request call back
	 */	
	public void delete(String endpoint, HttpParams requestParameters, HttpParams headers, AsyncHttpResponse response)
	{
		mAsyncHttpResponse = response;
		String urlStr = endpoint;
		
		if (requestParameters != null)
		{
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
			url += requestParameters.toString();
		}
			
		mHttpLoader.put(url, postData, httpHeaders, response);
	}
	
	/**
	 * This class is the main AsyncTask loader for the requests
	 */
	private class HttpLoader extends AsyncTask<String, Void, Object>
	{
		private final int GET = 0x01;
		private final int GET_IMAGE = 0x11;
		private final int POST = 0x02;
		private final int PUT = 0x03;
		private final int DELETE = 0x04;
		
		private long mLoadTime = 0;
		private String mResponse;
		private AsyncHttpResponse mAsyncHttpResponse;
		private int type = 0;
		private Handler mTimeoutHandler;
		private int mTimeout = 10000;
		private Object mSendData;
		private HttpParams mHttpParams;
		private int responseCode = 0;
		private String responseMessage;
		public ConnectionInfo mConnectionInfo = new ConnectionInfo();
		
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
				
		
		@Override
		protected void onPreExecute()
		{
			if (mAsyncHttpResponse != null)
			{
				mAsyncHttpResponse.onSend();
			}
					
			mTimeoutHandler = new Handler(Looper.getMainLooper());	
			
			if (this.mTimeout > 0)
			{
				mTimeoutHandler.postDelayed(timeoutRunnable, this.mTimeout);
			}
		}
		
		@Override
		protected Object doInBackground(String... url)
		{				
			mLoadTime = System.currentTimeMillis();
			
			mConnectionInfo.connectionHeaders = mHttpParams;
			mConnectionInfo.connectionResponseTime = mLoadTime;
			mConnectionInfo.connectionUrl = url[0];
			if (mAsyncHttpResponse != null)
			{	
				mAsyncHttpResponse.setConnectionInfo(mConnectionInfo);
			}
			
			switch (type)
			{
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
						
						HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
						conn.setDoInput(true);
						conn.setDoOutput(true);
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
												
					    responseCode = conn.getResponseCode();
					    responseMessage = conn.getResponseMessage();
					    
					    mConnectionInfo.connectionResponseCode = responseCode;
					    mConnectionInfo.connectionResponseMessage = responseMessage;
					  
					    // Get the response
					    PatchInputStream i = new PatchInputStream(conn.getInputStream());
					    InputStream is = new BufferedInputStream(i);
					    
					    InputStreamReader reader = new InputStreamReader(is);
					    BufferedReader rd = new BufferedReader(reader);				    
					    String line;
					    StringBuilder sb = new StringBuilder();
						
						while ((line = rd.readLine()) != null)
						{
							sb.append(line);
						}
						
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
						return "";
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
						
						HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setUseCaches(false);
						conn.setRequestProperty("Connection", "close");
					    
					    if (type == PUT)
					    {
					    	conn.setRequestMethod("PUT");
					    }
					    else
					    {
					    	conn.setRequestMethod("POST");
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
					    
					    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					    wr.write(mSendData.toString());
					    wr.flush();
					    wr.close();
					    					   
					   	responseCode = conn.getResponseCode();
					    responseMessage = conn.getResponseMessage();		
					    
					    mConnectionInfo.connectionResponseCode = responseCode;
					    mConnectionInfo.connectionResponseMessage = responseMessage;
					    				    
					    // Get the response
					    PatchInputStream i = new PatchInputStream(conn.getInputStream());
					    InputStream is = new BufferedInputStream(i);
					    
					    InputStreamReader reader = new InputStreamReader(is);
					    BufferedReader rd = new BufferedReader(reader);		
					    String line;
					    StringBuilder sb = new StringBuilder();
					    
					    while ((line = rd.readLine()) != null) 
					    {
					    	sb.append(line);
					    }
					    				    
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
				
				case GET_IMAGE:
				{
	                try 
	                {                           	
	                	HttpURLConnection conn = (HttpURLConnection)new URL(url[0]).openConnection();
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setUseCaches(false);
						conn.setRequestProperty("Connection", "close");
						                    
						responseCode = conn.getResponseCode();
					    responseMessage = conn.getResponseMessage();			
					    
					    mConnectionInfo.connectionResponseCode = responseCode;
					    mConnectionInfo.connectionResponseMessage = responseMessage;
						
	                    BitmapFactory.Options opts = new BitmapFactory.Options();                
	        			
	        			PatchInputStream stream = new PatchInputStream(conn.getInputStream());
	        			
	                    Bitmap bm = BitmapFactory.decodeStream(stream, null, opts);
	                    
	                    conn.disconnect();                        
	                    stream.close();
	                    
	                    return bm;
	                } 
	                catch (Exception e) 
	                {
	                	return null;
	                }		               		        				
				}
				
				default:
				{
					return "";
				}
			}
		}	
		
		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);	
			mTimeoutHandler.removeCallbacks(timeoutRunnable);
			
			if (mAsyncHttpResponse != null)
			{				
				mAsyncHttpResponse.beforeFinish();
			
				if (result != null || (responseCode >= 200 && responseCode < 300))
				{								
					mAsyncHttpResponse.onSuccess(result);						
				}
				else 
				{
					mAsyncHttpResponse.onFailure();
					
					if (result != null)
					{
						mAsyncHttpResponse.onFailure(result);				
					}
								
					mAsyncHttpResponse.onFailure(responseCode, responseMessage);
				}	
				
				mAsyncHttpResponse.onFinish();
			}
		}				
	}
}

class PatchInputStream extends FilterInputStream
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