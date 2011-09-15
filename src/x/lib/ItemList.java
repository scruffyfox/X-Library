/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.util.ArrayList;

/**
 * @brief Extends arraylist to allow adding multiple items inline
 * @param <E>
 */
class ItemList<E> extends ArrayList
{
	/**
	 * Default Constructor
	 */
	public ItemList()
	{
	}
		
	/**
	 * Default Constructor
	 * @param object The object to add to the list
	 */
	public ItemList(E... object)
	{
		add(object);
	}
		
	/**
	 * Method to add items to the list
	 * @param object Array of items to add to the list 
	 * @return true
	 */
	public boolean add(E... object)
	{
		for (E o : object)
		{
			add(o);
		}
		
		return true;
	}
}