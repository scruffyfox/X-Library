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
	public Option<String> tabText = new Option("", "");
	public Option<Integer> tabTextColor = new Option(0xff000000, 0xff000000);
	public Option<Integer> tabIcon = new Option(-1, -1);
	public Option<Drawable> tabBackground = new Option(null, null);
	public Intent intent = null;	
	public int gravity = 0;
	public RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
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
		public E selected;
		public E deselected;
		
		public Option(E selected, E deselected)
		{
			this.selected = selected;
			this.deselected = deselected;
		}
	}
}
