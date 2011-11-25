package x.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;

/**
 * @Brief Utilities for bitmap processing
 */
public class BitmapUtils
{
	public static int FLIP_HORIZONTAL = 0x01;
	public static int FLIP_VERTICAL = 0x10;
	
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
		canvas.drawBitmap(bm, new Rect(0, 0, bm.getWidth(), bm.getHeight()), new Rect(0, 0, width, height), new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG));
		
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
	 * Fixes the orientation of a bitmap. Original bitmap is recycled after this method is called
	 * @param bm The bitmap to fix
	 * @param currentOrientation The current orientation as discripted in {@link ExifInterface}
	 * @return The fixed bitmap
	 */
	public static Bitmap fixOrientation(Bitmap bm, int currentOrientation)
	{
		switch (currentOrientation)
		{
			//	Horizontal
			case 2:
			{
				return flip(bm, FLIP_HORIZONTAL);
			}
			
			//	180 rotate left
			case 3:	
			{
				return rotate(bm, -180);
			}
			
			//	Vertical flip
			case 4:	
			{
				return flip(bm, FLIP_VERTICAL);
			}
			
			//	Vertical flip + 90 rotate right
			case 5:
			{
				return rotate(flip(bm, FLIP_VERTICAL), 90);				
			}
		
			//	90 rotate right
			case 6:	
			{
				return rotate(bm, 90);
			}
		
			//	horizontal flip + 90 rotate right
			case 7:	
			{
				return rotate(flip(bm, FLIP_HORIZONTAL), 90);				
			}
		
			//	90 rotate left
			case 8:	
			{
				return rotate(bm, -90);				
			}
		}
		
		return bm;
	}
}
