/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.io.InputStream;
import java.io.ObjectInputStream;

import x.lib.Debug;
import x.util.StringUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class XUIWebView extends WebView
{
	private OnPageLoadListener mOnPageLoadListener;
	private OnLinkClickedListener mOnLinkClickedListener;
	public Context mContext;
	
	/**
	 * Default constructor
	 * @param context
	 */
	public XUIWebView(Context context)
	{
		super(context);
	
		mContext = context;
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
		
		mContext = context;
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
	
	/**
	 * Sets the OnLinkClickedListener
	 * @param listener The new listener
	 */
	public void setOnLinkClickedListener(OnLinkClickedListener listener)
	{
		this.mOnLinkClickedListener = listener;
	}
	
	private void init()
	{
		ViewClient mViewClient = new ViewClient();		
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
		int loadCount = 0;
		
		@Override
		public void onPageFinished(WebView view, String url)
		{
			if (loadCount < 1)
			{
				if (mOnPageLoadListener != null && (url.contains("http://") || url.contains("https://") || url.contains("file://")))
				{					
					mOnPageLoadListener.onPageLoad();
				}
				
				loadCount++;
			}
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{					
			if (mOnLinkClickedListener != null)
			{
				return mOnLinkClickedListener.onLinkClicked(url);
			}
			else
			{
				return super.shouldOverrideUrlLoading(view, url);
			}
		}	
	};
	
	private class ViewChromeClient extends WebChromeClient
	{ 
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID)
		{		
			Debug.out("WebClient: (" + lineNumber + ") " + message);
		}
	}

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
	
	public interface OnLinkClickedListener
	{
		public boolean onLinkClicked(String url);		
	};
}
