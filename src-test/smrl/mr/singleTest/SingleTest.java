package smrl.mr.singleTest;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import smrl.mr.owasp.OTG_AUTHZ_001b;
import smrl.mr.owasp.OTG_AUTHZ_002;
import smrl.mr.owasp.OTG_AUTHZ_002a;
import smrl.mr.owasp.OTG_AUTHZ_002b;
import smrl.mr.owasp.OTG_AUTHZ_002c;
import smrl.mr.owasp.OTG_AUTHZ_002d;
import smrl.mr.owasp.OTG_AUTHZ_002e;
import smrl.mr.owasp.OTG_SESS_003;
import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRBaseTest;

public class SingleTest extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "jenkins";
//	private static String system = "iws";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.out.println("*** Starting time: " + getCurrentTime() +" ***");
		
		//by default, the SUT is the Jenkins
		String configFile = "./testData/Jenkins/jenkinsSysConfig.json";
//		String configFile = "./testData/Jenkins/jenkinsSysConfig_withProxy.json";
//		String configFile = "./testData/Jenkins/collectedData/jenkinsSysConfig.json";
		
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
	
	@AfterClass
    public static void printEndingTime() {
		System.out.println("*** Ending time: " + getCurrentTime() + " ***");
    }   
	
	@Test
	public void test_OTG_AUTHZ_001b() {
		super.test(provider,OTG_AUTHZ_001b.class);
	}
	

	@Test
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
	  *This test case should detect CVE-2018-1999003"
	  */
	public void test_OTG_AUTHZ_002b() {
		super.test(provider,OTG_AUTHZ_002b.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999046"
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
	
	@Test
	public void test_cover_urls_u2_u1() {
		assertTrue(provider.coverAllUrls("user2", "user1"));
//		assertTrue(provider.coverAllUrls("admin", "user1"));
//		assertTrue(provider.coverAllUrls("user1", "user2"));
//		assertTrue(provider.coverAllUrls("user1", "admin"));
//		assertTrue(provider.coverAllUrls("user2", "admin"));
//		assertTrue(provider.coverAllUrls("admin", "user2"));
	}
	
	@Test
	public void test_cover_urls_u1_u2() {
//		assertTrue(provider.coverAllUrls("user2", "user1"));
//		assertTrue(provider.coverAllUrls("admin", "user1"));
		assertTrue(provider.coverAllUrls("user1", "user2"));
//		assertTrue(provider.coverAllUrls("user1", "admin"));
//		assertTrue(provider.coverAllUrls("user2", "admin"));
//		assertTrue(provider.coverAllUrls("admin", "user2"));
	}
	
	@Test
	public void check_driver() {
		String exePath = "/usr/local/bin/chromedriver";
		System.setProperty("webdriver.chrome.driver", exePath);
		ChromeDriver driver = null;
		
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);

		ChromeOptions chOptions = new ChromeOptions();
		chOptions.setExperimentalOption("prefs", chromePrefs);
				
		driver = new ChromeDriver(chOptions);
		
		driver.get("http://192.168.56.102:8080");
		driver.get("/");
	}

	public static String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now).toString();  
	}
}
