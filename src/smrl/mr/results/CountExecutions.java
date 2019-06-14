package smrl.mr.results;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

public class CountExecutions {

	public static void main(String[] args) throws IOException {
		File file = new File( args[0] );
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		
		int sourceInputsRequested = 0;
		int followUpInputsRequested = 0;
		boolean first = true;
		int lastInputs=0;
		int actions=0;
		while ( ( line = r.readLine() ) != null ) {
			if ( line.startsWith("Executed") ) {
				if ( line.contains("Input(2)") ) {
					System.out.println(line);
					actions+=lastInputs*count(line,"Action");
				}
				lastInputs=0;
				first = true;
				continue;
			}
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
		}
		
		r.close();
		
		System.out.println("Source inputs requested :"+sourceInputsRequested);
		System.out.println("Follow-up inputs requested :"+followUpInputsRequested);
		System.out.println("Actions "+actions);
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

}
