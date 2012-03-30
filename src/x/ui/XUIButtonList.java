/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import x.lib.Debug;
import x.type.ItemList;
import x.util.StringUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * @brief This custom control allows you to have children and set them as buttons (clickable="true")
 * with one method to handle the click of the children, rather than having to set the onClick
 * for each child, or setting an onClick listener for each child.
 * 
 * Note: you <b>must</b> set the view as clickable if you want it to register as a button
 * 
 * @param XML <b>onButtonClick</b>: This value is used in XML and uses a method name reference for the button click callback. 
 * You can also set the callback using the {@link OnButtonClickedListener} instead of the XML way
 * 
 * The method callback must contain 2 parameters for example:
 * @code
 * public void buttonListClick(int index, View v)
 * {
 * 	// code here
 * }
 * @endcode
 * 
 * Example code
 * @code
 * <x.ui.XUIButtonList
 * 	android:layout_width="match_parent"
 * 	android:layout_height="wrap_content"
 * 	x:onButtonClick="buttonListClick"
 * >
 * 	<ImageView
 * 		android:layout_width="wrap_content"
 * 		android:layout_width="wrap_content"
 * 		android:clickable="true"
 * 	/>
 * 
 * 	<ImageView
 * 		android:layout_width="wrap_content"
 * 		android:layout_width="wrap_content"
 * 		android:clickable="false"
 * 	/>
 * </x.ui.XUIButtonList>
 * @endcode
 */
public class XUIButtonList extends LinearLayout
{	
	private Context mContext;
	private OnButtonClickedListener mOnButtonClickedListener;
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIButtonList(Context context)
	{
		super(context);
		mContext = context;
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUIButtonList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context; 
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XUIButtonList);		
		final String onClickStr = a.getString(R.styleable.XUIButtonList_onButtonClick);
		
		if (onClickStr != null && onClickStr.length() > 0)
		{			
			Debug.out(onClickStr);
	
			mOnButtonClickedListener = new OnButtonClickedListener()
			{
				@Override public void onButtonClicked(int index, View v)
				{
					Method mHandler = null;
					
					if (mHandler == null)
					{
						try
						{
							mHandler = getContext().getClass().getMethod(onClickStr, int.class, View.class);
						}
						catch (NoSuchMethodException e)
						{							
							throw new IllegalStateException("Could not find method " + onClickStr + " in class " + getContext().getClass());
						}
					}

					try
					{
						mHandler.invoke(getContext(), index, v);
					}
					catch (IllegalAccessException e)
					{
						throw new IllegalStateException("Could not execute non public method of the activity", e);
					}
					catch (InvocationTargetException e)
					{
						throw new IllegalStateException("Could not execute method of the activity", e);
					}	
				}
			};
		}
	}
	
	/**
	 * Gets the child index of a view
	 * @param v The view to look for
	 * @return The index of the view or -1 if it was not found
	 */
	public int getChildIndex(View v)
	{
		int childCount = getChildCount();
		int skipped = 0;
		for (int index = 0; index < childCount; index++)
		{
			if (!getChildAt(index).isClickable()) 
			{
				skipped++;
				continue;
			}
			
			if (getChildAt(index) == v)
			{
				return index - skipped;
			}
		}
		
		return -1;
	}
	
	/**
	 * Gets all the buttons in the view
	 * @return All of the <b>clickable</b> views in the container 
	 */
	public View[] getButtons()
	{
		ArrayList<View> views = new ArrayList<View>();
		
		int childCount = getChildCount();
		int skipped = 0;
		for (int index = 0; index < childCount; index++)
		{
			if (getChildAt(index).isClickable()) 
			{
				views.add(getChildAt(index));
			}			
		}
		
		return views.toArray(new View[views.size()]);
	}
	
	/**
	 * Sets the click listener for the button list
	 * @param l The new click listener
	 */
	public void setOnButtonClickedListener(OnButtonClickedListener l)
	{
		this.mOnButtonClickedListener = l;
	}

	@Override protected void onFinishInflate()
	{
		super.onFinishInflate();
		
		int childCount = getChildCount();
		int skiped = 0;
		for (int index = 0; index < childCount; index++)
		{
			View child = getChildAt(index);			
			if (child.isClickable() && mOnButtonClickedListener != null)
			{
				child.setOnClickListener(new OnClickListenerHelper(index - skiped)
				{
					@Override public void onClick(View v)
					{
						mOnButtonClickedListener.onButtonClicked(viewIndex, v);
					}
				});
			}
			else
			{
				skiped++;
				continue;
			}
		}
	}
	
	/**
	 * @brief The click listener for the button list
	 */
	public interface OnButtonClickedListener
	{
		/**
		 * Called when a button is clicked
		 * @param index The index of the view minus non-clickable views
		 * @param v The view clicked
		 */
		public void onButtonClicked(int index, View v);		
	}
	
	private class OnClickListenerHelper implements OnClickListener
	{
		protected int viewIndex;
		public OnClickListenerHelper(int index)
		{
			viewIndex = index;
		}
		
		@Override public void onClick(View v){}
	}
}
