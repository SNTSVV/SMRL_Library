package smrl.mr.crawljax;

import java.util.ArrayList;
import java.util.List;

import smrl.mr.language.Action;
import smrl.mr.language.AugmentInput;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.test.ReplicateInputs;

public class FilterInputs {
	
	static String configFile = "./testData/Jenkins/jenkinsSysConfig_filter.json";
	static String[] avoidString = {"$stapler/bound/", "ajaxexecutors", "ajaxbuildqueue", "buildhistory/ajax"};

	public static void main(String[] args) {
		WebProcessor webPro = ReplicateInputs.setupWebProcessor(configFile);
		
		
		//1. loads inputs
		List<WebInputCrawlJax> inputList = webPro.getInputList();
		
		if(inputList==null || inputList.size()<1) {
			return;
		}
		
		
		List<WebInputCrawlJax> newInputsList = new ArrayList<WebInputCrawlJax>();
		
		for(WebInputCrawlJax input:inputList) {
			if((input!=null && input.size()>0) &&
					(!containAugmentedAction(input) ||
					!containAvoidString(input))) {
				newInputsList.add(input);
			}
		}
		
		if(newInputsList.size()>0){
			System.out.println("Number of inputs after filtering: " +newInputsList.size());
			
			
			String fileName = webPro.sysConfig.getInputFile();
			if(fileName.endsWith(".json")){
				fileName = fileName.substring(0, fileName.length()-5) + "_augmented_filtered.json";
			}
			
			AugmentInput.exportInputListToFile(fileName, newInputsList);
		}
		else{
			System.out.println("no new input");
		}

	}

	private static boolean containAvoidString(WebInputCrawlJax input) {
		if(input==null || input.size()<1) {
			return false;
		}
		
		for(Action act:input.actions()) {
			if(actionContainAvoidString(act)) {
				return true;
			}
		}
		return false;
	}

	private static boolean actionContainAvoidString(Action act) {
		String url = act.getUrl();
		
		if(url==null || url.isEmpty()) {
			return false;
		}
		
		for(String avdString:avoidString) {
			if(url.toLowerCase().contains(avdString)) {
				return true;
			}
		}
		
		return false;
	}

	private static boolean containAugmentedAction(WebInputCrawlJax input) {
		if(input==null || input.size()<1) {
			return false;
		}
		
		for(Action act:input.actions()) {
			if(isAugmentedAction(act)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isAugmentedAction(Action act) {
		if(!(act instanceof StandardAction)) {
			return false;
		}
		else if(((StandardAction)act).getText().equals(AugmentInput.augmentedText)) {
			return true;
		}
		return false;
	}

}
