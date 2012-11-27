/**
 * @brief x util is the utility library which includes the method extentions for common data types
 *
 * @author Callum Taylor
**/
package x.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.view.ViewGroup;

/**
 * @brief Utilities for doing operations on Views
 */
public class ViewUtils
{
	private static HashMap<Integer, String> mIdValues;
	private static void getIdValues(Class R)
	{
		mIdValues = new HashMap<Integer, String>();
		for (Field f : R.getFields())
		{
			try
			{
				mIdValues.put((Integer)R.getField(f.getName()).get(Integer.class), f.getName());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Dumps the hierarchy of a view
	 * @param R The ID reference of your project, must be R.id.class
	 * @param depth The initial tab depth (usually 0)
	 * @param parent The root view
	 * @return A line seperated, tabbed structure of the view's hierarchy
	 */
	public static String dump(Class R, int depth, View parent)
	{
		if (mIdValues == null)
		{
			getIdValues(R);
		}

		String retStr = "";
		retStr += "\n" + (depth == 0 ? "" : (StringUtils.padTo("", depth, "\t"))) + parent.toString();

		if (mIdValues.get(parent.getId()) != null)
		{
			retStr += " id: R.id." + mIdValues.get(parent.getId());
		}

		if (parent instanceof ViewGroup)
		{
			int childCount = ((ViewGroup)parent).getChildCount();

			for (int index = 0; index < childCount; index++)
			{
				retStr += dump(R, depth + 1, ((ViewGroup)parent).getChildAt(index));
			}
		}

		return retStr;
	}

	/**
	 * Gets all views of a parent that match an instance (recursive)
	 * @param parent The parent view
	 * @param instance The instance to check
	 * @return An array of views
	 */
	public static ArrayList<View> getAllChildrenByInstance(ViewGroup parent, Class... instance)
	{
		ArrayList<View> views = new ArrayList<View>();
		int childCount = parent.getChildCount();

		for (int childIndex = 0; childIndex < childCount; childIndex++)
		{
			View child = parent.getChildAt(childIndex);

			if (child instanceof ViewGroup)
			{
				views.addAll(getAllChildrenByInstance((ViewGroup)child, instance));
			}
			else
			{
				for (Class c : instance)
				{
					if (child.getClass() == c)
					{
						views.add(child);
					}
				}
			}
		}

		return views;
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
