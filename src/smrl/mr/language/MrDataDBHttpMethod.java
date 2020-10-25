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

import java.util.ArrayList;
import java.util.HashMap;

import smrl.mr.language.Action.HTTPMethod;

public class MrDataDBHttpMethod extends MrDataDB<Object> {

	private ArrayList db;


	public MrDataDBHttpMethod(String dbName) {
		super(dbName);
		db = new ArrayList<>();
		HTTPMethod[] vals = HTTPMethod.values();
		for ( HTTPMethod val : vals ) {
			db.add(val.toString());	
		}
		
		LEN=db.size();
	}


	
	public Object get(int i) {
		
		
		int pos = (START+i-1) % LEN;
		
		Object data = db.get(pos);
		
		String key = dbName+"("+i+")";
		generatedData.put(key, data);
		
		return data;
	}
	
	
	
	

}
