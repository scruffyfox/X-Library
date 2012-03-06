/**
 * @brief x util is the utility library which includes the method extentions for common data types
 * 
 * @author Callum Taylor
**/
package x.util;

import x.type.ItemList;
import android.view.View;
import android.view.ViewGroup;

/**
 * Utilities for doing operations on Views
 */
public class ViewUtils
{
	/**
	 * Gets all views of a parent that match an instance (recursive)
	 * @param parent The parent view 
	 * @param instance The instance to check
	 * @return An array of views, or null
	 */
	public static View[] getAllChildrenByInstance(ViewGroup parent, Class instance)
	{
		ItemList<View> views = new ItemList<View>();
		int childCount = parent.getChildCount();
		
		for (int childIndex = 0; childIndex < childCount; childIndex++)
		{
			View child = parent.getChildAt(childIndex);
			
			if (child instanceof ViewGroup)
			{
				views.add(getAllChildrenByInstance((ViewGroup)child, instance));
			}
			else
			{
				if (child.getClass() == instance)
				{
					views.add(child);
				}
			}
		}
		
		return views.size() > 0 ? (View[])views.toArray(new View[views.size()]) : null;
	}
	
	/**
	 * Gets the first child it finds in a parent matched from an instance (recursive)
	 * @param parent The parent view
	 * @param instance The instance to check
	 * @return The found view, or null
	 */
	public static View getFirstChildByInstance(ViewGroup parent, Class instance)
	{		
		View retView = null;
		int childCount = parent.getChildCount();
		
		for (int childIndex = 0; childIndex < childCount; childIndex++)
		{
			View child = parent.getChildAt(childIndex);
			
			if (child.getClass() == instance)
			{
				return child;
			}
			
			if (child instanceof ViewGroup)
			{
				View v = getFirstChildByInstance((ViewGroup)child, instance);
				
				if (v != null)
				{
					return v;
				}
			}									
		}
		
		return retView;
	}
}
