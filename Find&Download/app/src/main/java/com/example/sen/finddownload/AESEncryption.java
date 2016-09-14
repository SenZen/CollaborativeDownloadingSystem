package com.example.sen.finddownload;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {
    public static final String ALGORITHM = "AES";

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kpg = KeyGenerator.getInstance(ALGORITHM);
        kpg.init(128);
        SecretKey secretKey = kpg.generateKey();
        return secretKey;
    }

    public static byte[] aesEncrypt(byte[] data, SecretKey secretKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }

    public static byte[] aesDecrypt(byte[] cipherData, SecretKey secretKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decipherData = cipher.doFinal(cipherData);
        return decipherData;
    }

}
