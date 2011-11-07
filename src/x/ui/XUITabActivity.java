/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.app.ActivityGroup;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

/**
 * @brief The tab activity to be used when using XUITabHost and XUITab
 */
public class XUITabActivity extends ActivityGroup
{
	private OnBackPressedListener onBackPressedListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		onBackPressedListener = new OnBackPressedListener()
		{			
			public boolean onBackPressed()
			{		
				return false;
			}
		};
		
		super.onCreate(savedInstanceState);				
	}

	/**
	 * Sets the OnBackPressedlistener
	 * @param listener The new OnBackPressedListener
	 */
	public void setOnBackPressedListener(OnBackPressedListener listener)
	{		
		this.onBackPressedListener = listener;			
	}
	
	@Override
	public void onBackPressed()
	{
		if (!this.onBackPressedListener.onBackPressed())
		{
			super.onBackPressed();
		}
	}	
	
	/**
	 * @brief The back button press listener
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

