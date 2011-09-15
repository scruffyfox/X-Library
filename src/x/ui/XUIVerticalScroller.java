/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class XUIVerticalScroller extends ScrollView
{
	public XUIVerticalScroller(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
		
	public XUIVerticalScroller(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public XUIVerticalScroller(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public float startX = 0, startY = 0;
	public boolean scrollingLeft = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{						
		if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_OUTSIDE)
		{
			scrollingLeft = false;
		}
		
		if (scrollingLeft)
		{
			return false;
		}				
		
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			scrollingLeft = false;
			
			startX = ev.getX();
			startY = ev.getY();			
		}
		else if (ev.getAction() == MotionEvent.ACTION_MOVE)
		{
			if (Math.round(Math.abs(ev.getX() - startX)) > Math.round(Math.abs(ev.getY() - startY)))
			{				
				scrollingLeft = true;
				return false;
			}
			
			startX = ev.getX();
			startY = ev.getY();
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
	
		//super.onScrollChanged(l, t, oldl, oldt);
	}
}