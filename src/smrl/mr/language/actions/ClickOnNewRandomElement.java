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
package smrl.mr.language.actions;

import java.util.List;
import java.util.Map.Entry;

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;
import smrl.mr.language.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ClickOnNewRandomElement extends Action {
	public ClickOnNewRandomElement() {
		this.eventType = ActionType.randomClickOnNewElement;
		this.user = new Account();
		this.innerActions = null;
		setActionID();
	}
	
	public ClickOnNewRandomElement(JsonObject randomClickAction){
		this.eventType = ActionType.randomClickOnNewElement;
		boolean setType = false;
		if(randomClickAction.keySet().contains("eventType")){
			if(randomClickAction.get("eventType").getAsString().trim().toLowerCase().equals(
					ActionType.randomClickOnNewElement.toString().toLowerCase())){
				setType = true;
			}
		}
		if(!setType){
			System.out.println("There is no correct info of eventType in JsonObject!");
		}
		
		this.user = new Account();
		
		this.innerActions = null;
		setActionID();
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String getParameterValue(int p) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setChannel(String string) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public List<Entry<String, String>> getParameters() {
//		// TODO Auto-generated method stub
//		return null;
//	}



	@Override
	public boolean setEncryption(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUrl(String url) {
		return false;		
	}

	@Override
	public boolean setMethod(String method) {
		throw new NotImplementedException();
	}

	@Override
	public String getMethod() {
		return "";
	}

//	@Override
//	public String getParameterName(int p) {
//		throw new NotImplementedException();
//	}

//	@Override
//	public boolean addParameter(String name, String value) {
//		return false;
//	}

	@Override
	public boolean containAccount(Account acc) {
		return false;
	}

	@Override
	public boolean containCredential(Account acc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action changeCredential(Account acc) {
		// Nothing todo		
		try {
			return (ClickOnNewRandomElement)this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public boolean containCredential(String userParam, String passwordParam) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Account getCredential(String userParam, String passwordParam) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String toString() {
		return "[Action " + actionID + " : randomly click on one of new elements]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof ClickOnNewRandomElement)){
			return false;
		}
		ClickOnNewRandomElement that = (ClickOnNewRandomElement)obj;
		return (this.eventType.equals(that.eventType));
	}

	@Override
	public boolean isChannelChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonArray toJson() {
		JsonArray res = new JsonArray();
		
		JsonObject act = new JsonObject();
		
		//Example:
//		"eventType": "randomClickOnNewElement",
		
		act.addProperty("eventType", this.eventType.toString());
		
		res.add(act);
		
		if(innerActions!=null && innerActions.size()>0){
			for(InnerAction iAct:innerActions){
				res.addAll(iAct.toJson());
			}
		}
		
		return res;
	}

	@Override
	public boolean containFormInput() {
		return false;
	}

	@Override
	public JsonArray getFormInputs() {
		return null;
	}

	@Override
	public boolean containFormInputForFilePath() {
		return false;
	}

	@Override
	public String getOldMethod() {
		return null;
	}

	@Override
	public boolean isMethodChanged() {
		return false;
	}

	@Override
	public String getText() {
		return "randomly click";
	}

	@Override
	public boolean setId(String id) {
		return false;
	}

	@Override
	public String getCipherSuite() {
		return null;
	}
}
