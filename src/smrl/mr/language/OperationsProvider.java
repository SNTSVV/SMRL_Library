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

import java.util.ArrayList;
import java.util.List;

import smrl.mr.crawljax.WebInputCrawlJax;

public interface OperationsProvider {

	Action DeleteCookies();

	boolean isLogin(Action action);

	boolean cannotReachThroughGUI(Object user, smrl.mr.language.Input lastURL);
	
	boolean cannotReachThroughGUI(Object user, String URL);

	/**
	 * @param input
	 * @param position of the item in the input (value from 1)
	 * @return
	 */
	Output Output(smrl.mr.language.Input input, int pos);

	Output Output(smrl.mr.language.Input input);

	smrl.mr.language.Input changeCredentials(smrl.mr.language.Input input, Object user);
//
//	List<User> loadUsers();
//
//	List<I> loadInputs();

	Object Session(smrl.mr.language.Input input, int x);

	List load(String dataName);

	boolean notVisibleWithoutLoggingIn(String url);

	int[] parametersWithDifferentValues(Action action1, Action action2);

	boolean urlOfActionChangesInDifferentExecutions(Input input, int x);

	Action LogoutInAnotherTab();

	int[] extractUserRoleParameters(Action action1);

	boolean isReadEMailAction(Action action);

	Object deriveRandomData(String value);

	boolean userCanRetrieveContent(Object user, Object output);

	boolean notAnonymous(Object user);

	boolean isEncrypted(Action action);

	void nextTest();

	boolean afterLogin(Action action);

	boolean isUserIdParameter(Action a, int parpos, Object user);

	boolean isLogout(Action action);

	smrl.mr.language.Input Input(Action[] as);

	Action newLoginAction(Object user);
	
	Action newLoginAction(WebInputCrawlJax input, Object user);

	smrl.mr.language.Input Input(Action action);

	boolean isSignup(Action action);

	smrl.mr.language.Input Input(List<Action> actions);

	boolean isAdmin(Object user);

	boolean isFormInputForFilePath(Object fi);

	boolean isSupervisorOf(Object user1, Object user2);

	boolean isError(Object output);

	ArrayList<Action> actionsUpdatedUrl();

	SystemConfig getSysConfig();

	void resetProxy();

	void setKeepCache(boolean keep);
	
	boolean keepCache();
	
	

}
