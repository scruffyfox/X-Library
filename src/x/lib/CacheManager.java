/**
 * x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.lib;

import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.FileObserver;
import android.provider.OpenableColumns;
import android.util.Log;

/**
 * @brief This class is used to store and retrive data to the user's phone in a serialized form
 */
public class CacheManager implements Serializable
{
	private Context context;
	private ArrayList<String> fileNames;
	private String mPackageName;
	
	/**
	 * The default constructor
	 * @param context The application's context
	 * @param packageName The application's unique package name identifier
	 */
	public CacheManager(Context context, String packageName)
	{
		this.context = context;
		this.mPackageName = packageName;
		fileNames = new ArrayList<String>();
	}

	/**
	 * Gets an MD5 hash of an input string
	 * @param input The input string
	 * @return The MD5 hash of the input string
	 */
	public String getMD5(String input)
	{
		String hashFileName = "";
		
    	try
    	{
    		MessageDigest md5 = MessageDigest.getInstance("MD5");
    		hashFileName = Base64.encodeBytes((md5.digest(input.getBytes()))).replace('/', '.');
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return hashFileName;
	}
	
	/**
	 * Gets the total size of the cache in bytes
	 * @return The size of the cache in bytes
	 */
	public long getCacheSize()
	{
		File files = this.context.getCacheDir();
				
		FileFilter filter = new FileFilter()
		{
			public boolean accept(File arg0) 
			{				
				if (arg0.getName().contains("cache_"))
				{
					return true;					
				}
				
				return false;
			};
		};
		
		File[] fileList = files.listFiles(filter);
		
		long totalSize = 0;
		for (File f : fileList)
		{
			totalSize += f.length();	
		}
						
		return totalSize;
	}
	
	/**
	 * Checks if a file exists within the cache
	 * @param fileName The file to check
	 * @return True if the file exists, false if not
	 */
	public boolean fileExists(String fileName)
	{
		try
		{
			File f = new File(this.context.getCacheDir().getAbsolutePath(), "cache_" + fileName);			
			
			return f.exists();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Gets the modified date of the file
	 * @param fileName The file
	 * @return The modified date in ms since 1970 (EPOCH)
	 */
	public long fileModifiedDate(String fileName)
	{
		File f = new File(this.context.getCacheDir().getAbsoluteFile(), "cache_" + fileName);
		
		return f.lastModified();
	}
	
	/**
	 * Checks if a file was created before a certain date
	 * @param fileName The file to check
	 * @param date The date to check against
	 * @return True if the file is older, false if not
	 */
	public boolean fileOlderThan(String fileName, long date)
	{
		long lastDate = fileModifiedDate(fileName);
				
		if (lastDate > date)
		{						
			return false;
		}		
		
		return true;
	}
	
	/**
	 * Checks if the cache has reached the user's cache limit stored in user preference as "cacheLimit"
	 * and removes the oldest files to make space
	 */
	public void checkCacheLimit()
	{
		SharedPreferences mPrefs = context.getSharedPreferences(mPackageName, Context.MODE_WORLD_WRITEABLE);
        long currentCacheLimit = mPrefs.getInt("cacheLimit", 10) * 1024 * 1024;
        long currentUsed = getCacheSize();
        
        if (currentCacheLimit > currentUsed) return;
        
		File files = this.context.getCacheDir();		
		FileFilter filter = new FileFilter()
		{
			public boolean accept(File arg0) 
			{				
				if (arg0.getName().contains("cache_"))
				{
					return true;					
				}
				
				return false;
			};
		};
		
		Comparator c = new Comparator<File>()
		{
			public int compare(File object1, File object2)
			{
				if (object1.lastModified() > object2.lastModified())
				{
					return 1;
				}
				else if (object1.lastModified() < object2.lastModified())
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}			
		};
		
		File[] fileList = files.listFiles(filter);
		Arrays.sort(fileList, c);
		
		for (File f : fileList)
		{
			if (currentUsed > currentCacheLimit)
			{
				currentUsed -= f.length();
				f.delete();
			}
			else
			{
				break;			
			}
		}
	}
	
	/**
	 * Adds an image to the cache
	 * @param fileName The file name for the file
	 * @param fileContents The contents for the file
	 * @return true
	 */
	public boolean addImage(String fileName, Bitmap fileContents)
	{
		return addImage(fileName, fileContents, Bitmap.CompressFormat.PNG);
	}
	
	/**
	 * Adds an image to the cache
	 * @param fileName The file name for the file
	 * @param fileContents The contents for the file
	 * @param format The compression format for the image
	 * @return true
	 */
	public boolean addImage(String fileName, Bitmap fileContents, Bitmap.CompressFormat format)
	{					
		AddFileRunnable r = new AddFileRunnable(fileName, fileContents, format)
		{						
			public void run()
			{
				try
				{			
					File outputPath = new File(context.getCacheDir().getPath(), "cache_" + mFileName);
										
					FileOutputStream output = new FileOutputStream(outputPath);										
					mImage.compress(mFormat, 60, output);
								
					output.flush();
					output.close();	
															
					//	Now delete to make up for more room
					checkCacheLimit();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					checkCacheLimit();
				}
			}
		};				
		
		r.start();
		
		return true;
	}
	
	/**
	 * Adds a file to the cache
	 * @param fileName The file name for the file
	 * @param fileContents The contents for the file
	 * @return true
	 */
	public boolean addFile(String fileName, Serializable fileContents)
	{
		final Serializable fFileContents = fileContents;
		final String fFileName = fileName;
		
		Thread r = new Thread()
		{						
			public void run()
			{
				try
				{			
					File outputPath = new File(context.getCacheDir().getPath(), "cache_" + fFileName);
					FileOutputStream output = new FileOutputStream(outputPath);
					ObjectOutputStream stream = new ObjectOutputStream(output);
					stream.writeObject(fFileContents);
					stream.flush();
					output.flush();
					stream.close();
					output.close();		
					
					//	Now delete to make up for more room
					checkCacheLimit();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					
					checkCacheLimit();
				}
			}
		};				
		
		r.start();
		
		return true;		
	}
	
	/**
	 * Reads an image from cache
	 * @param fileName The image to retrieve
	 * @return The file as a bitmap or null if there was an OutOfMemoryError or Exception
	 */
	public Bitmap readImage(String fileName)
	{
		try
		{					
			File file = new File(context.getCacheDir().getAbsolutePath(), "cache_" + fileName);
			FileInputStream input = new FileInputStream(file);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inDither = true;						
			
			Bitmap b = BitmapFactory.decodeStream(input, null, opts);	
			
			input.close();	
			
			return b;
		}
		catch (OutOfMemoryError e)
		{
			return null;
		}
		catch (Exception e)
		{
			return null;
		}
	}	
	
	/**
	 * Reads a file from cache
	 * @param fileName The file to retrieve
	 * @return The file as an Object or null if there was an OutOfMemoryError or Exception
	 */
	public Object readFile(String fileName)
	{
		try
		{
			File file = new File(context.getCacheDir().getAbsolutePath(), "cache_" + fileName);
			FileInputStream input = new FileInputStream(file);
			ObjectInputStream stream = new ObjectInputStream(input);
			Object data = stream.readObject();
			stream.close();
			input.close();
			
			return data;
		}
		catch (OutOfMemoryError e)
		{
			return null;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Clears the cache
	 */
	public void clearCache()
	{
		clearCache(false);
	}

	/**
	 * Clears the cache
	 * @param showProgress Shows a dialog or not
	 */
	public void clearCache(boolean showProgress)
	{
		ProgressDialog dialog = new ProgressDialog(context);
		if (showProgress)
		{			
			dialog.setMessage("Clearing Cache");
			dialog.show();
		}
		
		File files = this.context.getCacheDir();		
		FileFilter filter = new FileFilter()
		{
			public boolean accept(File arg0) 
			{				
				if (arg0.getName().contains("cache_"))
				{
					return true;					
				}
				
				return false;
			};
		};
		
		File[] fileList = files.listFiles(filter);
		double totalSize = 0;
		for (File f : fileList)
		{
			f.delete();
		}
		
		if (showProgress)
		{
			dialog.dismiss();
		}
	}
	
	/**
	 * The class that adds files to the cache in its own thread
	 */
	private class AddFileRunnable extends Thread
	{
		protected String mFileName;
		protected Bitmap mImage;
		protected Bitmap.CompressFormat mFormat;		
		
		public AddFileRunnable()
		{
			
		}
		
		public AddFileRunnable(String fileName, Bitmap image, Bitmap.CompressFormat format)
		{
			mFileName = fileName;
			mImage = image;
			mFormat = format;
		}
	}
}

/**
 * @brief The class that serailizes data
 */
class Serializer implements Serializable
{
	/**
	 * Serializes data into bytes
	 * @param data The data to be serailized
	 * @return The serialized data in a byte array
	 */
	public static byte[] serializeObject(Object data)
	{		
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(data);
			byte[] yourBytes = bos.toByteArray(); 		
			
			return yourBytes;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}					
	}
	
	/**
	 * Deserailizes data into an object
	 * @param data The byte array to be deserialized
	 * @return The data as an object
	 */
	public static Object desterializeObject(byte[] data)
	{
		try 
		{
			ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(data));
			Object objectData = input.readObject();
			input.close();
			
			return objectData;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}