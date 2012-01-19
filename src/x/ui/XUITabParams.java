/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @brief The Param class to be used with XUITab to set the tab settings
 */
public class XUITabParams
{
	/**
	 * The text for the tab
	 */
	public Option<String> tabText = new Option("", "");
	
	/**
	 * The text size in the tab in pixels
	 */
	public int tabTextSize = 14;
	
	/**
	 * The text color of the tab
	 */
	public Option<Integer> tabTextColor = new Option(0xff000000, 0xff000000);
	
	/**
	 * The icon of the tab. This is a reference to a drawable res
	 */
	public Option<Integer> tabIcon = new Option(-1, -1);
	
	/**
	 * The background drawable for the tab
	 */
	public Option<Drawable> tabBackground = new Option(null, null);
	
	/**
	 * The intent to load when the tab is selected
	 */
	public Intent intent = null;	
	
	/**
	 * The gravity of the text
	 */
	public int gravity = 0;
	
	/**
	 * The orientation of the text to icon
	 */
	public int layoutOrientation = LinearLayout.HORIZONTAL;
	
	/**
	 * The layout params for the tab
	 */
	public RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	
	/**
	 * The layout params for the icon
	 */
	public LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 	
	
	/**
	 * Default constructor
	 */
	public XUITabParams()
	{
		 
	}
	
	/**
	 * Default constructor
	 * @param params The params to copy from
	 */
	public XUITabParams(XUITabParams params)
	{
		this.tabText = params.tabText;
		this.tabTextColor = params.tabTextColor;
		this.tabIcon = params.tabIcon;
		this.tabBackground = params.tabBackground;
		this.intent = params.intent;			
		this.gravity = params.gravity;		
		this.layoutParams = params.layoutParams;		
		this.iconLayoutParams = params.iconLayoutParams;
	}
	
	/**
	 * @breif Pseudoclass for selected/deslsect types in the tab
	 * @param <E> Type for the option
	 */ 
	public static class Option<E>
	{
		/**
		 * The selected state
		 */
		public E selected;
		
		/**
		 * The deselected state
		 */
		public E deselected;		
		
		/**
		 * Default constructor
		 * @param data The value to set to both selected and deselected
		 */
		public Option(E data)
		{
			this.selected = data;
		}
		
		/**
		 * Default constructor
		 * @param selected The value to set to the selected state
		 * @param deselected The value to set to the deselected state
		 */
		public Option(E selected, E deselected)
		{
			this.selected = selected;
			this.deselected = deselected;
		}
	}
}
