package smrl.mr.singleTest;

import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRBaseTest;
//import smrl.mr.owasp.OTG_AUTHZ_001b;
//import smrl.mr.owasp.OTG_AUTHZ_002;
//import smrl.mr.owasp.OTG_AUTHZ_001b;
//import smrl.mr.owasp.OTG_AUTHZ_002;
//import smrl.mr.owasp.OTG_AUTHZ_002a;
//import smrl.mr.owasp.OTG_AUTHZ_002b;
//import smrl.mr.owasp.OTG_AUTHZ_002c;
//import smrl.mr.owasp.OTG_AUTHZ_002d;
//import smrl.mr.owasp.OTG_INPVAL_004;
//import smrl.mr.owasp.OTG_SESS_003;
//import smrl.mr.owasp.OTG_AUTHZ_002a;
//import smrl.mr.owasp.OTG_AUTHZ_002b;
//import smrl.mr.owasp.OTG_AUTHZ_002c;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleTest extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "jenkins";
//	private static String system = "iws";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		//by default, the SUT is the Jenkins
//		String configFile = "./testData/Jenkins/jenkinsSysConfig.json";
		String configFile = "./testData/Jenkins/collectedData/jenkinsSysConfig.json";
		
		if(SingleTest.system.equals("iws")){
			configFile = "./testData/IWS/iwsSysConfig.json";
		}
		
		
		provider = new WebOperationsProvider(configFile);
//		provider.coverAllUrls("user2", "user1");
	}

	@Before
	public void setUp() throws Exception {
		setProvider(provider);
	}
	
//	
//	@Test
//	public void test_OTG_AUTHZ_001b() {
//		super.test(provider,OTG_AUTHZ_001b.class);
//	}
//	
//
//	@Test
//	public void test_OTG_AUTHZ_002() {
//		super.test(provider,OTG_AUTHZ_002.class);
//	}
//	
//	@Test
//	/**
//	  *This test case should detect CVE-2018-1999004"
//	  */
//	public void test_OTG_AUTHZ_002a() {
//		super.test(provider,OTG_AUTHZ_002a.class);
//	}
//	
//	@Test
//	/**
//	  *This test case should detect CVE-2018-1999003"
//	  */
//	public void test_OTG_AUTHZ_002b() {
//		super.test(provider,OTG_AUTHZ_002b.class);
//	}
//	
//	@Test
//	/**
//	  *This test case should detect CVE-2018-1999046"
//	  */
//	public void test_OTG_AUTHZ_002c() {
//		super.test(provider,OTG_AUTHZ_002c.class);
//	}
	
//	@Test
//	/**
//	  *This test case should detect CVE-2018-1000406"
//	  */
//	public void test_OTG_AUTHZ_002d() {
//		super.test(provider,OTG_AUTHZ_002d.class);
//	}
//	
//	@Test
//	public void test_OTG_INPVAL_004() {
//		super.test(provider,OTG_INPVAL_004.class);
//	}
//	
//	@Test
//	/**
//	  *This test case should detect CVE-2018-1000409"
//	  */
//	public void test_OTG_SESS_003() {
//		super.test(provider,OTG_SESS_003.class);
//	}
//	
	@Test
	public void test_cover_urls() {
		assertTrue(provider.coverAllUrls("user2", "user1"));
//		assertTrue(provider.coverAllUrls("admin", "user1"));
//		assertTrue(provider.coverAllUrls("user1", "user2"));
//		assertTrue(provider.coverAllUrls("user1", "admin"));
	}
}
