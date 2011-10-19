package x.lib;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Overlay item class that is used in MapViews to display a bubble of information over a point on a map view. Used commonly with XUIMapOverlay
 */
public class IconOverlayItem extends OverlayItem
{
	private Bitmap mDrawable = null;
	private int id = -1;
	
	/**
	 * Default Constructor
	 * @param point The point of the pin
	 * @param title The title of the info bubble
	 * @param snippet The small description of the info bubble
	 */
	public IconOverlayItem(GeoPoint point, String title, String snippet)
	{
		super(point, title, snippet);		
	}
	
	/**
	 * Default Constructor
	 * @param point The point of the pin
	 * @param title The title of the info bubble
	 * @param snippet The small description of the info bubble
	 * @param icon The icon that gets displayed in the bubble
	 */
	public IconOverlayItem(GeoPoint point, String title, String snippet, Drawable icon)
	{
		super(point, title, snippet);	
		
		mDrawable = ((BitmapDrawable)icon).getBitmap();
	}
	
	/**
	 * Default Constructor
	 * @param point The point of the pin
	 * @param title The title of the info bubble
	 * @param snippet The small description of the info bubble
	 * @param icon The icon that gets displayed in the bubble
	 */
	public IconOverlayItem(GeoPoint point, String title, String snippet, Bitmap icon)
	{
		super(point, title, snippet);	
		
		mDrawable = icon;
	}
	
	/**
	 * Default Constructor
	 * @param point The point of the pin
	 * @param title The title of the info bubble
	 * @param snippet The small description of the info bubble
	 * @param icon The icon that gets displayed in the bubble
	 * @param id The unique identifier of the pin
	 */
	public IconOverlayItem(GeoPoint point, String title, String snippet, Drawable icon, int id)
	{
		super(point, title, snippet);	
		
		mDrawable = ((BitmapDrawable)icon).getBitmap();
		this.id = id;
	}

	/**
	 * Default Constructor
	 * @param point The point of the pin
	 * @param title The title of the info bubble
	 * @param snippet The small description of the info bubble
	 * @param icon The icon that gets displayed in the bubble
	 * @param id The unique identifier of the pin
	 */
	public IconOverlayItem(GeoPoint point, String title, String snippet, Bitmap icon, int id)
	{
		super(point, title, snippet);	
		
		mDrawable = icon;
		this.id = id;
	}
	
	/**
	 * Returns the icon of the bubble
	 * @return The icon
	 */
	public Bitmap getDrawable()
	{
		return mDrawable;
	}

	/**
	 * Gets the ID of the bubble
	 * @return The id
	 */
	public int getId()
	{
		return id;
	}
}