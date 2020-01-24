package smrl.mr.utils;

import java.io.File;

public class FileUtil {
	
	public static boolean exist(String fileName) {
		File f = new File(fileName);
		return (f.exists() && f.isFile());
	}
	
}
