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

import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.language.Action;
import smrl.mr.language.BasicUser;
import smrl.mr.language.Input;
import smrl.mr.language.OperationsProvider;
import smrl.mr.language.SystemConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BasicOperationsProvider implements OperationsProvider {

	@Override
	public Action DeleteCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLogin(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cannotReachThroughGUI(Object user, Input lastURL) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public smrl.mr.language.Output Output(Input input, int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public smrl.mr.language.Output Output(Input input) {
		return new BasicOutput( input.toString() );
	}

	@Override
	public Input changeCredentials(Input input, Object user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object Session(Input input, int x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List load(String dataName) {
		List l = new ArrayList<>();
		if ( "Input".equals(dataName) ){
			for ( int i = 0; i < 10 ; i++ ){
				l.add(new BasicInput(""+i));
			}
		} else if ( "User".equals(dataName) ){
			for ( int i = 0; i < 10 ; i++ ){
				l.add(new BasicUser(""+i));
			}
		}
		return l;
	}

	@Override
	public boolean notVisibleWithoutLoggingIn(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] parametersWithDifferentValues(Action action1, Action action2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean urlOfActionChangesInDifferentExecutions(Input input, int x) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action LogoutInAnotherTab() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] extractUserRoleParameters(Action action1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadEMailAction(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object deriveRandomData(String value) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean notAnonymous(Object user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEncrypted(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void nextTest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean afterLogin(Action action) {
		Input input = action.getInput();
		
		for ( Action a : input.actions() ){
			if ( a == action ){
				return false;
			}
			if ( isLogin(a) ){
				return true;
			}
		}
		
		throw new IllegalStateException("The action does not belong to the input");
	}

	@Override
	public boolean isUserIdParameter(Action a, int parpos, Object user) {
		throw new NotImplementedException();
//		return false;
	}

	@Override
	public boolean isLogout(Action action) {
		throw new NotImplementedException();
	}

	@Override
	public smrl.mr.language.Input Input(Action[] as) {
		throw new NotImplementedException();
	}

	@Override
	public boolean cannotReachThroughGUI(Object user, String URL) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action newLoginAction(Object user) {
		throw new NotImplementedException();
	}

	@Override
	public smrl.mr.language.Input Input(Action action) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isSignup(Action action) {
		throw new NotImplementedException();
	}

	@Override
	public smrl.mr.language.Input Input(List<Action> actions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAdmin(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFormInputForFilePath(Object fi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupervisorOf(Object user1, Object user2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isError(Object output) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean userCanRetrieveContent(Object user, Object output) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action newLoginAction(WebInputCrawlJax input, Object user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Action> actionsUpdatedUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SystemConfig getSysConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetProxy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKeepCache(boolean keep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keepCache() {
		// TODO Auto-generated method stub
		return false;
	}


}
