package x.ui;

import x.lib.AsyncHttpClient;
import x.lib.AsyncHttpResponse;
import x.lib.Debug;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class XUIImageView extends ImageView
{
	public XUIImageView(Context context)
	{
		super(context);
	}
	
	public XUIImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void setImage(String url)
	{	
		final ScaleType scale = getScaleType();
		
		//setImageResource(R.drawable)
		//setScaleType(ScaleType.CENTER_INSIDE);
		
		AsyncHttpClient downloader = new AsyncHttpClient();
		downloader.getImage(url, new AsyncHttpResponse()
		{
			@Override
			public void onSuccess(Object response)
			{				
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
}
