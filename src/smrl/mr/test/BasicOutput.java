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

import java.io.File;
import java.util.List;
import java.util.Map;

import smrl.mr.language.CollectionOfConcepts;
import smrl.mr.language.Output;
import smrl.mr.language.Session;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BasicOutput implements Output {

	private String value;

	public BasicOutput(String string) {
		this.value = string;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String redirectURL() {
		throw new NotImplementedException();
	}

	@Override
	public boolean isError() {
		throw new NotImplementedException();
	}

	@Override
	public boolean hasStrictTransportSecurityHeader() {
		throw new NotImplementedException();
	}

	@Override
	public String getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession(int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmptyFile() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean noFile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public File file() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containListOfTags() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<CollectionOfConcepts> listsOfTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionOfConcepts listOfTags(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}
