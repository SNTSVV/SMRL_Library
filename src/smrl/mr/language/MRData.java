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
import java.util.List;

public class MRData implements Cloneable {

	String id;
	List<MRData> reassignments = new ArrayList<MRData>();
	boolean alreadyUsedInRHS = false;
	
	public void setAlreadyUsedInRHS() {
		alreadyUsedInRHS = true;
	}
	
	public MRData clone() throws CloneNotSupportedException{
		MRData cloned = (MRData) super.clone();
		cloned.reassignments = new ArrayList<>();
		
		return cloned;
	}

	public void addReassignment(MRData rhs) {
		reassignments.add(rhs);
	}

	public boolean isReassignable() {
		if ( alreadyUsedInRHS ) {
			return false;
		}
		
		return reassignments.size() == 0;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	
}
