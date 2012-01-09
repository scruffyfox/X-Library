/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.util.ArrayList;

import x.lib.Debug;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @brief Title bar class used to template a title bar with/without buttons in Android.
 * You can override the text style in the title bar by overriding @style/titlebar_label in the style XML
 * 
 * @code
 * 	<x.ui.XUITitleBar
 * 		android:layout_width="fill_parent"
 * 		android:layout_height="wrap_content"
 * 		x:labelIcon="@drawable/logo"
 * 		x:label="Home Screen"
 * 		x:gravity="left"
 * 		android:background="#ffffffff"
 *	/>
 * @endcode
 * 
 * TODO: So far only accepts XUITitleButtonHost as a child.
 */
public class XUITitleBar extends RelativeLayout
{
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private RelativeLayout mLayout;
	private XUITitleButtonHost mButtonHost;
	private TextView mLabel;
	private ImageView mLabelImage;
	private String mTitleText = "";
	private int mTitleIconDrawable = -1;	
	private int mGravity;
	
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
		mTitleText = a.getString(R.styleable.XUITitleBar_label);
		mTitleIconDrawable = a.getResourceId(R.styleable.XUITitleBar_labelIcon, -1);
		mGravity = a.getInteger(R.styleable.XUITitleBar_gravity, Gravity.LEFT);
		
		init(); 
	}
	
	/**
	 * Initializes the view
	 */
	private void init()
	{ 
		mLayout = (RelativeLayout)mLayoutInflater.inflate(R.layout.xui_titlebar, this);
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
		mLabel.setContentDescription(mTitleText);
		mLabel.setFocusable(true);
	} 
	
	/**
	 * Sets the title bar text label
	 * @param textId The id of the text resource to use
	 */
	public void setTitleText(int textId)
	{
		setTitleText(mContext.getResources().getString(textId));				
	}
	
	/**
	 * Sets the title bar text size
	 * @param size The new text size
	 */
	public void setTitleTextSize(int size)
	{
		mLabel.setTextSize(size);
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
		
		buttonHost.setGravity(Gravity.RIGHT);
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
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		
		ArrayList<XUITitleButton> titleButtons = new ArrayList<XUITitleButton>();
		
		int childCount = getChildCount();
		for (int index = 0; index < childCount; index++)
		{
			if (getChildAt(index) instanceof XUITitleButtonHost)
			{
				int buttonCount = ((ViewGroup)getChildAt(index)).getChildCount();
				ViewGroup buttons = ((ViewGroup)getChildAt(index));
				for (int childIndex = 0; childIndex < buttonCount; childIndex++)
				{
					titleButtons.add((XUITitleButton)buttons.getChildAt(childIndex));
				}								
				
				buttons.removeAllViews();
			}
		}
				
		mButtonHost.addButtons(titleButtons);
	}
}
