package com.example.sen.finddownload;

import junit.framework.TestCase;

import javax.crypto.SecretKey;

/**
 * Created by Sen on 23/08/2016.
 */
public class AESEncryptionTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void test() throws Exception {
        String s = "hello";
        SecretKey key = AESEncryption.generateKey();
        byte[] a = s.getBytes();
        byte[] cipher = AESEncryption.aesEncrypt(a, key);
        System.out.println(new String(cipher));

        byte[] decipher = AESEncryption.aesDecrypt(cipher, key);
        String p = new String(decipher);
        System.out.println(p);
        assertEquals(p, s);
    }
}