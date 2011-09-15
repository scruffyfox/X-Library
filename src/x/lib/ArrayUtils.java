/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import android.view.View;

/**
* @brief This class is for applying a function to each cell in an array 
*/
public class ArrayUtils
{
	/**
	* Iterates through an array and applies the filter to each cell.
	* @param arr The input array to be iterated
	* @param iterator The iterator command to apply to the array
	*/
	public static Object[] iterateCommand(Object[] arr, CommandIterator iterator)
	{		
		int count = arr.length;
		for (int index = 0; index < count; index++)
		{
			arr[index] = iterator.doToItem(arr[index], index);
		}
		return arr;			
	}
	
	/**
	 * @brief Interface class to apply to each item in an array.	 
	 */
	interface CommandIterator
	{		
		/**
		 * Function to manipulate the cells in an array
		 * @param item The array item to process
		 * @param position The position of the item in the array
		 * @return The item to be reassigned to the array
		 */
		public Object doToItem(Object item, int position);		
	}
}