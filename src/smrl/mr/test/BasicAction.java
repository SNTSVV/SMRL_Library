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

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;
import smrl.mr.language.Session;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BasicAction extends Action {

	private String id;

	public BasicAction(String string) {
		this.id = string;
	}
	
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Action:"+id+"]";
	}



	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String getParameterValue(int p) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public boolean setParameterValue(int p, Object object) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setChannel(String string) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public List<Entry<String, String>> getParameters() {
//		// TODO Auto-generated method stub
//		return null;
//	}


	@Override
	public boolean setEncryption(Object object) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean setUrl(String url) {
		return false;
	}



	@Override
	public boolean setMethod(String method) {
		throw new NotImplementedException();
	}



	@Override
	public String getMethod() {
		throw new NotImplementedException();
	}



//	@Override
//	public String getParameterName(int p) {
//		throw new NotImplementedException();
//	}

//	@Override
//	public boolean addParameter(String name, String value) {
//		throw new NotImplementedException();
//	}



	@Override
	public boolean containAccount(Account acc) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean containCredential(Account acc) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public Action changeCredential(Account acc) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean containCredential(String userParam, String passwordParam) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public Account getCredential(String userParam, String passwordParam) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean isChannelChanged() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public JsonArray toJson() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean containFormInput() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public JsonArray getFormInputs() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean containFormInputForFilePath() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public String getOldMethod() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean isMethodChanged() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean setId(String id) {
		return false;
	}



	@Override
	public String getCipherSuite() {
		// TODO Auto-generated method stub
		return null;
	}
}
