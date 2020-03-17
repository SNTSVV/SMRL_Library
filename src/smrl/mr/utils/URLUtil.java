package smrl.mr.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class URLUtil {

	public static String extractActionURL( String url ) {
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
		
		return url;
	}
	
	public static boolean hasTheSameUrl(String url1, String url2) {
		if (url1==null || url2==null) {
			return false;
		}
		
		String thisUrl = url1;
		String thatUrl = url2;
		
		thisUrl = standardUrl(thisUrl);
		thatUrl = standardUrl(thatUrl);
		
		return thisUrl.equals(thatUrl);
	}

	public static String standardUrl(String url) {
		if(url==null) {
			return null;
		}
		url = url.trim().toLowerCase();
		while(url.endsWith("/") ||
				url.endsWith("#")) {
			url = url.substring(0, url.length()-1);
		}
		return url;
	}
	
	public static boolean getOrPost(String method) {
		return (method!=null && 
				(method.equalsIgnoreCase("get") ||
						method.equalsIgnoreCase("post")));
	}
	
	/** Check if a URL contain a given extention in the path or in a query
	 * @param url the URL to check
	 * @param ext the given extention, e.g., ".js"
	 * @return true if the URL contain the given extention in its path or query
	 */
	public static boolean containExtension(String url, String ext) {
		if (url==null || url.isEmpty() ||
				ext==null || ext.isEmpty()) {
			return false;
		}
		String path = "";
		String query = "";
		try {
			URI uri = new URI (url.toLowerCase());
			path = uri.getPath();
			query = uri.getQuery();
			
			String extLower = ext.toLowerCase();
			
			if(path!=null && path.endsWith(extLower)) {
				return true;
			}
			
			if(query!=null) {
				for(String q:query.split("&")) {
					if(q.endsWith(extLower)) {
						return true;
					}
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
}
