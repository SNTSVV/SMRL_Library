package smrl.mr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
	
	public static boolean exist(String fileName) {
		File f = new File(fileName);
		return (f.exists() && f.isFile());
	}
	
	public static String readLines(String filePath) 
	{
		if(!exist(filePath)) {
			return null;
		}
		
		String content = "";

		try
		{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return content;
	}
	
}
