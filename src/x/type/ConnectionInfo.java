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
	public String connectionUrl = "";
	public HttpParams connectionHeaders = null;
	public String connectionSentData = null;
	public long connectionInitiationTime = 0;
	public long connectionResponseTime = 0;
	public int connectionResponseCode = 0;
	public String connectionResponseMessage = "";
	
	@Override public String toString()
	{
		return "ConnectionInfo \n[\n\tconnectionUrl=" + connectionUrl + ", \n\tconnectionHeaders=" + connectionHeaders + ", \n\tconnectionSentData=" + connectionSentData + ", \n\tconnectionInitiationTime=" + connectionInitiationTime + ", \n\tconnectionResponseTime=" + connectionResponseTime + ", \n\tconnectionResponseCode=" + connectionResponseCode + ", \n\tconnectionResponseMessage=" + connectionResponseMessage + "\n]";
	}		
}