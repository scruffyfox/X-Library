package x.type;

/**
 * @brief This class is used to create an object collection of files to use when posting with AsyncHttpClient.
 */
public class FileHttpParams 
{
	private ItemList<String> fieldNames;
	private ItemList<String> fileNames;
	private ItemList<String> fileTypes;
	private ItemList<byte[]> files;
	
	/**
	 * Default constructor
	 */
	public FileHttpParams()
	{
		fieldNames = new ItemList<String>();
		fileNames = new ItemList<String>();
		fileTypes = new ItemList<String>();
		files = new ItemList<byte[]>();
	}		
	
	/**
	 * Adds a file to the collection
	 * @param fieldName The field name to post
	 * @param fileName The filename to post
	 * @param fileType The filetype to post. Based on MIME types E.G. "image/png", "text/plain"
	 * @param fileContents The file contents as a byte array
	 */
	public void addFile(String fieldName, String fileName, String fileType, byte[] fileContents)
	{
		fieldNames.add(fieldName);
		fileNames.add(fileName);
		fileTypes.add(fileType);
		files.add(fileContents);
	}
	
	/**
	 * Gets the size of the collection
	 * @return
	 */
	public int size()
	{
		return fileNames.size();
	}
	
	/**
	 * Gets the field name from an index
	 * @param index The index
	 * @return The field name
	 */
	public String getFieldName(int index)
	{
		return fieldNames.get(index);
	}
	
	/**
	 * Gets the file name from an index
	 * @param index The index
	 * @return The field name
	 */
	public String getFileName(int index)
	{
		return fileNames.get(index);
	}
	
	/**
	 * Gets the file type from an index
	 * @param index The index
	 * @return The field name
	 */
	public String getFileType(int index)
	{
		return fileTypes.get(index);
	}
	
	/**
	 * Gets the field data from an index
	 * @param index The index
	 * @return The field name
	 */
	public byte[] getFileContents(int index)
	{
		return files.get(index);
	}
}