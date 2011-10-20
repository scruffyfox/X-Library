package x.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * This class is used to register application crashes with a server. (Original from http://code.google.com/p/android-remote-stacktrace)
 */
public class ApplicationManager
{
	private static String[] stackTraceFileList = null;
	
	/**
	 * Register handler for unhandled exceptions.
	 * @param context
	 */
	public static boolean register(Context context)
	{
		
		// Get information about the Package
		PackageManager pm = context.getPackageManager();
		try
		{
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);			
			ApplicationInfo.applicationVersion = pi.versionName;
			ApplicationInfo.applicationPackage = pi.packageName;
			ApplicationInfo.applicationFileLocation = context.getFilesDir().getAbsolutePath();
			ApplicationInfo.androidModel = android.os.Build.MODEL;
			ApplicationInfo.androidVersion = android.os.Build.VERSION.RELEASE;			
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		boolean stackTracesFound = false;
		
		//	We'll return true if any stack traces were found
		if (searchForStackTraces().length > 0)
		{
			stackTracesFound = true;
		}
		
		new Thread()
		{
			@Override
			public void run()
			{
				//	First of all transmit any stack traces that may be lying around				
				submitStackTraces();				
				UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
				
				//	Don't register again if already registered
				if (!(currentHandler instanceof UncaughtExceptionHandler))
				{
					//	Register default exceptions handler
					Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
					{						
						public void uncaughtException(Thread thread, Throwable ex)
						{
							//	Here you should have a more robust, permanent record of problems
							final Writer result = new StringWriter();
							final PrintWriter printWriter = new PrintWriter(result);
							
							try
							{
								//	Random number to avoid duplicate files
								Random generator = new Random();
								int random = generator.nextInt(99999);
								
								//	Embed version in stacktrace filename
								String filename = ApplicationInfo.applicationVersion + "-" + Integer.toString(random);
								
								//	Write the stacktrace to disk
								BufferedWriter bos = new BufferedWriter(new FileWriter(ApplicationInfo.applicationFileLocation + "/" + filename + ".stacktrace"));
								bos.write(ApplicationInfo.androidVersion + "\n");
								bos.write(ApplicationInfo.androidModel + "\n");
								bos.write(result.toString());
								bos.flush();
								bos.close();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							
							uncaughtException(thread, ex);
						}
					});
				}
			}
		}.start();
		
		return stackTracesFound;
	}
	
	/**
	 * Register handler for unhandled exceptions.
	 * @param context
	 * @param Url
	 */
	public static void register(Context context, String url)
	{
		//	Use custom URL
		ApplicationInfo.serverUrl = url;
		
		//	Call the default register method
		register(context);
	}
	
	/**
	 * Search for stack trace files.
	 * @return string array of found stack traces
	 */
	private static String[] searchForStackTraces()
	{
		if (stackTraceFileList != null)
		{
			return stackTraceFileList;
		}
		
		File dir = new File(ApplicationInfo.applicationFileLocation + "/");
		
		//	Try to create the files folder if it doesn't exist
		dir.mkdir();
		
		//	Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".stacktrace");
			}
		};
		
		return (stackTraceFileList = dir.list(filter));
	}
	
	/**
	 * Look into the files folder to see if there are any "*.stacktrace" files.
	 * If any are present, submit them to the trace server.
	 */
	public static void submitStackTraces()
	{
		try
		{
			String[] list = searchForStackTraces();
			
			if (list != null && list.length > 0)
			{
				for (int index = 0; index < list.length; index++)
				{
					String filePath = ApplicationInfo.applicationFileLocation + "/" + list[index];
					
					//	Extract the version from the filename:
					//	"packagename-version-...."
					String version = list[index].split("-")[0];
					
					//	Read contents of stacktrace
					StringBuilder contents = new StringBuilder();
					BufferedReader input = new BufferedReader(new FileReader(filePath));
					String line = null;
					String androidVersion = null;
					String phoneModel = null;
					
					while ((line = input.readLine()) != null)
					{
						if (androidVersion == null)
						{
							androidVersion = line;
							continue;
						}
						else if (phoneModel == null)
						{
							phoneModel = line;
							continue;
						}
						
						contents.append(line);
						contents.append(System.getProperty("line.separator"));
					}
					
					input.close();
					String stacktrace;
					stacktrace = contents.toString();
					
					//	Transmit stack trace with POST request
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(ApplicationInfo.serverUrl);
					
					List<NameValuePair> postData = new ArrayList<NameValuePair>();
					postData.add(new BasicNameValuePair("package_name", ApplicationInfo.applicationPackage));
					postData.add(new BasicNameValuePair("package_version", version));
					postData.add(new BasicNameValuePair("phone_model", phoneModel));
					postData.add(new BasicNameValuePair("android_version", androidVersion));
					postData.add(new BasicNameValuePair("stacktrace", stacktrace));
					httpPost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
					
					//	We don't care about the response, so we just hope it went
					//	Well and on with it
					httpClient.execute(httpPost);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				String[] list = searchForStackTraces();
				for (int index = 0; index < list.length; index++)
				{
					File file = new File(ApplicationInfo.applicationFileLocation + "/" + list[index]);
					file.delete();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Class for storing the application info
	 */
	static class ApplicationInfo
	{
		public static String applicationFileLocation = null;
		public static String applicationVersion = "unknown";
		public static String applicationPackage = "unknown";
		public static String androidVersion = "unknown";
		public static String androidModel = "unknown";
		public static String serverUrl = "http://android.3sidedcube.com/server.php";
	}
}
