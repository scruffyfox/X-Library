/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;

import x.lib.Debug;
import x.util.StringUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @brief Custom web view that adds extra method to call javascript methods and set javascript variables. Also has interfaces for click listeners and JS alert listeners
 */
public class XUIWebView extends WebView
{
	private OnPageLoadListener mOnPageLoadListener;
	private OnLinkClickedListener mOnLinkClickedListener;
	private OnAlertListener mOnAlertListener;
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
	
	/**
	 * Sets the alert listener
	 * @param mOnAlertListener
	 */
	public void setOnAlertListener(OnAlertListener listener)
	{
		this.mOnAlertListener = listener;
	}
	
	/**
	 * Initializes the view
	 */
	private void init()
	{
		ViewClient mViewClient = new ViewClient();		
		setWebViewClient(mViewClient);	
		
		setWebChromeClient(new ViewChromeClient());
		
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
	
	/**
	 * Class to handle page loading and url clicking
	 */
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
				Uri uri = Uri.parse(url);
				if (mOnLinkClickedListener.onLinkClicked(uri))
				{
					return true;
				}
			}
			
			return super.shouldOverrideUrlLoading(view, url);			
		}
	};
	
	/**
	 * Class for custom console message handling and alert dialogs
	 */
	private class ViewChromeClient extends WebChromeClient
	{ 		
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result)
		{
			if (mOnAlertListener != null)
			{
				if (mOnAlertListener.onAlert(message, url))
				{
					result.cancel();					
					return true;
				}
			}			
			
			return super.onJsAlert(view, url, message, result);			
		}				
	}

	/**
	 * @brief The interface for page load 
	 */
	public interface OnPageLoadListener
	{
		/**
		 * Called when the page has been loaded
		 */
		public void onPageLoad();
	};
	
	/**
	 * @brief The interface for the onclick event in the page.
	 */ 
	public interface OnLinkClickedListener
	{
		/**
		 * Called when a link is clicked in the web page
		 * @param url The url of the clicked link
		 * @return True if the event is handled, false if not.
		 */
		public boolean onLinkClicked(Uri url);		
	};
	
	/**
	 * @brief The interface for when javascript triggers an alert
	 */
	public interface OnAlertListener
	{
		/**
		 * Called when the javascript alert method is called
		 * @param message The message in the alert
		 * @param url The url of the page the alert called from
		 * @return
		 */
		public boolean onAlert(String message, String url);
	};
}
