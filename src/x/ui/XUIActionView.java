/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import org.xml.sax.AttributeList;

import x.ui.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

/**
 * @brief The view for swipe and long press actions
 * 
 * Example XML
 * @code
 *	<x.ui.XUIActionView
 *		xmlns:android="http://schemas.android.com/apk/res/android"
 *		android:id="@+id/flipper"
 *	   	android:layout_width="fill_parent" 
 *	   	android:layout_height="fill_parent"
 *	>
 *		<RelativeLayout 		
 *		    android:orientation="vertical"
 *		    android:layout_width="fill_parent"
 *		    android:layout_height="fill_parent"    
 *		>
 *			<!-- Content -->			
 *		</RelativeLayout>
 *		<RelativeLayout
 *			android:layout_width="fill_parent"
 *			android:layout_height="fill_parent"
 *			android:background="@drawable/quick_actions_bg_drawable"
 *			android:gravity="center_vertical"
 *		>
 *			<TableLayout
 *				android:layout_width="fill_parent"
 *				android:layout_height="fill_parent"
 *				android:gravity="center"						
 *				android:orientation="horizontal"
 *			>
 *				<TableRow
 *					android:layout_width="fill_parent"
 *					android:layout_height="fill_parent"
 *				>
 *					<ImageView
 *						android:layout_width="wrap_content"
 *						android:layout_height="wrap_content"
 *						android:src="@drawable/button1"		
 *						android:id="@+id/button1"
 *						android:layout_weight="1"					
 *					/>
 *					<ImageView
 *						android:layout_width="wrap_content"
 *						android:layout_height="wrap_content"
 *						android:src="@drawable/button2"		
 *						android:layout_weight="1"
 *						android:id="@+id/button2"
 *					/>  					
 *				</TableRow>
 *			</TableLayout>		
 *		</RelativeLayout> 
 *	</x.ui.XUIActionView>
 * @endcode
 */
public class XUIActionView extends ViewFlipper
{
	private Context context;
	protected GestureDetector mGestureDetector;
	protected TouchListener mGestureListener;
	protected OnViewChangeListener mViewChangeListener;
	protected OnClickListener mOnClickListener;
	protected int viewIndex = 0;
	
	/**
	 * The gesture used for the swipe detection
	 */
	public enum Gesture
	{
		LEFT_TO_RIGHT,
		RIGHT_TO_LEFT,
		TOP_TO_BOTTOM,
		BOTTOM_TO_TOP
	};
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIActionView(Context context)
	{
		super(context);
		
		this.context = context;
		init();				
	}	
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param attrs The attribute set parsed from SAX parser
	 */
	public XUIActionView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.context = context;
		init();
	}
	
	private void init()
	{
		mGestureDetector = new GestureDetector(new QuickActions());	  
		mGestureListener = new TouchListener(mGestureDetector);
		
		mViewChangeListener = null;
		mOnClickListener = null;
				
		setOnClickListener(mOnClickListener);
		setOnTouchListener(mGestureListener);		
	}
	
	/**
	 * Removes the onTouchListener
	 */
	public void clearOnTouchListener()
	{
		mGestureListener = null;
		setOnTouchListener(null);
	}
	
	/**
	 * Resets the touch listener to the default
	 */
	public void resetOnTouchListener()
	{
		mGestureListener = new TouchListener(mGestureDetector);
		setOnTouchListener(mGestureListener);		
	}
	
	/**
	 * Sets the click listener
	 */
	public void setOnClickListener(OnClickListener l)
	{
		this.mOnClickListener = l;
	}
	
	@Override
	public void setOnTouchListener(XUIActionView.OnTouchListener l)
	{	
		super.setOnTouchListener(l);				
	}
	
	@Override
	protected void onDetachedFromWindow() 
	{
	    try 
	    {
	        super.onDetachedFromWindow();
	    }
	    catch (IllegalArgumentException e) 
	    {	    

	    }
	}
	
	/**
	 * Slides the view to the new child index
	 * @param childIndex The child index to slide to
	 */
	public void gotoChild(int childIndex)
	{		
        Animation slideRightIn = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        Animation slideRightOut = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
        
        setInAnimation(slideRightIn);
        setOutAnimation(slideRightOut);
        setDisplayedChild(childIndex);
	}
	
	/**
	 * Sets the view change listener for when the view changes when swiped
	 * @param viewChangeListener The view change listener
	 */
	public void setViewChangeListener(OnViewChangeListener viewChangeListener)
	{
		this.mViewChangeListener = viewChangeListener;
	}
	
	@Override
	protected void onFinishInflate()
	{		
		super.onFinishInflate();
		
		if (getChildCount() < 2)
		{
			try
			{
				throw new Exception("Action view must contain 2 or more child views");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{	
		super.onLayout(changed, left, top, right, bottom);
	}	
	
	/**
	 * Resets the view back to the first
	 */
	public void closeView()
	{
		triggerGesture(Gesture.RIGHT_TO_LEFT, false, false);
	}	
	
	/**
	 * Triggers the gesture
	 * @param gesture The gesture to be triggered
	 * @param useSound Whether to use sound or not
	 */
	public void triggerGesture(Gesture gesture, boolean useSound)
	{
		triggerGesture(gesture, useSound, true);
	}
	
	private void triggerGesture(Gesture gesture, boolean useSound, boolean sendCallback)
    {	    	
        Animation slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        Animation slideLeftOut = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        Animation slideRightIn = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        Animation slideRightOut = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);             
    	    	                   
        switch (gesture)
        {
        	case LEFT_TO_RIGHT:
        	{
        		setInAnimation(slideRightIn);
                setOutAnimation(slideRightOut);
            	showPrevious();
            	
            	viewIndex = getDisplayedChild();
            	
            	if (sendCallback)
            	{
            		mViewChangeListener.ViewDidChange(getCurrentView(), viewIndex);
            	}
            	
            	if (useSound)
            	{
            		try
            		{
		            	MediaPlayer mp = MediaPlayer.create(context, R.raw.actions_pop);
		                mp.start();
            		}
            		catch (Exception e)
            		{
            			e.printStackTrace();
            		}
            	}
            	
        		break;
        	}
        	
        	case RIGHT_TO_LEFT:
        	{
        		setInAnimation(slideLeftIn);
                setOutAnimation(slideLeftOut);
            	showNext();
            	
            	viewIndex = getDisplayedChild();

            	if (sendCallback)
            	{
            		mViewChangeListener.ViewDidChange(getCurrentView(), viewIndex);
            	}
            	
            	if (useSound)
            	{
            		try
            		{
		            	MediaPlayer mp = MediaPlayer.create(context, R.raw.actions_pop);
		                mp.start();
            		}
            		catch (Exception e)
            		{
            			e.printStackTrace();
            		}
            	}
            	
        		break;
        	}
        }
    }
        
	private class QuickActions extends SimpleOnGestureListener
	{
		private static final int SWIPE_MIN_DISTANCE = 10;		
		private static final int SWIPE_THRESHOLD_VELOCITY = 0;				
		private boolean longPress = true;
				
		@Override
		public void onLongPress(MotionEvent e)
		{		
			//if (false)
			{
				if (viewIndex == 0)
				{
					triggerGesture(Gesture.LEFT_TO_RIGHT, true);	
				}
				else
				{
					triggerGesture(Gesture.RIGHT_TO_LEFT, true);
				}
			}
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{		
			try
			{ 
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					triggerGesture(Gesture.RIGHT_TO_LEFT, true);					
					
					return true;
				}				
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					triggerGesture(Gesture.LEFT_TO_RIGHT, true);
					
					return true;
				}
			}
			catch (Exception e)
			{
				return false;
			}

			return false;
		}
	}
	
	/**
	 * @brief The touch listener for the view
	 */
	public class TouchListener implements View.OnTouchListener
	{ 
		private GestureDetector gestureDetector;
		
		/**		
		 * Default Constructor
		 * @param gd The gesture detector for the listener
		 */
		public TouchListener(GestureDetector gd)
		{			
			this.gestureDetector = gd;
		}
		
		public boolean onTouch(View arg0, MotionEvent event)
		{		
			if (this.gestureDetector.onTouchEvent(event))
			{
				return true;
			}
			else
			{
				mOnClickListener.onClick(arg0);
				return false;
			}
		}
	};
	
	/**
	 * @brief Interface for when the view is clicked	 
	 */
	public interface OnClickListener
	{
		/**
		 * Called when the view is clicked
		 * @param v The view that has been clicked
		 */
		void onClick(View v);		
	}
	
	/**
	 * @brief Interface for when the view changes 
	 */
	public interface OnViewChangeListener
	{
		/**
		 * Called when the view changes
		 * @param newView The new view
		 * @param viewIndex The view's index
		 */
		public void ViewDidChange(View newView, int viewIndex);
	};
}

/**
 * @brief Can't remember why I overrode this class...
 */
abstract class OnClickListener implements XUIActionView.OnClickListener 
{
	private int pos = -1;
	
	public OnClickListener()
	{
		
	}
	
	public OnClickListener(int position)
	{
		pos = position;
	}
	
	public void onClick(View v)
	{
		if (pos > -1)
		{
			onClick(v, pos);
		}
	}
	
	public abstract void onClick(View v, int position);	
}
