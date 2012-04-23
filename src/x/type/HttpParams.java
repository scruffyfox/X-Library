/**
 * @brief x type is the type library which includes the commonly used data types in the X Library lib
 * 
 * @author Callum Taylor
**/
package x.type;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;

import x.lib.Debug;


/**
 * @brief Http Params to be used with AsyncHttpClient (Key, Value pair class)
 */	
public class HttpParams implements Serializable
{
	private ItemList<String[]> queryString;
		
	/**
	 * Default Constructor
	 */	 
	public HttpParams()
	{
		queryString = new ItemList<String[]>();
	}
	
	/**
	 * Default constructor
	 * @param params The url formatted string to parse
	 */
	public HttpParams(String params)
	{
		queryString = new ItemList<String[]>();
		parseString(params);
	}
	
	/**
	 * Default constructor
	 * @param params The parameters to be added (In the format [{key, value}, {key, value}]
	 */
	public HttpParams(String[]... params)
	{		
		queryString = new ItemList<String[]>();
		for (String[] param : params)
		{
			if (param == null) continue;
						
			addParam(param[0], param[1]);
		}
	}

	/**
	 * Creates a HttpParams instance from a url
	 * @param url The url with the request params
	 * @return Null if no request params were set
	 */
	public static HttpParams parseUrl(String url)
	{
		if (url.indexOf('?') > -1)
		{
			HttpParams retParams = new HttpParams();
			
			String[] parts = url.split("[?]");
			String[] variables = parts[1].split("[&]");			
			
			for (int varIndex = 0; varIndex < variables.length; varIndex++)
			{
				String[] params = variables[varIndex].split("[=]");								
				
				for (int index = 0; index < params.length; index += 2)
				{
					retParams.addParam(params[index], params.length > index ? params[index + 1] : null);
				}
			}
						
			return retParams;
		}
		
		return null;
	}
	
	/**
	 * Encodes the values to the URL standard 
	 */
	public void URLEncode()
	{
		int size = queryString.size();
		for (int index = 0; index < size; index++)
		{
			String[] parts = queryString.get(index);
			parts[1] = URLEncoder.encode(parts[1] == null ? "" : parts[1]);
		}
	}
	
	/**
	 * Parses a string into the param type. Format has to be ?key=value&key2=value2. Does not require ? at the start.
	 * @param url
	 */
	public void parseString(String url)
	{
		if (url.length() > 0)
		{
			int questionMarkIndex = Math.max(0, url.indexOf("?") + 1);
			String shortUrl = url.substring(questionMarkIndex, url.length());
			String[] params = shortUrl.split("&");

			for (String param : params) 
			{
				String[] value = param.split("=");
				
				if (value.length < 2)
				{
					addParam(value[0], "");
				}
				else
				{
					addParam(value[0], value[1]);
				}
			}
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
	 * Gets a value from a param key
	 * @param key The key to get the value from
	 * @return Null if not found
	 */
	public String getParam(String key)
	{
		int dataCount = queryString.size();
		for (int dataIndex = 0; dataIndex < dataCount; dataIndex++)
		{
			if (queryString.get(dataIndex)[0].equals(key))
			{				
				return queryString.get(dataIndex)[1];
			}
		}	
		
		return null;
	}
	
	/**
	 * Adds a param to the class
	 * The string must contain 2 values in the order 0: key, 1: value
	 * @param keyval The array containing the key and value
	 */
	public void addParam(String[] keyval)
	{
		addParam(keyval[0], keyval[1]);
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
	 * Add a http params class to the class
	 * @param params The params to add
	 */
	public void addParams(HttpParams params)
	{
		queryString.addAll(params.queryString);
	}		
	
	/**
	 * Get the headers as an array list
	 * @return The headers as the array list
	 */
	public ItemList<String[]> getHeaders()
	{
		return queryString;
	}
	
	/**
	 * To String
	 * @return returns the http headers in the format ?<b>key</b>=value&<b>key</b>=value
	 */
	@Override public String toString()
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