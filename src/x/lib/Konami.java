/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

/**
 * @brief The konami touch extension to allow cheat codes to be added to views
 * 
 * Usage
 * @code
 * Konami cheatCode = new Konami(new int[]{Konami.UP, Konami.UP, Konami.DOWN, Konami.DOWN, Konami.LEFT, Konami.RIGHT, Konami.LEFT, Konami.RIGHT});
 * cheatCode.setOnKonamiCodeListener(new OnKonamiCodeListener()
 * {
 * 	public void OnSuccessfullCode()
 * 	{
 * 		Log.v("", "Successful code entered");
 *	}
 * });
 * 
 * view.setOnTouchListener(cheatCode);
 * @endcode
 */
public class Konami implements OnTouchListener 
{    
	/**
	 * Left swipe value
	 */
	public static final int LEFT = 0x00;
	/**
	 * Right swipe value
	 */
	public static final int RIGHT = 0x01;
	/**
	 * Up swipe value
	 */
	public static final int UP = 0x02;
	/**
	 * Down swipe value
	 */
	public static final int DOWN = 0x03;
	/**
	 * Tap value
	 */
	public static final int TAP = 0x04;
	
	private static final int SWIPE_MIN_DISTANCE = 0;
    private static final int SWIPE_THRESHOLD_VELOCITY = 500;
	private ItemList<Integer> mGestureList;
	private int mCurrentPtr = 0;
	private GestureDetector mGestureScanner;		
    private int[] mCode = {UP, UP, DOWN, DOWN, LEFT, RIGHT, LEFT, RIGHT, TAP, TAP, TAP};
    private OnKonamiCodeListener mKonamiCodeListener;

    /**
     * Default Constructor
     */
    public Konami()
	{		
    	mGestureList = new ItemList<Integer>();
		mGestureScanner = new GestureDetector(new gestureDetector());		
	}
    
    /**
     * Default constructor
     * @param listener The listener for successful code
     */
    public Konami(OnKonamiCodeListener listener)
    {
    	this();
    	setOnKonamiCodeListener(listener);    	
    }
    
    /**
     * Default Constructor
     * @param konamiCode The array of cheat commands (Max 32 items)
     * @throws Exception If the code size is greater than 32
     */
    public Konami(int[] konamiCode) throws Exception
	{	
    	if (konamiCode.length > 32)
		{
			throw new Exception("Konami code too long");
		}
    	
    	mGestureList = new ItemList<Integer>();
		mCode = konamiCode;
		mGestureScanner = new GestureDetector(new gestureDetector());
	}    
	
	public boolean onTouch(View v, MotionEvent event) 
	{		
		mGestureScanner.onTouchEvent(event);
		
		return true;
	}		
	
	/**
	 * Set the callback for the successful commands
	 * @param listener The listener for the successful command
	 */
	public void setOnKonamiCodeListener(OnKonamiCodeListener listener)
	{
		mKonamiCodeListener = listener;
	}
	
	/**
	 * Sets the cheat code
	 * @param konamiCode The cheat code to set
	 * @throws Exception If the code size is greater than 32
	 */
	public void setCode(int[] konamiCode) throws Exception
	{
		if (konamiCode.length > 32)
		{
			throw new Exception("Konami code too long");
		}
		
		mCode = konamiCode;
	}
	
	private class gestureDetector extends SimpleOnGestureListener
	{			
		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{			
        	mGestureList.add(TAP);
        	checkCode();
        	
			return true;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
		{			
			try 
			{	            	           
	            // right to left swipe
	            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
	            {	            	
	            	mGestureList.add(LEFT);
	            }  
	            // left to right swipe
	            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
	            {	            	
	            	mGestureList.add(RIGHT);
	            }
	            // down to up swipe
	            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) 
	            {
	            	mGestureList.add(UP);
	            }  
	            // up to down swiper
	            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) 
	            {	            	
	            	mGestureList.add(DOWN);	 
	            }	          
	        } 
			catch (Exception e) 
	        {
	            // nothing
				e.printStackTrace();
	        }
			
			//	If the last gesture matches the end of the code, make a check
			if (mGestureList.get(mGestureList.size() - 1) == mCode[mCode.length - 1])
			{
				checkCode();
			}
			
			return true;
		}
		
		private void checkCode()
		{			
			//	Check the array for code
			int totalCorrect = 0;
			for (int loop = 0; loop < mGestureList.size(); loop++)
			{
				//	Check if it is correct
				if (mCode[loop] == mGestureList.get(loop))
				{					
					totalCorrect++;
				}
				else
				{
					//	Reset the gesture list
					mGestureList.clear();										
					break;
				}
			}
			
			//	If the code was right, execte the callback
			if (totalCorrect == mCode.length)
			{
				if (mKonamiCodeListener != null)
				{
					mKonamiCodeListener.OnSuccessfullCode();
				}
				
				//	Reset the gesture list
				mGestureList.clear();						
			}
		}
	};
	
	/**
	 * @brief The interface for the konami code 
	 */
	public interface OnKonamiCodeListener
	{
		/**
		 * Gets called when a successful code is entered
		 */
		public void OnSuccessfullCode();		
	};
}
