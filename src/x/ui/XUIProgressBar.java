package x.ui;

import x.lib.Debug;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class XUIProgressBar extends ImageView
{
	public enum ProgressMode
	{
		SPINNER_INFINATE,
		SPINNER_INFINATE_REVERSE,
		LONG_PROGRESS
	}
	
	private Context mContext;
	private Animation mAnimation;
	private ProgressMode mProgressMode = ProgressMode.SPINNER_INFINATE;
	
	public XUIProgressBar(Context context)
	{
		super(context);
		
		mContext = context;
	}
	
	public XUIProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		mContext = context;
	}	
	
	public void setProgressMode(ProgressMode mode)
	{
		mProgressMode = mode;
	}
	
	public void startProgress()
	{
		switch (mProgressMode)
		{
			case SPINNER_INFINATE_REVERSE:
			case SPINNER_INFINATE:
			{
				int centerX = getDrawable().getMinimumWidth() / 2;
				int centerY = getDrawable().getMinimumHeight() / 2;				
				float from = 0.0f;
				float to = 360.0f;
				
				if (mProgressMode == ProgressMode.SPINNER_INFINATE_REVERSE)
				{
					from = 360.0f;
					to = 0.0f;
				}
				
				RotateAnimation rotationAnimation = new RotateAnimation(from, to, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
				rotationAnimation.setRepeatCount(Animation.INFINITE);
				rotationAnimation.setDuration(400);
				rotationAnimation.setInterpolator(new LinearInterpolator());
				mAnimation = rotationAnimation;
				
				break;
			}
		}
		
		this.startAnimation(mAnimation);
	}
	
	public void stopProgress()
	{
		this.getAnimation().cancel();
	}
}
