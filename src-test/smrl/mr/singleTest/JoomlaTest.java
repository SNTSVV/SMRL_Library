package smrl.mr.singleTest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRBaseTest;
import smrl.mr.owasp.CheckTags;

public class JoomlaTest extends MRBaseTest {
	private static WebOperationsProvider provider;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("*** Starting time: " + SingleTest.getCurrentTime() +" ***");
		
		String configFile = "./testData/Joomla/joomlaSysConfig.json";
		
		provider = new WebOperationsProvider(configFile);
	}
	
	@Before
	public void setUp() throws Exception {
		setProvider(provider);
	}
	
	@AfterClass
    public static void printEndingTime() {
		System.out.println("*** Ending time: " + SingleTest.getCurrentTime() + " ***");
    }   
	
	@Test
	public void test_CheckTags() {
		super.test(provider,CheckTags.class);
	}

}
