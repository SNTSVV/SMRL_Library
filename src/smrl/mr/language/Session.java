package smrl.mr.language;

public interface Session {

	/**
	 * @return the number of seconds since now until the session expires
	 */
	public long getTimeout();

	public void update(Object session);
	
}
