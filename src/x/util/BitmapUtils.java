package x.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BitmapUtils
{
	public static Bitmap resize(Bitmap bm, int width, int height)
	{
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(bm, new Rect(0, 0, bm.getWidth(), bm.getHeight()), new Rect(0, 0, width, height), new Paint());
		
		bm.recycle();
		
		return newBitmap;
	}
	
	public static Bitmap resizeToWidth(Bitmap bm, int width)
	{
		//	Calculate the new height
		float ratio = (float)width / (float)bm.getWidth();
		int height = (int)((float)bm.getHeight() * ratio);
		
		return BitmapUtils.resize(bm, width, height);
	}
	
	public static Bitmap resizeToHeight(Bitmap bm, int height)
	{
		//	Calculate the new width
		float ratio = (float)height / (float)bm.getHeight();
		int width = (int)((float)bm.getWidth() * ratio);
		
		return BitmapUtils.resize(bm, width, height);
	}
	
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
}
