package smrl.mr.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import smrl.mr.crawljax.Account;
import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.crawljax.WebOutputCleaned;
import smrl.mr.crawljax.WebOutputSequence;
import smrl.mr.crawljax.WebProcessor;
import smrl.mr.language.Action;
import smrl.mr.language.Input;

public class ReplicateInputs {
	
//	static String configFile = "./testData/OTG_AUTHZ_002/jenkins-agentLog/jenkinsSysconfig.json";
//	static String configFile = "./testData/OTG_AUTHZ_002/jenkins-session-afterSignUp/jenkinsSysconfig.json";
//	static String configFile = "./testData/OTG_AUTHZ_002/jenkins-CVE-2018-1999003/jenkinsSysConfig.json";
	static String configFile = "./testData/OTG_AUTHZ_002/jenkins-CVE-2018-1000406/jenkinsSysConfig.json";
	static String destFolder;
	private static int counter=0;

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		WebProcessor processor = setupWebProcessor(configFile);
		
		List<WebInputCrawlJax> inputList = processor.getInputList();
		
		for(WebInputCrawlJax input:inputList){
			WebOutputSequence outSequence = processor.output(input);
			if(outSequence==null){
				continue;
			}
			
			destFolder = processor.getSysConfig().getOutputStore();
			
			
			store(input, outSequence);
		}
	}

	private static void store(Input inputSequence, WebOutputSequence outSequence) {
		//Note: number of items in input and output are always the same
		List<Action> actions = inputSequence.actions();

		for(int i = 0; i< actions.size(); i++){
			Action act = actions.get(i);
			WebOutputCleaned pageOut = (WebOutputCleaned)outSequence.getOutputAt(i);

			Account user = (Account)act.getUser();
			String username = user.getUsername();
			if(username==null || username.isEmpty()){
				username = "ANONYMOUS";
			}
			File userFolder = new File ( destFolder, username );
			userFolder.mkdirs();
			
			int id = (counter++);

			File destHtml = new File( userFolder, "output_"+id+".html" );
			File destText = new File( userFolder, "output_"+id+".txt" );

			storeToFile( destHtml, pageOut.html );
			storeToFile( destText, pageOut.text );
		}
	}

	private static void storeToFile(File destFile, String content) {
		try {
			FileWriter w = new FileWriter( destFile );
			w.write(content);
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	public static WebProcessor setupWebProcessor(String configFile) {
		WebProcessor processor = new WebProcessor();
		
		if(configFile!=null && !configFile.isEmpty()){
			processor.setConfig(configFile);
		}
		
		try {
			processor.loadInput(processor.getSysConfig().getInputFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		processor.setOutputFile(processor.getSysConfig().getOutputFile());
		
		processor.loadUsers();
		
		try {
			processor.loadRandomFilePath(processor.getSysConfig().getRandomFilePathFile());
			processor.loadRandomAdminFilePath(processor.getSysConfig().getRandomAdminFilePathFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return processor;
	}

}
