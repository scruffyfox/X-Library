/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.util.Timer;
import java.util.TimerTask;

import x.lib.AsyncHttpClient;
import x.lib.AsyncHttpResponse;
import x.lib.Debug;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @brief A custom ImageView class that allows support for remote image loading
 */
public class XUIImageView extends ImageView
{
	private Context mContext;
	private float mOpacity;
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIImageView(Context context)
	{
		super(context);
		mContext = context;
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUIImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XUIImageView);
		mOpacity = attributes.getFloat(R.styleable.XUIImageView_opacity, 1.0f);				
		attributes.recycle();
		
		mContext = context;
	}
	
	@Override protected void onDetachedFromWindow()
	{	
		super.onDetachedFromWindow();
		
		try
		{
			setBackgroundDrawable(null);
			setImageDrawable(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the opacity of the image view
	 * @param opacity The opacity 0-1.0
	 */
	public void setOpacity(float opacity)
	{
		mOpacity = opacity;
		getBackground().setAlpha((int)(mOpacity * 255));
	}
	
	/**
	 * Gets the opacity of the image view
	 * @return The opacity 0-1.0
	 */
	public float getOpacity()
	{
		return mOpacity;
	}
	
	/**
	 * Sets the image to a resource located on the internet
	 * @param url The Uri to the image
	 */
	public void setImage(Uri url)
	{
		setImage(url.toString());
	}
	
	/**
	 * Sets the image to a resource located on the internet
	 * @param url The URL to the image
	 */
	public void setImage(String url)
	{	
		final ScaleType scale = getScaleType();
						
		final int[] loader = new int[]
		{
			R.drawable.image_loading_1,
			R.drawable.image_loading_2,
			R.drawable.image_loading_3,
			R.drawable.image_loading_4,
			R.drawable.image_loading_5,
			R.drawable.image_loading_6,
			R.drawable.image_loading_7,
			R.drawable.image_loading_8
		};
				
		setScaleType(ScaleType.CENTER_CROP);
		setAdjustViewBounds(true);
		
		final Handler h = new Handler();  
		final Runnable r = new Runnable()
		{			
			int index = 0;
			public void run()
			{
				index = index > 7 ? 0 : index;				
				setImageDrawable(getResources().getDrawable(loader[index++]));
				
				h.postDelayed(this, 100);
			}
		};
		h.postDelayed(r, 100);
				
		AsyncHttpClient downloader = new AsyncHttpClient();
		downloader.getImage(url, new AsyncHttpResponse()
		{
			@Override
			public void onSuccess(Object response)
			{				
				h.removeCallbacks(r);
				
				Bitmap b = (Bitmap)response;
				setScaleType(scale);
				setImageBitmap(b);
			}
			
			@Override
			public void onFailure(int responseCode, String responseMessage)
			{
				Debug.out(responseCode + " " + responseMessage);
			}
		});
	}
	
	@Override protected void onDraw(Canvas canvas)
	{	
		super.onDraw(canvas);
		
		setAlpha((int)(mOpacity * 255));
	}
	
	@Override protected void onFinishInflate()
	{	
		super.onFinishInflate();				
	}
}
