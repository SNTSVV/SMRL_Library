package smrl.mr.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReaderInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.crawljax.WebProcessor;

public class MergeInputs {

	public static void main(String[] args) {
		String listFileName = "./testData/FINAL/listInputFiles_short.txt";
		String configFile = "./testData/FINAL/sysConfig.json";
		String outFileName = "./testData/FINAL/inputShort.json";

		
		
		WebProcessor webPro = new WebProcessor();
		
		if(configFile!=null && !configFile.isEmpty()){
			webPro.setConfig(configFile);
		}
		
		
		ArrayList<String> inputFileNames = new ArrayList<String>();
		
		try {
			FileReader fileReader = new FileReader(listFileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				if(!line.trim().isEmpty()){
					inputFileNames.add(line);
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(listFileName + " does NOT exist!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<WebInputCrawlJax> listFullInputs = new ArrayList<WebInputCrawlJax>();
		
		for(String iFileName:inputFileNames){
			File f = new File(iFileName);
			if(!f.exists()){
				continue;
			}
			
			List<WebInputCrawlJax> currentInputs = loadInputFromFile(iFileName, webPro);
			
			if(currentInputs==null || currentInputs.size()<1){
				continue;
			}
			
			for(WebInputCrawlJax input:currentInputs){
				if(!listFullInputs.contains(input)){
					listFullInputs.add(input);
				}
			}
		}
		
//		exporttofile
		AugmentInput.exportInputListToFile(outFileName, listFullInputs);
		
		System.out.println("Done!!!\nNumber of inputs: " + listFullInputs.size());
	}

	private static List<WebInputCrawlJax> loadInputFromFile(String jsonInputFileName, WebProcessor webProcessor) {
		List<WebInputCrawlJax> res = new ArrayList<WebInputCrawlJax>();
		
		SystemConfig sysConfig = webProcessor.sysConfig;
		String userParam = sysConfig.getUserParameter();
		String passwordParam = sysConfig.getPasswordParameter();
		
		Gson gson = new Gson();
		File jsonFile = Paths.get(jsonInputFileName).toFile();
		if (!jsonFile.exists()) {
			System.out.println("Input file not found: " + jsonInputFileName);
			return res;
		}
		
		JsonObject jsonObject=null;
		try {
			jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(String key:jsonObject.keySet()){
			JsonArray jsonInput = jsonObject.get(key).getAsJsonArray();
			if(jsonInput!= null && jsonInput.size()>0){
				WebInputCrawlJax input = new WebInputCrawlJax(jsonInput);

				

				if(userParam!=null && passwordParam!=null &&
						!userParam.isEmpty() && !passwordParam.isEmpty()){
					input.identifyUsers(userParam, passwordParam, webProcessor);
				}

				res.add(input);
			}
		} 
		
		return res;
	}

}
