/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.io.Serializable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * @brief The title button to be used with XUITitleButtonHost
 */
public class XUITitleButton extends ImageButton implements Serializable
{
	private int mImageId;
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUITitleButton(Context context)
	{
		super(context);
	}
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param imageId The image resource of the button
	 */
	public XUITitleButton(Context context, int imageId)
	{
		super(context);
		
		setImage(imageId);		
	}
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param imageId The image resource of the button
	 * @param l Set the onclick listener for the button
	 */
	public XUITitleButton(Context context, int imageId, OnClickListener l)
	{
		super(context);
	
		setOnClickListener(l);
		setImage(imageId);		
	}
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 * @param attrs The attribute set passed via the SAX parser
	 */
	public XUITitleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * Sets the image resource of the button
	 * @param id The id of the drawable
	 */
	public void setImage(int id)
	{
		mImageId = id;
		this.setBackgroundResource(mImageId);
	}	
}
