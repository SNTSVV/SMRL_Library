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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.crawljax.Account;
import smrl.mr.language.actions.IndexAction;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.language.actions.WaitAction;

public class Operations {

	/**
	 * SMRL boolean operator.
	 * 
	 * @param a
	 * @param b
	 * @return !a || b
	 */
	public static boolean IMPLIES( boolean a, boolean b ){ return !a || b; }
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @param b
	 * @return a && b
	 */
	public static boolean AND( boolean a, boolean b ){ return a && b; }
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @param b
	 * @return a||b
	 */
	public static boolean OR( boolean a, boolean b ){ return a || b; }
	
//	public static boolean XOR( boolean a, boolean b ){ throw new RuntimeException("Not expected to be called. This is replaced by the xtext compiler."); }
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @return !a
	 */
	public static boolean NOT( boolean a ){ return false == a; }
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @return a==false
	 */
	public static boolean FALSE( boolean a ){ return false == a; }
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @return a==true
	 */
	public static boolean TRUE( boolean a ){ return true == a; }
	
	/**
	 * @param a
	 * @return a==null
	 */
	public static boolean NULL( Object a ){ return a == null; }
	
	/**
	 * @param a
	 * @param b
	 * @return true if a equals b or when a can be assigned using the value of b; false if a does not equal b, or cannot assign b to a.
	 */
	public static boolean equal( Object a, Object b ){ return EQUAL(a, b); };
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @param b
	 * @return true if a equals b or when a can be assigned using the value of b; false if a does not equal b, or cannot assign b to a.
	 */
	public static boolean EQUAL( Object a, Object b ){ 
		MR.CURRENT.setLastEQUAL( a, b );
		boolean eq =false;
		
		//Tracing: edited by Phu on 24/3/2020 to fix the error occurred when b is null
		if(b!=null) {
				eq = MR.CURRENT.equal(a, b);
		}
		
		if ( ! eq ){
			System.out.println("!!! NOT EQUAL: \n\t"+a+" \n\t"+b);
		}
		
		return eq;
	};
	
	/**
	 * SMRL boolean operator.
	 * @param a
	 * @param b
	 * @return true if a is different b; false in opposite.
	 */
	public static boolean different( Object a, Object b ){ return MR.CURRENT.different(a, b); };
	
	/**
	 * @param x
	 * @return x
	 */
	public static int myint( int x ){ return x; };
	
	/**
	 * Data Representation Function. 
	 * Returns the i-th input sequence.
	 *  
	 * @param i	The index of the input sequence to be returned, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Input Input(int x){ 
		return (smrl.mr.language.Input) MR.CURRENT.getMRData("Input",x);
	}
	
	/**
	 * Data Representation Function. 
	 * Return a new input sequence built from an array of actions.
	 * 
	 * @param as Array of actions.
	 * @return New built input sequence.
	 */
	public static Input Input(Action... as){ 
		return MR.CURRENT.provider.Input( as );
	}
	
	/**
	 * Data Representation Function. 
	 * Return a new input sequence built from a list of actions.
	 * 
	 * @param actions List of actions.
	 * @return New built input sequence.
	 */
	public static Input Input(List<Action>... actions){
		List<Action> allActions = new LinkedList<Action>();
		for ( List<Action> curList : actions ){
			allActions.addAll(curList);
		}
		return MR.CURRENT.provider.Input( allActions );
	}
	
	/**
	 * Data Representation Function. 
	 * Change credentials (if exist) in a input sequence.
	 * 
	 * @param input The source input to change credentials.
	 * @param user	The credential will be used in the new input sequence.
	 * @return The new input sequence after changing credentials.
	 */
	public static Input changeCredentials( Input input, Object user){
		return MR.CURRENT.provider.changeCredentials(input,user);
	}
	
	/**
	 * Web-specific function. 
	 * Add an action at the position pos in a input sequence.
	 * 
	 * @param input The source input.
	 * @param pos The position in the sequence of input to add new action.
	 * @param action The action will be added.
	 * @return The new input sequence after adding a new action.
	 */
	public static Input addAction( Input input, int pos, Action action ){
		try {
			Input clone = (smrl.mr.language.Input) input.clone();
			clone.addAction( pos, action);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Web-specific function. 
	 * Copy the action at the position "from" to the position "to" in the input sequence.
	 * 
	 * @param input The source input sequence.
	 * @param from The position of the action will be copied.
	 * @param to The position to paste the copied action.
	 * @return The new input sequence after copying the action from "from" to "to".
	 */
	public static Input copyActionTo( Input input, int from, int to ){
		try {
			Input clone = (smrl.mr.language.Input) input.clone();
			clone.copyActionTo(from, to);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Web-specific function. 
	 * Add an action at the end of the input sequence.
	 * 
	 * @param input The source input sequence.
	 * @param action The action will be added.
	 * @return	The new input sequence after adding the "action" at the end.
	 */
	public static Input addAction( Input input, Action action ){
		try {
			Input clone = (smrl.mr.language.Input) input.clone();
			clone.addAction( action);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Data Representation Function. 
	 * Return the 1st user, with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static Object User(){
		return User(1);
	}
	
	/**
	 * Data Representation Function. 
	 * Returns the i-th user.
	 *  
	 * @param i	The index of the user to be returned, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Object User(int i){
		return MR.CURRENT.getMRData("User",i);
	}
	
	/**
	 * Data Representation Function. 
	 * Return the 1st weak encryption algorithm, with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static Object WeakEncryption() {
		return MR.CURRENT.getMRData("WeakEncryption",1);
	}
	
	/**
	 * Data Representation Function. 
	 * Return the 1st action available without login, with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static Action ActionAvailableWithoutLogin() {
		return (Action) MR.CURRENT.getMRData("ActionAvailableWithoutLogin",1);
	}
	
	/**
	 * Data Representation Function. 
	 * Return the i-th action available without login.
	 * 
	 * @param i The index of the action to be returned, with respect the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Action ActionAvailableWithoutLogin(int i) {
		return (Action) MR.CURRENT.getMRData("ActionAvailableWithoutLogin",i);
	}
	
	/**
	 * Data Representation Function.
	 * Return a random value based on a pattern value.
	 * 
	 * @param value The pattern value (e.g., "123", "123.45", "word").
	 * @return
	 */
	@MRDataProvider
	public static Object RandomValue(String value) {
		Class type = typeOf( value );
		return RandomValue(type);
		//TODO: basically the data provider should be populated with 100 random Integers, 100 random Doubles, 100 random Strings, 100 random Paths
	}
	
	/**
	 * Data Representation Function.
	 * Return a random value of a specific class.
	 * 
	 * @param type The class type to be returned.
	 * @return
	 */
	@MRDataProvider
	public static Object RandomValue(Class type) {
		MrDataDBRandom randomDB = (MrDataDBRandom) MR.CURRENT.getDataDB("RandomValue");
		return randomDB.get(type,1);
	}
	
	/**
	 * Web-specific function.
	 * Return the corresponding class of a value pattern.
	 * 
	 * @param value The value pattern (e.g., "123", "123.45", "./file.txt", "word").
	 * @return
	 */
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

	/**
	 * Web-specific function.
	 * Derive a random data based on a value pattern.
	 * 
	 * @param value The value pattern.
	 * @return
	 */
	public static Object deriveRandomData(String value) {
		return MR.CURRENT.provider.deriveRandomData(value);
	}
	

	/**
	 * Web-specific function.
	 * Returns true if an output can be reached by the given user.
	 * The result depends on the output store which contains all outputs of all input sequences.
	 * 
	 * @param user The user to check.
	 * @param output The output to check.
	 * @return
	 */
	public static boolean userCanRetrieveContent(Object user, Object output) {
//		return MR.CURRENT.provider.userCanRetrieveContent(user,output);
		MR.printCallerLog("start");
		boolean ret = MR.CURRENT.provider.userCanRetrieveContent(user,output);
		MR.printCallerLog("end");
		return ret;
	}

	/**
	 * Web-specific function.
	 * Return true if the "user" is not anonymous.
	 * 
	 * @param user The given user to check.
	 * @return
	 */
	public static boolean notAnonymous(Object user) {
		return MR.CURRENT.provider.notAnonymous(user);
	}
	
	/**
	 * Web-specific function.
	 * Check if the "action" is requested under an encrypted channel.
	 * The result depends on the protocol in the URL.
	 * 
	 * @param action The action to check.
	 * @return true if the action is executed under an encrypted channel (e.g., https, ftps).
	 */
	public static boolean isEncrypted(Action action) {
		return MR.CURRENT.provider.isEncrypted(action);
	}
	
	/**
	 * Web-specific function.
	 * Check whether the "action" is a successful login action.
	 * The result depends on the URL of the action, the form inputs and the following action (e.g., not "try again").
	 * 
	 * @param action The action to check.
	 * @return true if the "action" is a successful login action.
	 */
	public static boolean isLogin(Action action) {
		return MR.CURRENT.provider.isLogin(action);
	}
	
	/**
	 * Web-specific function.
	 * Check whether the "action" is a logout action. 
	 * The result depends on the URL of the action (by comparing with the logout URL specified in the test configuration)
	 * 
	 * @param action The action to check.
	 * @return true if the "action" is a logout action
	 */
	public static boolean isLogout(Action action) {
		return MR.CURRENT.provider.isLogout(action);
	}
	
	/**
	 * Web-specific function.
	 * Check whether the "action" will be executed after logging in.
	 * 
	 * @param action The action to check
	 * @return true if the "action" follows a login action
	 */
	public static boolean afterLogin(Action action) {
		return MR.CURRENT.provider.afterLogin(action);
	}
	
	/**
	 * Web-specific function.
	 * Return the session at the x-th position in the input.
	 * Result depends on the outputs sequence (having by executing input).
	 * 
	 * @param input The input to check
	 * @param x	The index of the action to check its session
	 * @return
	 */
	public static Object Session(Input input, int x){
		return MR.CURRENT.provider.Session(input,x);
	}
	
	/**
	 * Web-specific function.
	 * This method returns a DeleteCookies action.
	 * 
	 * @return An DeleteCookies action
	 */
	public static Action DeleteCookies(){
		return MR.CURRENT.provider.DeleteCookies();
	}
	
	/**
	 * Web-specific function.
	 * Check whether an action can be executed before logging in.
	 * The result depends on the inputs sequences under test.
	 * 
	 * @param action The action to be checked.
	 * @return true if the action is not available without logging in.
	 */
	public static boolean notAvailableWithoutLoggingIn(Action action) {
		return MR.CURRENT.provider.notVisibleWithoutLoggingIn(action.getUrl()); 
	}
	
	/**
	 * Web-specific function.
	 * Check whether an action is available before logging in.
	 * The result depends on the inputs sequences under test.
	 * 
	 * @param action The action to be checked.
	 * @return true if the action is available without logging in.
	 */
	public static boolean availableWithoutLoggingIn(Action action) {
		return _visibleWithoutLoggingIn(action.getUrl()); 
	}
	
	/**
	 * Web-specific function.
	 * Check whether a URL is not available before logging in.
	 * The result depends on the inputs sequences under test.
	 * 
	 * @param url The URL to be checked.
	 * @return true if the URL is not available before logging in.
	 */
	public static boolean notVisibleWithoutLoggingIn(String url) {
		return MR.CURRENT.provider.notVisibleWithoutLoggingIn(url);
	}
	
	private static HashSet<String> visibleWithoutLogin;
	
	/**
	 * 
	 * Check whether a URL is not available before logging in.
	 * The result depends on the inputs sequences under test.
	 * 
	 * @param url The URL to be checked.
	 * @return true if the URL is not available before logging in.
	 */
	private static boolean _notVisibleWithoutLoggingIn(String url) {
		return ! _visibleWithoutLoggingIn(url);
	}
	
	/**
	 * 
	 * Check whether a URL is visible before logging in.
	 * The result depends on the inputs sequences under test.
	 * 
	 * @param url The url to be checked.
	 * @return true if the URL is visible before logging in
	 */
	private static boolean _visibleWithoutLoggingIn(String url) {
		try {
			return !MR.CURRENT.provider.notVisibleWithoutLoggingIn(url);
		} catch ( Throwable t ) {

			if ( visibleWithoutLogin == null ) {
				visibleWithoutLogin = new HashSet<String>();
				MrDataDB<Input> inputsDB = MR.CURRENT.inputsDB();
				Iterator<smrl.mr.language.Input> it = inputsDB._it();

				while ( it.hasNext() ) {
					smrl.mr.language.Input next = it.next();
					for ( Action a : next.actions() ) {
						if ( isLogin(a) ) {
							break;
						}
						if(a.getUrl()!=null) {
							visibleWithoutLogin.add(a.getUrl());
						}
					}
				}
			}

			return visibleWithoutLogin.contains(url);
		}
	}
	
	/**
	 * Web-specific function.
	 * Returns arrays of indexes of parameters relevant to user group in the URL of an action.
	 * 
	 * @param action1 The action to be processed
	 * @return The array of indexes (in integer if exists)
	 */
	public static int[] extractUserGroupParameters(Action action1) {
		return MR.CURRENT.provider.extractUserRoleParameters(action1 );
	}
	
	/**
	 * Web-specific function.
	 * Check if the URL of the x-th action in the input sequence changes over multiple executions. 
	 * 
	 * @param input The input sequence to check.
	 * @param x The index of the action to check.
	 * @return true if the the URL of the x-th action changes over multiple executions
	 */
	public static boolean urlOfActionChangesOverMultipleExecutions(Input input, int x) {
		return _urlOfActionChangesInDifferentExecutions(input,x);
	}
	
	/**
	 * Web-specific function.
	 * Check if the URL of an action changes over multiple executions. 
	 * 
	 * @param a The action to be checked.
	 * @return true if the the URL of the action changes over multiple executions.
	 */
	public static boolean urlOfActionChangesOverMultipleExecutions(Action a) {
		return _urlOfActionChangesInDifferentExecutions(a.getInput(),a.getPosition());
	}
	
	private static HashSet<Action> urlChangesOverMultipleExecutions;
	
	/**
	 * 
	 * Check if the URL of the action at the pos position in the input sequence changes over different executions. 
	 * 
	 * @param i The input sequence to check.
	 * @param pos The index of the action to check.
	 * @return true if the the URL of the pos-th action changes over different executions
	 */
	private static boolean _urlOfActionChangesInDifferentExecutions(Input i, int pos) {
		try {
			return MR.CURRENT.provider.urlOfActionChangesInDifferentExecutions(i,pos);
		} catch ( Throwable t ) {

			if ( urlChangesOverMultipleExecutions == null ) {
				urlChangesOverMultipleExecutions = new HashSet<Action>();
				
				HashMap<Action, String> actionsURLs = new HashMap<Action,String>();
				
				MrDataDB<Input> inputsDB = MR.CURRENT.inputsDB();
				Iterator<smrl.mr.language.Input> it = inputsDB._it();

				while ( it.hasNext() ) {
					smrl.mr.language.Input next = it.next();
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
	
	/**
	 * Web-specific function.
	 * Returns a LogoutInAnotherTab action.
	 * The LogoutInAnotherTab action will open a new tab in the web browser, then execute the logout action.
	 * @return The LogoutInAnotherTab action
	 */
	public static Action LogoutInAnotherTab() {
		return MR.CURRENT.provider.LogoutInAnotherTab();
	}
	
	/**
	 * Web-specific function.
	 * Returns a wait action, which makes the framework sleep during the given time length "millis"
	 * @param millis The time length in millisecond
	 * @return The Wait action
	 */
	public static Action Wait(long millis) {
		return new WaitAction(millis);
	}
	
	/**
	 * Web-specific function.
	 * Check whether the "action" is for reading an email
	 * 
	 * @param action The action to be checked
	 * @return true if the action is for reading an email
	 */
	public static boolean isReadEMailAction(Action action) {
		return MR.CURRENT.provider.isReadEMailAction(action);
	}
	
	
	/**
	 * Data Representation Function.
	 * Return a file at the 1st file, with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static Object File() {
		return MR.CURRENT.getMRData("File",1);
	}
	
	/**
	 * Web-specific function.
	 * Checks whether the "user" cannot reach one of URL in a input sequence through his graphic user interface. 
	 * 
	 * @param user The user to check
	 * @param lastURL The input sequence to check
	 * @return true if the "user" cannot reach one or more URL(s) from the input sequence lastURL
	 */
	public static boolean cannotReachThroughGUI(Object user, Input lastURL){
		return MR.CURRENT.provider.cannotReachThroughGUI(user,lastURL);
	}
	
	/**
	 * Web-specific function.
	 * Returns true if a URL cannot be reached by the given user by exploring the user interface of the system (e.g., by traversing anchors).
	 * The result depends on the data collected by the data collection method.
	 * 
	 * @param user
	 * @param URL
	 * @return	true if the user cannot reach the given URL
	 */
	public static boolean cannotReachThroughGUI(Object user, String URL){
		boolean res = MR.CURRENT.provider.cannotReachThroughGUI(user, URL);
		
//		if ( res ){
//			System.out.println("!!!!Cannot reach (" + user + ", "+URL +")"); 
//		}
		
		return res;
	}
	
	/**
	 * Data Representation Function.
	 * Returns the output of the i-th element of an input sequence
	 * 
	 * @param input
	 * @param pos
	 * @return
	 */
	public static Output Output(Input input, int pos){
		MR.CURRENT.setLastInputProcessed( input,  pos );
		return MR.CURRENT.provider.Output(input,pos);
	}

	/**
	 * Data Representation Function.
	 * Returns the output produced by the last action in an input sequence
	 * 
	 * @param input
	 * @return
	 */
	public static Output Output(Input input){
		MR.CURRENT.setLastInputProcessed( input,  -1 );
		return MR.CURRENT.provider.Output(input);
	}
	
	/**
	 * Web-specific function.
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
	
	/**
	 * Data Representation Function.
	 * Returns a random file path.
	 * 
	 * @param x The x-th file path, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Object RandomFilePath(int x){ 
//		return MR.CURRENT.getMRData("RandomValue:"+Path.class.getCanonicalName(),x);
		return MR.CURRENT.getMRData("RandomFilePath",x);
	}
	
	/**
	 * Data Representation Function.
	 * Returns a random file path related to the administrator.
	 * 
	 * @param x The x-th admin file path, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Object RandomAdminFilePath(int x){ 
//		return MR.CURRENT.getMRData("RandomValue:"+Path.class.getCanonicalName(),x);
		return MR.CURRENT.getMRData("RandomAdminFilePath",x);
	}
	
	/**
	 * Data Representation Function.
	 * Returns a random HTTP method (GET, POST, PUT, HEAD,...)
	 * 
	 * @return
	 */
	@MRDataProvider
	public static String HttpMethod(){ 
		return RandomHttpMethod(1);
	}
	
	/**
	 * Data Representation Function.
	 * Returns the 1st random HTTP method (GET, POST, PUT, HEAD,...), with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static String RandomHttpMethod(){ 
		return RandomHttpMethod(1);
	}
	
	/**
	 * Data Representation Function.
	 * Returns the 1-th random file path, with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static Object RandomFilePath(){ 
		return RandomFilePath(1);
	}
	
	/**
	 * Data Representation Function.
	 * Returns the 1-th random admin file path, with respect to the current data view.
	 * 
	 * @return
	 */
	@MRDataProvider
	public static Object RandomAdminFilePath(){ 
		return RandomAdminFilePath(1);
	}
	
	/**
	 * Data Representation Function.
	 * Returns the x-th random HTTP method (GET, POST, PUT, HEAD,...), with respect to the current data view.
	 * 
	 * @param x The x-th method, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static String RandomHttpMethod(int x){ 
//		return (String) MR.CURRENT.getMRData("RandomHttpMethod",x);
		return (String) MR.CURRENT.getMRData("HttpMethod",x);
	}
	
	
	/**
	 * Web-specific function.
	 * Changes the protocol in the URL of the action a.
	 * 
	 * @param protocol The protocol to use.
	 * @param a The action to be processed.
	 * @return true if the change is successful.
	 */
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
	
	/**
	 * Web-specific function.
	 * Checks whether the parameter at the parpos position of the action a is for user.
	 * 
	 * @param a The action to check.
	 * @param parpos The position of the parameter in the array of parameters of the action a
	 * @param user The user containing username, password parameters, which is used to check.
	 * @return true if the parameter is for user.
	 */
	public static boolean isUserIdParameter( Action a, int parpos, Object user ){
		return MR.CURRENT.provider.isUserIdParameter(a,parpos,user);
	}
	
	
	/**
	 * Web-specific function.
	 * Returns a Login action with the given credential "user"
	 * @param user The credential used for logging in
	 * @return The Login action with the credential "user"
	 */
	public static Action LoginAction(Object user) {
		return MR.CURRENT.provider.newLoginAction(user);
	}

	/**
	 * Web-specific function.
	 * Checks whether the "action" is a sign-up action.
	 * The result depends on the sign-up URL specified in the configuration.
	 * 
	 * @param action The action to check.
	 * @return true if the action is the sign-up action.
	 */
	public static boolean isSignup(Action action) {
		return MR.CURRENT.provider.isSignup(action);
	}

	/**
	 * Web-specific function.
	 * Updates the value in a form input.
	 * 
	 * @param formInput The form input to update value.
	 * @param value The value used to update.
	 * @return true if the updating is successful.
	 */
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
			MR.CURRENT.setConsiderParameters();
			return true;
		}
		return false;
	}

	/**
	 * Web-specific function.
	 * Combine a path with a given URL.
	 * @param url The original URL to combine.
	 * @param addedPath The path to add.
	 * @return The URL after combining.
	 */
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
	
	static HashMap<String,HashSet<String>> triedInputs = new HashMap<String,HashSet<String>>();
	
	
	
	/**
	 * Web-specific function.
	 * Checks whether a given user tried to access a given URL until the current execution period.
	 *  
	 * @param user The user to check.
	 * @param url The URL to be checked.
	 * @return true if the "user" did not try to access "url".
	 */
	public static boolean notTried(Object user, String url) {
		if(!(user instanceof Account)) {
			return true;
		}
		
		String username = ((Account)user).getUsername();
		
		HashSet<String> setOfInputs = triedInputs.get(username);
		
		if ( setOfInputs == null ) {
			setOfInputs = new HashSet<String>();
			triedInputs.put( username, setOfInputs );
		}
		
		if ( setOfInputs.contains(url) ) {
			return false;
		}
		
		setOfInputs.add(url);
		
		return true;
	}
	
	public static boolean notTried(Object user, Object... others) {
		if(!(user instanceof Account)) {
			return true;
		}
		
		if(others.length<1) {
			return false;
		}
		
		String username = ((Account)user).getUsername();
		
		HashSet<String> setOfInputs = triedInputs.get(username);
		
		if ( setOfInputs == null ) {
			setOfInputs = new HashSet<String>();
			triedInputs.put( username, setOfInputs );
		}
		
		String checkedString = "";
		for(Object str:others) {
			checkedString += str + "_";
		}
		
		if(checkedString.endsWith("_")) {
			checkedString = checkedString.substring(0, checkedString.length()-1);
		}
		
		if ( setOfInputs.contains(checkedString) ) {
			return false;
		}
		
		setOfInputs.add(checkedString);
		
		return true;
	}
	
	/**
	 * Web-specific function.
	 * Checks whether a given user is an administrator.
	 * The result is based on the information in the configuration file.
	 * 
	 * @param user The user to check.
	 * @return true if the "user" is an administrator.
	 */
	public static boolean isAdmin( Object user) {
		return MR.CURRENT.provider.isAdmin( user );
	}
	
	/**
	 * Web-specific function.
	 * Checks if a form input is for a file path. 
	 * A form input value is considered as a file path, if it contains a file extension.  
	 * 
	 * @param formInput the form input to be checked.
	 * @return true if the form input is for file path.
	 */
	public static boolean isFormInputForFilePath(Object formInput) {
		return MR.CURRENT.provider.isFormInputForFilePath(formInput);
	}
	
	/**
	 * Web-specific function.
	 * Checks if user1 is a supervisor of user2.
	 * E.g., an administrator is a supervisor of a normal user.
	 * @param user1, the user who is expected as a supervisor
	 * @param user2, the user who is supervised by user1
	 * @return true if user1 is a supervisor of user2 
	 */
	public static boolean isSupervisorOf(Object user1, Object user2) {
		return MR.CURRENT.provider.isSupervisorOf(user1, user2);
	}
	
	/**
	 * Web-specific function.
	 * Checks whether the output contains an error message (e.g., HTTP 404)
	 * @param output is the output of a sequence of actions
	 * @return true if the output contains an error message
	 */
	public static boolean isError(Object output) {
		return MR.CURRENT.provider.isError(output);
	}

	/**
	 * Data Representation Function.
	 * Returns the 1-th parameter value used by other users
	 * 
	 * @return
	 */
	@MRDataProvider
	public static String parameterValueUsedByOtherUsers(Action action, int parPosition) {
		String dbName = MR.CURRENT.getParameterValueUsedByOtherUsers_DBName(action, parPosition);
		if (dbName==null) {
			return null;
		}
		
		//check if the mrDatabase contains dbName
		
		if(MR.CURRENT.getDataDB(dbName)==null) {
			return null;
		}
		
		return MR.CURRENT.getMRData(dbName,1).toString();
	}
	
	public static Object randomFilePath(int x){ 
//		return MR.CURRENT.getMRData("RandomValue:"+Path.class.getCanonicalName(),x);
		return MR.CURRENT.getMRData("RandomFilePath",x);
	}
	
	public static int randomFilePathSize(){ 
//		return MR.CURRENT.getMRData("RandomValue:"+Path.class.getCanonicalName(),x);
		return MR.CURRENT.getMRDataSize("RandomFilePath");
	}

}

