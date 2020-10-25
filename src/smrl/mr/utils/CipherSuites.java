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
package smrl.mr.utils;

import java.util.ArrayList;

public class CipherSuites {
	public static String[] secrecy = { 
										"0x1301",
										"0x1302",
										"0x1303",
										"0xc02b",
										"0xc02c",
										"0xc02f",
										"0xc030",
										"0xcca8",
										"0xcca9"}; 
	
	public static String[] weak = {"0xc013",
									"0xc014",
									"0x9c",
									"0x9d",
									"0x2f",
									"0x35",
									"0xa"
									};
	
	/**
	 * @return list of weak cipher suite. 
	 * Each cipher suite is a string used for the command argument --cipher-suite-blacklist
	 */
	public static ArrayList<String> weakCipherSuite() {
		ArrayList<String> result = new ArrayList<String>();
		
		String allSecrecy = "";
		for(String s:secrecy) {
			allSecrecy += "," + s;
		}
		
		allSecrecy = allSecrecy.substring(1);
		
		for(String w:weak) {
			String suite = allSecrecy;
			
			for(String w2:weak) {
				if(!w2.equals(w)) {
					suite += "," + w2;
				}
			}
			
			result.add(suite);
		}
		
		return result;
	}
	
}
