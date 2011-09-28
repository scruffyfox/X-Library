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
public class ItemList<E> extends ArrayList
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
	
	/**
	 * Gets an object at the specified index
	 * @return the object pre-casted to the pseudo-type
	 */
	public E get(int index)
	{	
		return (E)super.get(index);
	}

	/**
	 * Removes all items from an index to the end
	 * @param from The index to start from
	 */
	public void removeFrom(int from)
	{
		removeFrom(from, this.size() - from);
	}
	
	/**
	 * Removes the length of index starting from 'from'
	 * @param from The index to start from
	 * @param length The length to remove
	 */
	public void removeFrom(int from, int length)
	{
		for (int index = from; index < length + from - 1; index++)
		{
			remove(index);
		}
	}
}