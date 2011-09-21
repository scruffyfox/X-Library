package x.lib;

import java.util.ArrayList;

/**
 * @brief Http Params to be used with AsyncHttpClient (Key, Value pair class)
 */	
public class HttpParams
{
	private ArrayList<String[]> queryString;
		
	/**
	 * Default Constructor
	 */	 
	public HttpParams()
	{
		queryString = new ArrayList<String[]>();
	}
	
	/**
	 * Default constructor
	 * @param params The parameters to be added (In the format [{key, value}, {key, value}]
	 */
	public HttpParams(String[]... params)
	{
		queryString = new ArrayList<String[]>();
		for (String[] param : params)
		{
			addParam(param[0], param[1]);
		}
	}	
	
	/**
	 * Setting a parameter with the key to the value (Will add if it doesnt exist already)
	 * @param key The key
	 * @param value The value
	 */
	public void setParam(String key, String value)
	{
		int dataCount = queryString.size();
		for (int dataIndex = 0; dataIndex < dataCount; dataIndex++)
		{
			if (queryString.get(dataIndex)[0].equals(key))
			{
				queryString.get(dataIndex)[1] = value;
				return;
			}
		}
		
		addParam(key, value);
	}
	
	/**
	 * Add a param to the class
	 * @param key The key
	 * @param value The value
	 */
	public void addParam(String key, String value)
	{
		queryString.add(new String[]{key, value});
	}
	
	/**
	 * Get the headers as an array list
	 * @return The headers as the array list
	 */
	public ArrayList<String[]> getHeaders()
	{
		return queryString;
	}
	
	/**
	 * To String
	 * @return returns the http headers in the format ?<b>key</b>=value&<b>key</b>=value
	 */
	@Override
	public String toString()
	{
		String qString = "?";
		
		int dataCount = queryString.size();
		for (int dataIndex = 0; dataIndex < dataCount; dataIndex++)
		{
			qString += queryString.get(dataIndex)[0] + "=" + queryString.get(dataIndex)[1] + "&";
		}
		
		qString = (qString.replace(" ", "%20"));
		qString = (qString.toCharArray()[qString.length() - 1] == '&' ? qString.substring(0, qString.length() - 1) : qString);
		
		return qString;
	}
}