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

import smrl.mr.crawljax.WebInputCrawlJax;

public class DBPopulator extends MR {
	
	private OutputDBFiller db;

	public DBPopulator(OutputDBFiller db){
		this.db = db;
	}

	@Override
	public boolean mr() {
		for ( Action a : Input(1).actions() ) {
			Object user = a.getUser();
			Output output = Operations.Output(Input(1),a.getPosition());
			db.store( user, output );
		}
		
		
		return true;
	}
	
	
	//To assure that this method work well, the function nextTest of the provider have to be overridden to not do anything
	public void exportActionsChangedUrl() {
		ArrayList<Action> actUpdatedUrl = provider.actionsUpdatedUrl();

		//Create the list of actions which changed URL after multiple executions
		if(actUpdatedUrl==null || actUpdatedUrl.isEmpty()) {
			return;
		}

		WebInputCrawlJax inp = new WebInputCrawlJax(actUpdatedUrl);
		SystemConfig sysCon = provider.getSysConfig();

		if(sysCon!=null &&
				sysCon.getActionsChangedUrlFileName()!=null &&
				!sysCon.getActionsChangedUrlFileName().isEmpty()) {
			inp.exportToFile(sysCon.getActionsChangedUrlFileName());
		}
	}
	
}