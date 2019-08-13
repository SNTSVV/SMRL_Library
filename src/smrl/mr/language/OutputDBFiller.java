package smrl.mr.language;

import static smrl.mr.language.Operations.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/edlah2/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/jenkins-1/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/jenkins-agentLog/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/OTG_AUTHZ_002/jenkins/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/Jenkins/simple/outputStore"));
//		OutputDBFiller db = new OutputDBFiller(new File("./testData/Jenkins/fullWithAnonym/outputStore"));
		OutputDBFiller db = new OutputDBFiller(new File("./testData/Jenkins/outputStore"));
		
		
		DBPopulator mr = new DBPopulator(db);
		
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/edlah2/edlah2Sysconfig.json");
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/jenkins-1/jenkinsSysconfig.json");
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/jenkins-agentLog/jenkinsSysconfig.json");
//		WebOperationsProvider provider = new WebOperationsProvider("./testData/OTG_AUTHZ_002/jenkins/jenkinsSysconfig.json");
		WebOperationsProvider provider = new WebOperationsProvider("./testData/Jenkins/jenkinsSysConfig.json");
		
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
		
		store( destHtml, pageOut.html );
		
		store( destText, pageOut.text );
		
	}

	private void store(File destHtml, String html) {
		try {
			FileWriter w = new FileWriter( destHtml );
			w.write(html);
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
