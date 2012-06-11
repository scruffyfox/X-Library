/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
 **/
package x.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @brief The menu buttons to be used with XUIMenuButtonGroup. 
 * The menu button can only have a maximum of 3 views (Any more and the parser will ignore them), the first view
 * must be a TextView (the "label") the second view can be anything.
 * The current combination of children are as followed:
 * [layout]
 * [text view]
 * [image view, text view]
 * [text view, view]
 * [image view, text view, view]	
 *  
 * @param x:colorState [ref] You can use this to set the default colours list to the cell of the drawable. Only accepts the pressed state and normal state
 * Example of setting the colours of the group
 * @code
 * <?xml version="1.0" encoding="utf-8"?>
 * <selector xmlns:android="http://schemas.android.com/apk/res/android">
 * 	<item android:state_pressed="true" android:color="#Ff0fab02" />	
 * 	<item android:color="#ffffffff" /> <!-- not selected -->
 * </selector>
 * @endcode
 * 
 * Example XML Code (Note to make a button clickable, you must declare it clickable by using android:clickable="true") 
 * This example uses the 4th combination of [text view, view]
 * @code
 *	<x.ui.XUIMenuButton
 *		android:layout_width="fill_parent" 
 *		android:layout_height="wrap_content"
 *		android:id="@+id/town"
 *		android:onClick="townClick"  
 *		android:clickable="true"
 *		x:colorState="@drawable/list"
 *	>
 *		<TextView
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content"
 *			android:text="Town / City"    					
 *		/>
 *		<TextView
 *			android:layout_width="wrap_content"
 *			android:layout_height="wrap_content" 
 *			android:text=""
 *			android:textSize="16dp"
 *		/>
 *	</x.ui.XUIMenuButton>
 * @endcode
 * 
 * 
 * When using clickable, its important to use colour lists for when the button is pressed for accessibility. Changing the colour to a brighter colour (if the press state is dark) or
 * darker if the press state is light.
 * 
 * An example of this is. Note this is stored as a drawable and used in textviews attribute android:textColor="@drawable/list" for example.
 * @code
 * <?xml version="1.0" encoding="utf-8"?>
 * <selector xmlns:android="http://schemas.android.com/apk/res/android">
 * 	<item android:state_pressed="true" android:color="#FFF" />
 * 	<item android:state_focused="true" android:color="#FFF" />
 * 	<item android:color="#ff352B21" /> <!-- not selected -->
 * </selector>
 * @endcode
 */
public class XUIMenuButton extends LinearLayout
{
	protected static final int NONE = 0x00;
	protected static final int TOP = 0x01;
	protected static final int BOTTOM = 0x10;
	
	private Context mContext;
	private ViewGroup mLayoutView;
	private LayoutInflater mLayoutInflater;
	private int mChildCount = 0;
	private int mCornerLocation;
	private ViewGroup mLayout;
	private ImageView mImageView;
	private TextView mLabel;
	private View mContentView;
	private boolean mSetOnClickListener;
	private ColorStateList mStateColours;

	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIMenuButton(Context context)
	{
		super(context);		

		this.mContext = context;

		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		mLayoutView = (ViewGroup)mLayoutInflater.inflate(R.layout.xui_menu_button, this);
		mLayout = (ViewGroup)mLayoutView;

		init();
	}

	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */ 
	public XUIMenuButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		this.mContext = context; 

		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XUIMenuButtonGroup);				
		mStateColours = attributes.getColorStateList(R.styleable.XUIMenuButton_colorState);

		init();
	}	
	
	/**
	 * Sets the state colours for the view
	 * @param states The resource to the states
	 */
	public void setStateColors(int states)
	{
		setStateColors(getResources().getColorStateList(states));
	}
	
	/**
	 * Sets the state colours for the view
	 * @param states The colour states
	 */
	public void setStateColors(ColorStateList states)
	{
		mStateColours = states;
		updateDrawable();
	}

	/**
	 * Override of the padding method
	 * @param l Left padding
	 * @param t Top padding
	 * @param r Right padding
	 * @param b Bottom padding
	 */
	public void setMenuPadding(int l, int t, int r, int b)
	{
		mLayout.getChildAt(0).setPadding(l, t, r, b);
	}

	private void init()
	{		
		if (mStateColours == null)
		{
			mStateColours = getResources().getColorStateList(R.drawable.xui_menu_button_colors);
		}
		
		//	Only set the onlick listener if it hasn't already		
		if (!mSetOnClickListener && this.isClickable())
		{			
			OnClickListener l = new OnClickListener()
			{			
				public void onClick(View v)
				{
					View input = ((LinearLayout)mLayout.findViewById(R.id.input_container)).getChildAt(0);
					input.requestFocus();
					input.requestFocusFromTouch(); 

					if (input instanceof EditText)
					{
						EditText t = ((EditText)input);
						t.requestFocus();
						t.requestFocusFromTouch();
						InputMethodManager m = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);						        
						m.showSoftInput(t, 0);		 
					}
					else if (input instanceof CheckBox)
					{
						((CheckBox)input).toggle();
					}
					else if (input instanceof XUICheckBox)
					{
						((XUICheckBox)input).toggle();	
					}				
				}
			};

			this.setOnClickListener(l);
		}
	}

	@Override public void setClickable(boolean clickable)
	{		
		super.setClickable(clickable);
		init();
	};

	@Override public void setOnClickListener(OnClickListener l)
	{	
		mSetOnClickListener = true;

		super.setOnClickListener(l);
	}

	/**
	 * Sets the label text
	 * @param text The new text for the label
	 */
	public void setLabel(String text)
	{
		int childCount = ((LinearLayout)mLayout.findViewById(R.id.label)).getChildCount();	
		ColorStateList list = getResources().getColorStateList(R.drawable.button_group_text);

		if (childCount < 1)
		{
			TextView t = new TextView(mContext);
			t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			t.setText(Html.fromHtml("<b>" + text + "</b>"));
			t.setGravity(Gravity.CENTER_VERTICAL);
			t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));	
			t.setTextColor(list);
			t.setDuplicateParentStateEnabled(true);
			((LinearLayout)mLayout.findViewById(R.id.label)).addView(t);						
		}  
		else
		{
			TextView t = (TextView)((LinearLayout)mLayout.findViewById(R.id.label)).getChildAt(0);
			t.setGravity(Gravity.CENTER_VERTICAL);
			t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
			t.setTextColor(list);
			t.setDuplicateParentStateEnabled(true);
			t.setText(text); 	
		}
	}

	/**
	 * Sets the input view
	 * @param v The new input view
	 */
	public void setInput(View v)
	{
		mContentView = v;

		((LinearLayout)mLayout.findViewById(R.id.input_container)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.input_container)).addView(v);
	} 

	/**
	 * Gets the input view
	 * @return The input view
	 */
	public View getInput() 
	{ 
		return mContentView;
	} 

	/**
	 * Sets the background state resource
	 * @param res The drawable resource
	 */
	public void setStateResouces(int res)
	{
		mLayoutView.setBackgroundResource(res);
	} 

	/**
	 * Sets the icon in the menu item
	 * @param v The new ImageView
	 */
	public void setIcon(ImageView v)
	{
		mImageView = v;		
		mImageView.setDuplicateParentStateEnabled(true);

		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}

	/**
	 * Sets the icon in the menu item
	 * @param b The new bitmap of the icon
	 */
	public void setIcon(Bitmap b)
	{
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setImageBitmap(b);
		mImageView.setDuplicateParentStateEnabled(true);

		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}

	/**
	 * Sets the icon in the menu item
	 * @param d The new Drawable of the icon
	 */
	public void setIcon(Drawable d)
	{
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundDrawable(d);
		mImageView.setDuplicateParentStateEnabled(true);
		mImageView.setFocusable(true);

		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}

	/**
	 * Sets the icon in the menu item
	 * @param res The new Drawable of the icon
	 */
	public void setIcon(int res)
	{
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mImageView.setImageResource(res);
		mImageView.setDuplicateParentStateEnabled(true);
		mImageView.setFocusable(true);

		((LinearLayout)mLayout.findViewById(R.id.icon)).removeAllViews();
		((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);
		((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
	}

	/**
	 * Sets the corner location of the drawable
	 * @param location The location, either TOP, BOTTOM or NONE
	 */
	protected void setCornerLocation(int location)
	{
		mCornerLocation = location;
		updateDrawable();
	}
	
	/**
	 * Updates the background drawable
	 */
	private void updateDrawable()
	{
		float[] radii = new float[8];
		
		if ((mCornerLocation & TOP) == TOP)
		{
			radii[0] = getResources().getDimension(R.dimen.xuimenubutton_radius);
			radii[1] = getResources().getDimension(R.dimen.xuimenubutton_radius);
			radii[2] = getResources().getDimension(R.dimen.xuimenubutton_radius);
			radii[3] = getResources().getDimension(R.dimen.xuimenubutton_radius);
		}
		
		if ((mCornerLocation & BOTTOM) == BOTTOM)
		{
			radii[4] = getResources().getDimension(R.dimen.xuimenubutton_radius);
			radii[5] = getResources().getDimension(R.dimen.xuimenubutton_radius);
			radii[6] = getResources().getDimension(R.dimen.xuimenubutton_radius);
			radii[7] = getResources().getDimension(R.dimen.xuimenubutton_radius);
		}		
		
		StateListDrawable newDrawable = new StateListDrawable();				
		ItemDrawable normal = new ItemDrawable(0xffcccccc, getResources().getDimension(R.dimen.xuimenubutton_stroke));
		ItemDrawable pressed = new ItemDrawable(0xffcccccc, getResources().getDimension(R.dimen.xuimenubutton_stroke));				
		RoundRectShape rect = new RoundRectShape(radii, null, null);		
					
		normal.setShape(rect);
		pressed.setShape(rect);		
		
		int normalColor = mStateColours.getColorForState(new int[]{}, getResources().getColor(R.color.xui_button_group_bg));
		normal.getPaint().setColor(normalColor);
				
		int pressedColor = mStateColours.getColorForState(new int[]{android.R.attr.state_pressed}, getResources().getColor(R.color.xui_button_group_bg_selected));
		pressed.getPaint().setColor(pressedColor);

		newDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
		newDrawable.addState(new int[]{}, normal);
		
		setBackgroundDrawable(newDrawable);  
	}
	
	/**
	 * Is called when the view is being layed out
	 * @param changed If the view has changed or not
	 * @param l The left coordinate
	 * @param t The top coordinate
	 * @param r The right coordinate
	 * @param b The bottom coordinate
	 */
	@Override protected void onLayout(boolean changed, int l, int t, int r, int b) 
	{	
		super.onLayout(changed, l, t, r, b);

		mChildCount = getChildCount();	
	}

	/**
	 * Called when the view has finished loading in the children
	 */
	@Override protected void onFinishInflate()  
	{	
		super.onFinishInflate();

		/**
		 * Possible combinations:
		 * [layout]
		 * [text]
		 * [image, text]
		 * [text, content]
		 * [image, text, content]
		 */
		if (getChildCount() == 1)
		{
			if (getChildAt(0) instanceof ViewGroup)
			{
				mContentView = (View)getChildAt(0);
			}
			else
			{
				mLabel = (TextView)getChildAt(0);
			}
		}
		else if (getChildCount() == 2)
		{			
			if (getChildAt(0) instanceof TextView)
			{
				mLabel = (TextView)getChildAt(0);
				mContentView = (View)getChildAt(1);
			}
			else
			{ 
				mImageView = (ImageView)getChildAt(0);
				mLabel = (TextView)getChildAt(1);		
			}
		}
		else if (getChildCount() == 3)
		{
			mImageView = (ImageView)getChildAt(0); 
			mLabel = (TextView)getChildAt(1); 
			mContentView = (View)getChildAt(2); 
		}

		Rect padding = new Rect(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());

		this.detachAllViewsFromParent(); 

		if (mLabel != null)
		{
			ColorStateList list = getResources().getColorStateList(R.drawable.button_group_text);

			mLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mLabel.setText(Html.fromHtml("<b>" + mLabel.getText() + "</b>"));		
			mLabel.setTextColor(list);
			mLabel.setDuplicateParentStateEnabled(true);		
		}

		mLayoutView = (ViewGroup)mLayoutInflater.inflate(R.layout.xui_menu_button, this, true);
		mLayout = (ViewGroup)((LinearLayout)mLayoutView).getChildAt(0);

		setMenuPadding(padding.left, padding.top, padding.right, padding.bottom);

		if (mLabel != null)
		{						 
			((LinearLayout)mLayoutView.findViewById(R.id.label)).addView(mLabel);			
		}

		if (mContentView != null)
		{			
			((LinearLayout)mLayout.findViewById(R.id.input_container)).addView(mContentView);

			int gravity = ((LinearLayout.LayoutParams)mContentView.getLayoutParams()).gravity;
			if (gravity != Gravity.LEFT || gravity != Gravity.RIGHT)
			{
				gravity = Gravity.RIGHT;
			}

			((LinearLayout)mLayout.findViewById(R.id.input_container)).setGravity(gravity);			
		}

		if (mImageView != null)
		{
			((LinearLayout)mLayout.findViewById(R.id.icon)).addView(mImageView);		
			((LinearLayout)mLayout.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
		}
	}
	
	private class ItemDrawable extends ShapeDrawable
	{
		int strokeColor;
		float strokeSize;		
		
		public ItemDrawable(int strokeColor, float strokeSize)
		{
			this.strokeColor = strokeColor;
			this.strokeSize = strokeSize;
		}
		
		@Override protected void onDraw(Shape shape, Canvas canvas, Paint paint) 
		{			
		    Paint fillpaint = this.getPaint();
		    Paint strokepaint = new Paint(fillpaint);
		    strokepaint.setAntiAlias(true);
		    strokepaint.setStyle(Paint.Style.STROKE);
		    //to set stroke width and color instead of <stroke android:width="2dp" android:color="#FFFFFFFF" /> 
		    strokepaint.setStrokeWidth(strokeSize);
		    strokepaint.setColor(strokeColor);

		    shape.draw(canvas, fillpaint);
		    shape.draw(canvas, strokepaint);
		}
	}
}