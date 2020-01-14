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
			// TODO Auto-generated catch block
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
}
