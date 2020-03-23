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
