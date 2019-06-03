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
}
