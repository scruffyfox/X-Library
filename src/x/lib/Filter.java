package x.lib;

import x.lib.Filter.FilterSet.FilterMode;
import x.util.BitmapUtils;
import x.util.BitmapUtils.Colour;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class Filter
{
	private Bitmap bm;
	private Colour[] colourList;
			
	public Filter(Bitmap bitmap)
	{			
		bm = bitmap;
		int width = bm.getWidth();
		int height = bm.getHeight();
		
		colourList = new Colour[width * height];
		
		int index = 0;
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				int c = bm.getPixel(x, y);
				int r = (c >> 16) & 0xFF;
				int g = (c >> 8) & 0xFF;
				int b = c & 0x0000FF;
				
				colourList[index++] = new Colour(255, r, g, b);
			}
		}
	}
	
	public void apply(FilterSet[] filters)
	{
		for (int index = 0; index < colourList.length; index++)
		{
			for (FilterSet fset : filters)
			{
				if (fset.filter == FilterMode.ADJUST)
				{
					adjust(fset.amount, index, index + 1);
				}					
				else if (fset.filter == FilterMode.SATURATION)					
				{
					saturation(fset.amount, index, index + 1);
				}					
				else if (fset.filter == FilterMode.POSTERIZE)					
				{
					posterize(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.BIAS)					
				{
					bias(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.BRIGHTNESS)
				{
					brightness(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.CONTRAST)
				{
					contrast(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.GAMMA)
				{
					gamma(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.INVERT)
				{
					invert(index, index + 1);
				}
				else if (fset.filter == FilterMode.MONOTONE)
				{
					monotone(index, index + 1);
				}
				else if (fset.filter == FilterMode.OPACITY)
				{
					opacity(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.SEPIA)
				{
					sepia(index, index + 1);
				}
				else if (fset.filter == FilterMode.THRESHOLD)
				{
					threshold(fset.amount, index, index + 1);
				}
				else if (fset.filter == FilterMode.TINT)
				{
					tint(fset.amount, fset.amount2, index, index + 1);
				}				
			}
		}
	}
	
	public Bitmap flatten()
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		
		Bitmap finalBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(finalBitmap);						
		
		Paint p = new Paint();
		int index = 0;
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{					
				p.setARGB(colourList[index].alpha, colourList[index].red, colourList[index].green, colourList[index].blue);
				c.drawRect(x, y, x + 1, y + 1, p);
				index++;
			}
		}
		
		c = null;
		bm.recycle();
		bm = null;
		colourList = null;
		System.gc();
		
		return finalBitmap;
	}
		
	public void saturation(int amount)
	{
		saturation(amount, 0, colourList.length);
	}
	
	/**
	 * 
	 * @param amount 0-100
	 */
	private void saturation(int amount, int start, int colourIndex)
	{
		double t = (double)amount / 100.0;
		for (int index = start; index < colourIndex; index++)
		{
			int average = (colourList[index].red + colourList[index].green + colourList[index].blue) / 3;
			colourList[index].red = BitmapUtils.safe((int)(average + t * (colourList[index].red - t)));
			colourList[index].green = BitmapUtils.safe((int)(average + t * (colourList[index].green - t)));
			colourList[index].blue = BitmapUtils.safe((int)(average + t * (colourList[index].blue - t)));
		}
	}
	
	public void posterize(int amount)
	{
		posterize(amount, 0, colourList.length);
	}
	
	/**
	 * 
	 * @param amount 0-255
	 */
	private void posterize(int amount, int start, int colourIndex)
	{
		double step = Math.floor(255.0 / (double)amount);
		for (int index = start; index < colourIndex; index++)
		{ 
			colourList[index].red = BitmapUtils.safe((int)(Math.floor((double)colourList[index].red / step) * step));
			colourList[index].green = BitmapUtils.safe((int)(Math.floor((double)colourList[index].green / step) * step));
			colourList[index].blue = BitmapUtils.safe((int)(Math.floor((double)colourList[index].blue / step) * step));
		}
	}	
	
	public void threshold(int amount)
	{
		threshold(amount, 0, colourList.length);
	}
	
	/**
	 * 
	 * @param amount 0-255
	 */
	private void threshold(int amount, int start, int colourIndex)
	{
		for (int index = start; index < colourIndex; index++)
		{
			int colour = 255;
			if (colourList[index].red < amount || colourList[index].green < amount || colourList[index].blue < amount)
			{
				colour = 0;
			}
			
			colourList[index].red = BitmapUtils.safe(colour);
			colourList[index].green = BitmapUtils.safe(colour);
			colourList[index].blue = BitmapUtils.safe(colour);
		}
	}
	
	public void brightness(int amount)
	{
		brightness(amount, 0, colourList.length);
	}
	
	/**
	 * 
	 * @param amount 0-255
	 */
	private void brightness(int amount, int start, int colourIndex)
	{
		for (int index = start; index < colourIndex; index++)
		{			
			colourList[index].red = BitmapUtils.safe(colourList[index].red + amount);
			colourList[index].green = BitmapUtils.safe(colourList[index].green + amount);
			colourList[index].blue = BitmapUtils.safe(colourList[index].blue + amount);
		}
	}
	
	public void gamma(int amount)
	{
		gamma(amount, 0, colourList.length);
	}
	
	/**
	 * 
	 * @param amount 0-100
	 */
	private void gamma(int amount, int start, int colourIndex)
	{
		double t = ((double)amount / 100.0);
		
		for (int index = start; index < colourIndex; index++)
		{			
			int redGamma = (int)Math.min(255, (int)((255.0 * Math.pow(colourList[index].red / 255.0, 1.0 / t)) + 0.5));  
	        int greenGamma = (int)Math.min(255, (int)((255.0 * Math.pow(colourList[index].green / 255.0, 1.0 / t)) + 0.5));  
	        int blueGamma = (int)Math.min(255, (int)((255.0 * Math.pow(colourList[index].blue / 255.0, 1.0 / t)) + 0.5));  
			
			colourList[index].red = redGamma;
			colourList[index].green = greenGamma;
			colourList[index].blue = blueGamma;
		}
	}
	
	public void adjust(int colour)
	{
		adjust(colour, 0, colourList.length);
	}
	
	/**
	 * Magnifies the colour to the max value in each channel 
	 * For example:
	 * 	Original Red: 150
	 * 	Mask: 0x000A0000
	 * 	New Red: 165 
	 * @param colour use 32bit colour mask the amount for each colour
	 */
	private void adjust(int colour, int start, int colourIndex)
	{
		double r = (1.0 / 255.0) * (double)Color.red(colour);
		double g = (1.0 / 255.0) * (double)Color.green(colour);
		double b = (1.0 / 255.0) * (double)Color.blue(colour);
		double a = (1.0 / 255.0) * (double)Color.alpha(colour);
				
		for (int index = start; index < colourIndex; index++)
		{						
			colourList[index].red = BitmapUtils.safe(colourList[index].red * (r + 1));
			colourList[index].green = BitmapUtils.safe(colourList[index].green * (g + 1));
			colourList[index].blue = BitmapUtils.safe(colourList[index].blue * (b + 1));
		}
	}
	
	public void invert()
	{
		invert(0, colourList.length);
	}
	
	private void invert(int start, int colourIndex)
	{
		for (int index = start; index < colourIndex; index++)
		{			
			colourList[index].red = BitmapUtils.safe(255 - colourList[index].red);
			colourList[index].green = BitmapUtils.safe(255 - colourList[index].green);
			colourList[index].blue = BitmapUtils.safe(255 - colourList[index].blue);
		}
	}
	
	public void monotone()
	{
		monotone(0, colourList.length);
	}
	
	private void monotone(int start, int colourIndex) 
	{
		for (int index = start; index < colourIndex; index++)
		{	
			int average = (colourList[index].red + colourList[index].green + colourList[index].blue) / 3;
			colourList[index].red = BitmapUtils.safe(average);
			colourList[index].green = BitmapUtils.safe(average);
			colourList[index].blue = BitmapUtils.safe(average);
		}
	}
	
	public void contrast(int amount)
	{
		contrast(amount, 0, colourList.length);
	}
	
	private void contrast(int amount, int start, int colourIndex)
	{
		double t = (double)amount / 100.0;
		for (int index = start; index < colourIndex; index++)
		{	
			double r = ((double)colourList[index].red / 255.0);
			r = (r - 0.5) * (t + 0.5);
		
			double g = ((double)colourList[index].green / 255.0);
			g = (g - 0.5) * (t + 0.5);
			
			double b = ((double)colourList[index].blue / 255.0);
			b = (b - 0.5) * (t + 0.5);
					
			colourList[index].red = BitmapUtils.safe((int)(255 * r));
			colourList[index].green= BitmapUtils.safe((int)(255 * g));
			colourList[index].blue = BitmapUtils.safe((int)(255 * b));
		}
	}
	
	public void bias(int amount)
	{
		bias(amount, 0, colourList.length);
	}
	
	private void bias(int amount, int start, int colourIndex)
	{
		double t = (1.0 / 255.0 * (double)amount);				
		for (int index = start; index < colourIndex; index++)
		{	
			double r = ((double)colourList[index].red / 255.0);
			r = r / ((1.0 / t - 1.9) * (0.9 - r) + 1);
		
			double g = ((double)colourList[index].green / 255.0);
			g = g / ((1.0 / t - 1.9) * (0.9 - g) + 1);
			
			double b = ((double)colourList[index].blue / 255.0);
			b = b / ((1.0 / t - 1.9) * (0.9 - b) + 1);
					
			colourList[index].red = BitmapUtils.safe((int)(255 * r));
			colourList[index].green= BitmapUtils.safe((int)(255 * g));
			colourList[index].blue = BitmapUtils.safe((int)(255 * b));
		}
	}
	
	public void sepia()
	{
		sepia(0, colourList.length);		
	}
	
	private void sepia(int start, int colourIndex)
	{
		for (int index = start; index < colourIndex; index++)
		{	
			double r = colourList[index].red;
			double g = colourList[index].green;
			double b = colourList[index].blue;
			
			r = ((r * 0.393) + (g * 0.769) + (b * 0.189));
			g = ((r * 0.349) + (g * 0.686) + (b * 0.168));
			b = ((r * 0.272) + (g * 0.534) + (b * 0.131));
			
			colourList[index].red = BitmapUtils.safe((int)r);
			colourList[index].green= BitmapUtils.safe((int)g);
			colourList[index].blue = BitmapUtils.safe((int)b);
		}
	}
	
	public void opacity(int amount)
	{
		opacity(amount, 0, colourList.length);
	}
	
	private void opacity(int amount, int start, int colourIndex)
	{
		for (int index = start; index < colourIndex; index++)
		{				
			colourList[index].alpha = amount;
		}
	}
	
	private void tint(int minColour, int maxColour, int start, int colourIndex)
	{
		for (int index = start; index < colourIndex; index++)
		{	
			int minRed = Color.red(minColour);
			int minGreen = Color.green(minColour);
			int minBlue = Color.blue(minColour);
			
			int maxRed = Color.red(maxColour);
			int maxGreen = Color.green(maxColour);
			int maxBlue = Color.blue(maxColour);
			
			int r = (colourList[index].red - minRed) * (int)((255.0 / (double)Math.max(1, (maxRed - minRed))));
			int g = (colourList[index].green - minGreen) * (int)((255.0 / (double)Math.max(1, (maxGreen - minGreen))));
			int b = (colourList[index].blue - minBlue) * (int)((255.0 / (double)Math.max(1, (maxBlue - minBlue))));
			
			colourList[index].red = BitmapUtils.safe((int)r);
			colourList[index].green= BitmapUtils.safe((int)g);
			colourList[index].blue = BitmapUtils.safe((int)b);
		}
	}
	
	public static class FilterSet
	{		
		public enum FilterMode
		{
			SATURATION,
			POSTERIZE,
			THRESHOLD,
			BRIGHTNESS,
			GAMMA,
			INVERT,
			MONOTONE,
			CONTRAST,
			BIAS,
			SEPIA,			
			OPACITY,
			TINT,
			ADJUST
		}
		
		public FilterMode filter;
		public int amount = 0;
		public int amount2 = 0;
		
		public FilterSet(FilterMode f)
		{
			filter = f;
		}
		
		public FilterSet(FilterMode f, int a)
		{
			filter = f;
			amount = a;
		}
		
		public FilterSet(FilterMode f, int a, int a2)
		{
			filter = f;
			amount = a;
			amount2 = a2;
		}
	}
}