/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 *
 * @author Callum Taylor
**/
package x.ui;

import x.util.StringUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @brief Custom web view that adds extra method to call javascript methods and set javascript variables.
 * Also has interfaces for click listeners and JS alert listeners
 */
public class XUIWebView extends WebView
{
	private OnPageLoadListener mOnPageLoadListener;
	private OnLinkClickedListener mOnLinkClickedListener;
	private OnAlertListener mOnAlertListener;
	private Context mContext;
	private boolean mHasLoaded = false;

	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUIWebView(Context context)
	{
		super(context);

		mContext = context;
		init();
	}

	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
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

	public boolean hasLoaded()
	{
		return mHasLoaded;
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

	public void callFunction(String functionName)
	{
		loadUrl("javascript: " + functionName + "();");
	}

	public void callFunction(String functionName, String... param)
	{
		loadUrl("javascript: " + functionName + "(" + StringUtils.join(param, ",") + ");");
	}

	/**
	 * Calls a JavaScript function with the params
	 * @param functionName The function name to call
	 * @param param Array of params to pass
	 */
	public void callFunction(String functionName, Object... param)
	{
		String params = "";

		for (int index = 0; index < param.length; index++)
		{
			if (param[index] instanceof Integer ||
				param[index] instanceof Boolean ||
				param[index] instanceof Double ||
				param[index] instanceof Float)
			{
				params += "" + param[index] + ", ";
			}
			else
			{
				params += "\"" + param[index].toString() + "\", ";
			}
		}

		params = params.substring(0, params.length() - 2);
		loadUrl("javascript: " + functionName + "(" + params + ");");
	}

	/**
	 * @brief Class to handle page loading and url clicking
	 */
	private class ViewClient extends WebViewClient
	{
		int loadCount = 0;

		@Override public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			{
				if (mOnLinkClickedListener != null)
				{
					Uri uri = Uri.parse(url);
					mOnLinkClickedListener.onLinkClicked(uri);
				}
			}

			super.onPageStarted(view, url, favicon);
		}

		/**
		 * Called when the page has finished loading
		 * @param view The webview where the page has finished loading
		 * @param url The url of the page that has been loaded
		 */
		@Override public void onPageFinished(WebView view, String url)
		{
			mHasLoaded = true;

			//if (loadCount < 1)
			{
				if (mOnPageLoadListener != null && (url.contains("http://") || url.contains("https://") || url.contains("file://")))
				{
					mOnPageLoadListener.onPageLoad();
				}
			}
		}

		/**
		 * Called when a link is clicked in the web view
		 * @param view The webview where the link was clicked
		 * @param url The url of the link that was clicked
		 */
		@Override public boolean shouldOverrideUrlLoading(WebView view, String url)
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
	 * @brief Class for custom console message handling and alert dialogs
	 */
	private class ViewChromeClient extends WebChromeClient
	{
		/**
		 * Called when a javascript alert has been called
		 * @param view The webview where the alert was called
		 * @param url The url of the page where the alert was called
		 * @param message The message content of the alert
		 * @param result The result of the alert view
		 * @return If the alert was handled or not
		 */
		@Override public boolean onJsAlert(WebView view, String url, String message, JsResult result)
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
