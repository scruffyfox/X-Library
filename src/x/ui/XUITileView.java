package x.ui;

import java.util.ArrayList;
import java.util.Random;

import x.lib.Debug;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class XUITileView extends LinearLayout
{
	private Context mContext;
	private int mMaxColumns = 1; 
	private LinearLayout mMainContainer;
	
	public XUITileView(Context context)
	{
		super(context);
		
		mContext = context;
	}
	
	public XUITileView(Context context, AttributeSet attr)
	{
		super(context, attr);

		mContext = context;
		
		TypedArray attributes = context.obtainStyledAttributes(attr, R.styleable.XUITileView);		
		mMaxColumns = attributes.getInt(R.styleable.XUITileView_maxColumns, 1);
		
		mMainContainer = new LinearLayout(mContext);
		mMainContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));	
		mMainContainer.setBackgroundColor(0xff00ff00);
		mMainContainer.setOrientation(LinearLayout.VERTICAL);
		
		this.setOrientation(LinearLayout.VERTICAL);				
	}
		
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{	
		super.onLayout(changed, l, t, r, b);
		
		if (changed)
		{
			int[] color = new int[]{0xff0000ff, 0xffabcdef};
			int width = getWidth();
			int childCount = mMainContainer.getChildCount();
			
			for (int index = 0; index < childCount; index++)
			{
				LinearLayout container = (LinearLayout)getChildAt(0);
				Debug.out(container.getChildAt(index).toString());
				//mMainContainer.getChildAt(index).
				
				container.getChildAt(index).setBackgroundColor(color[index]);
				
				LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)container.getChildAt(index).getLayoutParams();
				p.width = 240;
				Debug.out(p.width);
				
				((LinearLayout)container.getChildAt(index)).setLayoutParams(p);				
			}
		}
	}
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		
		this.removeAllViews();	
		
		int childCount = getChildCount();
		
		LinearLayout column = new LinearLayout(mContext);
		//column.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
		column.setOrientation(LinearLayout.VERTICAL);
		column.setBackgroundColor(0xffff0000);
		
		LinearLayout asd = new LinearLayout(mContext);
		//asd.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
		asd.setOrientation(LinearLayout.VERTICAL);
		asd.setBackgroundColor(0xffffff00);
		
		mMainContainer.addView(column);
		mMainContainer.addView(asd);
		
		this.addView(mMainContainer);				
	}
}
