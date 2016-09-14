import java.io.BufferedInputStream;
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
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.w3c.dom.ls.LSInput;

public class Client1 {

	private final static int PORT = 8080;
	private final static String SERVER_LOCATION = "127.0.0.1";
	protected final static int BUFFER_SIZE = 2048;
	public static boolean signatureResult = false;
	static String messageToServer = "2-1-1.pdf";
	static String[] str = messageToServer.split("-");
	static String[] fix = messageToServer.split("\\.");

	public static void echo(String msg) {
		System.out.println(msg);
	}

	private static void downloadFile(String intAddress, int port) throws IOException, InterruptedException {
		Socket socket = null;
		// Main step
		try {

			long startTime = System.nanoTime();
			
			socket = new Socket(intAddress, port);
			echo("Connecting to" + " " + socket.getInetAddress().getHostName() + " : " + socket.getPort());

			// receive AES signature from server
//			DataInputStream disAESSignature = new DataInputStream(socket.getInputStream());
//
//			int disAESDigitalSignatureLength = disAESSignature.readInt();
//			byte[] AESSignatureBytes = new byte[disAESDigitalSignatureLength];
//			if (disAESDigitalSignatureLength > 0) {
//				disAESSignature.readFully(AESSignatureBytes, 0, AESSignatureBytes.length);
//			}

			// decrypt the signature
			@SuppressWarnings("resource")
			final PrivateKey privatekey = (PrivateKey) new ObjectInputStream(new FileInputStream("./key/private.key"))
					.readObject();
			@SuppressWarnings("resource")
			final PublicKey publicDSkey = (PublicKey) new ObjectInputStream(new FileInputStream("./key/publicDS.key"))
					.readObject();
			
			// receive an AES key and a signature
			
			DataInputStream disAES = new DataInputStream(socket.getInputStream());

			int disAESLength = disAES.readInt();
			byte[] byteAES = new byte[disAESLength];
			long startAESTime = System.nanoTime();
//			Thread.sleep(15166);
			if (disAESLength > 0) {
				disAES.readFully(byteAES, 0, byteAES.length);
			}
			long endAESTime = System.nanoTime();
			
			System.out.println("disAESLength " + disAESLength + " B");
			System.out.println("Time " + (endAESTime - startAESTime) + " ns");
			System.out.println("File Speed:" + (float) disAESLength * 1000 / (endAESTime - startAESTime) + " MB/s");
			
			byte[] AESSignatureBytes = Arrays.copyOfRange(byteAES, 0, 256);
			byte[] AESKeyByte = Arrays.copyOfRange(byteAES, 256, disAESLength);			
			
			byte[] decrytedAESSignature = RSAEncryption.decrypt(AESSignatureBytes, privatekey);

			// receive the AES key
//			DataInputStream disAES = new DataInputStream(socket.getInputStream());
//
//			int disAESLength = disAES.readInt();
//			byte[] byteAES = new byte[disAESLength];
//			if (disAESLength > 0) {
//				disAES.readFully(byteAES, 0, byteAES.length);
//			}
			

			// decrypt the AES key
			byte[] decrytedAESKey = RSAEncryption.decrypt(AESKeyByte, privatekey);


			// verify the signature
			SecretKey AESkey = (SecretKey) Serialization.deserialize(decrytedAESKey);
			DigitalSignature dsAES = new DigitalSignature();
			dsAES.setDigest(AESkey.toString());

			signatureResult = dsAES.verifySignature(publicDSkey, decrytedAESSignature, dsAES.getDigest());
			System.out.println("Is AES key valid? : " + signatureResult);
			signatureResult = false;

			long aesTime = System.nanoTime();
			
			System.out.println("aesTime " +(aesTime - startTime)/1000000 + "ms");
			
			// Send the message to the file server
			byte[] messageByte = messageToServer.getBytes();
			byte[] m = AESEncryption.aesEncrypt(messageByte, AESkey);
			DataOutputStream dosMessage = new DataOutputStream(socket.getOutputStream());
			dosMessage.writeInt(m.length);
			dosMessage.write(m);
			System.out.println("Message sent to the server is " + messageToServer);

//			// receive the signature
//			DataInputStream disFileSignature = new DataInputStream(socket.getInputStream());
//
//			int disFileSignatureLength = disFileSignature.readInt();
//			byte[] fileSignatureBytes = new byte[disFileSignatureLength];
//			if (disFileSignatureLength > 0) {
//				disFileSignature.readFully(fileSignatureBytes, 0, fileSignatureBytes.length);
//			}

			// receive encrypted file and a siganture
			
			DataInputStream disSplitFile = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			int disSplitFileLength = disSplitFile.readInt();
			byte[] disSplitFileBytes = new byte[disSplitFileLength];
			
			long startFileTime = System.nanoTime();
			if (disSplitFileLength > 0) {
				disSplitFile.readFully(disSplitFileBytes, 0, disSplitFileBytes.length);
			}
			
			long endFileTime = System.nanoTime();
			System.out.println("File downlaod time:" + (float) (endFileTime - startFileTime) / 1000000 + " ms");
			
			byte[] fileSignatureBytes = Arrays.copyOfRange(disSplitFileBytes, 0, 256);
			byte[] fileByte = Arrays.copyOfRange(disSplitFileBytes, 256, disSplitFileLength);		
			
			// decrypt the signature
			byte[] decrytedFileSignature = RSAEncryption.decrypt(fileSignatureBytes, privatekey);
			

			// receive encrypted file
			// byte[] buffer = new byte[BUFFER_SIZE];
			// InputStream is = socket.getInputStream();
			// FileOutputStream fos = new FileOutputStream(fileLocation);
			//
			// echo("Ready to transmit...");
			//
			// int read;
			// int totalRead = 0;
			// while ((read = is.read(buffer)) != -1) {
			// bos.write(buffer, 0, read);
			// totalRead += read;
			// }
			//
			// bos.flush();

			// get the received file as byte[]
			// byte[] disSplitFileBytes =
			// Files.readAllBytes(Paths.get(fileLocation));

			// decrypt the split file
			byte[] splitFileContent = AESEncryption.aesDecrypt(fileByte, AESkey);

			// verify the signature
			DigitalSignature dsFile = new DigitalSignature();
			dsFile.setDigest(new String(splitFileContent));

			signatureResult = dsFile.verifySignature(publicDSkey, decrytedFileSignature, dsFile.getDigest());
			System.out.println("Is file signature valid? : " + signatureResult);
			signatureResult = false;
			
			long fileTime = System.nanoTime();
			
			System.out.println("fileTime " + (fileTime - startTime)/1000000 + "ms");

			// save the file locally
			FileOutputStream fosSplitFile = new FileOutputStream(str[1]);
			fosSplitFile.write(splitFileContent);
			Thread.sleep(1775);
			long fileSaveTime = System.nanoTime();
			
			System.out.println("File Speed:" + (float) disSplitFileLength * 1000 / (fileSaveTime - startTime) + " MB/s");
			
			System.out.println("File received " + str[1]);

			fosSplitFile.close();
			disAES.close();

		} catch (ClassNotFoundException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException,
			InvalidKeySpecException, ClassNotFoundException, InvalidKeyException, SignatureException, InterruptedException {
		// TODO Auto-generated method stub
//		 downloadFile(SERVER_LOCATION, PORT);
		File file = new File("/Users/senzheng/Desktop/416M.mov");
		long start = System.nanoTime();
		splitLargeFile(file, 3);
		long end = System.nanoTime();
		System.out.println((float) (end - start) / 1000000);
//		
//		long start = System.nanoTime();
//		List<File> fileList = new ArrayList<File>();
//		
//		for (int i = 1; i <= 2; i++) {
//			File tmp = new File(Integer.toString(i));
//			fileList.add(tmp);
//		}
//		
//		File re = new File("3.pdf");
//		
//		OutputStream os = new FileOutputStream(re);
//		MergeFiles.mergeFiles(fileList, os);
//		long end = System.nanoTime();
//		
//		System.out.println((float) (end - start) / 1000000);
		
		long start1 = System.nanoTime(); 
		mergeLargeFile();
		long end1 = System.nanoTime();
		
		System.out.println((float) (end1 - start1) / 1000000);
	}
	
	public static void splitLargeFile(File file, int n) throws InterruptedException {
		try {
			String[] env = {"PATH=/bin:/usr/bin/"};
			String cmd = "split -b " + file.length() / n + " " + file.getAbsolutePath() + " ";
			String cmd1 = "mv xaa 1";
			String cmd2 = "mv xab 2";
			String cmd3 = "mv xac 3";
			Process process = Runtime.getRuntime().exec(cmd, env);
			process.waitFor();
			Process process1 = Runtime.getRuntime().exec(cmd1, env);
			process1.waitFor();
			Process process2 = Runtime.getRuntime().exec(cmd2, env);
			process2.waitFor();
			Process process3 = Runtime.getRuntime().exec(cmd3, env);
			process3.waitFor();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void mergeLargeFile() throws InterruptedException {
		try {
			
			ProcessBuilder builder = new ProcessBuilder("cat", "1", "2", "3");
			builder.redirectOutput(new File("1.mov"));
			builder.redirectError(new File("1.mov"));
			Process p = builder.start(); // may throw IOException

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
