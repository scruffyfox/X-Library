/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import x.lib.AsyncHttpClient.RequestMode;
import x.type.ItemList;

import android.os.Bundle;
import android.os.Handler;

/**
 * @brief The helper class for queueing AsyncHttpClient requests
 * 
 * Example:
 * @code
 * AsyncHttpClient c1 = AsyncHttpClient.createClient(RequestMode.GET, "http://google.com");
 *    
 * AsyncHttpQueuer.createQueue(new AsyncHttpResponse()
 * {  
 * 	@Override public void onSuccess(Object response)
 * 	{
 * 		//	Response code
 * 	}
 * }, c1).start();		
 * @endcode
 * 
 * Example:
 * @code
 * AsyncHttpQueuer queuer = new AsyncHttpQueuer();
 * queuer.addToQueue(c1);
 * queuer.setResponse(new AsyncHttpResponse()
 * {  
 * 	@Override public void onSuccess(Object response)
 * 	{
 * 		Debug.out(response.toString()); 
 * 	}
 * 	
 * 	@Override public void onFinish()
 * 	{   
 * 		Debug.out("conn pos: " + getExtras().get(AsyncHttpQueuer.BUNDLE_POSITION));
 * 	}
 * });
 * queuer.start();
 * @endcode
 */
public class AsyncHttpQueuer
{
	/**
	 * The key for the position of the request passed in the bundle of the AsyncHttpResponse
	 */
	public static final String BUNDLE_POSITION = "position";
	
	private Queue<AsyncHttpClient> requestQueue = new LinkedList<AsyncHttpClient>();
	private AsyncHttpResponse mResponse;
	private int mMaxProcess = 1;
	private int currentQueuePos = 0;	
	private int totalPos = 0;
	
	private Handler processer = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{		
			if (msg.what == 1)
			{
				return;
			}
			
			while (currentQueuePos < mMaxProcess)
			{
				executeRequest(requestQueue.poll());
			}
		};
	};
	
	/**
	 * Starts the queue
	 */
	public void start()
	{
		processer.sendEmptyMessage(0);
	} 
	
	/**
	 * Executes the request given from the handler
	 * @param client The client to execute
	 */
	private void executeRequest(AsyncHttpClient client)
	{
		if (client == null) return;			
		 
		currentQueuePos++;	
		totalPos++;
						
		Bundle b = new Bundle();
		b.putInt(BUNDLE_POSITION, totalPos - 1);
		client.execute(new AsyncHttpResponse(b)
		{
			@Override public void beforeFinish()
			{
				if (mResponse != null)
				{
					mResponse.beforeFinish();
				}
			}
			
			@Override public void onFailure(Object response)
			{
				if (mResponse != null)
				{
					mResponse.onFailure(response);
				}
			}
			
			@Override public void onFailure(int responseCode, String responseMessage)
			{
				super.onFailure(responseCode, responseMessage);
				
				if (mResponse != null)
				{
					mResponse.onFailure(responseCode, responseMessage);
				}
			}
			
			@Override public void onFinish()
			{
				currentQueuePos--;
								
				if (requestQueue.size() < 1)
				{
					processer.sendEmptyMessage(1);	
				}
				else
				{
					processer.sendEmptyMessage(0);
				}

				if (mResponse != null)
				{
					mResponse.onFinish();
				}
				
				super.onFinish();
			}
			 
			@Override public void onSend()
			{
				if (mResponse != null)
				{
					mResponse.getExtras().putInt(BUNDLE_POSITION, getExtras().getInt(BUNDLE_POSITION));
					mResponse.onSend();
				}					
			}
			
			@Override public void onSuccess(Object response)
			{				
				if (mResponse != null)
				{										
					mResponse.onSuccess(response);
				}
			}
		});		
	}
	
	/**
	 * Creates a new queue instance
	 * @param client The client array 
	 * @return The newly initialized AsyncHttpQueuer
	 */
	public static AsyncHttpQueuer createQueue(AsyncHttpClient... client)
	{
		AsyncHttpQueuer queuer = new AsyncHttpQueuer();
		queuer.addToQueue(new ItemList<AsyncHttpClient>(client));
		
		return queuer;
	}
	
	/**
	 * Creates a new queue instance
	 * @param response The response for the queue
	 * @param client The client array 
	 * @return The newly initialized AsyncHttpQueuer
	 */
	public static AsyncHttpQueuer createQueue(AsyncHttpResponse response, AsyncHttpClient... client)
	{
		AsyncHttpQueuer queuer = new AsyncHttpQueuer();
		queuer.addToQueue(new ItemList<AsyncHttpClient>(client));
		queuer.setResponse(response);
		
		return queuer;
	}
	
	/**
	 * Creates a new queue instance
	 * @param response The response for the queue
	 * @param max The maximum amount of connections to have open at once
	 * @param client The client array 
	 * @return The newly initialized AsyncHttpQueuer
	 */
	public static AsyncHttpQueuer createQueue(AsyncHttpResponse response, int max, AsyncHttpClient... client)
	{
		AsyncHttpQueuer queuer = new AsyncHttpQueuer();
		queuer.setMaxProcess(max);
		queuer.addToQueue(new ItemList<AsyncHttpClient>(client));
		queuer.setResponse(response);
		
		return queuer;
	}
	
	/**
	 * Adds a client to the queue
	 * @param clients The client to add
	 */
	public void addToQueue(AsyncHttpClient clients)
	{
		requestQueue.add(clients);
	}
	
	/**
	 * Adds a collection of clients to the queue
	 * @param clients The clients to add
	 */
	public void addToQueue(Collection<AsyncHttpClient> clients)
	{			
		requestQueue.addAll(clients);
	}
	
	/**
	 * Sets the response of each request
	 * @param response The response for the queue
	 */
	public void setResponse(AsyncHttpResponse response)
	{
		mResponse = response;
	}
	
	/**
	 * Sets the maximum ammount of connections to have open
	 * @param max
	 */
	public void setMaxProcess(int max)
	{
		mMaxProcess = max;
	}
}
