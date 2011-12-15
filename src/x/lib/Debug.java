/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.text.DecimalFormat;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import x.type.HttpParams;
import android.location.Location;
import android.util.Log;

/**
	@brief This static class is for debugging 
	@todo Add debug outputs for more data types	
*/
public class Debug
{
	private final static String LOG_TAG = "TSC";
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(String message)
	{
		try
		{
			Log.d(LOG_TAG, message);
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(String[] message)
	{
		try
		{
			for (String m : message)
			{
				out(m);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(String[][] message)
	{
		try
		{
			for (String[] m : message)
			{
				out(m);
				
				for (String mes : m)
				{
					out("\t" + mes);
				}		
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(Boolean message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message);
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(Object message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message);
		}
		catch (Exception e)
		{
		}
	}

	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(Object[] message)
	{
		try
		{
			for (Object m : message)
			{
				out(m.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(List<?> message)
	{
		out(message.toArray());
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(HttpParams message)
	{
		try
		{			
			List params = message.getHeaders();
			int count = params.size();
			
			out("HttpParams$" + params.hashCode());
			for (int index = 0; index < count; index++)
			{
				String[] p = (String[])params.get(index);
				out("\t" + p[0] + ":" + p[1]);
			}
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(Location message)
	{
		try
		{
			out(message.toString());
			out("\t Latitude: " + message.getLatitude());
			out("\t Longitude: " + message.getLongitude());
			out("\t Accuracy: " + message.getAccuracy());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(JSONObject message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message.toString(1));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(JSONArray message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message.toString(1));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(int message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(long message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(double message)
	{
		try
		{
			Log.d(LOG_TAG, "" + message);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(double[] message)
	{
		try
		{
			for (double m : message)
			{
				out(m);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	/**
	 * Logs the heap size of the application and any allocation sizes
	 * @param mClass The class of the application to check
	 */
	public static void logHeap(Class mClass) 
	{
		logHeap("", mClass);
	}
	
	/**
	 * Logs the heap size of the application and any allocation sizes
	 * @param msg A message to display
	 * @param mClass The class of the application to check
	 */
	public static void logHeap(String msg, Class mClass) 
	{
	    Double allocated = new Double(android.os.Debug.getNativeHeapAllocatedSize()) / new Double((1048576));
	    Double available = new Double(android.os.Debug.getNativeHeapSize() / 1048576.0);
	    Double free = new Double(android.os.Debug.getNativeHeapFreeSize() / 1048576.0);
	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(2);
	    df.setMinimumFractionDigits(2);

	    Log.d("MEM", "" + System.currentTimeMillis() + " - DUMP: " + msg);
	    Log.d("MEM", "Memory Heap Debug. ==============================================================================================================");
	    Log.d("MEM", "Memory Heap Native: Allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [" + mClass.getName() + "]");
	    Log.d("MEM", "Memory Heap App: Allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory() / 1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory() / 1048576)) + "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory() / 1048576)) + "MB free)");
	    Log.d("MEM", "Memory Heap Debug. ==============================================================================================================");
	}
}
