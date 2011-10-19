/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *  @brief The menu container for XUIMenuButtons
 *
 *  Custom Attributes:
 *  groupName - The label text for the group
 *  
 *  Example XML layout code
 *  @code
 *  <x.ui.XUIMenuButtonGroup
 *		android:layout_width="fill_parent"
 *		android:layout_height="wrap_content"
 *		tab:groupName="Location"
 *		android:layout_marginTop="10dp"
 *	>			
 *		<x.ui.XUIMenuButton
 *			android:layout_width="fill_parent"
 *			android:layout_height="wrap_content"
 *			android:id="@+id/town"
 *			android:onClick="townClick"  
 *			android:clickable="true"
 *		>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text="Town / City"    					
 *			/>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text=""
 *				android:textSize="16dp"
 *			/>
 *		</x.ui.XUIMenuButton>
 *		<x.ui.XUIMenuButton
 *			android:layout_width="fill_parent"
 *			android:layout_height="wrap_content"				
 *		>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text="Search Radius"
 *			/>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text="50 Miles"
 *				android:textSize="16dp"
 *			/>
 *		</x.ui.XUIMenuButton>
 *	</x.ui.XUIMenuButtonGroup>
 *  @endcode
 */
public class XUIMenuButtonGroup extends LinearLayout
{
	private Context mContext;
	private ViewGroup layoutView = null;
	private int childCount = 0;
	private LayoutInflater mLayoutInflater;
	private String groupName = "";
	private OnMenuButtonAdded mOnMenuButtonAdded;
	private OnMenuButtonRemoved mOnMenuButtonRemoved;
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUIMenuButtonGroup(Context context)
	{
		super(context);
		mContext = context;
		
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = (ViewGroup)mLayoutInflater.inflate(R.layout.xui_menu_button_group, this);		
	}
	
	/**
	 * Default Constructor
	 * @param context
	 * @param attrs
	 */
	public XUIMenuButtonGroup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XUIMenuButtonGroup);		
		groupName = attributes.getString(R.styleable.XUIMenuButtonGroup_groupName);
		
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = (ViewGroup)mLayoutInflater.inflate(R.layout.xui_menu_button_group, null);				
	}
	
	/**
	 * Sets the menu button added listener
	 * @param mOnMenuButtonAdded The listener
	 */
	public void setOnMenuButtonAdded(OnMenuButtonAdded mOnMenuButtonAdded)
	{
		this.mOnMenuButtonAdded = mOnMenuButtonAdded;
	}
	
	/**
	 * Sets the on menu button removed listener
	 * @param mOnMenuButtonRemoved The listener
	 */
	public void setOnMenuButtonRemoved(OnMenuButtonRemoved mOnMenuButtonRemoved)
	{
		this.mOnMenuButtonRemoved = mOnMenuButtonRemoved;
	}
	
	/**
	 * Gets all the buttons as an array from the group
	 * @return The buttons within the group as an XUIMenuButton array
	 */
	public XUIMenuButton[] getButtons()
	{
		LinearLayout itemContainer = ((LinearLayout)layoutView.findViewById(R.id.items));
		int childCount = itemContainer.getChildCount();
		XUIMenuButton[] buttons = new XUIMenuButton[childCount];
		
		for (int index = 0; index < childCount; index++)
		{
			buttons[index] = (XUIMenuButton)itemContainer.getChildAt(index);
		}
		
		return buttons;
	}
	
	/**
	 * Gets the XUIMenuButton at the specified index
	 * @param index The index of the view
	 * @return The view
	 */
	public XUIMenuButton getButtonAt(int index)
	{
		LinearLayout itemContainer = ((LinearLayout)layoutView.findViewById(R.id.items));
		return (XUIMenuButton)itemContainer.getChildAt(index);
	}
	
	/**
	 * Adds a new button to the group
	 * @param child The new button to add
	 */
	public void addMenuButton(XUIMenuButton child)
	{		
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child);		
		updateLayout();
		
		if (mOnMenuButtonAdded != null)
		{
			mOnMenuButtonAdded.onMenuButtonAdded(child, ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount() - 1);
		}
	}

	/** 
	 * Adds a new button to the group
	 * @param child The new button to add
	 */
	public void addMenuButton(XUIMenuButton... child)
	{
		for (XUIMenuButton b : (XUIMenuButton[])child)
		{
			((LinearLayout)layoutView.findViewById(R.id.items)).addView(b);
			
			if (mOnMenuButtonAdded != null)
			{
				mOnMenuButtonAdded.onMenuButtonAdded(b, ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount() - 1);
			}
		}
		
		updateLayout();
	}
	
	/**
	 * Adds a new button to the group at the specified index
	 * @param child The new button to add
	 * @param index The index to put the new button
	 */
	public void addMenuButton(XUIMenuButton child, int index)
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child, index);
		updateLayout();
		
		if (mOnMenuButtonAdded != null)
		{
			mOnMenuButtonAdded.onMenuButtonAdded(child, index);
		}
	}
	
	/**
	 * Adds a new button to the group at the specified index
	 * @param child The new button to add
	 * @param params The params for the new view
	 */
	public void addMenuButton(XUIMenuButton child, android.view.ViewGroup.LayoutParams params)
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child, params);
		updateLayout();
		
		if (mOnMenuButtonAdded != null)
		{
			mOnMenuButtonAdded.onMenuButtonAdded(child, ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount() - 1);
		}
	}
	
	/**
	 * Removes a view from the group
	 * @param view The view to remove
	 */
	public void removeMenuButton(XUIMenuButton view)
	{	
		int index = ((LinearLayout)layoutView.findViewById(R.id.items)).indexOfChild(view);
		((LinearLayout)layoutView.findViewById(R.id.items)).removeView(view);
		updateLayout();
		
		if (mOnMenuButtonRemoved != null)
		{
			mOnMenuButtonRemoved.onMenuButtonRemoved(index);
		}
	}

	/**
	 * Removes a view from the group
	 * @param view The index of where to remove the view
	 */
	public void removeMenuButtonAt(int index)
	{		
		((LinearLayout)layoutView.findViewById(R.id.items)).removeViewAt(index);
		updateLayout();
		
		if (mOnMenuButtonRemoved != null)
		{
			mOnMenuButtonRemoved.onMenuButtonRemoved(index);
		}
	}
	
	/**
	 * Removes all views from the group
	 */
	public void removeAllMenuButtons()
	{
		int count = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount();
		((LinearLayout)layoutView.findViewById(R.id.items)).removeAllViews();
		updateLayout();
		
		for (int index = 0; index < count; index++)
		{
			if (mOnMenuButtonRemoved != null)
			{
				mOnMenuButtonRemoved.onMenuButtonRemoved(index);
			}
		}
	}
	
	private void updateLayout()
	{
		childCount = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount();
		
		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{
			View childView = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildAt(viewIndex);
			
			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1) 
			{			
				if (childCount == 1)
				{
					childView.setBackgroundResource(R.drawable.button_group_all);
				}		
				else
				{
					if (viewIndex == 0)
					{
						childView.setBackgroundResource(R.drawable.button_group_top);
					}
					else if (viewIndex == childCount - 1)
					{
						childView.setBackgroundResource(R.drawable.button_group_bottom);
					}
					else
					{
						childView.setBackgroundResource(R.drawable.button_group_middle);
					}
				}	
			}
			else
			{
				childView.setBackgroundResource(R.drawable.button_group_middle);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{	
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	protected void onFinishInflate()
	{	
		super.onFinishInflate();
						 
		int childCount = getChildCount();
		XUIMenuButton[] views = new XUIMenuButton[childCount];
				
		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{			
			views[viewIndex] = (XUIMenuButton)getChildAt(viewIndex);					
		}
					
		this.removeAllViews();
				
		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{
			LinearLayout container = (LinearLayout)views[viewIndex].findViewById(R.id.container).getParent();
			
			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1) 
			{			
				if (childCount == 1)
				{
					container.setBackgroundResource(R.drawable.button_group_all);
				}		
				else
				{
					if (viewIndex == 0)
					{
						container.setBackgroundResource(R.drawable.button_group_top);
					}
					else if (viewIndex == childCount - 1)
					{
						container.setBackgroundResource(R.drawable.button_group_bottom);
					}
					else
					{
						container.setBackgroundResource(R.drawable.button_group_middle);
					}
				}	
			}
			else
			{
				container.setBackgroundResource(R.drawable.button_group_middle);
			}
						
			((LinearLayout)layoutView.findViewById(R.id.items)).addView(views[viewIndex]);			
		}
		
		this.addView(layoutView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));		
		
		if (groupName != null && groupName.length() > 0)
		{			
			((TextView)findViewById(R.id.group_label)).setText(x.lib.StringUtils.capitalize(groupName));
		}
		else
		{
			((TextView)findViewById(R.id.group_label)).setVisibility(View.GONE);	
		}
	}
	
	public interface OnMenuButtonAdded
	{
		public void onMenuButtonAdded(XUIMenuButton button, int index);
	}
	
	public interface OnMenuButtonRemoved
	{
		public void onMenuButtonRemoved(int index);
	}
}
