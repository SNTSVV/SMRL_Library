package smrl.mr.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.input.ReaderInputStream;
import org.hamcrest.core.IsInstanceOf;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import smrl.mr.crawljax.Account;
import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.crawljax.WebProcessor;

public class MergeInputs {
	public static void main(String[] args) {
		String listFileName = "./testData/Jenkins/collectedData/listInputFiles.txt";
		String configFile = "./testData/Jenkins/collectedData/jenkinsSysConfig.json";
		String outFileName = "./testData/Jenkins/collectedData/input.json";
		boolean jenkinsSystem = true;

		
		
		WebProcessor webPro = new WebProcessor();
		
		if(configFile!=null && !configFile.isEmpty()){
			webPro.setConfig(configFile);
		}
		
		
		ArrayList<String> inputFileNames = new ArrayList<String>();
		
		try {
			FileReader fileReader = new FileReader(listFileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				if(!line.trim().isEmpty()){
					inputFileNames.add(line);
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(listFileName + " does NOT exist!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<WebInputCrawlJax> listFullInputs = new ArrayList<WebInputCrawlJax>();
		Map<String, HashSet<String>> addedUrls = new HashMap<String, HashSet<String>>();
		
		for(String iFileName:inputFileNames){
			File f = new File(iFileName);
			if(!f.exists()){
				continue;
			}
			
			List<WebInputCrawlJax> currentInputs = loadInputFromFile(iFileName, webPro, jenkinsSystem);
			
			if(currentInputs==null || currentInputs.size()<1){
				continue;
			}
			
			for(WebInputCrawlJax input:currentInputs){
				
				if(!listFullInputs.contains(input) &&
						containNewUrl(addedUrls, input)){
					listFullInputs.add(input);
				}
			}
		}
		
//		exporttofile
		AugmentInput.exportInputListToFile(outFileName, listFullInputs);
		
		int numUrls = 0;
		for(String key: addedUrls.keySet()) {
			numUrls += addedUrls.get(key).size();
		}
		
		System.out.println("Merging input files: Done!!!\n"
				+ "\nNumber of inputs: " + listFullInputs.size() 
//				+ "\nNumber of users: " + addedUrls.size()
				+ "\nNumber of Urls: " + numUrls);
	}

	private static boolean containNewUrl(Map<String, HashSet<String>> addedUrls, WebInputCrawlJax input) {
//		System.out.println("Begin checking: " + addedUrls);
		boolean containNew = false;
		
		if(input==null || input.actions()==null || input.actions().size()<1) {
			return false;
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
					username = "anonymous";
				}
				
				if(!addedUrls.keySet().contains(username)) {
					addedUrls.put(username, new HashSet<String>());
				}

				containNew = addedUrls.get(username).add(url) || containNew;
			}
		}
//		System.out.println("End checking: " + addedUrls);
		return containNew;
	}

	private static List<WebInputCrawlJax> loadInputFromFile(String jsonInputFileName, WebProcessor webProcessor, boolean jenkinsSystem) {
		List<WebInputCrawlJax> res = new ArrayList<WebInputCrawlJax>();
		
		SystemConfig sysConfig = webProcessor.sysConfig;
		String userParam = sysConfig.getUserParameter();
		String passwordParam = sysConfig.getPasswordParameter();
		
		Gson gson = new Gson();
		File jsonFile = Paths.get(jsonInputFileName).toFile();
		if (!jsonFile.exists()) {
			System.out.println("Input file not found: " + jsonInputFileName);
			return res;
		}
		
		JsonObject jsonObject=null;
		try {
			jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(String key:jsonObject.keySet()){
			JsonArray jsonInput = jsonObject.get(key).getAsJsonArray();
			if(jsonInput!= null && jsonInput.size()>0){
				WebInputCrawlJax input = new WebInputCrawlJax(jsonInput);

				

				if(userParam!=null && passwordParam!=null &&
						!userParam.isEmpty() && !passwordParam.isEmpty()){
					input.identifyUsers(userParam, passwordParam, webProcessor);
				}
				
				if(jenkinsSystem) {
					if(containExactLoginAction(webProcessor, input)) {
						res.add(input);
					}
				}
				else {
					res.add(input);
				}
			}
		} 
		
		return res;
	}
	
	private static boolean containExactLoginAction(WebProcessor webPro, WebInputCrawlJax inputSequence) {
		if(inputSequence==null || 
				inputSequence.actions()==null ||
				inputSequence.actions().size()<1) {
			return false;
		}
		
		for(int i=0; i<inputSequence.actions().size(); i++) {
			Action act = inputSequence.actions().get(i);
			if(act!=null &&
					exactLogin(webPro, act)) {
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * Check whether an action is an exact login action, based on the action URL, HTTP Post method and input forms 
	 * (only form names, do not care form values) 
	 * @param webPro An instance of the class WebProcessor 
	 * @param act The action to be check
	 * @return true if the act is an exact login action
	 */
	@SuppressWarnings("static-access")
	private static boolean exactLogin(WebProcessor webPro, Action act) {
		if(webPro==null || webPro.getSysConfig()==null ||
				act==null || act.getUrl()==null || act.getUrl().isEmpty())
		{
			return false;
		}
		
		if(act.getMethod().toLowerCase().equals("post") &&
			webPro.getSysConfig().isLoginURL(act.getUrl())) {
			JsonArray fInputs = act.getFormInputs();
			
			if(fInputs==null || fInputs.size()<1) {
				return false;
			}
			
			String userParam = webPro.getSysConfig().getUserParameter();
			String passwordParam = webPro.getSysConfig().getPasswordParameter();
			if(userParam==null || userParam.isEmpty() ||
					passwordParam==null || passwordParam.isEmpty()) {
				return false;
			}
			
			boolean containUsernameParam = false;
			boolean containPasswordParam = false;
			
			for(int i=0; i<fInputs.size() && !(containUsernameParam && containPasswordParam); i++) {
				JsonElement fi = fInputs.get(i);
				if(fi!=null && fi instanceof JsonObject) {
					if(((JsonObject)fi).keySet().contains("identification")) {
						JsonObject iden = ((JsonObject)fi).get("identification").getAsJsonObject();
						if(iden!=null && iden.keySet().contains("value")) {
							String value = iden.get("value").getAsString();
							if(value!=null && !value.isEmpty()) {
								value = value.trim();
								if(value.equals(userParam)) {
									containUsernameParam=true;
								}
								else if(value.equals(passwordParam)) {
									containPasswordParam = true;
								}
							}
						}
					}
				}
			}
			
			if(containUsernameParam && containPasswordParam) {
				return true;
			}
		}
		
		return false;
	}

}
