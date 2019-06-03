package smrl.mr.analysis;

import java.net.URL;
import java.security.CodeSource;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		CodeSource src = com.google.common.util.concurrent.SimpleTimeLimiter.class.getProtectionDomain().getCodeSource();
		if (src != null) {
		    URL jar = src.getLocation();
		    System.out.println(jar);
		}
	}

}
