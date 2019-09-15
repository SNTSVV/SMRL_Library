package smrl.mr.crawljax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import com.google.common.base.Charsets;

public class WebOutputCleaned {
	
	private static boolean useEditDistance=true;
	private static final float similarityThreshold_Html = (float) 0.95;
	private static final float similarityThreshold_Text = (float) 0.7;
	
	public String resultedUrl;
	public String html;
	public String originalHtml;
	public String text;
	public File downloadedFile;
	public HashMap<String,String> downloadedObjects;
	public int statusCode;
	
	
	public WebOutputCleaned() {
		this.resultedUrl = null;
		this.html = null;
		this.originalHtml = null;
		this.text = null;
		this.downloadedFile = null;
		this.downloadedObjects = null;
		this.statusCode = -1;
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
	
}