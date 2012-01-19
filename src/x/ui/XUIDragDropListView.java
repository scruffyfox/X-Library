/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * @brief The Drag and drop sort class for reordering cells in a list view by the user 
 * Still in development
 */
public class XUIDragDropListView extends ListView
{
	protected boolean mDragMode;

	protected int mStartPosition;
	protected int mEndPosition;
	protected int mDragPointOffset; // Used to adjust drag view location

	protected ImageView mDragView;
	protected GestureDetector mGestureDetector;

	protected DropListener mDropListener;
	protected RemoveListener mRemoveListener;
	protected DragListener mDragListener;
	
	protected Rect mDragHandle;

	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param attrs The attribute set passed via the SAX parser
	 */
	public XUIDragDropListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);		
	}

	/**
	 * Set the drop listener
	 * @param l The drop listener
	 */
	public void setDropListener(DropListener l)
	{
		mDropListener = l;
	}

	/**
	 * Sets the remove listener
	 * @param l The remove listener
	 */
	public void setRemoveListener(RemoveListener l)
	{
		mRemoveListener = l;
	}

	/**
	 * Sets the drag listener
	 * @param l The drag listener
	 */
	public void setDragListener(DragListener l)
	{
		mDragListener = l;
	}
	
	/**
	 * Sets the drag handle
	 * @param x The x coordinate for the drag handle
	 * @param y The y coordinate for the drag handle
	 * @param width The width of the drag handle
	 * @param height The height of the drag handle
	 */
	public void setDragHandle(int x, int y, int width, int height)
	{
		mDragHandle = new Rect(x, y, x + width, y + height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (mDropListener == null || mDropListener == null)
		{
			return super.onTouchEvent(ev);
		}
		
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();
		
		if (action == MotionEvent.ACTION_DOWN && x > getWidth() - (getWidth() / 5))
		{						
			mDragMode = true;			
		}

		if (!mDragMode)
		{
			return super.onTouchEvent(ev);
		}
		
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
			{
				mStartPosition = pointToPosition(x, y);
				if (mStartPosition != INVALID_POSITION)
				{
					int mItemPosition = mStartPosition - getFirstVisiblePosition();
					mDragPointOffset = y - getChildAt(mItemPosition).getTop();
					mDragPointOffset -= ((int) ev.getRawY()) - y;
					startDrag(mItemPosition, y);
					drag(0, y);
				}
				
				break;
			}
			
			case MotionEvent.ACTION_MOVE:
			{
				drag(0, y);
				break;
			}
			
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			default:
			{
				mDragMode = false;
				mEndPosition = pointToPosition(x, y);
				stopDrag(mStartPosition - getFirstVisiblePosition());				
				
				if (y > getChildAt(0).getHeight() * getChildCount() && mDropListener != null)
				{
					mDropListener.onDrop(mStartPosition, getChildCount() - 1);
					break;
				}
				else if (y < getChildAt(0).getHeight() && mDropListener != null)
				{
					mDropListener.onDrop(mStartPosition, getFirstVisiblePosition());
					break;
				}
				
				if (mDropListener != null && mStartPosition != INVALID_POSITION && mEndPosition != INVALID_POSITION)
				{
					mDropListener.onDrop(mStartPosition, mEndPosition);
				}
				
				break;
			}
		}
		return true;
	}

	// move the drag view
	private void drag(int x, int y)
	{
		if (mDragView != null)
		{
			WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams)mDragView.getLayoutParams();
			layoutParams.x = x;
			layoutParams.y = y - mDragPointOffset;
			WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.updateViewLayout(mDragView, layoutParams);

			if (mDragListener != null)
			{
				mDragListener.onDrag(x, y, null);			
			}
		}
	}

	// enable the drag view for dragging
	private void startDrag(int itemIndex, int y)
	{
		stopDrag(itemIndex);

		View item = getChildAt(itemIndex);
		if (item == null)
		{
			return;
		}
		
		item.setDrawingCacheEnabled(true);
		if (mDragListener != null)
		{
			mDragListener.onStartDrag(item);
		}
		
		// Create a copy of the drawing cache so that it does not get recycled
		// by the framework when the list tries to clean up memory
		Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

		WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP;
		mWindowParams.x = 0;
		mWindowParams.y = y - mDragPointOffset;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		Context context = getContext();
		ImageView v = new ImageView(context);
		v.setImageBitmap(bitmap);

		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	// destroy drag view
	private void stopDrag(int itemIndex)
	{
		if (mDragView != null)
		{
			if (mDragListener != null)
			{
				mDragListener.onStopDrag(getChildAt(itemIndex));
			}
			
			mDragView.setVisibility(GONE);
			WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
	}

//	private GestureDetector createFlingDetector()
//	{
//		return new GestureDetector(getContext(), new SimpleOnGestureListener()
//		{
//			@Override
//			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
//			{
//				if (mDragView != null)
//				{
//					int deltaX = (int) Math.abs(e1.getX() - e2.getX());
//					int deltaY = (int) Math.abs(e1.getY() - e2.getY());
//
//					if (deltaX > mDragView.getWidth() / 2 && deltaY < mDragView.getHeight())
//					{
//						mRemoveListener.onRemove(mStartPosition);
//					}
//
//					stopDrag(mStartPosition - getFirstVisiblePosition());
//
//					return true;
//				}
//				return false;
//			}
//		});
//	}
	
	/**
	 * @brief The drag listener
	 */
	public interface DragListener 
	{
		/**
		 * Called when the cell has started to be dragged
		 * @param itemView
		 */
		void onStartDrag(View itemView);
		/**
		 * Called when the cell is being dragged
		 * @param x The x coordinate of the view
		 * @param y The y coordinate of the view
		 * @param listView The listview
		 */
		void onDrag(int x, int y, ListView listView);
		/**
		 * Called when the cell has stopped being dragged
		 * @param itemView The view 
		 */
		void onStopDrag(View itemView);
	}
	
	/**
	 * @brief The drop listener	 
	 */
	public interface DropListener 
	{	
		/**
		 * Called when the view has been dropped
		 * @param from The original position of the cell
		 * @param to The new position of the cell
		 */
		void onDrop(int from, int to);
	}
	
	/**
	 * @brief The remove listener
	 */
	public interface RemoveListener 
	{	
		/**
		 * Called when a cell is removed from a list
		 * @param which The position of the removed cell
		 */
		void onRemove(int which);
	}		
}
