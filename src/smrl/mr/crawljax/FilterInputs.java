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
import smrl.mr.language.AugmentInput;
import smrl.mr.language.DBPopulator;
import smrl.mr.language.MR;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.test.ReplicateInputs;

public class FilterInputs {
	
	//For Jenkins
//	static String configFile = "./testData/Jenkins/jenkinsSysConfig_filter.json";
//	static String[] avoidString = {"$stapler/bound/", "ajaxexecutors", "ajaxbuildqueue", "buildhistory/ajax"};
	
	//For Joomla
	static String configFile = "./testData/Joomla/joomlaSysConfig.json";
	static String[] avoidString = {"opensearch", "module.orderposition", "update.ajax", "jform_articletext"};
	
	static boolean printAugmentedInput = true;

	public static void main(String[] args) {
		WebOperationsProvider provider = new WebOperationsProvider(configFile);
		DBPopulator dbPop = new DBPopulator(null);
		dbPop.setProvider(provider);
		MR.CURRENT = dbPop;
		
//		WebProcessor webPro = ReplicateInputs.setupWebProcessor(configFile);
		WebProcessor webPro = provider.getWebProcessor();
		
		
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
				
				if(printAugmentedInput && containAugmentedAction(input)) {
					System.out.println(input);
				}
			}
		}
		
		if(newInputsList.size()>0){
			System.out.println("Number of inputs after filtering: " +newInputsList.size());
			
			
			String fileName = webPro.sysConfig.getInputFile();
			if(fileName.endsWith(".json")){
				fileName = fileName.substring(0, fileName.length()-5) + "_filtered.json";
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
