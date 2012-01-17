package x.ui;

import x.lib.Debug;
import x.util.BitmapUtils;
import android.content.Context;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.util.AttributeSet;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.*;

/**
 * This view is an extention of {@link ScrollView} which allows parallex scrolling of views.
 * A parallex scroller view is a view with many subviews that move at different speeds and
 * directions to the direction/speed being scrolled.
 * 
 * Only one direct child is allowed, best to use LinearLayout or XUILinearLayout for conventional
 * reasons.
 * 
 * Example XML
 * @code
 *	<x.ui.XUIParallexScrollView
 *  	android:layout_width="fill_parent"
 *    	android:layout_height="fill_parent"    	
 *  >
 *  	<LinearLayout
 *	    	android:layout_width="fill_parent"
 *	    	android:layout_height="fill_parent"
 *	    	android:orientation="vertical"	
 *    	>    		
 *    		<x.ui.XUIParallexView
 *    			android:layout_width="fill_parent"
 *    			android:layout_height="800dp"
 *    			android:background="@drawable/ea_wood"
 *    		>
 *    			<ImageView 
 *    				android:layout_width="wrap_content"
 *    				android:layout_height="wrap_content"
 *     				android:src="@drawable/ea_text_1"
 *    				x:layout_x="0px"
 *    				x:layout_y="0px"
 *    				x:scroll_direction="forward"
 *    				x:scroll_speed="0px"	
 *    			/>    			
 *    			<ImageView 
 *    				android:layout_width="wrap_content"
 *    				android:layout_height="wrap_content"
 *    				android:src="@drawable/ea_mouse"
 *    				x:layout_x="0px"
 *    				x:layout_y="0px"
 *    				x:scroll_direction="backward"
 *    				x:scroll_speed="1px"	
 *    				x:layout_gravity="right"
 *    			/>
 *    		</x.ui.XUIParallexView>     		
 *    	</LinearLayout>    	
 * 	</x.ui.XUIParallexScrollView>
 * @endcode
 */
public class XUIParallexScrollView extends ScrollView
{
	private Context mContext;
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUIParallexScrollView(Context context)
	{
		super(context);		
		mContext = context;
	}
	
	/**
	 * Default Constructor
	 * @param context
	 * @param attrs
	 */
	public XUIParallexScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}
	
	/**
	 * Adds a parallex view to the scroller
	 * @param view The parallex view to add
	 */
	public void addParallexView(XUIParallexView view)
	{
		addParallexView(view, generateDefaultLayoutParams());
	}
	
	/**
	 * Adds a parallex view to the scroller
	 * @param view The parallex view to add
	 * @param params The layout params to apply to the view
	 */
	public void addParallexView(XUIParallexView view, LayoutParams params)
	{
		if (getChildAt(0) == null || getChildAt(1) != null)
		{
			throw new IndexOutOfBoundsException("XUIParallexScrollView must have one direct child");			
		}
		
		FrameLayout container = new FrameLayout(mContext);
		
		XUIParallexView background = new XUIParallexView(mContext);
				
		ImageView backgroundView = new ImageView(mContext);		
		backgroundView.setBackgroundDrawable(view.getBackground());			
		
		XUIParallexView.LayoutParams backgroundLp = new XUIParallexView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0, 0);
		
		background.addView(backgroundView, backgroundLp);
		
		container.addView(background);
		container.addView(view);
		
		((ViewGroup)getChildAt(0)).addView(container, params);
	}
	
	/**
	 * Initializes the view
	 */
	private void init()
	{
		setScrollbarFadingEnabled(false);
		setFadingEdgeLength(0);
		setVerticalFadingEdgeEnabled(false);
		setHorizontalFadingEdgeEnabled(false);
	}
	
	@Override protected void onFinishInflate()
	{		
		super.onFinishInflate();
		
		//	We need to get the children from XML and put them into our own format from
		//	XUIParallexView
		//		View ...
		//
		//	To
		//
		//	XUIParallexView
		//		FrameLayout
		//			XUIParallexView
		//			View ...
		ViewGroup child = ((ViewGroup)getChildAt(0));
		int childCount = child.getChildCount(); 
		XUIParallexView[] views = new XUIParallexView[childCount];
		
		for (int childIndex = 0; childIndex < childCount; childIndex++)
		{
			views[childIndex] = (XUIParallexView)child.getChildAt(childIndex);			
		}
		
		child.removeAllViews();
		
		for (int childIndex = 0; childIndex < childCount; childIndex++)
		{			
			ViewGroup.LayoutParams p = (ViewGroup.LayoutParams)views[childIndex].getLayoutParams();			
			this.addParallexView(views[childIndex], new LayoutParams(p.width, p.height));
		}
		
		views = null;		
		init();
	}
	
	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		
		//if (changed)
		{
			//	Loop through the children and get their layout params
			ViewGroup child = ((ViewGroup)getChildAt(0));
			int childCount = child.getChildCount();
			
			int topOffset = 0;
			for (int childIndex = 0; childIndex < childCount; childIndex++)
			{
				FrameLayout c = (FrameLayout)child.getChildAt(childIndex);							
				XUIParallexView subChild = (XUIParallexView)c.getChildAt(1);															
							
				//	Layout the children
				int subChildCount = subChild.getChildCount();				
				for (int index = 0; index < subChildCount; index++)
				{	
					View finalView = subChild.getChildAt(index);
					Debug.out(finalView.getLayoutParams().toString());
					XUIParallexView.LayoutParams lp = (XUIParallexView.LayoutParams)finalView.getLayoutParams();
					int t = 0;
					int marginLeft = lp.leftMargin;
					int marginRight = lp.rightMargin;
					int marginTop = lp.topMargin;
					int marginBottom = lp.bottomMargin;
					
					switch (lp.scroll_direction)
					{
						case XUIParallexView.SCROLL_DIRECTION_FORWARD:
						{							
							t = (c.getTop() * (int)lp.scroll_speed) + lp.y;		
							break;
						}
						
						case XUIParallexView.SCROLL_DIRECTION_BACKWARD:
						{
							t = -(c.getTop() * ((int)lp.scroll_speed + 1)) + lp.y;
							break;
						}
					}
					
					finalView.layout(finalView.getLeft() + marginLeft, t + marginTop, finalView.getRight() + marginLeft, t + finalView.getHeight() + marginTop);
				}
			}
		}
	}
	
	@Override protected void onScrollChanged(int left, int top, int oldl, int oldt)
	{
		int distanceY = top - oldt;
		
		//	Loop through the children and get their layout params
		ViewGroup child = ((ViewGroup)getChildAt(0));
		int childCount = child.getChildCount();
		
		for (int childIndex = 0; childIndex < childCount; childIndex++)
		{
			FrameLayout c = (FrameLayout)child.getChildAt(childIndex);							
			XUIParallexView subChild = (XUIParallexView)c.getChildAt(1);		
			
			//	Layout the children
			int subChildCount = subChild.getChildCount();				
			for (int index = 0; index < subChildCount; index++)
			{
				View finalView = subChild.getChildAt(index);
				XUIParallexView.LayoutParams lp = (XUIParallexView.LayoutParams)finalView.getLayoutParams();
				
				float childScrollSpeed = lp.scroll_speed;
				int t = 0, l = 0, r = 0, b = 0;	
				
				int marginLeft = lp.leftMargin;
				int marginRight = lp.rightMargin;
				int marginTop = lp.topMargin;
				int marginBottom = lp.bottomMargin;
															
				switch (lp.scroll_direction)
				{
					case XUIParallexView.SCROLL_DIRECTION_FORWARD:
					{							
						t = (int)((c.getTop() - top) * childScrollSpeed);	
						l = finalView.getLeft();
						r = finalView.getRight();
						b = t + finalView.getHeight();	
						break;
					}
					
					case XUIParallexView.SCROLL_DIRECTION_BACKWARD:
					{
						t = -(int)(((c.getTop() - top) * (childScrollSpeed + 1)));	
						l = finalView.getLeft();
						r = finalView.getRight();
						b = t + finalView.getHeight();	
						break;
					}
				}
				
				finalView.layout(l + marginLeft, t + lp.y + marginTop, r + marginLeft, b + lp.y + marginTop);
			}
		}
		
		super.onScrollChanged(left, top, oldl, oldt);
	}
}
