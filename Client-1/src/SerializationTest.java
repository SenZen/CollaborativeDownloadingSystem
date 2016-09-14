import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.junit.Test;

public class SerializationTest {

	@Test
	public void test() throws NoSuchAlgorithmException, ClassNotFoundException, IOException {
		SecretKey key = AESEncryption.generateKey();
		
		assertEquals(true, Serialization.serialize(key).getClass().isArray());
		
		assertEquals(true, key.equals(Serialization.deserialize(Serialization.serialize(key))));
	}

}
