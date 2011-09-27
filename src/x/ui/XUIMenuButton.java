/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import x.lib.Debug;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	private View mLayoutView;
	private LayoutInflater mLayoutInflater;
	private int mChildCount = 0;
	private ViewGroup mLayout;
	private TextView mLabel;
	private View mContentView;
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUIMenuButton(Context context)
	{
		super(context);		
		
		this.mContext = context;

		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		mLayoutView = mLayoutInflater.inflate(R.layout.xui_menu_button, this);
		mLayout = (ViewGroup)((LinearLayout)mLayoutView).getChildAt(0);	
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
		mLayoutView = mLayoutInflater.inflate(R.layout.xui_menu_button, null);
		mLayout = (ViewGroup)((LinearLayout)mLayoutView).getChildAt(0);
		
		this.setOnClickListener(new OnClickListener()
		{			
			public void onClick(View v)
			{
				View input = ((LinearLayout)mLayout.findViewById(R.id.input_container)).getChildAt(0);
				
				if (input instanceof EditText)
				{
					((EditText)input).requestFocusFromTouch();
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
		});
	}	 
	
	/**
	 * Sets the label text
	 * @param text The new text for the label
	 */
	public void setLabel(String text)
	{
		int childCount = ((LinearLayout)mLayout.findViewById(R.id.label)).getChildCount();
		
		if (childCount < 1)
		{
			TextView t = new TextView(mContext);
			t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			t.setTextColor(0xff352B21);		
			t.setText(Html.fromHtml("<b>" + text + "</b>"));
			
			((LinearLayout)mLayout.findViewById(R.id.label)).addView(t);
		}
		else
		{
			TextView t = (TextView)((LinearLayout)mLayout.findViewById(R.id.label)).getChildAt(0);
			t.setText(text);
		}
	}
	
	/**
	 * Sets the input view
	 * @param v The new input view
	 */
	public void setInput(View v)
	{
		((LinearLayout)mLayout.findViewById(R.id.input_container)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.input_container)).addView(v);
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
		
		mLabel = (TextView)getChildAt(0);
		mContentView = (View)getChildAt(1);
			
		this.removeAllViews();				
		
		mLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mLabel.setTextColor(0xff352B21);		
		mLabel.setText(Html.fromHtml("<b>" + mLabel.getText() + "</b>"));
		
		((LinearLayout)mLayout.findViewById(R.id.label)).addView(mLabel);
		((LinearLayout)mLayout.findViewById(R.id.input_container)).addView(mContentView);		
		
		this.addView(mLayoutView);
	}
		
	/**
	 * Gets the content view
	 * @return The content view
	 */
	public View getContentView()
	{
		return mContentView;
	}
}