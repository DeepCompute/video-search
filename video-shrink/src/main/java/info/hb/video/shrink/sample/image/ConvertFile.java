package info.hb.video.shrink.sample.image;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In this tutorial, I will show you how to convert a file to a byte
 * array and convert a byte array to a file.
 * To convert a file to byte array, ByteArrayOutputStream class is used.
 * This class implements an output stream in which the data is written
 * into a byte array. The buffer automatically grows as data is written
 * to it. The data can be retrieved using toByteArray() and toString().
 *
 * To convert byte array back to the original file, FileOutputStream class is used.
 * A file output stream is an output stream for writing data to a File or to a FileDescriptor.
 *
 * @author wanggang
 *
 */
public class ConvertFile {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		File file = new File("java.pdf");

		FileInputStream fis = new FileInputStream(file);
		//System.out.println(file.exists() + "!!");
		//InputStream in = resource.openStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum); //no doubt here is 0
				//Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
				System.out.println("read " + readNum + " bytes,");
			}
		} catch (IOException ex) {
			Logger.getLogger(ConvertFile.class.getName()).log(Level.SEVERE, null, ex);
		}
		byte[] bytes = bos.toByteArray();

		//below is the different part
		File someFile = new File("java2.pdf");
		FileOutputStream fos = new FileOutputStream(someFile);
		fos.write(bytes);
		fos.flush();
		fos.close();
	}

	/**
	 * Here is the code for converting a file to a char array.
	 * Actually, it should be reading file content to a char array.
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static char[] ReadFileToCharArray(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString().toCharArray();
	}

}
