package smrl.mr.utils;

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
}
