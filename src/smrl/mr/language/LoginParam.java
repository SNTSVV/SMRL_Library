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

public class LoginParam {
	public String loginUrl;
	public String userParam;
	public String passwordParam;
	
	public LoginParam() {
		this.loginUrl = "";
		this.userParam = "";
		this.passwordParam = "";
	}
	
	public LoginParam(String url, String usernameParam, String passParam) {
		this.loginUrl = url.trim();
		this.userParam = usernameParam;
		this.passwordParam = passParam;
	}
	
	public boolean matchLoginParam(String url, String usernameParam, String passParam) {
		if(this.loginUrl==null || this.userParam==null || this.passwordParam==null ||
				url==null || usernameParam==null || passParam==null ||
				this.loginUrl.isEmpty() || url.trim().isEmpty()) {
			return false;
		}
		
		if(this.loginUrl.equals(url.trim()) &&
				this.userParam.equals(usernameParam) &&
				this.passwordParam.equals(passParam)) {
			return true;
		}
		
		return false;
	}

	public boolean hasAccountParemeters() {
		if(this.userParam != null &&
				this.passwordParam!= null &&
				!this.userParam.isEmpty() &&
				!this.passwordParam.isEmpty()){
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LoginParam)){
			return false;
		}
		
		LoginParam that = (LoginParam)obj;
		return matchLoginParam(that.loginUrl, that.userParam, that.passwordParam);
	}

	@Override
	public String toString() {
		String res = "Url: " + loginUrl + " (userParam: " + userParam + ", passwordParam: " + passwordParam + ")";
		return res;
	}
	
	
}
