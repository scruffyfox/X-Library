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
import x.util.StringUtils;

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
	public static final int TEXT_TRANSFORM_NORMAL = 0x0;
	public static final int TEXT_TRANSFORM_UPPERCASE = 0x01;
	public static final int TEXT_TRANSFORM_LOWERCASE = 0x10;
	public static final int TEXT_TRANSFORM_CAPITALIZE = 0x100;
	public static final int TEXT_TRANSFORM_CAMEL_CASE = 0x1000;
	
	private int mTextTransform = TEXT_TRANSFORM_NORMAL;
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
		mTextTransform = attrs.getInteger(R.styleable.XUITextView_text_transform, TEXT_TRANSFORM_NORMAL);
		
		attrs.recycle();
		
		mContext = context;		
		init();
	}	
	
	/**
	 * Initialize the view
	 */
	private void init()
	{
		updateText();
		
		if (mFontResource != null)
		{
			setFont(mFontResource);
		}				
	}	
	
	/**
	 * Updates the text in the view
	 */
	private void updateText()
	{
		if (mTextTransform == TEXT_TRANSFORM_UPPERCASE)
		{
			char[] characters = getText().toString().toCharArray();
			for (int index = 0; index < characters.length; index++)
			{
				if ((int)characters[index] < 123 && (int)characters[index] > 96)
				{
					characters[index] = (char)((int)characters[index] - 32);
				}
			}
			
			setText(characters);
		}
		else if (mTextTransform == TEXT_TRANSFORM_LOWERCASE)
		{
			char[] characters = getText().toString().toCharArray();
			for (int index = 0; index < characters.length; index++)
			{
				if ((int)characters[index] < 91 && (int)characters[index] > 64)
				{
					characters[index] = (char)((int)characters[index] + 32);
				}
			}
			
			setText(characters);
		}
		else if (mTextTransform == TEXT_TRANSFORM_CAPITALIZE)
		{
			setText(StringUtils.capitalize(getText().toString()));
		}
	}
	
	/**
	 * Sets the text transformation in the view
	 * @param transform The transformation
	 */
	public void setTextTransform(int transform)
	{
		mTextTransform = transform;
		updateText();
	}
	
	/**
	 * Gets the text transformation
	 * @return The text transformation
	 */
	public int getTextTransform()
	{
		return mTextTransform;
	}

	/**
	 * Sets the text of the view using a character arra
	 * @param charArray The array to use
	 */
	public void setText(char[] charArray)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(charArray);
		setText((CharSequence)builder.toString());
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
