package smrl.mr.results;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

public class CountExecutionsAdvanced {

	private static final boolean IGNORE_MANUAL = false;

	
	static HashMap<String,String> inspectedFailures = new HashMap<String,String>();
	static HashMap<String,Integer> inspectedFailuresKey = new HashMap<String,Integer>();
	static HashSet<String> observedFailures = new HashSet<>(); 
	static HashSet<String> failures = new HashSet<>(); 
	static int ignoredFollowUpInputs=0;
	private static HashSet<String> manualInputs;
	public static void main(String[] args) throws IOException {
		manualInputs = new HashSet<String>();
		manualInputs.add("http://192.168.56.102:8080/job/listRoot/build?delay=0sec");
		manualInputs.add("http://192.168.56.102:8080/queue/cancelItem?id=2");
		manualInputs.add("http://192.168.56.102:8080/job/jobWithFileParam/configure");
		manualInputs.add("http://192.168.56.102:8080/job/jobWithFileParam/configSubmit");
		manualInputs.add("http://192.168.56.102:8080/job/jobWithFileParam/build?delay=0sec");
		manualInputs.add("http://192.168.56.102:8080/job/jobWithFileParam/lastBuild/console");
		manualInputs.add("http://192.168.56.102:8080/job/jobWithFileParam/lastBuild/consoleText");


		File file = new File( args[0] );
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;

		int sourceInputsRequested = 0;
		int followUpInputsRequested = 0;
		boolean first = true;
		int lastInputs=0;
		int actions=0;
		
		int followUpInputs = 0;
		int sourceInputs = 0;
		
		boolean ignoreFollowing = false;
		while ( ( line = r.readLine() ) != null ) {

			
			
			if ( line.startsWith("Executed") ) {
				
				

				if ( line.contains("Input(1)") ) {
					ignoreFollowing=false;
				}

				if ( containsManualInput(line) ) {
					if ( line.contains("Input(1)") ) {
						ignoreFollowing=true;
					}
					continue;
				}

				if ( line.contains("Input(2)") ) {
					//					System.out.println(line);
					actions+=lastInputs*count(line,"Action");
//					actions+=count(line,"Action");
				}
				
				if ( ! ignoreFollowing ) {
					if ( line.contains("Executed with: Input(2)") ) {
						followUpInputs++;
					}
					if ( line.contains("Executed with: Input(1)") ) {
						sourceInputs++;
					}
				}
				
				lastInputs=0;
				first = true;
				continue;
			}

			if ( ! ignoreFollowing ) {
				
				
				
				if ( line.startsWith("Starting ChromeDriver") ) {
					if ( first ) {
						first = false;
						sourceInputsRequested++;
					} else {
						lastInputs++;
						followUpInputsRequested++;
					}
					continue;
				}
				if ( line.startsWith("FAILURE") ) {

					if ( containsManualInput(line) ) {
						continue;
					}

					int pos = line.indexOf("=[[");
					line = line.substring(pos);
					String[] actionsA = line.split(", \\[");
					String key = "";
					for ( String action : actionsA ) {
						int p = action.indexOf(":");
						String content = action.substring(p);
						if ( content.equals(": randomly click on one of new elements]") ) {
							content="";
						}
						key+=content;
					}
					if ( ! observedFailures.contains(key) ) {
						observedFailures.add(key);
						failures.add(key+" ::: "+line);
						
						if ( containsNewURLs(line) ) {
							inspectedFailures.put(line,key);
							inspectedFailuresKey.put(key, 1);
						}
						
//						failures.add(key+" ::: "+line);

					} else { 
						
						Integer count = inspectedFailuresKey.get(key);
						
						if ( count != null ) {
							inspectedFailuresKey.put(key, count+1);	
						}
						
						ignoredFollowUpInputs++;
					}
					continue;
				}
			}
		}

		r.close();

		System.out.println("Source inputs requested :"+sourceInputsRequested);
		System.out.println("Follow-up inputs requested :"+followUpInputsRequested);
		System.out.println("Follow-up inputs requested (excluding ignored):"+(followUpInputsRequested-ignoredFollowUpInputs));
		System.out.println("Actions "+actions);
		System.out.println("Failures "+failures.size());
		
		System.out.println("Follow-up inputs  :"+followUpInputs);
		System.out.println("Source inputs :"+sourceInputs);

		System.out.println("FAILURES:");
		for (String s : failures ) {
			System.out.println(s);
		}
		
		int tot = 0;
		
		System.out.println("FAILURES TO INSPECT: " +inspectedFailures.size());
		System.out.println("FAILURES TO INSPECT:");
		for (Entry<String, String> se : inspectedFailures.entrySet() ) {
			
			Integer val = inspectedFailuresKey.get(se.getValue());
			System.out.println(se.getKey()+" : "+val);
			
			tot += val;
		}
		
		System.out.println(tot);
	}

	private static boolean containsManualInput(String line) {
		if ( ! IGNORE_MANUAL ) {
			return false;
		}
		for ( String mI : manualInputs ) {
			if ( line.contains(mI) ) {
				return true;
			}
		}
		return false;
	}

	private static int count(String input,String v) {
		int index = input.indexOf(v);
		int count = 0;
		while (index != -1) {
			count++;
			input = input.substring(index + 1);
			index = input.indexOf(v);
		}

		return count;
	}
	
	static HashSet<String> inspectedURLs = new HashSet<String>(); 
	private static boolean containsNewURLs(String input) {
		String v = "Action";
		int index = input.indexOf(v);
		int count = 0;
		
		boolean containsNewURLs = false;
		
		while (index != -1) {
			count++;
			input = input.substring(index + 1);
			index = input.indexOf(v);
			String content = input.substring(index + 1);
			int start = content.indexOf("http");
			int end = content.indexOf("]");
			
			if ( start > 0 && end > start ) {

				String url = content.substring(start,end);

				int q = url.indexOf('?');
				if ( q>0 ) {
					url = url.substring(0,q);
				}
				
				if ( url.endsWith("#") ) {
					url = url.substring(0,url.length()-1);
				}
				if ( url.endsWith("/") ) {
					url = url.substring(0,url.length()-1);
				}

				if ( ! inspectedURLs.contains(url) ) {
					System.out.println("!!!NEW-INPUT: "+url);
					containsNewURLs = true;
					inspectedURLs.add(url);
				}

			}
			
			
		}

		return containsNewURLs;
	}

}
