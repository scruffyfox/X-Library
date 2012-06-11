/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.lib.Debug;
import x.type.TextStyle;
import x.util.StringUtils;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @brief Custom text view class which gives additional support for XML font loading
 * 
 * Example code of loading fonts
 * @code
 * <x.ui.XUITextView
 * 	android:layout_width="wrap_content"
 * 	android:layout_height="wrap_content"
 * 	x:font="arial.ttf"
 * 	x:text_transform="capitalize"
 * />
 * @endcode
 */
public class XUITextView extends TextView
{
	/**
	 * XML Attribute: keeps the text as is typed in
	 */
	public static final int TEXT_TRANSFORM_NORMAL = 0x0;
	/**
	 * XML Attribute: makes the text uppercase
	 */
	public static final int TEXT_TRANSFORM_UPPERCASE = 0x01;
	/**
	 * XML Attribute: makes the text lowercase
	 */
	public static final int TEXT_TRANSFORM_LOWERCASE = 0x10;
	/**
	 * XML Attribute: makes the first letter in every word capital
	 */
	public static final int TEXT_TRANSFORM_CAPITALIZE = 0x100;
	/**
	 * XML Attribute: makes the first letter lower case, then the first letter
	 * in every word after capital
	 */
	public static final int TEXT_TRANSFORM_CAMEL_CASE = 0x1000;
	/**
	 * XML Attribute: makes the first letter in a sentence uppercase
	 */
	public static final int TEXT_TRANSFORM_GRAMATICAL = 0x10000;
	
	private int mTextTransform = TEXT_TRANSFORM_NORMAL;
	private String mFontResource;
	private Context mContext;
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUITextView(Context context)
	{
		super(context);
		
		mContext = context;		
		init();
	}	
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
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
			setText(getText().toString().toUpperCase());
		}
		else if (mTextTransform == TEXT_TRANSFORM_LOWERCASE)
		{			
			setText(getText().toString().toLowerCase());
		}
		else if (mTextTransform == TEXT_TRANSFORM_CAPITALIZE)
		{
			setText(StringUtils.capitalize(getText().toString()));
		}
		else if (mTextTransform == TEXT_TRANSFORM_GRAMATICAL)
		{
			char[] characters = getText().toString().toLowerCase().toCharArray();
			for (int index = 0; index < characters.length; index++)
			{
				if ((int)characters[index] < 123 && (int)characters[index] > 96)
				{
					if (index == 0)
					{
						characters[index] = (char)((int)characters[index] - 32);
					}
					else
					{
						if ((index - 2 > 0 && characters[index - 2] == '.') || (index - 1 > 0 && characters[index - 1] == '\n'))
						{
							characters[index] = (char)((int)characters[index] - 32);
						}											
					}
				}
			}						
			
			setText(characters);
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
	
	@Override protected void onFinishInflate()
	{	
		super.onFinishInflate();
		
		// we need to check if there are any references to @string, @integer, @dimen, @color
		String text = getText().toString();
		
		// find all words starting with m or c, and ends with n or r or s. 
		// RegEx backslash should be escaped with an additional one.
		Pattern p = Pattern.compile("(@(.*?(\\s|$)))");
		Matcher m = p.matcher(text);
		while (m.find()) 
		{
		    String match = m.group().trim();		  
		    String[] id = match.split("[/]");
		    
		    Debug.out(id);
		    
		    int resId = mContext.getResources().getIdentifier(id[1], id[0].replace("@", ""), mContext.getPackageName());
		    if (resId <= 0) continue;
		    
		    String replacement = "";
		    if (id[0].toLowerCase().contains("string"))
		    {
		    	replacement = mContext.getResources().getString(resId);
		    }
		    else if (id[0].toLowerCase().contains("integer"))
		    {
		    	replacement = mContext.getResources().getInteger(resId) + "";
		    }
		    else if (id[0].toLowerCase().contains("dimen"))
		    {
		    	replacement = mContext.getResources().getDimension(resId) + "";
		    }
		    else if (id[0].toLowerCase().contains("color"))
		    {
		    	replacement = mContext.getResources().getColor(resId) + "";
		    }
		    
		    text = text.replace(match, replacement);
		}	    
		
		setText(text);
	}
}
