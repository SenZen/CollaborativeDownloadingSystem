import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Utility class for encrypting/decrypting files.
 * 
 * @author Michael Lones
 */
public class FileEncryption {

	public static final int AES_KEY_SIZE = 128;

	Cipher aesCipher;
	byte[] aesKey;
	SecretKeySpec aeskeySpec;
	
	public void setKey(byte[] aesKey) {
		this.aesKey = aesKey;
	}
	
	public byte[] getKey() {
		return this.aesKey;
	}

	/**
	 * Constructor: creates ciphers
	 */
	public FileEncryption() throws GeneralSecurityException {
		// create AES shared key cipher
		aesCipher = Cipher.getInstance("AES");
	}

	/**
	 * Creates a new AES key
	 */
	public SecretKey makeKey() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(AES_KEY_SIZE);
		SecretKey key = kgen.generateKey();
		return key;
	}

	/**
	 * Encrypts and then copies the contents of a given file.
	 */
	public void encrypt(File in, File out) throws IOException, InvalidKeyException {
		aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

		FileInputStream is = new FileInputStream(in);
		CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

		copy(is, os);

		os.close();
	}

	/**
	 * Decrypts and then copies the contents of a given file.
	 */
	public void decrypt(File in, File out) throws IOException, InvalidKeyException {
		aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

		CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
		FileOutputStream os = new FileOutputStream(out);

		copy(is, os);

		is.close();
		os.close();
	}

	/**
	 * Copies a stream.
	 */
	private void copy(InputStream is, OutputStream os) throws IOException {
		int i;
		byte[] b = new byte[1024];
		while ((i = is.read(b)) != -1) {
			os.write(b, 0, i);
		}
	}
}