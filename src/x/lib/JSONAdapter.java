/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @brief JSON adapter for list views, maps keys to views to be outputted in a view
 */
public class JSONAdapter
{
	protected JSONBaseAdapter mJSONBaseAdapter; 
	protected JSONMapper mJSONMapper;
	private int mResourceLayoutId;
	private Context mContext;
	
	public JSONAdapter(Context context, int resourceLayoutId, JSONMapper mapper)
	{
		mResourceLayoutId = resourceLayoutId;
		mJSONMapper = mapper;
		mContext = context;
		
		init();
	}
	
	public JSONAdapter(Context context, int resourceLayoutId)
	{
		mResourceLayoutId = resourceLayoutId;
		mContext = context;
		
		init();
	}
	
	public void setJSONMapper(JSONMapper mapper)
	{
		mJSONMapper = mapper;
	}
	
	public ArrayAdapter getArrayAdapter()
	{
		return mJSONBaseAdapter;
	}
	
	private void init()
	{
		mJSONBaseAdapter = new JSONBaseAdapter(mContext, mResourceLayoutId, null);
	}
	
	private class JSONBaseAdapter extends ArrayAdapter
	{
		private HashMap<Integer, Integer> mResourceHash;
		private HashMap<Integer, Object> mDataHash;
		private HashMap<Integer, Class> mViewHash;
		private LayoutInflater mLayoutInflater;
		private int layoutResource;
		
		public JSONBaseAdapter(Context context, int resource, Object[] objects)
		{
			super(context, resource, objects);
			
			mResourceHash = mJSONMapper.getResourceHash();
			mDataHash = mJSONMapper.getDataHash();
			mViewHash = mJSONMapper.getViewHash();
			layoutResource = resource;
			
			mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}	
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{		
			if (convertView == null)
			{
				convertView = mLayoutInflater.inflate(layoutResource, null);
			}
			
			int viewId = mResourceHash.get(position);
			Class viewClass = mViewHash.get(position);
			Object data = mDataHash.get(position);
			
			if (viewClass == TextView.class)
			{
				TextView t = (TextView)convertView.findViewById(viewId);
				
				if (t != null)
				{
					t.setText(data.toString());
				}
			}
			else if (viewClass == ImageView.class)
			{
				ImageView i = (ImageView)convertView.findViewById(viewId);
				
				if (i != null)
				{
					i.setBackgroundDrawable((Drawable)data);
				}
			}
			
			return super.getView(position, convertView, parent);
		}
	}
	
	//	Only works with setText and setBackground!
	public class JSONMapper 
	{
		private int mHashMapKey = 0;
		private HashMap<Integer, Integer> mResourceHash = new HashMap<Integer, Integer>();
		private HashMap<Integer, Object> mDataHash = new HashMap<Integer, Object>();
		private HashMap<Integer, Class> mViewHash = new HashMap<Integer, Class>();
		
		public JSONMapper()
		{
			
		}
		
		public JSONMapper(int resourceId, Object data, Class resourceType)
		{
			addValuesToMap(resourceId, data, resourceType);
		}
		
		public JSONMapper(int[] resourceId, Object[] data, Class[] resourceType) throws InvalidParameter		
		{
			int loopCount = resourceId.length;
			int dataCounter = data.length;
			
			for (int index = 0, resourceIndex = 0; index < dataCounter; index++, resourceIndex++)
			{
				if (resourceIndex >= loopCount)
				{
					resourceIndex = 0;
				}
				
				addValuesToMap(resourceId[resourceIndex], data[index], resourceType[resourceIndex]);
			}
		}
				
		public void parseJSONAndArray(int[] resourceId, JSONArray data, Class[] resourceType) throws InvalidParameter
		{
			if ((resourceId.length + data.length() + resourceType.length) / 3 != resourceId.length)
			{
				throw new InvalidParameter("Array counts are not consistent");
			}
		}
		
		private void addValuesToMap(int resourceId, Object data, Class resourceType)
		{
			mResourceHash.put(mHashMapKey, resourceId);
			mDataHash.put(mHashMapKey, data);
			mViewHash.put(mHashMapKey, resourceType);
			
			mHashMapKey++;
		}
		
		public HashMap<Integer, Integer> getResourceHash()
		{
			return mResourceHash;			
		}
		
		public HashMap<Integer, Object> getDataHash()
		{
			return mDataHash;
		}
		
		public HashMap<Integer, Class> getViewHash()
		{
			return mViewHash;
		}
	}
	
	private class InvalidParameter extends Exception
	{		
		public InvalidParameter(String message)
		{
			System.err.println(message);
		}
	}
}
