package x.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Checkable;

/**
 * @brief This is a custom version of CheckBox
 */
public class XUICheckBox extends ImageView implements Checkable
{
	private boolean mIsChecked;
	private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};	
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private boolean mSetOnClickListener = false;
	
	/**
	 * Default constructor
	 * @param context
	 */
	public XUICheckBox(Context context)
	{
		super(context); 
	}
	
	/**
	 * Default constructor
	 * @param context
	 * @param attrs
	 */
	public XUICheckBox(Context context, AttributeSet attrs)
	{
		super(context, attrs);
				
		//setBackgroundResource(R.drawable.xui_checkbox_drawable);
		refreshDrawableState();
		
		//	Only set the default click listener if there is none set
		//	in the XML layout
		if (!mSetOnClickListener)
		{
			setOnClickListener(new OnClickListener()
			{			
				public void onClick(View v)
				{
					((XUICheckBox)v).toggle();
				}
			});					 
		}
	}
	
	/**
	 * Toggles the checked status of the checkbox
	 */
	public void toggle()
	{
		setChecked(!mIsChecked);
	}
	
	/**
	 * Sets if the checkbox is checked or not
	 */
	public void setChecked(boolean isChecked)
	{
		if (mIsChecked != isChecked)
		{
			mIsChecked = isChecked; 
			
			refreshDrawableState();
			 
			if (mOnCheckedChangeListener != null)
			{				
				mOnCheckedChangeListener.onCheckChanged(this, mIsChecked);
			}
		}
	}
	
	@Override
	public void setOnClickListener(OnClickListener l)
	{
		mSetOnClickListener = true;
		super.setOnClickListener(l);
	}
	
	/**
	 * Sets the oncheckecchange listener 
	 * @param l The new listener
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener l)
	{		
		mOnCheckedChangeListener = l;
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace)
	{
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
	   
		if (mIsChecked) 
	    {
	        mergeDrawableStates(drawableState, CHECKED_STATE_SET);
	    }
		
	    return drawableState;
	}
	
	/**
	 * Returns if the checkbox is checked
	 */
	public boolean isChecked()
	{		
		return mIsChecked;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{	
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onFinishInflate()
	{	
		super.onFinishInflate();
		
		setChecked(false);
	}
	
	/**
	 * @breif Interface for when the checkbox gets checked or unchecked
	 */
	public interface OnCheckedChangeListener
	{
		/**
		 * Called when the checkbox is toggled
		 * @param view The checkbox toggled
		 * @param isChecked The new state of the checkbox
		 */
		public void onCheckChanged(XUICheckBox view, boolean isChecked);
	}
}
