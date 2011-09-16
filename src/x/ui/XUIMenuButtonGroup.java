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
	 * Adds a new button to the group
	 * @param child The new button to add
	 */
	@Override	
	public void addView(View child)
	{
		if (!(child instanceof XUIMenuButton)) return;
		
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child);		
		updateLayout();
	}

	/**
	 * Adds a new button to the group
	 * @param child The new button to add
	 */
	public void addView(View... child)
	{
		for (XUIMenuButton b : (XUIMenuButton[])child)
		{
			if (!(b instanceof XUIMenuButton)) continue;
			((LinearLayout)layoutView.findViewById(R.id.items)).addView(b);
		}
		
		updateLayout();
	}
	
	/**
	 * Adds a new button to the group at the specified index
	 * @param child The new button to add
	 * @param index The index to put the new button
	 */
	@Override
	public void addView(View child, int index)
	{
		if (!(child instanceof XUIMenuButton)) return;

		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child, index);
		updateLayout();
	}
	
	/**
	 * Adds a new button to the group at the specified index
	 * @param child The new button to add
	 * @param params The params for the new view
	 */
	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params)
	{
		if (!(child instanceof XUIMenuButton)) return;

		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child, params);
		updateLayout();
	}
	
	/**
	 * Removes a view from the group
	 * @param view The view to remove
	 */
	@Override
	public void removeView(View view)
	{	
		if (!(view instanceof XUIMenuButton)) return;

		((LinearLayout)layoutView.findViewById(R.id.items)).removeView(view);
		updateLayout();
	}

	/**
	 * Removes a view from the group
	 * @param view The index of where to remove the view
	 */
	@Override
	public void removeViewAt(int index)
	{		
		((LinearLayout)layoutView.findViewById(R.id.items)).removeViewAt(index);
		updateLayout();
	}
	
	/**
	 * Removes all views from the group
	 */
	@Override
	public void removeAllViews()
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).removeAllViews();
		updateLayout();
	}
	
	private void updateLayout()
	{
		childCount = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount();
		
		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{
			View childView = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildAt(viewIndex);
			
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
						
			((LinearLayout)layoutView.findViewById(R.id.items)).addView(views[viewIndex]);			
		}
		
		this.addView(layoutView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));		
		
		((TextView)findViewById(R.id.group_label)).setText(x.lib.StringUtils.capitalize(groupName));
	}
}
