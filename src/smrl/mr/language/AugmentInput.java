package smrl.mr.language;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.crawljax.WebOutputCleaned;
import smrl.mr.crawljax.WebOutputSequence;
import smrl.mr.crawljax.WebProcessor;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.test.ReplicateInputs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AugmentInput {
//	static String configFile = "./testData/OTG_AUTHZ_002/jenkins-CVE-2018-1999046-2/jenkinsSysConfig.json";
//	static String configFile = "./testData/Jenkins/simple/jenkinsSysConfig.json";
//	static String configFile = "./testData/Jenkins/fullWithAnonym/jenkinsSysConfigToAugment.json";
//	static String configFile = "./testData/FINAL/sysConfig_Short_ToAugment.json";
//	static String configFile = "./testData/Jenkins/jenkinsSysConfig_withProxy.json";
	public static String augmentedText = "augmented action";
	

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		String configFile = "./testData/Jenkins/jenkinsSysConfig_withProxy.json";
		
		if(args!=null && args.length>=1) {
			configFile = args[0].trim();
		}
		else{
			System.out.println("Usage: " + AugmentInput.class.getSimpleName() + 
					" <path_to_system_config_file>");
			return;
		}
		
		WebProcessor webPro = ReplicateInputs.setupWebProcessor(configFile);
		
		
		//1. run inputs: step by step (in loops)
		List<WebInputCrawlJax> inputList = webPro.getInputList();
		
		ArrayList<WebInputCrawlJax> newInputsList = new ArrayList<WebInputCrawlJax>();
		
		for(WebInputCrawlJax input:inputList){
			WebOutputSequence outSequence = webPro.output(input,true);
			if(outSequence==null){
				continue;
			}
			
			ArrayList<WebInputCrawlJax> newInputs = generateNewInputs(input, outSequence);
			if(newInputs!=null && newInputs.size()>0){
				for(WebInputCrawlJax i:newInputs){
					if(!newInputsList.contains(i) &&
							hasNewURL(inputList, i)){
						newInputsList.add(i);
					}
				}
//				newInputsList.addAll(newInputs);
			}
		}
		
		//3. export all inputs (old and new) in a new input file
		if(newInputsList.size()>0){
			System.out.println("Number of new inputs: " +newInputsList.size());
			for(WebInputCrawlJax i:newInputsList){
				System.out.println("- " + i);
			}
	
			for(WebInputCrawlJax i:newInputsList){
				if(!inputList.contains(i)){
					inputList.add(i);
				}
			}
//			inputList.addAll(newInputsList);
			
			String fileName = webPro.sysConfig.getInputFile();
			if(fileName.endsWith(".json")){
				fileName = fileName.substring(0, fileName.length()-5) + "_augmented.json";
			}
			
			exportInputListToFile(fileName, inputList);
		}
		else{
			System.out.println("no new input");
		}
	}


	private static boolean hasNewURL(List<WebInputCrawlJax> inputsList, WebInputCrawlJax input) {
		if((inputsList==null || inputsList.size()<1) &&
				(input!=null && input.size()>0)) {
			return true;
		}
		for(Action act:input.actions()) {
			String url = act.getUrl();
			if(url!=null && !url.isEmpty() &&
					!containUrl(inputsList, url)) {
			return true;
			}
		}
		return false;
	}


	private static ArrayList<WebInputCrawlJax> generateNewInputs(
			WebInputCrawlJax inputSequence, WebOutputSequence outSequence) {
		return generateNewInputs(inputSequence, outSequence, false);
	}
	
	private static ArrayList<WebInputCrawlJax> generateNewInputs(
			WebInputCrawlJax inputSequence, WebOutputSequence outSequence, boolean outDomain) {
		if(inputSequence==null || outSequence==null ||
				outSequence.getOutputSequence()==null ||
				inputSequence.size()<1 || 
				outSequence.getOutputSequence().size()<1 ||
				inputSequence.size()!=outSequence.getOutputSequence().size()){
			return null;
		}
		
		String domain = getDomainFromInputSequence(inputSequence);
		
		ArrayList<WebInputCrawlJax> res = new ArrayList<WebInputCrawlJax>();
		
		int order = -1;
		
		for(Object out:outSequence.getOutputSequence()){
			order++;
			WebOutputCleaned actOut = (WebOutputCleaned)out;
			if(actOut.downloadedObjects==null || 
					actOut.downloadedObjects.isEmpty()){
				continue;
			}
			
			for(String key : actOut.downloadedObjects.keySet()){
				boolean execute = false;
				
				if((outDomain || 
						hasSameDomain(key,domain)) &&
						!ignoreUrl(key) &&
						!containUrl(res,key)){
					execute = true;
				}
				
				if(execute){
					WebInputCrawlJax newInput = new WebInputCrawlJax();
					String currentURL = "";

					//copy the previous actions
					for(int i=0; i<=order; i++){
						Action clonedAction;
						try {
							clonedAction = inputSequence.actions().get(i).clone();
							newInput.addAction(clonedAction);

							currentURL = inputSequence.actions().get(i).getUrl();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}

					}

					//create new action
					StandardAction newAction = new StandardAction();
					newAction.setText(augmentedText);
					newAction.setUrl(key);
					newAction.setMethod(actOut.downloadedObjects.get(key));
					newAction.setCurrentURL(currentURL);

					newInput.addAction(newAction);

					if(newInput.size()>0){
						//should check if res already contain newInput before add it into res
						if(!res.contains(newInput)){
							res.add(newInput);
						}
					}
				}
			}
			
		}
		return res;
	}

	private static boolean containUrl(List<WebInputCrawlJax> inputsList, String url) {
		if(inputsList==null || url==null) {
			return false;
		}
		
		for(WebInputCrawlJax input:inputsList) {
			if(input.contain(url)) {
				return true;
			}
		}
		
		return false;
	}


	private static boolean ignoreUrl(String url) {
		
		if(url==null || url.trim().isEmpty()) {
			return false;
		}
		
		String path = "";
		
		try {
			URI uri = new URI(url);
			path = uri.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		if(path==null) {
			return false;
		}
		
		
		if(path.endsWith(".js") ||
				path.endsWith(".css")) {
			return true;
		}
		
		return false;
	}


	private static boolean hasSameDomain(String url1, String url2) {
		if(url1==null || url2==null ||
				url1.isEmpty() || url2.isEmpty()){
			return false;
		}
		
		String u1 = url1.trim();
		String u2 = url2.trim();
		
		while(u1.endsWith("/") ||
				u1.endsWith("#")){
			u1 = u1.substring(0, u1.length()-1);
		}
		
		while(u2.endsWith("/") ||
				u2.endsWith("#")){
			u2 = u2.substring(0, u2.length()-1);
		}
		
		String domain1 = getDomainFromURL(u1);
		String domain2 = getDomainFromURL(u2);
		
		return domain1.equals(domain2);
	}


	private static String getDomainFromInputSequence(WebInputCrawlJax inputSequence) {
		if(inputSequence==null ||
				inputSequence.actions() ==null ||
				inputSequence.actions().size()<1){
			return null;
		}
		
		String domain = null;
		
		//get domain from index action
		for(Action act:inputSequence.actions()){
			if(act.getEventType().equals(Action.ActionType.index)){
				domain = act.getUrl();
				break;
			}
		}
		
		//get domain from any action in the actions sequence
		if(domain==null){
			for(Action act:inputSequence.actions()){
				domain = act.getUrl();
				
				if(domain!=null && !domain.isEmpty()){
					break;
				}
			}
		}
		
		domain = getDomainFromURL(domain);
		
		return domain;
	}
	
	public static String getDomainFromURL(String url){
		String domain = url;
				
		//keep only the protocol (e.g., http, https) and the domain name + port
		if(domain!=null && !domain.isEmpty()){
			try {
				URI uri = new URI(domain);

				if(uri.getScheme()!=null && uri.getAuthority()!=null){
					domain = uri.getScheme() + "://" + uri.getAuthority();
				}
				else{
					domain = domain.substring(0,domain.indexOf("/"));
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		return domain;
	}


	public static void exportInputListToFile(String fileName,
			List<WebInputCrawlJax> inputList) {

		JsonObject jsonResult = new JsonObject(); 	//containing all paths
		for(int i=0; i<inputList.size(); i++){
			String pathName = "path" + String.valueOf(i+1);
			JsonArray path = inputList.get(i).toJson();
			jsonResult.add(pathName, path);
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(jsonResult);
		
		FileWriter writer;
		try {
			writer = new FileWriter(fileName);

			
//			writer.write(jsonResult.toString());
			writer.write(prettyJson);
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
