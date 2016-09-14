import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MergeFiles {
	
	public static int mergeFiles(List<File> listFile, OutputStream os) throws IOException {
		int totalRead = 0;
		byte[] buffer = new byte[2048];
		for (File file : listFile) {
		InputStream is = new FileInputStream(file);
		
		int read = 0, totalPartRead = 0;
		while((read = is.read(buffer)) != -1) {
			os.write(buffer, 0, read);
			totalPartRead += read;
			totalRead += read;
		}	
		}
		
		return totalRead; 
	}
	
	public static void mergeLargeFile() {
		try {
			String[] env = {"PATH=/bin:/usr/bin/"};
			String cmd = "cat 1 2 > 4.pdf"; 
		
			Process process2 = Runtime.getRuntime().exec(cmd, env);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
