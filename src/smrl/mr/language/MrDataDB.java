package smrl.mr.language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MrDataDB<D> {
	Logger LOGGER = Logger.getLogger(MrDataDB.class.getName());
	
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
		
		
		String key = dbName+"("+i+")";
		
		logDataStatus(key);
		
		
		if ( generatedData.containsKey(key) ){
			System.out.println("!!!Returning existing data "+key);
			LOGGER.log(Level.FINE,"!!!Returning existing data "+key);
			return generatedData.get(key);
		}
		
		if ( reassignedData.containsKey(key) ){
			LOGGER.log(Level.FINE,"!!!Returning existing reassigned data "+key);
			return reassignedData.get(key);
		}
		
		LOGGER.log(Level.FINE,"\t!!!Referring to origimal data "+key);
		
		D input = inputs.get( (START+i-1) % LEN );
		
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

	private void logDataStatus(String key) {
		if ( ! LOGGER.isLoggable(Level.FINE) ) {
			return;
		}
		
		LOGGER.log(Level.FINE,"!!!Request for : "+key);
		
		LOGGER.log(Level.FINE,"\t!!!GeneratedData: ");
		for ( Entry e : generatedData.entrySet() ) {
			LOGGER.log(Level.FINE,"\t\t"+e.getKey()+" "+e.getValue());
		}
		
		LOGGER.log(Level.FINE,"\t!!!reassignedData: ");
		for ( Entry e : reassignedData.entrySet() ) {
			LOGGER.log(Level.FINE,"\t\t"+e.getKey()+" "+e.getValue());
		}
	}
	
	public void nextTest() {
		LOGGER.log(Level.FINE,"!!!NEXT_TEST");
		START++;
		cleanUpGeneratedAndReassignedData();
	}

	public void cleanUpGeneratedAndReassignedData() {
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
		
		LOGGER.log(Level.FINE,"!!!Reassigning "+lhs.id +" "+lhs);
		
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
			System.out.println("Deleting "+e.getKey());
			//No need to set D as not reassigned because it should never be referenced again; anyway, it would be unsafe.
			generatedData.remove(e.getKey());	
		}
		
		reassignedData.clear();
	}

	public int getUsedSourceInputs() {
		return generatedData.size()-reassignedData.size();
	}
	
	public int size() {
		return inputs.size();
	}
	
	List<D> unshuffled = null;
	Random rnd = null;
	public void shuffle() {
		if ( unshuffled != null ) {
			inputs = unshuffled;
		} else {
			System.out.println("New random");
			rnd = new Random(System.currentTimeMillis());
		}
		
		int start = START % LEN;
		
		//we shuffle everything except the one at position start, which should remain at position start
		
		unshuffled = inputs;
		inputs = new LinkedList<D>();
		
		LinkedList<D> toShuffle = new LinkedList<D>();
		toShuffle.addAll(unshuffled);
		
		
		
		System.out.println("START "+START);
		System.out.println("start "+start);
		
//		System.out.println("TOKEEP (before) "+toShuffle.get(start-1).hashCode()+" "+toShuffle.get(start-1));
		System.out.println("TOKEEP  "+toShuffle.get(start).hashCode()+" "+toShuffle.get(start));
		System.out.println("TOKEEP (after) "+toShuffle.get(start+1).hashCode()+" "+toShuffle.get(start+1));
		
		
		D first = toShuffle.remove(start);
		
		
		
		Collections.shuffle(toShuffle, rnd);
		
		inputs.addAll(toShuffle);
		inputs.add(start, first);
		
		System.out.println("ELEMENT-1 "+inputs.get(start).hashCode()+" "+inputs.get(start));
		System.out.println("ELEMENT-2 "+inputs.get(start+1).hashCode()+" "+inputs.get(start+1));
		
		cleanUpGeneratedAndReassignedData();
		
	}

	public void unshuffle() {
		if ( unshuffled != null ) {
			inputs = unshuffled;
		}
		unshuffled = null;
	}
}
