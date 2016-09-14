import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Test;

public class RSAEncryptionTest {

	@Test
	public void test() throws ClassNotFoundException, FileNotFoundException, IOException {
		@SuppressWarnings("resource")
		final PublicKey publicKey = (PublicKey) new ObjectInputStream(new FileInputStream("./key/publicDS_test.key"))
				.readObject();
		
		@SuppressWarnings("resource")
		final PrivateKey privateKey = (PrivateKey) new ObjectInputStream(new FileInputStream("./key/privateDS_test.key"))
				.readObject();
		
		String s = "2-2-1.pdf";
		
		byte[] ciphertext = RSAEncryption.encrypt(s.getBytes(), publicKey);
		
		System.out.println("message: " + s);
		System.out.println("ciphertext:" + new String(ciphertext));
		
		assertArrayEquals(s.getBytes(), RSAEncryption.decrypt(ciphertext, privateKey));
	}

}
