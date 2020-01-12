package smrl.mr.language;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import smrl.mr.crawljax.Account;
import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.crawljax.WebOutputCleaned;
import smrl.mr.crawljax.WebOutputSequence;

public class OutputDBFiller {
	
	private File destFolder;
	
	public OutputDBFiller( File destFolder ) {
		this.destFolder = destFolder;
	}

	public static void main(String[] args) {
		String outFolder = "./testData/Jenkins/outputStore";
		String sysConfigFile = "./testData/Jenkins/jenkinsSysConfig.json";
		
//		if(args!=null && args.length>=2) {
//			outFolder = args[0].trim();
//			sysConfigFile = args[1].trim();
//		}
//		else {
//			System.out.println("Usage: " + OutputDBFiller.class.getSimpleName() + 
//					" <path_of_output_store> <path_to_system_config_file>");
//			return;
//		}
		
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/edlah2/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/jenkins-1/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/jenkins-agentLog/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/jenkins/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/Jenkins/simple/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/Jenkins/fullWithAnonym/outputStore"));
		OutputDBFiller db = new OutputDBFiller(new File(outFolder));
		
		
		DBPopulator mr = new DBPopulator(db);
		
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/edlah2/edlah2Sysconfig.json");
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/jenkins-1/jenkinsSysconfig.json");
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/jenkins-agentLog/jenkinsSysconfig.json");
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/jenkins/jenkinsSysconfig.json");
		WebOperationsProvider provider = new WebOperationsProvider(sysConfigFile);
		
		mr.setProvider(provider);
		
		
		mr.run();
		
		//should be populated
	}

	int counter = 0;
	public void store(Object user, Output output) {
		Account _user = (Account) user;
		
		WebOutputSequence seq = (WebOutputSequence) output;
		
		WebOutputCleaned pageOut = (WebOutputCleaned) seq.getOutputAt(0);
		
		String userName = null;
		
		if ( _user.isAnonymous() ){
			userName = "ANONYMOUS";
		} else {
			userName = _user.getUsername();
		}
		
		File userFolder = new File ( destFolder, userName );
		userFolder.mkdirs();
		
		int id = (counter++);
		
		File destHtml = new File( userFolder, "output_"+id+".html" );
		File destText = new File( userFolder, "output_"+id+".txt" );
		
		store(userFolder, destHtml, pageOut.html );
		
		store(userFolder, destText, pageOut.text );
		
	}

	private void store(File folder, File destHtml, String html) {
		if(alreadyStored(folder, html)) {
			System.out.println("!!! already stored: " + destHtml.getAbsolutePath());
		}
		
		//TODO: in the near future, should use alreadyStored function to filter content to be stored
		try {
			FileWriter w = new FileWriter( destHtml );
			w.write(html);
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean alreadyStored(File folder, String content) {
		if(!folder.exists() || !folder.isDirectory()) {
			return false;
		}
		
		for(File file:folder.listFiles()) {
			String fileContent = readLines(file.getAbsolutePath());
			if(fileContent.equals(content)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static String readLines(String filePath) 
	{
		String content = "";

		try
		{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return content;
	}

}
