package smrl.mr.crawljax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Charsets;

import smrl.mr.language.CollectionOfConcepts;

public class WebOutputCleaned {
	
	private static boolean useEditDistance=true;
	private static final float similarityThreshold_Html = (float) 0.95;
	private static final float similarityThreshold_Text = (float) 0.7;
	private static final int CONCEPT_LENGHT_LIMIT = 25;
	
	public String resultedUrl;
	public String html;
	public String originalHtml;
	public String text;
	public File downloadedFile;
	public HashMap<String,String> downloadedObjects;
	public int statusCode;
	private ArrayList<CollectionOfConcepts> allConcepts;
	public String realRequestedUrl; 
	public String realClickedElementText;
	
	
	public WebOutputCleaned() {
		this.resultedUrl = null;
		this.html = null;
		this.originalHtml = null;
		this.text = null;
		this.downloadedFile = null;
		this.downloadedObjects = null;
		this.statusCode = -1;
		this.allConcepts = new ArrayList<CollectionOfConcepts>();
		this.realRequestedUrl = null;
		this.realClickedElementText = null;
	}



	public boolean isUseEditDistance() {
		return useEditDistance;
	}



	public void setUseEditDistance(boolean useEditDistance) {
		WebOutputCleaned.useEditDistance = useEditDistance;
	}
	
	public void setDownloadedFile(File file) {
		this.downloadedFile = file;
	}

	public boolean compare(WebOutputCleaned o2) {
		if(compareWebOutputCleaned(this, o2)==false){
			return false;
		}
		
		if ( this.downloadedFile != null ){ 
			if ( !  this.downloadedFile.getName().equals(o2.downloadedFile.getName()) ){
				return false;
			}
		} else if ( o2.downloadedFile != null ){ //o1 is null 
			return false;
		}
		
		//additional checks if needed
		
		
		return true;
	}
	
	/**
	 * @param o1
	 * @param o2
	 */
	public static boolean compareWebOutputCleaned(WebOutputCleaned o1, WebOutputCleaned o2) {
		if ( _compare(o1.html, o2.html, similarityThreshold_Html)==false 
				&& _compare(o1.text, o2.text, similarityThreshold_Text)==false ){
			return false;
		}
		return true;
	}
	
	private static boolean _compare(String o1, String o2, float threshold) {

		if(useEditDistance){
			LevenshteinDistance measurer = new LevenshteinDistance();
			int editDistance = measurer.apply(o2, o1);
			float similarity = (float) ((float)1.0 - 
					((float)editDistance)/((float)Math.max(o1.length(), o2.length())));
			
//			if(threshold==similarityThreshold_Html){
//				System.out.println("-- HTML similarity: " + similarity);
//			}
//			else if(threshold==similarityThreshold_Text){
//				System.out.println("-- TEXT similarity: " + similarity);
//			}
			
			if(similarity<threshold){
				return false;
			}
		}
		else if (!o1.equals(o2)) {
			return false;
		}
		
		return true;
	}



	public boolean hasStrictTransportSecurityHeader() {
		if(resultedUrl!=null && resultedUrl.trim().toLowerCase().startsWith("https")){
			return true;
		}
		return false;
	}



	public String getChannel() {
		if(resultedUrl!=null && !resultedUrl.isEmpty()){
			int sepIndex = resultedUrl.indexOf("://");
			if(sepIndex>0){
				return resultedUrl.substring(0, sepIndex).toLowerCase();
			}
		}
		return null;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	



	public boolean compare(String content) {
		if(content ==null) {
			return false;
		}
		
		// firstly conpare with page content
		if (_compare(this.text, content, similarityThreshold_Text)) {
			return true;
		}

		// compare with downloaded file (if having)
		if ( this.downloadedFile != null ){ 
			try {
				String fileContent = FileUtils.readFileToString(this.downloadedFile, Charsets.UTF_8);

				if(fileContent!=null && !fileContent.isEmpty()) {
					return _compare(fileContent, content, similarityThreshold_Text);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return false;
	}
	
	
	
	public ArrayList<CollectionOfConcepts> getAllConcepts() {
		if(allConcepts!=null && allConcepts.size()>0) {
			return allConcepts;
		}
		
		extractConcepts();
		return allConcepts;
	}



	/**
	 * @return true if successfully extracting concepts from html
	 */
	public boolean extractConcepts() {
		
		if(this.html==null || this.html.trim().isEmpty()) {
			return false;
		}
		
		//currentDOM is not null
		Document currentDoc = Jsoup.parse(html);
		
		//Hashmap of concepts list
		HashMap<String, ArrayList<String>> currentConcepts = new HashMap<String, ArrayList<String>>();
		
		//1. from heading (h1, h2, h3, h4, h5, h6)
		// strategy: if a heading does not contain any sub-heading, consider it as a concept
		for(int i=1; i<=6; i++) {
			Elements hiTags = currentDoc.getElementsByTag("h"+i);
			
			if(hiTags!=null && hiTags.size()>0) {
				for(Element hTag:hiTags) {
					if(!containSubHeading(hTag)) {
						addHeadingConcepts(currentConcepts, hTag);
					}
				}
			}
			
		}
		
		//2. from a list (select tag)
		Elements selectTags = currentDoc.getElementsByTag("select");
		if(selectTags!=null && selectTags.size()>0) {
			for(Element sec:selectTags) {
				String key = getId(sec);
				ArrayList<String> values = new ArrayList<String>();
				Elements allOptions = sec.children();
				if(allOptions!=null) {
					for(Element op:allOptions) {
						if(op.tagName().toLowerCase().equals("option") &&
								!CollectionOfConcepts.isIgnoredConcept(op.text())) {
							values.add(op.text().trim());
						}
					}
				}
				if(values.size()>0) {
					currentConcepts.put(key, values);
				}
			}
		}

		
		//reset the concepts
		this.allConcepts = new ArrayList<CollectionOfConcepts>();
		
		if(currentConcepts.size()>0) {
			for(String key: currentConcepts.keySet()) {
				if(passConceptFilter(currentConcepts.get(key))) {
					CollectionOfConcepts cc = new CollectionOfConcepts();
					cc.id = key;
					cc.concepts.addAll(currentConcepts.get(key));
					this.allConcepts.add(cc);
				}
			}
		}
		
		return (this.allConcepts.size()>0);
	}



	private boolean passConceptFilter(ArrayList<String> conceptsList) {
		if(conceptsList==null || conceptsList.size()<1) {
			return false;
		}
		
		for(String concept:conceptsList) {
			if(!(concept!=null && !concept.isEmpty() &&
					concept.length()<CONCEPT_LENGHT_LIMIT)) {
				return false;
			}
		}

		return true;
	}



	private boolean containSubHeading(Element element) {
		if(element==null || element.childNodeSize()<1) {
			return false;
		}
		
		for(Element subEle:element.children()) {
			if(isHeadingElement(subEle)) {
				return true;
			}
			else {
				if( containSubHeading(subEle)) {
					return true;
				}
			}
		}
		
		return false;
	}



	private boolean isHeadingElement(Element subEle) {
		if(subEle==null) {
			return false;
		}
		
		String tag = subEle.tagName().toLowerCase().trim();
		if(tag.length()==2 && tag.startsWith("h") && !tag.endsWith("r")) {
			return true;
		}
		
		return false;
	}



	private void addHeadingConcepts(HashMap<String, ArrayList<String>> conceptMap, Element ele) {
		
		String id = getSpecifiParentID(ele);
		
		if(conceptMap==null) {
			conceptMap = new HashMap<String, ArrayList<String>>();
		}
		
		//1. check if there is already a set of Concepts with having the same id with the ele
		if(conceptMap.keySet().contains(id)){
			conceptMap.get(id).add(ele.text());
		}
		//2. if the concepMap 
		else {
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(ele.text());
			conceptMap.put(id, newList);
		}
	}



	private String getSpecifiParentID(Element ele) {
		//ID of the element is the id of ancestor (h tag, or body tag)
		Element specParent = specificParent(ele);
		
		if(specParent==null) {	//If the specParent is null, assume that ele is its parent
			specParent = ele;
		}
		
		return getId(specParent);
		
//		String result = specParent.id();
//		if(result==null || result.isEmpty()) {
//			//Get element's name
//			result = specParent.attr("name");
//			if(result==null || result.isEmpty()) {
//				//get element's xpath
//				result = getXPath(specParent);
//			}
//		}
//		return result;
	}
	
	private String getId(Element ele) {
		if(ele==null) {	//If the specParent is null, assume that ele is its parent
			return "";
		}
		
		//Get element's id
		String result = ele.id();
		if(result==null || result.isEmpty()) {
			//Get element's name
			result = ele.attr("name");
			if(result==null || result.isEmpty()) {
				//get element's xpath
				result = getXPath(ele);
			}
		}
		return result;
	}


	/**
	 * Find the closest ancestor of an element. The closest ancestor must be a body or heading tag
	 * @param ele the current element to find ancestor
	 * @return the closest ancestor, which is a body or heading tag
	 */
	private Element specificParent(Element ele) {
		if(ele==null || ele.parent()==null) {
			return null;
		}
		
		Element result = ele.parent();
		
		while(!bodyMainOrHeadingTag(result) &&
				result.parent()!=null) {
			result = result.parent();
		}
		
		if(!bodyMainOrHeadingTag(result)) {
			result = null;
		}
		return result;
	}

	private boolean bodyMainOrHeadingTag(Element ele) {
		if(ele==null) {
			return false;
		}
		
		String resultTag = ele.tagName().toLowerCase().trim();
		if(resultTag.toLowerCase().equals("body") ||
				resultTag.equals("main") ||
				(resultTag.length()==2 && resultTag.startsWith("h") && !resultTag.endsWith("r"))) {
			return true;
		}
		return false;
	}

	public String getXPath(Element ele) {
		if(ele==null) {
			return "";
		}
		
		String xpath = ele.tagName();
		int indexSameTag = 1;
		Element previousEle = ele.previousElementSibling();
		while(previousEle!=null) {
			if(ele.tagName().equals(previousEle.tagName())) {
				++indexSameTag;
			}
			previousEle = previousEle.previousElementSibling();
		}
		if(indexSameTag>1) {
			xpath += "[" + indexSameTag + "]";
		}
		
		
		if(ele.parent()==null || ele.tagName().toLowerCase().equals("html")) {
			return "/" + xpath;
		}
		
		return getXPath(ele.parent()) + "/" + xpath;
	}
	
}