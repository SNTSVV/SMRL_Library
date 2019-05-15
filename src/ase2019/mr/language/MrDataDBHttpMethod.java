package ase2019.mr.language;

import java.util.ArrayList;
import java.util.HashMap;

import ase2019.mr.language.Action.HTTPMethod;

public class MrDataDBHttpMethod extends MrDataDB<Object> {

	private ArrayList db;


	public MrDataDBHttpMethod(String dbName) {
		super(dbName);
		db = new ArrayList<>();
		HTTPMethod[] vals = HTTPMethod.values();
		for ( HTTPMethod val : vals ) {
			db.add(val.toString());	
		}
		
		LEN=db.size();
	}


	
	public Object get(int i) {
		
		
		int pos = (START+i-1) % LEN;
		
		Object data = db.get(pos);
		
		String key = dbName+"("+i+")";
		generatedData.put(key, data);
		
		return data;
	}
	
	
	
	

}
