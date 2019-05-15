package ase2019.mr.test;

import java.util.ArrayList;
import java.util.List;

import ase2019.mr.language.Action;
import ase2019.mr.language.BasicUser;
import ase2019.mr.language.Input;
import ase2019.mr.language.OperationsProvider;
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
	public ase2019.mr.language.Output Output(Input input, int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ase2019.mr.language.Output Output(Input input) {
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
	public boolean userCanRetrieveContent(Object user, ase2019.mr.language.Output output) {
		// TODO Auto-generated method stub
		return false;
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
	public ase2019.mr.language.Input Input(Action[] as) {
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
	public ase2019.mr.language.Input Input(Action action) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isSignup(Action action) {
		throw new NotImplementedException();
	}

	@Override
	public ase2019.mr.language.Input Input(List<Action> actions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAdmin(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

}
