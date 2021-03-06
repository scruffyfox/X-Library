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
import android.text.TextUtils.TruncateAt;
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
 * The forced height of the titlebar is 68px relative to the screen size (46dp) for HDPI
 * 
 * @code
 *	<x.ui.XUITitleBar
 * 		android:layout_width="fill_parent"
 * 		android:layout_height="wrap_content"
 * 		x:labelIcon="@drawable/logo"
 * 		x:label="Home Screen"
 * 		x:gravity="left"
 * 		android:background="#ffffffff"
 *	/>
 * @endcode
 * 
 * TODO: So far only accepts XUITitleButtonHost as a child, and only inflates to the right button host.
 * TODO: Add xml inflate support for the left button host
 */
public class XUITitleBar extends RelativeLayout
{
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private RelativeLayout mLayout;
	private XUITitleButtonHost mRightButtonHost;
	private XUITitleButtonHost mLeftButtonHost;
	private TextView mLabel;
	private ImageView mLabelImage;
	private String mTitleText = "";
	private int mTitleIconDrawable = -1;	
	private int mGravity;
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUITitleBar(Context context)
	{
		super(context);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		init();
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
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
				
		mRightButtonHost = ((XUITitleButtonHost)mLayout.findViewById(R.id.titlebar_buttons_right));
		mLeftButtonHost = ((XUITitleButtonHost)mLayout.findViewById(R.id.titlebar_buttons_left));
		mLabel = (TextView)mLayout.findViewById(R.id.titlebar_label); 
		
		mLabel.setSingleLine(true);
		
		setTitleText(mTitleText); 
		setTitleGravity(mGravity);		
	}
	
	/**
	 * Set the title bar text label
	 * @param text The new text for the titlebar
	 */
	public void setTitle(String text)
	{
		mTitleText = text;
		mLabel.setText(mTitleText); 
		mLabel.setContentDescription(mTitleText);
		mLabel.setFocusable(true);					
	} 
	
	/**
	 * @deprecated Use setTitle(String text) instead
	 * Set the title bar text label
	 * @param text The new text for the titlebar
	 */
	@Deprecated public void setTitleText(String text)
	{
		mTitleText = text;
		mLabel.setText(mTitleText); 
		mLabel.setContentDescription(mTitleText);
		mLabel.setFocusable(true);					
	} 
	
	/**
	 * Sets the ellipsize for the title text
	 * @param pos The position for the ellipsis
	 */
	public void setEllipsize(TruncateAt pos)
	{
		mLabel.setEllipsize(pos);
	}
	
	/**
	 * Gets the ellipsize for the title text
	 * @return
	 */
	public TruncateAt getEllipsize()
	{
		return mLabel.getEllipsize();
	}
	
	/**
	 * Sets the title bar text label
	 * @param textId The id of the text resource to use
	 */
	public void setTitle(int textId)
	{
		setTitleText(mContext.getResources().getString(textId));
	}
	
	/**
	 * @deprecated use setTitle(int textId) instead
	 * Sets the title bar text label
	 * @param textId The id of the text resource to use
	 */
	@Deprecated public void setTitleText(int textId)
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
	 * Sets the right titlebar button host
	 * @param buttonHost The new button host
	 */
	public void setTitleButtons(XUITitleButtonHost buttonHost)
	{
		setTitleButtons(buttonHost, Gravity.RIGHT);
	}
	
	/**
	 * Sets the titlebar button host
	 * @param buttonHost The new button host
	 * @param gravity The gravity of the button host. Only accepts Gravity.LEFT or Gravity.RIGHT
	 */
	public void setTitleButtons(XUITitleButtonHost buttonHost, int gravity)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				removeView(mLeftButtonHost);
				
				buttonHost.setGravity(Gravity.LEFT);
				mLeftButtonHost = buttonHost;
				addView(mLeftButtonHost, 0);
				
				break;
			}
			
			case Gravity.RIGHT:
			{
				removeView(mLeftButtonHost);
				
				buttonHost.setGravity(Gravity.RIGHT);
				mRightButtonHost = buttonHost;
				addView(mRightButtonHost);
				
				break;
			}
		}			
	}

	/**
	 * Adds a button to the button host
	 * @param button The button to add
	 */
	public void addButton(XUITitleButton button)
	{
		addButton(button, Gravity.RIGHT);
	}

	/**
	 * Adds a button to the button host
	 * @param button The button to add
	 * @param gravity The gravity of the button host to use, Either Gravity.LEFT or Gravity.RIGHT
	 */
	public void addButton(XUITitleButton button, int gravity)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				mLeftButtonHost.addButton(button);
				break;
			}
			
			case Gravity.RIGHT:
			{
				mRightButtonHost.addButton(button);
				break;
			}
		}		
	}
	
	/**
	 * Adds buttons to the button host
	 * @param button The buttons to add
	 */
	public void addButtons(XUITitleButton... button)
	{
		addButtons(Gravity.RIGHT, button);
	}
	
	/**
	 * Adds a button to the button host
	 * @param gravity The gravity of the button host to use, Either Gravity.LEFT or Gravity.RIGHT	 
	 * @param button The button to add 
	 */
	public void addButtons(int gravity, XUITitleButton... button)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				mLeftButtonHost.addButtons(button);
				break;
			}
			
			case Gravity.RIGHT:
			{
				mRightButtonHost.addButtons(button);
				break;
			}
		}		
	}

	/**
	 * Removes a button from a specific index
	 * @param index The index of the button to remove
	 */
	public void removeButton(int index)
	{
		mRightButtonHost.removeButton(index);
	}
	
	/**
	 * Removes a button from the specific index
	 * @param index The index of the button to remove
	 * @param gravity The gravity of the button host to use, Either Gravity.LEFT or Gravity.RIGHT
	 */
	public void removeButton(int index, int gravity)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				mLeftButtonHost.removeButton(index);
				break;
			}
			
			case Gravity.RIGHT:
			{
				mRightButtonHost.removeButton(index);
				break;
			}
		}	
	}

	/**
	 * Remove a button from the view
	 * @param button The button to remove
	 */
	public void removeButton(XUITitleButton button)
	{
		removeButton(button, Gravity.RIGHT);
	}
	
	/**
	 * Remove a button from the view
	 * @param button The button to remove
	 * @param gravity The gravity of the button host to use, Either Gravity.LEFT or Gravity.RIGHT
	 */
	public void removeButton(XUITitleButton button, int gravity)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				mLeftButtonHost.removeButton(button);
				break;
			}
			
			case Gravity.RIGHT:
			{
				mRightButtonHost.removeButton(button);
				break;
			}
		}	
	}
	
	/**
	 * Removes all the buttons from both of the button hosts
	 */
	public void removeAllButtons()
	{
		mRightButtonHost.removeAllButtons();
		mLeftButtonHost.removeAllButtons();
	}
	
	/**
	 * Removes all the buttons from the view
	 * @param gravity The gravity of the button host to use, Either Gravity.LEFT or Gravity.RIGHT
	 */
	public void removeAllButtons(int gravity)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				mLeftButtonHost.removeAllButtons();
				break;
			}
			
			case Gravity.RIGHT:
			{
				mRightButtonHost.removeAllButtons();
				break;
			}
		}	
	}

	/**
	 * Sets the buttons in the view
	 * @param button The buttons to set
	 */
	public void setButtons(XUITitleButton... button)
	{
		setButtons(Gravity.RIGHT, button);
	}	
	
	/**
	 * Sets the buttons in the view
	 * @param gravity The gravity of the button host to use, Either Gravity.LEFT or Gravity.RIGHT
	 * @param button The buttons to set 
	 */
	public void setButtons(int gravity, XUITitleButton... button)
	{
		switch (gravity)
		{
			case Gravity.LEFT:
			{
				mLeftButtonHost.setButtons(button);
				break;
			}
			
			case Gravity.RIGHT:
			{
				mRightButtonHost.setButtons(button);
				break;
			}
		}	
	}	
	
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
	}
		
	/**
	 * Measures the view and its children
	 * @param widthMeasureSpec the width spec for the view to calculate its width
	 * @param heightMeasureSpec the height spec for the view to calculate its height
	 */
	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{						
		super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int)mContext.getResources().getDimension(R.dimen.titlebar_size), MeasureSpec.EXACTLY));
	}
	
	/**
	 * Called when the view has finished loading in the children 
	 * to the view
	 */
	@Override protected void onFinishInflate()
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
				
		mRightButtonHost.addButtons(titleButtons);
	}
}
