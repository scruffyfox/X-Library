/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * @brief This class is used to fetch the user's current location
 * using either cached location (if available) or requests it using
 * the LocationListener if not
 */
public class LocationHelper implements LocationListener
{
	/**
	 * Message ID: Used when a provider has been disabled
	 */
	public static final int MESSAGE_PROVIDER_DISABLED = 0;
	/**
	 * Message ID: Used when the search has timed out
	 */
	public static final int MESSAGE_TIMEOUT = 1;
	/**
	 * Message ID: Used when the user cancels the request
	 */
	public static final int MESSAGE_FORCED_CANCEL = 2;
	
	private Context mContext;	
	private LocationManager mLocationManager;
	private LocationListener mLocationListener = this;
	private long mTimeout = 0;
	private LocationResponse mCallback = null;	
	private Handler mTimeoutHandler = new Handler();
	
	private Runnable mTimeoutRunnable = new Runnable()
	{
		public void run()
		{						
			if (mCallback != null)
			{
				mCallback.onLocationFailed("Timeout", MESSAGE_TIMEOUT);
				mCallback.onTimeout();
			}
			
			mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
			mLocationManager.removeUpdates(mLocationListener);					
		};
	};
	
	/**
	 * Default Constructor
	 * @param context The application/activity context to use
	 */
	public LocationHelper(Context context)
	{
		mContext = context;
		mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);		
	}
	
	/**
	 * Cancels the request
	 */
	public void cancelRequest()
	{								
		mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
		mLocationManager.removeUpdates(this);		 
		
		if (mCallback != null)
		{
			mCallback.onLocationFailed("Canceld", MESSAGE_FORCED_CANCEL);
		}		
	}
	
	/**
	 * Fetches the location
	 * @param timeout The time out for the request in MS
	 * @param callback The callback for the request
	 */
	public void fetchLocation(long timeout, LocationResponse callback)
	{
		mCallback = callback;		
		mCallback.onRequest();
		Location userLocation = null;		
		
		//	Try to get the cache location first
		userLocation = getCachedLocation();
		if (userLocation == null)
		{
			Criteria c = new Criteria();
		    c.setAccuracy(Criteria.ACCURACY_FINE);
		    String p = mLocationManager.getBestProvider(c, true);
		    
		    Criteria c2 = new Criteria();
		    c2.setAccuracy(Criteria.ACCURACY_COARSE);
		    String p2 = mLocationManager.getBestProvider(c2, true);
		   
		    if (timeout > 0)
		    {
		    	mTimeoutHandler.postDelayed(mTimeoutRunnable, timeout);
		    }
		    
		    if (p == null && p2 == null)
		    {
		    	return;
		    }		    		    
		    		   
		    if (p != null)
		    {
		    	mLocationManager.requestLocationUpdates(p, 0, 0, this);
		    }
		    
		    if (p2 != null)
		    {
		    	mLocationManager.requestLocationUpdates(p2, 0, 0, this);
		    }
		}
		else
		{
			if (callback != null)
			{
				callback.onLocationAquired(userLocation);
			}
		}				
	}
	
	/**
	 * Gets the cached location
 	 * @return The location, or null if one was not retrieved 
	 */
	public Location getCachedLocation() 
	{		
	    List<String> providers = mLocationManager.getProviders(true);	
	    Location l = null;
	     
	    for (int i = providers.size() - 1; i >= 0; i--) 
	    {
	        l = mLocationManager.getLastKnownLocation(providers.get(i));
	        if (l != null) break;
	    }	    	    	    	   
	    
	    return l;
	}
		
	@Override public void onLocationChanged(Location location)
	{
		if (location != null)
		{				
			mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
			mLocationManager.removeUpdates(this);		
			
			if (mCallback != null)
			{
				mCallback.onLocationAquired(location);
			}
		}
	}

	@Override public void onProviderDisabled(String provider)
	{
		mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
		mLocationManager.removeUpdates(this);		
		
		if (mCallback != null)
		{
			mCallback.onLocationFailed("Location provider disabled", MESSAGE_PROVIDER_DISABLED);
		}
	}

	@Override public void onProviderEnabled(String provider)
	{
		
	}

	@Override public void onStatusChanged(String provider, int status, Bundle extras)
	{
		
	}
	
	/**
	 * @brief The location response for the callback of the
	 * LocationHelper
	 */
	public static abstract class LocationResponse
	{
		/**
		 * Called when the request was initiated
		 */
		public void onRequest(){}
		
		/**
		 * Called when the location was aquired
		 * @param l The location recieved
		 */
		public abstract void onLocationAquired(Location l);
		
		/**
		 * Called when the request timed out
		 */
		public void onTimeout(){}
		
		/**
		 * Called when the request failed
		 * @param message The message
		 * @param messageId The ID of the message
		 */
		public void onLocationFailed(String message, int messageId){}
	}
}
