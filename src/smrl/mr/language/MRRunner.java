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
package smrl.mr.language;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

public class MRRunner {

	public static void runInPackage(OperationsProvider provider, String packageName) {
		Class[] a = retrieveMRClassesArray(packageName);
		 
		 run(provider,a);
	}

	public static Class[] retrieveMRClassesArray(String packageName) {
		Set<Class<? extends MR>> allClasses = (Set<Class<? extends MR>>) retrieveMRClasses(packageName);
		 
		 Class[] a = new Class[allClasses.size()];
		 int i = 0;
		 for (  Class c : allClasses ){
			 a[i]=c;
			 i++;
		 }
		return a;
	}

	public static Collection<Class<? extends MR>> retrieveMRClasses(String packageName) {
		Reflections reflections = new Reflections(packageName);

		 Set<Class<? extends MR>> allClasses = reflections.getSubTypesOf(MR.class);
		return allClasses;
	}
	
	public static void run(OperationsProvider provider, Class... classes) {
		for( Class clazz : classes ){
			try {
				runAndGetFailures(provider, clazz);
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static List<String> runAndGetFailures(OperationsProvider provider, Class clazz)
			throws InstantiationException, IllegalAccessException {
		MR mr = (MR)clazz.newInstance();
		mr.setProvider(provider);
		mr.run();
		
		return mr.failures;
	}

}
