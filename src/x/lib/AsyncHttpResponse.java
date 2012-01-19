package x.lib;

import x.type.ConnectionInfo;
import android.os.Bundle;

/**
 * @brief The response class for the requests
 */
public abstract class AsyncHttpResponse
{
	private Bundle mExtras = null;
	private ConnectionInfo mConnectionInfo = null;
	
	/**
	 * Default constructor
	 */
	public AsyncHttpResponse(){}
	
	/**
	 * Default constructor
	 * @param extras The extra bundle that gets passed to the response
	 */
	public AsyncHttpResponse(Bundle extras)
	{
		mExtras = extras;
	}
	
	/**
	 * Sets the connection info to a new connection info
	 * @param connectionInfo
	 */
	public void setConnectionInfo(ConnectionInfo connectionInfo)
	{
		mConnectionInfo = connectionInfo;
	}
	
	/**
	 * Gets the connection info
	 * @return The connection info
	 */
	protected ConnectionInfo getConnectionInfo()
	{
		return this.mConnectionInfo;
	}
	
	/**
	 * Gets the extras from the class
	 * @return The extras
	 */
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
	public void onSuccess(Object response){};
	
	/**
	 * The function that gets called when the server response with >= 200 and < 300
	 * @param response The response message
	 */
	public void onSuccess(byte[] response){};
	
	/**
	 * The function that gets called when the server response with an array @see AsyncHttpClient.getImages()
	 * @param response The response message
	 */
	public void onSuccess(Object[] response){}
	
	/*
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