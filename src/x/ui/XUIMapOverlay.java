package x.ui;

import java.util.ArrayList;
import java.util.List;

import x.lib.IconOverlayItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class XUIMapOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item>
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
	private OnBallonClickListener mOnBallonClickListener;
	private OnBalloonLongClickListener mOnBalloonLongClickListener;
	private OnBalloonOpenedListener mBalloonOpenedListener;
	
	/**
	 * Default constructor
	 * @param mapView The map view
	 */
	public XUIMapOverlay(MapView mapView)
	{			
		this(mapView.getContext().getResources().getDrawable(R.drawable.map_pin), mapView);
	}
	
	/**
	 * Default constructor
	 * @param defaultMarker The default pin
	 * @param mapView The map view
	 */
	public XUIMapOverlay(Drawable defaultMarker, MapView mapView)
	{
		super(boundCenter(defaultMarker));
		
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
	 * Adds an overlay item to the stack
	 * @param item The item to add
	 */
	public void addOverlay(OverlayItem item)
	{
		mOverlayItems.add(item);
		populate();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
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
	
	@Override
	public Item getFocus() 
	{
		return mCurrentFocussedItem;
	}

	@Override
	public void setFocus(Item item) 
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
	
	@Override
	protected final boolean onTap(int index)
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
	protected void hideBalloon()
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
			if (overlay instanceof XUIMapOverlay<?> && overlay != this)
			{
				((XUIMapOverlay<?>) overlay).hideBalloon();
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
				if (mOnBallonClickListener != null)
				{
					mOnBallonClickListener.onBalloonClick(mCurrentFocussedIndex, mCurrentFocussedItem);
				}
			}				
		};
	}
	
	protected Item createItem(int i)
	{
		return (Item)mOverlayItems.get(i);
	}
	
	public int size()
	{		
		return mOverlayItems.size();
	}
	
	/**
	 * Interface for when the balloon is clicked
	 * @param <Item> Template of the class (must extend OverlayItem) 
	 */
	public interface OnBallonClickListener<Item>
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
		
		public BalloonOverlayView(Context context, int balloonBottomOffset)
		{
			super(context);

			setPadding(0, 0, 0, balloonBottomOffset);
			layout = new LinearLayout(context);
			layout.setVisibility(VISIBLE);

			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.balloon_overlay, layout);
			title = (TextView) v.findViewById(R.id.balloon_item_title);
			snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
			imageIcon = (ImageView) v.findViewById(R.id.balloon_item_icon);

			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.NO_GRAVITY;
			
			addView(layout, params);
		}

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
			 
			Bitmap drawable;
			if ((drawable = ((IconOverlayItem)i).getDrawable()) != null)
			{						
				int width = drawable.getWidth();
				int height = drawable.getHeight();  
				
				imageIcon.setVisibility(VISIBLE);
				imageIcon.setBackgroundDrawable(new BitmapDrawable(drawable));
				imageIcon.setLayoutParams(new RelativeLayout.LayoutParams(width, height));			
			}			
			
			if (i.getSnippet() != null)
			{
				snippet.setVisibility(VISIBLE);
				snippet.setText(i.getSnippet());
			}
			else
			{
				snippet.setVisibility(GONE);
			}
		}
	}
}