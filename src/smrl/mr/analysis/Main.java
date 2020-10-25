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
package smrl.mr.analysis;

import java.net.URL;
import java.security.CodeSource;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		CodeSource src = com.google.common.util.concurrent.SimpleTimeLimiter.class.getProtectionDomain().getCodeSource();
		if (src != null) {
		    URL jar = src.getLocation();
		    System.out.println(jar);
		}
	}

}
