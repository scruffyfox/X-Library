/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 *  @brief Parallex view for {@link XUIParallexScrollView}
 * 
 * 	Child view params
 * 	@param x:layout_x The x coordinate of the view relative to the screen width 480px
 * 	@param x:layout_y The y coordinate of the view relative to the screen height 800px
 * 	@param x:layout_float The base position of the view. Overrides layout_x and layout_y.
 *  @param x:scroll_direction The scrolling direction of the view
 *  @param x:scroll_speed The scroll speed of the view
 * 
 *	Example XML
 *	@code
 *	<x.ui.XUIParallexView
 *		android:layout_width="wrap_content"
 *		android:layout_height="wrap_content"
 *		android:background="@drawable/background_image"
 *	>
 *		<TextView
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content"
 *			x:layout_x="0dp"
 *			x:layout_y="0dp"
 *			x:layout_float="right"
 *			x:scroll_direction="forward"
 *			x:scroll_speed="4dp"
 *		/>
 *	</x.ui.XUIParallexView>
 *	@endcode
 */
public class XUIParallexView extends XUIAbsoluteLayout
{	
	public static final int SCROLL_DIRECTION_FORWARD = 0x01;
	public static final int SCROLL_DIRECTION_BACKWARD = 0x02;
		
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIParallexView(Context context)
	{
		super(context);
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUIParallexView(Context context, AttributeSet attrs)
	{
		super(context, attrs);				
	}
	
	/**
	 * This method generates default layout params for child views
	 * @return The new layout params
	 */
	@Override protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams()
	{	
		return (XUIParallexView.LayoutParams)super.generateDefaultLayoutParams();
	}	

	/**
	 * This method generates default layout params for child views
	 * @param attrs The attribute set to use when generating the params
	 * @return The new layout params
	 */
	@Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new XUIParallexView.LayoutParams(getContext(), attrs);
	}
	
	/**
	 * Is called when the view is being layed out
	 * @param changed If the view has changed or not
	 * @param l The left coordinate
	 * @param t The top coordinate
	 * @param r The right coordinate
	 * @param b The bottom coordinate
	 */
	@Override protected void onLayout(boolean changed, int l, int t, int r, int b)
	{			
		super.onLayout(changed, l, t, r, b);				
	}
	
	/**
	 * Per-child layout information associated with AbsoluteLayout. See
	 * {@link android.R.styleable#AbsoluteLayout_Layout Absolute Layout
	 * Attributes} for a list of all child view attributes that this class
	 * supports.
	 */
	public static class LayoutParams extends XUIAbsoluteLayout.LayoutParams
	{
		/**
		 * The scroll direction of the view. SCROLL_DIRECTION_FORWARD, SCROLL_DIRECTION_BACKWARD
		 */
		public int scroll_direction;
		
		/**
		 * The speed of the scroll for the view in PX.
		 */
		public float scroll_speed;
		
		/**
		 * Creates a new set of layout parameters with the specified width,
		 * height and location.
		 * 
		 * @param width The width, either {@link #MATCH_PARENT}, {@link #WRAP_CONTENT} or a fixed size in pixels
		 * @param height The height, either {@link #MATCH_PARENT}, {@link #WRAP_CONTENT} or a fixed size in pixels
		 * @param x The X location of the child
		 * @param y The Y location of the child
		 */
		public LayoutParams(int width, int height, int x, int y)
		{
			super(width, height, x, y);
			
			scroll_direction = SCROLL_DIRECTION_FORWARD;
			scroll_speed = 0.0f;
		}

		/**
		 * Creates a new set of layout parameters. The values are extracted from
		 * the supplied attributes set and context. The XML attributes mapped to
		 * this set of layout parameters are:
		 * <ul>
		 * <li><code>scroll_direction</code>: the scroll direction, forward | backward</li>
		 * <li><code>scroll_speed</code>: the scroll speed of the view</li>
		 * </ul>
		 * 
		 * @param c The application environment
		 * @param attrs The set of attributes fom which to extract the layout  parameters values
		 */
		public LayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);
			
			TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.XUIParallexView_Layout);
			
			scroll_direction = a.getInt(R.styleable.XUIParallexView_Layout_scroll_direction, SCROLL_DIRECTION_FORWARD);
			scroll_speed = a.getDimensionPixelOffset(R.styleable.XUIParallexView_Layout_scroll_speed, 0);						
			
			a.recycle();
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams source)
		{
			super(source);
		}
	}
}