import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;

import org.junit.Test;

public class DigitalSignatureTest {

	@Test
	public void test() throws ClassNotFoundException, FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
//
//		DigitalSignature xo = new DigitalSignature();
//		xo.generateKey();
//		
		@SuppressWarnings("resource")
		final PublicKey publicKey = (PublicKey) new ObjectInputStream(new FileInputStream("./key/publicDS_test.key"))
				.readObject();
		
		@SuppressWarnings("resource")
		final PrivateKey privateKey = (PrivateKey) new ObjectInputStream(new FileInputStream("./key/privateDS_test.key"))
				.readObject();
		
		DigitalSignature ds = new DigitalSignature();
		byte[] a = new byte[10000000];
		for (int j = 0; j < a.length; j++) {
			a[j] = 1;
		}
		
		String s = new String(a);
		
		final long startTimeNano = System.nanoTime();
		ds.setDigest(s);
		byte[] signature = ds.signSignature(privateKey, ds.getDigest());
		final long endTimeNano = System.nanoTime();
		System.out.println("Sign " + (endTimeNano - startTimeNano) / 1000 + " " + signature.length);
		
		final long startTimeNano1 = System.nanoTime();
		ds.verifySignature(publicKey, signature, ds.getDigest());
		final long endTimeNano1 = System.nanoTime();
		System.out.println("Verify " + (endTimeNano1 - startTimeNano1) / 1000 );
			
		// removed
//		String b = "2-1-.pdf";
//		DigitalSignature bp = new DigitalSignature();
//		bp.setDigest(b);
//		System.out.println(ds.verifySignature(publicKey, signature, bp.getDigest()));
//		
//		// modified
//		String c = "2-1-2.pdf";
//		DigitalSignature cp = new DigitalSignature();
//		cp.setDigest(c);
//		System.out.println(ds.verifySignature(publicKey, signature, cp.getDigest()));
//		
//		// enriched
//		String d = "2-1-211.pdf";
//		DigitalSignature dp = new DigitalSignature();
//		dp.setDigest(d);
//		System.out.println(ds.verifySignature(publicKey, signature, dp.getDigest()));
//		
//		
////		
//		assertEquals(true, ds.verifySignature(publicKey, signature, ds.getDigest()));
		
	}

}
