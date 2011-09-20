package x.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
	private XUITitleButtonHost mButtonHost;
	
	public XUITitleBar(Context context)
	{
		super(context);
		mContext = context;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		init();
	}
	
	public XUITitleBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);	 
		mContext = context;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		TypedArray a = mContext.obtainStyledAttributes(R.styleable.XUITitleBar);
		mTitleText = a.getString(R.styleable.XUITitleBar_title);
		
		init();
	}

	private void init()
	{
		mLayout = (RelativeLayout)mLayoutInflater.inflate(R.layout.titlebar, this, true);
		mLabel = (TextView)mLayout.findViewById(R.id.label);
		mButtonHost = ((XUITitleButtonHost)findViewById(R.id.titlebar_buttons));
		
		setTitleText(mTitleText);
	}
	
	public void setTitleText(String text)
	{
		mTitleText = text;
		mLabel.setText(mTitleText);
	}
	
	public void setTitleButtons(XUITitleButtonHost buttonHost)
	{
		removeView(mButtonHost);
		
		mButtonHost = buttonHost;
		addView(mButtonHost);		
	}

	public void addButton(XUITitleButton button)
	{
		mButtonHost.addButton(button);
	}

	public void addButtons(XUITitleButton... button)
	{
		mButtonHost.addButtons(button);
	}

	public void removeButton(int index)
	{
		mButtonHost.removeButton(index);
	}

	public void removeButton(XUITitleButton button)
	{
		mButtonHost.removeButton(button);
	}

	public void setButtons(XUITitleButton... button)
	{
		mButtonHost.setButtons(button);
	}		
}
