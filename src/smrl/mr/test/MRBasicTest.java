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
package smrl.mr.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import smrl.mr.language.MRRunner;
import smrl.mr.language.OperationsProvider;

@RunWith(Parameterized.class)
public abstract class MRBasicTest {
	
	public static OperationsProvider _provider;
	public static Class[] MRs = {};
	
	@Parameter(0)
	public Class clazz;

	



	public static String packageName;

//	public static void setPackageName(String packageName){
////		MRBasicPackageTest.packageName = packageName;
//		System.out.println("set packageName "+MRBasicPackageTest.packageName);
//	}

	public static void setProvider(OperationsProvider provider) {
		_provider = provider;
	}

	public static void setMRs(Class[] mrs) {
		MRs = mrs;
	}

	

	
	
	@Parameters
	public static List retrieveParameters() {
		System.out.println(Thread.currentThread().getStackTrace()[2].getClassName());
		ArrayList<Object[]> pars = new ArrayList<Object[]>();
		
		for ( Class clazz : MRs ){
			Object[] o = new Object[1];
			o[0]=clazz;
			pars.add(o);
			
			System.out.println("!!! MR to be tested: "+clazz);
		}

		
		return pars;
	}


	@Test
	public void test(){


		
		//		Object[] testData = myProjectTestRule.getTestData();
		List<String> fails = new ArrayList<>();

		try {
			List<String> failuesMsgs = MRRunner.runAndGetFailures(_provider, clazz);
			if ( failuesMsgs.size() > 0 ){
				fails.add(clazz.getCanonicalName());
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if ( fails.size() > 0 ){
			fail(fails.toString());
		}
	}


}
