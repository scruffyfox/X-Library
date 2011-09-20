package x.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

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
		
		int childCount = getChildCount();
		views = new View[childCount];
		
		for (int index = 0; index < childCount; index++)
		{
			views[index] = getChildAt(index);
		}
		
		this.removeAllViews();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		
		int maxWidth = this.getWidth();
		int currentWidth = 0;		
		int childCount = getChildCount();
		
		for (int index = 0, currentRowIndex = 0; index < childCount; index++)
		{
			if (currentWidth + views[index].getMeasuredWidth() > maxWidth)
			{
				currentRowIndex++;
				currentWidth = 0;
			}
			
			if (currentWidth <= 0)
			{
				LinearLayout row = new LinearLayout(mContext);
				row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				
				this.addView(row);
			}
			
			LinearLayout row = (LinearLayout)getChildAt(currentRowIndex);			
			row.addView(views[index]);			
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
