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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

public abstract class MRBaseTest {

	private OperationsProvider _provider;

	public void setProvider(OperationsProvider provider) {
		this._provider = provider;
	}

	public void test(OperationsProvider provider, Class clazz){


		//		Object[] testData = myProjectTestRule.getTestData();
		List<String> fails = new ArrayList<>();


		System.out.println("!!!EXECUTING "+clazz.getCanonicalName());
		try {
			fails.addAll( MRRunner.runAndGetFailures(provider, clazz) );
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if ( fails.size() > 0 ){
			fail(fails.toString());
		}
	}

}
