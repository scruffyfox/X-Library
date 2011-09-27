package x.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class XUIWrapLayout extends LinearLayout
{
	private Context mContext;
	private View views[];
	
	public XUIWrapLayout(Context context)
	{
		super(context);
		
		mContext = context;
	}
	
	public XUIWrapLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate()
	{ 
		super.onFinishInflate();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);		
		
		int left = 0;
		for (int i = 0; i < getChildCount(); i++) 
		{
			getChildAt(i).layout(left * i, t, getChildAt(i).getWidth(), getChildAt(i).getHeight());
			left += getChildAt(i).getWidth();
		}
	}
	 
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
