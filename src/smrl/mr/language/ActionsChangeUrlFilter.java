package smrl.mr.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.crawljax.WebOutputCleaned;
import smrl.mr.crawljax.WebOutputSequence;
import smrl.mr.crawljax.WebProcessor;
import smrl.mr.test.ReplicateInputs;

public class ActionsChangeUrlFilter {

	public static void main(String[] args) {
		String sysConfigFile = "./testData/Jenkins/jenkinsSysConfig.json";
		
		WebOperationsProvider provider = new WebOperationsProvider(sysConfigFile);
		
		MRForActionsChangedUrl mr = new  MRForActionsChangedUrl(provider);
		
		mr.run();
		
		mr.exportResultToFile();
		
		System.out.println("Done");
	}

}
