/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import x.type.ItemList;
import x.ui.R;
import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
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
 *  
 *  If you are using fragments instead of Activities, you need to extend FragmentActivity and call setup with getSupportFragmentManager();
 *  @code
 *  tabHost.setup(getSupportFragmentManager());
 *  @endcode
 */
public class XUITabHost extends RelativeLayout
{
	private Context mContext;
	private int mTotalChildren;
	private int mTargetView = -1;
	private LocalActivityManager mActivityManager;
	private FragmentManager mFragmentManager;
	private OnTabSelectedListener mOnTabSelected;
	private Animation mContentAnimation;
	private Fragment lastFragment = null;
	
	/**
	 * Default constructor
	 * @param context Context
	 */
	public XUITabHost(Context context) 
	{
		super(context);
		this.mContext = context;
		
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
		this.mContext = context;			
		
		TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.XUITabHost);		
		mTargetView = attrs.getResourceId(R.styleable.XUITabHost_targetContainer, -1);
		
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
	 * @deprecated Tabbing with Activities is depreicated, use Fragments {@link Fragment}
	 */
	@Deprecated public void setup(LocalActivityManager activityManager)
	{
		if (mFragmentManager != null)
		{
			throw new IllegalArgumentException("Setup has already been called before with FragmentManager");
		}
		
		this.mActivityManager = activityManager;		
	}
	
	/**
	 * Calls to set up the tab host
	 * @param fragmentManager The activity manager from FragmentActivity
	 */
	public void setup(FragmentManager fragmentManager)
	{
		if (mActivityManager != null)
		{
			throw new IllegalArgumentException("Setup has already been called before with LocalActivityManager");
		}
		
		this.mFragmentManager = fragmentManager;		
	}
	
	/**
	 * Selects a tab at a specific index
	 * @param index The index of the tab
	 */
	public void selectTab(int index)
	{ 						
		XUITab tab = (XUITab)getChildAt(index);
		tab.setSoundEffectsEnabled(false);
		tab.performClick();		
		tab.setSoundEffectsEnabled(true);
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
		for (int childCount = 0; childCount < mTotalChildren; childCount++)
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
		tab.setFocusable(false);
		tab.setFocusableInTouchMode(false);
		
		this.addView(tab);		
		
		mTotalChildren = this.getChildCount();		

		//	Add the onclick for the intent
		tab.setOnClickListener(new CustomTabClickListener(mTotalChildren - 1));	
	}
	
	/**
	 * Adds a tab to the host
	 * @param tab The new tab to add
	 * @param params The tab's params
	 */
	public void addTab(XUITab tab, XUITabParams params)
	{
		tab.setFocusable(false);
		tab.setFocusableInTouchMode(false);
		
		tab.setParams(params);				
		this.addView(tab);		
		mTotalChildren = this.getChildCount();		

		//	Add the onclick for the intent
		tab.setOnClickListener(new CustomTabClickListener(mTotalChildren - 1));	
	}
	
	/**
	 * Adds a tab to the host
	 * @param tab The new tab to add
	 * @param params The tab's params
	 * @param position The position to put the tab
	 */
	public void addTab(XUITab tab, XUITabParams params, int position)
	{
		tab.setFocusable(false);
		tab.setFocusableInTouchMode(false);
		
		tab.setParams(params);				
		this.addView(tab, position);		
		mTotalChildren = this.getChildCount();		

		//	Add the onclick for the intent
		tab.setOnClickListener(new CustomTabClickListener(position));
	}
	
	/**
	 * Gets the total tab count in the tab host
	 * @return The total count of children
	 */
	public int getTabCount()
	{
		return mTotalChildren;
	}
	
	/**
	 * @brief Custom tab click listener which accepts index of tab 
	 */
	private class CustomTabClickListener implements OnClickListener
	{
		private int index;		
		
		/**
		 * Default constructor
		 * @param index The index of the tab that was clicked
		 */
		public CustomTabClickListener(int index)
		{
			this.index = index;
		}
				
		/**
		 * Called when the view has been clicked
		 * @param view The view that was clicked
		 */
		public void onClick(View view)
		{				
			if (((XUITab)view).isSelected()) return; 
			
			if (mContentAnimation != null)
			{
				((XUITab)view).setAnimation(mContentAnimation);
			}
			
			deselectAll();								
	        
			if (mFragmentManager != null)
			{
				lastFragment = ((XUITab)view).select(mFragmentManager, lastFragment, mTargetView);
			}
			else
			{
				((XUITab)view).select(mActivityManager, mTargetView);
			}
	        
	        if (mOnTabSelected != null)
			{
				mOnTabSelected.onTabSelect(index);
			}		         
		}	
	};
	
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
		
		//if (changed)
		{
			updateLayout();
		}
	}		
	
	/**
	 * Updates the layout of the view
	 */
	private void updateLayout()
	{
		mTotalChildren = this.getChildCount();		
		
		//	Get how much width is left in the container for the tabs
		int widthLeft = this.getMeasuredWidth();
		int currentXPos = 0;			
			
		for (int childCount = 0; childCount < mTotalChildren; childCount++)
		{			
			XUITab child = (XUITab)this.getChildAt(childCount);	
			LayoutParams childLayout = (LayoutParams)child.getLayoutParams();	
			int tabHeight = childLayout.height < 0 ? this.getHeight() : childLayout.height;					
			int tabWidth = 0;
			
			//	If the width is set to fill parent or wrap content
			if (childLayout.width < 0)
			{							
				//	get how many tabs are left and devide the space equally
				tabWidth = widthLeft / (mTotalChildren - childCount);						
			}
			else
			{		
				tabWidth = childLayout.width;
			}		
			
			child.layout(currentXPos + childLayout.leftMargin, 0, tabWidth + (currentXPos + childLayout.leftMargin), tabHeight);
			currentXPos += tabWidth;
			widthLeft -= tabWidth;	
			 
			int mWidth = 0, mHeight = 0, marginLeft = 0, marginTop = 0;
			
			LinearLayout tabContainer = (LinearLayout)child.findViewById(R.id.tabInsides);
			mWidth = tabContainer.getWidth();
			mHeight = tabContainer.getHeight();
			
			//	Center gravity
			if ((Gravity.CENTER & child.getParams().gravity) == Gravity.CENTER)
			{			
				if ((Gravity.CENTER_HORIZONTAL & child.getParams().gravity) == Gravity.CENTER_HORIZONTAL)
				{
					marginLeft = (int)(((double)tabWidth - (double)mWidth) / 2.0);
				}
				
				if ((Gravity.CENTER_VERTICAL & child.getParams().gravity) == Gravity.CENTER_VERTICAL)
				{
					marginTop = (int)(((double)tabHeight - (double)mHeight) / 2.0);
				}						
			}	
			 
			//	Left gravity
			if ((Gravity.LEFT & child.getParams().gravity) == Gravity.LEFT)
			{												
				marginLeft = 0;
			}
			
			//	Right gravity
			if ((Gravity.RIGHT & child.getParams().gravity) == Gravity.RIGHT)
			{
				marginLeft = (tabWidth - mWidth);
			}
			
			//	Top gravity
			if ((Gravity.TOP & child.getParams().gravity) == Gravity.TOP)
			{
				marginTop = 3;
			}
			
			//	Bottom gravity
			if ((Gravity.BOTTOM & child.getParams().gravity) == Gravity.BOTTOM)
			{
				marginTop = (tabHeight - mHeight) - 3;
			}
			
			tabContainer.layout(marginLeft, marginTop, marginLeft + mWidth, marginTop + mHeight);
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