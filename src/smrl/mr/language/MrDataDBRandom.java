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

public class MrDataDBRandom extends MrDataDB<Object> {

	public MrDataDBRandom(String dbName) {
		super(dbName);
		LEN=100;
	}


	HashMap<Class, ArrayList<Object>> typesDB = new HashMap<>();
	public Object get(Class type, int i) {
		ArrayList<Object> db = typesDB.get(type);
		if ( db == null ) {
			db = populateDB( type );
		}
		
		int pos = (START+i-1) % LEN;
		
		Object data = db.get(pos);
		
		String key = dbName+"("+i+")";
		generatedData.put(key, data);
		
		return data;
	}
	
	
	private ArrayList<Object> populateDB(Class type) {
		ArrayList<Object> db = new ArrayList<>();
		if ( type == String.class ) {
			for ( int i = 0; i < LEN; i++ ) {
				int val = (int) Math.floor( Math.random()*100 );
				db.add(val);
			}
		} else if ( type == Integer.class ) {
			for ( int i = 0; i < LEN; i++ ) {
				int val = (int) Math.floor( Math.random()*100 );
				db.add(val);
			}
		} else if ( type == Double.class ) {
			for ( int i = 0; i < LEN; i++ ) {
				double val = Math.random()*100 ;
				db.add(val);
			}
		}
		return db;
	}
	
	

}
