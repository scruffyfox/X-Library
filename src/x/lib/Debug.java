/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import x.type.HttpParams;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * @brief This static class is for debugging 
 * @todo Add debug outputs for more data types	
 */
public class Debug
{
	private final static String LOG_TAG = "TSC";
	private static boolean DEBUG = true;
	
	/**
	 * Sets if the app is in debug mode. 
	 * @param inDebug If set to true then outputs will be made, else they wont
	 */
	public static void setDebugMode(boolean inDebug)
	{
		DEBUG = inDebug;
	}
	
	private static String getCallingMethodInfo()
	{
		Throwable fakeException = new Throwable();
		StackTraceElement[] stackTrace = fakeException.getStackTrace();

		if (stackTrace != null && stackTrace.length >= 2)
		{
			StackTraceElement s = stackTrace[2];
			if (s != null)
			{
				return s.getFileName() + "(" + s.getMethodName() + ":" + s.getLineNumber() + "):";
			}
		}

		return null;
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(String message)
	{
		if (!DEBUG) return;
		
		try
		{			
			Log.e(LOG_TAG, getCallingMethodInfo() + " " + message);
		}
		catch (Exception e)
		{
		}
	}	
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(String... message)
	{
		if (!DEBUG) return;
		
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
	public static void out(Exception message)
	{
		Debug.out(message.getLocalizedMessage());
		Debug.out(message.getStackTrace());
	}
	
	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(String[]... message)
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
			out("" + message);
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
			out("" + message);
		}
		catch (Exception e)
		{
		}
	}

	/**
	* Outputs a message to the debug console
	* @param message The message to output	 
	*/
	public static void out(Object... message)
	{
		try
		{
			Debug.out(message.toString());
			int index = 0;
			for (Object m : message)
			{
				out("\tIndex " + index++ + " : " + m.toString());
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
		if (!DEBUG) return;
		
		try
		{
			Log.e(LOG_TAG, getCallingMethodInfo() + " " + "" + message.toString(1));
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
		if (!DEBUG) return;
		
		try
		{
			Log.e(LOG_TAG, getCallingMethodInfo() + " " + "" + message.toString(1));
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
			out("" + message);
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
			out("" + message);
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
			out("" + message);
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
	public static void out(double... message)
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
	 * Outputs a message to the debug console
	 * @param bundle The bundle to output
	 */
	public static void out(Bundle bundle)
	{
		try
		{ 
			Set<String> keys = bundle.keySet();
			out(bundle.toString());
			
			for (String key : keys)
			{
				out("\t[" + key + "] : " + bundle.get(key).toString());
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
		if (!DEBUG) return;
		
	    Double allocated = new Double(android.os.Debug.getNativeHeapAllocatedSize()) / new Double((1048576));
	    Double available = new Double(android.os.Debug.getNativeHeapSize() / 1048576.0);
	    Double free = new Double(android.os.Debug.getNativeHeapFreeSize() / 1048576.0);
	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(2);
	    df.setMinimumFractionDigits(2);

	    Log.e("MEM", "" + System.currentTimeMillis() + " - DUMP: " + msg);
	    Log.e("MEM", "Memory Heap Debug. ==============================================================================================================");
	    Log.e("MEM", "Memory Heap Native: Allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [" + mClass.getName() + "]");
	    Log.e("MEM", "Memory Heap App: Allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory() / 1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory() / 1048576)) + "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory() / 1048576)) + "MB free)");
	    Log.e("MEM", "Memory Heap Debug. ==============================================================================================================");
	}
}
