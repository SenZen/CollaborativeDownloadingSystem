import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryption {
	public static final String ALGORITHM = "RSA";
	public static final String PRIVATE_KEY_FILE = "private.key";
	public static final String PUBLIC_KEY_FILE = "public.key";

	public static void generateKey()
			throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
		kpg.initialize(2048);
		KeyPair keyPair = kpg.generateKeyPair();

		File privateKeyFile = new File(PRIVATE_KEY_FILE);
		File publicKeyFile = new File(PUBLIC_KEY_FILE);

		// Create files to store public and private key
		if (privateKeyFile.getParentFile() != null) {
			privateKeyFile.getParentFile().mkdirs();
		}
		privateKeyFile.createNewFile();

		if (publicKeyFile.getParentFile() != null) {
			publicKeyFile.getParentFile().mkdirs();
		}
		publicKeyFile.createNewFile();

		// Saving the Public key in a file
		ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
		publicKeyOS.writeObject(keyPair.getPublic());
		publicKeyOS.close();

		// Saving the Private key in a file
		ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
		privateKeyOS.writeObject(keyPair.getPrivate());
		privateKeyOS.close();
	}

	/**
	 * The method checks if the pair of public and private key has been
	 * generated.
	 * 
	 * @return flag indicating if the pair of keys were generated.
	 */
	public static boolean areKeysPresent() {

		File privateKey = new File(PRIVATE_KEY_FILE);
		File publicKey = new File(PUBLIC_KEY_FILE);

		if (privateKey.exists() && publicKey.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Encrypt the plain text using public key.
	 * 
	 * @param text
	 *            : original plain text
	 * @param key
	 *            :The public key
	 * @return Encrypted text
	 * @throws java.lang.Exception
	 */
	public static byte[] encrypt(byte[] message, PublicKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	/**
	 * Decrypt text using private key.
	 * 
	 * @param text
	 *            :encrypted text
	 * @param key
	 *            :The private key
	 * @return plain text
	 * @throws java.lang.Exception
	 */
	public static byte[] decrypt(byte[] text, PrivateKey key) {
		byte[] dectyptedText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(text);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return dectyptedText;
	}

}
