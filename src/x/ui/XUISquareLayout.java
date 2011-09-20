package x.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Square layout class to create layouts that adjust their width/height to achieve the 1:1 scale ratio.
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
	 * @param context
	 */
	public XUISquareLayout(Context context)
	{
		super(context);
	}
	
	/**
	 * Default Constructor
	 * @param context
	 * @param attrs
	 */
	public XUISquareLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
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