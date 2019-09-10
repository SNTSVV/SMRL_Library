package smrl.mr.language;

public interface Output {

	/**
	 * @return the last redirect URL will be returned
	 */
	public String redirectURL();
	
	public boolean isError();
	
	/**
	 * Check if the last output contains an empty file
	 * @return true if the last output containing an empty file
	 */
	public boolean isEmptyFile();
	
	public boolean hasStrictTransportSecurityHeader();
	
	public String getChannel();
	
	public Session getSession();	// get the last session status
	
	public Session getSession(int pos);	//get the session status after executing the action at the pos position 

}
