package x.ui;

import java.io.File;
import java.io.InputStream;

import x.lib.Debug;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom text view class which gives additional support for XML font loading
 * 
 * Example code of loading fonts
 * @code
 * <x.ui.XUITextView
 * 		android:layout_width="wrap_content"
 * 		android:layout_height="wrap_content"
 * 		x:font="arial.ttf"
 * />
 * @endcode
 */
public class XUITextView extends TextView
{
	private String mFontResource;
	private Context mContext;
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUITextView(Context context)
	{
		super(context);
		
		mContext = context;		
		init();
	}	
	
	/**
	 * Default Constructor
	 * @param context
	 * @param attributes
	 */
	public XUITextView(Context context, AttributeSet attributes)
	{
		super(context, attributes);
		
		TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.XUITextView);	
		mFontResource = attrs.getString(R.styleable.XUITextView_font);
		
		attrs.recycle();
		
		mContext = context;		
		init();
	}	
	
	/**
	 * Initialize the view
	 */
	private void init()
	{
		if (mFontResource != null)
		{
			setFont(mFontResource);
		}
	}
	
	/**
	 * Set the font of the text view
	 * @param fontRes The resource to the font file. Must be stored in ASSETS
	 */
	public void setFont(String fontPath)
	{	
		Typeface face = Typeface.createFromAsset(mContext.getAssets(), fontPath);
		this.setTypeface(face);
	}
}
