package smrl.mr.language;

import static smrl.mr.language.Operations.Input;

import java.util.ArrayList;

import smrl.mr.crawljax.WebInputCrawlJax;

public class DBPopulator extends MR {
	
	private OutputDBFiller db;

	public DBPopulator(OutputDBFiller db){
		this.db = db;
	}

	@Override
	public boolean mr() {
		for ( Action a : Input(1).actions() ) {
			Object user = a.getUser();
			Output output = Operations.Output(Input(1),a.getPosition());
			db.store( user, output );
		}
		
		
		return true;
	}
	
	public void exportActionsChangedUrl() {
		ArrayList<Action> actUpdatedUrl = provider.actionsUpdatedUrl();

		//Create the list of actions which changed URL after multiple executions
		if(actUpdatedUrl==null || actUpdatedUrl.isEmpty()) {
			return;
		}

		WebInputCrawlJax inp = new WebInputCrawlJax(actUpdatedUrl);
		SystemConfig sysCon = provider.getSysConfig();

		if(sysCon!=null &&
				sysCon.getActionsChangedUrlFileName()!=null &&
				!sysCon.getActionsChangedUrlFileName().isEmpty()) {
			inp.exportToFile(sysCon.getActionsChangedUrlFileName());
		}
	}
	
}