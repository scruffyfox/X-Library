/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import x.lib.*;

public class XUIWebView extends WebView
{
	private ViewClient mViewClient;
	private OnPageLoadListener mOnPageLoadListener;
	
	/**
	 * Default constructor
	 * @param context
	 */
	public XUIWebView(Context context)
	{
		super(context);
					
		init();
	}
	
	/**
	 * Default constructor
	 * @param context
	 * @param attr
	 */
	public XUIWebView(Context context, AttributeSet attr)
	{
		super(context, attr);
		
		init();
	}
	
	/**
	 * Sets the OnPageLoadListener
	 * @param listener The new listener
	 */
	public void setOnPageLoadListener(OnPageLoadListener listener)
	{
		this.mOnPageLoadListener = listener;
	}
	
	private void init()
	{
		mViewClient = new ViewClient();
		setWebViewClient(mViewClient);
		
		this.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
	}
	
	/**
	 * Loads a file from the assets folder
	 * @param fileName The file to load
	 */
	public void loadFromAssets(String fileName)
	{
		loadUrl("file:///android_asset/" + fileName);		
	}
	
	/**
	 * Sets a JavaScript variable to a JSON string in the Web View
	 * @param variableName The variable name
	 * @param JSON The JSON string
	 */
	public void loadJSON(String variableName, String JSON)
	{
		loadUrl("javascript: var " + variableName + " = " + JSON + ";");
	}
	
	/**
	 * Calls a JavaScript function with the params
	 * @param functionName The function name to call
	 * @param param Array of params to pass
	 */
	public void callFunction(String functionName, String... param)
	{	
		loadUrl("javascript: " + functionName + "(" + StringUtils.join(param, ",") + ");");		
	}
	
	private class ViewClient extends WebViewClient
	{
		@Override
		public void onPageFinished(WebView view, String url)
		{
			if (mOnPageLoadListener != null)
			{
				mOnPageLoadListener.onPageLoad();
			}
			
			super.onPageFinished(view, url);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{		
			return true;
		}
	};
	
	/**
	 * The interface for page load 
	 */
	public interface OnPageLoadListener
	{
		/**
		 * Called when the page has been loaded
		 */
		public void onPageLoad();
	};
}
