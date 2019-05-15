package ase2019.mr.language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class MrDataDB<D> {
	protected String dbName;
	
	public MrDataDB(String dbName){
		this.dbName = dbName;
	}
	
	protected HashMap<String,D> generatedData = new HashMap<String,D>();
	private HashMap<String,D> reassignedData = new HashMap<String,D>();
	
	public Iterator<D> _it() {
		return inputs.iterator();
	}
	
	public D get(int i) {
		D input = inputs.get( (START+i-1) % LEN );
		
		String key = dbName+"("+i+")";
		
		if ( generatedData.containsKey(key) ){
//			System.out.println("Returning existing data "+key);
			return generatedData.get(key);
		}
		
		try {
			
//			Method cloneM = input.getClass().getMethod("clone"); 
			
//			if ( cloneM == null ){
				if ( Modifier.isFinal(input.getClass().getModifiers() ) ){
					//we assume that we cannot alter the content of final classes
					//(it might not be the case but is safe in this context)
					//thus we return the class itself
					//it works for String, Integer, etc...
					//it might not be good for custom defined types
					
					return input;
				}
//			}
			Method cloneM = input.getClass().getMethod("clone"); 
			
			D _input = (D) cloneM.invoke(input);
			generatedData.put(key, _input);
			
			if ( _input instanceof MRData ){
				((MRData)_input).setID(key);
			}
			return _input;
//			return (D) ( ((Cloneable)input)).clone();
		
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void nextTest() {
		START++;
		generatedData.clear();
		reassignedData.clear();
	}

	private List<D> inputs;
	protected int LEN;
	protected int START;

	public void load(List<D> loadInputs) {
		inputs = loadInputs;
		LEN = inputs.size();
		START=0;
	}

	public boolean hasMore() {
		return START < LEN;
	}

	public void resetTestsCounter() {
		START=0;
	}

	public HashMap<String, D> getProcessedInputs() {
		return generatedData;
	}



	
	public boolean reassign(MRData lhs, MRData rhs) {
		if ( lhs.isReassignable() == false ){
			return false;
		}
//		System.out.println("!!!Reassigning "+lhs);
		MRData _lhs;
		try {
			_lhs = (MRData) rhs.clone();
			rhs.setAlreadyUsedInRHS();
			
			_lhs.id = lhs.id;
			_lhs.addReassignment( rhs );
			generatedData.put( lhs.id, (D)_lhs );
			reassignedData.put( lhs.id, (D)_lhs );
			
			return true;
		} catch (CloneNotSupportedException e) {
			return false;
		}
		
	}

	public boolean contains(MRData lhs) {
		return generatedData.containsKey(lhs.id);
	}
	
	public void cleanupReassignedData() {
		
		for (  Entry<String, D> e:  reassignedData.entrySet() ) {
//			System.out.println("Deleting "+e.getKey());
			//No need to set D as not reassigned because it should never be referenced again; anyway, it would be unsafe.
			generatedData.remove(e.getKey());	
		}
		
		
	}
}
