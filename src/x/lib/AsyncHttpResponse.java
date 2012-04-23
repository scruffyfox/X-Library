package x.lib;

import x.type.ConnectionInfo;
import android.os.Bundle;

/**
 * @brief The response class for the requests
 */
public abstract class AsyncHttpResponse
{
	private Bundle mExtras = null;
	private ConnectionInfo mConnectionInfo = new ConnectionInfo();
	
	/**
	 * Default constructor
	 */
	public AsyncHttpResponse()
	{
		mExtras = new Bundle();
	}
	
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
	
	/**
	 * Called when the client has processed some bytes in a request
	 * @param amountProcessed The amount of bytes that have been processed (0 to totalSize)
	 * @param totalSize The total size of the data to be downloaded
	 */
	public void onBytesProcessed(int amountProcessed, int totalSize){};
	
	/**
	 * The function that gets called when the request is sent
	 */
	public void onSend(){};
	
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
	public void onSuccess(Object[] response){};
	
	/**
	 * The function that gets called when the response from the server fails
	 */
	public void onFailure(){};
	
	/**
	 * The function that gets called when the response from the server fails
	 * @param response The response message
	 */
	public void onFailure(Object response){};
	
	/**
	 * The function that gets called when the response from the server fails
	 * @param responseCode The response code
	 * @param responseMessage The response message
	 */
	public void onFailure(int responseCode, String responseMessage){};
	
	/**
	 * The function that gets called before the async task ends. Called before onSuccess/onFailure.
	 */
	public void beforeFinish(){};
	
	/**
	 * The function that gets called after everything has been completed
	 */
	public void onFinish(){};
}