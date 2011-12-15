package x.lib;

/**
 * An extention to java Random
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
	
	public int nextInt(int from, int to)
	{
		Random r = new Random();
		int range = to - from + 1;
		return (r.nextInt(range)) + from;
	}
}
