package x.ui;

import x.lib.Debug;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Title bar class used to template a title bar with/without buttons in Android.
 * 
 * @code
 * 	<x.ui.XUITitleBar
 * 		android:layout_width="fill_parent"
 * 		android:layout_height="wrap_content"
 * 		xui:label="Home Screen"
 * 		android:background
 *	/>
 * @endcode
 * 
 * TODO: Add support for child XUITitleButtonHost in XML 
 */
public class XUITitleBar extends RelativeLayout
{
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private RelativeLayout mLayout;
	private String mTitleText = "";
	private TextView mLabel;
	private int mGravity;
	private XUITitleButtonHost mButtonHost;
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUITitleBar(Context context)
	{
		super(context);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		init();
	}
	
	/**
	 * Default Constructor
	 * @param context
	 * @param attrs
	 */
	public XUITitleBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);	
		
		mContext = context;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.XUITitleBar);
		mTitleText = a.getString(R.styleable.XUITitleBar_title);
		mGravity = a.getInteger(R.styleable.XUITitleBar_gravity, Gravity.LEFT);
		
		init();
	}

	private void init()
	{ 
		mLayout = (RelativeLayout)mLayoutInflater.inflate(R.layout.titlebar, this);
		mLabel = (TextView)findViewById(R.id.titlebar_label);
		mButtonHost = ((XUITitleButtonHost)findViewById(R.id.titlebar_buttons));
						
		setTitleText(mTitleText);
		setTitleGravity(mGravity);
	}
	
	/**
	 * Set the title bar text label
	 * @param text The new text for the titlebar
	 */
	public void setTitleText(String text)
	{
		mTitleText = text;
		mLabel.setText(mTitleText); 
	} 
	
	/**
	 * Set the gravity of the title label
	 * @param gravity The new gravity (Only accepts LEFT, RIGHT and CENTER)
	 */
	public void setTitleGravity(int gravity)
	{
		LayoutParams lp = (LayoutParams)mLabel.getLayoutParams();
		
		if (gravity == Gravity.LEFT)
		{
			lp.leftMargin = 10;
		}
		else if (gravity == Gravity.RIGHT)
		{
			lp.leftMargin = 0;
			lp.rightMargin = 10;
		}
		else if (gravity == Gravity.CENTER)
		{
			lp.leftMargin = 0;
			lp.rightMargin = 0;
		}
		else
		{
			return;
		}
		
		mGravity = gravity;
		mLabel.setGravity(mGravity | Gravity.CENTER_VERTICAL);
	}
	
	/**
	 * Sets the titlebar button host
	 * @param buttonHost The new button host
	 */
	public void setTitleButtons(XUITitleButtonHost buttonHost)
	{
		removeView(mButtonHost);
		
		mButtonHost = buttonHost;
		addView(mButtonHost);		
	}

	/**
	 * Adds a button to the button host
	 * @param button The button to add
	 */
	public void addButton(XUITitleButton button)
	{
		mButtonHost.addButton(button);
	}

	/**
	 * Adds buttons to the button host
	 * @param button The buttons to add
	 */
	public void addButtons(XUITitleButton... button)
	{
		mButtonHost.addButtons(button);
	}

	/**
	 * Removes a button from a specific index
	 * @param index The index of the button to remove
	 */
	public void removeButton(int index)
	{
		mButtonHost.removeButton(index);
	}

	/**
	 * Remove a button from the view
	 * @param button The button to remove
	 */
	public void removeButton(XUITitleButton button)
	{
		mButtonHost.removeButton(button);
	}

	/**
	 * Sets the buttons in the view
	 * @param button The buttons to set
	 */
	public void setButtons(XUITitleButton... button)
	{
		mButtonHost.setButtons(button);
	}		
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{						
		super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int)mContext.getResources().getDimension(R.dimen.titlebar_size), MeasureSpec.EXACTLY));
	}
}
