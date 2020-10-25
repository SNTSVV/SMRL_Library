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

public class CollectionOfConcepts {
	public String id;	//this id should be an ID, a Name or an XPATH of the specific parent element
	public ArrayList<String> concepts;
	
	private static String[] ignoredConcepts = {"none", "choose", "select"};
	
	public CollectionOfConcepts() {
		this.id = null;
		this.concepts = new ArrayList<String>();
	}
	
	public boolean isEmpty() {
		if(this.id==null && 
				(this.concepts ==null || this.concepts.isEmpty())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}
		
		if(!(obj instanceof CollectionOfConcepts)) {
			return false;
		}
		
		CollectionOfConcepts that = (CollectionOfConcepts) obj;
		
		if(this.isEmpty() && that.isEmpty()) {
			return true;
		}
		
		return (this.concepts!=null && 
				that.concepts!=null &&
				this.concepts.size()==that.concepts.size() &&
				this.concepts.containsAll(that.concepts));
	}

	@Override
	public String toString() {
		return id + ":" + concepts.toString();
	}
	
	public static boolean isIgnoredConcept(String concept) {
		if(concept==null || concept.isEmpty()) {
			return true;
		}
		
		for(String con:ignoredConcepts) {
			if(concept.trim().toLowerCase().contains(con.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	
}
