package ase2019.mr.language;

import static ase2019.mr.language.Operations.Input;

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
	
}