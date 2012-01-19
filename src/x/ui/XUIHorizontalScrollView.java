/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.*;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
//import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * @brief Horizontal gallery view for swiping views similar to the iPhone gallery.
 * @brief Horizontal gallery view must have only one direct child when inflating from XML.
 * @added Added generic children, gallery swiper now supports any children, not just image views.
 * @added Ability to set child views to force the size of the scroller (width)
 * 
 * XML Example
 * @code
 * <x.ui.XUIHorizontalScrollView
 *		android:layout_width="fill_parent"
 *		android:layout_height="fill_parent"
 *		android:fadingEdge="none"	
 *		android:scrollbars="none"
 *		android:id="@+id/galleryView"
 *		xui:scrollMode="step"
 *	>							
 *		<LinearLayout
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content"
 *			android:orientation="horizontal"			
 *		>				
 *			<!-- Content here -->				
 *		</LinearLayout>
 *	</x.ui.XUIHorizontalScrollView>
 *  @endcode
 *  
 *  Code Example
 *  @code
 *  for (int imageIndex = fFrom; imageIndex < fTo; imageIndex++)
 *  {	        	
 *	  Bitmap mImage = cacheManager.readImage(hashFileName);		       		
 *	
 *	  image = new ImageView(context);
 *	  image.setLayoutParams(new LayoutParams(dm.widthPixels, (int)Math.round(dm.widthPixels / 1.66)));
 *	  image.setScaleType(ScaleType.FIT_XY);		
 *	  image.setDrawingCacheQuality(ImageView.DRAWING_CACHE_QUALITY_LOW);
 *	
 *	  galleryView.addChildView(image);		
 *  }
 *  @endcode
 *  
 *  @note To force the child to the width of the scroller, set the width of the child to FILL_PARENT or MATCH_PARENT
 *  
 *  @TODO Add remove all views and remove view support
 */
public class XUIHorizontalScrollView extends HorizontalScrollView
{
	/**
	 * Scroll mode of the scroll view
	 * 
	 * SMOOTH: Allows smooth scrolling such as like the standard scroll view
	 * STEP: Allows scrolling such as a gallery view where a fling progresses to the next view
	 * NONE: Disables scrolling
	 * 
	 * Default value: STEP.
	 */
	public static final int SMOOTH = 0x10;
	public static final int STEP = 0x01;
	public static final int NONE = 0x00;
	
	private Context context;		
	private GestureDetector gestureDetector;
	protected int SWIPE_MIN_DISTANCE = 10;
	protected int SWIPE_THRESHOLD_VELOCITY = 20;
	private boolean touchDown = false;
	private int currentChildIndex = 0;
	private int screenWidth;
	private int childCount = 0;
	private boolean canScroll = true;
	private OnViewChangedListener mOnViewChangedLister;
	private int mScrollMode;
	private ScrollView mParentScrollView;
	
	private boolean mScrollingVertically = false;
	private boolean mScrollingHorizontally = false;	
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIHorizontalScrollView(Context context) 
	{
		super(context);
		this.context = context;		
				
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		this.addView(layout, 0);
		
		setFillViewport(true);
		
		init();
	}
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param attributes The attribute set passed via the SAX parser 
	 */	
	public XUIHorizontalScrollView(Context context, AttributeSet attributes) 
	{
		super(context, attributes);
		this.context = context;	
		
		TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.XUIHorizontalScrollView);	
		mScrollMode = attrs.getInt(R.styleable.XUIHorizontalScrollView_scrollMode, STEP);	
		attrs.recycle();		
	}		
	
	/**
	 * Adds an image to the scroller
	 * @param child The view to add
	 */
	public void addChildView(View child)
	{
		((ViewGroup)this.getChildAt(0)).addView(child);
	}
	
	/**
	 * Adds an image to the scroller
	 * @param child The view to add
	 * @param index The index to add the view at
	 */
	public void addChildView(View child, int index)
	{
		((ViewGroup)this.getChildAt(0)).addView(child, index);
	}
	
	/**
	 * Adds a view to the scroller
	 * @param chile The view to add
	 * @param params The params for the new view
	 */
	public void addChildView(View child, android.view.ViewGroup.LayoutParams params)
	{		
		((ViewGroup)this.getChildAt(0)).addView(child, params);
	}
	
	/**
	 * Adds a view to the scroller
	 * @param child The view to add
	 * @param index The index of the view to add
	 * @param params The params for the new view
	 */
	public void addChildView(View child, int index, android.view.ViewGroup.LayoutParams params)
	{
		((ViewGroup)this.getChildAt(0)).addView(child, index, params);
	}
	
	/**
	 * Removes a view at an index
	 * @param view The view to remove
	 */
	public void removeChild(View view)
	{
		((ViewGroup)this.getChildAt(0)).removeView(view);
	}
	
	/**
	 * Removes a view at an index
	 * @param index The index of the view to remove
	 */
	public void removeChildAt(int index)
	{
		((ViewGroup)this.getChildAt(0)).removeViewAt(index);
	}
	
	/**
	 * Removes all the views
	 */
	public void removeAllChildren()
	{		
		((ViewGroup)this.getChildAt(0)).removeAllViews();
	}

	/**
	 * Gets a view from an index
	 * @param index The index of the view
	 * @return The View
	 */
	public View getChildViewAt(int index)
	{
		return (View)((ViewGroup)this.getChildAt(0)).getChildAt(index);
	}
	
	/**
	 * Returns the current view in the view
	 * @return The current view
	 */
	public View getCurrentChildView()
	{
		return getChildViewAt(currentChildIndex);
	}
	
	/**
	 * Gets the current view's index
	 * @return The current view's index
	 */
	public int getCurrentIndex()
	{
		return currentChildIndex;
	}
	
	/**
	 * Forces the gallery view to go to the view at a specific index
	 * This must be called BEFORE onLayout in order to work
	 * @param index The index to go to
	 */
	public void setStartingChildView(int index)
	{
		if (index > getChildViewCount()) 
		{
			throw new IllegalStateException("Index out of bounds of gallery [index: " + index + " size: " + getChildViewCount() +"]");
		}
		
		currentChildIndex = index;        
	}
	
	/**
	 * Scrolls the view to the specified view index
	 * @param index The index to go to
	 */
	public void scrollToChildAt(int index)
	{
		if (index > getChildViewCount()) 
		{
			throw new IllegalStateException("Index out of bounds of gallery [index: " + index + " size: " + getChildViewCount() +"]");
		}
		
		int featureWidth = getMeasuredWidth();
		scrollTo(getChildViewAt(index).getLeft(), 0);
        
		if (currentChildIndex != index)
        {
        	currentChildIndex = index;
        }
        
		if (mOnViewChangedLister != null)
		{
			mOnViewChangedLister.onViewChange(index);                		                   	
		}        
	}
	
	/**
	 * Scrolls the view to the specified view index
	 * @param index The index to go to
	 */
	public void smoothScrollToChildAt(int index)
	{
		if (index > getChildViewCount()) 
		{
			throw new IllegalStateException("Index out of bounds of gallery [index: " + index + " size: " + getChildViewCount() +"]");
		}
		
		int featureWidth = getMeasuredWidth();
		smoothScrollTo(getChildViewAt(index).getLeft(), 0);
       
		if (currentChildIndex != index)
		{
			currentChildIndex = index;
		}
        
		if (mOnViewChangedLister != null)
		{        
			mOnViewChangedLister.onViewChange(index);                		        	                    
		}
	}
	
	/**
	 * Sets the scroll mode of the view
	 * @param scrollMode The new scroll mode
	 */
	public void setScrollMode(int scrollMode)
	{
		this.mScrollMode = scrollMode;
		init();
	}
	
	int prevX = 0;
	private void init()
	{		
		Dimension dm = new Dimension(getContext());
		screenWidth = dm.getScreenWidth();
        				         
		if (mScrollMode == STEP || mScrollMode == SMOOTH)
		{
			gestureDetector = new GestureDetector(new SwipeGestureDetector());
			setOnTouchListener(new View.OnTouchListener() 
			{            
				public boolean onTouch(View v, MotionEvent event) 
				{	            	            	
					if (!canScroll) return true;
					
					//	If the user swipes
					if (gestureDetector.onTouchEvent(event)) 
					{
						return true;
					}                
					else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
					{
						if (mScrollMode == SMOOTH) return true;
	                	
						mScrollingHorizontally = false;
						mScrollingVertically = false;
	                	
						int scrollX = getScrollX();
						int featureWidth = v.getMeasuredWidth();                                                            
						int mActiveFeature = currentChildIndex;            	                    
	                    	                    
						View nextChild = getChildViewAt(getCurrentIndex() + 1);
						View currentChild = getCurrentChildView();
	                    	                    
						int currentChildLeft = 0;
						int nextChildLeft = 0;
	                    
						if (currentChild != null)
						{
							currentChildLeft = currentChild.getLeft();
						}
	                    
						if (nextChild != null)
						{
							nextChildLeft = nextChild.getLeft();
						}
	                    
						if (scrollX < (currentChildLeft - (featureWidth / 2)))
						{                    	                    	
							mActiveFeature -= currentChildIndex - 1 < 0 ? 0 : 1;
						}
						else if (scrollX > (nextChildLeft - (featureWidth / 2)))
						{
							mActiveFeature += currentChildIndex + 1 >= childCount ? 0 : 1;
						}
	                    
						smoothScrollTo(getChildViewAt(mActiveFeature).getLeft(), 0);
	                    
						if (currentChildIndex != mActiveFeature)
						{
							currentChildIndex = mActiveFeature;
						}
	                    	                    
						if (mOnViewChangedLister != null)
						{                    	
							mOnViewChangedLister.onViewChange(currentChildIndex);                    		                    	               	
						}                    
	                    
						return true;
					}
					else
					{
						return false;
					}
				}
			});			
		}
		else
		{
			gestureDetector = null;
			setOnTouchListener(null);
		} 
	}		
	
	/**
	 * Sets the view change listener
	 * @param l The new view change listener
	 */
	public void setOnViewChange(OnViewChangedListener l)
	{
		this.mOnViewChangedLister = l;
	}
	
	/**
	 * Sets if the view can scroll
	 * @param canScroll True if the view can scroll, false if not
	 */
	public void setCanScroll(boolean canScroll)
	{
		this.canScroll = canScroll;
	}
	
	/**
	 * Gets if the view can scroll
	 * @return True if the view can scroll, false if not
	 */
	public boolean canScroll()
	{	
		return canScroll;
	}
	
	/**
	 * Disables the scrolling of the view
	 */
	public void disableScroll()
	{
		canScroll = false;
	}
	
	/**
	 * Enables the scrolling of the view
	 */
	public void enableScroll()
	{
		canScroll = true;
	}		
	
	/**
	 * Gets the view count
	 * @return The view count
	 */
	public int getChildViewCount()
	{				
		return ((ViewGroup)this.getChildAt(0)).getChildCount();
	}	
	 
	/**
	 * Called when the view has finished loading in the children
	 */
	@Override protected void onFinishInflate()
	{	
		super.onFinishInflate();
				
		if (getChildCount() < 1)		
		{
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
			
			this.setVerticalFadingEdgeEnabled(false);
			this.setHorizontalFadingEdgeEnabled(false);
			this.addView(layout, 0);
		}
		
		childCount = getChildViewCount();				
		
		init();
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
		
		childCount = ((ViewGroup)getChildAt(0)).getChildCount();	
		
		if (changed)
		{				
			int featureWidth = getMeasuredWidth();  
			int totalWidth = featureWidth * childCount;
			    
			childCount = getChildViewCount();
			for (int index = 0; index < childCount; index++)
    		{		
    			View child = getChildViewAt(index);
    			    			
    			if (child.getLayoutParams().width == LayoutParams.MATCH_PARENT)
    			{   
    				int height = child.getHeight();
    				
    				if (child.getLayoutParams().height == LayoutParams.MATCH_PARENT)
    				{
    					height = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
    				}    				    				
    				    				
    				child.getLayoutParams().width = featureWidth;
    				child.measure(MeasureSpec.makeMeasureSpec(featureWidth, MeasureSpec.EXACTLY), height);    				
    				requestLayout();
    			}    			    			
    		}
    		
			getChildAt(0).measure(MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST));
			requestLayout();
    		
    		//smoothScrollTo(totalWidth - featureWidth, 0);
			scrollToChildAt(currentChildIndex);
            
			if (mOnViewChangedLister != null)
			{            	
				mOnViewChangedLister.onViewChange(currentChildIndex);                		            	                    
			}                        
		}	
		
		super.onLayout(changed, l, t, r, b);
	}
	
	/**
	 * Called when the view is attached to the window
	 */
	@Override protected void onAttachedToWindow()
	{	
		super.onAttachedToWindow();
		
		ViewParent v = getParent();		
		while (true)
		{
			v = v.getParent();
			
			if (v == null) break;			
			if (v instanceof ScrollView)
			{				
				mParentScrollView = (ScrollView)v;
				break;
			}
		}		
	}

	/**
	 * @brief The swipe detector listener for the fling and scroll checks
	 */
	private class SwipeGestureDetector extends SimpleOnGestureListener
	{	
		/**
		 * Called when the view is scrolled
		 * @param e1 The motion event for the start of the scroll
		 * @param e2 The motion event for the end of the scroll
		 * @param distanceX The distance scrolled in the x direction
		 * @param distanceY The distance scrolled in the y direction
		 */
		@Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
		{
			mScrollingHorizontally = Math.abs(distanceX) > Math.abs(distanceY);
			
			//	Stop the parent views from scrolling if we're scrolling
			if (mScrollingHorizontally && mParentScrollView != null)
			{				
				mParentScrollView.requestDisallowInterceptTouchEvent(true);				
			}
			
			return false;
		}

		/**
		 * Called when the view has been pressed
		 * @param arg0 The motion event 
		 */
		@Override public void onShowPress(MotionEvent arg0) 
		{
		}

		/**
		 * Called when the view has been tapped once
		 * @param arg0 The motion event for the tap
		 */
		@Override public boolean onSingleTapUp(MotionEvent arg0) 
		{
			return false;
		}	

		/**
		 * Called when the user swipes on the view
		 * @param e1 The motion event of the start of the swipe
		 * @param e2 The motion event of the end of the swipe
		 * @param velocityX The velocity of the swipe in the x location
		 * @param velocityY The velocity of t he swipe in the y location
		 */
		@Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{				
			canScroll = true;
			if (e1 == null || e2 == null) return false;
			if (mScrollMode == SMOOTH) return true;
			if (mScrollingVertically) return false;
			
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
			{            	
				int featureWidth = getMeasuredWidth();
				int mActiveFeature = (currentChildIndex < (childCount - 1)) ? currentChildIndex + 1 : childCount - 1;
				smoothScrollTo(getChildViewAt(mActiveFeature).getLeft(), 0);
	            
				if (currentChildIndex != mActiveFeature)
				{
					currentChildIndex = mActiveFeature;
				}
	            
				if (mOnViewChangedLister != null)
				{	            	
					mOnViewChangedLister.onViewChange(mActiveFeature);                		                                    
				}
	            
				return true;				            	
			}
            // left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
			{    
				int featureWidth = getMeasuredWidth();
				int mActiveFeature = (currentChildIndex > 0) ? currentChildIndex - 1 : 0;
				smoothScrollTo(getChildViewAt(mActiveFeature).getLeft(), 0);
                
				if (currentChildIndex != mActiveFeature)
				{
					currentChildIndex = mActiveFeature;
				}
                
				if (mOnViewChangedLister != null)
				{                	
					mOnViewChangedLister.onViewChange(mActiveFeature);                		                	                    
				}
                
				return true;
			}
			
			return false;
		}
	}
	
	/**
	 * @brief The view change listener
	 */
	public interface OnViewChangedListener
	{
		/**
		 * Called when the view is changed
		 * @param newViewIndex What index the view is currently on
		 */
		public void onViewChange(int newViewIndex);
	};
}
