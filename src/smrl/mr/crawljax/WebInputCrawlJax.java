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
package smrl.mr.crawljax;

import java.util.ArrayList;
import java.util.List;

import smrl.mr.language.Action;
import smrl.mr.language.Input;
import smrl.mr.language.LoginParam;
import smrl.mr.language.Operations;
import smrl.mr.language.PrintUtil;
import smrl.mr.language.actions.AlertAction;
import smrl.mr.language.actions.StandardAction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WebInputCrawlJax extends Input{

	private ArrayList<Action> actions; 
	
	
	public WebInputCrawlJax(JsonArray jsonInput) {
		this.actions = new ArrayList<Action>();
		
		for(int i = 0; i<jsonInput.size(); i++){
			JsonObject jsonAct = jsonInput.get(i).getAsJsonObject();
			if(jsonAct!= null){
				Action act = Action.newAction(jsonAct);
				if (act!=null){
					act.setInput(this);
					
					//Check if the next is an inner input
					int j = i+1;
					while(j<jsonInput.size()){
						JsonObject nextAct = jsonInput.get(j).getAsJsonObject();
						if(nextAct!= null && 
								nextAct.keySet().contains("eventType") &&
								nextAct.get("eventType").getAsString().equals("alert")){
							
							//load inner action
							AlertAction iAct = new AlertAction(nextAct);
							iAct.setMainAction(act);
							
							act.addInnerAction(iAct);
							j+=1;
						}
						else{
							break;
						}
					}
					i = j - 1;
					
					this.actions.add(act);
				}
			}
		}
		
		
	}
	
	public WebInputCrawlJax(Action action) {
		this.actions = new ArrayList<Action>();
		
		try {
			Action clone = action.clone();
			clone.setInput(this);
			this.addAction(clone);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public WebInputCrawlJax(List<Action> actions) {
		this.actions = new ArrayList<Action>();
		if(actions!=null && !actions.isEmpty()){
			for(int i=0; i<actions.size(); i++){
				try {
					Action clone = actions.get(i).clone();
					clone.setInput(this);
					this.addAction(clone);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public WebInputCrawlJax() {
		this.actions = new ArrayList<Action>();
		
	}
	
	public Input Input( List<Action> actions ) {
		return new WebInputCrawlJax(actions);
	}
	
	@Override
	public ArrayList<Action> actions() {
		return this.actions;
	}

	public void setActions(List<Action> actions){
		this.actions = new ArrayList<Action>();
		
		for(int i = 0; i<actions.size(); i++){
			addAction(i, actions.get(i));
		}
	}
	

	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof WebInputCrawlJax)){
			return false;
		}
		
		WebInputCrawlJax that = (WebInputCrawlJax) obj;
		
		if(this.actions==null && that.actions==null){
			return true;
		}
		
		if(this.actions.size()!= that.actions.size()){
			return false;
		}
		
		for(int i=0; i<this.actions.size(); i++){
			if(!this.actions.get(i).equals(that.actions.get(i))){
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void copyActionTo(int x, int y) {
		actions.add(y, actions.get(x));
	}

	@Override
	public void addAction(int pos, Action action) {
		if(pos<0 || pos>actions.size()){
			return;
		}
		
		Action a;
		try {
			a = (Action) action.clone();
			a.setInput(this);
			
			//*** update user in a
			//1. if a contains a user
//			fix: have to find the exact account parameters (username and password parameters)
//			String userParam = WebProcessor.getSysConfig().getUserParameter();
//			String passwordParam = WebProcessor.getSysConfig().getPasswordParameter();
			
			if(a instanceof StandardAction) { 
				ArrayList<LoginParam> loginParams = WebProcessor.getSysConfig().getLoginParams();

				LoginParam usedLoginParam = ((StandardAction)a).usedLoginParam(loginParams);

//				if(a.containCredential(userParam, passwordParam)){
				if(usedLoginParam!=null) {
					Account user = new Account();
//					user.setUsernameParam(userParam);
//					user.setPasswordParam(passwordParam);
					user.setUsernameParam(usedLoginParam.userParam);
					user.setPasswordParam(usedLoginParam.passwordParam);
					user.setUsername(null);
					user.setPassword(null);
//					user = processCurrentActionAndUpdateUser((StandardAction) a, user, userParam, passwordParam);
					user = processCurrentActionAndUpdateUser(
							(StandardAction) a, user, usedLoginParam.userParam, usedLoginParam.passwordParam);
				}
			}
			
			//2. if not, a inherits the user from the action at pos
			else{
				if(pos>0){
					a.setUser((Account)actions.get(pos-1).getUser());
				}
				else{
					a.setUser(new Account());
				}
			}
			
			actions.add( pos, a);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addAction(Action action) {
		addAction(actions.size(), action);
	}
	
	@Override
	public String toString() {
		
		if ( PrintUtil.USER_FRIENDLY_TO_STRING ) {
			StringBuffer sb = new StringBuffer();
			sb.append("[\n");
			for (Action a : actions ) {
				sb.append("\t");
				sb.append(a);
				sb.append("\n");
			}
			sb.append("]");

			return sb.toString();
		}
		
		return actions.toString();
	}

	@Override
	public WebInputCrawlJax clone() throws CloneNotSupportedException {
		WebInputCrawlJax clone = (WebInputCrawlJax) super.clone();
		clone.actions = new ArrayList<>();
		for ( Action action : actions ){
//			clone.actions.add((Action)action.clone());
			clone.addAction(action.clone());
		}
		
		return clone;
	}
	
	/**
	 * Return the number of actions in the input
	 */
	public int size(){
		return this.actions.size();
	}
	
	/**
	 * Check if the input contain a concrete url
	 * @param url to find
	 */
	public boolean contains(String url){
		for(Action a : this.actions){
			if(a.contain(url)){
				return true;
			}
		}
		return false;
	}
	
	public boolean containAccount(Account user){
		for(Action acc:this.actions){
			if(acc.containAccount(user)){
				return true;
			}
		}
		return false;
	}
	
	public WebInputCrawlJax changeCredential(Account user2){
		if(user2==null || 
				user2.getUsername()==null || 
				user2.getPassword()==null) {
			return null;
		}
		
		Account loginAccount = null;
		boolean changed = false;
		for(Action act:actions()) {
			if(Operations.isLogin(act)){
				loginAccount = (Account) act.getUser();
				if(loginAccount!=null &&
						!(loginAccount.getUsername().equals(user2.getUsername()) &&		//will not change if user2 and loginAccount have the same username and password
						  loginAccount.getPassword().equals(user2.getPassword()))) {
					loginAccount.setUsername(user2.getUsername());
					loginAccount.setPassword(user2.getPassword());
					changed = true;
					break;
				}
			}
		}
		
		if(loginAccount==null || changed==false) {
			return null;
		}
		
		WebInputCrawlJax res=null;
		try {
			res = this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		if(res == null){
			return null;
		}
		
		List<Action> newActions = res.actions();
		for(int i=0; i<res.actions().size(); i++){
			if(res.actions.get(i).containCredential(loginAccount)){
				try {
					Action toReplaceAct =  (Action) res.actions.get(i).clone();
					Action newAct = toReplaceAct.changeCredential(loginAccount);

					if(newAct != null){
						newActions.remove(i);
						newActions.add(i, newAct);
					}
					else{
						System.out.println("Cannot change credencial in the action "+i+ " in the input " + this.toString());
					}

				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		res.setActions(newActions);
		return res;
	}

	public List<String> getAllUrls() {
		List<String> res = new ArrayList<String>();
		for(Action act:this.actions){
			String url = act.getUrl();
			if(url!=null && !url.isEmpty()){
				res.add(url);
			}
		}
		return res;
	}

	@Override
	public int indexOf(Action action) {
		return this.actions.indexOf(action);
	}

	public void identifyUsers(String userParam, String passwordParam, WebProcessor webPro) {
		Account user = new Account();
		user.setUsernameParam(userParam);
		user.setPasswordParam(passwordParam);
		user.setUsername(null);
		user.setPassword(null);
		
		boolean hasConfig = false;
		if(webPro!=null &&
				WebProcessor.sysConfig!=null){
			hasConfig = true;
		}

		for(Action a:actions){
			if (a instanceof StandardAction) {
				if(hasConfig && WebProcessor.isLogin(a)){
					user = processCurrentActionAndUpdateUser((StandardAction) a, user, userParam, passwordParam);
				}
				else{	//accept all account-like info as user, even failed login
					user = processCurrentActionAndUpdateUser((StandardAction) a, user, userParam, passwordParam);
				}
				
			} else {
				a.setUser(user);
			}
		}
	}

	private Account processCurrentActionAndUpdateUser(StandardAction a, Account user, String userParam, String passwordParam) {
		StandardAction sAct = (StandardAction) a;
		JsonArray fInputs = sAct.getFormInputs();
		
		String username = "";
		String password = "";
		
		boolean hasUser = false;
		boolean hasPassword = false;
		for(int i = 0; i<fInputs.size(); i++){
			JsonObject fi = fInputs.get(i).getAsJsonObject();
			if(fi.keySet().contains("identification") && fi.keySet().contains("values")){
				JsonObject iden = fi.get("identification").getAsJsonObject();
				JsonArray fiValues = fi.get("values").getAsJsonArray();
				
				
				
				if(iden.keySet().contains("value") && fiValues.size()>=1){
					String idenKey = iden.get("value").getAsString().trim();
					if(idenKey.equals(userParam)){
						if(fiValues.size()>0){
							username = fiValues.get(0).getAsString().trim();
							hasUser = true;
						}
					}
					else if(idenKey.equals(passwordParam)){
						if(fiValues.size()>0){
							password = fiValues.get(0).getAsString().trim();
							hasPassword = true;
						}
					}
				}
			}
			
			if(hasUser && hasPassword){
				break;
			}
		}
		
		if(hasUser && hasPassword){
			user = new Account();
			user.setUsernameParam(userParam);
			user.setPasswordParam(passwordParam);
			user.setUsername(username);
			user.setPassword(password);
			a.setUser(user);
			//setUser(user);
			
		} else {
			a.setUser(user);
		}
		
		return user;
	}

	@Override
	public JsonArray toJson() {
		JsonArray res = new JsonArray();
		
		if(actions==null || actions.size()<1){
			return res;
		}
		
		for(Action act:actions){
			res.addAll(act.toJson());
		}
		
		return res;
	}

	
	public void identifyUsers(WebProcessor webProcessor) {
		if(webProcessor==null || WebProcessor.sysConfig==null) {
			return;
		}
		
		Account user = new Account();
		user.setUsernameParam(null);
		user.setPasswordParam(null);
		user.setUsername(null);
		user.setPassword(null);
		
		for(Action a:actions){
			if (a instanceof StandardAction) {
				LoginParam usedLoginParam = ((StandardAction)a).usedLoginParam(WebProcessor.sysConfig.getLoginParams());
				if(usedLoginParam!=null) {
					user.setUsernameParam(usedLoginParam.userParam);
					user.setPasswordParam(usedLoginParam.passwordParam);

					if(WebProcessor.isLogin(a)){
						user = processCurrentActionAndUpdateUser((StandardAction) a, user, usedLoginParam.userParam, usedLoginParam.passwordParam);
					}
					else{	//accept all account-like info as user, even failed login
						user = processCurrentActionAndUpdateUser((StandardAction) a, user, usedLoginParam.userParam, usedLoginParam.passwordParam);
					}
				}
				else {
					a.setUser(user);
				}
				
			} else {
				a.setUser(user);
			}
		}
		
		
	}

	
	public boolean containAction(Action addedAction) {
		return actions.contains(addedAction);
	}

	
}
