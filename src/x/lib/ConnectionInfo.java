package x.lib;

/**
 * Gives details on the connection made to a server
 */
public class ConnectionInfo 
{
	public String connectionUrl = "";
	public HttpParams connectionHeaders = null;
	public long connectionInitiationTime = 0;
	public long connectionResponseTime = 0;
	public int connectionResponseCode = 0;
	public String connectionResponseMessage = "";
	
	/* (non-Javadoc) 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ConnectionInfo \n[\n\tconnectionHeaders=" + connectionHeaders + ", \n\tconnectionInitiationTime=" + connectionInitiationTime + ", \n\tconnectionResponseCode=" + connectionResponseCode + ", \n\tconnectionResponseMessage=" + connectionResponseMessage + ", \n\tconnectionResponseTime=" + connectionResponseTime + ", \n\tconnectionUrl=" + connectionUrl + "\n]";
	}
}