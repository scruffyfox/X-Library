/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import x.ui.R;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *  @brief Tab class to be used with XUITabHost
 * 
 *  Creating tabs in Code
 *  @code
 *  XUITabParams tabParams = new XUITabParams();
 *  tabParams.selectedIcon = selectedIcon;
 *  tabParams.deselectedIcon = deselectedIcon; 
 *  tabParams.layoutParams.width = 200;     	        	
 *  tabParams.deselectedDrawable = new BitmapDrawable(deselectedBg);
 *  tabParams.selectedDrawable = new BitmapDrawable(selectedBg);
 *  tabParams.intent = new Intent(this, newClass.class);
 *  tabParams.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
 *	
 *  XUITab tab = new XUITab(this, tabParams);
 *  @endcode
 *  
 *  @todo Add support to XML inflating tabs
 */
public class XUITab extends RelativeLayout 
{
	private boolean isSelected = false;
	protected Context context;		
	
	//	View Elements
	private ImageView imageIcon;
	private TextView tabText;
	private XUITabParams params;
	private ViewGroup tabView = null;
	private ViewGroup mContentView;
	private OnPostLayoutListener mOnPostLayout;
	private OnTabSelectedListener mOnTabSelectedListener;
	private Animation mAnimation;
	private View mLaunchedView;	
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUITab(Context context) 
	{
		super(context);
		this.context = context;				
		
		params = new XUITabParams();
		params.tabText = new XUITabParams.Option("");
		params.layoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
		
		init();
	}
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param params The tab parameters
	 */
	public XUITab(Context context, XUITabParams params) 
	{
		super(context);
		this.context = context;						
		this.params = params;
		
		init();
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUITab(Context context, AttributeSet attributes) 
	{
		super(context, attributes);
		this.context = context;		
	}		
	
	/**
	 * Called when the view has finished loading in the children
	 */
	@Override protected void onFinishInflate()
	{
		super.onFinishInflate();	
						
		init();
	}
	
	/**
	 * Sets the tab with new parameters
	 * @param params The new parameters
	 */
	public void setParams(XUITabParams params)
	{
		this.params = params;
		updateSettings();
	}	
	
	/**
	 * Gets the tab's parameters
	 * @return The tab's parameters
	 */
	public XUITabParams getParams()
	{
		return this.params;
	}	
		
	/**
	 * Selects the tab
	 * @param activityManager The tabhost's activity manager
	 * @param targetView The target container
	 */
	public void select(LocalActivityManager activityManager, int targetView)
	{
		if (isSelected) return;
		
		//	Start the new intent
		if (activityManager == null)
		{
			Log.e("XUITab", "No activity manager");
			select();
			return;
		}
		
		if (targetView < 1)
		{
			Log.e("XUITab", "No Target view"); 
			select();
			return;
		}
		
		if (this.params.intent == null)
		{
			Log.e("XUITab", "No Intent for tab");
			select();
			return;
		}
			
		Activity a = (Activity)context;				
		ViewGroup mContentView = (ViewGroup)a.findViewById(targetView);
        Intent mIntent = this.params.intent;	       
                       
        Window w = activityManager.startActivity("" + System.currentTimeMillis(), mIntent);
        View mLaunchedView = w != null ? w.getDecorView() : null;	
        
        mContentView.removeAllViewsInLayout();
        
        if (mLaunchedView != null) 
        {
            mLaunchedView.setVisibility(View.VISIBLE);
            mLaunchedView.setFocusableInTouchMode(true);
            ((ViewGroup)mLaunchedView).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        } 
                
        mContentView.addView(mLaunchedView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mContentView.requestFocus();        
        
        select();
	}		
	
	/**
	 * Selects the tab using the fragment manager
	 * @param fragmentManager The FragmentManager used for initiating fragment views
	 * @param targetView The target container
	 */
	public Fragment select(FragmentManager fragmentManager, Fragment lastFragment, int targetView)
	{
		Fragment retLastFragment = null;
		
		if (isSelected) return lastFragment;
		
		//	Start the new intent
		if (fragmentManager == null)
		{
			Log.e("XUITab", "No fragment manager");
			select();
			return lastFragment;
		}
		
		if (targetView < 1)
		{
			Log.e("XUITab", "No Target view"); 
			select();
			return lastFragment;
		}
		
		if (this.params.intent == null)
		{
			Log.e("XUITab", "No Intent for tab");
			select();
			return lastFragment;
		}
			
		try
		{						
			Class<?> fragmentClass = Class.forName(this.params.intent.getComponent().getClassName());			
			Fragment clazz = Fragment.instantiate(context, fragmentClass.getName());
			FragmentTransaction ft = fragmentManager.beginTransaction();
			
//			if (lastFragment != null)
//			{
//				ft.detach(lastFragment);	
//				((ViewGroup)((Activity)context).findViewById(targetView)).removeAllViewsInLayout();
//			}		    				
					    	
	    	ft.replace(targetView, clazz);
	    	ft.commit(); 	    		    	
	    	
	    	retLastFragment = clazz;
	    	
	    	fragmentManager.executePendingTransactions();
	    	
	    	((ViewGroup)((Activity)context).findViewById(targetView)).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	    	((ViewGroup)((Activity)context).findViewById(targetView)).requestFocus();	    	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        
        select();

        return retLastFragment;
	}	
	
	/**
	 * Selects the tab
	 */
	public void select()
	{		
		isSelected = true;
		
		if (this.params.tabIcon.selected > -1) 
		{
			imageIcon.setVisibility(View.VISIBLE);
			imageIcon.setImageDrawable(getResources().getDrawable(this.params.tabIcon.selected));
		}
		
		tabText.setContentDescription(this.params.tabText.deselected != null ? this.params.tabText.deselected : this.params.tabText.selected);
		tabText.setText(this.params.tabText.selected);
		tabText.setTextColor(this.params.tabTextColor.selected);		
		this.setBackgroundDrawable(this.params.tabBackground.selected);	
		
		if (mOnTabSelectedListener != null)
		{
			mOnTabSelectedListener.onTabSelected(this);
		}
	}
	
	/**
	 * Deselects the tab
	 */
	public void deselect()
	{
		isSelected = false;  
		
		if (this.params.tabIcon.deselected == null || this.params.tabIcon.deselected > -1)
		{
			imageIcon.setVisibility(View.VISIBLE);
			imageIcon.setImageDrawable(getResources().getDrawable(this.params.tabIcon.deselected != null ? this.params.tabIcon.deselected : this.params.tabIcon.selected));
		}
		
		tabText.setContentDescription(this.params.tabText.deselected != null ? this.params.tabText.deselected : this.params.tabText.selected);
		tabText.setText(this.params.tabText.deselected != null ? this.params.tabText.deselected : this.params.tabText.selected);
		tabText.setTextColor(this.params.tabTextColor.deselected != null ? this.params.tabTextColor.deselected : this.params.tabTextColor.selected);
		this.setBackgroundDrawable(this.params.tabBackground.deselected != null ? this.params.tabBackground.deselected : this.params.tabBackground.selected);		
	}
	
	/**
	 * Initiliazes the view
	 */
	private void init()
	{			
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tabView = (ViewGroup)inflater.inflate(R.layout.xuitab, this);
						
		imageIcon = (ImageView)tabView.findViewById(R.id.tabIcon);
		tabText = (TextView)tabView.findViewById(R.id.tabText);		
				
		updateSettings();
	}	
	
	/**
	 * Updates the settings of the view
	 */
	private void updateSettings()
	{			
		tabText.setText(this.params.tabText.selected);
		tabText.setTextSize(this.params.tabTextSize);
		tabText.setContentDescription(this.params.tabText.selected);
		
		LayoutParams lp = new LayoutParams(this.params.layoutParams.width, this.params.layoutParams.height);
		lp.leftMargin = this.params.layoutParams.leftMargin;
		lp.topMargin = this.params.layoutParams.topMargin;
		lp.rightMargin = this.params.layoutParams.rightMargin;
		lp.bottomMargin = this.params.layoutParams.bottomMargin;		
		this.setLayoutParams(lp);	
		
		LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(this.params.iconLayoutParams.width, this.params.iconLayoutParams.height);
		ilp.leftMargin = this.params.iconLayoutParams.leftMargin;
		ilp.topMargin = this.params.iconLayoutParams.topMargin;
		ilp.rightMargin = this.params.iconLayoutParams.rightMargin;
		ilp.bottomMargin = this.params.iconLayoutParams.bottomMargin;		
		this.imageIcon.setLayoutParams(ilp);
			
		LinearLayout tabInsides = ((LinearLayout)tabView.findViewById(R.id.tabInsides));
		tabInsides.setOrientation(this.params.layoutOrientation);		
		tabInsides.setGravity(this.params.gravity);
		
		if (isSelected)
		{
			this.select();
		}
		else 
		{
			this.deselect();
		}
	}
	 
	/**
	 * Gets the intent set to the tab
	 * @return The intent associated with the tab
	 */
	public Intent getIntent()
	{
		return this.params.intent;
	}
	
	/** 
	 * Sets the post layout listener
	 * @param l The new post layout listener
	 */
	public void setOnPostLayout(OnPostLayoutListener l)
	{
		mOnPostLayout = l;
	}
	
	/**
	 * Sets the tab selected listener
	 * @param mOnTabSelectedListener The new tab selected listener
	 */
	public void setOnTabSelectedListener(OnTabSelectedListener mOnTabSelectedListener)
	{
		this.mOnTabSelectedListener = mOnTabSelectedListener;
	}
	
	/**
	 * Is called when the view is being layed out
	 * @param changed If the view has changed or not
	 * @param l The left coordinate
	 * @param t The top coordinate
	 * @param r The right coordinate
	 * @param b The bottom coordinate
	 */
	@Override public void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);					
		
		if (mOnPostLayout != null)
		{
			mOnPostLayout.onPostLayout(this, changed);
		}
	}
	
	/**
	 * Get if the tab is selected or not
	 * @return If the tab is currently selected
	 */
	public boolean isSelected()
	{
		return isSelected;
	}
	
	/**
	 * @brief The post layout listener
	 */
	public interface OnPostLayoutListener
	{
		/** 
		 * Called when the tab has been inflated. Useful for getting the calculated size to manipulate
		 * bitmaps with and set as icons or backgrounds.
		 * @param tab The tab that has been inflated
		 * @param changed If the tab has changed or not
		 */
		public void onPostLayout(XUITab tab, boolean changed);
	}
	
	/**
	 * @brief The listener for when the tab is selected
	 */
	public interface OnTabSelectedListener
	{
		/**
		 * Called when the tab has been selected
		 * @param tab The tab which has been selected
		 */
		public void onTabSelected(XUITab tab);
	}
}
