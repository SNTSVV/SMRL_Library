package smrl.mr.singleTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRBaseTest;
import smrl.mr.owasp.OTG_AUTHN_004;
import smrl.mr.owasp.OTG_AUTHN_010;
import smrl.mr.owasp.OTG_AUTHZ_001a;
import smrl.mr.owasp.OTG_AUTHZ_001b;
import smrl.mr.owasp.OTG_AUTHZ_002;
import smrl.mr.owasp.OTG_AUTHZ_002a;
import smrl.mr.owasp.OTG_AUTHZ_002b;
import smrl.mr.owasp.OTG_AUTHZ_002c;
import smrl.mr.owasp.OTG_AUTHZ_002d;
import smrl.mr.owasp.OTG_AUTHZ_002e;
import smrl.mr.owasp.OTG_INPVAL_004;
import smrl.mr.owasp.OTG_SESS_003;

public class SingleTest extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "jenkins";
//	private static String system = "iws";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.out.println("*** Starting time: " + getCurrentTime() +" ***");
		
		//by default, the SUT is the Jenkins
		String configFile = "./testData/Jenkins/jenkinsSysConfig.json";
		
		if(SingleTest.system.equals("iws")){
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
	public void test_OTG_AUTHN_004() {
		super.test(provider,OTG_AUTHN_004.class);
	}
	
	@Test
	public void test_OTG_AUTHN_010() {
		super.test(provider,OTG_AUTHN_010.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_001a() {
		super.test(provider,OTG_AUTHZ_001a.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999006"
	  */
	public void test_OTG_AUTHZ_001b() {
		super.test(provider,OTG_AUTHZ_001b.class);
	}

	@Test
	/**
	  *This test case should detect CVE-2018-1999004"
	  */
	public void test_OTG_AUTHZ_002() {
		super.test(provider,OTG_AUTHZ_002.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999004"
	  */
	public void test_OTG_AUTHZ_002a() {
		super.test(provider,OTG_AUTHZ_002a.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999003 and CVE-2018-1999004
	  */
	public void test_OTG_AUTHZ_002b() {
		super.test(provider,OTG_AUTHZ_002b.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999046 and CVE-2018-1999004"
	  */
	public void test_OTG_AUTHZ_002c() {
		super.test(provider,OTG_AUTHZ_002c.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1000406"
	  */
	public void test_OTG_AUTHZ_002d() {
		super.test(provider,OTG_AUTHZ_002d.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999003"
	  */
	public void test_OTG_AUTHZ_002e() {
		super.test(provider,OTG_AUTHZ_002e.class);
	}
	
	@Test
	public void test_OTG_INPVAL_004() {
		super.test(provider,OTG_INPVAL_004.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1000409"
	  */
	public void test_OTG_SESS_003() {
		super.test(provider,OTG_SESS_003.class);
	}

	public static String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now).toString();  
	}

}
