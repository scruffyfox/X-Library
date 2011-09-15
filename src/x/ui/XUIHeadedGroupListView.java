/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.lang.reflect.Array;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class XUIHeadedGroupListView extends FrameLayout
{
	protected Context mContext;
	private ListViewAdapter mListViewAdapter;
	
	public XUIHeadedGroupListView(Context context)
	{
		super(context);
		mContext = context;
		
		init();
	}
	
	public XUIHeadedGroupListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		
		init();
	}
	
	public void init()
	{
		mListViewAdapter = new ListViewAdapter(mContext);	
				
		//this.addView(mListViewAdapter);
		
		RelativeLayout t = new RelativeLayout(mContext);
		t.setLayoutParams(new LayoutParams(-1, 20));
		t.setBackgroundColor(0xffff0000);
		//t.setGravity(Gravity.TOP);
		
		this.addView(t);
	}
	
	public void setAdapter(ListAdapter listAdapter)
	{
		if (listAdapter instanceof XUIHeadedGroupListAdapter)
		{
			mListViewAdapter.setAdapter(listAdapter);
		}
		else
		{			
			mListViewAdapter.setAdapter(listAdapter);
		}
	}
	
	public ListViewAdapter getListView()
	{
		return mListViewAdapter;
	}
	
	protected class ListViewAdapter extends ListView
	{
		public ListViewAdapter(Context context)
		{
			super(context);
		}
		
		public ListViewAdapter(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}		
	}
}

class XUIHeadedGroupListAdapter extends ArrayAdapter<GroupHashMap>
{
	public XUIHeadedGroupListAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);		
	}		
}

/**
 * Group Hash Map
 * @param <K> Key (Group Name)
 * @param <V> Value (Array Data)
 * 
 * @code
 * //HashMap:-
 * // 	GroupName:-
 * // 		Item
 * // 		Item
 * // 		Item
 * // 	GroupName:-
 * // 		Item
 * // 		Item
 * 
 * HashMap<String, String[]> objects = new HashMap<String, String[]>();
 * objects.put("GroupName", new String[]{"Item", "Item", "Item"});
 * objects.put("GroupName", new String[]{"Item", "Item"});
 * 
 * GroupHashMap<String, String> dataObjects = new GroupHashMap<String, String>();
 * dataObjects.setObjects(objects);
 * @endcode
 */
class GroupHashMap<K, V>
{
	private HashMap<K, V[]> dataObjects = new HashMap<K, V[]>();
	
	public GroupHashMap()
	{
		
	}
	
	public GroupHashMap(HashMap<K, V[]> data)
	{
		setObjects(data);
	}
	
	public void setObjects(HashMap<K, V[]> data)
	{
		dataObjects = data;
	}
}