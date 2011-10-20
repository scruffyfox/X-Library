/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import x.ui.R;
import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 *  @brief The tab host container for the XUITab
 * 
 *  Custom Attributes:
 *  targetContainer - The id of the target container for the tab's activities (Required)
 * 
 *  Sample in XML
 *  @code
 *  <x.ui.XUITabHost
 *		android:layout_width="fill_parent"
 *		android:layout_height="46dp"
 *		tab:targetContainer="@+id/content_view" 
 *		android:id="@+id/tab_host"
 *	/>	
 *  @endcode
 *  
 *  Adding tabs in Code
 *  @code
 *  XUITabHost tabHost = (XUITabHost)findViewById(R.id.tab_host); 
 *  tabHost.setup(getLocalActivityManager());
 *    
 *  XUITabParams tabParams = new XUITabParams();
 *  tabParams.selectedIcon = selectedIcon;
 *  tabParams.deselectedIcon = deselectedIcon; 
 *  tabParams.layoutParams.width = 200;     	        	
 *  tabParams.deselectedDrawable = new BitmapDrawable(deselectedBg);
 *  tabParams.selectedDrawable = new BitmapDrawable(selectedBg);
 *  tabParams.intent = new Intent(this, newClass.class);
 *  tabParams.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
 *	
 *  XUITab tab = new XUITab(this);
 *  tabHost.addTab(tab, tabParams);        	   
 *  tabHost.selectTab(0);
 *  @endcode
 */
public class XUITabHost extends RelativeLayout
{
	private Context context;
	private int totalChildren;
	private int targetView = -1;
	private LocalActivityManager mActivityManager;
	private OnTabSelectedListener mOnTabSelected;
	
	/**
	 * Default constructor
	 * @param context Context
	 */
	public XUITabHost(Context context) 
	{
		super(context);
		this.context = context;
		
		mOnTabSelected = null;
	} 
	
	/**
	 * Default constructor
	 * @param context
	 * @param attributes
	 */
	public XUITabHost(Context context, AttributeSet attributes)
	{
		super(context, attributes);
		this.context = context;			
		
		TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.XUITabHost);		
		targetView = attrs.getResourceId(R.styleable.XUITabHost_targetContainer, -1);
		
		mOnTabSelected = null;
	}	
	
	/**
	 * Sets the tab select listener
	 * @param listener The new listener
	 */
	public void setOnTabSelectedListener(OnTabSelectedListener listener)
	{
		mOnTabSelected = listener;
	}
	
	/**
	 * Calls to set up the tab host
	 * @param activityManager The activity manager from XUITabActivity
	 */
	public void setup(LocalActivityManager activityManager)
	{
		this.mActivityManager = activityManager;
	}
	
	/**
	 * Selects a tab at a specific index
	 * @param index The index of the tab
	 */
	public void selectTab(int index)
	{ 		
		if (((XUITab)this.getChildAt(index)).isSelected) return;
		
		if (mOnTabSelected != null)
		{
			mOnTabSelected.onTabSelect(index);
		}
		
		deselectAll();		
		((XUITab)this.getChildAt(index)).select(mActivityManager, targetView);
	}
	
	/**
	 * Removes all the tabs
	 */
	public void removeAllTabs()
	{
		this.removeAllViews();
	}
	
	/**
	 * Deselects all the tabs
	 */
	public void deselectAll()
	{
		for (int childCount = 0; childCount < totalChildren; childCount++)
		{
			XUITab tab = (XUITab)this.getChildAt(childCount);
			tab.deselect();
		}
	}
	
	/**
	 * Adds a tab to the host
	 * @param tab The new tab to add
	 */
	public void addTab(XUITab tab)
	{			
		this.addView(tab);		
		totalChildren = this.getChildCount();		

		//	Add the onclick for the intent
		tab.setOnClickListener(new customTabClickListener(totalChildren - 1));	
	}
	
	/**
	 * Adds a tab to the host
	 * @param tab The new tab to add
	 * @param params The tab's params
	 */
	public void addTab(XUITab tab, XUITabParams params)
	{
		tab.setParams(params);				
		this.addView(tab);		
		totalChildren = this.getChildCount();		

		//	Add the onclick for the intent
		tab.setOnClickListener(new customTabClickListener(totalChildren - 1));	
	}
	
	/**
	 * Adds a tab to the host
	 * @param tab The new tab to add
	 * @param params The tab's params
	 * @param position The position to put the tab
	 */
	public void addTab(XUITab tab, XUITabParams params, int position)
	{
		tab.setParams(params);				
		this.addView(tab, position);		
		totalChildren = this.getChildCount();		

		//	Add the onclick for the intent
		tab.setOnClickListener(new customTabClickListener(position));
	}
	
	private class customTabClickListener implements OnClickListener
	{
		private int index;
		
		public customTabClickListener(int index)
		{
			this.index = index;
		}
		
		public void onClick(View view)
		{	
			if (((XUITab)view).isSelected) return;
			
			if (mOnTabSelected != null)
			{
				mOnTabSelected.onTabSelect(index);
			}
			
			deselectAll();								
	        ((XUITab)view).select(mActivityManager, targetView);		       
		}	
	};
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		
		updateLayout();
	}		
	
	/**
	 * Updates the layout of the view
	 */
	private void updateLayout()
	{
		totalChildren = this.getChildCount();		
		
		//	Get how much width is left in the container for the tabs
		int widthLeft = this.getMeasuredWidth();
		int currentXPos = 0;				
		
		for (int childCount = 0; childCount < totalChildren; childCount++)
		{			
			XUITab child = (XUITab)this.getChildAt(childCount);	
			LayoutParams childLayout = (LayoutParams)child.getLayoutParams();	
			int height = childLayout.height < 0 ? this.getHeight() : childLayout.height;					
			int tabWidth = 0;
			
			//	If the width is set to fill parent or wrap content
			if (childLayout.width < 0)
			{							
				//	get how many tabs are left and devide the space equally
				tabWidth = widthLeft / (totalChildren - childCount);						
			}
			else
			{		
				tabWidth = childLayout.width;
			}		
			
			child.layout(currentXPos + childLayout.leftMargin, 0, tabWidth + (currentXPos + childLayout.leftMargin), height);
			currentXPos += tabWidth;
			widthLeft -= tabWidth;	
			
			//	Calculate the gravity of the view
			switch (child.getParams().gravity)
			{
				case Gravity.CENTER:
				{ 
					LinearLayout tabContainer = (LinearLayout)child.findViewById(R.id.tabInsides);
					int mWidth = tabContainer.getWidth();
					int mHeight = tabContainer.getHeight();					
					int marginLeft = (tabWidth - mWidth) / 2;
					int marginTop = (height - mHeight) / 2;
					
					tabContainer.layout(marginLeft, marginTop, marginLeft + mWidth, marginTop + mHeight);
					
					break;
				}
			}
		}
	}
	
	/**
	 * @brief Listener for tab selection
	 */
	public interface OnTabSelectedListener
	{
		/**
		 * Called when a tab is selected
		 * @param tabIndex The index of the selected tab
		 */
		public void onTabSelect(int tabIndex);
	}
}