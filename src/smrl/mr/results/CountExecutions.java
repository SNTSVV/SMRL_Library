/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.mr.results;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

public class CountExecutions {

	public static void main(String[] args) throws IOException {
		File file = new File( args[0] );
		
		int followUpInput = -1;
		if(args.length>1) {
			followUpInput = Integer.parseInt(args[1]);
		}
		
		String followUpString = "Input(";
		if(followUpInput>1) {
			followUpString += followUpInput + ")";
		}
		else {
			followUpString += 2 + ")";
		}
		
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		
		int sourceInputsRequested = 0;
		int followUpInputsRequested = 0;
		boolean first = true;
		int lastInputs=0;
		int actions=0;
		while ( ( line = r.readLine() ) != null ) {
			if ( line.startsWith("Executed") ) {
//				if ( line.contains("Input(2)") ) {
				if ( line.contains(followUpString) ) {
//					System.out.println(line);
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
