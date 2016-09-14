import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;

public class DigitalSignature {

	public static final String PRIVATE_DSKEY_FILE = "privateDS_test.key";
	public static final String PUBLIC_DSKEY_FILE = "publicDS_test.key";
	public static final String SIGNATURE_KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	public static final String DIGEST_ALGORITHM = "SHA1";
	public static final int RSA_KEY_SIZE = 1024;

	static String message;
	static Signature instance;
	static MessageDigest sha1;
	public byte[] digest;

	public DigitalSignature() throws NoSuchAlgorithmException {
		// create signature shared key cipher
		instance = Signature.getInstance(SIGNATURE_ALGORITHM);
		sha1 = MessageDigest.getInstance(DIGEST_ALGORITHM);
	}

	public void setDigest(String message) {
		digest = sha1.digest((message).getBytes());
	}
	
	public byte[] getDigest() {
		return this.digest;
	}

	public void generateKey()
			throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(SIGNATURE_KEY_ALGORITHM);
		kpg.initialize(RSA_KEY_SIZE);
		KeyPair keyPair = kpg.generateKeyPair();

		File privateKeyFile = new File(PRIVATE_DSKEY_FILE);
		File publicKeyFile = new File(PUBLIC_DSKEY_FILE);

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

	public byte[] signSignature(PrivateKey key, byte[] digest)
			throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		// Compute digest
		instance.initSign(key);
		instance.update(digest);
		// instance.update(message.getBytes("UTF8"));
//		System.out.println("Singature:" + new String(instance.sign(), StandardCharsets.UTF_8));
		return instance.sign();
	}

	public boolean verifySignature(PublicKey key, byte[] signature, byte[] digest)
			throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		instance.initVerify(key);
		instance.update(digest);
		return instance.verify(signature);
	}

}
