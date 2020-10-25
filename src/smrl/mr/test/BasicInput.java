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
package smrl.mr.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.language.Action;
import smrl.mr.language.Input;
import smrl.mr.language.MRData;

public class BasicInput extends Input {

	public BasicInput(String string) {
		setID(string);
		
		for(int i = 0; i<10; i++){
			addAction(new BasicAction(string+":"+i));
		}
	}

	List<Action> actions = new ArrayList<Action>();
	
	@Override
	public void addAction(Action _basicAction) {
		addAction(actions.size(), _basicAction);
	}
	
	@Override
	public void addAction(int pos, Action _basicAction) {
		
		Action basicAction;
		try {
			basicAction = (Action) _basicAction.clone();
			basicAction.setInput(this);
			actions.add( pos, basicAction);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Action> actions() {
		// TODO Auto-generated method stub
		return actions;
	}

	@Override
	public void copyActionTo(int x, int y) {
		actions.add(y, actions.get(x));
	}


	@Override
	public String toString() {
		return "Input("+this.getId()+")" + actions.toString();
	}

	@Override
	public BasicInput clone() throws CloneNotSupportedException {
		
		BasicInput clone = (BasicInput) super.clone();
		clone.actions = new ArrayList<>();
		clone.actions.addAll(actions);
		
		return clone;
	}

	@Override
	public int indexOf(Action action) {
		return this.actions.indexOf(action);
	}

	@Override
	public JsonArray toJson() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
