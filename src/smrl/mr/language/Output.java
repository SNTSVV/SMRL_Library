package smrl.mr.language;

import java.io.File;

public interface Output {

	/**
	 * @return the last redirect URL will be returned
	 */
	public String redirectURL();
	
	public boolean isError();
	
	/**
	 * Check if the last output contains an empty file
	 * @return true if the last output contains an empty file
	 */
	public boolean isEmptyFile();
	
	/**
	 * @return File of the last output
	 */
	public File file();
	
	/**
	 * Check if the last output contains a file
	 * @return true if the last output does not contain any file
	 */
	public boolean noFile();
	
	public boolean hasStrictTransportSecurityHeader();
	
	public String getChannel();
	
	public Session getSession();	// get the last session status
	
	public Session getSession(int pos);	//get the session status after executing the action at the pos position 

}
