/**
 * @brief x util is the utility library which includes the method extentions for common data types
 * 
 * @author Callum Taylor
**/
package x.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import x.lib.Debug;
import x.lib.Filter;
import x.lib.Filter.FilterSet;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.media.ExifInterface;

/**
 * @brief Utilities for bitmap processing and manipulation
 */
public class BitmapUtils
{
	public static int FLIP_HORIZONTAL = 0x01;
	public static int FLIP_VERTICAL = 0x10;
	
	//	Orientation vars
	public static final int ORIENTATION_HORIZONTAL = 2;
	public static final int ORIENTATION_180_ROTATE_LEFT = 3;
	public static final int ORIENTATION_VERTICAL_FLIP = 4;
	public static final int ORIENTATION_VERTICAL_FLIP_90_ROTATE_RIGHT = 5;
	public static final int ORIENTATION_90_ROTATE_RIGHT = 6;
	public static final int ORIENTATION_HORIZONTAL_FLIP_90_ROTATE_RIGHT = 7;
	public static final int ORIENTATION_90_ROTATE_LEFT = 8;
	 
	/**
	 * Makes sure the colour does not exceed the 0-255 bounds
	 * @param colour The colour integer
	 * @return The numerical value of the colour
	 */
	public static int safe(int colour)
	{
		return Math.min(255, Math.max(colour, 0));
	}
	
	/**
	 * Makes sure the colour does not exceed the 0-255 bounds
	 * @param colour The colour integer
	 * @return The numerical value of the colour
	 */
	public static int safe(double colour)
	{
		return (int)Math.min(255.0, Math.max(colour, 0.0));
	}
	
	/**
	 * Resizes a bitmap. Original bitmap is recycled after this method is called.
	 * @param bm The bitmap to resize
	 * @param width The new width
	 * @param height Thew new height
	 * @return The resized bitmap
	 */
	public static Bitmap resize(Bitmap bm, int width, int height)
	{
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(bm, new Rect(0, 0, bm.getWidth(), bm.getHeight()), new Rect(0, 0, width, height), new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		
		bm.recycle();
		
		return newBitmap;
	}

	/**
	 * Resizes a bitmap to a specific width, maintaining the ratio. Original bitmap is recycled after this method is called.
	 * @param bm The bitmap to resize
	 * @param width The new width
	 * @return The resized bitmap
	 */
	public static Bitmap resizeToWidth(Bitmap bm, int width)
	{
		//	Calculate the new height
		float ratio = (float)width / (float)bm.getWidth();
		int height = (int)((float)bm.getHeight() * ratio);
		
		return BitmapUtils.resize(bm, width, height);
	}
	
	/**
	 * Resizes a bitmap to a specific height, maintaining the ratio. Original bitmap is recycled after this method is called.
	 * @param bm The bitmap to resize
	 * @param height The new width
	 * @return The resized bitmap
	 */
	public static Bitmap resizeToHeight(Bitmap bm, int height)
	{
		//	Calculate the new width
		float ratio = (float)height / (float)bm.getHeight();
		int width = (int)((float)bm.getWidth() * ratio);
		
		return BitmapUtils.resize(bm, width, height);
	}
	
	/**
	 * Resizes a bitmap to a which ever axis is the largest, maintaining the ratio. Original bitmap is recycled after this method is called.
	 * @param bm The bitmap to resize
	 * @param maxWidth The max width size
	 * @param maxHeight The max height size
	 * @return The resized bitmap
	 */
	public static Bitmap maxResize(Bitmap bm, int maxWidth, int maxHeight)
	{		
		//	Calculate what is larger width or height
		if (bm.getWidth() > bm.getHeight())
		{
			return BitmapUtils.resizeToWidth(bm, maxWidth);
		}
		else
		{
			return BitmapUtils.resizeToHeight(bm, maxHeight);
		}
	}
	
	/**
	 * Compresses a bitmap. original bitmap is recycled after this method is called.
	 * @param bm The bitmap to be compressed.
	 * @param compression The compression ratio 0-100.
	 * @return The compressed bitmap.
	 */
	public static Bitmap compress(Bitmap bm, int compression)
	{
		ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, compression, bitmapOutputStream);
		bm.recycle();
		
		return BitmapFactory.decodeByteArray(bitmapOutputStream.toByteArray(), 0, bitmapOutputStream.size());		
	}
	
	/**
	 * Rotates a bitmap. Original bitmap is recycled after this method is called.
	 * @param bm The bitmap to rotate
	 * @param degrees The degrees to rotate at. 0-360 clockwise.
	 * @return The rotated bitmap
	 */
	public static Bitmap rotate(Bitmap bm, int degrees)
	{
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate((float)degrees);		
		
		Bitmap newBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotateMatrix, true);
		bm.recycle();
		
		return newBitmap;
	}
	
	/**
	 * Flips an image
	 * @param bm The image to flip. Original bitmap is recycled after this method is called.
	 * @param mode The mode to flip
	 * @return The flipped bitmap
	 */
	public static Bitmap flip(Bitmap bm, int mode)
	{
		Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		Matrix flipMatrix = new Matrix();
		float xFlip = 1.0f, yFlip = 1.0f;
		
		if ((mode & FLIP_HORIZONTAL) == mode)
		{
			xFlip = -1.0f;
		}
		
		if ((mode & FLIP_VERTICAL) == mode)
		{
			yFlip = -1.0f;
		}
		
		flipMatrix.preScale(xFlip, yFlip);
		canvas.drawBitmap(bm, flipMatrix, new Paint());
		bm.recycle();
		
		return newBitmap;
	}
	
	/**
	 * Crops a bitmap at the given indexes. Original bitmap is recycled after this method is called.
	 * @param bm The bitmap to crop
	 * @param startX The start x coord starting in TOP LEFT
	 * @param startY The start y coord starting in TOP LEFT
	 * @param width The width of the crop
	 * @param height The height of the crop
	 * @return The newly cropped bitmap.
	 */
	public static Bitmap crop(Bitmap bm, int startX, int startY, int width, int height)
	{
		int w = width;
	    int h = height;
	    
	    Bitmap ret = Bitmap.createBitmap(w, h, bm.getConfig());
	    Canvas canvas = new Canvas(ret);
	    canvas.drawBitmap(bm, -startX, -startY, null);
	    bm.recycle();
	    
	    return ret;
	}
	
	/**
	 * Blend modes to use with the merge method
	 */
	public static enum BlendMode
	{			
		//	Standard PorterDuff modes
		CLEAR,
		DARKEN,
		DST,
		DST_ATOP,
		DST_IN,
		DST_OUT,
		DST_OVER,
		LIGHTEN,
		MULTIPLY,
		SCREEN,
		SRC,
		SRC_ATOP,
		SRC_IN,
		SRC_OUT,
		SRC_OVER,
		XOR,
		
		//	Custom Blend modes
		OVERLAY,
		ADD,
		DIFFERENCE,
		EXCLUSION,
		SOFTLIGHT
	}
	
	/**
	 * Merges an image ontop of another image using a specific blend mode. Both images are recycled after the merge.
	 * @param original The bottom image
	 * @param overlay The image to merge to
	 * @param blendMode The blending mode of the merge
	 * @return The merged images
	 */
	public static Bitmap merge(Bitmap original, Bitmap overlay, BlendMode blendMode)
	{
		try
		{
			PorterDuff.Mode m = PorterDuff.Mode.valueOf(blendMode.name());

			int w = original.getWidth();
			int h = original.getHeight();
			
			Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas c = new Canvas(newBitmap);
			c.drawBitmap(original, 0, 0, null);
			
			
			Paint paint = new Paint();
			paint.setXfermode(new PorterDuffXfermode(m));
			
			c.drawBitmap(overlay, new Rect(0, 0, overlay.getWidth(), overlay.getHeight()), new Rect(0, 0, w, h), paint);
			
			original.recycle();
			overlay.recycle();				
			
			return newBitmap;
		}
		catch (IllegalArgumentException e) 
		{
			int w = original.getWidth();
			int h = original.getHeight();
			
			Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas c = new Canvas(newBitmap);
			c.drawBitmap(overlay, new Rect(0, 0, overlay.getWidth(), overlay.getHeight()), new Rect(0, 0, w, h), new Paint());
			
			for (int x = 0; x < w; x++)
			{
				for (int y = 0; y < h; y++)
				{
					int colour1 = original.getPixel(x, y);
					int colour2 = newBitmap.getPixel(x, y);
					
					double[] rgb1 = new double[]{Color.red(colour1), Color.green(colour1), Color.blue(colour1)};
					double[] rgb2 = new double[]{Color.red(colour2), Color.green(colour2), Color.blue(colour2)};
					int[] rgb3 = new int[3];
					
					if (blendMode == BlendMode.OVERLAY)
					{
						rgb3[0] = safe(rgb1[0] > 128.0 ? (255.0 - 2.0 * (255.0 - rgb2[0]) * (255.0 - rgb1[0]) / 255.0) : ((rgb1[0] * rgb2[0] * 2.0) / 255.0));
						rgb3[1] = safe(rgb1[1] > 128.0 ? (255.0 - 2.0 * (255.0 - rgb2[1]) * (255.0 - rgb1[1]) / 255.0) : ((rgb1[1] * rgb2[1] * 2.0) / 255.0));
						rgb3[2] = safe(rgb1[2] > 128.0 ? (255.0 - 2.0 * (255.0 - rgb2[2]) * (255.0 - rgb1[2]) / 255.0) : ((rgb1[2] * rgb2[2] * 2.0) / 255.0));
					} 
					else if (blendMode == BlendMode.DIFFERENCE)
					{
						rgb3[0] = safe(Math.abs(rgb2[0] - rgb1[0]));
						rgb3[1] = safe(Math.abs(rgb2[1] - rgb1[1]));
						rgb3[2] = safe(Math.abs(rgb2[2] - rgb1[2]));
					}
					else if (blendMode == BlendMode.SOFTLIGHT)
					{
						rgb3[0] = safe(rgb1[0] > 128 ? 255 - ((255 - rgb1[0]) * (255 - (rgb2[0] - 128))) / 255 : (rgb1[0] * (rgb2[0] + 128)) / 255);
						rgb3[1] = safe(rgb1[1] > 128 ? 255 - ((255 - rgb1[1]) * (255 - (rgb2[1] - 128))) / 255 : (rgb1[1] * (rgb2[1] + 128)) / 255);
						rgb3[2] = safe(rgb1[2] > 128 ? 255 - ((255 - rgb1[2]) * (255 - (rgb2[2] - 128))) / 255 : (rgb1[2] * (rgb2[2] + 128)) / 255);
					}
					
					Paint p = new Paint();					
					p.setARGB(255, rgb3[0], rgb3[1], rgb3[2]);
					c.drawRect(x, y, x + 1, y + 1, p);
				}
			}
			
			original.recycle();
			overlay.recycle();
			
			return newBitmap; 
		}		
	}
	
	/**
	 * Fixes the orientation of a bitmap. Original bitmap is recycled after this method is called
	 * @param bm The bitmap to fix
	 * @param currentOrientation The current orientation as discripted in {@link ExifInterface}
	 * @return The fixed bitmap
	 */
	public static Bitmap fixOrientation(Bitmap bm, int currentOrientation)
	{
		switch (currentOrientation)
		{
			case ORIENTATION_HORIZONTAL:
			{
				return flip(bm, FLIP_HORIZONTAL);
			}
			
			case ORIENTATION_180_ROTATE_LEFT:	
			{
				return rotate(bm, -180);
			}
			
			case ORIENTATION_VERTICAL_FLIP:	
			{
				return flip(bm, FLIP_VERTICAL);
			}
			
			case ORIENTATION_VERTICAL_FLIP_90_ROTATE_RIGHT:
			{
				return rotate(flip(bm, FLIP_VERTICAL), 90);				
			}
		
			case ORIENTATION_90_ROTATE_RIGHT:	
			{
				return rotate(bm, 90);
			}
		
			case ORIENTATION_HORIZONTAL_FLIP_90_ROTATE_RIGHT:	
			{
				return rotate(flip(bm, FLIP_HORIZONTAL), 90);				
			}
		
			case ORIENTATION_90_ROTATE_LEFT:	
			{
				return rotate(bm, -90);				
			}
		} 
		
		return bm;
	}
	
	/**
	 * Applies a saturation filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of saturation to apply 0-255
	 * @return The new image
	 */
	public static Bitmap saturation(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.saturation(amount);
		
		return f.flatten();
	}
	
	/**
	 * Applies a posterize filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of posterization to apply 0-255
	 * @return The new image
	 */
	public static Bitmap posterize(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.posterize(amount);
		
		return f.flatten();
	}
	
	/**
	 * Applies a threshold filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of threshold to apply 0-255
	 * @return The new image
	 */
	public static Bitmap threshold(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.threshold(amount);
		
		return f.flatten();
	}	
	
	/**
	 * Applies a bais filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of bias to apply 0-255
	 * @return The new image
	 */
	public static Bitmap bias(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.bias(amount);
		
		return f.flatten();
	}
	
	/**
	 * Applies a brighrness filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of brightness to apply 0-255
	 * @return The new image
	 */
	public static Bitmap brightness(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.brightness(amount);
		
		return f.flatten();
	}
	
	/**
	 * Applies a contrast filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of contrast to apply 0-100
	 * @return The new image
	 */
	public static Bitmap contrast(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.contrast(amount);
		
		return f.flatten();
	}
	
	/**
	 * Applies a gamma filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of gamma to apply 0-100
	 * @return The new image
	 */
	public static Bitmap gamma(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.gamma(amount);
		
		return f.flatten();
	}
	
	/**
	 * Inverts the image's colours
	 * @param b The image to apply the filter to
	 * @return The new image
	 */
	public static Bitmap invert(Bitmap b)
	{
		Filter f = new Filter(b);
		f.invert();
		
		return f.flatten();
	}
	
	/**
	 * Sets the image's colour to black and white
	 * @param b The image to apply the filter to
	 * @return The new image
	 */
	public static Bitmap monotone(Bitmap b)
	{
		Filter f = new Filter(b);
		f.monotone();
		
		return f.flatten();
	}
	
	/**
	 * Applies a sepia filter
	 * @param b The image to apply the filter to
	 * @return The new image
	 */
	public static Bitmap sepia(Bitmap b)
	{
		Filter f = new Filter(b);
		f.sepia();
		
		return f.flatten();
	}
	
	/**
	 * Applies a opacity filter to an image
	 * @param b The image to apply the filter to
	 * @param amount The amount of opacity to apply 0-255
	 * @return The new image
	 */
	public static Bitmap opacity(Bitmap b, int amount)
	{
		Filter f = new Filter(b);
		f.opacity(amount);
		
		return f.flatten();
	}
	
	/**
	 * Applies a filter set to the image
	 * @param b The image to apply the filters to
	 * @param filters The filter set
	 * @return The new bitmap
	 */
	public static Bitmap applyFilterSet(Bitmap b, FilterSet[] filters)
	{
		Filter f = new Filter(b);
		f.apply(filters);
		
		return f.flatten();
	}
			
	/**
	 * Class for holding ARGB colour
	 */
	public static class Colour
	{
		public int alpha = 255;
		public int red = 255;
		public int green = 255;
		public int blue = 255;
		
		/**
		 * Constructor
		 * @param a Alpha 0-255
		 * @param r Red channel 0-255
		 * @param g Green channel 0-255
		 * @param b Blue channel 0-255
		 */
		public Colour(int a, int r, int g, int b)
		{
			alpha = a;
			red = r;
			green = g;
			blue = b;
		}
	}
}
