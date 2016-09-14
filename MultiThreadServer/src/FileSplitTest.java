import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FileSplitTest {

	@Test
	public void test() throws IOException {
		String fileLocation = "/Users/senzheng/Desktop/1.pdf";
		File myFile = new File(fileLocation);
		FileSplit.splitFile(myFile, 2);
	}

}
