package ase2019.mr.language;

import java.util.List;

public interface OperationsProvider {

	

	Action DeleteCookies();

	boolean isLogin(Action action);

	boolean cannotReachThroughGUI(Object user, ase2019.mr.language.Input lastURL);
	
	boolean cannotReachThroughGUI(Object user, String URL);

	/**
	 * @param input
	 * @param position of the item in the input (value from 1)
	 * @return
	 */
	Output Output(ase2019.mr.language.Input input, int pos);

	Output Output(ase2019.mr.language.Input input);

	ase2019.mr.language.Input changeCredentials(ase2019.mr.language.Input input, Object user);
//
//	List<User> loadUsers();
//
//	List<I> loadInputs();

	Object Session(ase2019.mr.language.Input input, int x);

	List load(String dataName);

	boolean notVisibleWithoutLoggingIn(String url);

	int[] parametersWithDifferentValues(Action action1, Action action2);

	boolean urlOfActionChangesInDifferentExecutions(Input input, int x);

	Action LogoutInAnotherTab();

	int[] extractUserRoleParameters(Action action1);

	boolean isReadEMailAction(Action action);

	Object deriveRandomData(String value);

	boolean userCanRetrieveContent(Object user, Output output);

	boolean notAnonymous(Object user);

	boolean isEncrypted(Action action);

	void nextTest();

	boolean afterLogin(Action action);

	boolean isUserIdParameter(Action a, int parpos, Object user);

	boolean isLogout(Action action);

	ase2019.mr.language.Input Input(Action[] as);

	Action newLoginAction(Object user);

	ase2019.mr.language.Input Input(Action action);

	boolean isSignup(Action action);

	ase2019.mr.language.Input Input(List<Action> actions);

	boolean isAdmin(Object user);

	boolean isFormInputForFilePath(Object fi);
	

}
