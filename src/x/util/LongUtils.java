/**
 * @brief x util is the utility library which includes the method extentions for common data types
 * 
 * @author Callum Taylor
**/
package x.util;

/**
 * @brief The class that gives more manipulation options for Long
 */
public class LongUtils
{
	/**
	 * Parses an string to long by removing any non-integer character
	 * @param str The input long value as a string
	 * @return The converted long value (I.E. 1,000 becomes 1000) returns 0 if fails
	 */
	public static long parseLong(String str)
	{		
		str = str.replaceAll("[^0-9]+", "");
		
		try
		{
			return Long.parseLong(str);			
		}
		catch (Exception e)
		{
			return 0;
		}				
	}
	
	/**
	 * Adds commas to a long
	 * @param value The long value
	 * @return Comma formatted string value of the long (I.E. 1000 becomes 1,000)
	 */
	public static String addCommas(long value)
	{
		String finalString = "";
		String intStr = "" + value;
		
		int strCount = intStr.length();
		for (int index = strCount - 1, pointerCount = 0; index >= 0; index--, pointerCount++)
		{
			if (pointerCount > 0)
			{
				if (pointerCount % 3 == 0)
				{
					finalString = "," + finalString;
				}
			}
			
			finalString = intStr.charAt(index) + finalString;
		}
		
		return finalString;
	}
}