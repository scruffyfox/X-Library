/**
 * @brief x type is the type library which includes the commonly used data types in the X Library lib
 *
 * @author Callum Taylor
**/
package x.type;

/**
 * @brief Gives details on the connection made to a server
 */
public class ConnectionInfo
{
	/**
	 * The URL of the connection
	 */
	public String connectionUrl = "";
	/**
	 * The headers that were sent (if any)
	 */
	public HttpParams connectionHeaders = new HttpParams();
	/**
	 * The data that was sent (if any)
	 */
	public Object connectionSentData = new Object();
	/**
	 * The initiated time of the contection
	 */
	public long connectionInitiationTime = 0;
	/**
	 * The time the response was recieved
	 */
	public long connectionResponseTime = 0;
	/**
	 * The response code of the connection
	 */
	public int connectionResponseCode = 0;
	/**
	 * The response message
	 */
	public String connectionResponseMessage = "";
	/**
	 * The response headers
	 */
	public HttpParams connectionResponseHeaders = new HttpParams();
	/**
	 * The connection method
	 */
	public String connectionMethod = "";

	@Override public String toString()
	{
		return "ConnectionInfo " + hashCode() + "\n[\n    connectionUrl=" + connectionUrl + ", \n    connectionHeaders=" + connectionHeaders + ", \n    connectionSentData=" + connectionSentData + ", \n    connectionInitiationTime=" + connectionInitiationTime + ", \n    connectionResponseTime=" + connectionResponseTime + ", \n    connectionResponseCode=" + connectionResponseCode + ", \n    connectionResponseMessage=" + connectionResponseMessage + ", \n    connectionResponseHeaders=" + connectionResponseHeaders + ", \n    connectionMethod=" + connectionMethod + "\n]";
	}
}