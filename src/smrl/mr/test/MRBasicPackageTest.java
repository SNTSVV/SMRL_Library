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

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import smrl.mr.language.MR;
import smrl.mr.language.MRRunner;
import smrl.mr.language.OperationsProvider;

@RunWith(Parameterized.class)
public abstract class MRBasicPackageTest {
	@Parameter(0)
	public Class clazz;

	public OperationsProvider _provider;




	public static String packageName;

//	public static void setPackageName(String packageName){
////		MRBasicPackageTest.packageName = packageName;
//		System.out.println("set packageName "+MRBasicPackageTest.packageName);
//	}

	public void setProvider(OperationsProvider provider) {
		this._provider = provider;
	}



	@Parameters
	public static List parameters() {
		
		Field f;
		try {
			f = ClassLoader.class.getDeclaredField("classes");
			f.setAccessible(true);

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Vector<Class> classes =  (Vector<Class>) f.get(classLoader);
			for (  Class clazz : classes ){
				if ( clazz != null && clazz.getSuperclass() == MRBasicPackageTest.class ){
					packageName=clazz.getPackage().getName();
					break;
				}
			}
			
			System.out.println("!!! package under test: "+packageName);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ArrayList<Object[]> pars = new ArrayList<Object[]>();
//		System.out.println("packageName "+packageName);
		Collection<Class<? extends MR>> clazzes = MRRunner.retrieveMRClasses(packageName);
		for ( Class clazz : clazzes ){
			Object[] o = new Object[1];
			o[0]=clazz;
			pars.add(o);
			
			System.out.println("!!! MR to be tested: "+clazz);
		}

		
		return pars;
	}


	@Test
	public void test(){

		if ( ! clazz.getCanonicalName().startsWith(packageName+".") ){
			System.out.println("!!!Ignoring "+clazz.getCanonicalName());
			return;
		}
		
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
