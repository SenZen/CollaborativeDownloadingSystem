import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.Test;

public class AESEncryptionTest {

	@Test
	public void test() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String s = "2-1-1.pdf";
        
		byte[] a = new byte[3000000];
		for (int j = 0; j < a.length; j++) {
			a[j] = 1;
		}
		
        SecretKey key = AESEncryption.generateKey();
//        byte[] a = s.getBytes();
        
        	final long startTimeNano = System.nanoTime();
            byte[] cipher = AESEncryption.aesEncrypt(a, key);
            final long endTimeNano = System.nanoTime();
    		System.out.println("En " + (endTimeNano - startTimeNano) / 1000 );
    		
    		
    		// long startTime = System.currentTimeMillis();

//    		byte[] y = DESEncryption.aesDecrypt(x, key, iv);
    		final long startTimeNano1 = System.nanoTime();
            byte[] decipher = AESEncryption.aesDecrypt(cipher, key);

    		final long endTimeNano1 = System.nanoTime();
    		System.out.println("De " + (endTimeNano1 - startTimeNano1) / 1000 );

//        byte[] decipher = AESEncryption.aesDecrypt(cipher, key);
//        String p = new String(decipher);
//        System.out.println(s);
//        System.out.println(new String(cipher));
//        assertEquals(p, s);
	}

}
