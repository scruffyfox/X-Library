/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 *
 * @author Callum Taylor
**/
package x.ui;

import x.ui.XUIMenuButton.LineDrawable;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *  @brief The menu container for XUIMenuButtons
 *
 *  @param x:groupName The label text for the group
 *
 *  Example XML layout code
 *  @code
 *  <x.ui.XUIMenuButtonGroup
 *		android:layout_width="fill_parent"
 *		android:layout_height="wrap_content"
 *		android:layout_marginTop="10dp"
 *		x:groupName="Location"
 *	>
 *		<x.ui.XUIMenuButton
 *			android:layout_width="fill_parent"
 *			android:layout_height="wrap_content"
 *			android:id="@+id/town"
 *			android:onClick="townClick"
 *			android:clickable="true"
 *		>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text="Town / City"
 *			/>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text=""
 *				android:textSize="16dp"
 *			/>
 *		</x.ui.XUIMenuButton>
 *		<x.ui.XUIMenuButton
 *			android:layout_width="fill_parent"
 *			android:layout_height="wrap_content"
 *		>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text="Search Radius"
 *			/>
 *			<TextView
 *				android:layout_width="wrap_content"
 *				android:layout_height="wrap_content"
 *				android:text="50 Miles"
 *				android:textSize="16dp"
 *			/>
 *		</x.ui.XUIMenuButton>
 *	</x.ui.XUIMenuButtonGroup>
 *  @endcode
 */
public class XUIMenuButtonGroup extends LinearLayout
{
	private Context mContext;
	private LinearLayout layoutView = null;
	private int childCount = 0;
	private LayoutInflater mLayoutInflater;
	private String groupName = "";
	private int mStrokeColor = 0xffcccccc;
	private int mStrokeSize = 1;
	private int mLabelStrokeColor = 0xff333333;
	private int mLabelStrokeSize = 3;
	private int mGroupNameTransform = TEXT_TRANSFORM_NORMAL;
	private OnMenuButtonAdded mOnMenuButtonAdded;
	private OnMenuButtonRemoved mOnMenuButtonRemoved;
	private int mStyle = R.style.menu_button_group_iphone;

	/**
	 * XML Attribute: keeps the text as is typed in
	 */
	public static final int TEXT_TRANSFORM_NORMAL = 0x0;
	/**
	 * XML Attribute: makes the text uppercase
	 */
	public static final int TEXT_TRANSFORM_UPPERCASE = 0x01;
	/**
	 * XML Attribute: makes the text lowercase
	 */
	public static final int TEXT_TRANSFORM_LOWERCASE = 0x10;
	/**
	 * XML Attribute: makes the first letter in every word capital
	 */
	public static final int TEXT_TRANSFORM_CAPITALIZE = 0x100;
	/**
	 * XML Attribute: makes the first letter lower case, then the first letter
	 * in every word after capital
	 */
	public static final int TEXT_TRANSFORM_CAMEL_CASE = 0x1000;
	/**
	 * XML Attribute: makes the first letter in a sentence uppercase
	 */
	public static final int TEXT_TRANSFORM_GRAMATICAL = 0x10000;

	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIMenuButtonGroup(Context context)
	{
		this(context, R.style.menu_button_group_iphone);
	}

	public XUIMenuButtonGroup(Context context, int defStyle)
	{
		super(context);
		finalConstructor(context, null, defStyle, true);
	}

	public XUIMenuButtonGroup(Context context, AttributeSet attrs)
	{
		this(context, attrs, attrs.getStyleAttribute());
	}

	public XUIMenuButtonGroup(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs);
		finalConstructor(context, attrs, defStyle, false);
	}

	private void finalConstructor(Context context, AttributeSet attrs, int defStyle, boolean attach)
	{
		mContext = context;
		mStyle = defStyle == 0 ? R.style.menu_button_group_iphone : defStyle;

		TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.XUIMenuButtonGroup, 0, mStyle);
		//TypedArray attributes = mContext.obtainStyledAttributes(mStyle, R.styleable.XUIMenuButtonGroup);

		groupName = attributes.getString(R.styleable.XUIMenuButtonGroup_groupName);
		mGroupNameTransform = attributes.getInteger(R.styleable.XUIMenuButtonGroup_groupName_transform, TEXT_TRANSFORM_NORMAL);

		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = (LinearLayout)mLayoutInflater.inflate(R.layout.xui_menu_button_group, attach ? this : null);

		mStrokeColor = attributes.getColor(R.styleable.XUIMenuButtonGroup_strokeColor, 0xffcccccc);
		mStrokeSize = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_strokeSize, 2.0f);
		mLabelStrokeColor = attributes.getColor(R.styleable.XUIMenuButtonGroup_labelStrokeColor, 0xff333333);
		mLabelStrokeSize = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelStrokeSize, 3.0f);
		int labelSize = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelSize, 0);
		int padding = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelPadding, 0);
		int paddingLeft = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelPaddingLeft, 0);
		int paddingRight = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelPaddingRight, 0);
		int paddingBottom = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelPaddingBottom, 0);
		int paddingTop = (int)attributes.getDimension(R.styleable.XUIMenuButtonGroup_labelPaddingTop, 0);

		((TextView)layoutView.findViewById(R.id.group_label)).setTextSize(labelSize);
		((TextView)layoutView.findViewById(R.id.group_label)).setTextColor(attributes.getColor(R.styleable.XUIMenuButtonGroup_labelColor, 0xff000000));

		if (padding > 0)
		{
			layoutView.findViewById(R.id.group_label).setPadding(padding, padding, padding, padding);
		}
		else
		{
			layoutView.findViewById(R.id.group_label).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		}

		layoutView.setOrientation(VERTICAL);
		setOrientation(VERTICAL);

		if (attach)
		{
			setTitle(groupName);
		}
	}

	public void setLabelPadding(int left, int top, int right, int bottom)
	{
		layoutView.findViewById(R.id.group_label).setPadding(left, top, right, bottom);
	}

	/**
	 * Sets the menu button added listener
	 * @param mOnMenuButtonAdded The listener
	 */
	public void setOnMenuButtonAdded(OnMenuButtonAdded mOnMenuButtonAdded)
	{
		this.mOnMenuButtonAdded = mOnMenuButtonAdded;
	}

	/**
	 * Sets the on menu button removed listener
	 * @param mOnMenuButtonRemoved The listener
	 */
	public void setOnMenuButtonRemoved(OnMenuButtonRemoved mOnMenuButtonRemoved)
	{
		this.mOnMenuButtonRemoved = mOnMenuButtonRemoved;
	}

	/**
	 * Gets the child count for the sub container
	 * @return the amount of children in the container
	 */
	public int getMenuButtonCount()
	{
		LinearLayout itemContainer = ((LinearLayout)layoutView.findViewById(R.id.items));
		return itemContainer.getChildCount();
	}

	/**
	 * Gets all the buttons as an array from the group
	 * @return The buttons within the group as an XUIMenuButton array
	 */
	public XUIMenuButton[] getButtons()
	{
		LinearLayout itemContainer = ((LinearLayout)layoutView.findViewById(R.id.items));
		int childCount = itemContainer.getChildCount();
		XUIMenuButton[] buttons = new XUIMenuButton[childCount];

		for (int index = 0; index < childCount; index++)
		{
			buttons[index] = (XUIMenuButton)itemContainer.getChildAt(index);
		}

		return buttons;
	}

	/**
	 * Gets the XUIMenuButton at the specified index
	 * @param index The index of the view
	 * @return The view
	 */
	public XUIMenuButton getButtonAt(int index)
	{
		LinearLayout itemContainer = ((LinearLayout)layoutView.findViewById(R.id.items));
		return (XUIMenuButton)itemContainer.getChildAt(index);
	}

	/**
	 * Get the index of the menu button
	 * @param button The button to search for
	 * @return The index of the button, else -1
	 */
	public int getButtonIndex(XUIMenuButton button)
	{
		XUIMenuButton[] buttons = getButtons();
		int index = 0;
		for (XUIMenuButton b : buttons)
		{
			if (button == b)
			{
				return index;
			}

			index++;
		}

		return -1;
	}

	/**
	 * Adds a new button to the group
	 * @param child The new button to add
	 */
	public void addMenuButton(XUIMenuButton child)
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child);
		updateLayout();

		if (mOnMenuButtonAdded != null)
		{
			mOnMenuButtonAdded.onMenuButtonAdded(child, ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount() - 1);
		}
	}

	/**
	 * Adds a new button to the group
	 * @param child The new button to add
	 */
	public void addMenuButton(XUIMenuButton... child)
	{
		for (XUIMenuButton b : child)
		{
			((LinearLayout)layoutView.findViewById(R.id.items)).addView(b);

			if (mOnMenuButtonAdded != null)
			{
				mOnMenuButtonAdded.onMenuButtonAdded(b, ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount() - 1);
			}
		}

		updateLayout();
	}

	/**
	 * Adds a new button to the group at the specified index
	 * @param child The new button to add
	 * @param index The index to put the new button
	 */
	public void addMenuButton(XUIMenuButton child, int index)
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child, index);
		updateLayout();

		if (mOnMenuButtonAdded != null)
		{
			mOnMenuButtonAdded.onMenuButtonAdded(child, index);
		}
	}

	/**
	 * Adds a new button to the group at the specified index
	 * @param child The new button to add
	 * @param params The params for the new view
	 */
	public void addMenuButton(XUIMenuButton child, android.view.ViewGroup.LayoutParams params)
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).addView(child, params);
		updateLayout();

		if (mOnMenuButtonAdded != null)
		{
			mOnMenuButtonAdded.onMenuButtonAdded(child, ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount() - 1);
		}
	}

	/**
	 * Removes a view from the group
	 * @param view The view to remove
	 */
	public void removeMenuButton(XUIMenuButton view)
	{
		int index = ((LinearLayout)layoutView.findViewById(R.id.items)).indexOfChild(view);
		((LinearLayout)layoutView.findViewById(R.id.items)).removeView(view);
		updateLayout();

		if (mOnMenuButtonRemoved != null)
		{
			mOnMenuButtonRemoved.onMenuButtonRemoved(index);
		}
	}

	/**
	 * Removes a view from the group
	 * @param view The index of where to remove the view
	 */
	public void removeMenuButtonAt(int index)
	{
		((LinearLayout)layoutView.findViewById(R.id.items)).removeViewAt(index);
		updateLayout();

		if (mOnMenuButtonRemoved != null)
		{
			mOnMenuButtonRemoved.onMenuButtonRemoved(index);
		}
	}

	/**
	 * Removes all views from the group
	 */
	public void removeAllMenuButtons()
	{
		int count = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount();
		((LinearLayout)layoutView.findViewById(R.id.items)).removeAllViews();
		updateLayout();

		for (int index = 0; index < count; index++)
		{
			if (mOnMenuButtonRemoved != null)
			{
				mOnMenuButtonRemoved.onMenuButtonRemoved(index);
			}
		}
	}

	/**
	 * Sets the title of the list group
	 * @param title The new title for the list group
	 */
	public void setTitle(String title)
	{
		groupName = title;

		if (!TextUtils.isEmpty(title))
		{
			((XUITextView)findViewById(R.id.group_label)).setText((groupName));
			((XUITextView)findViewById(R.id.group_label)).setTextTransform(mGroupNameTransform);
			((XUITextView)findViewById(R.id.group_label)).setVisibility(View.VISIBLE);
			findViewById(R.id.divider).setVisibility(View.VISIBLE);
		}
		else
		{
			((XUITextView)findViewById(R.id.group_label)).setVisibility(View.GONE);
			findViewById(R.id.divider).setVisibility(View.GONE);
		}
	}

	private LineDrawable createICSDrawable()
	{
		return new LineDrawable(mContext, getResources().getDimension(R.dimen.xuimenubutton_stroke), 0x100, mStrokeColor);
	}

	/**
	 * Called to update the layout
	 */
	private void updateLayout()
	{
		childCount = ((LinearLayout)layoutView.findViewById(R.id.items)).getChildCount();

		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{
			XUIMenuButton childView = (XUIMenuButton)((LinearLayout)layoutView.findViewById(R.id.items)).getChildAt(viewIndex);
			childView.setStyle(mStyle);
			childView.setStrokeColor(mStrokeColor);
			childView.setStrokeSize(mStrokeSize);

			if (mStyle == R.style.menu_button_group_ics)
			{
				//layoutView.setBackgroundDrawable(createICSDrawable());
				layoutView.findViewById(R.id.group_label).setVisibility(View.VISIBLE);
				layoutView.findViewById(R.id.items).setBackgroundDrawable(null);
				layoutView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
				layoutView.findViewById(R.id.divider).setBackgroundColor(mLabelStrokeColor);
				layoutView.findViewById(R.id.divider).setPadding(0, mLabelStrokeSize, 0, 0);
				layoutView.findViewById(R.id.items).setPadding(0, 0, 0, 0);

				if (TextUtils.isEmpty(groupName))
				{
					layoutView.findViewById(R.id.group_label).setVisibility(View.GONE);
					layoutView.findViewById(R.id.divider).setVisibility(View.GONE);
				}
			}
			else
			{
				layoutView.setBackgroundDrawable(null);
				layoutView.findViewById(R.id.items).setBackgroundResource(R.drawable.button_group_all_faux);
				layoutView.findViewById(R.id.divider).setVisibility(View.GONE);
				layoutView.findViewById(R.id.items).setPadding(1, 1, 1, 1);
			}

			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1)
			{
				if (childCount == 1)
				{
					childView.setCornerLocation(XUIMenuButton.TOP | XUIMenuButton.BOTTOM);
				}
				else
				{
					if (viewIndex == 0)
					{
						childView.setCornerLocation(XUIMenuButton.TOP);
					}
					else if (viewIndex == childCount - 1)
					{
						childView.setCornerLocation(XUIMenuButton.BOTTOM);
					}
					else
					{
						childView.setCornerLocation(XUIMenuButton.NONE);
					}
				}
			}
			else
			{
				childView.setCornerLocation(XUIMenuButton.NONE);
			}
		}
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
	}

	/**
	 * Called when the view has finished loading in the children
	 */
	@Override protected void onFinishInflate()
	{
		super.onFinishInflate();

		int childCount = getChildCount();
		XUIMenuButton[] views = new XUIMenuButton[childCount];

		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{
			views[viewIndex] = (XUIMenuButton)getChildAt(viewIndex);
		}

		this.removeAllViews();

		if (mStyle == R.style.menu_button_group_ics)
		{
			//layoutView.setBackgroundDrawable(createICSDrawable());
			layoutView.findViewById(R.id.group_label).setVisibility(View.VISIBLE);
			layoutView.findViewById(R.id.items).setBackgroundDrawable(null);
			layoutView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
			layoutView.findViewById(R.id.divider).setPadding(0, mLabelStrokeSize, 0, 0);
			layoutView.findViewById(R.id.divider).setBackgroundColor(mLabelStrokeColor);
			layoutView.findViewById(R.id.items).setPadding(0, 0, 0, 0);

			if (TextUtils.isEmpty(groupName))
			{
				layoutView.findViewById(R.id.group_label).setVisibility(View.GONE);
				layoutView.findViewById(R.id.divider).setVisibility(View.GONE);
			}
		}
		else
		{
			layoutView.setBackgroundDrawable(null);
			layoutView.findViewById(R.id.items).setBackgroundResource(R.drawable.button_group_all_faux);
			layoutView.findViewById(R.id.divider).setVisibility(View.GONE);
			layoutView.findViewById(R.id.items).setPadding(1, 1, 1, 1);
		}

		for (int viewIndex = 0; viewIndex < childCount; viewIndex++)
		{
			views[viewIndex].setStyle(mStyle);
			views[viewIndex].setStrokeColor(mStrokeColor);
			views[viewIndex].setStrokeSize(mStrokeSize);

			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1)
			{
				if (childCount == 1)
				{
					views[viewIndex].setCornerLocation(XUIMenuButton.BOTTOM | XUIMenuButton.TOP);
				}
				else
				{
					if (viewIndex == 0)
					{
						views[viewIndex].setCornerLocation(XUIMenuButton.TOP);
					}
					else if (viewIndex == childCount - 1)
					{
						views[viewIndex].setCornerLocation(XUIMenuButton.BOTTOM);
					}
					else
					{
						views[viewIndex].setCornerLocation(XUIMenuButton.NONE);
					}
				}
			}
			else
			{
				views[viewIndex].setCornerLocation(XUIMenuButton.NONE);
			}

			((LinearLayout)layoutView.findViewById(R.id.items)).addView(views[viewIndex]);
		}

		this.addView(layoutView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		if (!TextUtils.isEmpty(groupName))
		{
			((XUITextView)findViewById(R.id.group_label)).setText(groupName);
			((XUITextView)findViewById(R.id.group_label)).setTextTransform(mGroupNameTransform);
			((XUITextView)findViewById(R.id.group_label)).setVisibility(View.VISIBLE);
			findViewById(R.id.divider).setVisibility(View.VISIBLE);
		}
		else
		{
			((XUITextView)findViewById(R.id.group_label)).setVisibility(View.GONE);
			findViewById(R.id.divider).setVisibility(View.GONE);
		}
	}

	/**
	 * @brief Interface for when a button gets added to the group
	 */
	public interface OnMenuButtonAdded
	{
		/**
		 * Called when a button is added to the group
		 * @param button The button that has been added
		 * @param index The index of the button
		 */
		public void onMenuButtonAdded(XUIMenuButton button, int index);
	}

	/**
	 * @brief Interface for when a button gets removed from the group
	 */
	public interface OnMenuButtonRemoved
	{
		/**
		 * Called when a button is removed from the group
		 * @param index The index of the button that was removed
		 */
		public void onMenuButtonRemoved(int index);
	}
}
