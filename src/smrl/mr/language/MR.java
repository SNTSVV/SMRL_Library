package smrl.mr.language;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.analysis.ASMUtil;
import smrl.mr.analysis.ASMUtil.ASM_MRData;
import smrl.mr.utils.URLUtil;


public abstract class MR {
	
	Logger LOGGER = Logger.getLogger(MR.class.getCanonicalName());
	
	public static MR CURRENT;
	
	private static final boolean COLLECT_ALL_FAILURES = true;

	private static final boolean PERFORM_FILTERING = true;

	private static final int MAX_SHUFFLING = 10;

	private static boolean MEexecutedAtLeastOnce = false;

	OperationsProvider provider;
	

	private List<String> dataConsidered;

	private HashMap<String, MrDataDB> dataDBs = new HashMap<String,MrDataDB>();

	private int executions;

	private ArrayList<MrDataDB> sortedDBs;





//	public MR( OperationsProvider<Input, Output > provider, List<String> dataConsidered ){
//		this.provider = provider;
//		this.dataConsidered = dataConsidered;
//		
//	}
	
	public void setProvider(OperationsProvider provider){
		this.provider = provider;
	}
	
	
	public void run() {
		
		LOGGER.log(Level.FINE,"!!! Executing MR: "+this.getClass().getName());
		
		CURRENT=this;
		
		try {
			ASM_MRData _mrData = ASMUtil.extractMRData(this);
			dataConsidered = ASMUtil.retrieveDataConsideredInMR(_mrData);
			
			totalMetamorphicExpressions = _mrData.getExpressionPassCounter();
			
			LOGGER.log(Level.FINE,"Total ME: "+totalMetamorphicExpressions);
			
			LOGGER.log(Level.FINE,"Data: "+dataConsidered);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		sortedDBs = new ArrayList<MrDataDB>(); 
		{
			for(String dataName : dataConsidered ){
				if ( dataName.equals("RandomValue") ) {
					MrDataDBRandom db = new MrDataDBRandom(dataName);
					dataDBs.put(dataName, db);
					sortedDBs.add(db);
					continue;
				} 
				
				if ( dataName.equals("RandomHttpMethod") ) {
					MrDataDBHttpMethod db = new MrDataDBHttpMethod(dataName);
					dataDBs.put(dataName, db);
					sortedDBs.add(db);
					continue;
				} 
				
				
				MrDataDB db = new MrDataDB(dataName);
				dataDBs.put(dataName, db);
				db.load(provider.load(dataName));
				sortedDBs.add(db);
			}

		}
		
		
		executions = 0;
		
		resetMRState();
		
		iterateMR( sortedDBs, 0 );
		
//		//basically we iterate over a potential set of inputs entities
//		inputsDB.resetTestsCounter();
//		while ( inputsDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
//			usersDB.resetTestsCounter();
//			while( usersDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
//				if ( ! mr() ){
//					fail();
//					FAILED=true;
//				}
//				String msg = extractExecutionInformation();
//				System.out.println("Executed with: "+msg);
//				
//				executions++;
//				usersDB.nextTest();
//			}
//			inputsDB.nextTest();
//		}
		
		
		System.out.println("MR tested with "+executions+" set of inputs");
	}
	
	boolean FAILED=false;
	private void iterateMR(List<MrDataDB> sortedDBs, int i) {
//		System.out.println("!!!iterateMR executions="+executions+" i="+i);
		
		if ( sortedDBs.size() == i ){ //no other data to iterate on, execute the MR
			try {
				if ( ! mr() ){
					fail();
					FAILED=true;
				}
			} catch ( Throwable t ) {
				t.printStackTrace();
				System.out.println("!!!!IGNORING EXCEPTION, sleep");
				
				
				
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("!!!!IGNORING EXCEPTION, go ahead");
			}
			
			
			String msg = extractExecutionInformation(false);
			System.out.println("Executed with: "+msg);
			
			executions++;
			
			cleanupReassignedData();
			resetMRState();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Runtime.getRuntime().exec("killall chromedriver");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		MrDataDB db = sortedDBs.get(i);
		db.resetTestsCounter();
		while ( db.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
			
//			int expectedSrcInputs = expectedSourceInputsOfType(db);
//			if ( expectedSrcInputs > 1 ) {
////				if ( true ) {
////					throw new IllegalStateException("This is a debug message, this code should be executed for SESS_003, never tested");
////				}
//				
//				iterateMRshuffling(sortedDBs, db, i);
//			} else {
//				iterateMR(sortedDBs, i+1);
//			}
			
			int expectedSrcInputs = expectedSourceInputsOfType(db);
			
			if ( expectedSrcInputs <= 1 ) {
				iterateMR(sortedDBs, i+1);
				traceSourceInputsOfSameType(db);
				expectedSrcInputs = expectedSourceInputsOfType(db);
			}
			
			
			
			if ( expectedSrcInputs > 1 ) {
//				if ( true ) {
//					throw new IllegalStateException("This is a debug message, this code should be executed for SESS_003, never tested");
//				}
				
				iterateMRshuffling(sortedDBs, db, i);
			}
			
			db.nextTest();
			provider.nextTest();
		}
	}


	private void iterateMRshuffling(List<MrDataDB> sortedDBs, MrDataDB db, int i) {
//		System.out.println("Shuffling "+db.dbName);
		int max = db.size() < MAX_SHUFFLING ? db.size() : MAX_SHUFFLING;
		for ( int j = 0; j < max; j++ ) {
			db.shuffle();
			iterateMR(sortedDBs, i+1);
		}
		db.unshuffle();
	}

	private int expectedSourceInputsOfType(MrDataDB db) {
		if ( ! usedSourceInputsMap.containsKey(db) ) {
			return 1;
		}
		return usedSourceInputsMap.get(db);
	}

	private void traceSourceInputsOfSameType(MrDataDB db) {
		if ( usedSourceInputsMap.containsKey(db) ) {
//			System.out.println("OPTIMIZATION");
			return; //This is an optimization, we just compute once per DB. We may change policy in the future.
		}
		int usedSrcInputs = db.getUsedSourceInputs();
//		System.out.println("!!Used source inputs of last execution for "+db.dbName+" "+usedSrcInputs);
		int lastUsedSrcInputs = 0;
		if ( usedSourceInputsMap.containsKey(db) ) {
//			System.out.println("!!Used source inputs for "+db.dbName+" "+usedSrcInputs);
			lastUsedSrcInputs = usedSourceInputsMap.get(db);
		}
		if ( MEexecutedAtLeastOnce ) {
			if ( usedSrcInputs > lastUsedSrcInputs ) {
//				System.out.println("Updating inputs map");
				usedSourceInputsMap.put(db,usedSrcInputs);
			}
		} 
//		else {
//			System.out.println("No ME executed");
//		}
	}

	HashMap<MrDataDB,Integer> usedSourceInputsMap = new HashMap<MrDataDB,Integer>();

	LinkedList<String> failures = new LinkedList<String>();
	public LinkedList<String> getFailures() {
		return failures;
	}


	public void fail(){
		
		LOGGER.log(Level.INFO,"FAILURE");
		
		String msg = extractExecutionInformation(true);
		
		if ( msg == null ) {
			System.out.println("(DUPLICATED FAILURE, ignoring)");
			return;
		}
		
		failures.add(msg);
		System.out.println("FAILURE: "+msg);
	}
	
	private String extractExecutionInformation(boolean performFiltring) {
		String msg = "";
		
		for ( MrDataDB db : sortedDBs ){
			HashMap<String, Object> inputsMap = db.getProcessedInputs();
			
			boolean filteringApplied = false;
			
			for ( Entry<String,Object> i : inputsMap.entrySet() ){
				
				if ( performFiltring && PERFORM_FILTERING ) {
					Object value = i.getValue();
					if ( value instanceof Input ) {
						if ( filteringApplied == false) { //filtering is done on the first returned follow-up input, which is the one submitted
							Input inp = (Input) value;
							boolean containsNewData = registerInput(inp );

							if ( ! containsNewData ) {
//								System.out.println("!!! Does not contain new data");
								return null;
							} 
//							else {
//								System.out.println("!!! Contains new data");
//							}
							filteringApplied = true;
						}
					}
				}
				
				msg += i.getKey()+": "+i.toString()+"\n";
			}	
		}
		
		msg += " [Last input processed: "+lastInput+" position: "+lastInputPos+"] "+"[Last equal: "+lastEqual+"]";
		
		lastInput = null;
		lastInputPos = -1;
		lastEqual = null;
		
//		HashMap<String, I> inputsMap = inputsDB.getProcessedInputs();
//		String msg = "";
//		for ( Entry<String,I> i : inputsMap.entrySet() ){
//			msg += i.getKey()+": "+i.toString()+"\n";
//		}
//		
//		HashMap<String, User> usersMap = usersDB.getProcessedInputs();
//		for ( Entry<String,User> i : usersMap.entrySet() ){
//			msg += i.getKey()+": "+i.toString()+"\n";
//		}
		return msg;
	}
	
	private boolean considerParameters = false;
	
	public void setConsiderParameters() {
		considerParameters = true;
	}
	
	private HashSet<String> observedInputKeys = new HashSet<>();
	protected boolean registerInput(Input inp) {
		
//		System.out.println("!!!Register input "+inp);
		
		boolean isNew = false;
		for ( Action action : inp.actions() ) {
			String url = action.getUrl();
			
			if ( url == null ) {
				url = "";
			}
			
			url = url.trim();
			
			url = URLUtil.extractActionURL(url);
			
			if ( considerParameters ) {
				String pars = extractParametersString(action);
				url = url +":"+pars;
			}
			
			boolean isThisNew = observedInputKeys.add(url);
			if ( isThisNew ) {
				isNew = true;
			}
			
		}
		
		return isNew;
	}


	private String extractParametersString(Action action) {
		JsonArray formInputs = action.getFormInputs();
		
		String pars = "";
		for(int i=0; i<formInputs.size(); i++){
			JsonObject fi = formInputs.get(i).getAsJsonObject();
			
			if(fi.keySet().contains("type") &&
					fi.keySet().contains("values")){
				
				String formType = fi.get("type").getAsString().toLowerCase();
				JsonArray values = fi.get("values").getAsJsonArray();
				if(values.size()>0 &&
						(formType.startsWith("text") ||
							formType.equals("password") || 
							formType.equals("hidden") ||
							formType.equals("file"))){
					for(int iValue=0; iValue<values.size(); iValue++){
						String value = values.get(iValue).getAsString().trim();
						pars=pars+value+";";
					}
				}
			}
		}
		
		return pars;
	}


	public abstract boolean mr();
	
//	public I Input(int i){
//		I input = (I) dataDBs.get("Input").get(i);
//
//		return input;
//	}
//
//	public User User(){
//		return (User) dataDBs.get("User").get(1);
//	}
	
	
	
	

	

	

	
	//SMART COMPARISON
	public boolean equals(Object lhs, Object rhs) {
		return equal(lhs,rhs);
	}
	
	public boolean equal(Object _lhs, Object rhs) {
		if ( _lhs.equals(rhs) ){
			return true;
		}
		
		if ( _lhs instanceof MRData ){
			MRData lhs = (MRData) _lhs;
			if ( subTypes( lhs, rhs ) ){	
				if ( inputsDB().contains(lhs) ){
					MRData _rhs;
					if ( ! instanceOf(lhs, rhs) ){
						_rhs = buildReassignableElement(lhs,rhs);
						if ( _rhs == null ){
							return false;
						}
					} else {
						_rhs = (MRData) rhs;
					}
					
					return inputsDB().reassign(lhs,_rhs);
				}
			}
		}
		
		return false;
	}

	private MRData buildReassignableElement(MRData lhs, Object rhs) {
		MRData _rhs=null;
//		if ( 1 == 1 ){
//			throw new RuntimeException("PLease note that this is the first time you are re-assigning something to a different obj type, e.g. Input(2)==Action, you'll need to debug");
//		}
		Class<? extends Object> _parClass = rhs.getClass();
		
		while ( _rhs == null && _parClass != null ){
			try {
//				System.out.println("!!! "+_parClass.getCanonicalName());
				Constructor<? extends MRData> constructor = lhs.getClass().getConstructor( _parClass );
				_rhs = constructor.newInstance(rhs);
			} catch (NoSuchMethodException e) {
				//TODO: the best would be to iterate also over interfaces, but for now it's ok
				_parClass = _parClass.getSuperclass();
			}  catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if ( _rhs == null ){
			Constructor<?>[] constrs = lhs.getClass().getConstructors();
			for ( Constructor<?> constr : constrs ){
				Class<?>[] pars = constr.getParameterTypes();
				if ( pars.length > 1 ){
					return null;
				}
				if ( Collection.class.isAssignableFrom( pars[0] ) ){
					try {
						Collection _i = (Collection) pars[0].newInstance();
						_i.add(rhs);
						_rhs = (MRData) constr.newInstance(_i);
						break;
					} catch (InstantiationException e1) {
						e1.printStackTrace();
						return null;
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
						return null;
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
						return null;
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
						return null;
					}
				}
			}
			
			
		}
		
		return _rhs;
	}

	private boolean instanceOf(MRData lhs, Object rhs) {
		return lhs.getClass().isAssignableFrom(rhs.getClass());
	}
	

	


	protected MrDataDB inputsDB() {
		return dataDBs.get("Input");
	}

	private boolean subTypes(Object lhs, Object rhs) {
		if ( 
				lhs.getClass().isAssignableFrom( rhs.getClass() )
				||  rhs.getClass().isAssignableFrom( rhs.getClass()) ) {
			return true;
		}
		return false;
	}

	public boolean different(Object lhs, Object rhs) {
		if ( lhs == null || rhs == null ){
			return true;
		}
		return ! lhs.equals(rhs);
	}
	
	
	
	
//	protected boolean implies(boolean a, boolean b) {
//		return (!a) || b;
//	}
//	
//	protected boolean implies(ExecutableParameter a, ExecutableParameter b) {
//		boolean _a = a.run();
//		if ( !_a ){
//			return true;
//		}
//		
//		return b.run();
//	}
	
	
	

	
	
	public Object getMRData(String name, int i){
		return dataDBs.get(name).get(i);
	}


	public String getCurrentExecutionId() {
		return ""+executions;
	}
	
	/**
	 * This method is supposed to be invoked after checking the last metamorphic 
	 * expression within a metamorphic relation. It is necessary to ensure that
	 * the follow-up inputs created by the loops inside a MR last for only one 
	 * cycle. 
	 * 
	 * Attention: all teh follow up inputs are supposed to be created within the 
	 * loop, otherwise we'll have inconsistent indexes.
	 * 
	 */
	public void cleanupReassignedData() {
		
//		System.out.println("cleanupReassignedData");
		for ( MrDataDB db : sortedDBs ){
			db.cleanupReassignedData();	
		}
	}
	
	int passingExpressions = 0;
	int totalMetamorphicExpressions = 0;
	
	private void resetMRState() {
//		System.out.println("resetPassingExpressionsCounter");
		passingExpressions = 0;
		ifBlocksCounter = 0;
		
		if ( lineOfFirstME > 0 ) {
			MEexecutedAtLeastOnce = true;
		}
		lineOfFirstME = -1;
	}
	
	private void resetIfBocksCounter() {
//		System.out.println("resetPassingExpressionsCounter");
		ifBlocksCounter = 0;
	}
	
	int lineOfFirstME=-1;
	int lineOfLastME=-1;
	int ifBlocksCounter = 0;

	private int lastInputPos;
	private String lastInput;

	private String lastEqual;
	
	public void ifThenBlock() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		int pos = 2;
		int currentLine = st[pos].getLineNumber();
		
//		System.out.println("IF_THEN_BLOCK");
		LOGGER.fine("Current line "+currentLine+" : "+st[pos].getClassName()+"."+st[pos].getMethodName());
		
		if ( lineOfFirstME == -1 ) {
			lineOfFirstME = currentLine;	
		}
		
//		System.out.println("lineOfFirstME: "+lineOfFirstME);
		
		if ( currentLine == lineOfFirstME ) {
			LOGGER.fine("new ME cycle "+currentLine);
			cleanupReassignedData();
		} 
//		The following enables resetting reassigned data every time an internal loop is re-executed, 
//		but I'm not sure it is what we may want.
//		else if ( currentLine < lineOfLastME ) {
//			LOGGER.fine("new ME sub-cycle "+currentLine);
//			cleanupReassignedData();
//		}
//		
//		lineOfLastME = currentLine;
		
		
		ifBlocksCounter++;
	}
	
	@ExpressionPassTag
	public void expressionPass() {
		passingExpressions++;
		
//		//FIXME: for now we always reset, in the future we should add a call at the beginning of each block that 
//		//counts how many times we entered a block that should lead to a reset
//		if ( passingExpressions == ifBlocksCounter ) {
//					cleanupReassignedData();
//		resetPassingExpressionsCounter();
//		resetIfBocksCounter();
//		}
	}


	public MrDataDB getDataDB(String string) {
		return dataDBs.get(string);
	}


	public void setLastInputProcessed(Input input, int pos) {
		this.lastInput = input.toString();
		this.lastInputPos = pos;
	}


	public void setLastEQUAL(Object a, Object b) {
		// TODO Auto-generated method stub
		lastEqual = " "+a+ ", "+b;
	}
		

}
