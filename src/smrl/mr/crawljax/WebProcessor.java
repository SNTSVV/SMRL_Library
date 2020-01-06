package smrl.mr.crawljax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.language.Action;
import smrl.mr.language.Action.ActionType;
import smrl.mr.language.CookieSession;
import smrl.mr.language.LoginParam;
import smrl.mr.language.MR;
import smrl.mr.language.NoMoreInputsException;
import smrl.mr.language.Operations;
import smrl.mr.language.SystemConfig;
import smrl.mr.language.actions.AlertAction;
import smrl.mr.language.actions.IndexAction;
import smrl.mr.language.actions.InnerAction;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.language.actions.WaitAction;
import smrl.mr.utils.URLUtil;

public class WebProcessor {
	private List<Account> userList;
	private List<WebInputCrawlJax> inputList;
	private Iterator<WebInputCrawlJax> inputIter;
	private List<String> randomFilePath;
	private String latestUrl;
	private String currentUsername;
	private String changedUsername;
	private File outputFile;
	public static SystemConfig sysConfig;
	private ClientApi	proxyApi;
	private boolean cleanUpDom;
	private String downloadFilePath;
	private boolean waitBeforeEachAction=true;
	
	private String[] ignoredObjects = {".js", ".css"};
	
	//updateUrlMap contains action IDs: 
	//- first one is the action should check
	//- second one is the action has the url which will be updated (from the first one)
	private HashMap<Long, Long> updateUrlMap;	
	

	HashMap<String,HashSet<String>> urlsAccessedByUsers = new HashMap<String, HashSet<String>>();
	private static String WRONG_USERNAME = "wrong";
	private Set<String> URLsAcessedByEveryUser;

	private boolean autoDetectConfirmation = true;
	private boolean alwaysConfirm = true;
	private boolean prioritizeButton=true;
//	private String[] confirmationTexts = {"You must use POST method to trigger builds", 
//	"The URL you're trying to access requires that requests be sent using POST (like a form submission)"};

	
	private static String ADMIN_USERNAME = "admin";
	
	private Account admin;
	private ArrayList<String> randomAdminFilePath;
	private boolean headless=false;
	private boolean backToRightPageBeforeAction=true;
	private boolean checkStatusCode=false;
	private static HashSet<String> visibleWithoutLogin;
	
	
	
	public WebProcessor() {
		this.userList = new ArrayList<Account>();
		this.setInputList(new ArrayList<WebInputCrawlJax>());
		this.inputIter = this.getInputList().iterator();
		this.randomFilePath = new ArrayList<String>();
		this.latestUrl = "";
		this.currentUsername = "";
		this.changedUsername = "";
		this.outputFile = null;
		sysConfig = new SystemConfig();
		this.proxyApi = null;
		this.cleanUpDom = true;
//		configDownloadFolder("./Downloads");
		this.updateUrlMap = new HashMap<Long, Long>();
	}
	

	public boolean isHeadless() {
		return headless;
	}


	public void setHeadless(boolean headless) {
		this.headless = headless;
	}


	public boolean isAutoDetectConfirmation() {
		return autoDetectConfirmation;
	}


	public boolean isAlwaysConfirm() {
		return alwaysConfirm;
	}


	public boolean isPrioritizeButton() {
		return prioritizeButton;
	}


	public void resetUpdateUrlMap(){
		this.updateUrlMap.clear();
	}
	
	public ClientApi getProxyApi() {
		return proxyApi;
	}

	public boolean isCleanUpDom() {
		return cleanUpDom;
	}

	public static SystemConfig getSysConfig() {
		return sysConfig;
	}

	public void setCleanUpDom(boolean cleanUpDom) {
		this.cleanUpDom = cleanUpDom;
	}

	public String getCurrentUrl() {
		return latestUrl;
	}

	public String getCurrentUsername() {
		return currentUsername;
	}

	public String getChangedUsername() {
		return changedUsername;
	}

	public List<Account> getUserList() {
		return userList;
	}

	public void setUserList(List<Account> userList) {
		this.userList = userList;
	}

	public List<WebInputCrawlJax> getInputList() {
		return inputList;
	}

	public void setInputList(List<WebInputCrawlJax> inputList) {
		this.inputList = inputList;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outFile) {
		String outFolderName = outputFolder();
		File outFolder = new File(outFolderName);
		if(!outFolder.exists()){
			outFolder.mkdirs();
		}
		
		this.outputFile = new File(outFile);
//		try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.outputFile))) {
//			bw.write("Testing result:\n");
//			bw.close( );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		//clear folder doms
		File folder = new File(this.outputFile.getParent()+"/doms");
		if(folder.exists()){
			try {
				FileUtils.deleteDirectory(folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		folder.mkdirs();
	}

	public List<Account> userIterator() {
		return this.userList;
	}

	public boolean guiNotContain(Account user, WebInputCrawlJax input) {
		if (user==null || input == null){
			throw new IllegalStateException();
		}
		
		Set<String> urlsAccessedByUser = retrieveURLsAcessedByUser(user);
		
		for(Action acc:input.actions()){
			String url = acc.getUrl();
//			System.out.println("!!!LOOKING FOR "+url);
			if(url!= null){
				url = url.trim();
				if(!url.isEmpty()){
					if ( ! urlsAccessedByUser.contains(url) ){
//						System.out.println("!!!NOT THERE ");
						return true;
					}
				}
			}
		}
		
//		System.out.println("!!!All URLs found : "+user+" "+input);
		return false;
	}
	
	
	/**
	 * @param user
	 * @return
	 */
	private Set<String> retrieveURLsAcessedByEveryUser() {
		
		HashSet<String> urlsAccessedByUser = new HashSet<String>();

		for(WebInputCrawlJax i: this.inputList){
			
				for(Action acc:i.actions()){
					if ( isLogin(acc) ) {
						break;
					}
					if ( acc.getUrl() != null ){
						String url_ = acc.getUrl().trim();
						if ( url_.endsWith("#") ) {
							url_ = url_.substring(0, url_.length()-1);
						}
						if ( url_.endsWith("/") ) {
							url_ = url_.substring(0, url_.length()-1);
						}
						urlsAccessedByUser.add(url_);
					}
						
//						urlsAccessedByUser.add(acc.getUrl().trim());
//					}
				}
			
		}

		

		return urlsAccessedByUser;
	}
	
	
	
	
	

	


	public void setAutoDetectConfirmation(boolean autoDetectConfirmation) {
		this.autoDetectConfirmation = autoDetectConfirmation;
	}
	
	

	public void setAlwaysConfirm(boolean alwaysConfirm) {
		this.alwaysConfirm = alwaysConfirm;
	}

	public void setPrioritizeButton(boolean prioritizeButton) {
		this.prioritizeButton = prioritizeButton;
	}

	/**
	 * @param user
	 * @return
	 */
	private Set<String> retrieveURLsAcessedByUser(Account user) {
		HashSet<String> urlsAccessedByUser  = urlsAccessedByUsers.get(user.getUsername());

		if ( urlsAccessedByUser != null ){
			return urlsAccessedByUser;
		}

		urlsAccessedByUser = new HashSet<String>();

		for(WebInputCrawlJax i: this.inputList){
			if(i.containAccount(user)){
				for(Action acc:i.actions()){
					if ( acc.getUrl() != null ){
						String url_ = acc.getUrl().trim();
						if ( url_.endsWith("#") ) {
							url_ = url_.substring(0, url_.length()-1);
						}
						if ( url_.endsWith("/") ) {
							url_ = url_.substring(0, url_.length()-1);
						}
						urlsAccessedByUser.add(url_);
					}
						
//						urlsAccessedByUser.add(acc.getUrl().trim());
//					}
				}
			}
		}

		urlsAccessedByUsers.put(user.getUsername(), urlsAccessedByUser);

		return urlsAccessedByUser;
	}
	
	public boolean guiNotContain(Account user, String url){
		if (user==null || url == null ){
			return true;
		}
		
		if ( inputList.isEmpty() ){
			return true;
		}
		
		if ( URLsAcessedByEveryUser == null ) {
			URLsAcessedByEveryUser = retrieveURLsAcessedByEveryUser();
		}
	
		Set<String> urlsAccessedByUser = retrieveURLsAcessedByUser(user);
		
		url = url.trim();
		if(!url.isEmpty()){
			if ( url.endsWith("#") ) {
				url = url.substring(0, url.length()-1);
			}
			if ( url.endsWith("/") ) {
				url = url.substring(0, url.length()-1);
			}
			
			if ( URLsAcessedByEveryUser.contains(url) ){
				//						System.out.println("!!!NOT THERE ");
				return false;
			}
			
			if ( ! urlsAccessedByUser.contains(url) ){
				//						System.out.println("!!!NOT THERE ");
				return true;
			}
		}
		
//		System.out.println("!!!NOT THERE "+url);

		return false;
	}

	public WebInputCrawlJax changeCredential(WebInputCrawlJax _input, Account user2) {
		String userParam = user2.getUsernameParam();
		String passParam = user2.getPasswordParam();
		if(userParam.isEmpty() || passParam.isEmpty()){
			System.out.println("Information of account parameters is missing");
			return _input;
		}
		
		WebInputCrawlJax input;
		try {
			input = _input.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		
		List<Action> actions = input.actions();
		//get the username before changing it
		for(Action act:actions){
			boolean gotten = false;
			if(act.containCredential(user2)){
				//TODO: get info
				JsonArray fInputs = ((StandardAction)act).getFormInputs();
				for(int i = 0; i<fInputs.size(); i++){
					JsonObject fi = fInputs.get(i).getAsJsonObject();
					if(fi.keySet().contains("identification") && fi.keySet().contains("values")){
						JsonObject iden = fi.get("identification").getAsJsonObject();
						JsonArray fiValues = fi.get("values").getAsJsonArray();

						if(iden.keySet().contains("value") && fiValues.size()>=1){
							String idenKey = iden.get("value").getAsString().trim();
							if(idenKey.equals(user2.getUsernameParam())){
								this.currentUsername = fiValues.get(0).getAsString().trim();
								this.changedUsername = user2.getUsername();
								gotten = true;
								break;
							}
						}
					}
				}
			}
			if(gotten){
				break;
			}
		}
		
		//get the last URL in the input
		boolean gotURL = false;
		for(int i=actions.size()-1; i>=0; i--){
			String url = actions.get(i).getUrl();
			if(url!= null){
				if (!url.isEmpty()){
					this.latestUrl = url;
					gotURL = true;
					break;
				}
			}
		}
		if(!gotURL){
			this.latestUrl = "";
		}

		return input.changeCredential(user2);
	}

	public void loadInput(String fileName) throws FileNotFoundException,
			IOException {
		
		//1. Load input from json file
		Gson gson = new Gson();
		File jsonFile = Paths.get(fileName).toFile();
		if (!jsonFile.exists()) {
			System.out.println("Input file not found: " + fileName);
			return;
		}
		
		JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
		
		for(String key:jsonObject.keySet()){
			JsonArray jsonInput = jsonObject.get(key).getAsJsonArray();
			if(jsonInput!= null){
				if(jsonInput.size()>0){
					WebInputCrawlJax input = new WebInputCrawlJax(jsonInput);
					
//					ArrayList<LoginParam> allLoginParams = WebProcessor.sysConfig.getLoginParams();
//					LoginParam usedLoginParam = null;
//					
//					for(Action a:input.actions()) {
//						if(a instanceof StandardAction) {
//							usedLoginParam = ((StandardAction)a).usedLoginParam(allLoginParams);
//						}
//						if(usedLoginParam!=null) {
//							break;
//						}
//					}
//					
//					if(usedLoginParam!=null){
//						input.identifyUsers(usedLoginParam.userParam, usedLoginParam.passwordParam, this);
//					}
					input.identifyUsers(this);

					
					this.inputList.add(input);
				}
			}
			
		} 
		
		// Update inputIter
		this.inputIter = this.getInputList().iterator();
		
	}

	public WebInputCrawlJax newInput() throws NoMoreInputsException {
		// TODO Auto-generated method stub
		return null;
	}

	public WebOutputSequence output(WebInputCrawlJax input) {
		return output(input, false);
	}
	
	public WebOutputSequence output(WebInputCrawlJax input, boolean checkDownloadedObjects) {
		WebOutputSequence outputSequence = new WebOutputSequence();
		
		String latestUrl = "";
		
		//call web browser
		String exePath = "/usr/local/bin/chromedriver";
		System.setProperty("webdriver.chrome.driver", exePath);
		ChromeDriver driver = null;
		
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", this.downloadFilePath);
		
//		System.out.println("Download file path: " + this.downloadFilePath);

		ChromeOptions chOptions = new ChromeOptions();
		chOptions.setExperimentalOption("prefs", chromePrefs);
		chOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		
		if(sysConfig.isUsedProxy()){
			String proxyAP = sysConfig.getProxyAddress().trim() + 
					":" + sysConfig.getProxyPort();
			Proxy proxy = new Proxy();
			proxy.setHttpProxy(proxyAP).setSslProxy(proxyAP);
			
			chOptions.setProxy(proxy);
			
		}
		
		//To accept all SSL certificates, even insecure certificates, and ignore certificate errors
		chOptions.addArguments("--ignore-certificate-errors");
		chOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		chOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		//FIXME: add certificate to driver if needed
		
		if(headless) {
			chOptions.addArguments("headless");
		}
		
		driver = new ChromeDriver(chOptions);
		
		
		List<Action> actions = input.actions();
		
		HashMap<Long,String> actionUrls = new HashMap<Long, String>();
		
		int timeOfConfirm=0;
		for(int i=0; i<actions.size(); i++){
			Action act = actions.get(i);
			String text = "index";
			String aURL = act.getUrl();
			
			if(act.getUrl()!=null && 
					!act.getUrl().trim().isEmpty() && 
					!actionUrls.containsKey(act.getActionID())) {
				actionUrls.put(act.getActionID(), act.getUrl());
			}
			
			ActionType type = act.getEventType();
			
			//Get text and URL
			switch (type){
			case index: 
				text = "index";
				break;
			case alert: 
				text = "Alert " + ((AlertAction)act).getText();
				aURL = "";
				break;
			case click: 
				text = ((StandardAction)act).getText();
				break;
			case hover: 
				text = ((StandardAction)act).getText();
				break;
			case randomClickOnNewElement: 
				text = "randomly click";
				aURL = "";
				break;
			case wait: 
				text = "Wait " + ((WaitAction)act).getMillis();
				aURL = "";
				break;
			default:
				break;
			}
			
			if(Operations.isLogin(act) && act.getUser()!=null) {
				text = "log in with " + ((Account)act.getUser()).getUsername();
			}
			
			System.out.print("\t- Action " + act.getActionID() + " : " + text + " (" + aURL + ")");
			
			if(waitBeforeEachAction){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// process the action (depend on type of action)
			boolean doneAction = false;
			String redirectURL = "";
			String ruleDescription = "";
			
			//create replace rule
			if ( act.isChannelChanged() ){
				String oldChannel = act.getOldChannel();
				String newChannel = act.getNewChannel();
				ruleDescription = "replace_" + oldChannel + "_to_" + newChannel; 
				//set the rule in the proxy to replace the channel or the address
				try {
					proxyApi.replacer.addRule(ruleDescription, "true", "REQ_HEADER_STR", "true", oldChannel, newChannel, "");
				} catch (ClientApiException e) {
					e.printStackTrace();
				}
				
				//proxy.replace( act.getOldChannel(), act.getNewChannel() ) 
			}
			
			//TODO Replace HTTP method using the proxy replacer
			
			//The max id of message in the proxy
			int maxId = 0;
			if(checkDownloadedObjects){
				maxId = getMaxIdFromProxy();
			}
			
			//Start to process request following the type of action (index, click, ...)
			switch (type){
			case index:
			{
				if(aURL!=null && !aURL.isEmpty()){
					try{
						driver.get(aURL);
						doneAction = true;
					}
					catch(WebDriverException e){
						e.printStackTrace();
					}
					
					if(doneAction){
						//get redirect URL
						redirectURL = getRedirectUrl(driver, aURL);
					}
					else{
						doneAction = true;
						redirectURL = "";
					}
				}
				else{
					System.out.println("!!! The index URL should NOT be empty");
					driver.close();
					return outputSequence;
				}
				System.out.println(" --> DONE");
				break;
			}
			
			case alert:
			{
				closeAlertAndGetItsText(driver, ((AlertAction)act).getAccept());
				doneAction = true;
				System.out.println(" --> DONE");
				redirectURL = getRedirectUrl(driver, aURL);
				break;
			}
			
			case click: 
			{
				text = ((StandardAction)act).getText();
				
				//If this action is the POST one, 
				// check if the current URL (from browser) is similar with the currentURL
				// if not -> go back to the currentURL before execute the action
				if(backToRightPageBeforeAction) {
//					if(act.getMethod().toLowerCase().trim().equals("post")){
						String actCurrentURL = ((StandardAction)act).getCurrentURL();
						if(actCurrentURL!=null 
//								&& !actCurrentURL.isEmpty() &&
//								!actCurrentURL.trim().equals("/")
								){
							String browserURL = driver.getCurrentUrl().trim();
							if(!actCurrentURL.equals(browserURL)){
								
								actCurrentURL = processUrlBeforeRequest(driver, actCurrentURL);
								
								if(actCurrentURL!=null) {
									//go back to the actCurrentURL
									driver.get(actCurrentURL);
								}
							}
						}
//					}
				}
				
				//Filling inputs in form
				JsonArray formInputs = ((StandardAction)act).getFormInputs();
				if(formInputs!=null && formInputs.size()>0){
					for(int iForm=0; iForm<formInputs.size(); iForm++){
						JsonObject fInput = formInputs.get(iForm).getAsJsonObject();
						if(fInput.get("values").getAsJsonArray().size() <1){
							continue;
						}
						
						String idHow = fInput.get("identification").getAsJsonObject().get("how").getAsString();
						String idValue = fInput.get("identification").getAsJsonObject().get("value").getAsString();
						
						By by = getByType(idHow, idValue);
						
						//If cannot find any element in the current page by using the "by", go to next form input
						if(by==null ){
							continue;
						}
						else{
							try{
								WebElement foundE = driver.findElement(by);
								if(foundE==null){
									continue;
								}
							}
							catch(NoSuchElementException e){
								continue;
							}
						}
						
						JsonArray values = fInput.get("values").getAsJsonArray();
						if(values.size()<1){
							continue;
						}
						
						String formType = fInput.get("type").getAsString().toLowerCase();
						if(formType.startsWith("text") 
								|| formType.equals("password") 
								|| formType.equals("hidden")
								|| formType.equals("file")){
							
							String valueToSend = "";
							if(values.size() >0){
								valueToSend = values.get(0).getAsString().trim();
							}
							
							//Process the case in which this action is a signup action
							if(isSignup(act)){
								//If this form is username in the signup page
								//add random string (to avoid the case the username has already existed)
								String userParam = sysConfig.getSignupUserParam().trim();
								if(userParam!= null &&
										userParam.equals(idValue)){
									valueToSend += RandomStringUtils.random(5,true,false);
								}
								else{
									//Check if this input is the confirm password in the signup action
									//then get value from password
									List<String> passParams = sysConfig.getSignupPasswordParams();
									if(passParams.size()>1 && 
											passParams.contains(idValue)){
										String passPar = passParams.get(0);	//Get the first password param
										//get value of passPar in the formInputs
										valueToSend = getFormInputValueFromParamName(formInputs, passPar);
									}
								}
							}
							
							if(!valueToSend.isEmpty()){
								//clear available value
								driver.findElement(by).clear();
								
								//send new value to the element
								driver.findElement(by).sendKeys(valueToSend);
							}
						}
						
						else if (formType.equals("checkbox")){
							boolean checkValue = values.get(0).getAsBoolean();
							WebElement option = driver.findElement(by);
							
							if(checkValue){	//if the check box should be selected
								if(!option.isSelected()){	//if the check box is current UNselected
									option.click();
								}
							}
							else{	//if the check box should NOT be selected
								if(option.isSelected()){ //if the check box is current selected
									option.click();
								}
							}
						}
						
						else if (formType.equals("radio")){
							//TODO: 
						}
						
						else if (formType.equals("select")){
							//TODO: 
						}
					}
				}
				
				boolean clicked = false;
				
				//Click based on the information in "id" field (xpath)
				String elementID = ((StandardAction)act).getId();
				WebElement eleToClick = findElementMatchToAction(driver, (StandardAction)act);
				
				//Click on found element
				if(eleToClick!=null){
					//check the conformance between the xpath ID and URL
					String newURL = null;
					newURL = getElementURL(driver, eleToClick);
					checkUpdateUrlMap(act, actionUrls, newURL);
					
					//Click on the found element
					try{
						eleToClick.click();
						clicked = true;
						System.out.println(" --> DONE");
					} catch(Throwable t){
						t.printStackTrace();
					}
				}
				
				//follow URL if cannot find the element to click
				if(!clicked){
					System.out.println("\n\t\t!!! NOT FOUND: " + elementID);
					if(!aURL.isEmpty()){
						System.out.print("\t\t--> access directly the element URL (" + aURL + ")");
						
						String urlToGet = aURL;

						//update urlToGet, if actionUrls contains the url of the action
						if(actionUrls.containsKey(act.getActionID())) {
							urlToGet = actionUrls.get(act.getActionID());
						}
						
						urlToGet = processUrlBeforeRequest(driver, urlToGet);
						
						if(urlToGet!=null) {
							driver.get(urlToGet);
							clicked = true;
							System.out.println(" --> DONE");
						}
						
					}
				}
				
				if(!clicked){
					System.out.println(" --> NOT DONE");
				}
				
				doneAction = true;
				
				//get redirect URL
				if(clicked){
					redirectURL = getRedirectUrl(driver, aURL);
				}
				else{
					redirectURL = "";
				}
				
				break;
			}
				
			case hover: 
			{
				//TODO: update this type of action in needed cases
				doneAction = true;
				System.out.println(" --> DONE");
				//get redirect URL
				redirectURL = getRedirectUrl(driver, aURL);
				break;
			}
				
			case randomClickOnNewElement: 
			{
				String prevDom = "";
				String currentDom = "";
				if(i>=2){
					prevDom = (String) ((WebOutputCleaned)outputSequence.getOutputAt(i-2)).originalHtml;
					currentDom = (String) ((WebOutputCleaned)outputSequence.getOutputAt(i-1)).originalHtml;
				}
				else if(i==1){
					currentDom = (String) ((WebOutputCleaned)outputSequence.getOutputAt(0)).originalHtml;
				}
				
				Elements newElements = getNewElements(prevDom, currentDom);
				
				if(newElements.size()>0){
					//randomly choose an element to click
					int randomIndex = ThreadLocalRandom.current().nextInt(0, newElements.size());
					Element executeEle = newElements.get(randomIndex);

					String tagName = executeEle.tagName().toLowerCase();
					By elementBy = null;
//					if(tagName.equals("a")){
//						if(executeEle.attributes().hasKey("href")){
//							 elementBy = getByType("linkText", executeEle.attr("href"));
//						}
//					}
//					else if (tagName.equals("button")){
//						if(executeEle.attributes().hasKey("id")){
//							 elementBy = getByType("id", executeEle.attr("id"));
//						}
//					}
					
					if(tagName.equals("a")){
						if(executeEle.attributes().hasKey("href")){
							String xpath = "//a[@href='" + executeEle.attr("href").trim() + "']";
							elementBy = getByType("xpath", xpath);
//							elementBy = getByType("linkText", executeEle.attr("href"));
						}
					}
					else if (tagName.equals("button")){
						if(executeEle.attributes().hasKey("id")){
							 elementBy = getByType("id", executeEle.attr("id"));
						}
						else if(executeEle.text()!=null &&
								!executeEle.text().isEmpty()){
							elementBy = getByType("linkText", executeEle.text());
						}
						
					}
					
					try{
						List<WebElement> eles = driver.findElements(elementBy);

						if(eles!=null && eles.size()>0){
							String elementURL = getElementURL(driver, executeEle);

							try {
								driver.findElement(elementBy).click();
							} catch ( Throwable t ){
								System.out.print("!!!Ignored (cannot click): "+elementURL);

							}
							aURL = elementURL;

							System.out.println(" --> DONE");
							//get redirect URL
							redirectURL = getRedirectUrl(driver, elementURL);
						}

						System.out.println("\t\t" + executeEle);
					}catch( Throwable t ){
						System.out.println("!!!Ignored (cannot find element): " + elementBy);
					}
					
				}
				doneAction = true;
				break;
			}
				
			case wait: 
			{
				try {
					Thread.sleep(((WaitAction)act).getMillis());
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				doneAction = true;
				System.out.println(" --> DONE");
				break;
			}
				
			default:
				break;
			}
			
			//Wait for loading page before executing next action
			try {
				Thread.sleep(sysConfig.getWaitTimeAfterAction());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			
			if(autoDetectConfirmation){
				boolean confirmed = false;
				
				//1. check if there is dialog
				while(isDialogPresent(driver)){
					Alert alert = driver.switchTo().alert();

					//always click on yes
					if(alwaysConfirm){
						alert.accept();
						System.out.print("\n\t\t!!Auto confirmed dialog!");
					}
					else{
						String alertText = alert.getText();
						
						if(containConfirmationText(alertText)){
							alert.accept();
							System.out.print("\n\t\t!!Confirmed dialog!");
						}
						else{
							alert.dismiss();
							System.out.print("\n\t\t!!Dismissed dialog!");
						}
					}
					confirmed = true;
				}
				
				//2. check if the current page contains confirmation request
				String newDom = driver.getPageSource();
				Element executeEle = confirmationButton(newDom);
				
				if(executeEle==null && containConfirmationText(newDom)){
					//FIXME: these following instructs should be reused from the case of randomClick
					
					String prevDom = "";
					String currentDom = newDom;
					if(i>=1){
						prevDom = (String) ((WebOutputCleaned)outputSequence.getOutputAt(i-1)).originalHtml;
					}
					
					Elements newElements = getNewElements(prevDom, currentDom);
					
					if(newElements.size()>0){
						
						//if we prioritize buttons and input_submits, filter newElements to get only buttons from there
						if(prioritizeButton){
							Elements onlyButtons = new Elements();
							
							for(int iEle=0; iEle<newElements.size(); iEle++){
								Element elem = newElements.get(iEle);
								if(elem.tagName().toLowerCase().equals("button") ||
										elem.tagName().toLowerCase().equals("input")){
									onlyButtons.add(elem);
								}
							}
							
							if(onlyButtons.size()>0){
								newElements = onlyButtons;
							}
						}
						
						//randomly choose an element to click
						int randomIndex = ThreadLocalRandom.current().nextInt(0, newElements.size());
						executeEle = newElements.get(randomIndex);
						
					}
				}
				
				if(executeEle!=null) {
//					executeEle.text();
					String tagName = executeEle.tagName().toLowerCase();
					By elementBy = null;
					if(tagName.equals("a")){
						if(executeEle.attributes().hasKey("href")){
							String xpath = "//a[@href='" + executeEle.attr("href").trim() + "']";
							elementBy = getByType("xpath", xpath);
							//								elementBy = getByType("linkText", executeEle.attr("href"));
						}
					}
					else if (tagName.equals("button")){
						if(executeEle.attributes().hasKey("id")){
							elementBy = getByType("id", executeEle.attr("id"));
						}
						else if(executeEle.text()!=null &&
								!executeEle.text().isEmpty()){
							elementBy = getByType("linkText", executeEle.text());
						}

					}
					if(tagName.equals("input")){
						if(executeEle.attributes().hasKey("type")){
							elementBy = getByType("tagName", "input");
						}
					}

					try{
						List<WebElement> eles = driver.findElements(elementBy);

						

						if(eles!=null && eles.size()>0){
							String beforeUrl = driver.getCurrentUrl();
							String elementURL = getElementURL(driver, executeEle);
							//								System.out.println("\t--Will click on: " + elementURL);

							for(WebElement e1:eles) {
								if(matchElement(e1, executeEle)) {
									if(e1.getTagName().equalsIgnoreCase("input") &&
											e1.getAttribute("type")!=null &&
											e1.getAttribute("type").equalsIgnoreCase("submit")) {
										e1.submit();;
									}
									else {
										e1.click();
									}
									confirmed = true;
									break;
								}
							}
							
							//Phu: just commented statements under (20/12/2019) to try another way to click on the element
//							try {
//								driver.findElement(elementBy).click();
//							} catch ( Throwable t ){
//								System.out.print("!!!Ignored (auto confirmation cannot click): "+elementURL);
//
//							}
//							confirmed = true;
							//end of commented
							
							aURL = elementURL;


							//get redirect URL
							redirectURL = getRedirectUrl(driver, beforeUrl, elementURL);
//							redirectURL = getRedirectUrl(driver, elementURL);
						}

						//							System.out.println("\t\t" + executeEle);
					}catch( Throwable t ){
						System.out.println("!!!Ignored (auto confirmation cannot find element): " + elementBy);
					}
				}

				
				if(confirmed){
					timeOfConfirm++;
					System.out.println("\t\t!! automatically confirm --> DONE");
					try {
						Thread.sleep(sysConfig.getWaitTimeAfterAction());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
				
			//Add result to the outputSequence
			if(doneAction){
				latestUrl = driver.getCurrentUrl();
//				if(redirectURL!=null && !redirectURL.isEmpty()) {
//					System.out.println("\t\t!!! Redirect URL: " + redirectURL);
//				}
				
				//Execute inner actions if they exist
				executeInnerActions(driver, act);
				
				//get new dom
				String newDom = "";
				
				try{
					if(this.isDialogPresent(driver)){
						//get alert text
						Alert alert = driver.switchTo().alert();
						newDom = alert.getText();
					}
					else{
						newDom = driver.getPageSource();
					}
				} catch(Throwable t){
					t.printStackTrace();
				}
				
				if(newDom==null){
					newDom = "";
				}
				
				
				//normalize the new dom
				WebOutputCleaned outObj = cleanUpOutPut(newDom);
				outObj.resultedUrl = driver.getCurrentUrl();
				
				if(checkStatusCode) {
					outObj.statusCode = getStatusCode(driver);
				}
				
				

				File file = findNewDownloadedFile();
				if ( file != null ){
					String folderName = "OUTPUT_FILE_"+System.currentTimeMillis();
					File outFolder = new File(outputFolder());
					
					File outFolderFile = new File( outFolder, folderName);
					outFolderFile.mkdir();
					
					File newFile = new File( outFolderFile, file.getName() );
					file.renameTo(newFile );
					outObj.setDownloadedFile( newFile );
				}
				
				//get list of relevant downloaded objects
				if(checkDownloadedObjects){
					outObj.downloadedObjects = getDownloadedObjectsFromProxy(maxId, aURL, redirectURL);
				}
				else{
					outObj.downloadedObjects = null;
				}
				
				outputSequence.add(outObj);
				outputSequence.addRedirectURL(redirectURL);
				
				//get cookie
				CookieSession currentSession = null;
				if(!isDialogPresent(driver)){
					currentSession = new CookieSession(driver.manage().getCookies());
				}
				else{
					CookieSession lastSession = (CookieSession)outputSequence.getSession();
					if(lastSession!=null){
						currentSession = lastSession;
					}
					else{
						currentSession = new CookieSession();
					}
				}
				outputSequence.addSession(currentSession);
				
				String inputID = input.getId();
				String executionId = null;
				
				try{
					executionId = MR.CURRENT.getCurrentExecutionId();
				} catch(NullPointerException e){
//					e.printStackTrace();
				}
				
				if(executionId==null){
					executionId = "";
				}
				
				String fileName = executionId+"_"+inputID+"_" + text + "_" + aURL;
				saveDomToFile(outObj.html, fileName);
				
				fileName = executionId+"_"+inputID+"_" + text + "_text_" + aURL;
				saveDomToFile(outObj.text, fileName);
			}
			
			//clear all replacer rule in the proxy
			if(!ruleDescription.isEmpty()){
				try {
					proxyApi.replacer.removeRule(ruleDescription);
					ruleDescription = "";
				} catch (ClientApiException e) {
					e.printStackTrace();
				}
			}
			
			//Update URLs of following actions (if needed)
			updateUrlsForNextActions(driver, act, input);
		}
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.close();
		
		System.out.println("\tTimes of automatic confirmation: "+timeOfConfirm);
		
		return outputSequence;
	}


	


	private boolean matchElement(WebElement webElement, Element jsoupElement) {
		if(webElement==null ||
				jsoupElement==null ||
				!webElement.getTagName().equalsIgnoreCase(jsoupElement.tagName())) {
			return false;
		}
		
		Attributes allAttr = jsoupElement.attributes();
		for(Attribute attr:allAttr) {
			if(webElement.getAttribute(attr.getKey())==null ||
					!webElement.getAttribute(attr.getKey()).equals(attr.getValue())) {
				return false;
			}
		}

		return true;
	}


	private int getStatusCode(ChromeDriver driver) {
		int status = -1;
		LogEntries logs = driver.manage().logs().get("performance");
//				System.out.println("Per logs: " + logs);

		for (Iterator<LogEntry> it = logs.iterator(); it.hasNext();)
		{
		    LogEntry entry = it.next();

		    try
		    {
		        JSONObject json = new JSONObject(entry.getMessage());

//	                    System.out.println(json.toString());

		        JSONObject message = json.getJSONObject("message");
		        String method = message.getString("method");

		        if (method != null
		                && "Network.responseReceived".equals(method))
		        {
		            JSONObject params = message.getJSONObject("params");

		            JSONObject response = params.getJSONObject("response");
		            String messageUrl = response.getString("url");

		            if (driver.getCurrentUrl().equals(messageUrl))
		            {
		                status = response.getInt("status");
		                break;
		            }
		        }
		    } catch (JSONException e)
		    {
		    	System.out.println("To use this function of ");
		        e.printStackTrace();
		    }
		}

//		System.out.println("\tstatus code: " + status);
		return status;
	}
	
	private String processUrlBeforeRequest(ChromeDriver driver, String urlToGet) {
		if(urlToGet==null) {
			return "";
		}
		
		String tempUrl = driver.getCurrentUrl();
		
		if(tempUrl!=null && !tempUrl.isEmpty()) {
			try {
				URI uri = new URI(tempUrl);
				return uri.resolve(urlToGet).toString();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}


	private boolean containConfirmationText(String text) {
		List<String> conTexts = sysConfig.getConfirmationTexts();
		if(conTexts==null || conTexts.size()<1) {
			return false;
		}
		for(int iText=0; iText<conTexts.size(); iText++){
			if(text.indexOf(conTexts.get(iText)) >0){
				return true;
			}
		}
		return false;
	}
	
	private boolean containConfirmationButton(String dom) {
		//If the current dom is empty, there is no button 
		if (isEmptyHtml(dom) || 
				sysConfig==null ||
				sysConfig.getConfirmationButtons()==null ||
				sysConfig.getConfirmationTexts().size()<=0){
			return false;
		}

		//currentDOM is not null
		Document currentDoc = Jsoup.parse(dom);
		
		//Get all elements of tags "button"
		Elements buttonElements = currentDoc.getElementsByTag("button");
		if(buttonElements.size()<=0) {
			return false;
		}
		
		for(Element button:buttonElements) {
			String text = button.text();
			if(text==null || text.trim().isEmpty()) {
				continue; 
			}
			for(String conButton:sysConfig.getConfirmationButtons()) {
				if(text.contains(conButton)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private Element confirmationButton(String dom) {
		//If the current dom is empty, there is no button 
		if (isEmptyHtml(dom) || 
				sysConfig==null ||
				sysConfig.getConfirmationButtons()==null ||
				sysConfig.getConfirmationTexts().size()<=0){
			return null;
		}

		//currentDOM is not null
		Document currentDoc = Jsoup.parse(dom);
		
		//Get all elements of tags "button"
		Elements buttonElements = currentDoc.getElementsByTag("button");
		if(buttonElements.size()<=0) {
			return null;
		}
		
		for(Element button:buttonElements) {
			String text = button.text();
			if(text==null || text.trim().isEmpty()) {
				continue; 
			}
			for(String conButton:sysConfig.getConfirmationButtons()) {
				if(text.contains(conButton)) {
					return button;
				}
			}
		}
		
		return null;
	}

	private HashMap<String, String> getDownloadedObjectsFromProxy(int startID, String lastURL, String redirectURL) {
		HashMap<String,String> res = new HashMap<String,String>();	//url, method
		
		if(lastURL==null || lastURL.isEmpty()){
			return res;
		}
		
		try {
			ApiResponseList allMsg = (ApiResponseList) this.proxyApi.core.messages(null, null, null);
			
			if(allMsg==null || allMsg.getItems().size()<=0){
				return res;
			}
			
			for(ApiResponse msg:allMsg.getItems()){
				ApiResponseSet msgSet = (ApiResponseSet)msg;
				
				String headers = ((ApiResponseElement) msgSet.getValue("requestHeader")).getValue();
				int id = Integer.parseInt(msgSet.getValue("id").toString());
				String url = URLUtil.standardUrl(getURLFromRequestHeaders(headers));
				String method = getHttpMethodFromRequestHeaders(headers).toLowerCase();
				String responseBody = ((ApiResponseElement) msgSet.getValue("responseBody")).getValue();
				
				
				if(id > startID &&
						!URLUtil.hasTheSameUrl(url, lastURL) &&
						(redirectURL.isEmpty() ||
								(!redirectURL.isEmpty() && 
								!URLUtil.hasTheSameUrl(url, redirectURL)) ) &&
						!res.keySet().contains(url) &&
						isNotIgnoredURL(url) &&
						responseBody!=null && !responseBody.isEmpty()
						){
					res.put(url, method);
				}
			}
			
		} catch (ClientApiException e) {
			e.printStackTrace();
		}
		
		return res;
	}

	private boolean isNotIgnoredURL(String url) {
		for(int i=0; i<ignoredObjects.length; i++){
			if(url.toLowerCase().endsWith(ignoredObjects[i])){
				return false;
			}
		}
		return true;
	}

	private String getURLFromRequestHeaders(String headers) {
		if(headers ==null || headers.isEmpty()){
			return null;
		}
		String requestLine = headers.split("\n")[0]; 
		String url = requestLine.split(" ")[1];
		return url;
	}
	
	private String getHttpMethodFromRequestHeaders(String headers) {
		if(headers ==null || headers.isEmpty()){
			return null;
		}
		String requestLine = headers.split("\n")[0]; 
		String method = requestLine.split(" ")[0];
		return method;
	}

	private int getMaxIdFromProxy() {
		if(sysConfig.isUsedProxy()){
			try {
				ApiResponseList msgList = (ApiResponseList) this.proxyApi.core.messages(null, null, null);
				int listSize = msgList.getItems().size();
				
				if(listSize<1){
					return 0;
				}
				
				ApiResponseSet lastMsg = (ApiResponseSet) msgList.getItems().get(listSize-1);
				listSize--;
				
				while(lastMsg==null && listSize>0){
					lastMsg = (ApiResponseSet) msgList.getItems().get(listSize-1);
					listSize--;
				}
				
				if(lastMsg!=null){
					return Integer.parseInt(lastMsg.getValue("id").toString());
				}
			} catch (ClientApiException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	private WebElement findElementMatchToAction(RemoteWebDriver driver, StandardAction act) {
		WebElement eleResult = null;
		
		String elementID = act.getId().trim();
		if(elementID.split(" ").length !=2){
			System.out.print("\t\t!!! Cannot get id for the action: " + act.getText());
		}
		else if(elementID.split(" ").length ==2){
			String elementHow = elementID.split(" ")[0].toLowerCase();
			elementID = elementID.split(" ")[1];
			By elementBy = getByType(elementHow, elementID);

			List<WebElement> elements = driver.findElements(elementBy);
			if(elements!=null && !elements.isEmpty()){
				eleResult = driver.findElement(elementBy); 
			}
		}
		
		//Click based on the information in "element" field
		if(eleResult==null) {
			//Try to find element by using information of "element" field in inputs
			String elementInfo = act.getElement();
			String id = getInfoFromElement(elementInfo, "id");
			if(id!=null && !id.isEmpty()){
				try{
					eleResult = driver.findElementById(id);
				}
				catch(NoSuchElementException e){
					return null;
				}
			}
			
			//if eleToClick is still null, try to find element based on element text
//			if(eleResult==null){
//				//if did not click, try to find an element by text
//				String textID = getInfoFromElement(elementInfo, "text");
//				if(textID!=null && !textID.isEmpty()){
//					eleResult = driver.findElementByPartialLinkText(textID);
//				}
//			}
		}
		return eleResult;
	}

	/**
	 * @param driver web driver, which is running and showing the result after executing currentAction
	 * @param currentAction 
	 * @param input sequence of actions, which contains the currentAction
	 */
	private void updateUrlsForNextActions(RemoteWebDriver driver, Action currentAction, WebInputCrawlJax input) {
//		System.out.println("\t  -- In updateUrlsForNextActions");
		if(currentAction==null ||
				currentAction.getActionID()==null ||
				input==null ||
				input.actions()==null ||
				!updateUrlMap.containsKey(currentAction.getActionID())){
			return;
		}
		
		//Get action to change its URL
		Long actToChangeID = updateUrlMap.get(currentAction.getActionID());
		Action actToChange = null;
		
		ArrayList<Action> listActToChange = new ArrayList<Action>();
		for(Action a:input.actions()){
			if(a.getActionID().equals(actToChangeID)){
				listActToChange.add(a);
			}
		}
		
		if(listActToChange.isEmpty()) {
			return;
		}
		
		for(Action act:listActToChange) {
			if(act==null ||
					!act.getEventType().equals(Action.ActionType.click)){
				return;
			}
			
			if(act.getUrl()==null || act.getUrl().trim().isEmpty()){
				return;
			}
			
			WebElement eleToFind = findElementMatchToAction(driver, (StandardAction)act);
			System.out.println("\t  -- element to change: " + eleToFind);
			
			//if cannot find the relevant element, do nothing
			if(eleToFind==null){
				return;
			}
			
			//get URL of the eleToFind
			String newURL = getElementURL(driver, eleToFind);
			System.out.println("\t  ** New URL: " + newURL);
			
			//update the URL of the actToChange, if it changed
			if(newURL!=null &&
					!newURL.equals(act.getUrl())){
				act.updateUrl(newURL);
			}
		}
		
		
//		for(Action a:input.actions()){
//			if(a.getActionID().equals(actToChangeID)){
//				actToChange = a;
//				break;
//			}
//		}
//		
//		if(actToChange==null ||
//				!actToChange.getEventType().equals(Action.ActionType.click)){
//			return;
//		}
//		
//		if(actToChange.getUrl()==null || actToChange.getUrl().trim().isEmpty()){
//			return;
//		}
//		
//		WebElement eleToFind = findElementMatchToAction(driver, (StandardAction)actToChange);
//		System.out.println("\t  -- element to change: " + eleToFind);
//		
//		//if cannot find the relevant element, do nothing
//		if(eleToFind==null){
//			return;
//		}
		
//		//get URL of the eleToFind
//		String newURL = getElementURL(driver, eleToFind);
//		System.out.println("\t  ** New URL: " + newURL);
//		
//		//update the URL of the actToChange, if it changed
//		if(newURL!=null &&
//				!newURL.equals(actToChange.getUrl())){
//			actToChange.updateUrl(newURL);
//		}
	}

	private String getElementURL(WebDriver driver, Element element) {
		if(element==null){
			return null;
		}
		
		String res = "";
	
		//If the element (a, link tag) has the attribute "href"
		if(element.attributes().hasKey("href")){
			 res = element.attr("href").trim();
			 return res;
		}
		
		if(element.attributes().hasKey("action")){
			String act = element.attr("action").trim();
			res = getCombinedURL(driver, act);
			return res;
		}
		
		String tagName = element.tagName().toLowerCase();

		//If the element is a button
		Element currentE = null;
		String source = driver.getPageSource();
		Document currentDoc = Jsoup.parse(source);
		if (tagName.equals("button")){
			if(element.attributes().hasKey("id")){
				String buttonId = element.attr("id");
				currentE = currentDoc.getElementById(buttonId);
			}
		}
		//other kinds of element
		else if (tagName.equals("input")){
			//TODO
		}
		
		if(currentE!=null){
			res = getUrlFromFormTag(driver, currentE);
		}
		
		return res;
	}

	private String getElementURL(WebDriver driver, WebElement webElement) {
		if(webElement==null || driver==null){
			return null;
		}
		
		String res = null;
		
		//If the element (a, link tag) has the attribute "href"
		if(webElement.getAttribute("href") != null){
			res = webElement.getAttribute("href").trim();
			return res;
		}

		//If the element has the attribute "action"
		if(webElement.getAttribute("action") != null){
			String act = webElement.getAttribute("action").trim();
			res = getCombinedURL(driver, act);
			return res;
		}
		
		if(isDialogPresent(driver)){
			return null;
		}
		
		String tagName = webElement.getTagName().toLowerCase();
		Element currentE = null;
		String source = driver.getPageSource();
		Document currentDoc = Jsoup.parse(source);

		//If the element is a button
		if (tagName.equals("button")){
			if(webElement.getAttribute("id")!=null && !webElement.getAttribute("id").isEmpty()){
				String buttonId = webElement.getAttribute("id");
				currentE = currentDoc.getElementById(buttonId);
			}
			else if(webElement.getText()!=null &&
					!webElement.getText().isEmpty()){
				Elements eles = currentDoc.getElementsMatchingText(webElement.getText());
				if(eles!=null){
					for(Element e:eles){
						if(e.tagName().equals("button")){
							currentE = e;
							break;
						}
					}
				}
			}
		}
		//if the element is an input tag with type of submit/button
		else if (tagName.equals("input") &&
				webElement.getAttribute("value")!=null &&
				webElement.getAttribute("type")!=null &&
				(webElement.getAttribute("type").equals("submit") ||
						webElement.getAttribute("type").equals("button"))){
			String eValue = webElement.getAttribute("value");
			
			Elements possibleElements = null;
			if(webElement.getAttribute("type").equals("submit")){
				possibleElements = currentDoc.getElementsByAttributeValue("type", "submit");
			}
			else if(webElement.getAttribute("type").equals("button")){
				possibleElements = currentDoc.getElementsByAttributeValue("type", "button");
			}
			
			if(possibleElements!=null){
				for(Element e:possibleElements){
					if(e.tagName().equals("input") &&
							e.hasAttr("value")){
						Attributes attrs = e.attributes();
						boolean rightElement = false;
						for(Attribute a:attrs){
							if(a.getKey().equals("value") &&
									a.getValue().equals(eValue)){
								rightElement = true;
								break;
							}
						}
						if(rightElement){
							currentE = e;
							break;
						}
					}
				}
			}
		}
		
		if(currentE!=null){
			res = getUrlFromFormTag(driver, currentE);
		}
		
		return res;
	}

	/**
	 * @param driver
	 * @param nestedElement
	 * @return
	 */
	private String getUrlFromFormTag(WebDriver driver, Element nestedElement) {
		String res;
		String actionString = "";

		Element parent = nestedElement.parent();
		while(!parent.tagName().toLowerCase().equals("html")){
			if(!parent.tagName().toLowerCase().equals("form")){
				parent = parent.parent();
			}
			else{
				if(parent.attributes().hasKey("action")){
					actionString = parent.attr("action");
				}
				break;
			}
		}
		res = getCombinedURL(driver, actionString);
		return res;
	}

	/**
	 * @param act
	 * @param newURL
	 */
	private void checkUpdateUrlMap(Action act, HashMap<Long, String> actionUrls, String newURL) {
		String actURL = act.getUrl();
		if(newURL!=null && actURL!=null && 
				!actURL.isEmpty() &&
				!newURL.equals(actURL)){
			//1.get previous action
			Long prevActID = act.getPreviousActionID();
			//2. add item in updateUrlMap
			//add only item with new value
			//e.g., if there exist already (a, b), do not add (c,b)
			if(prevActID!=null &&
					!updateUrlMap.containsValue(act.getActionID())){
				//Update updateUrlMap
				updateUrlMap.put(prevActID, act.getActionID());
				
				//Update actionUrls hashmap
				actionUrls.put(act.getActionID(), newURL);
				
				System.out.println("\t  updateUrlMap: " + updateUrlMap);
			}
		}
	}
	
	private void executeInnerActions(RemoteWebDriver driver, Action act) {
		if(act==null ||
				act.getInnerActions()==null ||
				act.getInnerActions().size()==0){
			return;
		}
		for(InnerAction iAct:act.getInnerActions()){
			closeAlertAndGetItsText(driver, ((AlertAction)iAct).getAccept());
		}
	}

	private String closeAlertAndGetItsText(RemoteWebDriver driver, Boolean acceptAlert) {
		if(isDialogPresent(driver)){
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		}
		return "";
	  }

	public static String getInfoFromElement(String elementInfo, String field) {
		//Example of elementInfo: "Element{node=[BUTTON: null], tag=BUTTON, text=log in, attributes={atusa=id1232306490_0, id=yui-gen1-button, tabindex=0, type=button}}"
		String res = null;
		if(elementInfo==null || field==null || elementInfo.isEmpty() || field.isEmpty()){
			return res;
		}
		
		String regex = "([\\s,\\{]" + field + "=)([^\\s,\\}]*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(elementInfo);
		
		if(matcher.find()){
			res = matcher.group(2);
		}
		
		return res;
	}

	private String getFormInputValueFromParamName(JsonArray formInput, String parameter) {
		String res = null;
		
		for(int iForm=0; iForm<formInput.size(); iForm++){
			JsonObject fInput = formInput.get(iForm).getAsJsonObject();
			String idValue = fInput.get("identification").getAsJsonObject().get("value").getAsString();
			if(idValue==null ||
					(!idValue.equals(parameter))||
					fInput.get("values").getAsJsonArray().size() <1){
				continue;
			}
			
			String formType = fInput.get("type").getAsString().toLowerCase();
			if(formType.startsWith("text") 
					|| formType.equals("password") 
					|| formType.equals("hidden")){
				JsonArray values = fInput.get("values").getAsJsonArray();
				if(values.size() >0){
					res = values.get(0).getAsString().trim();
					break;
				}
			}
		}
		
		return res;
	}

	private String outputFolder() {
		File f = new File(sysConfig.getOutputFile());
		String res = f.getParent();
		if(!res.endsWith(File.separator)){
			res += File.separator;
		}
		return res;
	}

	private File findNewDownloadedFile() {
		//assume Download is empty
		File downloads = new File(this.downloadFilePath);
		
//		System.out.println("Download folder: " + downloads.getAbsolutePath());
		
		File[] filesD = downloads.listFiles();
		if ( filesD.length == 0 ) {
//			System.out.println("No file (findNewDownloadedFile)");
			return null;
		}
//		System.out.println("HAVE file (findNewDownloadedFile) " + filesD[0]);
		return filesD[0];
	}

	private void saveDomToFile(String dom, String fileName) {
		String fullFileName = this.getOutputFile().getParent();
		if(fullFileName.endsWith("/")){
			fullFileName += "doms/";
		}
		else{
			fullFileName += "/doms/";
		}
		
		String onlyFileName = fileName;
		if(onlyFileName.endsWith("/")){
			onlyFileName = onlyFileName.substring(0, onlyFileName.length()-1);
		}
		onlyFileName = onlyFileName.replaceAll("/", "_").replaceAll(":", "");
		
		fullFileName += onlyFileName;
		
		int i=0;
		File tmpFile = null;
		
		do{
			String name = fullFileName + "_" + i + ".html";
			tmpFile = new File(name);
			i++;
		}while(tmpFile.exists());
		
		fullFileName += "_" + (i-1) + ".html";
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fullFileName, false))) {
			bw.write(dom);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	

	private String getCombinedURL(WebDriver driver, String action) {
		if(action.trim().contains("://")){
			return action.trim();
		}
		String res = driver.getCurrentUrl();
		
		String newAct = action.trim();
		if(newAct.isEmpty()){
			return res;
		}
		
		try {
			URI oldUri = new URI(res);
			URI resolved = oldUri.resolve(newAct);
			res = resolved.toString();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		return res;
	}

	
	boolean isDialogPresent(WebDriver driver){
		Alert alert = ExpectedConditions.alertIsPresent().apply(driver);
		return (alert!=null);
	}
	
	/**
	 * @param driver
	 * @param requestedURL
	 */
	private String getRedirectUrl(WebDriver driver, String requestedURL) {
		String res="";

//		Set<String> h = driver.getWindowHandles();
		//For the case there is an alert on the browser
//		Alert alert = driver.switchTo().alert();

		if(isDialogPresent(driver) || requestedURL==null){
			return res;
		}
		
		String currentURL = driver.getCurrentUrl().trim();
		while(currentURL.endsWith("/")) 
		{
			currentURL = currentURL.substring(0, currentURL.length()-1);
		}
		
		String _requestedURL = requestedURL.trim();
		while(_requestedURL.endsWith("/"))
		{
			_requestedURL = _requestedURL.substring(0, _requestedURL.length()-1);
		}
		
		
		if(currentURL.equals(_requestedURL)){
			res = "";
		}
		else{
			res = currentURL;
		}
		return res;
	}
	
	private String getRedirectUrl(ChromeDriver driver, String beforeUrl, String requestedURL) {
		String tempRedirectUrl = getRedirectUrl(driver, requestedURL);
		if(tempRedirectUrl!=null && !tempRedirectUrl.isEmpty() &&
				!beforeUrl.trim().equalsIgnoreCase(tempRedirectUrl)) {
			return tempRedirectUrl;
		}
		return "";
	}

	public void loadUsers(String accFile) throws IOException {
		// Assume that there is a file containing information of accounts
		// (usernameParam & passwordParam)
		// Get account params
		File f = new File(accFile);
		if (!f.exists()) {
			System.out.println("File not found: " + accFile);
			return;
		}
		List<String> userParams = new ArrayList<String>();
		List<String> passParams = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		while (line != null) {
			String[] parts = line.trim().split(" ");
			if (parts.length == 2) {
				userParams.add(parts[0]);
				passParams.add(parts[1]);
			}
			line = br.readLine();
		}
		br.close();

		if (userParams.size() < 1) {
			System.out.println("There is no information of account parameters");
			return;
		}
		
		// visit inputs to get accounts
		for(WebInputCrawlJax input:this.inputList){
			if(input.size()>0){
				for(Action act:input.actions()){
					//try to find an account from each action
					for(int i=0; i<userParams.size(); i++){
						String uParam = userParams.get(i);
						String pParam = passParams.get(i);
						
						if(act.containCredential(uParam, pParam)){
							//this means that the act is an instance of StandardAction
							Account acc = ((StandardAction)act).getCredential(uParam, pParam);
							if(acc!=null){
								//if the current userList does not contain acc, add it to the list
								boolean contain = false;
								for(Account a: this.userList){
									if(a.getUsernameParam().equals(acc.getUsernameParam()) 
											&& a.getUsername().equals(acc.getUsername())
											&& a.getPasswordParam().equals(acc.getPasswordParam())
											&& a.getPassword().equals(acc.getPassword())){
										contain = true;
										break;
									}
								}
								if(!contain){
									this.userList.add(acc);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Load users based on account parameters defining in sysConfig
	 */
	@SuppressWarnings("static-access")
	public void loadUsers() {
		if(!this.sysConfig.hasAccountParameters()){
			return;
		}
//		String userParam = this.sysConfig.getUserParameter();
//		String passParam = this.sysConfig.getPasswordParameter();
		ArrayList<LoginParam> allLoginParams = WebProcessor.sysConfig.getLoginParams();
		
		for(WebInputCrawlJax input:this.inputList){
			if(input.size()>0){
				for(Action act:input.actions()){
					LoginParam usedLoginParam = null;
					if(act instanceof StandardAction) {
						usedLoginParam = ((StandardAction)act).usedLoginParam(allLoginParams);
					}
					
					//try to find an account from each action
//					if(act.containCredential(userParam, passParam)){
					if(usedLoginParam!=null) {
						//this means that the act is an instance of StandardAction
//						Account acc = ((StandardAction)act).getCredential(userParam, passParam);
						Account acc = ((StandardAction)act).getCredential(usedLoginParam.userParam, usedLoginParam.passwordParam);
						if(acc!=null){
							//if the current userList does not contain acc, add it to the list
							boolean contain = false;
							for(Account a: this.userList){
								if(a.getUsernameParam().equals(acc.getUsernameParam()) 
										&& a.getUsername().equals(acc.getUsername())
										&& a.getPasswordParam().equals(acc.getPasswordParam())
										&& a.getPassword().equals(acc.getPassword())){
									contain = true;
									break;
								}
							}
							if(!contain){
								this.userList.add(acc);
							}
						}
					}
				}
			}
		}
		
	}
	
	private By getByType(String how, String id){
		By elementBy = null;
		if(how.equals("id")){
			elementBy = By.id(id);
		}
		else if(how.equals("name")){
			elementBy = By.name(id);
		}
		else if(how.equals("xpath")){
			elementBy = By.xpath(id);
		}
		else if(how.equals("linkText")){
			elementBy = By.linkText(id);
		}
		else if(how.equals("partialLinkText")){
			elementBy = By.partialLinkText(id);
		}
		else if(how.equals("tagName")){
			elementBy = By.tagName(id);
		}
		
		return elementBy;
	}
	
	private Elements getNewElements(String prevDom, String currentDom) {
		Elements newElements = new Elements();
		
		//If the current dom is empty, there is no element 
		if (isEmptyHtml(currentDom)){
			return newElements;
		}
		
		//currentDOM is not null
		Document currentDoc = Jsoup.parse(currentDom);
		
		//Get all elements of tags "a" and "button"
		Elements currentElements = currentDoc.getElementsByTag("a");
		currentElements.addAll(currentDoc.getElementsByTag("button"));
		
		Elements currentInputElements = getSubmitInputElements(currentDoc);
		if(currentInputElements!=null && currentInputElements.size()>0) {
			currentElements.addAll(currentInputElements);
		}
		//currentElements.addAll(currentDoc.getElementsByTag("form"));
		
		//If the previous dom is empty, of the current dom has no element
		if(isEmptyHtml(prevDom) || currentElements.size()<1){
			return currentElements;
		}
		
		//In the case both prevDom and currentDom are not empty
		Document prevDoc = Jsoup.parse(prevDom);
		Elements prevElements = prevDoc.getElementsByTag("a");
		prevElements.addAll(prevDoc.getElementsByTag("button"));
		
		Elements prevInputElements = getSubmitInputElements(prevDoc);
		if(prevInputElements!=null && prevInputElements.size()>0) {
			prevElements.addAll(prevInputElements);
		}
		//prevElements.addAll(prevDoc.getElementsByTag("form"));

		for(Element cElement:currentElements){
			String cTagName = cElement.tagName().toLowerCase();
			boolean contain = false;
			//anchor tag
			if(cTagName.toLowerCase().equals("a")){
				//find if any tag "a" in the previous dom has the same url (href)
				for(Element pElement:prevElements){
					if(pElement.tagName().toLowerCase().equals("a")){
						if(cElement.attributes().hasKey("href") &&
								pElement.attributes().hasKey("href")){
							String pUrl = pElement.attr("href").trim();
							String cUrl = cElement.attr("href").trim();

							if((!cUrl.isEmpty()) && cUrl.equals(pUrl)){
								contain = true;
								break;
							}
						}
					}
				}
				if(cElement.attributes().hasKey("href") && (!contain)){
					newElements.add(cElement);
				}
			}
			//button tag
			else if (cTagName.toLowerCase().equals("button")){
				//find if any tag "button" in the previous dom has the same id
				for(Element pElement:prevElements){
					if(pElement.tagName().toLowerCase().equals("button")){
						if(cElement.attributes().hasKey("id") &&
								pElement.attributes().hasKey("id")){
							String pId = pElement.attr("id").trim();
							String cId = cElement.attr("id").trim();

							if((!cId.isEmpty()) && cId.equals(pId)){
								contain = true;
								break;
							}
						}
					}
				}
				
				if(cElement.attributes().hasKey("id") && (!contain)){
					newElements.add(cElement);
				}
			}
			//input submit tag
			else if (cTagName.toLowerCase().equals("input")){
				//find if any tag "input" in the previous dom has the same id
				for(Element pElement:prevElements){
					if(pElement.tagName().toLowerCase().equals("input")){
						if(cElement.attributes().hasKey("value") &&
								pElement.attributes().hasKey("value")){
							String pValue = pElement.attr("value").trim();
							String cValue = cElement.attr("value").trim();

							if((!cValue.isEmpty()) && cValue.equals(pValue)){
								contain = true;
								break;
							}
						}
					}
				}
				
				if(cElement.attributes().hasKey("value") && (!contain)){
					newElements.add(cElement);
				}
			}
		}
		
		return newElements;
	}


	private Elements getSubmitInputElements(Document document) {
		Elements result = new Elements();
		Elements inputElements = document.getElementsByTag("input");
		if(inputElements!=null && inputElements.size()>0) {
			for(Element ie:inputElements) {
				if(ie.attr("type")!=null && ie.attr("type").equals("submit")) {
					result.add(ie);
				}
			}
		}
		return result;
	}
	
	private boolean isEmptyHtml(String dom){
		if(dom.isEmpty()){
			return true;
		}
		
		String str = dom;
		str = str.replace("<html>", "")
				.replace("</html>", "")
				.replace("<head>", "")
				.replace("</head>", "")
				.replace("<body>", "")
				.replace("</body>", "").trim();
		if(str.isEmpty()){
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public void setConfig(String configFile) {
		this.sysConfig = new SystemConfig(configFile);
		this.configProxy();
		
//		configDownloadFolder(sysConfig.getOutputFile());
		configDownloadFolder(outputFolder());
		
	}



	/**
	 * 
	 */
	private void configDownloadFolder(String path) {
		//Config download folder
		String filepath = path;
		
		//Get filePath from the parameter path
		if(filepath!=null && !filepath.isEmpty()){
			File fileOfPath = new File(filepath);
			if(fileOfPath.isFile()) {
				filepath = filepath.substring(0, filepath.lastIndexOf(File.separator));
			}
			
			if(filepath.endsWith(File.separator)){
				filepath += "Downloads";
			}
			else{
				filepath += File.separator + "Downloads";
			}
		}
		//Get filePath of outputFolder (if having)
		else{
//			filepath = "./Downloads";
			filepath = outputFolder();
			if(filepath==null || filepath.isEmpty()) {
				filepath = "Downloads";
			}
		}
		
		File f = new File(filepath);
		
		
		this.downloadFilePath = f.getAbsolutePath();
		
		//Create download folder
		File downloadFolder = new File(filepath);
		if(!downloadFolder.exists()){
			downloadFolder.mkdirs();
		}
	}

	public static boolean isLogin(Action action) {
		String url = action.getUrl();
		if(url==null || url.isEmpty()){
			return false;
		}
		if(action.getMethod().toLowerCase().equals("post")){
			boolean possibleLogin = sysConfig.isLoginURL(url);
			
			if ( possibleLogin ) {
				Account user = (Account) action.getUser();
				//FIXME: need to check the account parameters corresponding 
				//to the login url (in a LoginParam object) 
				if ( user.getUsername().equals(WRONG_USERNAME) ) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean notVisibleWithoutLoggingIn(String url) {		
		if(url==null || url.trim().isEmpty()) {
			return false;
		}
		
		if ( visibleWithoutLogin == null ) {
			visibleWithoutLogin = new HashSet<String>();
			if(this.inputList!=null && this.inputList.size()>0) {
				for(WebInputCrawlJax input:this.inputList){
					for(Action act : input.actions()){
						if(this.isLogin(act)){
							break;
						}
						if(act.getUrl()!=null) {
							visibleWithoutLogin.add(act.getUrl().trim());
						}
					}
				}
			}
		}
		
		boolean contain = false;
		
		for(String vUrl: visibleWithoutLogin) {
			if(SystemConfig.equalURL(vUrl, url.trim())) {
				contain = true;
				break;
			}
		}

		return !contain;
	}

	@SuppressWarnings("static-access")
	public void configProxy() {
		if(this.sysConfig.isUsedProxy()){
			this.proxyApi = new ClientApi(this.sysConfig.getProxyAddress(), 
					this.sysConfig.getProxyPort(), this.sysConfig.getProxyApiKey());
		}
		
	}
	
	private WebOutputCleaned cleanUpOutPut(String page) {
		Document doc = Jsoup.parse(page);
		
		WebOutputCleaned out = new WebOutputCleaned();
		out.originalHtml = doc.toString();
		
		if(this.cleanUpDom){
			@SuppressWarnings("static-access")
			JsonObject jsonCleanUp = this.sysConfig.getCleanUp();

			//clean up SCRIPTS
			if(jsonCleanUp.keySet().contains("script") ){
				if ( jsonCleanUp.getAsJsonArray("script").size()>0){

					JsonArray scriptArray = jsonCleanUp.getAsJsonArray("script");
					if (scriptArray.size() > 0) {
						Elements eScript = doc.getElementsByTag("script");


						//Standardize attributes
						for(Element ele:eScript){
							for(Attribute att:ele.attributes()){
								for (int k = 0; k < scriptArray.size(); k++) {
									String expr = scriptArray.get(k).getAsString();
									if(att.getKey().equals(expr)){
										att.setValue("standadized");
									}
								}
							}
						}

						//Standardize data
						for (int i = 0; i < eScript.size(); i++) {
							List<org.jsoup.nodes.Node> tempChildren = eScript.get(i).childNodes();
							for (int j = 0; j < tempChildren.size(); j++) {
								Node node = tempChildren.get(j);
								for (int k = 0; k < scriptArray.size(); k++) {
									String contain = scriptArray.get(k).getAsString();
									if (node.attributes().get("data").contains(contain)) {
										node.attr("data", "standadized");
										break;
									}
								}
							}
						}
					}
				} else{
					//delete all scripts
					for(Element ele:doc.getElementsByTag("script")){
						ele.remove();
					}
				}
			}

			//Clean up CLASSES
			if(jsonCleanUp.keySet().contains("class") ){
				if ( jsonCleanUp.getAsJsonArray("class").size()>0){

					JsonArray classArray = jsonCleanUp.getAsJsonArray("class");
					for(Element ele:doc.getElementsByAttribute("class")){
						for(int i=0; i<classArray.size(); i++){
							String tobeCleanedClass = classArray.get(i).getAsString();
							if(ele.attr("class").equals(tobeCleanedClass)){
								//							ele.attr("class", "wasCleanedUp");
								ele.remove();
								break;
							}
						}
					}
				} else {
					for(Element ele:doc.getElementsByAttribute("class")){
						ele.attr("class", "*");
					}
				}
			}
			
			//clean up elements based on ATTRIBUTES
			if (jsonCleanUp.keySet().contains("attribute")) {
				JsonObject attObject = jsonCleanUp.getAsJsonObject("attribute");
				if (attObject.keySet().contains("name") && attObject.getAsJsonArray("name").size()>0) {
					JsonArray nameArray = attObject.get("name").getAsJsonArray();
					for (int i = 0; i < nameArray.size(); i++) {
						String attName = nameArray.get(i).getAsString();
						Elements element = doc.getElementsByAttribute(attName);
						if (element != null) {
							element.remove();
						}
					}
				}
				if (attObject.keySet().contains("nameValue") && attObject.getAsJsonArray("nameValue").size()>0) {
					JsonArray nameValueArray = attObject.get("nameValue").getAsJsonArray();
					for (int i = 0; i < nameValueArray.size(); i++) {
						JsonObject nameValueObject = nameValueArray.get(i).getAsJsonObject();
						for (String key : nameValueObject.keySet()) {
							Elements element = doc.getElementsByAttributeValue(key,
									nameValueObject.get(key).getAsString());
							if (element != null) {
								element.remove();
							}
						}
					}
				}
			}

			//clean up elemetns based on tag name
			if (jsonCleanUp.keySet().contains("tag")) {
				JsonArray tagObject = jsonCleanUp.getAsJsonArray("tag");
				for (int i = 0; i < tagObject.size(); i++) {
					String tagName = tagObject.get(i).getAsString();
					Elements eTag = doc.getElementsByTag(tagName);
					if (eTag != null) {
						eTag.remove();
					}
				}
			}

			//Clean up elements based on IDs
			if (jsonCleanUp.keySet().contains("id") && jsonCleanUp.getAsJsonArray("id").size()>0) {
				JsonArray idObject = jsonCleanUp.getAsJsonArray("id");
				for (int i = 0; i < idObject.size(); i++) {
					String id = idObject.get(i).getAsString();
					Element eID = doc.getElementById(id);
					if (eID != null) {
						eID.remove();
					}
				}
			}
			else{
				//delete all id values
				Elements eIDs = doc.getElementsByAttribute("id");
				for(Element ele:eIDs){
//					ele.attr("id", "This id was cleaned up");
					ele.attr("id", "");
				}
			}
		}
		out.html = doc.toString();
		out.text = doc.text();
		
		return out;
	}

	public List<String> getRandomFilePath() {
		return this.randomFilePath;
	}
	
	public void loadRandomAdminFilePath(String randomFilePathCatalogFile ) throws IOException {
		randomAdminFilePath = new ArrayList<String>();
		_loadRandomFilePath(randomFilePathCatalogFile, randomAdminFilePath);
	}

	public void loadRandomFilePath(String randomFilePathCatalogFile ) throws IOException {
		randomFilePath = new ArrayList<String>();
		_loadRandomFilePath(randomFilePathCatalogFile, randomFilePath);
	}
	
	public void loadAdminRandomFilePath(String randomAdminFilePathCatalogFile ) throws IOException {
		randomAdminFilePath = new ArrayList<String>();
		_loadRandomFilePath(randomAdminFilePathCatalogFile, randomAdminFilePath);
	}
	
	public void _loadRandomFilePath(String randomFilePathCatalogFile, List<String> randomFilePath ) throws IOException {
		if(randomFilePathCatalogFile==null || randomFilePathCatalogFile.isEmpty()){
			return;
		}
		
		//Each line in the file contain a filePath
		File f = new File(randomFilePathCatalogFile);
		if (!f.exists()) {
			System.out.println("File not found: " + randomFilePathCatalogFile);
			return;
		}

		BufferedReader br = new BufferedReader(new FileReader(f));
		
		String line = br.readLine();
		
		
		
		while (line != null) {
			if(!line.trim().isEmpty()){
				String path = line.trim();
				
				randomFilePath.add(path);
			}
			line = br.readLine();
		}
		br.close();
	}



	public boolean isSignup(Action action) {
		String url = action.getUrl();
		if(url==null || url.isEmpty()){
			return false;
		}
		if(action.getMethod()!=null && action.getMethod().toLowerCase().equals("post")){
			return sysConfig.isSignupUrl(url);
		}
		return false;
	}

	public Object getAdmin() {
		if ( admin == null ) {
			for ( Account user : userList ) {
				if ( user.getUsername().equals(ADMIN_USERNAME) ) {
					admin=user;
				}
			}
		}
		return admin;
	}


	public List getRandomAdminFilePath() {
		return randomAdminFilePath;
	}
	
	/**
	 * Check if the user_1 covers all URLs dedicated to user_2
	 * @param username1
	 * @param username2
	 * @return
	 */
	public boolean coverAllUrls(String username1, String username2) {
		if(username1==null || username2==null ||
				username1.isEmpty() || username2.isEmpty()) {
			return false;
		}
		
		if(userList==null || userList.size()<1 ||
				inputList==null || inputList.size()<1) {
			System.out.println("Need to load inputs before calling this function!");
			return false;
		}
		
		//Firstly check if both of two usernames exist
		boolean u1Exist = false;
		boolean u2Exist = false;
		
		String anonym = "anonym";
		
		//list all urls accessed by each user
		Map<String, HashSet<String>> allUserUrls = new HashMap<String, HashSet<String>>();
		allUserUrls.put(anonym, new HashSet<String>());
		
		String u1 = username1.trim();
		String u2 = username2.trim();
		
		for(Account user:userList) {
			if(user.getUsername()!=null) {
				String un = user.getUsername().trim();
				if(un.equals(u1)) {
					u1Exist = true;
				}
				else if(un.equals(u2)) {
					u2Exist = true;
				}
				
				allUserUrls.put(un, new HashSet<String>());
			}
			
		}
		
		if(!(u1Exist && u2Exist)) {
			return false;
		}
		
		for(WebInputCrawlJax input:inputList) {
			if(input==null ||
					input.actions()==null ||
					input.actions().size()<1) {
				continue;
			}
			
			for(Action act:input.actions()) {
				String url = act.getUrl();
				if(url!=null && !url.trim().isEmpty()) {
					url = url.trim();
					if(url.endsWith("/")) {
						url = url.substring(0, url.length()-1);
					}
					Account user = (Account)act.getUser();
					String username = user.getUsername();
					
					if(username==null || username.trim().isEmpty()) {
						username = anonym;
					}

					if(username.equals(u1) || username.equals(u2)) {
						allUserUrls.get(username).add(url);
					}
				}
			}
		}
		
		List<String> diff = new ArrayList<String>();
		
		for(String u2Url:allUserUrls.get(u2)) {
			if(!allUserUrls.get(u1).contains(u2Url)) {
				diff.add(u2Url);
			}
		}
		
		if(diff.size()<1) {
			return true;
		}
		
		System.out.println("num of " + u1 + ": " +allUserUrls.get(u1).size());
		System.out.println("num of " + u2 + ": " +allUserUrls.get(u2).size());
		
		System.out.println("List of URLs accessed by " + u2 + 
				" but not accessed by " + u1 +": (" + diff.size() + ")");
		for(String u:diff) {
			System.out.println("\t- " + u);
		}
		
		return false;
	}
	
	


	public boolean isSupervisorOf(Object user1, Object user2) {
		if(user1==null || user2==null ||
				!(user1 instanceof Account) ||
				!(user2 instanceof Account)) {
			return false;
		}
		
		String username1 = ((Account)user1).getUsername();
		String username2 = ((Account)user2).getUsername();
		
		return sysConfig.isSupervisorOf(username1, username2);
	}
	


	public boolean isError(Object output) {
		if(!(output instanceof WebOutputSequence)){
			return false;
		}
		
		return ((WebOutputSequence)output).isError();
	}


	
	public String parameterValueUsedByOtherUsers(Action action, int parPosition) {
		if(action==null || 
				!((action instanceof StandardAction) || (action instanceof IndexAction))) {
			return null;
		}
		//TODO
		String parValue = "";
		
		return parValue;
	}


	
	
}
