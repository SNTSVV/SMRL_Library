package smrl.mr.singleTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRBaseTest;
import smrl.mr.owasp.OTG_AUTHN_001;
import smrl.mr.owasp.OTG_AUTHN_004;
import smrl.mr.owasp.OTG_AUTHN_010;
import smrl.mr.owasp.OTG_AUTHZ_001;
import smrl.mr.owasp.OTG_AUTHZ_001b;
import smrl.mr.owasp.OTG_AUTHZ_001b2;
import smrl.mr.owasp.OTG_AUTHZ_002;
import smrl.mr.owasp.OTG_AUTHZ_002a;
import smrl.mr.owasp.OTG_AUTHZ_002b;
import smrl.mr.owasp.OTG_AUTHZ_002c;
import smrl.mr.owasp.OTG_AUTHZ_002d;
import smrl.mr.owasp.OTG_AUTHZ_002e;
import smrl.mr.owasp.OTG_AUTHZ_003;
import smrl.mr.owasp.OTG_AUTHZ_004;
import smrl.mr.owasp.OTG_BUSLOGIC_005;
import smrl.mr.owasp.OTG_CONFIG_007;
import smrl.mr.owasp.OTG_CRYPST_004;
import smrl.mr.owasp.OTG_INPVAL_003;
import smrl.mr.owasp.OTG_INPVAL_004;
import smrl.mr.owasp.OTG_SESS_003;
import smrl.mr.owasp.OTG_SESS_006;
import smrl.mr.owasp.OTG_SESS_007;
import smrl.mr.owasp.OTG_SESS_008;

public class ICSE2020DemoTest extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "jenkins";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.out.println("*** Starting time: " + getCurrentTime() +" ***");
		
		//by default, the SUT is the Jenkins
		String configFile = "./testData/Jenkins/jenkinsSysConfigDEMO.json";
//		String configFile = "./testData/Jenkins/jenkinsSysConfig_withProxy.json";
//		String configFile = "./testData/Jenkins/jenkinsSysConfig_trying.json";
		
		if(ICSE2020DemoTest.system.equals("iws")){
			configFile = "./testData/IWS/iwsSysConfig.json";
		}
		
		provider = new WebOperationsProvider(configFile);
	}

	@Before
	public void setUp() throws Exception {
		setProvider(provider);
	}
	
	@AfterClass
    public static void printEndingTime() {
		System.out.println("*** Ending time: " + getCurrentTime() + " ***");
    }   
	
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999004". A malicious user can start an agent.
	  */
	public void test_OTG_AUTHZ_002() {
		super.test(provider,OTG_AUTHZ_002.class);
	}
	
	
	
	public static String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now).toString();  
	}

}
