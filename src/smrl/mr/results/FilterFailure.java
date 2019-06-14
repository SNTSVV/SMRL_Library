package smrl.mr.results;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilterFailure {

	public static void main(String[] args) throws IOException {
		File file = new File( args[0] );
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		
		String result = "";
		int num = 1;
		String processingLines = "===== FAILURE "+ num +" =====\n";
		
		Boolean failure = false;
		Boolean executed = false;
		Boolean secondSign = false;
		
		while ( ( line = r.readLine() ) != null ) {
			if((line.startsWith("Starting ChromeDriver") || 
					line.startsWith("Executed with")) 
					&& executed) {
				secondSign = true;
			}
			else if ( line.startsWith("Executed with") ) {
				executed = true;
					
			}
			else if ( line.startsWith("FAILURE:") ) {
				failure = true;
			}
			
			if(failure && secondSign) {
				result += processingLines + "\n";
				num++;
			}
			
			if(secondSign) {
				processingLines = "===== FAILURE "+ num +" =====\n";
				failure = false;
				executed = false;
				secondSign = false;
				
				if(line.startsWith("Executed with")) {
					executed = true;
				}
			}
						
			processingLines += line + "\n"; 
		}
		
		r.close();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(args[0]+".filtered.txt"));
		writer.write(result);
		writer.close();
		
//		System.out.println(result);

	}

}
