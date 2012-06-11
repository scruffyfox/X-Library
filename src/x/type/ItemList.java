/**
 * @brief x type is the type library which includes the commonly used data types in the X Library lib
 * 
 * @author Callum Taylor
**/
package x.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @brief Extends arraylist to allow adding multiple items inline
 * @param <E>
 */
public class ItemList<E> extends ArrayList<E> implements Serializable
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
	 * Interface for use with {@link map}
	 * @param <E> The type of the ItemList
	 */
	public interface MapInterface<E>
	{
		/**
		 * Called on each iteration in the current list
		 * @param item The item in the list
		 * @param index The index of the item
		 * @return The item that has been changed. This will replace the original value
		 */
		public E apply(E item, int index);
	}
	
	/**
	 * Loops through the current list and calls the {@link MapInterface.apply} method on each item
	 * @param itemInterface The map interface to use
	 */
	public void map(MapInterface itemInterface)
	{
		int count = size();
		for (int index = 0; index < count; index++)
		{
			set(index, (E)itemInterface.apply(get(index), index));
		}
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
		removeFrom(from, this.size() - (from + 1));
	}
	
	/**
	 * Removes the length of index starting from 'from'
	 * @param from The index to start from
	 * @param length The length to remove
	 */
	public void removeFrom(int from, int length)
	{
		for (int index = from; index <= length + from; index++)
		{
			//Debug.out("i: " + index);
			remove(from);
		}
	}
}