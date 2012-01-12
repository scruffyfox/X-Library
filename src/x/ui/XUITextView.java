/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.io.File;
import java.io.InputStream;

import x.lib.Debug;
import x.type.TextStyle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @brief Custom text view class which gives additional support for XML font loading
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
	 * Sets the text style. Using {@link TextStyle} for the attributes.
	 * @param style The new text style 
	 */
	public void setTextStyle(int style)
	{ 
		String preTag = "";
		String endTag = "";
		
		if ((style & TextStyle.BOLD) == style)
		{
			preTag += "<b>";
			endTag += "</b>";
		}

		if ((style & TextStyle.UNDERLINE) == style)
		{			
			preTag += "<u>";
			endTag += "</u>";
		}
		
		if ((style & TextStyle.ITALIC) == style)
		{
			preTag += "<i>";
			endTag += "</i>";
		}
				
		setText(Html.fromHtml(preTag + getText() + endTag));
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
