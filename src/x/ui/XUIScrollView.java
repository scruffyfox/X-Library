/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * @brief Custom scroll view which allows the setting of over scroll mode in XML (if available)
 * 
 * Example
 * @code
 * <x.ui.XUIScrollView
 * 	android:layout_width="match_parent"
 * 	android:layout_height="match_parent"
 * 	x:allowOverScroll="true|false"
 * />
 * @endcode
 */
public class XUIScrollView extends ScrollView
{	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIScrollView(Context context)
	{
		super(context);	
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUIScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);	
		TypedArray attribues = context.obtainStyledAttributes(attrs, R.styleable.XUIScrollView);
	
		boolean allowOverscroll = attribues.getBoolean(R.styleable.XUIScrollView_allowOverScroll, false);
		if (allowOverscroll)
		{
			try
			{
				Class c = Class.forName("android.widget.ScrollView");
				Method m = c.getMethod("setOverScrollMode", int.class);
				m.invoke(this, 0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}						
		}
	}
}
 