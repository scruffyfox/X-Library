/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

/**
 * @brief The tab activity to be used when using XUITabHost and XUITab
 * 
 * When overriding onBackPress to detect when the user is exiting the app for example,
 * you must override the back press method in each of your sub views and call the parent
 * back press. For example
 * @code
 * @Override public void onBackPressed()
 *	{
 *		try
 *		{
 *			getParent().onBackPressed();
 *		}
 *		catch (Exception e) 
 *		{
 *			super.onBackPressed();
 *		}
 *	}
 * @endcode
 * 
 * Then you handle the back button press inside your XUITabActivity class either by overriding the onBackPressed() method
 * or the OnBackPressedListener interface.
 */
public class XUITabActivity extends ActivityGroup
{
	private OnBackPressedListener mOnBackPressedListener;
	
	/**
	 * Default constructor
	 * @param savedInstanceStated Passed bundle on screen rotation and when the Activity is created
	 */
	@Override protected void onCreate(Bundle savedInstanceState)
	{	
		mOnBackPressedListener = new OnBackPressedListener()
		{			
			public boolean onBackPressed()
			{		
				return false;
			}
		};
		
		super.onCreate(savedInstanceState);				
	}

	/**
	 * Sets the current selected tab
	 * @param index The index of the tab
	 * @throws IllegalStateException if index < 0 || index > tabCount
	 */
	public void setCurrentTab(int index)
	{
		XUITabHost tabHost = getTabHost();
		
		if (tabHost == null) return;
		if (index < 0 || index > tabHost.getTabCount() - 1)
		{
			throw new IllegalStateException("Tab index out of bounds");
		}
		
		tabHost.selectTab(index);
	}
	
	/**
	 * Gets the tab host from the current activity
	 * @return The tab host if it exists, null if not
	 */
	public XUITabHost getTabHost()
	{
		ViewGroup root = (ViewGroup)getWindow().getDecorView();
		return recursiveCheck(root);
	}
	
	/**
	 * Recursively checks the view tree for the tab host
	 * @param parent The parent to check
	 * @return The tab host if found, null if not
	 */
	private XUITabHost recursiveCheck(ViewGroup parent)
	{
		int childSize = parent.getChildCount();
		for (int index = 0; index < childSize; index++)
		{			
			if (parent.getChildAt(index) instanceof XUITabHost)
			{
				return (XUITabHost)parent.getChildAt(index);
			}
			else if (parent.getChildAt(index) instanceof ViewGroup)
			{
				XUITabHost v = recursiveCheck((ViewGroup)parent.getChildAt(index));
				if (v != null)
				{
					return v;
				}
			}			
		}
		
		return null;
	}
	
	/**
	 * Sets the OnBackPressedlistener
	 * @param listener The new OnBackPressedListener
	 */
	public void setOnBackPressedListener(OnBackPressedListener listener)
	{		
		this.mOnBackPressedListener = listener;			
	}

	/**
	 * Called when the back button is pressed 
	 */
	@Override public void onBackPressed()
	{
		if (!this.mOnBackPressedListener.onBackPressed())
		{
			super.onBackPressed();
		}
	}	
	
	/**
	 * @brief The back button press listener. This is used due to the nature of the back button
	 * when using tab hosts, it does not register the back button so we can use this listener
	 * to intercept the back button instead.
	 */
	public interface OnBackPressedListener 
	{
		/**
		 * Called when the back button is pressed in the tab view
		 * Useful for closing things that have been launched from within the 
		 * tab's activity IE a search dropdown
		 * @return true if the function has been handled, false it not
		 */
		public abstract boolean onBackPressed();
	};
}

