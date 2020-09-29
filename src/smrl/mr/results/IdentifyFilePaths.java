package smrl.mr.results;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import smrl.mr.utils.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

public class IdentifyFilePaths {

	public static void main(String[] args) throws IOException {
		File file = new File( args[0] );
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		
		HashSet<String> visited = new HashSet<>();
		HashSet<String> result = new HashSet<>();
		while ( ( line = r.readLine() ) != null ) {
//			if (!FileUtil.isFile(line)) {
//				continue;
//			}
			if ( line.endsWith(".jar") ) {
				continue;
			}
			if ( line.endsWith(".png") ) {
				continue;
			}
			if ( line.endsWith(".gif") ) {
				continue;
			}
			if ( line.endsWith(".js") ) {
				continue;
			}
			if ( line.endsWith(".css") ) {
				continue;
			}
			if ( line.endsWith(".jpi") ) {
				continue;
			}
			if ( line.endsWith(".svg") ) {
				continue;
			}
			if ( line.endsWith(".ttf") ) {
				continue;
			}
			if ( line.endsWith(".eot") ) {
				continue;
			}
			if ( line.endsWith(".woff") ) {
				continue;
			}
			if ( line.endsWith(".html") ) {
				continue;
			}
			if ( line.endsWith(".php") ) {
				continue;
			}
			int pos = line.lastIndexOf('/');
			if ( pos > 0 ) {
				String f = line.substring(pos);
				if ( visited.contains(f) ) {
					continue;
				}
				// if f is a file (containing ".")
				if(f.contains(".")) {
					visited.add(f);
					result.add(line);
				}
			}
//			line = line.replace("plugins/", "plugin/");
//			System.out.println(line);
		}
		
		r.close();
		
//		System.out.println(result);
		
		for (String l:result) {
			System.out.println(l);
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
