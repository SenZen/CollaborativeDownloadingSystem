import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MultiThreadServer {

	protected static final int PORT = 8080;
	

	public static void main(String args[]) throws IOException {

		ServerSocket ss = null;
		Socket socket = null;

		try {
			ss = new ServerSocket(PORT);
			echo("Server socket created.  " + "Waiting for connection...");

			while (true) {
				socket = ss.accept();
				echo("Connection received from " + socket.getInetAddress().getHostName() + " : " + socket.getPort());
				new peerHandler(socket).start();
			}
		} catch (IOException e) {
			System.err.println("IOException");
		}

		try {
			ss.close();
		} catch (IOException ioException) {
			System.err.println("Unable to close. IOexception");
		}
	}

	public static void echo(String msg) {
		System.out.println(msg);
	}
}

class peerHandler extends Thread {

	protected Socket socket;
	protected final static int BUFFER_SIZE = 2048;

	peerHandler(Socket socket) {
		this.socket = socket;
	}


	public void run() {
		File myFile = null;

		try {
			
//			final PrivateKey privatekey = (PrivateKey) new ObjectInputStream(new FileInputStream("./key/private.key"))
//					.readObject();
//			
//			DataOutputStream dosPrivatekey = new DataOutputStream(socket.getOutputStream());
//			dosPrivatekey.writeInt(Serialization.serialize(privatekey).length);
//			dosPrivatekey.write(Serialization.serialize(privatekey));
			
			
//			final PublicKey publicDSkey = (PublicKey) new ObjectInputStream(new FileInputStream("./key/publicDS.key"))
//					.readObject();
//			
//			DataOutputStream dospublicDSkey = new DataOutputStream(socket.getOutputStream());
//			dospublicDSkey.writeInt(Serialization.serialize(publicDSkey).length);
//			dospublicDSkey.write(Serialization.serialize(privatekey));
			
			
			// produce a AES key and write it into a file
			SecretKey AESkey = AESEncryption.generateKey();	
			
			DigitalSignature dsAES = new DigitalSignature();
//
//			// sign a signature using privateDS.key on AES key
			dsAES.setDigest(AESkey.toString());
			@SuppressWarnings("resource")
			final PrivateKey privateDSKey = (PrivateKey) new ObjectInputStream(
					new FileInputStream("./key/privateDS.key")).readObject();
			byte[] AESSignatureBytes = dsAES.signSignature(privateDSKey, dsAES.getDigest());
//
//			// encrypt the signature
			@SuppressWarnings("resource")
			final PublicKey publicKey = (PublicKey) new ObjectInputStream(new FileInputStream("./key/public.key"))
					.readObject();
			byte[] encryptedAESSignature = RSAEncryption.encrypt(AESSignatureBytes, publicKey);
			System.out.println(encryptedAESSignature.length);

			// send the AES signature
//			DataOutputStream dosAESDigitalSignature = new DataOutputStream(socket.getOutputStream());
//			dosAESDigitalSignature.writeInt(encryptedAESSignature.length);
//			dosAESDigitalSignature.write(encryptedAESSignature);

			System.out.println("AES signature is sent..." + encryptedAESSignature.length);
//			
//			// encrypt the AES file
//			byte[] AESFile = Files.readAllBytes(Paths.get("./key/AES.key"));
//			byte[] EncryptedAESFile = RSAEncryption.encrypt(AESFile, publicKey);
////			InputStream is = new ByteArrayInputStream(EncryptedAESFile);
//
//			// send encrypted AES file
//			DataOutputStream dosAESFile = new DataOutputStream(socket.getOutputStream());
//			dosAESFile.writeInt(EncryptedAESFile.length);
//			dosAESFile.write(EncryptedAESFile);

			// encrypt the AES key
			byte[] encryptedAESKey = RSAEncryption.encrypt(Serialization.serialize(AESkey), publicKey);
			System.out.println(encryptedAESKey.length);
			
			byte[] combinedAESKey = new byte[encryptedAESSignature.length + encryptedAESKey.length];
			System.arraycopy(encryptedAESSignature, 0, combinedAESKey, 0, encryptedAESSignature.length);
			System.arraycopy(encryptedAESKey, 0, combinedAESKey, encryptedAESSignature.length, encryptedAESKey.length);
			
//			 send the AES file with a signature
			DataOutputStream dosAES = new DataOutputStream(socket.getOutputStream());
			dosAES.writeInt(combinedAESKey.length);
			dosAES.write(combinedAESKey);
				

			System.out.println("AES file is sent...");

			// receive message
			DataInputStream peerInfoStream = new DataInputStream(socket.getInputStream());
			int peerInfoStreamLength = peerInfoStream.readInt();
			byte[] peerInfoStreamBytes = new byte[peerInfoStreamLength];
			if (peerInfoStreamLength > 0) {
				peerInfoStream.readFully(peerInfoStreamBytes, 0, peerInfoStreamBytes.length);
			}
			System.out.println(new String(peerInfoStreamBytes));
			String[] information = new String(AESEncryption.aesDecrypt(peerInfoStreamBytes, AESkey)).split("-");
			System.out.println(new String(AESEncryption.aesDecrypt(peerInfoStreamBytes, AESkey)));

			int peerNumber = Integer.parseInt(information[0]);
			System.out.println("peerNumber is " + peerNumber);
			int peerID = Integer.parseInt(information[1]);
			System.out.println("peerID is " + peerID);
			String fileName = information[2];
			System.out.println("File name is " + fileName);

			// receive a file location
			String fileLocation = "/Users/senzheng/Desktop/" + fileName;
			System.out.println("The file need to be sent is" + fileLocation);

			// split the file
			// long startTime = System.currentTimeMillis();
			
			long startSplit = System.nanoTime(); 
			myFile = new File(fileLocation);
			FileSplit.splitLargeFile(myFile, peerNumber);
			long endSplit = System.nanoTime(); 
			System.out.println("Spliting time: " + (float) (endSplit - startSplit)/1000000 + "ms");
			String splitFile = System.getProperty("user.dir") + "/"
					+ myFile.getName().substring(0, myFile.getName().lastIndexOf(".")) + "_" + peerID;

			System.out.println("Spliting Done!");
			System.out.println("The file will be sent is" + splitFile);

			// sign a hashed signature on split file
			byte[] splitFileContent = Files.readAllBytes(Paths.get(splitFile));

			DigitalSignature dsFile = new DigitalSignature();
			dsFile.setDigest(new String(splitFileContent));

			byte[] fileSignatureByte = dsFile.signSignature(privateDSKey, dsFile.getDigest());

			// encrypt that signature
			byte[] encryptedFileSignature = RSAEncryption.encrypt(fileSignatureByte, publicKey);

//			// send the AES signature
//			DataOutputStream dosFileSignature = new DataOutputStream(socket.getOutputStream());
//			dosFileSignature.writeInt(encryptedFileSignature.length);
//			dosFileSignature.write(encryptedFileSignature); // write the message
//
//			MultiThreadServer.echo("File Signature transferred!");

			// encrypt the split file
			byte[] fileContent = Files.readAllBytes(Paths.get(splitFile));
			byte[] encryptedSplitFile = AESEncryption.aesEncrypt(fileContent, AESkey);
			
			System.out.println(encryptedFileSignature.length);
			
			byte[] combinedFileSignature = new byte[encryptedFileSignature.length + encryptedSplitFile.length];
			System.arraycopy(encryptedFileSignature, 0, combinedFileSignature, 0, encryptedFileSignature.length);
			System.arraycopy(encryptedSplitFile, 0, combinedFileSignature, encryptedFileSignature.length, encryptedSplitFile.length);
			
			
			// send encrypted split file and a signature
			DataOutputStream dosSplitFile = new DataOutputStream(socket.getOutputStream());
			dosSplitFile.writeInt(combinedFileSignature.length);
			dosSplitFile.write(combinedFileSignature);
			
//			InputStream is = new ByteArrayInputStream(splitFileCipher);

//			// send encrypted split file
//			DataOutputStream dosSplitFile = new DataOutputStream(socket.getOutputStream());
//			dosSplitFile.writeInt(splitFileCipher.length);
//			dosSplitFile.write(splitFileCipher);

			// another way to do it
			// byte[] buffer = new byte[BUFFER_SIZE];
			// OutputStream os = socket.getOutputStream();
			//
			// int read = 0, totalRead = 0;
			// while ((read = is.read(buffer)) != -1) {
			// os.write(buffer, 0, read);
			// totalRead += read;
			// }
			//
			// os.flush();

//			MultiThreadServer.echo(splitFile + " is sent!");

			peerInfoStream.close();
			socket.close();
		}

		catch (IOException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
