/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

/**
 * @brief An extention to java Random
 */
public class Random extends java.util.Random
{
	/**
	 * Gets a random integer within the range of a 32bit hex color. Alpha is always 255.
	 * @return The generated hex color
	 */
	public int nextHexColor()
	{
		int randomInt = nextInt(0xffffff);
		return randomInt + (0xff << 24);
	}
	
	/**
	 * Gets a random number between 2 numbers
	 * @param from The minimum number to start from
	 * @param to The maximum number to return
	 * @return A random int between from and to
	 */
	public int nextInt(int from, int to)
	{		
		int range = to - from + 1;
		int ret = (nextInt(range)) + from;
		
		return ret;
	}
}
