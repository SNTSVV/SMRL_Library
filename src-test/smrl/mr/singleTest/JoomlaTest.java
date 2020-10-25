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
