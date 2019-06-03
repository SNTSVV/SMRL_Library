package smrl.mr.language;

import java.util.ArrayList;
import java.util.HashMap;

public class MrDataDBRandom extends MrDataDB<Object> {

	public MrDataDBRandom(String dbName) {
		super(dbName);
		LEN=100;
	}


	HashMap<Class, ArrayList<Object>> typesDB = new HashMap<>();
	public Object get(Class type, int i) {
		ArrayList<Object> db = typesDB.get(type);
		if ( db == null ) {
			db = populateDB( type );
		}
		
		int pos = (START+i-1) % LEN;
		
		Object data = db.get(pos);
		
		String key = dbName+"("+i+")";
		generatedData.put(key, data);
		
		return data;
	}
	
	
	private ArrayList<Object> populateDB(Class type) {
		ArrayList<Object> db = new ArrayList<>();
		if ( type == String.class ) {
			for ( int i = 0; i < LEN; i++ ) {
				int val = (int) Math.floor( Math.random()*100 );
				db.add(val);
			}
		} else if ( type == Integer.class ) {
			for ( int i = 0; i < LEN; i++ ) {
				int val = (int) Math.floor( Math.random()*100 );
				db.add(val);
			}
		} else if ( type == Double.class ) {
			for ( int i = 0; i < LEN; i++ ) {
				double val = Math.random()*100 ;
				db.add(val);
			}
		}
		return db;
	}
	
	

}
