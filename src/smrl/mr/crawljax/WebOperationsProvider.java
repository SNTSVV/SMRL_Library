package smrl.mr.crawljax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;

import smrl.mr.language.Action;
import smrl.mr.language.CookieSession;
import smrl.mr.language.Input;
import smrl.mr.language.Operations;
import smrl.mr.language.OperationsProvider;
import smrl.mr.language.Output;
import smrl.mr.language.SystemConfig;
import smrl.mr.language.actions.StandardAction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class WebOperationsProvider implements OperationsProvider {
	WebProcessor impl;
	HashMap<WebInputCrawlJax, WebOutputSequence> outputCache = new HashMap<WebInputCrawlJax, WebOutputSequence>();
	
	HashMap<String, HashMap<String, WebOutputCleaned>> outputStore = 
			new HashMap<String, HashMap<String, WebOutputCleaned>>(); 
	
	public WebOperationsProvider(String configFile) {
		impl = new WebProcessor();
		
		if(configFile!=null && !configFile.isEmpty()){
			impl.setConfig(configFile);
		}
		
		try {
			impl.loadInput(WebProcessor.getSysConfig().getInputFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		impl.setOutputFile(WebProcessor.getSysConfig().getOutputFile());
		
		impl.loadUsers();
		
		try {
			impl.loadRandomFilePath(impl.getSysConfig().getRandomFilePathFile());
			impl.loadRandomAdminFilePath(impl.getSysConfig().getRandomAdminFilePathFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public WebOperationsProvider(String inputFile, String outFile, String configFile) {
		impl = new WebProcessor();
		
		if(configFile!=null && !configFile.isEmpty()){
			impl.setConfig(configFile);
		}
		
		try {
			impl.loadInput(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		impl.setOutputFile(outFile);
		
		
		
		impl.loadUsers();
	}
	
	public WebOperationsProvider(String inputFile, String outFile, String configFile, String randomFilePath, String randomAdminFilePath) {
		this(inputFile, outFile, configFile);
		
		try {
			
			impl.loadRandomFilePath(randomFilePath);
			impl.loadRandomAdminFilePath(randomAdminFilePath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean loadOutputStore(){
		if(outputStore!=null && !outputStore.isEmpty()){
			return true;		//already loaded
		}
		
		outputStore = new HashMap<String, HashMap<String, WebOutputCleaned>>(); 
		
		String outputStoreFolder = WebProcessor.getSysConfig().getOutputStore();
		
		//check if the outputStoreFolder exist
		File outFolder = new File(outputStoreFolder);
		if(!outFolder.exists() || !outFolder.isDirectory()){
			return false;	// The directory does not exist
		}
		
		File[] subFolders = outFolder.listFiles();
		
		boolean loaded = false;
		for(File sf:subFolders){
			//Try to get outputs of each user
			
			//if this sf is not a directory -> continue
			if(!sf.isDirectory()){
				continue;
			}
			
			//Start to load all outputs of the current username/Anonymous
			String username = sf.getName();
			HashMap<String, WebOutputCleaned> lsOutput = new HashMap<String, WebOutputCleaned>();
			
			File[] filesList = sf.listFiles();
			for(File f:filesList){
				if(!f.isFile()){
					continue;
				}
				
				//1. get type of output (html/text)
				String fullFileName = f.getName();
				String extension = fullFileName;
				if(extension.toLowerCase().endsWith("html")){
					extension = "html";
				}
				else{
					continue;
				}
				
				//2. get the content of output
				String contentHTML = null;
				String htmlFileName = f.getAbsolutePath();
				contentHTML = readFile(htmlFileName);
				
				String contentText = null;
				String textFileName = htmlFileName.substring(0, htmlFileName.length()-4) + "txt";
				contentText = readFile(textFileName);
				
				
				if(contentHTML!=null || contentText!=null){
					String filename = fullFileName.substring(0, fullFileName.length()-5); //-5 because this is html file
					
					WebOutputCleaned outCleaned = new WebOutputCleaned();
					outCleaned.html = contentHTML;
					outCleaned.text = contentText;
					
					
					lsOutput.put(filename, outCleaned);
				}

			}
			if(!lsOutput.isEmpty()){
				outputStore.put(username, lsOutput);
			}
			
			loaded = true;
		}
		
		return loaded;
	}

	/**
	 * @param content
	 * @param htmlFileName
	 * @return
	 */
	private String readFile(String htmlFileName) {
		String content = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(htmlFileName));
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			boolean firstLine = true;
			while(line!=null){
				if(!firstLine){
					sb.append(System.lineSeparator());
				}
				firstLine = false;
				sb.append(line);
				line = reader.readLine();
			}
			reader.close();
			content = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	@Override
	public boolean isLogin(Action action) {
		return impl.isLogin(action);
	}

	public List<Account> loadUsers() {
		//FIXME
		return impl.userIterator();
	}

	public List<Action> loadActions() {
		List<WebInputCrawlJax> inps = impl.getInputList();
		LinkedList<Action> actions = new LinkedList<>();
		for (WebInputCrawlJax i : inps) {
			for ( Action a : i.actions() ) {
				actions.add(a);
			}
		}
		
		return actions;
	}
	
	public List<Action> loadActionsAvailableWithoutLogin() {
		List<WebInputCrawlJax> inps = impl.getInputList();
		LinkedList<Action> actions = new LinkedList<>();
		for (WebInputCrawlJax i : inps) {
			boolean loggedIn = false;
			boolean isLogOut = true;
			for ( Action a : i.actions() ) {
				if ( isLogin(a) ) {
					loggedIn = true;
					isLogOut = false;
					continue;
//					break;
				}
				
				isLogOut = isLogout(a);
				if(isLogOut) {
					loggedIn = false;
					continue;
				}
				
				if(!loggedIn && !isLogOut &&
						!containActionURL(actions, a) &&
						a.getUrl()!=null &&
						!a.getUrl().isEmpty() &&
						!impl.sysConfig.isLoginURL(a.getUrl()) &&
						!isEmptyUrl(a.getUrl())) {
					actions.add(a);
				}
			}
		}
		
		return actions;
	}

	private boolean isEmptyUrl(String url) {
		if(url==null || url.isEmpty() || 
				url.equals("/") ||
				url.equals("#")) {
			return true;
		}
		return false;
	}

	private boolean containActionURL(LinkedList<Action> actions, Action act) {
		if(actions==null || act==null || 
			actions.size()<1 ||
			act.getUrl()==null ||
			act.getUrl().isEmpty()) {
			return false;
		}
		
		for(Action a:actions) {
			if(a.getUrl()!=null && !a.getUrl().isEmpty() &&
					a.getUrl().equals(act.getUrl())) {
				return true;
			}
		}
		
		return false;
	}

	public List<WebInputCrawlJax> loadInputs() {
		return impl.getInputList();
	}
	
	private List loadRandomFilePath() {
		return impl.getRandomFilePath();
	}
	
	private List loadRandomAdminFilePath() {
		return impl.getRandomAdminFilePath();
	}

	@Override
	public smrl.mr.language.Input changeCredentials(smrl.mr.language.Input input, Object user) {
		return impl.changeCredential((WebInputCrawlJax) input, (Account) user);
	}

	@Override
	public boolean cannotReachThroughGUI(Object user, smrl.mr.language.Input input) {
		return impl.guiNotContain((Account) user, (WebInputCrawlJax) input);
	}
	
	@Override
	public boolean cannotReachThroughGUI(Object user, String URL) {
		return impl.guiNotContain((Account) user, URL);
	}

	@Override
	public Object Session(smrl.mr.language.Input input, int x) {
		return Operations.Output(input, x).getSession();
	}

	@Override
	public Action DeleteCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List load(String dataName) {
		switch ( dataName ){
		case "Input":
			return loadInputs();
		case "Action":
			return loadActions();
		case "ActionAvailableWithoutLogin":
			return loadActionsAvailableWithoutLogin();
		case "User":
			return loadUsers();
		case "RandomFilePath":
			return loadRandomFilePath();
		case "RandomAdminFilePath":
			return loadRandomAdminFilePath();
		}
		return null;
	}


	@Override
	public boolean notVisibleWithoutLoggingIn(String url) {
		return impl.notVisibleWithoutLoggingIn(url);
		
	}

	@Override
	public int[] parametersWithDifferentValues(Action action1, Action action2) {
		return new int[0];
	}

	@Override
	public boolean urlOfActionChangesInDifferentExecutions(smrl.mr.language.Input input, int x) {
		if(input.actions()==null || input.actions().get(x)==null) {
			return false;
		}
		
		WebInputCrawlJax actionsChangedUrl = impl.getActionsChangedUrl();
		
		if(actionsChangedUrl==null ||
				actionsChangedUrl.size()<1) {
			return false;
		}
		
		try {
			Action comparedAction = input.actions().get(x).clone();
			comparedAction.setUser(null);
			for(Action act: actionsChangedUrl.actions()) {
				Action act1 = act.clone();
				act1.setUser(null);

				if(act1.equals(comparedAction)) {
					return true;
				}
			}
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		
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
	public boolean userCanRetrieveContent(Object user, Object output) {
		if(loadOutputStore()==false || output==null){
			return false;
		}

		String username = ((Account)user).getUsername();
		
		//if output is an instance of smrl.mr.language.Output
		if(output instanceof smrl.mr.language.Output) {
			ArrayList<Object> outSequence = ((WebOutputSequence)output).getOutputSequence();
			
			//check Anonymous
			if(userCanRetrieve("ANONYMOUS", outSequence)){
				return true;
			}

			//Check username
			if(username!=null && !username.isEmpty() && 
					userCanRetrieve(username, outSequence)){
				return true;
			}
		}
		
		// if output is an instance of File
		if(output instanceof File) {
			try {
				String fileContent = FileUtils.readFileToString((File)output, Charsets.UTF_8);
				
				//check Anonymous
				if(userCanRetrieveContent("ANONYMOUS", fileContent)){
					return true;
				}

				
				return userCanRetrieveContent(username, fileContent);
					
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return false;
	}

	private boolean userCanRetrieve(String username, ArrayList<Object> outSequence) {
		if(username==null || username.isEmpty() || this.outputStore.isEmpty() || !this.outputStore.containsKey(username)){
			return false;
		}
		
		HashMap<String, WebOutputCleaned> allOutputs = this.outputStore.get(username);
		for(String key:allOutputs.keySet()){
			WebOutputCleaned storedOutput = allOutputs.get(key);
			for(Object out:outSequence){
				WebOutputCleaned newOutput = (WebOutputCleaned)out;
				if(storedOutput.compare(newOutput)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean userCanRetrieveContent(String username, String content) {
		if(username==null || username.isEmpty() || this.outputStore.isEmpty() || !this.outputStore.containsKey(username)){
			return false;
		}
		HashMap<String, WebOutputCleaned> allOutputs = this.outputStore.get(username);
		for(String key:allOutputs.keySet()){
			WebOutputCleaned storedOutput = allOutputs.get(key);
			
			if(storedOutput.compare(content)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean notAnonymous(Object user) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isEncrypted(Action action) {
		String channel = action.getChannel();
		
		if(channel!=null && channel.trim().toLowerCase().equals("https")){
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see smrl.mr.language.OperationsProvider#Output(smrl.mr.language.Input, int)
	 */
	@Override
	public smrl.mr.language.Output Output(Input input, int pos) {
		if(pos<0){
			return null;
		}
		
		boolean CACHED = outputCache.containsKey(input);
//		System.out.println("\t!!! CACHED:"+CACHED+" "+System.identityHashCode(input)+" "+input);
		if(CACHED){
			WebOutputSequence res = new WebOutputSequence();
			
			ArrayList<Object> listOutput = outputCache.get(input).getOutputSequence();
			
			if(listOutput.size() <= pos){
				int size = listOutput.size();
				res.add(listOutput.get(size-1));
				res.addRedirectURL(outputCache.get(input).redirectURL(size-1));
				res.addSession((CookieSession) outputCache.get(input).getSession(size-1));
				return res;
			}
			else{
				res.add(listOutput.get(pos));
				res.addRedirectURL(outputCache.get(input).redirectURL(pos));
				res.addSession((CookieSession) outputCache.get(input).getSession(pos));
//				res.add(listOutput.get(pos-1));
//				res.addRedirectURL(outputCache.get(input).redirectURL(pos-1));
				return res;
			}
		}
		
		//request the web server before get the output at the pos
		Output(input);
		return Output(input, pos);
	}

	@Override
	public Output Output(Input input) {
		if(outputCache.containsKey(input)){
			return (WebOutputSequence) outputCache.get(input);
		}
		
		//else, send requests to web server then get result
		WebOutputSequence s = impl.output((WebInputCrawlJax) input);
		
		if(s!= null){
			outputCache.put((WebInputCrawlJax) input, s);
		}
		return s;
	}


	@Override
	public void nextTest() {
		//this method should reset the cache in which we keep teh output of teh executed tests
		this.outputCache.clear();
		impl.resetUpdateUrlMap();
//		throw new NotImplementedException();
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
		if( a.getParameters()==null ||
				parpos<0 ||
				parpos>=a.getParameters().size() ||
				user==null ||
				!(user instanceof Account)) {
			return false;
		}
		
		Entry<String, String> par = a.getParameters().get(parpos);
		Account uAcc = (Account) user;
		
		if(par.getKey()!=null &&
				uAcc.getUsernameParam()!=null &&
				par.getKey().equals(uAcc.getUsernameParam())) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isLogout(Action action) {
		return impl.sysConfig.isLogoutUrl(action.getUrl());
	}

	@Override
	public Input Input(Action[] as) {
		if(as.length <1){
			return null;
		}
		
		WebInputCrawlJax res = null;
		
		for(int i=0; i<as.length; i++){
			if(i==0){
				try {
					res = new WebInputCrawlJax(as[i].clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					res.addAction(as[i].clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(res!=null){
//			ArrayList<LoginParam> allLoginParams = WebProcessor.sysConfig.getLoginParams();
//			LoginParam usedLoginParam = null;
//			
//			for(Action a:res.actions()) {
//				if(a instanceof StandardAction) {
//					usedLoginParam = ((StandardAction)a).usedLoginParam(allLoginParams);
//				}
//				if(usedLoginParam!=null) {
//					break;
//				}
//			}
//
//			if(usedLoginParam!=null){
//				res.identifyUsers(usedLoginParam.userParam, usedLoginParam.passwordParam, impl);
//			}
			res.identifyUsers(impl);
		}
		
		return res;
	}


	@Override
	public Action newLoginAction(Object user) {
		Action res = null;
		boolean found = false;
		for(WebInputCrawlJax input:impl.getInputList()){
			for(int i=0; i<input.actions().size(); i++){
				Action act = input.actions().get(i);
				if(Operations.isLogin(act)){
					smrl.mr.language.Input tempInput = Operations.Input(act);
					try {
						res = Operations.changeCredentials(tempInput, user).actions().get(0).clone();
						
						found = true;
						break;
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
			if(found){
				break;
			}
		}
		
		return res;
	}
	
	@Override
	public Action newLoginAction(WebInputCrawlJax input, Object user) {
		if(input==null || user==null) {
			return null;
		}
		
		Action res = null;

		for(int i=0; i<input.actions().size(); i++){
			Action act = input.actions().get(i);
			if(Operations.isLogin(act)){
				smrl.mr.language.Input tempInput = Operations.Input(act);
				try {
					res = Operations.changeCredentials(tempInput, user).actions().get(0).clone();
					break;
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}

	@Override
	public smrl.mr.language.Input Input(Action action) {
		if(impl.getInputList() != null)
		{
			 try {
				Object res = null;
				res = impl.getInputList().get(0).getClass().newInstance();
				((Input)res).addAction(action);
				return (Input)res;
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	
	@Override
	public boolean isSignup(Action action) {
		return impl.isSignup(action);
	}

	@Override
	public smrl.mr.language.Input Input(List<Action> actions) {
		return new WebInputCrawlJax(actions);
	}

	@Override
	public boolean isAdmin(Object user) {
		
		
		if ( user.equals(impl.getAdmin()) ) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isFormInputForFilePath(Object fi) {
		if ( fi instanceof JsonObject ) {
			return StandardAction.isFormInputForFilePath((JsonObject) fi);
		}
		return false;
	}

	public boolean coverAllUrls(String username1, String username2) {
		return impl.coverAllUrls(username1, username2);
	}

	@Override
	public boolean isSupervisorOf(Object user1, Object user2) {
		return impl.isSupervisorOf(user1, user2);
	}

	@Override
	public boolean isError(Object output) {
		return impl.isError(output);
	}

	@Override
	public ArrayList<Action> actionsUpdatedUrl() {
		return impl.actionsUpdatedUrl();
	}

	@SuppressWarnings("static-access")
	@Override
	public SystemConfig getSysConfig() {
		if(impl!=null) {
			return impl.sysConfig;
		}
		return null;
	}

	public WebProcessor getWebProcessor() {
		return impl;
	}

	@Override
	public void resetProxy() {
		this.impl.resetProxy();
		
	}
	

}
