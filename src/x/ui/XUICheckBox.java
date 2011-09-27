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
	
	public XUICheckBox(Context context)
	{
		super(context); 
	}
	
	public XUICheckBox(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setOnClickListener(new OnClickListener()
		{			
			public void onClick(View v)
			{
				((XUICheckBox)v).toggle();
			}
		});		
		 
		//setBackgroundResource(R.drawable.xui_checkbox_drawable);
		refreshDrawableState();
	}
	
	public void toggle()
	{
		setChecked(!mIsChecked);
	}
	
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
	
	public interface OnCheckedChangeListener
	{
		public void onCheckChanged(XUICheckBox view, boolean isChecked);
	}
}
