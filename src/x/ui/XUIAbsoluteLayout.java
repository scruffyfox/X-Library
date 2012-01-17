package x.ui;

import x.lib.Debug;
import x.lib.Dimension;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews.RemoteView;

/**
 * A layout that lets you specify exact locations (x/y coordinates) of its
 * children. Absolute layouts are less flexible and harder to maintain than
 * other types of layouts without absolute positioning.
 * 
 * Using this view is highly discouraged as the different screen sized devices
 * make it hard to maintain. If you are going to use this view, work out the 
 * relative size using the {@link Dimension} class.
 * 
 * Re-added to library because I needed to use it without risk of older systems
 * not supporting it.
 * 
 * @code
 * 	<x.ui.XUIAbsoluteLayout
 * 		android:layout_width="fill_parent"
 * 		android:layout_height="fill_parent"
 * 	>
 * 		<ImageView
 * 			android:layout_width="wrap_content"
 * 			android:layout_height="wrap_content"
 * 			android:layout_x="100dp"
 * 			android:layout_y="100dp"
 * 		/>
 * 	</x.ui.XUIAbsoluteLayout>
 * @endcode
 */ 
public class XUIAbsoluteLayout extends ViewGroup
{
	private int mPaddingLeft, mPaddingRight, mPaddingTop, mPaddingBottom;
	
	public XUIAbsoluteLayout(Context context)
	{
		super(context);
	}

	public XUIAbsoluteLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public XUIAbsoluteLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);		
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int count = getChildCount();

		int maxHeight = 0;
		int maxWidth = 0;

		// Find out how big everyone wants to be
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		// Find rightmost and bottom-most child
		for (int childIndex = 0; childIndex < count; childIndex++)
		{
			View child = getChildAt(childIndex);
			if (child.getVisibility() != GONE)
			{
				int childRight;
				int childBottom;

				XUIAbsoluteLayout.LayoutParams lp = (XUIAbsoluteLayout.LayoutParams)child.getLayoutParams();

				childRight = lp.x + child.getMeasuredWidth();
				childBottom = lp.y + child.getMeasuredHeight();

				maxWidth = Math.max(maxWidth, childRight);
				maxHeight = Math.max(maxHeight, childBottom);
			}
		}

		// Account for padding too
		maxWidth += mPaddingLeft + mPaddingRight;
		maxHeight += mPaddingTop + mPaddingBottom;

		// Check against minimum height and width
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
	}

	/**
	 * Returns a set of layout parameters with a width of
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}, a height of
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and with the
	 * coordinates (0, 0).
	 */
	@Override protected ViewGroup.LayoutParams generateDefaultLayoutParams()
	{		
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
	}

	@Override protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int count = getChildCount();

		int viewWidth = getMeasuredWidth();
		int viewHeight = getMeasuredHeight();
		for (int childIndex = 0; childIndex < count; childIndex++)
		{
			View child = getChildAt(childIndex);
			if (child.getVisibility() != GONE)
			{
				XUIAbsoluteLayout.LayoutParams lp = (XUIAbsoluteLayout.LayoutParams)child.getLayoutParams();																																			
				
				//	Center gravity
				if ((Gravity.CENTER & lp.layout_gravity) == Gravity.CENTER)
				{	
					int x = (viewWidth - child.getWidth()) / 2;
					int y = (viewHeight - child.getHeight()) / 2;
					
					if ((Gravity.CENTER_HORIZONTAL & lp.layout_gravity) == Gravity.CENTER_HORIZONTAL)
					{
						lp.x = (viewWidth - child.getWidth()) / 2;
					}
					
					if ((Gravity.CENTER_VERTICAL & lp.layout_gravity) == Gravity.CENTER_VERTICAL)
					{
						lp.y = (viewHeight - child.getHeight()) / 2;
					}				
				}	
				
				//	Left gravity
				if ((Gravity.LEFT & lp.layout_gravity) == Gravity.LEFT)
				{												
					lp.x = 0;
				}
				
				//	Right gravity
				if ((Gravity.RIGHT & lp.layout_gravity) == Gravity.RIGHT)
				{
					lp.x = viewWidth - child.getWidth();
				}
				
				//	Top gravity
				if ((Gravity.TOP & lp.layout_gravity) == Gravity.TOP)
				{
					lp.y = 0;
				}
				
				//	Bottom gravity
				if ((Gravity.BOTTOM & lp.layout_gravity) == Gravity.BOTTOM)
				{
					lp.y = viewHeight - child.getHeight();
				}
				
				int left = mPaddingLeft + lp.x;
				int top = mPaddingTop + lp.y;
																
				int leftMargin = lp.leftMargin - lp.rightMargin;
				int topMargin = lp.topMargin - lp.bottomMargin;
				
				child.layout(left + leftMargin, top + topMargin, left + child.getMeasuredWidth() + leftMargin, top + child.getMeasuredHeight() + topMargin);
			}
		}
	}

	@Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new XUIAbsoluteLayout.LayoutParams(getContext(), attrs);
	}

	// Override to allow type-checking of LayoutParams.
	@Override protected boolean checkLayoutParams(ViewGroup.LayoutParams p)
	{
		return p instanceof XUIAbsoluteLayout.LayoutParams;
	}

	@Override protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p)
	{
		return new LayoutParams(p);
	}

	/**
	 * Per-child layout information associated with AbsoluteLayout. See
	 * {@link android.R.styleable#AbsoluteLayout_Layout Absolute Layout
	 * Attributes} for a list of all child view attributes that this class
	 * supports.
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams
	{
		/**
		 * The horizontal, or X, location of the child within the view group.
		 */
		public int x;
		
		/**
		 * The vertical, or Y, location of the child within the view group.
		 */
		public int y;
		
		/**
		 * The gravity of the view relative to the container
		 */
		public int layout_gravity;
		
		/**
		 * Layout margin
		 */
		public int leftMargin, rightMargin, topMargin, bottomMargin;

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
			super(width, height);
			this.x = x;
			this.y = y;
			layout_gravity = Gravity.NO_GRAVITY;
			leftMargin = rightMargin = topMargin = bottomMargin = 0;
		}

		/**
		 * Creates a new set of layout parameters. The values are extracted from
		 * the supplied attributes set and context. The XML attributes mapped to
		 * this set of layout parameters are:
		 * <ul>
		 * <li><code>layout_x</code>: the X location of the child</li>
		 * <li><code>layout_y</code>: the Y location of the child</li>
		 * <li><code>layout_marginLeft</code>: the left margin of the child</li>
		 * <li><code>layout_marginRight</code>: the right margin of the child</li>
		 * <li><code>layout_marginTop</code>: the top margin of the child</li>
		 * <li><code>layout_marginBottom</code>: the bottom margin of the child</li>
		 * <li><code>layout_gravity</code>: the layout gravity of the child. Ignores X/Y coordinates. Use margin to control the position relative to the gravity.</li>
		 * <li>All the XML attributes from
		 * {@link android.view.ViewGroup.LayoutParams}</li>
		 * </ul>
		 * 
		 * @param c The application environment
		 * @param attrs The set of attributes fom which to extract the layout  parameters values
		 */
		public LayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);
			
			TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.XUIAbsoluteLayout_Layout);
			x = a.getDimensionPixelOffset(R.styleable.XUIAbsoluteLayout_Layout_layout_x, 0);
			y = a.getDimensionPixelOffset(R.styleable.XUIAbsoluteLayout_Layout_layout_y, 0);
			leftMargin = a.getDimensionPixelOffset(R.styleable.XUIAbsoluteLayout_Layout_layout_marginLeft, 0);
			rightMargin = a.getDimensionPixelOffset(R.styleable.XUIAbsoluteLayout_Layout_layout_marginRight, 0);
			topMargin = a.getDimensionPixelOffset(R.styleable.XUIAbsoluteLayout_Layout_layout_marginTop, 0);
			bottomMargin = a.getDimensionPixelOffset(R.styleable.XUIAbsoluteLayout_Layout_layout_marginBottom, 0);
			layout_gravity = a.getInt(R.styleable.XUIAbsoluteLayout_Layout_layout_gravity, Gravity.NO_GRAVITY);
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
