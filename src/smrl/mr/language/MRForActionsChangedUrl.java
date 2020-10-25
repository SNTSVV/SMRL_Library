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

import static smrl.mr.language.Operations.Input;

import java.util.ArrayList;
import java.util.HashMap;

import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.crawljax.WebOutputCleaned;
import smrl.mr.crawljax.WebOutputSequence;
import smrl.mr.crawljax.WebProcessor;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.language.actions.WaitAction;

public class MRForActionsChangedUrl extends MR {
	WebInputCrawlJax result;
	
	public MRForActionsChangedUrl(WebOperationsProvider provider) {
		this.result = new WebInputCrawlJax();
		this.setProvider(provider);
	}
	
	@Override
	public boolean mr() {
		WebInputCrawlJax input = (WebInputCrawlJax)Input(1);
		
		WebInputCrawlJax inputBackUp = null;
		try {
			inputBackUp = input.clone();
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		WebProcessor webProc = ((WebOperationsProvider)provider).getWebProcessor();
		
		System.out.println("*** Execute the input: " + input);
		//1. replicate the input 1st time
		System.out.println("- 1st time");
		WebOutputSequence outSequence1 = webProc.output(input);
		if(outSequence1==null ||
				outSequence1.getOutputSequence()==null ||
				outSequence1.getOutputSequence().size() <1 ||
				webProc.getUpdateUrlMap()==null ||
				webProc.getUpdateUrlMap().isEmpty()){
			webProc.resetUpdateUrlMap();
			return true;
		}
		
		//2. get the updatedUrlMap, then reset the updated Url map of the processor
		HashMap<Long, Long> updatedUrlMap1 = (HashMap<Long, Long>) webProc.getUpdateUrlMap().clone();
		webProc.resetUpdateUrlMap();
		
		//3. update urls of actions in the input (if needed)
		for(Action act:input.actions()) {
			if(updatedUrlMap1.containsValue(act.getActionID())) {
				String newUrl = ((WebOutputCleaned)outSequence1.getOutputAt(act.getPosition())).realRequestedUrl;
				act.updateUrl(newUrl);
			}
		}
		
		//4. replicate the input 2nd time
		System.out.println("- 2nd time");
		WebOutputSequence outSequence2 = webProc.output(input);
		
		if(outSequence2==null  ||
				outSequence2.getOutputSequence()==null ||
				outSequence2.getOutputSequence().size() <1 ||
				outSequence1.getOutputSequence().size() != outSequence2.getOutputSequence().size() ||
				webProc.getUpdateUrlMap()==null ||
				webProc.getUpdateUrlMap().isEmpty()){
			webProc.resetUpdateUrlMap();
			return true;
		}
		
		//6. get the updatedUrlMap, then reset the updated Url map of the processor
		HashMap<Long, Long> updatedUrlMap2 = (HashMap<Long, Long>) webProc.getUpdateUrlMap().clone();
		webProc.resetUpdateUrlMap();
		
		//update result
		ArrayList<Object> outSeq1 = outSequence1.getOutputSequence();
		ArrayList<Object> outSeq2 = outSequence2.getOutputSequence();
		
		for(int i=0; i<outSeq1.size(); i++) {
			if(updatedUrlMap1.containsValue(input.actions().get(i).actionID) &&
					updatedUrlMap2.containsValue(input.actions().get(i).actionID)) {
				WebOutputCleaned seq1 = (WebOutputCleaned)outSeq1.get(i);
				WebOutputCleaned seq2 = (WebOutputCleaned)outSeq2.get(i);
				
				if(seq1==null || seq2==null ||
						(seq1.realClickedElementText==null && seq2.realClickedElementText!=null) ||
						(seq2.realClickedElementText==null && seq1.realClickedElementText!=null)) {
					continue;
				}

				if(!seq1.realRequestedUrl.equals(seq2.realRequestedUrl) &&
						((seq1.realClickedElementText==null && seq2.realClickedElementText==null) ||
						seq1.realClickedElementText.equals(seq2.realClickedElementText))) {
					try {
						Action addedAction = inputBackUp.actions().get(i).clone();
						addedAction.setUser(null);
//						if(!containActionWithSameText(result, addedAction)) {
//							System.out.println("!!! added action: " + addedAction);
//							result.addAction(addedAction);
//						}
						
						if(!result.containAction(addedAction)) {
							System.out.println("!!! added action: " + addedAction);
							result.addAction(addedAction);
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean containActionWithSameText(WebInputCrawlJax input, Action action) {
		if(input==null) {
			input = new WebInputCrawlJax();
			return false;
		}
		
		if(action==null) {
			return true;
		}
		
		for(Action act:result.actions()) {
			if(act.equals(action)) {
				if(act instanceof StandardAction ||
						act instanceof WaitAction) {
					if(act.getText()!=null && action.getText()!=null &&
					act.getText().equals(action.getText())) {
						return true;
					}
				}
				else {	
					return true;
				}
			}
		}
		
		return false;
	}

	public void exportResultToFile() {
		SystemConfig sysCon = ((WebOperationsProvider)provider).getWebProcessor().sysConfig;
		if(sysCon!=null &&
				sysCon.getActionsChangedUrlFileName()!=null &&
				!sysCon.getActionsChangedUrlFileName().isEmpty()) {
			result.exportToFile(sysCon.getActionsChangedUrlFileName());
		}
	}

}
