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
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * @brief Horizontal gallery view for swiping views similar to the iPhone gallery.
 * @added Added generic children, gallery swiper now supports any children, not just image views.
 * 
 * XML Example
 * @code
 * <x.ui.XUIHorizontalGalleryView
 *		android:layout_width="fill_parent"
 *		android:layout_height="fill_parent"
 *		android:fadingEdge="none"	
 *		android:scrollbars="none"
 *		android:id="@+id/galleryView"
 *	>							
 *		<LinearLayout
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content"
 *			android:orientation="horizontal"			
 *		>								
 *		</LinearLayout>
 *	</x.ui.XUIHorizontalGalleryView>
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
 *	  galleryView.addImage(image);		
 *  }
 *  @endcode
 *  
 *  @TODO Add remove all images and remove image (view) support
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
	protected int SWIPE_MIN_DISTANCE = 40;
	protected int SWIPE_THRESHOLD_VELOCITY = 100;
	private boolean touchDown = false;
	private int currentChildIndex = 0;
	private int screenWidth;
	private int childCount = 0;
	private boolean canScroll = true;
	private OnViewChangedListener mOnViewChangedLister;
	private View childView;
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
		mScrollMode = attrs.getInt(R.styleable.XUIHorizontalScrollView_swipeMode, STEP);				
	}		
	
	/**
	 * Adds an image to the scroller
	 * @param image The imageview to add
	 */
	public void addChildView(View child)
	{
		((ViewGroup)this.getChildAt(0)).addView(child);
	}
	
	/**
	 * Adds an image to the scroller
	 * @param image The imageview to add
	 * @param index The index to add the view at
	 */
	public void addChildView(View child, int index)
	{
		((ViewGroup)this.getChildAt(0)).addView(child, index);
	}
	
	/**
	 * Adds an image to the scroller
	 * @param image The imageview to add
	 * @param params The params for the new view
	 */
	public void addChildView(View child, android.view.ViewGroup.LayoutParams params)
	{
		((ViewGroup)this.getChildAt(0)).addView(child, params);
	}
	
	/**
	 * Removes an image at an index
	 * @param view The view of the image to remove
	 */
	public void removeChild(View view)
	{
		((ViewGroup)this.getChildAt(0)).removeView(view);
	}
	
	/**
	 * Removes an image at an index
	 * @param index The index of the image to remove
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
	 * Gets an image from an index
	 * @param index The index of the image view
	 * @return The ImageView
	 */
	public View getChildViewAt(int index)
	{
		return (View)((ViewGroup)this.getChildAt(0)).getChildAt(index);
	}
	
	/**
	 * Returns the current image view in the view
	 * @return The current image view
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
	 * Scrolls the view to the specified image index
	 * @param index The index to go to
	 */
	public void scrollToChildAt(int index)
	{
		if (index > getChildViewCount()) 
		{
			throw new IllegalStateException("Index out of bounds of gallery [index: " + index + " size: " + getChildViewCount() +"]");
		}
		
		int featureWidth = getMeasuredWidth();
        scrollTo(index * featureWidth, 0);
        
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
	 * Scrolls the view to the specified image index
	 * @param index The index to go to
	 */
	public void smoothScrollToChildAt(int index)
	{
		if (index > getChildViewCount()) 
		{
			throw new IllegalStateException("Index out of bounds of gallery [index: " + index + " size: " + getChildViewCount() +"]");
		}
		
		int featureWidth = getMeasuredWidth();
        smoothScrollTo(index * featureWidth, 0);
       
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
		DisplayMetrics dm = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
				         
        if (mScrollMode == STEP || mScrollMode == SMOOTH)
        {
			gestureDetector = new GestureDetector(new swipeGestureDetector());
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
	                	mScrollingHorizontally = false;
            			mScrollingVertically = false;
	                	
	                    int scrollX = getScrollX();
	                    int featureWidth = v.getMeasuredWidth();                                                            
	                    int mActiveFeature = ((scrollX + (featureWidth / 2)) / featureWidth);
	                    
	                    if (scrollX < mActiveFeature * featureWidth)
	                    {                    	                    	
	                    	mActiveFeature -= mActiveFeature - 1 < 0 ? 0 : 1;
	                    }
	                    else if (scrollX > mActiveFeature * featureWidth)
	                    {
	                    	mActiveFeature += mActiveFeature + 1 >= childCount ? 0 : 1;
	                    }
	                    else
	                    {
	                    	mActiveFeature = currentChildIndex;
	                    }
	                    
	                    int scrollTo = mActiveFeature * featureWidth;
	                    smoothScrollTo(scrollTo, 0);
	                    
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
	 * Sets the image change listener
	 * @param imageListener The new image change listener
	 */
	public void setOnViewChange(OnViewChangedListener imageListener)
	{
		this.mOnViewChangedLister = imageListener;
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
	 * Gets the image count
	 * @return The image count
	 */
	public int getChildViewCount()
	{				
		return ((ViewGroup)this.getChildAt(0)).getChildCount();
	}	
	
	@Override
	protected void onFinishInflate()
	{	
		Debug.out(getRootView().toString());
		
		int mChildCount = getChildCount();
		View[] views = new View[mChildCount];
		
		for (int index = 0; index < mChildCount; index++)
		{
			views[index] = getChildAt(index);
		}
		
		this.detachAllViewsFromParent(); 
		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		
		for (int index = 0; index < mChildCount; index++)
		{
			layout.addView(views[index]);
		}
		
		this.addView(layout);
				
		childCount = ((ViewGroup)getChildAt(0)).getChildCount();
		
		super.onFinishInflate();
		
		init();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		
		childCount = ((ViewGroup)getChildAt(0)).getChildCount();		
		
		if (changed)
		{				
			int featureWidth = getMeasuredWidth();            
            smoothScrollTo(currentChildIndex * featureWidth, 0);
            
            if (mOnViewChangedLister != null)
            {            	
            	mOnViewChangedLister.onViewChange(currentChildIndex);                		            	                    
            }
		}
	}
	
	@Override
	protected void onAttachedToWindow()
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
	private class swipeGestureDetector extends SimpleOnGestureListener
	{	
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
		{
			mScrollingHorizontally = Math.abs(distanceX) > Math.abs(distanceY);
			
			//	Stop the parent views from scrolling if we're scrolling
			if (mScrollingHorizontally && mParentScrollView != null)
			{				
				mParentScrollView.requestDisallowInterceptTouchEvent(true);				
			}
			
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) 
		{
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) 
		{
			return false;
		}	
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{				
			canScroll = true;
			if (e1 == null || e2 == null) return false;
			if (mScrollMode == SMOOTH) return true;
			if (mScrollingVertically) return false;
			
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {            	
				int featureWidth = getMeasuredWidth();
	            int mActiveFeature = (currentChildIndex < (childCount - 1)) ? currentChildIndex + 1 : childCount - 1;
	            smoothScrollTo(mActiveFeature * featureWidth, 0);
	            
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
                smoothScrollTo(mActiveFeature * featureWidth, 0);
                
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
