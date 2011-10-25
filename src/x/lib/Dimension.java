/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @brief Calculates the dimension value 
 */
public class Dimension
{
	private DisplayMetrics mDM;
	private float mRatio = (800.0f / 480.0f);
	
	/**
	 * The default constructor
	 * @param context The activity context 
	 */
	public Dimension(Activity context)
	{
		mDM = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(mDM);		
	}
	
	/**
	 * Sets the ratio for the conversion
	 * @param width The width of the desired ratio
	 * @param height The height of the desired ratio
	 */
	public void setRatio(int width, int height)
	{
		mRatio = ((float)width / (float)height);
	}
	
	/**
	 * Sets the ratio for the conversion
	 * @param ratio The new ratio
	 */
	public void setRatio(float ratio)
	{
		mRatio = ratio;
	}
	
	/** 
	 * Get the calculated width of the ratio and desired width
	 * @param width The original width
	 * @return The new width calculated
	 */
	public float getWidthFromRatio(int width)
	{
		return mRatio * (float)width;
	}
	
	/**
	 * Get the calculated height of the ratio and desired height
	 * @param width The original height
	 * @return The new height calculated
	 */
	public float getHeightFromRatio(int height)
	{
		return mRatio * (float)height;
	}
	
	/**
	 * Get the width value from a screen percentage (0 - 100%)
	 * @param percent The amount of the screen in percentage from 0 to 100
	 * @return The size in px of the screen from the given percentage
	 */
	public int getPercentageWidthValue(int percent)
	{		
		return (int)(((float)percent / 100.0f) * mDM.widthPixels);
	}
	
	/**
	 * Get the height value from a screen percentage (0 - 100%)
	 * @param percent The amount of the screen in percentage from 0 to 100
	 * @return The size in px of the screen from the given percentage
	 */
	public int getPercentageHeightValue(int percent)
	{
		return (int)(((float)percent / 100.0f) * mDM.heightPixels);
	}
	
	/**
	 * Get the width value from a screen percentage (0.0f - 1.0f)
	 * @param percent The amount of the screen in percentage from 0.0f to 1.0f
	 * @return The size in px of the screen from the given percentage
	 */
	public float getPercentageWidthValue(float percent)
	{
		return (percent) * (float)mDM.widthPixels;
	}
	
	/**
	 * Get the height value from a screen percentage (0.0f - 1.0f)
	 * @param percent The amount of the screen in percentage from 0.0f to 1.0f
	 * @return The size in px of the screen from the given percentage
	 */
	public float getPercentageHeightValue(float percent)
	{
		return (percent) * (float)mDM.heightPixels;
	}
	
	/**
	 * Calculates the percentage of the size relative to the screen
	 * @param width The given width
	 * @return The percentage value of the width
	 */
	public float getCalculatedPerecentageFromWidth(int width)
	{
		return (float)(1.0f / (mDM.widthPixels / width));
	}
	
	/**
	 * Calculate the dp value from pixels
	 * @param px The input pixel size
	 * @return The size in dp
	 */
	public int densityPixelInverse(int px)
	{	
		int pixels = (int)((float)px * (1.0f / mDM.density));
	 
		return pixels;
	}
	
	/**
	 * Calculate the pixel value from density pixels
	 * @param dp The input density pixel value
	 * @return The converted pixel value
	 */
	public int densityPixel(int dp)
	{	
		int pixels = (int)((float)dp * mDM.density);
	 
		return pixels;
	}
	
	/**
	 * Gets the screen width of the activity
	 * @return Screen Width
	 */
	public int getScreenWidth()
	{
		return mDM.widthPixels;
	}
	
	/**
	 * Gets the screen height of the activity
	 * @return Screen Height
	 */
	public int getScreenHeight()
	{
		return mDM.heightPixels;
	}

	/**
	 * Gets the screen density of the activity
	 * @return Screen Density
	 */
	public float getScreenDensity()
	{
		return mDM.density;
	}
}
