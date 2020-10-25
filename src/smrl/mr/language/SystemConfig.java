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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class SystemConfig {
	private String SUT;
	private String inputFile;
	private String outputFile;
	private String outputStore;
	private String randomFilePathFile;
	private String randomAdminFilePathFile;
	private String logoutURL;
//	private String loginURL;
//	private String userParameter;
//	private String passwordParameter;
	private ArrayList<LoginParam> loginParams;
	private List<String> ignoreURLs; 
	private String proxyAddress;
	private int proxyPort;
	private String proxyApiKey;
	private String proxyCertificate;
	private boolean usedProxy;
	private JsonObject cleanUp;
	private long waitTimeAfterAction;	//in milisecond
	private JsonObject errorSigns;
	private JsonObject signup;
	private List<String> confirmationTexts;
	private List<String> confirmationButtons;
	private Map<String, ArrayList<String>> supervisedUser;
	private boolean headless;
	private String actionsChangedUrlFileName;
	
	
	static final int DEFAULT_WAIT_TIME = 1000;
	static final String anonymUsername = "wrong";
	static final String anonymPassword = "wrong";
	
	public SystemConfig() {
		this.SUT = "";
		this.inputFile = "";
		this.outputFile = "";
		this.outputStore = "";
		this.randomFilePathFile = "";
		this.randomAdminFilePathFile = "";
		this.logoutURL = "";
//		this.loginURL = "";
//		this.userParameter = "";
//		this.passwordParameter = "";
		this.loginParams = new ArrayList<LoginParam>();
		this.ignoreURLs = new ArrayList<String>();
		this.proxyAddress = "";
		this.proxyPort = 0;
		this.proxyApiKey = "";
		this.proxyCertificate = "";
		this.usedProxy = false;
		this.cleanUp = new JsonObject();
		this.waitTimeAfterAction = DEFAULT_WAIT_TIME;
		this.errorSigns = new JsonObject();
		this.signup = new JsonObject();
		this.confirmationTexts = new ArrayList<String>();
		this.confirmationButtons = new ArrayList<String>();
		this.supervisedUser = new HashMap<String, ArrayList<String>>();
		this.headless = false;
		this.actionsChangedUrlFileName = "";
	}
	
	public SystemConfig(String configFile){
		//1. Load input from json file
		Gson gson = new Gson();
		File jsonFile = Paths.get(configFile).toFile();
		if (!jsonFile.exists()) {
			System.out.println("File not found: " + configFile);
			return;
		}

		try {
			JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
			
			if(jsonObject.keySet().contains("SUT")){
				this.SUT = jsonObject.get("SUT").getAsString().trim();
			}
			else{
				this.SUT = "";
			}
			
			if(jsonObject.keySet().contains("inputFile")){
				this.inputFile = jsonObject.get("inputFile").getAsString().trim();
			}
			else{
				this.inputFile = "";
			}
			
			if(jsonObject.keySet().contains("outputFile")){
				this.outputFile = jsonObject.get("outputFile").getAsString().trim();
			}
			else{
				this.outputFile = "";
			}
			
			if(jsonObject.keySet().contains("outputStore")){
				this.outputStore = jsonObject.get("outputStore").getAsString().trim();
			}
			else{
				this.outputStore = "";
			}
			
			if(jsonObject.keySet().contains("randomFilePathFile")){
				this.randomFilePathFile = jsonObject.get("randomFilePathFile").getAsString().trim();
			}
			else{
				this.randomFilePathFile = "";
			}
			
			if(jsonObject.keySet().contains("randomAdminFilePathFile")){
				this.randomAdminFilePathFile = jsonObject.get("randomAdminFilePathFile").getAsString().trim();
			}
			else{
				this.randomAdminFilePathFile = "";
			}
			
			if(jsonObject.keySet().contains("logoutURL")){
				this.logoutURL = jsonObject.get("logoutURL").getAsString().trim();
			}
			else{
				this.logoutURL = "";
			}
			
			this.loginParams = new ArrayList<LoginParam>();
			if(jsonObject.keySet().contains("loginParams")){
				JsonArray loginPars = jsonObject.get("loginParams").getAsJsonArray();
				
				for(int i=0; i<loginPars.size(); i++){
					JsonObject logPar = loginPars.get(i).getAsJsonObject();
					String newUrl = "";
					String newUserPar = "";
					String newPassPar = "";
					
					if(logPar.keySet().contains("loginURL")) {
						newUrl = logPar.get("loginURL").getAsString();
					}
					if(logPar.keySet().contains("userParameter")) {
						newUserPar = logPar.get("userParameter").getAsString();
					}
					if(logPar.keySet().contains("passwordParameter")) {
						newPassPar = logPar.get("passwordParameter").getAsString();
					}

					if(newUrl!=null && newUserPar!=null && newPassPar!=null &&
							!newUrl.isEmpty() &&
							!newUserPar.isEmpty() &&
							!newPassPar.isEmpty()) {
						LoginParam newLoginPar = new LoginParam(newUrl, newUserPar, newPassPar);
						this.loginParams.add(newLoginPar);
					}
				}
			}

			if(jsonObject.keySet().contains("loginURL") && 
					jsonObject.keySet().contains("userParameter") && 
					jsonObject.keySet().contains("passwordParameter")){
				 String newUrl = jsonObject.get("loginURL").getAsString();
				 String newUserPar = jsonObject.get("userParameter").getAsString();
				 String newPassPar = jsonObject.get("passwordParameter").getAsString();
				 
				 if(newUrl!=null && newUserPar!=null && newPassPar!=null &&
						 !newUrl.isEmpty() &&
						 !newUserPar.isEmpty() &&
						 !newPassPar.isEmpty()) {
					 LoginParam newLoginPar = new LoginParam(newUrl, newUserPar, newPassPar);
					 if(!this.loginParams.contains(newLoginPar)) {
						 this.loginParams.add(newLoginPar);
					 }
				 }
				
			}
			
			
			if(jsonObject.keySet().contains("proxy")){
				JsonObject proxy = jsonObject.get("proxy").getAsJsonObject();
				boolean gotAddress = false;
				boolean gotPort = false;
				boolean gotApiKey = false;
				boolean gotCertificate = false;
				
				if(proxy.keySet().contains("address")){
					this.proxyAddress = proxy.get("address").getAsString().trim();
					gotAddress = true;
				}
				else{
					this.proxyAddress = "";
				}
				
				if(proxy.keySet().contains("port")){
					this.proxyPort = proxy.get("port").getAsInt();
					gotPort = true;
				}
				else{
					this.proxyPort = 0;
				}
				
				if(proxy.keySet().contains("api_key")){
					this.proxyApiKey = proxy.get("api_key").getAsString().trim();
					gotApiKey = true;
				}
				else{
					this.proxyApiKey = "";
				}
				
				if(proxy.keySet().contains("certificate")){
					this.proxyCertificate = proxy.get("certificate").getAsString().trim();
					gotCertificate = true;
				}
				else{
					this.proxyCertificate = "";
				}
				
				if(gotAddress && gotPort && gotApiKey && gotCertificate){
					this.usedProxy = true;
				}
				else{
					this.usedProxy = false;
				}
			}
			else{
				this.proxyAddress = "";
				this.proxyPort = 0;
				this.proxyApiKey = "";
				this.proxyCertificate = "";
				this.usedProxy = false;
			}
			
			if(jsonObject.keySet().contains("ignoreURLs")){
				ArrayList<String> ig = new ArrayList<String>();
				JsonArray jArray = jsonObject.get("ignoreURLs").getAsJsonArray();
				for(int i=0; i<jArray.size(); i++){
					String url = jArray.get(i).getAsString().trim();
					ig.add(url);
				}
				this.ignoreURLs = ig;
			}
			else{
				this.ignoreURLs = new ArrayList<String>();
			}
			
			if(jsonObject.keySet().contains("cleanUp")){
				this.cleanUp = jsonObject.get("cleanUp").getAsJsonObject();
			}
			else{
				this.cleanUp = new JsonObject();
			}
			
			if(jsonObject.keySet().contains("waitTimeAfterAction")){
				this.waitTimeAfterAction = jsonObject.get("waitTimeAfterAction").getAsLong();
			}
			else{
				this.waitTimeAfterAction = DEFAULT_WAIT_TIME;
			}
			
			if(jsonObject.keySet().contains("errorSigns")){
				this.errorSigns = jsonObject.get("errorSigns").getAsJsonObject();
			}
			else{
				this.errorSigns = new JsonObject();
			}
			
			if(jsonObject.keySet().contains("signup")){
				this.signup = jsonObject.get("signup").getAsJsonObject();
			}
			else{
				this.signup = new JsonObject();
			}
			
			if(jsonObject.keySet().contains("confirmationTexts")){
				ArrayList<String> conTexts = new ArrayList<String>();
				JsonArray jArray = jsonObject.get("confirmationTexts").getAsJsonArray();
				for(int i=0; i<jArray.size(); i++){
					String cText = jArray.get(i).getAsString().trim();
					conTexts.add(cText);
				}
				this.confirmationTexts = conTexts;
			}
			else{
				this.confirmationTexts = new ArrayList<String>();
			}
			
			if(jsonObject.keySet().contains("confirmationButtons")){
				ArrayList<String> conButtons = new ArrayList<String>();
				JsonArray jArray = jsonObject.get("confirmationButtons").getAsJsonArray();
				for(int i=0; i<jArray.size(); i++){
					String cText = jArray.get(i).getAsString().trim();
					conButtons.add(cText);
				}
				this.confirmationButtons = conButtons;
			}
			else{
				this.confirmationButtons = new ArrayList<String>();
			}
			
			if(jsonObject.keySet().contains("supervisedUser")){
				HashMap<String, ArrayList<String>> sUser = new HashMap<String, ArrayList<String>>();
				
				JsonObject suObject = jsonObject.get("supervisedUser").getAsJsonObject();
				if(suObject!=null && suObject.size()>0) {
					for(String key:suObject.keySet()) {
						JsonArray ls = suObject.get(key).getAsJsonArray();
						if(ls!=null && ls.size()>0) {
							ArrayList<String> listWeakUsers = new ArrayList<String>();
							for(int i=0; i<ls.size(); i++) {
								String username = ls.get(i).getAsString();
								if(username!=null && !username.isEmpty()) {
									listWeakUsers.add(username);
								}
							}
							
							if(listWeakUsers.size()>0) {
								sUser.put(key, listWeakUsers);
							}
						}
					}
				}
				
				this.supervisedUser = sUser;
			}
			else{
				this.supervisedUser = new HashMap<String, ArrayList<String>>();
			}
			
			if(jsonObject.keySet().contains("headless")){
				this.headless = jsonObject.get("headless").getAsBoolean();
			}
			else{
				this.headless = false;
			}
			
			if(jsonObject.keySet().contains("actionsChangedUrlFileName")){
				this.actionsChangedUrlFileName = jsonObject.get("actionsChangedUrlFileName").getAsString().trim();
			}
			else{
				this.actionsChangedUrlFileName = "";
			}
			
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getSUT() {
		return SUT;
	}

	public void setSUT(String sUT) {
		SUT = sUT;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputStore() {
		return outputStore;
	}

	public void setOutputStore(String outputStore) {
		this.outputStore = outputStore;
	}

	public String getRandomFilePathFile() {
		return randomFilePathFile;
	}

	public void setRandomFilePathFile(String randomFilePathFile) {
		this.randomFilePathFile = randomFilePathFile;
	}

	
	
	public String getRandomAdminFilePathFile() {
		return randomAdminFilePathFile;
	}
	

	public List<String> getConfirmationTexts() {
		return confirmationTexts;
	}

	public void setConfirmationTexts(List<String> confirmationTexts) {
		this.confirmationTexts = confirmationTexts;
	}

	public List<String> getConfirmationButtons() {
		return confirmationButtons;
	}

	public void setConfirmationButtons(List<String> confirmationButtons) {
		this.confirmationButtons = confirmationButtons;
	}

	public void setRandomAdminFilePathFile(String randomAdminFilePathFile) {
		this.randomAdminFilePathFile = randomAdminFilePathFile;
	}

	public Map<String, ArrayList<String>> getSupervisedUser() {
		return supervisedUser;
	}

	public void setSupervisedUser(Map<String, ArrayList<String>> supervisedUser) {
		this.supervisedUser = supervisedUser;
	}

	public String getLogoutURL() {
		return logoutURL;
	}

	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}

	public JsonObject getSignup() {
		return signup;
	}

	public void setSignup(JsonObject signup) {
		this.signup = signup;
	}

	public String getProxyCertificate() {
		return proxyCertificate;
	}

	public void setProxyCertificate(String proxyCertificate) {
		this.proxyCertificate = proxyCertificate;
	}

	public boolean isUsedProxy() {
		return usedProxy;
	}

	public void setUsedProxy(boolean usedProxy) {
		this.usedProxy = usedProxy;
	}

	public long getWaitTimeAfterAction() {
		return waitTimeAfterAction;
	}

	public void setWaitTimeAfterAction(long waitTimeAfterAction) {
		this.waitTimeAfterAction = waitTimeAfterAction;
	}

	public JsonObject getErrorSigns() {
		return errorSigns;
	}

	public void setErrorSigns(JsonObject errorSigns) {
		this.errorSigns = errorSigns;
	}

//	public String getLoginURL() {
//		return loginURL;
//	}
	
	public ArrayList<String> getLoginURLs() {
		ArrayList<String> result = new ArrayList<String>();
//		if(this.loginURL!=null && !this.loginURL.isEmpty()) {
//			result.add(this.loginURL);
//		}
			

		if(this.loginParams!=null && this.loginParams.size()>0) {
			for(LoginParam logPar:this.loginParams) {
				result.add(logPar.loginUrl);
			}
		}
		
		return result;
	}

//	public void setLoginURL(String loginURL) {
//		this.loginURL = loginURL.trim();
//	}

//	public String getUserParameter() {
//		return userParameter;
//	}
	
	public ArrayList<String> getUserParameters() {
		ArrayList<String> result = new ArrayList<String>();
		
//		if(this.userParameter!=null && !this.userParameter.isEmpty()) {
//			result.add(this.userParameter);
//		}
		
		if(this.loginParams!=null && this.loginParams.size()>0) {
			for(LoginParam logPar:this.loginParams) {
				result.add(logPar.userParam);
			}
		}
		
		return result;
	}

//	public void setUserParameter(String userParameter) {
//		this.userParameter = userParameter.trim();
//	}

//	public String getPasswordParameter() {
//		return passwordParameter;
//	}
	
	public ArrayList<String> getPasswordParameters() {
		ArrayList<String> result = new ArrayList<String>();
		
//		if(this.passwordParameter!=null && !this.passwordParameter.isEmpty()) {
//			result.add(this.passwordParameter);
//		}
		
		if(this.loginParams!=null && this.loginParams.size()>0) {
			for(LoginParam logPar:this.loginParams) {
				result.add(logPar.passwordParam);
			}
		}
		
		return result;
	}

//	public void setPasswordParameter(String passwordParameter) {
//		this.passwordParameter = passwordParameter.trim();
//	}
	
	public ArrayList<LoginParam> getLoginParams() {
		return loginParams;
	}
	

	public List<String> getIgnoreURLs() {
		return ignoreURLs;
	}
	

	public String getIgnoreURL(int pos) {
		String res = null;
		
		if(this.ignoreURLs.size()<=pos || pos<0){
			return null;
		}
		res = this.ignoreURLs.get(pos);
		
		return res;
	}

	public void setIgnoreURLs(List<String> ignoreURLs) {
		this.ignoreURLs = ignoreURLs;
	}
	
	public String getProxyAddress() {
		return proxyAddress;
	}

	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyApiKey() {
		return proxyApiKey;
	}

	public void setProxyApiKey(String proxyApiKey) {
		this.proxyApiKey = proxyApiKey;
	}

	public boolean isLoginURL(String url){
		if(url==null || url.isEmpty()) {
			return false;
		}
		
		ArrayList<String> allLoginUrls = getLoginURLs();
		if(allLoginUrls==null || allLoginUrls.size()<1) {
			return false;
		}
		
		for(String logUrl:getLoginURLs()) {
			if(equalURL(logUrl, url)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isIgnoreURL(String url){
		for(String iURL:this.ignoreURLs){
			if(equalURL(iURL, url)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean equalURL(String url1, String url2){
		if(url1==null || url2==null || url1.isEmpty() || url2.isEmpty()){
			return false;
		}
		
		String u1 = url1.trim().toLowerCase();
		String u2 = url2.trim().toLowerCase();
		while(u1.endsWith("/")){
			u1 = u1.substring(0, u1.length()-1);
		}
		while(u2.endsWith("/")){
			u2 = u2.substring(0, u2.length()-1);
		}
		return u2.equals(u1);
	}

	public JsonObject getCleanUp() {
		return cleanUp;
	}

	public void setCleanUp(JsonObject cleanUp) {
		this.cleanUp = cleanUp;
	}
	
	public boolean hasAccountParameters(){
//		if(this.userParameter != null &&
//				this.passwordParameter!= null &&
//				!this.userParameter.isEmpty() &&
//				!this.passwordParameter.isEmpty()){
//			return true;
//		}
		
		if(this.loginParams!=null && this.loginParams.size()>0) {
			for(LoginParam logPar:this.loginParams) {
				if(logPar.hasAccountParemeters()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSignupUrl(String url) {
		return equalURL(getSignupUrl(), url);
	}

	public String getSignupUrl() {
		String res = null;
		
		if(signup != null && signup.keySet().contains("url")){
			res =  signup.get("url").getAsString();
		}
		
		return res;
	}
	
	public String getSignupUserParam(){
		String res = null;
		
		if(signup != null && signup.keySet().contains("userParam")){
			res =  signup.get("userParam").getAsString();
		}
		
		return res;
	}
	
	public List<String> getSignupPasswordParams(){
		ArrayList<String> res = new ArrayList<String>();
		
		if(signup != null && signup.keySet().contains("passwordParams")){
			JsonArray pArray = signup.get("passwordParams").getAsJsonArray();
			if(pArray.size()>0){
				for(int i=0; i<pArray.size(); i++){
					res.add(pArray.get(i).getAsString().trim());
				}
			}
		}
		
		return res;
	}

	public boolean isLogoutUrl(String url) {
		if(url==null || url.isEmpty()){
			return false;
		}
		return equalURL(this.logoutURL, url);		
	}

	
	
	public boolean isHeadless() {
		return headless;
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	public boolean isSupervisorOf(String username1, String username2) {
		if(username1==null || username2==null) {
			return false;
		}
		
		if(username1.trim().equals(username2.trim())) {
			return true;
		}
		
		boolean isSuper = false;
		if(supervisedUser.containsKey(username1)) {
			ArrayList<String> listUsers = supervisedUser.get(username1);
			if(listUsers!=null && listUsers.contains(username2)) {
				isSuper = true;
			}
		}
		
		return isSuper;
	}

	
	public String getActionsChangedUrlFileName() {
		return this.actionsChangedUrlFileName;
	}
	
}
