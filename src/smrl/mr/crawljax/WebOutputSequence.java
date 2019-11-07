package smrl.mr.crawljax;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Cleaner;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.language.CookieSession;
import smrl.mr.language.CollectionOfConcepts;
import smrl.mr.language.Output;
import smrl.mr.language.Session;
import smrl.mr.language.SystemConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class WebOutputSequence implements Output {

	
	ArrayList<Object> seq;		//Object should be WebOutputCleaned
	ArrayList<String> redirectedURLs;
	
	private ArrayList<CookieSession> sessionSequence;
	
	public WebOutputSequence() {
		this.seq = new ArrayList<Object>();
		this.redirectedURLs = new ArrayList<String>();
//		this.useEditDistance = true;
		this.sessionSequence = new ArrayList<CookieSession>();
	}





	public ArrayList<String> getRedirectedURLs() {
		return redirectedURLs;
	}



	public void add(Object singleOutput) {
		seq.add(singleOutput);
//		seqText
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WebOutputSequence)) {
			return false;
		}
		WebOutputSequence rhs = (WebOutputSequence) obj;

		if ( ! compare(seq,rhs.seq) ){
			return false;
		}
		
		
		
	

		return true;
	}

	private boolean compare(ArrayList<Object> seq, ArrayList<Object> rhsSeq) {
		if (seq.size() != rhsSeq.size()) {
			return false;
		}
		
		for (int i = 0; i < seq.size(); i++) {
			WebOutputCleaned o1 = (WebOutputCleaned) seq.get(i);
			WebOutputCleaned o2 = (WebOutputCleaned) rhsSeq.get(i);
			
			if ( ! o1.compare( o2 ) ){
				return false;
			}
		}
		return true;
	}



	

	@Override
	public String redirectURL() {
		// return seq.get(seq.size()-1).redirectURL()
		String url = redirectedURLs.get(redirectedURLs.size()-1);
		if(url.isEmpty()){
			return null;
		}
		return url;
//		return redirectedURLs.get(redirectedURLs.size()-1);
//		return null;
	}
	
	public String redirectURL(int pos){
		if(this.redirectedURLs.size()>pos && pos>=0){
			return redirectedURLs.get(pos);
		}
		return null;
	}
	
	public void addRedirectURL(String url){
		this.redirectedURLs.add(url);
	}

	@Override
	public boolean isError() {
		// A webOutputSequence is an error if:
		//1. it is an empty one
		//2. it contains error signs, which are defined in the system configuration
		//3. it contains 4xx or 5xx http response
		
		if(this.seq == null || this.seq.size()==0){
			return true;
		}
		
		SystemConfig sysConfig = WebProcessor.getSysConfig();
		if(sysConfig==null || sysConfig.getErrorSigns()==null ||
				sysConfig.getErrorSigns().size()<=0){
			return false;
		}
		
		
		//check each output in the sequence
		for(Object out:this.seq){
			String cleanedOutHhml = ((WebOutputCleaned)out).html;
			Document doc = Jsoup.parse(cleanedOutHhml);

			JsonObject errorSigns = sysConfig.getErrorSigns();
			//Check class, script, attribute, id, title

			//Check CLASSES
			if(errorSigns.keySet().contains("class") && 
					errorSigns.getAsJsonArray("class").size()>0){
				JsonArray classArray = errorSigns.getAsJsonArray("class");
				for(Element ele:doc.getElementsByAttribute("class")){
					for(int i=0; i<classArray.size(); i++){
						String errorClass = classArray.get(i).getAsString();
						if(ele.attr("class").equals(errorClass)){
							return true;
						}
					}
				}
			} 	

			
			//Check SCRIPTS
			if(errorSigns.keySet().contains("script") && 
					errorSigns.getAsJsonArray("script").size()>0){

				JsonArray scriptArray = errorSigns.getAsJsonArray("script");
				if (scriptArray.size() > 0) {
					Elements eScript = doc.getElementsByTag("script");

					//check attributes of the script
					for(Element ele:eScript){
						for(Attribute att:ele.attributes()){
							for (int k = 0; k < scriptArray.size(); k++) {
								String expr = scriptArray.get(k).getAsString();
								if(att.getKey().equals(expr)){
									return true;
								}
							}
						}
					}

					//check data of the script
					for (int i = 0; i < eScript.size(); i++) {
						List<org.jsoup.nodes.Node> tempChildren = eScript.get(i).childNodes();
						for (int j = 0; j < tempChildren.size(); j++) {
							Node node = tempChildren.get(j);
							for (int k = 0; k < scriptArray.size(); k++) {
								String contain = scriptArray.get(k).getAsString();
								if (node.attributes().get("data").contains(contain)) {
									return true;
								}
							}
						}
					}
				}
			}
			
			//Check elements based on ATTRIBUTES
			if (errorSigns.keySet().contains("attribute")) {
				JsonObject attObject = errorSigns.getAsJsonObject("attribute");
				if (attObject.keySet().contains("name") && 
						attObject.getAsJsonArray("name").size()>0) {
					JsonArray nameArray = attObject.get("name").getAsJsonArray();
					for (int i = 0; i < nameArray.size(); i++) {
						String attName = nameArray.get(i).getAsString();
						Elements element = doc.getElementsByAttribute(attName);
						if (element != null) {
							return true;
						}
					}
				}
				if (attObject.keySet().contains("nameValue") && 
						attObject.getAsJsonArray("nameValue").size()>0) {
					JsonArray nameValueArray = attObject.get("nameValue").getAsJsonArray();
					for (int i = 0; i < nameValueArray.size(); i++) {
						JsonObject nameValueObject = nameValueArray.get(i).getAsJsonObject();
						for (String key : nameValueObject.keySet()) {
							Elements element = doc.getElementsByAttributeValue(key,
									nameValueObject.get(key).getAsString());
							if (element != null) {
								return true;
							}
						}
					}
				}
			}
			
			//Check elements based on IDs
			if (errorSigns.keySet().contains("id") && 
					errorSigns.getAsJsonArray("id").size()>0) {
				JsonArray idObject = errorSigns.getAsJsonArray("id");
				for (int i = 0; i < idObject.size(); i++) {
					String id = idObject.get(i).getAsString();
					Element eID = doc.getElementById(id);
					if (eID != null) {
						return true;
					}
				}
			}
			
			//Check page title
			if (errorSigns.keySet().contains("title") && 
					errorSigns.getAsJsonArray("title").size()>0) {
				JsonArray titleObject = errorSigns.getAsJsonArray("title");
				for (int i = 0; i < titleObject.size(); i++) {
					String titleError = titleObject.get(i).getAsString().toLowerCase();
					String title = doc.getElementsByTag("title").text().toLowerCase();
					if(title.contains(titleError)){
						return true;
					}
				}
			}
			
			//Check http response status code
			if( ((WebOutputCleaned)out).getStatusCode() >= 400){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean hasStrictTransportSecurityHeader() {
		if(seq==null || seq.size()<1){
			return false;
		}
		WebOutputCleaned lastOut = (WebOutputCleaned) seq.get(seq.size()-1);
		return lastOut.hasStrictTransportSecurityHeader();
	}

	@Override
	public String getChannel() {
		if(seq==null || seq.size()<1){
			return null;
		}
		WebOutputCleaned lastOut = (WebOutputCleaned) seq.get(seq.size()-1);
		return lastOut.getChannel();
	}
	
	public ArrayList<Object> getOutputSequence(){
		return this.seq;
	}
	
	public Object getOutputAt(int pos){
		if(this.seq.size()>pos && pos>=0){
			return seq.get(pos);
		}
		return null;
	}



	@Override
	public String toString() {
		return "WebOutputSequence [seq=" + seq + ", redirectedURLs="
				+ redirectedURLs +"]";
	}

	@Override
	public Session getSession() {
		//by defaut get the last session status
		int size = this.sessionSequence.size();
		if(size>0){
			return this.sessionSequence.get(this.sessionSequence.size()-1);
		}
		else{
			return null;
		}
	}
	
	@Override
	public Session getSession(int pos) {
		if(pos<0 || pos>=this.sessionSequence.size()){
			return null;
		}
		return this.sessionSequence.get(pos);
	}

	public void setSession(ArrayList<CookieSession> session) {
		this.sessionSequence = session;
	}

	public void addSession(CookieSession session) {
		this.sessionSequence.add(session);
	}





	@Override
	public boolean isEmptyFile() {
		//true if the last output containing an empty file:
		//- no output: false
		//- output without downloaded file: false
		//- output with an empty file: true
		//- output with a nonempty file: false
		
		if(seq.size()<1) {
			System.out.println("\t!!! no output (from isEmptyFile)");
			return false;
		}
		
		WebOutputCleaned lastOutput = (WebOutputCleaned)seq.get(seq.size()-1);
		if(lastOutput==null || lastOutput.downloadedFile==null) {
			System.out.println("\t!!! no downloaded file (from isEmptyFile)");
			return false;
		}
		
		if(lastOutput.downloadedFile.isFile() && lastOutput.downloadedFile.length()<=0) {
			System.out.println("\t!!! downloaded file is EMPTY (from isEmptyFile)");
			return true;
		}
		
		System.out.println("\t!!! downloaded file is NONempty (from isEmptyFile)");
		return false;
	}





	@Override
	public boolean noFile() {
		if(seq.size()<1) {
			System.out.println("\t!!! no output (from noFile)");
			return true;
		}
		
		WebOutputCleaned lastOutput = (WebOutputCleaned)seq.get(seq.size()-1);
		if ( lastOutput==null ) {
			System.out.println("\t!!! no downloaded file (from noFile) null lastOutput");
		}
		if(lastOutput==null || lastOutput.downloadedFile==null) {
			System.out.println("\t!!! no downloaded file (from noFile)");
			return true;
		}
		
		return false;
	}





	@Override
	public File file() {
		if(seq.size()<1) {
			return null;
		}
		
		WebOutputCleaned lastOutput = (WebOutputCleaned)seq.get(seq.size()-1);
		if(lastOutput==null) {
			return null;
		}

		return lastOutput.downloadedFile;
	}





	@Override
	public boolean containListOfTags() {
		
		List<CollectionOfConcepts> tagsList = listsOfTags();
		
		boolean result = false;
		
		if(tagsList!=null && tagsList.size()>0) {
			result = true;
		}
		
		
//		System.out.println("\t!!! Call containListOfTags: "+result);
		return result;
	}





	@Override
	public List<CollectionOfConcepts> listsOfTags() {
//		System.out.print("\t!!! Call listsOfTags: ");
		if(this.seq==null || this.seq.size()<1) {
//			System.out.println("null");
			return null;
		}
		ArrayList<CollectionOfConcepts> result = new ArrayList<CollectionOfConcepts>();
		
		for(Object out:seq) {
			if(!(out instanceof WebOutputCleaned)) {
				continue;
			}
			ArrayList<CollectionOfConcepts> newList = ((WebOutputCleaned)out).getAllConcepts();
			if(newList!=null && newList.size()>0) {
				result.addAll(newList);
			}
		}
		
//		System.out.println(result);
		
		return result;
	}





	@Override
	public CollectionOfConcepts listOfTags(String key) {
		List<CollectionOfConcepts> tagsList = listsOfTags();
		if(tagsList==null || tagsList.size()<1) {
			return null;
		}
		
		for(CollectionOfConcepts cc:tagsList) {
			if(cc.id.equals(key)) {
				return cc;
			}
		}
		
		return null;
	}





	

	
}
