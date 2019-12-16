package smrl.mr.language;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.utils.URIBuilder;
import org.hamcrest.core.IsInstanceOf;

import smrl.mr.crawljax.Account;
import smrl.mr.language.actions.AlertAction;
import smrl.mr.language.actions.ClickOnNewRandomElement;
import smrl.mr.language.actions.IndexAction;
import smrl.mr.language.actions.InnerAction;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.language.actions.WaitAction;
import smrl.mr.utils.URLUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class Action implements Cloneable {
	
	public static enum HTTPMethod {GET, POST, PUT, HEAD, DELETE, PATCH, CONNECT, OPTIONS, TRACE}
	public static enum ActionType {index, click, hover, randomClickOnNewElement, wait, alert}
	
	private Input input;
	protected Long actionID=null;
	protected ActionType eventType;
	protected Account user;		//User executing this current action
	protected List<InnerAction> innerActions; 
	
	public static StandardAction standard(JsonObject standardAction){
		if(standardAction.keySet().contains("eventType")){
			String type = standardAction.get("eventType").getAsString().trim().toLowerCase();
			if(type.equals("click") || type.equals("hover")){
				return new StandardAction(standardAction);
			}
			else{
				return null;
			}
		}
		return null;
	}
	
	public static IndexAction index(JsonObject indexAction){
		if(indexAction.keySet().contains("eventType")){
			String type = indexAction.get("eventType").getAsString().trim().toLowerCase();
			if(type.equals("index")){
				return new IndexAction(indexAction);
			}
			else{
				return null;
			}
		}
		return null;
	}
	
	public static ClickOnNewRandomElement randomClickOnNewElement(JsonObject randomAction){
		if(randomAction.keySet().contains("eventType")){
			String type = randomAction.get("eventType").getAsString().trim().toLowerCase();
			if(type.equals("randomClickOnNewElement")){
				return new ClickOnNewRandomElement(randomAction);
			}
			else{
				return null;
			}
		}
		return null;
	}
	
	public static AlertAction alert(JsonObject alertAction){
		if(alertAction.keySet().contains("eventType")){
			String type = alertAction.get("eventType").getAsString().trim().toLowerCase();
			if(type.equals("alert")){
				return new AlertAction(alertAction);
			}
			else{
				return null;
			}
		}
		return null;
	}
	
	public static Action newAction(JsonObject jsonAction){
		if(jsonAction.keySet().contains("eventType")){
			String type = jsonAction.get("eventType").getAsString().trim().toLowerCase();
			if(type.equals("click") || type.equals("hover")){
				return new StandardAction(jsonAction);
			}
			else if(type.equals("index")){
				return new IndexAction(jsonAction);
			}
			else if(type.equals("randomClickOnNewElement")){
				return new ClickOnNewRandomElement(jsonAction);
			}
			else if (type.equals("wait")){
				return new WaitAction(jsonAction);
			}
			else if (type.equals("alert")){
				return new AlertAction(jsonAction);
			}
		}
		return null;
	}
	
	public Long getActionID(){
		return this.actionID;
	}
	
	/**
	 * Generate action id. The id value is the current time in nano second
	 */
	public void setActionID(){
		this.actionID = new Long(System.nanoTime());
	}
	
	public ActionType getEventType() {
		return eventType;
	}

	public void setEventType(ActionType eventType) {
		this.eventType = eventType;
	}
	
	public void addInnerAction(InnerAction iAct){
		if(iAct==null){
			return;
		}
		
		if (innerActions==null){
			innerActions = new ArrayList<InnerAction>();
		}
		
		try {
			InnerAction actToAdd = (InnerAction) iAct.clone();
			
			if(actToAdd!=null){
				actToAdd.setMainAction(this);
				innerActions.add(actToAdd);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addInnerActions(List<InnerAction> iActions){
		if(iActions==null || iActions.size()==0){
			return;
		}
		
		if (innerActions==null){
			innerActions = new ArrayList<InnerAction>();
		}
		for(InnerAction a:iActions){
			InnerAction actToAdd = null;
			try {
				actToAdd = (InnerAction) a.clone();
				
				if(actToAdd==null){
					continue;
				}
				else{
					actToAdd.setMainAction(this);
					innerActions.add(actToAdd);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<InnerAction> getInnerActions(){
		return this.innerActions;
	}
	
	
	@Override
	public Action clone() throws CloneNotSupportedException {
		Action clone = (Action) super.clone();
		clone.eventType = this.eventType;
		clone.innerActions = null;
		clone.addInnerActions(this.innerActions);
		return clone;
	}

	public abstract String getUrl();
	
	public String getUrlPath() {
		String url = getUrl();
		if(url==null || url.isEmpty()){
			return url;
		}
		
		try {
			URI uri = new URI(url);
			
			String query = uri.getQuery();
			if(query!=null && !query.isEmpty())
			{
				return url.substring(0,url.indexOf(query)-1);
				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return url;
	}

	public String getParameterValue(int p){
		List<Entry<String, String>> allPars = getParameters();
		if(p<0 || allPars==null || allPars.size()<1 || p>=allPars.size()){
			return null;
		}
		
		String parValue = null;
		
		Entry<String, String> pair = allPars.get(p);
		parValue = pair.getValue();
		
		return parValue;
	}
	
	public String getParameterName(int p){
		List<Entry<String, String>> allPars = getParameters();
		if(p<0 || allPars==null || allPars.size()<1 || p>=allPars.size()){
			return null;
		}
		
		String parName = null;
		
		Entry<String, String> pair = allPars.get(p);
		parName = pair.getKey();
		
		return parName;
	}
	
	public boolean setParameterValue(int p, Object object){
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
			setResult = true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return setResult;
	}

	public abstract Session getSession();

	public String getChannel(){
		String url = getUrl();
		if(url==null || url.isEmpty()){
			return null;
		}
		
		int separatePos = url.indexOf("://");
		
		if(separatePos>0){
			return url.substring(0, separatePos).trim().toLowerCase();
		}
		
		return "";
	}
	
	public abstract boolean setChannel(String string);

	public List<Entry<String,String>> getParameters(){
		return getParameters(getUrl());
	}
	
	protected List<Entry<String,String>> getParameters(String url){
		List<Entry<String, String>> resList = new ArrayList<Entry<String,String>>();
		
		if(url==null || url.isEmpty()){
			return resList;
		}
		
		try {
			URI uri = new URI(url);
			String query = uri.getQuery();
			if(query==null || query.isEmpty()){
				return resList;
			}
			
			for(String q:query.split("&")){
				if(q.split("=").length==2) {
					String key = q.split("=")[0];
					String value = q.split("=")[1];
					resList.add(new AbstractMap.SimpleEntry<String, String>(key, value));
				}
				
			}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return resList;
	}

	public Object getUser(){
		return this.user;
	}

	public abstract boolean setEncryption(Object object);

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public int getPosition(){
//		return this.input.indexOf(this);
		List<Action> as = this.input.actions();

		for ( int i = 0; i < as.size(); i++ ){
		if ( as.get(i) == this ){
				return i;
			}
		}
		
		return -1;
	}
	
	public abstract boolean setUrl( String url );

	public abstract boolean setMethod( String method );
	
	public abstract String getMethod();
	
	public boolean addParameter(String name, String value) {
		String oldUrl = getUrl();
		
		if(oldUrl==null || oldUrl.isEmpty() ||
				name==null || name.isEmpty() ||
				value==null || value.isEmpty()){
			return false;
		}
		
		List<Entry<String, String>> allPars = getParameters(oldUrl);
		if(allPars==null){
			allPars = new ArrayList<Entry<String,String>>(); 
		}
		allPars.add(new AbstractMap.SimpleEntry<String, String>(name, value));
		
		int queryIndex = oldUrl.indexOf("?");
		
		String newUrl = oldUrl;
		if(queryIndex>=0){
			newUrl = oldUrl.substring(0, queryIndex);
		}
		
		newUrl += "?";
		
		for(int i=0; i<allPars.size(); i++){
			newUrl += allPars.get(i).getKey() + "=" + allPars.get(i).getValue();
			if(i<allPars.size()-1){
				newUrl += "&";
			}
		}
		
		setUrl(newUrl);

		return true;
	}
	
	public boolean contain(String url){
		String currentURL = this.getUrl();
		if(currentURL != null && url!=null){
			currentURL = URLUtil.standardUrl(currentURL);
			url = URLUtil.standardUrl(url);
			
			return currentURL.equals(url);
		}
		return false;
	}
	
	public abstract boolean containAccount(Account acc);
	
	public abstract boolean containCredential(String userParam, String passwordParam);
	
	public abstract boolean containCredential(Account acc);
	
	public abstract Account getCredential(String userParam, String passwordParam);
	
	public abstract Action changeCredential(Account acc);

	/**
	 * @return the new relevant channel of the action. This method should be overridden
	 */
	public String getNewChannel() {
		return null;
	}

	/**
	 * @return the old relevant channel of the action. This method should be overridden
	 */
	public String getOldChannel() {
		return null;
	}

	public abstract boolean isChannelChanged();

	public void setUser(Account user) {
		this.user = user;
	}

	public Long getPreviousActionID() {
		Long idToCompare = this.actionID;
		
		Long prevID = null;
		Long currentID = null;
		
		List<Action> actions = input.actions();
		
		for(int i=0; i<actions.size(); i++){
			Action act = actions.get(i);
			
			prevID = currentID;
			currentID = new Long(act.getActionID().longValue());
			
			if(currentID.equals(idToCompare)){
				return prevID;
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return "Action [" + eventType + "] : " + actionID;
	}

	public void updateUrl(String newURL) {
		setUrl(newURL);
	}
	
	public abstract JsonArray toJson();

	public abstract boolean containFormInput();

	public abstract JsonArray getFormInputs(); 
	
	public abstract boolean containFormInputForFilePath();
	
	public boolean hasTheSameUrl(Action act) {
		if (getUrl()==null || act.getUrl()==null) {
			return false;
		}
		
		return URLUtil.hasTheSameUrl(getUrl(), act.getUrl());
	}

	
}
