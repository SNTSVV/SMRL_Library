package smrl.mr.language.actions;

import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;
import smrl.mr.language.Session;

public class AlertAction extends InnerAction {
	
	private String text;
	private Boolean accept;
	private JsonArray formInputs;
	
	public AlertAction(){
		this.eventType = ActionType.alert;
		this.user = new Account();
		this.innerActions = null;
		this.text = "";
		this.accept = true;
		this.formInputs = new JsonArray();
		this.mainAction = null;
		setActionID();
	}
	
	public AlertAction(JsonObject jsonAction){
		this.eventType = ActionType.alert;
		this.user = new Account();
		this.innerActions = null;
		
		Set<String> keys = jsonAction.keySet();
		if(keys.contains("text")){
			this.text = jsonAction.get("text").getAsString();
		}
		else{
			this.text = "";
		}
		
		if(keys.contains("accept")){
			this.accept = jsonAction.get("accept").getAsBoolean();
		}
		else{
			this.accept = true;
		}
		
		if(keys.contains("formInputs")){
			this.formInputs = jsonAction.get("formInputs").getAsJsonArray();
		}
		else{
			this.formInputs = new JsonArray();
		}
		
		this.mainAction = null;
		setActionID();
	}
	
	
	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getAccept() {
		return accept;
	}

	public void setAccept(Boolean accept) {
		this.accept = accept;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof AlertAction)){
			return false;
		}
		
		AlertAction that = (AlertAction) obj;
		
		return this.eventType.equals(that.eventType) &&
				this.text.endsWith(that.text) &&
				this.accept==that.accept;
	}

	@Override
	public JsonArray getFormInputs() {
		return formInputs;
	}

	public void setFormInputs(JsonArray formInputs) {
		this.formInputs = formInputs;
	}
	

	@Override
	public Action clone() throws CloneNotSupportedException {
		AlertAction clone = (AlertAction) super.clone();
		clone.text = this.text;
		clone.accept = this.accept;
		clone.formInputs = this.formInputs.deepCopy();
		
		return clone;
	}

	@Override
	public String getUrl() {
		return null;
	}

//	@Override
//	public String getParameterValue(int p) {
//		return null;
//	}

//	@Override
//	public String getParameterName(int p) {
//		return null;
//	}

	@Override
	public Session getSession() {
		if(mainAction!=null){
			return this.mainAction.getSession();
		}
		return null;
	}

	@Override
	public boolean setChannel(String string) {
		return false;
	}

//	@Override
//	public List<Entry<String, String>> getParameters() {
//		return null;
//	}

	@Override
	public boolean setEncryption(Object object) {
		return false;
	}

	@Override
	public boolean setUrl(String url) {
		return false;
	}

	@Override
	public boolean setMethod(String method) {
		return false;
	}

	@Override
	public String getMethod() {
		return null;
	}

//	@Override
//	public boolean addParameter(String name, String value) {
//		return false;
//	}

	@Override
	public boolean containAccount(Account acc) {
		return false;
	}

	@Override
	public boolean containCredential(String userParam, String passwordParam) {
		return false;
	}

	@Override
	public boolean containCredential(Account acc) {
		return false;
	}

	@Override
	public Account getCredential(String userParam, String passwordParam) {
		return null;
	}

	@Override
	public Action changeCredential(Account acc) {
		return null;
	}

	@Override
	public boolean isChannelChanged() {
		return false;
	}

	@Override
	public JsonArray toJson() {
		JsonArray res = new JsonArray();
		
		JsonObject act = new JsonObject();
		
		//Example:
//		{
//		"text": "Are you sure you want to cancel the queued run of listRoot?",
//		"id": "",
//		"element": "",
//		"eventType": "alert",
//		"currentURL": "http://192.168.56.102:8080/",
//		"elementURL": "",
//		"method": "get",
//		"accept": true
//		}
		
		act.addProperty("text", this.text);
//		act.addProperty("id", "");
//		act.addProperty("element", "");
		act.addProperty("eventType", this.eventType.toString());
//		act.addProperty("currentURL", "");
//		act.addProperty("elementURL", "");
//		act.addProperty("method", "get");
		act.addProperty("accept", this.accept);
		
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
		if(formInputs==null || formInputs.size()<1){
			return false;
		}
		return true;
	}

	@Override
	public boolean containFormInputForFilePath() {
		// FIXME: have to fix in the case alert action has form input
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
