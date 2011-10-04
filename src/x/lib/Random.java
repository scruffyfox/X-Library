package x.lib;

public class Random extends java.util.Random
{
	public int nextHexColor()
	{
		int randomInt = nextInt(0xffffff);
		return randomInt + (0xff << 24);
	}
}
