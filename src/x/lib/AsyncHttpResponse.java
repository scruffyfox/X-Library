package x.lib;

import android.os.Bundle;

/**
 * @brief The response class for the requests
 */
public abstract class AsyncHttpResponse
{
	private Bundle mExtras = null;
	
	public AsyncHttpResponse(){}
	
	public AsyncHttpResponse(Bundle extras)
	{
		mExtras = extras;
	}
	
	protected Bundle getExtras()
	{
		return mExtras;
	}
	
	//	Async callbacks
	/**
	 * The function that gets called when the request is sent
	 */
	public void onSend(){}
	
	/**
	 * The function that gets called when the server response with >= 200 and < 300
	 * @param response The response message
	 */
	public abstract void onSuccess(Object response);
	
	/**
	 * The function that gets called when the server response with an array (@see getImages())
	 * @param response The response message
	 */
	public void onSuccess(Object[] response){}
	
	/**
	 * The function that gets called when the response from the server fails
	 */
	public void onFailure(){}
	
	/**
	 * The function that gets called when the response from the server fails
	 * @param response The response message
	 */
	public void onFailure(Object response){}
	
	/**
	 * The function that gets called when the response from the server fails
	 * @param responseCode The response code
	 * @param responseMessage The response message
	 */
	public void onFailure(int responseCode, String responseMessage){}
	
	/**
	 * The function that gets called before the async task ends
	 */
	public void beforeFinish(){}
	
	/**
	 * The function that gets called after everything has been completed
	 */
	public void onFinish(){}
}