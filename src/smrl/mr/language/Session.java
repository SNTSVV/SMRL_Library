package smrl.mr.language;

import java.util.Set;

import org.openqa.selenium.Cookie;

public interface Session {

	public long getTimeout();

	public void update(Object session);
	
}
