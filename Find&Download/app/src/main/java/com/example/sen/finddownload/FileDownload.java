package com.example.sen.finddownload;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by Sen on 27/07/2016.
 */

public class FileDownload {

    public static boolean signatureResult = false;

    public static void downloadFile(String serverLocation, int port, String message, PrivateKey privatekey, PublicKey publicDSkey, SecretKey AESkey) throws Exception{
        Socket socket = null;
        String[] str = message.split("-");
        String[] fix = message.split("\\.");

        try {
            // execute this when the downloader must be fired
            Log.d("Location", serverLocation + " " + port);
            socket = new Socket(serverLocation, port);
            Echo.echo("Connecting to" + " " + socket.getInetAddress().getHostName() + " : " + socket.getPort());

//            // receive AES signature from server
//            DataInputStream disAESSignature = new DataInputStream(socket.getInputStream());
//
//            int disAESDigitalSignatureLength = disAESSignature.readInt();
//            byte[] AESSignatureBytes = new byte[disAESDigitalSignatureLength];
//            if (disAESDigitalSignatureLength > 0) {
//                disAESSignature.readFully(AESSignatureBytes, 0, AESSignatureBytes.length);
//            }
//
//            // decrypt the signature
//            Log.v(" s", Integer.toString(AESSignatureBytes.length));
//            byte[] decrytedAESSignature = RSAEncryption.decrypt(AESSignatureBytes, privatekey);
//
//
//            // receive the AES key
//            DataInputStream disAES = new DataInputStream(socket.getInputStream());
//            int disAESLength = disAES.readInt();
//            byte[] byteAES = new byte[disAESLength];
//            if (disAESLength > 0) {
//                disAES.readFully(byteAES, 0, byteAES.length);
//            }
//
//            // decrypt the AES key
//            byte[] decrytedAESKey = RSAEncryption.decrypt(byteAES, privatekey);
//
//            // save the file locally
//            FileOutputStream fos = new FileOutputStream("./assets/AES.key");
//            fos.write(decrytedAESKey);
//
//            // verify the signature
//            SecretKey AESkey = (SecretKey) Serialization.deserialize(decrytedAESKey);
//            DigitalSignature dsAES = new DigitalSignature();
//            dsAES.setDigest(AESkey.toString());
//
//            signatureResult = dsAES.verifySignature(publicDSkey, decrytedAESSignature, dsAES.getDigest());
//            System.out.println("Is AES key valid? : " + signatureResult);
//            signatureResult = false;

            // Send the message to the file server
            byte[] messageByte = message.getBytes();
            DataOutputStream dosMessage = new DataOutputStream(socket.getOutputStream());
            dosMessage.writeInt(messageByte.length);
            dosMessage.write(messageByte);
            System.out.println("Message sent to the server is " + message);

            // receive the signature
            DataInputStream disFileSignature = new DataInputStream(socket.getInputStream());

            int disFileSignatureLength = disFileSignature.readInt();
            byte[] fileSignatureBytes = new byte[disFileSignatureLength];
            if (disFileSignatureLength > 0) {
                disFileSignature.readFully(fileSignatureBytes, 0, fileSignatureBytes.length);
            }

            // decrypt the signature
            byte[] decrytedFileSignature = RSAEncryption.decrypt(fileSignatureBytes, privatekey);

            // receive encrypted file
            DataInputStream disSplitFile = new DataInputStream(socket.getInputStream());
            int disSplitFileLength = disSplitFile.readInt();
            byte[] disSplitFileBytes = new byte[disSplitFileLength];
            if (disSplitFileLength > 0) {
                disSplitFile.readFully(disSplitFileBytes, 0, disSplitFileBytes.length);
            }

            // decrypt the split file
            byte[] splitFileContent = AESEncryption.aesDecrypt(disSplitFileBytes, AESkey);

            // verify the signature
            DigitalSignature dsFile = new DigitalSignature();
            dsFile.setDigest(new String(splitFileContent));

            signatureResult = dsFile.verifySignature(publicDSkey, decrytedFileSignature, dsFile.getDigest());
            System.out.println("Is file signature valid? : " + signatureResult);
            signatureResult = false;

            // save the file locally
            FileOutputStream fos = new FileOutputStream(str[1]);
            fos.write(splitFileContent);

            System.out.println("File received " + str[1]);

            fos.close();
//            disAESSignature.close();
//            disAES.close();
            disFileSignature.close();

//            // Send the message to the server
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.flush();
//
//            oos.writeObject(message);
//            Echo.echo("Message sent to the server is " + message);
//            socket.shutdownOutput();
//
//            // receive files
//            Long start = System.currentTimeMillis();
//
//            File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), message);
//            if (!file.exists()) file.createNewFile();
//
//            byte[] buffer = new byte[2048];
//            InputStream is = socket.getInputStream();
//            fos = new FileOutputStream(file.getAbsolutePath());
//            bos = new BufferedOutputStream(fos);
////                    bos = new BufferedOutputStream(new FileOutputStream(FILE_TO_RECEIVE));
//
//            Echo.echo("Ready to download...");
//
//            int read;
//            int totalRead = 0;
//            while ((read = is.read(buffer)) != -1) {
//                bos.write(buffer, 0, read);
//                totalRead += read;
//            }
//            bos.flush();
//
//            Echo.echo("File " + file.getAbsolutePath() + " " + file.length() + " downloaded");
//            long cost = System.currentTimeMillis() - start;
//            Echo.echo("Read " + totalRead + " bytes, download speed: " + totalRead / cost / 1000 + " MB/s");
//
//            is.close();
//            fos.close();
//            bos.close();
            socket.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
}
