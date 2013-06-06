package x.ui;

import x.lib.Debug;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class XUIEditText extends EditText
{
	private OnKeyListener mListener;
	private OnKeyListener mUserListener;
	
	public XUIEditText(Context context)
	{
		super(context); 
	}
	
	public XUIEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);						
	}
	
	@Override public void setSingleLine(boolean singleLine) 
	{	
		if (singleLine)
		{			
			mListener = new OnKeyListener()
			{				
				@Override public boolean onKey(View v, int keyCode, KeyEvent event)
				{
					if (mUserListener != null)
					{
						return mUserListener.onKey(v, keyCode, event);
					}
										
					if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
					{
						if (focusSearch(FOCUS_DOWN) != null) 
						{
		                    // An action has not been set, but the enter key will move to
		                    // the next focus, so set the action to that.
							onEditorAction(EditorInfo.IME_ACTION_NEXT);
		                } 
						else 
						{
		                    // An action has not been set, and there is no focus to move
		                    // to, so let's just supply a "done" action.
							onEditorAction(EditorInfo.IME_ACTION_DONE);
		                }
												
						return true;
					}
					
					return false;
				}
			};
			
			if (focusSearch(FOCUS_DOWN) != null) 
			{
				setImeOptions(EditorInfo.IME_ACTION_NEXT);
            } 
			else 
			{
				setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
			
			setOnKeyListener(mUserListener);
		}
		else
		{
			mListener = mUserListener;
		}			
	}
	
	@Override protected void onFinishInflate()
	{	
		super.onFinishInflate();
		 
		try
		{
			Debug.out(toString());
			Debug.out(getFocusables(FOCUS_DOWN).size());
			Debug.out("=======");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override public void setOnKeyListener(OnKeyListener l)
	{	
		mUserListener = l;
		super.setOnKeyListener(mListener);
	}
}
