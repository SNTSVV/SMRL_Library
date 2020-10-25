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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.crawljax.Account;

public abstract class Input extends MRData implements Cloneable {
	
	public Input() {}
	
	public Input( List<Action> a ) {}

	public abstract List<Action> actions();

	public abstract void copyActionTo(int x, int y);
	
	public abstract void addAction(int pos, Action action);
	
	public abstract void addAction(Action action);
	
	public abstract int indexOf(Action action);

	public abstract JsonArray toJson();

	/**
	 * @return whether the input contains any action containing form input(s) (except login and signup)
	 */
	public boolean containFormInput() {
		
		List<Action> acts = actions();
		
		if(acts ==null || acts.size()<1){
			return false;
		}
		
		for(Action act:acts){
			if(act.containFormInput()){
				return true;
			}
		}
		
		return false;
	}

	public boolean containAccount(Object user) {
		List<Action> acts = actions();
		
		if(acts==null || acts.size()<1 || !(user instanceof Account)){
			return false;
		}
		
		for(Action act:acts){
			if(act.containAccount((Account) user)){
				return true;
			}
		}
		return false;
	}

	public boolean containFormInputForFilePath() {
		List<Action> acts = actions();
		
		if(acts==null || acts.size()<1){
			return false;
		}
		
		for(Action act:acts){
			if(act.containFormInputForFilePath()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public void exportToFile(String fileName) {
		JsonArray jsonResult = toJson();
		if(jsonResult==null || jsonResult.size()<1) {
			return;
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(jsonResult);
		
		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			writer.write(prettyJson);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
