/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

import x.lib.Debug;
import x.type.ItemList;
import x.util.StringUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * @brief The title button to be used with XUITitleButtonHost
 * 
 * @code
 * 	<x.ui.XUITitleButtonHost
 * 		android:layout_width="wrap_content"
 * 		android:layout_height="match_parent"
 * 	>
 * 		<x.ui.XUITitleButton
 * 			android:layout_height="wrap_content"
 * 			android:layout_width="wrap_content"
 * 			android:background="@drawable/button1"
 * 		/>
 * 	</x.ui.XUITitleButtonHost>
 * @endcode
 */
public class XUITitleButton extends ImageButton implements Serializable
{
	private String onClickStr;
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
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XUITitleButton);		
		onClickStr = a.getString(R.styleable.XUITitleButton_onClick);
		
		if (onClickStr != null && onClickStr.length() > 0)
		{			
			setOnClickListener(new OnClickListener()
			{				
				public void onClick(View v)
				{
					try
					{
						ItemList<String> params = new ItemList<String>(onClickStr.split("\\."));
						String method = params.get(params.size() - 1);						
						params.remove(params.size() - 1);						
						
						Class c = Class.forName(StringUtils.join(params, "."));
						Method m = c.getMethod(method);
						m.invoke(this);
					} 
					catch (Exception e) 
					{
						throw new IllegalArgumentException("No method found");
					}
				}
			});			
		}
	}
	
	/**
	 * Sets the buttons drawable image
	 * @param d The drawable of the button
	 */
	@Override public void setBackgroundDrawable(Drawable d)
	{
		setImageDrawable(d);
	}
	
	/**
	 * Sets the background image from a resource id
	 * @param resid The resource id of the image
	 */
	@Override public void setBackgroundResource(int resid)
	{
		setImageResource(resid);
	}
	
	/**
	 * Sets the image resource of the button
	 * @param resId The resource id of the button
	 */
	@Override public void setImageResource(int resId)
	{
		setImage(resId);
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
	
	/**
	 * Gets the current set image id
	 * @return The id of the image resource
	 */
	public int getImageId()
	{
		return mImageId;
	}
}
