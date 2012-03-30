/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @brief Square layout class to create layouts that adjust its width/height to achieve the 1:1 scale ratio.
 * 
 * @code
 * <x.ui.XUISquareLayout
 * 	android:layout_width="fill_parent"
 * 	android:layout_height="20dp"
 * >
 * 	<!-- content -->
 * </x.ui.XUISquareLayout>
 * @endcode
 */
public class XUISquareLayout extends LinearLayout
{
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUISquareLayout(Context context)
	{
		super(context);
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUISquareLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * Measures the view and forces it to be square
	 * @param widthMeasureSpec The width spec to use when measuring
	 * @param heightMeasureSpec The height spec to use when measuring
	 */
	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{	
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int mScale = 1;
				
		if (width < (int) (mScale * height + 0.5))
		{
			width = (int) (mScale * height + 0.5);
		}
		else
		{
			height = (int) (width / mScale + 0.5);
		}
		
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}
}