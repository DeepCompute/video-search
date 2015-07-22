package info.hb.video.shrink.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

public class ImageManipulation {

	public static void main(String[] args) {
		File file = new File("test-image/test1.jpg");

		try {
			/*
			 * Reading a Image file from file system
			 */
			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);

			/*
			 * Converting Image byte array into Base64 String
			 */
			String imageDataString = encodeImage(imageData);

			/*
			 * Converting a Base64 String into Image byte array
			 */
			byte[] imageByteArray = decodeImage(imageDataString);

			/*
			 * Write a image byte array into file system
			 */
			FileOutputStream imageOutFile = new FileOutputStream("test-image/test1-1.jpg");
			imageOutFile.write(imageByteArray);

			imageInFile.close();
			imageOutFile.close();

			System.out.println("Image Successfully Manipulated!");
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}

	}

	public static String encodeImage(byte[] imageByteArray) {
		return Base64.encodeBase64URLSafeString(imageByteArray);
	}

	public static byte[] decodeImage(String imageDataString) {
		return Base64.decodeBase64(imageDataString);
	}

}
