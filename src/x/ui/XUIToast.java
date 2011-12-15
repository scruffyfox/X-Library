/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Simple Toast extention to accept listener for when the Toast has finished displaying
 */
public class XUIToast extends Toast
{
	private OnToastFinishedListener mToastFinishedListener;
	private Context mContext;
	
	public XUIToast(Context context)
	{
		super(context);
		mContext = context;
	}
	
	/**
	 * Sets the on toast finished listener for the toast
	 * @param listener The new listener
	 */
	public void setToastFinishedListener(OnToastFinishedListener listener)
	{
		this.mToastFinishedListener = listener;
	}
	
	@Override
	public void show()
	{	
		super.show();
		
		new Handler().postDelayed(new Runnable()
		{			
			public void run()
			{
				if (mToastFinishedListener != null)
				{
					mToastFinishedListener.onToastFinished();
				}
			}
		}, getDuration() + (mContext.getResources().getInteger(android.R.integer.config_longAnimTime) * 2) + 100);				
	}
	
	/**
	 * Interface listener for when the toast popup has been dismissed
	 */
	public interface OnToastFinishedListener
	{
		/**
		 * Called when the toast has finished
		 */
		public void onToastFinished();
	}
}
