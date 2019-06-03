package smrl.mr.language.actions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.client.utils.URIBuilder;

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;
import smrl.mr.language.Operations;
import smrl.mr.language.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class StandardAction extends Action {
	private String text;
	private String id;	//containing xpath
	private String element;
	private String currentURL;
	private String elementURL;
	private String method;
	private JsonArray formInputs;
	private boolean channelChanged;
	private String newChannel;
	private String oldChannel;
	
	
	public StandardAction(){
		this.text = "";
		this.id = "";
		this.element = "";
		this.eventType = ActionType.click;
		this.currentURL = "";
		this.elementURL = "";
		this.method = "get";
		this.formInputs = new JsonArray();
		this.channelChanged = false;
		this.newChannel = "";
		this.oldChannel = "";
		this.user = new Account();
		this.innerActions = null;
		setActionID();
	}
	
	/**
	 * @param jsonAction description of the action under the json object format
	 */
	public StandardAction(JsonObject jsonAction){
		Set<String> keys = jsonAction.keySet();
		if(keys.contains("text")){
			this.text = jsonAction.get("text").getAsString();
		}
		else{
			this.text = "";
		}
		
		if(keys.contains("id")){
			this.id = jsonAction.get("id").getAsString();
		}
		else{
			this.id = "";
		}
		
		if(keys.contains("element")){
			this.element = jsonAction.get("element").getAsString();
		}
		else{
			this.element = "";
		}
		
		this.eventType = ActionType.click;
		if(keys.contains("eventType")){
			String type = jsonAction.get("eventType").getAsString();
			if(type.toLowerCase().equals("hover")){
				this.eventType = ActionType.hover;
			}
		}
		
		if(keys.contains("currentURL")){
			this.currentURL = jsonAction.get("currentURL").getAsString();
		}
		else{
			this.currentURL = "";
		}
		
		if(keys.contains("elementURL")){
			this.elementURL = jsonAction.get("elementURL").getAsString();
		}
		else{
			this.elementURL = "";
		}
		
		boolean setMethod = false;
		if(keys.contains("method")){
			String m = jsonAction.get("method").getAsString().toLowerCase();
			for(HTTPMethod mi : HTTPMethod.values()){
				if (mi.toString().toLowerCase().equals(m)){
					this.method = m;
					setMethod = true;
					break;
				}
			}
		}
		if(!setMethod){
			this.method = "get";
		}

		if(keys.contains("formInputs")){
			this.formInputs = jsonAction.get("formInputs").getAsJsonArray();
		}
		else{
			this.formInputs = new JsonArray();
		}
		
		this.channelChanged = false;
		this.newChannel = "";
		if(this.elementURL.contains("://")){
			int ind = this.elementURL.indexOf("://");
			this.oldChannel = this.elementURL.substring(0, ind);
		}
		else{
			this.oldChannel = "";
		}
		
		this.user = new Account();
		this.innerActions = null;
		setActionID();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getCurrentURL() {
		return currentURL;
	}

	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
	}

	public String getElementURL() {
		return elementURL;
	}

	public void setElementURL(String elementURL) {
		this.elementURL = elementURL;
	}
	
	@Override
	public JsonArray getFormInputs() {
		return formInputs;
	}

	public void setFormInputs(JsonArray formInputs) {
		this.formInputs = formInputs;
	}

	@Override
	public String getUrl() {
		String res = elementURL;
		
		if(res==null || res.trim().isEmpty()){
			return res;
		}
		
		try {
			URI uri = new URI(res);
			String query = uri.getQuery();
			if(query==null || query.isEmpty()){
				if(!res.contains(".") && !res.endsWith("/")){
					res += "/";
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	@Override
	public String toString() {
		return "[Action " + actionID + " : " + eventType + " on " + elementURL +"]";
	}

//	@Override
//	public String getParameterValue(int p) {
//		return null;
//	}

	@Override
	public boolean setParameterValue(int p, Object object){
		if(method.toLowerCase().equals("post")){
			System.out.println("\t\t!!!Cannot modify URL's parameters of a POST request");
			return false;
		}
		
		List<Entry<String, String>> params = this.getParameters();
		
		if(params==null || params.size()<=0 || p<0 || p>=params.size() ||
				getUrl()==null || getUrl().isEmpty()){
			return false;
		}
		
		boolean setResult = false;
		
		params.get(p).setValue((String) object);	// Update value
		
		try {
			URIBuilder ub = new URIBuilder(getUrl());
			ub.clearParameters();
			for(Entry<String, String> par:params){
				ub.addParameter(par.getKey(), par.getValue());
			}
			setUrl(ub.toString());
			setId("");
			setResult = true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return setResult;
	}
	
//	@Override
//	public String getParameterName(int p) {
//		return null;
//	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isChannelChanged(){
		return channelChanged;
	}
	
	@Override
	public String getNewChannel(){
		return newChannel;
	}
	
	@Override
	public String getOldChannel(){
		return oldChannel;
	}
	
	@Override
	public boolean setChannel(String newChannelToSet) {
		if(newChannelToSet == null || newChannelToSet.isEmpty()){
			return false;
		}
		
		//if the current channel is equal newChannelToSet, return false
		String currentChannel = getChannel();
		if(currentChannel!=null &&
				currentChannel.trim().toLowerCase().equals(newChannelToSet.trim().toLowerCase())){
			return false;
		}
		
		channelChanged=true;
		newChannel=newChannelToSet;
		
		String newURL = this.elementURL;
		if(newURL.contains("://")){
			oldChannel = newURL.substring(0,newURL.indexOf("://"));
			newURL = newChannelToSet + newURL.substring(newURL.indexOf("://"));
		}
		else{
			oldChannel = "http";
			newURL = newChannelToSet + "://" + newURL;
		}
		this.elementURL = newURL;

		return true;
	}

//	@Override
//	public List<Entry<String, String>> getParameters() {
//		return getParameters(this.elementURL);
//	}


	@Override
	public boolean setEncryption(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUrl(String url) {
		this.elementURL = url;
		
		// to assure that this action should be execute by following the elementURL
		this.id = "";		
		this.element = "";
		return true;
	}

	@Override
	public boolean setMethod(String method) {
		for(HTTPMethod m : HTTPMethod.values()){
			if (m.toString().toLowerCase().equals(method.toLowerCase())){
				this.method = method.toLowerCase();
				return true;
			}
		}
		return false;
	}

	@Override
	public String getMethod() {
		return this.method;
	}

//	@Override
//	public boolean addParameter(String name, String value) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public boolean containAccount(Account acc) {
		if (acc.isEmpty()){
			return false;
		}
		
//		System.out.println("!!!CONTAIN_ACCOUNT: looking for "+acc);
		
		boolean hasUser = false;
		boolean hasPassword = false;
		JsonArray fInputs = this.getFormInputs();
		for(int i = 0; i<fInputs.size(); i++){
			JsonObject fi = fInputs.get(i).getAsJsonObject();
			if(fi.keySet().contains("identification") && fi.keySet().contains("values")){
				JsonObject iden = fi.get("identification").getAsJsonObject();
				JsonArray fiValues = fi.get("values").getAsJsonArray();
				
//				System.out.println("!!!CONTAIN_ACCOUNT: looking for value");
				
				if(iden.keySet().contains("value") && fiValues.size()>=1){
					String idenKey = iden.get("value").getAsString().trim();
					if(idenKey.equals(acc.getUsernameParam())){
						for(int j = 0; j<fiValues.size(); j++){
							String user = fiValues.get(j).getAsString().trim();
							if(user.equals(acc.getUsername())){
//								System.out.println("!!!CONTAIN_ACCOUNT: user found");
								hasUser = true;
								break;
							}
						}
					}
					else if(idenKey.equals(acc.getPasswordParam())){
						for(int j = 0; j<fiValues.size(); j++){
							String pass = fiValues.get(j).getAsString().trim();
							if(pass.equals(acc.getPassword())){
//								System.out.println("!!!CONTAIN_ACCOUNT: pwd found");
								hasPassword = true;
								break;
							}
						}
					}
				}
			}
			if(hasUser && hasPassword){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containCredential(String userParam, String passwordParam) {
		if(this.formInputs.size()<=0){
			return false;
		}
		boolean hasUserParam = false;
		boolean hasPassParam = false;
		for(int i = 0; i<this.formInputs.size(); i++){
			JsonObject fi = this.formInputs.get(i).getAsJsonObject();
			if(fi.keySet().contains("identification")){
				JsonObject iden = fi.get("identification").getAsJsonObject();
				if(iden.keySet().contains("value")){
					if(iden.get("value").getAsString().trim().equals(userParam.trim())){
						hasUserParam = true;
					}
					else if(iden.get("value").getAsString().trim().equals(passwordParam.trim())){
						hasPassParam = true;
					}
				}
			}
			if(hasUserParam && hasPassParam){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containCredential(Account acc){
		if(acc!=null && !acc.getUsernameParam().isEmpty() && !acc.getPasswordParam().isEmpty()){
			return this.containCredential(acc.getUsernameParam(), acc.getPasswordParam());
		}
		return false;
	}

	@Override
	public Action changeCredential(Account acc) {
		StandardAction newAct = null;
		try {
			newAct = (StandardAction) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		if(this.containCredential(acc)){
			if(newAct!=null){
				for(int i = 0; i<newAct.getFormInputs().size(); i++){
					JsonObject fi = newAct.getFormInputs().get(i).getAsJsonObject();
					if(fi.keySet().contains("identification")){
						JsonObject iden = fi.get("identification").getAsJsonObject();
						if(iden.keySet().contains("value")){
							if(iden.get("value").getAsString().trim().equals(acc.getUsernameParam())){
								fi.remove("values");
								
								JsonArray values = new JsonArray();
								values.add(acc.getUsername());
								fi.add("values", values);
							}
							else if(iden.get("value").getAsString().trim().equals(acc.getPasswordParam())){
								fi.remove("values");
								
								JsonArray values = new JsonArray();
								values.add(acc.getPassword());
								fi.add("values", values);
							}
						}
					}
				}
				return newAct;
			}
		}
		return this;
	}

	

	@Override
	public Account getCredential(String userParam, String passwordParam) {
		if(userParam.isEmpty() || passwordParam.isEmpty()){
			return null;
		}
		if(!containCredential(userParam, passwordParam)){
			return null;
		}
		Account acc = new Account();
		acc.setUsernameParam(userParam);
		acc.setPasswordParam(passwordParam);
		
		boolean gotUser = false;
		boolean gotPassword = false;
		for(int i = 0; i<formInputs.size(); i++){
			JsonObject input = formInputs.get(i).getAsJsonObject();
			if(input.keySet().contains("identification") && input.keySet().contains("values")){
				JsonObject iden = input.get("identification").getAsJsonObject();
				JsonArray values = input.get("values").getAsJsonArray();
				if(iden.keySet().contains("value")){
					if(iden.get("value").getAsString().equals(userParam)){
						if(values.size()>0){
							//get the first value
							acc.setUsername(values.get(0).getAsString());
							gotUser = true;
						}						
					}
					else if(iden.get("value").getAsString().equals(passwordParam)){
						if(values.size()>0){
							//get the first value
							acc.setPassword(values.get(0).getAsString());
							gotPassword = true;
						}	
						
					}
				}
			}
			if(gotUser && gotPassword){
				return acc;
			}
		}
		
		if(gotUser){
			return acc;
		}
		
		return null;
	}

	@Override
	public Action clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		StandardAction _cloned = (StandardAction) super.clone();
//		_cloned.element = element;
//		_cloned.elementURL = elementURL;
//		_cloned.eventType = eventType;
		_cloned.formInputs = formInputs.deepCopy();
//		_cloned.id = id;
		return _cloned;
		
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof StandardAction)){
			return false;
		}
		StandardAction that = (StandardAction)obj;
		return (this.text.equals(that.text) &&
				this.method.equals(that.method) &&
//				this.element.equals(that.element) &&
//				this.elementURL.equals(that.elementURL) &&
				this.eventType.equals(that.eventType) &&
				this.formInputs.equals(that.formInputs) &&
				this.id.equals(that.id) &&
				((this.user==null && that.user==null) || this.user.equals(that.user)));
	}
	
	@Override
	public void updateUrl(String newURL) {
		this.elementURL = newURL;
	}

	@Override
	public JsonArray toJson() {
		JsonArray res = new JsonArray();
		
		JsonObject act = new JsonObject();
		
		//Example:
//		"text": "slave1",
//		"id": "xpath /HTML[1]/BODY[1]/DIV[4]/DIV[1]/DIV[3]/DIV[2]/TABLE[1]/TBODY[1]/TR[4]/TH[1]/A[1]",
//		"element": "Element{node=[A: null], tag=A, text= slave1, attributes={class=model-link inside, href=/computer/slave1/}}",
//		"eventType": "click",
//		"currentURL": "http://192.168.56.102:8080/",
//		"elementURL": "http://192.168.56.102:8080/computer/slave1/",
//		"method": "get"
//		"formInputs": [...]
		
		act.addProperty("text", this.text);
		act.addProperty("id", this.id);
		act.addProperty("element", this.element);
		act.addProperty("eventType", this.eventType.toString());
		act.addProperty("currentURL", this.currentURL);
		act.addProperty("elementURL", this.elementURL);
		act.addProperty("method", this.method);
		
		if(formInputs!=null && formInputs.size()>0){
			act.add("formInputs", formInputs);
		}
		
		
		res.add(act);
		
		if(innerActions!=null && innerActions.size()>0){
			for(InnerAction iAct:innerActions){
				res.addAll(iAct.toJson());
			}
		}
		
		return res;
	}

	@Override
	/**
	 * @return whether this action contains form input(s) (except login and signup)
	 */
	public boolean containFormInput() {
		if(formInputs==null || 
				formInputs.size()<1 ||
				Operations.isLogin(this) ||
				Operations.isSignup(this)){
			return false;
		}
		return true;
	}

	@Override
	public boolean containFormInputForFilePath() {
		if(formInputs==null || 
				formInputs.size()<1 ||
				Operations.isLogin(this) ||
				Operations.isSignup(this)){
			return false;
		}
		
		for(int i=0; i<formInputs.size(); i++){
			JsonObject fi = formInputs.get(i).getAsJsonObject();
			
			if(isFormInputForFilePath(fi)){
				return true;
			}
		}
		
		
		return false;
	}

	public static boolean isFormInputForFilePath(JsonObject fi) {
		if(fi==null || fi.size()==0){
			return false;
		}
		
		if(fi.keySet().contains("type") &&
				fi.keySet().contains("values")){
			
			String formType = fi.get("type").getAsString().toLowerCase();
			JsonArray values = fi.get("values").getAsJsonArray();
			if(values.size()>0 &&
					(formType.startsWith("text") ||
						formType.equals("password") || 
						formType.equals("hidden") ||
						formType.equals("file"))){
				for(int iValue=0; iValue<values.size(); iValue++){
					String value = values.get(iValue).getAsString().trim();
					if(considerAsFile(value)){
						return true;
					}
				}
			}
			
		}

		return false;
	}

	private static boolean considerAsFile(String value) {
		if(value==null || value.isEmpty()){
			return false;
		}
		
		if(value.contains(".") && !value.endsWith(".")){
			return true;
		}
		
		return false;
	}

	
	
}
