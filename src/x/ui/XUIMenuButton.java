/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.lang.reflect.Method;

import x.lib.Debug;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @brief The menu buttons to be used with XUIMenuButtonGroup. 
 * @breif The menu button can only have a maximum of 2 views (Any more and the parser will ignore them), the first view
 * @breif must be a TextView (the "label") the second  view can be anything.
 *  
 * Example XML Code (Note to make a button clickable, you must declare it clickable by using android:clickable="true")
 * @code
 * <x.ui.XUIMenuButton
 *		android:layout_width="fill_parent" 
 *		android:layout_height="wrap_content"
 *		android:id="@+id/town"
 *		android:onClick="townClick"  
 *		android:clickable="true"
 *	>
 *		<TextView
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content"
 *			android:text="Town / City"    					
 *		/>
 *		<TextView
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content" 
 *			android:text=""
 *			android:textSize="16dp"
 *		/>
 * </x.ui.XUIMenuButton>
 * @endcode
 */
public class XUIMenuButton extends LinearLayout
{
	private Context mContext;
	private ViewGroup mLayoutView;
	private LayoutInflater mLayoutInflater;
	private int mChildCount = 0;
	private ViewGroup mLayout;
	private ImageView mImageView;
	private TextView mLabel;
	private View mContentView;
	private boolean mSetOnClickListener;
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUIMenuButton(Context context)
	{
		super(context);		
		
		this.mContext = context;

		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		mLayoutView = (ViewGroup)mLayoutInflater.inflate(R.layout.xui_menu_button, this);
		mLayout = (ViewGroup)mLayoutView;
		
		init();
	}
	
	/** 
	 * Default Constructor
	 * @param context 
	 * @param attrs 
	 */ 
	public XUIMenuButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
				
		this.mContext = context; 

		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
			 
		init();
	}	 
		
	/**
	 * Override of the padding method
	 * @param l Left padding
	 * @param t Top padding
	 * @param r Right padding
	 * @param b Bottom padding
	 */
	public void setMenuPadding(int l, int t, int r, int b)
	{
		mLayout.getChildAt(0).setPadding(l, t, r, b);
	}
	
	private void init()
	{		
		//	Only set the onlick listener if it hasn't already		
		if (!mSetOnClickListener && this.isClickable())
		{			
			OnClickListener l = new OnClickListener()
			{			
				public void onClick(View v)
				{
					View input = ((LinearLayout)mLayout.findViewById(R.id.input_container)).getChildAt(0);
					input.requestFocus();
					input.requestFocusFromTouch(); 
					
					if (input instanceof EditText)
					{
						EditText t = ((EditText)input);
						t.requestFocus();
						t.requestFocusFromTouch();
						InputMethodManager m = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);						        
				       	m.showSoftInput(t, 0);		 
					}
					else if (input instanceof CheckBox)
					{
						((CheckBox)input).toggle();
					}
					else if (input instanceof XUICheckBox)
					{
						((XUICheckBox)input).toggle();	
					}				
				}
			};
			 
			this.setOnClickListener(l);
		}
	}
	
	@Override
	public void setClickable(boolean clickable)
	{		
		super.setClickable(clickable);
		init();
	};
	
	@Override
	public void setOnClickListener(OnClickListener l)
	{	
		mSetOnClickListener = true;
		
		super.setOnClickListener(l);
	}
	
	/**
	 * Sets the label text
	 * @param text The new text for the label
	 */
	public void setLabel(String text)
	{
		int childCount = ((LinearLayout)mLayout.findViewById(R.id.label)).getChildCount();	
		ColorStateList list = getResources().getColorStateList(R.drawable.button_group_text);
		
		if (childCount < 1)
		{
			TextView t = new TextView(mContext);
			t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			t.setText(Html.fromHtml("<b>" + text + "</b>"));
			t.setGravity(Gravity.CENTER_VERTICAL);
			t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));	
			t.setTextColor(list);
			t.setDuplicateParentStateEnabled(true);
			((LinearLayout)mLayout.findViewById(R.id.label)).addView(t);						
		}  
		else
		{
			TextView t = (TextView)((LinearLayout)mLayout.findViewById(R.id.label)).getChildAt(0);
			t.setGravity(Gravity.CENTER_VERTICAL);
			t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
			t.setTextColor(list);
			t.setDuplicateParentStateEnabled(true);
			t.setText(text); 	
		}
	}
	
	/**
	 * Sets the input view
	 * @param v The new input view
	 */
	public void setInput(View v)
	{
		mContentView = v;
		
		((LinearLayout)mLayout.findViewById(R.id.input_container)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.input_container)).addView(v);
	} 
	
	/**
	 * Gets the input view
	 * @return The input view
	 */
	public View getInput() 
	{ 
		return mContentView;
	} 

	/**
	 * Sets the background state resource
	 * @param res The drawable resource
	 */
	public void setStateResouces(int res)
	{
		mLayoutView.setBackgroundResource(res);
	} 
	
	/**
	 * Sets the icon in the menu item
	 * @param v The new ImageView
	 */
	public void setIcon(ImageView v)
	{
		mImageView = v;		
		mImageView.setDuplicateParentStateEnabled(true);
		
		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets the icon in the menu item
	 * @param b The new bitmap of the icon
	 */
	public void setIcon(Bitmap b)
	{
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setImageBitmap(b);
		mImageView.setDuplicateParentStateEnabled(true);
		
		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets the icon in the menu item
	 * @param d The new Drawable of the icon
	 */
	public void setIcon(Drawable d)
	{
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundDrawable(d);
		mImageView.setDuplicateParentStateEnabled(true);
		mImageView.setFocusable(true);
		
		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets the icon in the menu item
	 * @param res The new Drawable of the icon
	 */
	public void setIcon(int res)
	{
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setImageResource(res);
		mImageView.setDuplicateParentStateEnabled(true);
		mImageView.setFocusable(true);
		
		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) 
	{	
		super.onLayout(changed, l, t, r, b);
		
		mChildCount = getChildCount();	
	}

	@Override
	protected void onFinishInflate()  
	{	
		super.onFinishInflate();
	
		/**
		 * Possible combinations:
		 * [text]
		 * [image, text]
		 * [text, content]
		 * [image, text, content]
		 */
		if (getChildCount() == 1)
		{
			mLabel = (TextView)getChildAt(0);
		}
		else if (getChildCount() == 2)
		{			
			if (getChildAt(0) instanceof TextView)
			{
				mLabel = (TextView)getChildAt(0);
				mContentView = (View)getChildAt(1);
			}
			else
			{ 
				mImageView = (ImageView)getChildAt(0);
				mLabel = (TextView)getChildAt(1);		
			}
		}
		else if (getChildCount() == 3)
		{
			mImageView = (ImageView)getChildAt(0); 
			mLabel = (TextView)getChildAt(1); 
			mContentView = (View)getChildAt(2); 
		}
		
		Rect padding = new Rect(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
		
		this.detachAllViewsFromParent(); 
		
		ColorStateList list = getResources().getColorStateList(R.drawable.button_group_text);

		mLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mLabel.setText(Html.fromHtml("<b>" + mLabel.getText() + "</b>"));		
		mLabel.setTextColor(list);
		mLabel.setDuplicateParentStateEnabled(true);
		
		mLayoutView = (ViewGroup)mLayoutInflater.inflate(R.layout.xui_menu_button, this, true);
		mLayout = (ViewGroup)((LinearLayout)mLayoutView).getChildAt(0);
		
		setMenuPadding(padding.left, padding.top, padding.right, padding.bottom);
		
		if (mLabel != null)
		{						 
			((LinearLayout)mLayoutView.findViewById(R.id.label)).addView(mLabel);			
		}
		 
		if (mContentView != null)
		{			
			((LinearLayout)mLayout.findViewById(R.id.input_container)).addView(mContentView);
		}
		
		if (mImageView != null)
		{
			((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);		
			((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
		}
	}
}