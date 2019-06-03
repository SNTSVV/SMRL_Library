package smrl.mr.language;

public interface Output {

	/**
	 * @return the last redirect URL will be returned
	 */
	public String redirectURL();
	
	public boolean isError();
	
	public boolean hasStrictTransportSecurityHeader();
	
	public String getChannel();
	
	public Session getSession();	// get the last session status
	
	public Session getSession(int pos);	//get the session status after executing the action at the pos position 

}
