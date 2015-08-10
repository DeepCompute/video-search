package info.hb.video.shrink.sample.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * In this tutorial, I will show you two different ways to convert
 * an image to a byte array and convert a byte array to an image.
 * First of all, the byte type in Java is an 8-bit signed two's complement integer.
 * Its range is [-128, 127]. A byte array is just an array of bytes. An image is
 * essentially a file. So the task is converting the file to an array so that it
 * can be stored or transferred more easily in different kinds of applications.
 *
 * @author wanggang
 *
 */
public class ConvertImage {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		/*
		 * 1. How to convert an image file to  byte array?
		 */

		File file = new File("image/test1.jpg");

		FileInputStream fis = new FileInputStream(file);
		//create FileInputStream which obtains input bytes from a file in a file system
		//FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				//Writes to this byte array output stream
				bos.write(buf, 0, readNum);
			}
		} catch (IOException ex) {
			Logger.getLogger(ConvertImage.class.getName()).log(Level.SEVERE, null, ex);
		}

		byte[] bytes = bos.toByteArray();

		/*
		 * 2. How to convert byte array back to an image file?
		 */

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");

		//ImageIO is a class containing static methods for locating ImageReaders
		//and ImageWriters, and performing simple encoding and decoding.

		ImageReader reader = (ImageReader) readers.next();
		Object source = bis;
		ImageInputStream iis = ImageIO.createImageInputStream(source);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();

		Image image = reader.read(0, param);
		//got an image file

		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		//bufferedImage is the RenderedImage to be written

		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, null, null);

		File imageFile = new File("image/test.jpg");
		ImageIO.write(bufferedImage, "jpg", imageFile);

		System.out.println(imageFile.getPath());
	}

}
