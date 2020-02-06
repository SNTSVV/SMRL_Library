package smrl.mr.language.actions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;
import smrl.mr.language.Session;
import smrl.mr.language.Action.HTTPMethod;
import smrl.mr.utils.URLUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class IndexAction extends Action {
	
	private String url;
	private String method;
	private boolean channelChanged;
	private String newChannel;
	private String oldChannel;
	private String oldMethod;
	
	public IndexAction(){
		this.url = "";
		this.eventType = ActionType.index;
		this.method = "get";
		this.channelChanged = false;
		this.newChannel = "";
		this.oldChannel = "";
		
		this.user = new Account();
		this.innerActions = null;
		setActionID();
		this.oldChannel = null;
		this.oldMethod = null;
	}
	
	public IndexAction(JsonObject indexAction){
		Set<String> keys = indexAction.keySet();
		
		if(keys.contains("url")){
			this.url = indexAction.get("url").getAsString();
		}
		else{
			this.url = "";
		}
		
		if(keys.contains("eventType")){
			String type = indexAction.get("eventType").getAsString();
			if(type.toLowerCase().equals("index")){
				this.eventType = ActionType.index;
			}
			else{
				System.out.println("Note: the action type is not index");
			}
		}
		else{
			this.eventType = ActionType.index;
		}
		
		boolean setMethod = false;
		if(keys.contains("method")){
			String m = indexAction.get("method").getAsString().toLowerCase();
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
		
		this.channelChanged = false;
		this.newChannel = "";
		if(this.url.contains("://")){
			int ind = this.url.indexOf("://");
			this.oldChannel = this.url.substring(0, ind);
		}
		else{
			this.oldChannel = "";
		}
		
		this.user = new Account();
		this.innerActions = null;
		setActionID();
		this.oldChannel = null;
		this.oldMethod = null;
	}
	
	@Override
	public String getUrl() {
		String res = this.url;
		
		try {
			URI uri = new URI(res);
			String query = uri.getQuery();
			if(query==null || query.isEmpty()){
				if(!res.contains(".") && !res.endsWith("/")){
					res += "/";
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}


	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
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
		
		
		String newURL = this.url;
		if(newURL.contains("://")){
			oldChannel = newURL.substring(0,newURL.indexOf("://"));
			newURL = newChannelToSet.trim() + newURL.substring(newURL.indexOf("://"));
		}
		else{
			oldChannel = "http";
			newURL = newChannelToSet.trim() + "://" + newURL;
		}
		this.url = newURL;

		return true;
	}



	@Override
	public boolean setEncryption(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUrl(String url) {
		this.url = url;
		return true;
	}

	@Override
	public boolean setMethod(String method) {
		if(URLUtil.getOrPost(this.method) &&
				URLUtil.getOrPost(method.trim())) {
			return false;
		}

		for(HTTPMethod m : HTTPMethod.values()){
			if (m.toString().equalsIgnoreCase(method.trim())){
				//if this action applied already the "method", do not to set it again
				if(this.method!=null && this.method.equalsIgnoreCase(method.trim())) {
					return false;
				}
				this.oldMethod = this.method;
				this.method = method.trim().toUpperCase();
				return true;
			}
		} 
		return false;
	}

	@Override
	public String getMethod() {
		return this.method;
	}
	
	@Override
	public String getOldMethod() {
		return this.oldMethod;
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
			return (IndexAction)this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
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
		return "[Action " + actionID + " : access the index " + this.url +"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof IndexAction)){
			return false;
		}
		IndexAction that = (IndexAction)obj;
		return (
				this.method.equals(that.method) &&
				this.hasTheSameUrl(that) &&
//				this.url.equals(that.url) &&
				this.eventType.equals(that.eventType) &&
				((this.user==null && that.user==null) || this.user.equals(that.user)));
	}

	@Override
	public boolean isChannelChanged(){
		return channelChanged;
	}

	@Override
	public JsonArray toJson() {
		JsonArray res = new JsonArray();
		
		JsonObject act = new JsonObject();
		
		//Example:
//		"url": "http://192.168.56.102:8080/",
//		"eventType": "index",
//		"method": "get"
		
		act.addProperty("url", getUrl());
		act.addProperty("eventType", this.eventType.toString());
		act.addProperty("method", this.method);
		
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
	public boolean isMethodChanged() {
		return this.oldMethod!=null;
	}

	@Override
	public String getText() {
		return "index";
	}

}
