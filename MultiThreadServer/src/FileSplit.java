import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileSplit {
	public static void splitFile(File file, int n) throws IOException {
		try {
			if (file.exists()) {
				String fileName = file.getName().substring(0, file.getName().lastIndexOf(".")); 
				File splitFile = new File(fileName);// Destination folder to
													// save.
				if (!splitFile.exists()) {
					// splitFile.mkdirs();
					// System.out.println("Directory Created -> "+
					// splitFile.getAbsolutePath());
				}

				int i = 1;// Files count starts from 1
				InputStream inputStream = new FileInputStream(file);
				String targetLocation = splitFile.getAbsolutePath() + "_" + i;
				OutputStream outputStream = new FileOutputStream(targetLocation);
				System.out.println("File Created Location: " + targetLocation);

				int splitSize = inputStream.available() / n;
				int streamSize = 0;
				int read = 0;

				while ((read = inputStream.read()) != -1) {
					if (splitSize == streamSize) {
						if (i != n) {
							i++;
							String mySplitFile = splitFile.getAbsolutePath() + "_" + i;
							outputStream = new FileOutputStream(mySplitFile);

							streamSize = 0;
						}
					}
					outputStream.write(read);
					streamSize++;
				}

				inputStream.close();
				outputStream.close();
				System.out.println("Total files Split -> " + n);
			} else {
				System.err.println(file.getAbsolutePath() + " File Not Found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void splitLargeFile(File file, int n) throws InterruptedException {
		try {
			String[] env = {"PATH=/bin:/usr/bin/"};
			String cmd = "split -b " + file.length() / n + " " + file.getAbsolutePath() + " ";
			String cmd1 = "mv xaa 1";
			String cmd2 = "mv xab 2";
			Process process = Runtime.getRuntime().exec(cmd, env);
			process.waitFor();
			Process process1 = Runtime.getRuntime().exec(cmd1, env);
			process1.waitFor();
			Process process2 = Runtime.getRuntime().exec(cmd2, env);
			process2.waitFor();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
