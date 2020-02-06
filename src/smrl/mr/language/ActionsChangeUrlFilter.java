package smrl.mr.language;

import smrl.mr.crawljax.WebOperationsProvider;

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
