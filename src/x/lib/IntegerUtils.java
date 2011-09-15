/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

/**
 * @brief The class that gives more manipulation options for integer
 */
class IntegerUtils
{
	/**
	 * Parses an string to integer by removing any non-integer character
	 * @param str The input integer value as a string
	 * @return The converted integer value (I.E. 1,000 becomes 1000) returns 0 if fails
	 */
	public static int parseInt(String str)
	{		
		str = str.replaceAll("[^0-9]+", "");
		
		try
		{
			return Integer.parseInt(str);			
		}
		catch (Exception e)
		{
			return 0;
		}				
	}
}