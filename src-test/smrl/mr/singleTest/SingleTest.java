/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

public class SingleTest extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "jenkins";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.out.println("*** Starting time: " + getCurrentTime() +" ***");
		
		//by default, the SUT is the Jenkins
		String configFile = "./testData/Jenkins/jenkinsSysConfig.json";
//		String configFile = "./testData/Jenkins/jenkinsSysConfig_withProxy.json";
//		String configFile = "./testData/Jenkins/jenkinsSysConfig_trying.json";
		
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
	
	//In ICST paper: 11 MRs
	@Test
	public void test_OTG_AUTHN_004() {
		super.test(provider,OTG_AUTHN_004.class);
	}
	
	@Test
	public void test_OTG_AUTHN_010() {
		super.test(provider,OTG_AUTHN_010.class);
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
	// End list of MRs in ICST paper //
	
		
	// New tests
	@Test
	//Done: all
	public void test_OTG_AUTHZ_001a() {
		super.test(provider,OTG_AUTHZ_001.class);
	}
	
	@Test
	/**
	  *This test case should detect CVE-2018-1999006"
	  */
	//Not execute
	public void test_OTG_AUTHZ_001b2() {
		super.test(provider,OTG_AUTHZ_001b2.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_AUTHN_001() {
		super.test(provider,OTG_AUTHN_001.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_AUTHZ_003() {
		super.test(provider,OTG_AUTHZ_003.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_AUTHZ_004() {
		super.test(provider,OTG_AUTHZ_004.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_BUSLOGIC_005() {
		super.test(provider,OTG_BUSLOGIC_005.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_CONFIG_007() {
		super.test(provider,OTG_CONFIG_007.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_CRYPST_004() {
		super.test(provider,OTG_CRYPST_004.class);
	}
	
	@Test
	//Done: all
	//Run with proxy
	//Re-run after modifying MR
	public void test_OTG_INPVAL_003() {
		super.test(provider,OTG_INPVAL_003.class);
	}
	
	@Test
	// Done: all
	public void test_OTG_SESS_006() {
		super.test(provider,OTG_SESS_006.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_SESS_007() {
		super.test(provider,OTG_SESS_007.class);
	}
	
	@Test
	//Done: all
	public void test_OTG_SESS_008() {
		super.test(provider,OTG_SESS_008.class);
	}
	
	public static String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now).toString();  
	}

}
