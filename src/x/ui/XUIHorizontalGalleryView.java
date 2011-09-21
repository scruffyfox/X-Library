/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.app.Activity;
import android.content.Context;
//import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

/**
 * @brief Horizontal gallery view for swiping images similar to the iPhone gallery
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
public class XUIHorizontalGalleryView extends HorizontalScrollView
{
	private Context context;		
	private GestureDetector gestureDetector;
	protected int SWIPE_MIN_DISTANCE = 40;
	protected int SWIPE_THRESHOLD_VELOCITY = 100;
	private boolean touchDown = false;
	private int currentChildIndex = 0;
	private int screenWidth;
	private int childCount = 0;
	private boolean canScroll = true;
	private OnImageChangedListener mOnImageChangedLister;
	private View childView;
	private int mActiveFeature = 0;	
  
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIHorizontalGalleryView(Context context) 
	{
		super(context);
		this.context = context;			
	}
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param attributes The attribute set passed via the SAX parser 
	 */	
	public XUIHorizontalGalleryView(Context context, AttributeSet attributes) 
	{
		super(context, attributes);
		this.context = context;		
	
		//TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.XUITab);	
	}		
	
	/**
	 * Adds an image to the scroller
	 * @param image The imageview to add
	 */
	public void addImage(View child)
	{
		if (!(child instanceof ImageView)) return;
		
		((ViewGroup)this.getChildAt(0)).addView(child);
	}
	
	/**
	 * Adds an image to the scroller
	 * @param image The imageview to add
	 * @param index The index to add the view at
	 */
	public void addImage(View child, int index)
	{
		if (!(child instanceof ImageView)) return;
		
		((ViewGroup)this.getChildAt(0)).addView(child, index);
	}
	
	/**
	 * Adds an image to the scroller
	 * @param image The imageview to add
	 * @param params The params for the new view
	 */
	public void addImage(View child, android.view.ViewGroup.LayoutParams params)
	{
		if (!(child instanceof ImageView)) return;
		
		((ViewGroup)this.getChildAt(0)).addView(child, params);
	}
	

	/**
	 * Removes an image at an index
	 * @param view The view of the image to remove
	 */
	public void removeImage(View view)
	{
		((ViewGroup)this.getChildAt(0)).removeView(view);
	}
	
	/**
	 * Removes an image at an index
	 * @param index The index of the image to remove
	 */
	public void removeImageAt(int index)
	{
		((ViewGroup)this.getChildAt(0)).removeViewAt(index);
	}
	
	/**
	 * Removes all the views
	 */
	public void removeAllImages()
	{
		((ViewGroup)this.getChildAt(0)).removeAllViews();
	}
	
	int prevX = 0;
	private void init()
	{
		DisplayMetrics dm = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        mOnImageChangedLister = new OnImageChangedListener()
		{			
			public void onImageChange(int newImageIndex)
			{
			}
		};
				
		gestureDetector = new GestureDetector(new swipeGestureDetector());
		setOnTouchListener(new View.OnTouchListener() 
		{            
            public boolean onTouch(View v, MotionEvent event) 
            {
            	requestFocusFromTouch();
            	
                //If the user swipes
                if (gestureDetector.onTouchEvent(event)) 
                {
                    return true;
                }
                
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                {
                    int scrollX = getScrollX();
                    int featureWidth = v.getMeasuredWidth();                                                            
                    mActiveFeature = ((scrollX + (featureWidth / 2)) / featureWidth);
                    
                    if (scrollX < mActiveFeature * featureWidth)
                    {                    	                    	
                    	mActiveFeature -= mActiveFeature - 1 < 0 ? 0 : 1;
                    }
                    else
                    {
                    	mActiveFeature += mActiveFeature + 1 >= childCount ? 0 : 1;
                    }
                    
                    int scrollTo = mActiveFeature * featureWidth;
                    smoothScrollTo(scrollTo, 0);
                    mOnImageChangedLister.onImageChange(mActiveFeature);
                    
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
	}
	
	/**
	 * Sets the image change listener
	 * @param imageListener The new image change listener
	 */
	public void setOnImageChange(OnImageChangedListener imageListener)
	{
		this.mOnImageChangedLister = imageListener;
	}
	
	private void disableScroll()
	{
		canScroll = false;
	}
	
	private void enableScroll()
	{
		canScroll = true;
	}
		
	/**
	 * Gets the image count
	 * @return The image count
	 */
	public int getImageCount()
	{				
		return ((ViewGroup)this.getChildAt(0)).getChildCount();
	}	
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		
		childCount = ((ViewGroup)getChildAt(0)).getChildCount();
		init();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		
		childCount = ((ViewGroup)getChildAt(0)).getChildCount();		
	}
	
	private class swipeGestureDetector extends SimpleOnGestureListener
	{				
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			return super.onScroll(e1, e2, distanceX, distanceY);
		}		
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{	
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {            	
				int featureWidth = getMeasuredWidth();
	            mActiveFeature = (mActiveFeature < (childCount - 1)) ? mActiveFeature + 1 : childCount - 1;
	            smoothScrollTo(mActiveFeature * featureWidth, 0);
	            mOnImageChangedLister.onImageChange(mActiveFeature);
	            
	            return true;				            	
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {    
            	int featureWidth = getMeasuredWidth();
                mActiveFeature = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
                smoothScrollTo(mActiveFeature * featureWidth, 0);
                mOnImageChangedLister.onImageChange(mActiveFeature);
                
            	return true;
            }
			
			return false;
		}
	}
	
	/**
	 * @brief The image change listener
	 */
	public interface OnImageChangedListener
	{
		/**
		 * Called when the image is changed
		 * @param newImageIndex What index the view is currently on
		 */
		public void onImageChange(int newImageIndex);
	};
}
