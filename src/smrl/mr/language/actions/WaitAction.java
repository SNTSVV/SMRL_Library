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

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;
import smrl.mr.language.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class WaitAction extends Action {

	private long millis;

	public WaitAction(){
		this.millis = 0;
		this.eventType = ActionType.wait;
		this.user = new Account();
		this.innerActions = null;
		setActionID();
	}
	
	public WaitAction(long millis){
		this.millis = millis;
		this.eventType = ActionType.wait;
		this.user = new Account();
		this.innerActions = null;
		setActionID();
	}
	
	public WaitAction(JsonObject waitAction){
		this.eventType = ActionType.wait;
		if(waitAction.keySet().contains("eventType")){
			if(!waitAction.get("eventType").getAsString().trim().toLowerCase().equals("wait")){
				System.out.println("There is no correct info of eventType in JsonObject!");
			}
		}
		if(waitAction.keySet().contains("time")){
			this.millis = waitAction.get("time").getAsLong();
		}
		setActionID();
	}
	
	
	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
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
		throw new NotImplementedException();
	}

//	@Override
//	public String getParameterName(int p) {
//		throw new NotImplementedException();
//	}

//	@Override
//	public boolean addParameter(String name, String value) {
//		throw new NotImplementedException();
//	}

	@Override
	public boolean contain(String url) {
		return false;
	}

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
		// nothing to do
		try {
			return (WaitAction) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
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
		return "[Action: wait in " + this.millis +" ms]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null && !(obj instanceof WaitAction)){
			return false;
		}
		WaitAction that = (WaitAction) obj;
		return (this.eventType.equals(that.eventType) &&
				this.millis == that.millis &&
				((this.user==null && that.user==null) || this.user.equals(that.user)));
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
//		"eventType": "wait",
//		"time": 5000
		
		act.addProperty("eventType", this.eventType.toString());
		act.addProperty("time", this.millis);
		
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
		return "wait " + millis;
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
