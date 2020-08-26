package smrl.mr.singleTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import smrl.mr.language.CollectionOfConcepts;

public class ResultCounting {
	@Test
	public void countCheckTags() {
		//Source inputs: Input(1), Input(2)
		//Follow-up inputs: Input(3), Input(4)
		
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/Log-checkTags_2020_08_08_full.txt";
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/crawljax/Log-checkTags_2020_08_09_crawljax.txt";
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/Log-checkTags_2020_08_08_full_12h.txt";
		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/crawljax/Log-checkTags_2020_08_14_headless_crawljax_12h.txt";
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/Log-checkTags_2020_08_12_headless_full_12h.txt";
		
		File f = new File(fileName);
		if (!f.exists()) {
			System.out.println("File not found: " + fileName);
			return;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));

			int failure = 0;
			
			int isSourceInput = 1;	// Souce input (1,2), follow-up input (3,4...)
			int sourceInput = 0;
			int followUpInput = 0;
			int actions = 0; 		// number of follow-up inputs' actions

			String line1 = br.readLine();
			System.out.println("Run time: --> " +line1);

			line1 = br.readLine().trim();
			while (line1 != null) {
				if(line1.contains("FAILURE:")) {
					failure++;
				}
				
				//reset isSourceInput
				else if(line1.contains("[Last input processed:")) {
					isSourceInput=1;
				}

				else if(line1.startsWith("Starting ChromeDriver")){
					if(isSourceInput <3) {
						sourceInput++;
					}
					else {
						followUpInput++;
					}
					isSourceInput++;
				}

				else if(line1.contains("- Action") && 
						isSourceInput >=3) {
					actions++;
				}



				else if(line1.contains("MR tested with") ||
						line1.contains("***")) {
					System.out.println("Run time: --> " +line1);
				}
				line1 = br.readLine();

			}
			br.close();
			
			System.out.println("Failures: " + failure);
			System.out.println("Source inputs: " + sourceInput);
			System.out.println("Follow-up inputs: " + followUpInput);
			System.out.println("Actions of follow-up inputs: " + actions);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void countLogInpVal003() {
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Jenkins/Logs/Log-INPVAL_003_2020_03_04_full.txt";
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Jenkins/Logs/crawljax/Log-INPVAL_003_2020_03_06_crawljax.txt";
//		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/Log-INPVAL_003_2020_08_24_full.txt";
		String fileName = "/Users/xuanphu.mai/PhD/EDLAH2/Bitbucket-EDLAH2/Software/SMRL_Framework_Blind/testData/Joomla/Logs/crawljax/Log-INPVAL_003_2020_08_25_crawljax.txt";
		File f = new File(fileName);
		if (!f.exists()) {
			System.out.println("File not found: " + fileName);
			return;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			boolean firstStart = false;
			int sourceInputs = 0;
			int followUpInputs = 0;
			int actions = 0;
			int failure = 0;
			int execFollowUp = 0;
			
			String line = br.readLine();
			System.out.println("Run time: --> " +line);


			while ((line = br.readLine()) != null) {
				if(line.contains("FAILURE:")) {
					failure++;
					continue;
				}
				if(line.startsWith("Starting ChromeDriver") ) {
					if(firstStart==false) {
						firstStart = true;
						sourceInputs++;
					}
					else {
						followUpInputs++;
						execFollowUp++;
					}
					continue;
				}
				if(line.contains("Input(2)")){
					if(firstStart) {
						actions += execFollowUp * count(line, "Action");
						
						firstStart=false;
						execFollowUp=0;
					}
					continue;
				}
				
				if(line.contains("MR tested with") ||
						line.contains("***")) {
					System.out.println("Run time: --> " +line);
				}
				
			}
			br.close();
			
			System.out.println("Failures: " + failure);
			System.out.println("Source inputs: " + sourceInputs);
			System.out.println("Follow-up inputs: " + followUpInputs);
			System.out.println("Actions of follow-up inputs: " + actions);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
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
