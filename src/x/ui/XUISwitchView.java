package x.ui;

import x.lib.Debug;
import x.ui.XUITabParams.Option;
import x.util.BitmapUtils;
import x.util.BitmapUtils.Colour;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Custom switch view control which allows a midway state.
 * 
 * The view has a fixed width and height which can not be changed.
 * @param x:color The color of the control when the switch is set to on
 * @param x:state The initial state of the control. Options are: on, off, both
 * 
 * Example XML
 * @code
 * <x.ui.XUISwitchView
 *		android:layout_width="wrap_content"
 *		android:layout_height="wrap_content"  
 *		x:color="#8DB344"	
 *		x:state="on" 								 
 * />
 * @endcode
 */
public class XUISwitchView extends View
{	
	private Context mContext;
	private SwitchState mCurrentSwitchState = SwitchState.BOTH;
	private int mWidth, mHeight, mBorderWidth = 2, mBorderRadius, mTextSize; 
	private Option<Integer> mStateColour;
	private Option<Integer> mStateTextColour;
	
	/**
	 * Enum for the state of the switch
	 * ON: The switch is set to the right position
	 * OFF: The switch is set to the left position
	 * BOTH: The switch is set to the middle 
	 */
	public enum SwitchState
	{
		ON("ON"),
		OFF("OFF"),
		BOTH("");
		
		private String mLabel;
		private SwitchState(String label)
		{
			this.mLabel = label;
		}
		
		public String getLabel()
		{
			return mLabel;
		}
	}
	
	/**
	 * Default Constructor
	 * @param context
	 */
	public XUISwitchView(Context context)
	{
		super(context);
		
		mContext = context;
		init();
	}
	
	/**
	 * Default Constructor
	 * @param context
	 * @param attrs
	 */
	public XUISwitchView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XUISwitchView);		
		int backgroundColor = a.getColor(R.styleable.XUISwitchView_color, 0xffF87557);
		int currentState = a.getInt(R.styleable.XUISwitchView_state, 0x01);
		
		if (currentState == 0x01)
		{
			mCurrentSwitchState = SwitchState.ON;
		}
		else if (currentState == 0x10)
		{
			mCurrentSwitchState = SwitchState.OFF;
		}
		else
		{
			mCurrentSwitchState = SwitchState.BOTH;
		}
		
		mStateColour = new Option<Integer>(backgroundColor, 0xffDADADA);
		
		mContext = context;	
		init();
	}
	
	/**
	 * Initializes the view, calculates the colours and sets the click listener
	 */
	private void init()
	{	
		mWidth = (int)getResources().getDimension(R.dimen.xuiswitchview_width);
		mHeight = (int)getResources().getDimension(R.dimen.xuiswitchview_height);
		mTextSize = (int)getResources().getDimension(R.dimen.xuiswitchview_text_size);
		mBorderRadius = (int)getResources().getDimension(R.dimen.xuiswitchview_corner_radius);
		
		if (mStateColour == null)
		{
			mStateColour = new Option<Integer>(0xffF87557, 0xffDADADA);
		}
		
		//	Calculate the on colour's brightness
		double red = Color.red(mStateColour.selected);
		double green = Color.green(mStateColour.selected);
		double blue = Color.blue(mStateColour.selected);
		int brightness = (int)Math.sqrt((red * red * 0.241) + (green * green * 0.691) + (blue * blue * 0.068));
		
		if (brightness < 170) 
		{
			mStateTextColour = new Option<Integer>(0xd0ffffff, 0xd0000000);
		}
		else
		{
			mStateTextColour = new Option<Integer>(0xd0000000, 0xd0000000);
		}
		
		setClickable(true);
		setOnClickListener(new OnClickListener()
		{			
			public void onClick(View v)
			{
				mCurrentSwitchState = mCurrentSwitchState == SwitchState.BOTH ? SwitchState.OFF : (mCurrentSwitchState == SwitchState.ON ? SwitchState.OFF : SwitchState.ON);
				invalidate();
			}
		});
	}	
	
	/**
	 * Toggles the switch between the on/off state
	 */
	public void toggle()
	{
		mCurrentSwitchState = mCurrentSwitchState == SwitchState.BOTH ? SwitchState.OFF : (mCurrentSwitchState == SwitchState.ON ? SwitchState.OFF : SwitchState.ON);
		invalidate();
	}
	
	/**
	 * Sets the current state of the switch
	 * @param state The new state for the view
	 */
	public void setSwitchState(SwitchState state)
	{
		mCurrentSwitchState = state;
		this.invalidate();
	}
	
	/**
	 * Gets the current state of the swtich
	 * @return The current state of the switch
	 */
	public SwitchState getCurrentSwitchState()
	{
		return mCurrentSwitchState;
	}
	
	/**
	 * Calculates the saturated colour from a colour and amount
	 * @param colour The colour to test
	 * @param amount The amount to saturate (0 - 100)
	 * @return The saturated colour
	 */
	private int saturation(int colour, int amount)
	{
		int finalColour = 0;
		double t = (double)amount / 100.0;
	
		Colour colourList = new Colour(255, Color.red(colour), Color.green(colour), Color.blue(colour));
		int average = (colourList.red + colourList.green + colourList.blue) / 3;
		colourList.red = BitmapUtils.safe((int)(average + t * (colourList.red - t)));
		colourList.green = BitmapUtils.safe((int)(average + t * (colourList.green - t)));
		colourList.blue = BitmapUtils.safe((int)(average + t * (colourList.blue - t)));
		
		finalColour = Color.argb(255, colourList.red, colourList.green, colourList.blue);
		return finalColour;
	}
	
	@Override protected void onDraw(Canvas canvas)
	{
		int currentColour = 0;
		if (mCurrentSwitchState == SwitchState.ON)
		{
			currentColour = mStateColour.selected;
		}
		else if (mCurrentSwitchState == SwitchState.OFF)
		{
			currentColour = mStateColour.deselected;
		}
		else
		{
			currentColour = saturation(mStateColour.selected, 20);			
		}
		
		//	Draw the background 		
		Paint p = new Paint();
		p.setColor(currentColour);		
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		Paint stroke = new Paint();			
		stroke.setColor(0xffffffff);
		stroke.setFlags(Paint.ANTI_ALIAS_FLAG);		
		
		canvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), mBorderRadius, mBorderRadius, stroke);
		canvas.drawRoundRect(new RectF(mBorderWidth, mBorderWidth, mWidth - mBorderWidth, mHeight - mBorderWidth), mBorderRadius, mBorderRadius, p);		
		
		int handleBorderWidth = 2;
		
		//	Draw the handle
		Paint handlePaint = new Paint();
		handlePaint.setColor(0xff666666);
		handlePaint.setStyle(Style.FILL);
		handlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
		
		Paint handleBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		handleBorderPaint.setColor(0xffeeeeee);
		handleBorderPaint.setStyle(Style.STROKE);
		handleBorderPaint.setStrokeWidth(2);
		handleBorderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
		
		int leftMargin = 0;
		if (mCurrentSwitchState == SwitchState.OFF)
		{
			leftMargin += mWidth / 2;
		}
		else if (mCurrentSwitchState == SwitchState.BOTH)
		{
			leftMargin += mWidth / 4;
		}
		
		int left = Math.abs((mWidth / 2) - leftMargin);
		canvas.drawRoundRect
		(
			new RectF
			(
				left + mBorderWidth, 
				mBorderWidth, 
				left + (mWidth / 2) - mBorderWidth, 
				mHeight - mBorderWidth
			), mBorderRadius, mBorderRadius, handleBorderPaint
		);
		
		canvas.drawRoundRect
		(
			new RectF
			(
				left + mBorderWidth + handleBorderWidth, 
				mBorderWidth + handleBorderWidth, 
				left + (mWidth / 2) - mBorderWidth - handleBorderWidth, 
				mHeight - mBorderWidth - handleBorderWidth
			), mBorderRadius, mBorderRadius, handlePaint
		);
		
		//	Draw the text
		Paint paint = new Paint();
		paint.setColor(mCurrentSwitchState == SwitchState.ON ? mStateTextColour.selected : mStateTextColour.deselected);		
		paint.setStyle(Paint.Style.FILL);		
		paint.setAntiAlias(true);		
		paint.setTextSize(mTextSize);		
		
		Rect bounds = new Rect();
		paint.getTextBounds(mCurrentSwitchState.getLabel(), 0, mCurrentSwitchState.getLabel().length(), bounds);				
		
		canvas.drawText(mCurrentSwitchState.getLabel(), leftMargin + ((mWidth / 2) / 2) - ((bounds.right - bounds.left) / 2), (mHeight / 2) + ((bounds.bottom - bounds.top) / 2), paint);
	}
	
	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{	
		super.onMeasure(new MeasureSpec().makeMeasureSpec(mWidth, MeasureSpec.EXACTLY), new MeasureSpec().makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
	}
}
