import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MergeFilesTest {
	static String messageToServer = "2-2-1.pdf";
	static String[] str = messageToServer.split("-");
	static String[] fix = messageToServer.split("\\.");

	@Test
	public void test() throws IOException {
		List<File> listFile = new ArrayList<File>();
		for (int i = 1; i <= Integer.parseInt(str[1]); i++) {
			listFile.add(new File(Integer.toString(i)));
		}
		
		FileOutputStream fos = new FileOutputStream(str[0] + "." + fix[1]);
		MergeFiles.mergeFiles(listFile, fos);
	}

}
