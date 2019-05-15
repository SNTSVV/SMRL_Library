package ase2019.mr.language;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ase2019.mr.language.actions.WaitAction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Operations {

	public static boolean IMPLIES( boolean a, boolean b ){ return !a || b; }
	
	public static boolean AND( boolean a, boolean b ){ return a && b; }
	
	public static boolean OR( boolean a, boolean b ){ return a || b; }
	
//	public static boolean XOR( boolean a, boolean b ){ throw new RuntimeException("Not expected to be called. This is replaced by the xtext compiler."); }
	
	public static boolean NOT( boolean a ){ return false == a; }
	
	public static boolean FALSE( boolean a ){ return false == a; }
	
	public static boolean TRUE( boolean a ){ return true == a; }
	
	public static boolean NULL( Object a ){ return a == null; }
	
	public static boolean equal( Object a, Object b ){ return EQUAL(a, b); };
	
	public static boolean EQUAL( Object a, Object b ){ 
		boolean eq = MR.CURRENT.equal(a, b);
		
		if ( ! eq ){
			System.out.println("!!! NOT EQUAL: \n\t"+a+" \n\t"+b);
		}
		
		return eq;
	};
	
	public static boolean different( Object a, Object b ){ return MR.CURRENT.different(a, b); };
	
	public static int myint( int x ){ return x; };
	
	@MRDataProvider
	public static Input Input(int x){ 
		return (ase2019.mr.language.Input) MR.CURRENT.getMRData("Input",x);
	}
	
	public static Input Input(Action... as){ 
		return MR.CURRENT.provider.Input( as );
	}
	
	public static Input Input(List<Action>... actions){
		List<Action> allActions = new LinkedList<Action>();
		for ( List<Action> curList : actions ){
			allActions.addAll(curList);
		}
		return MR.CURRENT.provider.Input( allActions );
	}
	
	public static Input changeCredentials( Input input, Object user){
		return MR.CURRENT.provider.changeCredentials(input,user);
	}
	
	public static Input addAction( Input input, int pos, Action action ){
		try {
			Input clone = (ase2019.mr.language.Input) input.clone();
			clone.addAction( pos, action);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Input copyActionTo( Input input, int from, int to ){
		try {
			Input clone = (ase2019.mr.language.Input) input.clone();
			clone.copyActionTo(from, to);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Input addAction( Input input, Action action ){
		try {
			Input clone = (ase2019.mr.language.Input) input.clone();
			clone.addAction( action);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	
	@MRDataProvider
	public static Object User(){
		return User(1);
	}
	
	@MRDataProvider
	public static Object User(int i){
		return MR.CURRENT.getMRData("User",i);
	}
	
	@MRDataProvider
	public static Object WeakEncryption() {
		return MR.CURRENT.getMRData("WeakEncryption",1);
	}
	
	@MRDataProvider
	public static Action ActionAvailableWithoutLogin() {
		return (Action) MR.CURRENT.getMRData("ActionAvailableWithoutLogin",1);
	}
	
	@MRDataProvider
	public static Action ActionAvailableWithoutLogin(int i) {
		return (Action) MR.CURRENT.getMRData("ActionAvailableWithoutLogin",i);
	}
	
	@MRDataProvider
	public static Object RandomValue(String value) {
		Class type = typeOf( value );
		return RandomValue(type);
		//TODO: basically the data provider should be populated with 100 random Integers, 100 random Doubles, 100 random Strings, 100 random Paths
	}
	
	@MRDataProvider
	public static Object RandomValue(Class type) {
		MrDataDBRandom randomDB = (MrDataDBRandom) MR.CURRENT.getDataDB("RandomValue");
		return randomDB.get(type,1);
	}
	
	public static Class typeOf(String value) {
		try { 
			Integer v = Integer.valueOf(value);
			return Integer.class;
		} catch ( NumberFormatException e ) {
			try {
				Double v = Double.valueOf(value);
				return Double.class;
			} catch ( NumberFormatException e1 ) {
				if ( value.contains("%2F") ) {
					return Path.class;
				} 
			}
		}
		
		//default
		return String.class;
	}

	public static Object deriveRandomData(String value) {
		return MR.CURRENT.provider.deriveRandomData(value);
	}
	

	public static boolean userCanRetrieveContent(Object user, Output output) {
		return MR.CURRENT.provider.userCanRetrieveContent(user,output);
	}

	public static boolean notAnonymous(Object user) {
		return MR.CURRENT.provider.notAnonymous(user);
	}
	
	
	
	public static boolean isEncrypted(Action action) {
		return MR.CURRENT.provider.isEncrypted(action);
	}
	
	public static boolean isLogin(Action action) {
		return MR.CURRENT.provider.isLogin(action);
	}
	
	public static boolean isLogout(Action action) {
		return MR.CURRENT.provider.isLogout(action);
	}
	
	public static boolean afterLogin(Action action) {
		return MR.CURRENT.provider.afterLogin(action);
	}
	
	public static Object Session(ase2019.mr.language.Input input, int x){
		return MR.CURRENT.provider.Session(input,x);
	}
	
	/**
	 * This method returns a DeleteCookies action.
	 * 
	 * @return
	 */
	public static Action DeleteCookies(){
		return MR.CURRENT.provider.DeleteCookies();
	}
	
	public static boolean notAvailableWithoutLoggingIn(Action action) {
		return _notVisibleWithoutLoggingIn(action.getUrl()); 
	}
	
	public static boolean availableWithoutLoggingIn(Action action) {
		return _visibleWithoutLoggingIn(action.getUrl()); 
	}
	
	public static boolean notVisibleWithoutLoggingIn(String url) {
		return _notVisibleWithoutLoggingIn(url);
	}
	
	private static HashSet<String> visibleWithoutLogin;
	public static boolean _notVisibleWithoutLoggingIn(String url) {
		return ! _visibleWithoutLoggingIn(url);
	}
	
	public static boolean _visibleWithoutLoggingIn(String url) {
		try {
			return MR.CURRENT.provider.notVisibleWithoutLoggingIn(url);
		} catch ( Throwable t ) {

			if ( visibleWithoutLogin == null ) {
				visibleWithoutLogin = new HashSet<String>();
				MrDataDB<Input> inputsDB = MR.CURRENT.inputsDB();
				Iterator<ase2019.mr.language.Input> it = inputsDB._it();

				while ( it.hasNext() ) {
					ase2019.mr.language.Input next = it.next();
					for ( Action a : next.actions() ) {
						if ( isLogin(a) ) {
							break;
						}
						visibleWithoutLogin.add(url);
					}
				}
			}

			return visibleWithoutLogin.contains(url);
		}
	}
	
	public static int[] extractUserGroupParameters(Action action1) {
		return MR.CURRENT.provider.extractUserRoleParameters(action1 );
	}
	
	public static boolean urlOfActionChangesOverMultipleExecutions(Input input, int x) {
		return _urlOfActionChangesInDifferentExecutions(input,x);
	}
	
	public static boolean urlOfActionChangesOverMultipleExecutions(Action a) {
		return _urlOfActionChangesInDifferentExecutions(a.getInput(),a.getPosition());
	}
	
	private static HashSet<Action> urlChangesOverMultipleExecutions;
	public static boolean _urlOfActionChangesInDifferentExecutions(Input i, int pos) {
		try {
			return MR.CURRENT.provider.urlOfActionChangesInDifferentExecutions(i,pos);
		} catch ( Throwable t ) {

			if ( urlChangesOverMultipleExecutions == null ) {
				urlChangesOverMultipleExecutions = new HashSet<Action>();
				
				HashMap<Action, String> actionsURLs = new HashMap<Action,String>();
				
				MrDataDB<Input> inputsDB = MR.CURRENT.inputsDB();
				Iterator<ase2019.mr.language.Input> it = inputsDB._it();

				while ( it.hasNext() ) {
					ase2019.mr.language.Input next = it.next();
					for ( Action a : next.actions() ) {
						if ( actionsURLs.containsKey(a) ) {
							String _oldUrl = actionsURLs.get(a);
							if ( ! _oldUrl.equals(a.getUrl()) ) {
								urlChangesOverMultipleExecutions.add(a);
							}
						} else {
							actionsURLs.put(a, a.getUrl() );
						}
					}
				}
			}
			
			Action action = i.actions().get(pos);

			return urlChangesOverMultipleExecutions.contains(action);
		}
	}
	
	public static Action LogoutInAnotherTab() {
		return MR.CURRENT.provider.LogoutInAnotherTab();
	}
	
	public static Action Wait(long millis) {
		return new WaitAction(millis);
	}
	
	public static boolean isReadEMailAction(Action action) {
		return MR.CURRENT.provider.isReadEMailAction(action);
	}
	
	
	public static Object File() {
		return MR.CURRENT.getMRData("File",1);
	}
	
	public static boolean cannotReachThroughGUI(Object user, Input lastURL){
		return MR.CURRENT.provider.cannotReachThroughGUI(user,lastURL);
	}
	
	public static boolean cannotReachThroughGUI(Object user, String URL){
		boolean res = MR.CURRENT.provider.cannotReachThroughGUI(user, URL);
		
		if ( ! res ){
			System.out.println("!!!!Canot reach "+URL); //This should be "CAN reach"
		}
		
		return res;
	}
	
	/**
	 * Returns the output of the i-th element of an input sequence
	 * 
	 * @param input
	 * @param pos
	 * @return
	 */
	public static Output Output(Input input, int pos){
		return MR.CURRENT.provider.Output(input,pos);
	}

	/**
	 * Returns the output produced by the last action in an input sequence
	 * 
	 * @param input
	 * @return
	 */
	public static Output Output(Input input){
		return MR.CURRENT.provider.Output(input);
	}
	
	/**
	 * Returns the output produced by the last action in an input sequence
	 * 
	 * @param input
	 * @return
	 */
	public static Collection Collection(Object... objs ){
		ArrayList seq = new ArrayList<>();
		for( Object obj : objs ){
			seq.add(obj);
		}
		return seq;
	}
	
	@MRDataProvider
	public static Object RandomFilePath(int x){ 
//		return MR.CURRENT.getMRData("RandomValue:"+Path.class.getCanonicalName(),x);
		return MR.CURRENT.getMRData("RandomFilePath",x);
	}
	
	@MRDataProvider
	public static String HttpMethod(){ 
		return RandomHttpMethod(1);
	}
	
	@MRDataProvider
	public static String RandomHttpMethod(){ 
		return RandomHttpMethod(1);
	}
	
	@MRDataProvider
	public static Object RandomFilePath(){ 
		return RandomFilePath(1);
	}
	
	@MRDataProvider
	public static String RandomHttpMethod(int x){ 
		return (String) MR.CURRENT.getMRData("RandomHttpMethod",x);
	}
	
	
	
	public static boolean changeProtocol( String protocol, Action a ){
		if ( protocol.equalsIgnoreCase("HTTP") ){
			if ( ! a.getUrl().startsWith("http://") ){
				return false;
			}
			a.setUrl(a.getUrl().replace("https:",protocol));
			
			return true;
		}
		
		if ( protocol.equalsIgnoreCase("HTTPS") ){
			if ( ! a.getUrl().startsWith("https://") ){
				return false;
			}
			a.setUrl(a.getUrl().replace("http:",protocol));
			return true;
		}
		
		return false;
	}
	
	public static boolean isUserIdParameter( Action a, int parpos, Object user ){
		return MR.CURRENT.provider.isUserIdParameter(a,parpos,user);
	}
	
	
	public static Action LoginAction(Object user) {
		return MR.CURRENT.provider.newLoginAction(user);
	}

	public static boolean isSignup(Action action) {
		return MR.CURRENT.provider.isSignup(action);
	}

	public static boolean updateStringFormInput(JsonObject formInput, Object value) {
		if(formInput==null || 
				value==null || 
				!formInput.keySet().contains("type")||
				!(formInput.get("type").getAsString().startsWith("text") ||
						formInput.get("type").getAsString().equals("password") ||
						formInput.get("type").getAsString().equals("hidden"))){
			return false;
		}
		
		JsonArray valueArray = new JsonArray();
		if (value instanceof Boolean) {
			valueArray.add((Boolean) value);
			
		}
		else if (value instanceof String) {
			valueArray.add((String) value);
		} 
		else if (value instanceof Number) {
			valueArray.add((Number) value);
		}
		
		if(valueArray.size()>0){
			formInput.add("values", valueArray);
			return true;
		}
		return false;
	}

	public static String resolveUrl(String url, String addedPath) {
		if(url==null){
			return null;
		}
		
		if(addedPath==null || addedPath.isEmpty()){
			return url;
		}
		
		String res = url;
		
		try {
			URI uri = new URI(url);
			
			String query = uri.getQuery();
			if(query!=null && !query.isEmpty())
			{
				res = url.substring(0,url.indexOf(query)-1);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		if(!res.endsWith("/")){
			res += "/";
		}
		String aPath = addedPath;
		if(aPath.startsWith("/")){
			aPath = aPath.substring(1);
		}
		
		res += aPath;
		
		return res;
	}
	
	static HashMap<Object,HashSet<String>> triedInputs = new HashMap<Object,HashSet<String>>();
	public static boolean notTried(Object user, String url) {
		HashSet<String> setOfInputs = triedInputs.get(user);
		
		if ( setOfInputs == null ) {
			setOfInputs = new HashSet<String>();
			triedInputs.put( user, setOfInputs );
		}
		
		if ( setOfInputs.contains(url) ) {
			return false;
		}
		
		setOfInputs.add(url);
		return true;
	}
	
	public static boolean isAdmin( Object user) {
		return MR.CURRENT.provider.isAdmin( user );
	}
}

