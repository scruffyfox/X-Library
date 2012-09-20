/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 *
 * @author Callum Taylor
**/
package x.lib;

import java.util.ArrayList;
import java.util.List;

import x.type.IconOverlayItem;
import x.ui.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * @brief Map overlay for use to display points with a balloon popup for map views.
 * @param <Item> The item template (Generally use IconOverlayItem included with x.lib)
 *
 * Example usage of the overlay.
 * @code
 *	for (int index = 0; index < count; index++)
 *	{
 *		XUIMapOverlay overlay = new XUIMapOverlay(map);
 *		MapExtras extra = points.get(index);
 *
 *		overlay.setOnBallonClickListener(new balloonClickListener());
 *
 *		IconOverlayItem item = new IconOverlayItem(new GeoPoint(extra.latE6, extra.lngE6), extra.title, extra.description);
 *		overlay.addOverlay(item);
 *
 *		overlays.add(overlay);
 *	}
 *
 *	map.invalidate();
 * @endcode
 */
public class MapOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item>
{
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private MapView mMapView;
	private BalloonOverlayView<Item> mBalloonView;
	private View mClickRegion;
	private int mViewOffset;
	private final MapController mMapController;
	private Item mCurrentFocussedItem;
	private int mCurrentFocussedIndex;

	//	Listeners
	private OnBalloonClickListener mOnBalloonClickListener;
	private OnBalloonLongClickListener mOnBalloonLongClickListener;
	private OnBalloonOpenedListener mBalloonOpenedListener;

	/**
	 * Default constructor
	 * @param mapView The map view
	 */
	public MapOverlay(MapView mapView)
	{
		this(mapView.getContext().getResources().getDrawable(R.drawable.map_pin), mapView);
	}

	/**
	 * Default constructor
	 * @param defaultMarker The resource id of the default pin
	 * @param mapView The map view
	 */
	public MapOverlay(int defaultMarker, MapView mapView)
	{
		this(mapView.getContext().getResources().getDrawable(defaultMarker), mapView);
	}

	/**
	 * Default constructor
	 * @param defaultMarker The default pin
	 * @param mapView The map view
	 */
	public MapOverlay(Drawable defaultMarker, MapView mapView)
	{
		super(boundCenterBottom(defaultMarker));

		this.mMapView = mapView;
		mViewOffset = 0;
		mMapController = mapView.getController();

		mMapView.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				hideBalloon();

				return false;
			}
		});
	}

	/**
	 * Sets the click listener for the bubble
	 * @param mOnBalloonClickListener The new click listener
	 */
	public void setOnBallonClickListener(OnBalloonClickListener mOnBalloonClickListener)
	{
		setOnBallonClickListener(mOnBalloonClickListener, true);
	}

	/**
	 * Sets the click listener for the bubble
	 * @param mOnBalloonClickListener The new click listener
	 * @param showArrow whether to show the arrow or not
	 */
	public void setOnBallonClickListener(OnBalloonClickListener mOnBalloonClickListener, boolean showArrow)
	{
		this.mOnBalloonClickListener = mOnBalloonClickListener;
	}

	/**
	 * Sets the long click listener for the bubble
	 * @param mOnBalloonLongClickListener The new click listener
	 */
	public void setOnBalloonLongClickListener(OnBalloonLongClickListener mOnBalloonLongClickListener)
	{
		this.mOnBalloonLongClickListener = mOnBalloonLongClickListener;
	}

	/**
	 * Adds an overlay item to the stack
	 * @param item The item to add
	 */
	public void addOverlay(OverlayItem item)
	{
		mOverlayItems.add(item);
		populate();
	}

	/**
	 * Draws the point onto the mapview
	 * @param canvas The canvas to use to draw
	 * @param mapView The map view to draw onto
	 * @param shadow If there should be a shadow on the pin
	 */
	@Override public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, false);
	}

	/**
	 * Sets the balloon offset to the pin
	 * @param pixels
	 */
	public void setBalloonBottomOffset(int pixels)
	{
		mViewOffset = pixels;
	}

	/**
	 * Gets the balloon's offset
	 * @return The offset of the balloon
	 */
	public int getBalloonBottomOffset()
	{
		return mViewOffset;
	}

	/**
	 * Gets the current focused item
	 * @return The current focused item
	 */
	@Override public Item getFocus()
	{
		return mCurrentFocussedItem;
	}

	/**
	 * Sets the focus onto an item
	 * @param item The item to set focus to
	 */
	@Override public void setFocus(Item item)
	{
		mCurrentFocussedItem = item;

		if (mCurrentFocussedItem == null)
		{
			hideBalloon();
		}
		else
		{
			createBalloonOverlayView();
		}
	}

	/**
	 * Called when an item is tapped
	 * @param index The index of the item that was tapped
	 */
	@Override protected final boolean onTap(int index)
	{
		mCurrentFocussedIndex = index;
		mCurrentFocussedItem = createItem(index);

		boolean isRecycled;
		if (mBalloonView == null)
		{
			mBalloonView = createBalloonOverlayView();
			mClickRegion = (View)mBalloonView.findViewById(R.id.balloon_inner_layout);
			mClickRegion.setOnClickListener(createBalloonClickListener());
			mClickRegion.setOnLongClickListener(createBalloonLongPressListener());
			isRecycled = false;
		}
		else
		{
			isRecycled = true;
		}

		mBalloonView.setVisibility(View.GONE);

		List<Overlay> mapOverlays = mMapView.getOverlays();
		if (mapOverlays.size() > 1)
		{
			hideOtherBalloons(mapOverlays);
		}

		mBalloonView.setData(mCurrentFocussedItem);

		GeoPoint point = mCurrentFocussedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		mBalloonView.setVisibility(View.VISIBLE);

		if (mOnBalloonClickListener != null)
		{
			mBalloonView.findViewById(R.id.balloon_inner_layout).setBackgroundResource(R.drawable.balloon_overlay_bg_selector_chevron);
		}
		else
		{
			mBalloonView.findViewById(R.id.balloon_inner_layout).setBackgroundResource(R.drawable.balloon_overlay_bg_selector);
		}

		if (isRecycled)
		{
			mBalloonView.setLayoutParams(params);
		}
		else
		{
			mMapView.addView(mBalloonView, params);
		}

		mMapController.animateTo(point);

		return true;
	}

	/**
	 * Creates the balloon overlay
	 * @return The created balloon overlay
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView()
	{
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}

	/**
	 * Gets the map view
	 * @return The map view
	 */
	protected MapView getMapView()
	{
		return mMapView;
	}

	/**
	 * Hides the balloon view
	 */
	public void hideBalloon()
	{
		if (mBalloonView != null)
		{
			mBalloonView.setVisibility(View.GONE);
		}
	}

	/**
	 * Hides the other balloon overlays
	 * @param overlays The overlays
	 */
	private void hideOtherBalloons(List<Overlay> overlays)
	{
		for (Overlay overlay : overlays)
		{
			if (overlay instanceof MapOverlay<?> && overlay != this)
			{
				((MapOverlay<?>) overlay).hideBalloon();
			}
		}
	}

	/**
	 * Creates the default long press listener for the view
	 * @return The new click listener
	 */
	private OnLongClickListener createBalloonLongPressListener()
	{
		return new OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				if (mOnBalloonLongClickListener != null)
				{
					mOnBalloonLongClickListener.onBalloonLongClick(mCurrentFocussedIndex, mCurrentFocussedItem);
				}

				return true;
			}
		};
	}

	/**
	 * Creates the default click listener
	 * @return The new click listener
	 */
	private OnClickListener createBalloonClickListener()
	{
		return new OnClickListener()
		{
			public void onClick(View arg0)
			{
				if (mOnBalloonClickListener != null)
				{
					mOnBalloonClickListener.onBalloonClick(mCurrentFocussedIndex, mCurrentFocussedItem);
				}
			}
		};
	}

	/**
	 * Creats an item from an int index in the overlay list
	 * @return The created Item
	 */
	@Override protected Item createItem(int i)
	{
		return (Item)mOverlayItems.get(i);
	}

	/**
	 * Gets the size of the map overlay
	 * @return The size of the overlay item list
	 */
	public int size()
	{
		return mOverlayItems.size();
	}

	/**
	 * Interface for when the balloon is clicked
	 * @param <Item> Template of the class (must extend OverlayItem)
	 */
	public interface OnBalloonClickListener<Item>
	{
		/**
		 * Method called when the balloon is clicked
		 * @param index The index of the item
		 * @param item The item
		 */
		public void onBalloonClick(int index, Item item);
	}

	/**
	 * Interface for when the ballon is long clicked
	 * @param <Item> Template of the class (must extend OverlayItem)
	 */
	public interface OnBalloonLongClickListener<Item>
	{
		/**
		 * Method called when the balloon is long clicked
		 * @param index The index of the item
		 * @param item The item
		 */
		public void onBalloonLongClick(int index, Item item);
	}

	/**
	 * Interface for when the baloon is opened
	 */
	public interface OnBalloonOpenedListener
	{
		/**
		 * Method called when the balloon is opened
		 * @param index The index of the item
		 */
		public void onBalloonOpened(int index);
	}

	/**
	 * Class for the balloon view of the overlay
	 * @param <Item> Template of the class (must extend OverlayItem)
	 */
	private class BalloonOverlayView<Item extends OverlayItem> extends FrameLayout
	{
		private LinearLayout layout;
		private TextView title;
		private TextView snippet;
		private ImageView imageIcon;

		/**
		 * The default constructor
		 * @param context The context for the application
 		 * @param balloonBottomOffset The bottom offset from the center of the point
		 */
		public BalloonOverlayView(Context context, int balloonBottomOffset)
		{
			super(context);

			setPadding(0, 0, 0, balloonBottomOffset);
			layout = new LinearLayout(context);
			layout.setVisibility(VISIBLE);

			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.balloon_overlay, layout);


			title = (TextView)v.findViewById(R.id.balloon_item_title);
			snippet = (TextView)v.findViewById(R.id.balloon_item_snippet);
			imageIcon = (ImageView)v.findViewById(R.id.balloon_item_icon);

			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.NO_GRAVITY;

			addView(layout, params);
		}

		/**
		 * Sets the data from an item
		 * @param i The item to use for the data
		 */
		public void setData(Item i)
		{
			layout.setVisibility(VISIBLE);

			if (i.getTitle() != null)
			{
				title.setVisibility(VISIBLE);
				title.setText(i.getTitle());
			}
			else
			{
				title.setVisibility(GONE);
			}

			if (i instanceof IconOverlayItem)
			{
				Bitmap drawable;
				if ((drawable = ((IconOverlayItem)i).getDrawable()) != null)
				{
					int width = drawable.getWidth();
					int height = drawable.getHeight();

					imageIcon.setVisibility(VISIBLE);
					imageIcon.setBackgroundDrawable(new BitmapDrawable(drawable));
					imageIcon.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
				}
			}

			if (!TextUtils.isEmpty(i.getSnippet()))
			{
				snippet.setVisibility(VISIBLE);
				snippet.setText(i.getSnippet());
			}
			else
			{
				snippet.setVisibility(GONE);
			}

			if (TextUtils.isEmpty(i.getSnippet()) && TextUtils.isEmpty(i.getTitle()))
			{
				layout.setVisibility(GONE);
			}
		}
	}
}