package info.hb.video.shrink.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.GrayFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.log.LogbackUtil;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图片相似度比较
 *
 * @author wanggang
 *
 */
@SuppressWarnings("restriction")
public class ImageCompare {

	private static Logger logger = LoggerFactory.getLogger(ImageCompare.class);

	protected BufferedImage img1 = null;
	protected BufferedImage img2 = null;
	protected BufferedImage imgc = null;
	protected int comparex = 0;
	protected int comparey = 0;
	protected int factorA = 0;
	protected int factorD = 10;
	protected boolean match = Boolean.FALSE;
	// 1: textual indication of change, 2: difference of factors
	protected int debugMode = 0;
	private int diffMax = 0;

	/* create a runable demo thing. */
	public static void main(String[] args) {
		// Create a compare object specifying the 2 images for comparison.
		ImageCompare ic = new ImageCompare("/home/wanggang/develop/deeplearning/test-videos/output/thumbnails-01.jpeg",
				"/home/wanggang/develop/deeplearning/test-videos/output/thumbnails-02.jpeg");
		// Set the comparison parameters.
		//   (num vertical regions, num horizontal regions, sensitivity, stabilizer)
		ic.setParameters(80, 50, 50, 100);
		// Display some indication of the differences in the image.
		ic.setDebugMode(0);
		// Compare.
		ic.compare();
		// Display if these images are considered a match according to our parameters.
		System.out.println("Match: " + ic.match());
		System.out.println(ic.diffMax);
	}

	public static boolean matchImage(BufferedImage image1, BufferedImage image2) {
		ImageCompare ic = new ImageCompare(image1, image2);
		// Set the comparison parameters.
		//   (num vertical regions, num horizontal regions, sensitivity, stabilizer)
		ic.setParameters(80, 50, 5, 100);
		// Display some indication of the differences in the image.
		ic.setDebugMode(0);
		// Compare.
		ic.compare();
		// Display if these images are considered a match according to our parameters.
		return ic.match();
	}

	// constructor 1. use filenames
	public ImageCompare(String file1, String file2) {
		this(loadJPG(file1), loadJPG(file2));
	}

	// constructor 2. use awt images.
	public ImageCompare(Image img1, Image img2) {
		this(imageToBufferedImage(img1), imageToBufferedImage(img2));
	}

	// constructor 3. use buffered images. all roads lead to the same place. this place.
	public ImageCompare(BufferedImage img1, BufferedImage img2) {
		this.img1 = img1;
		this.img2 = img2;
		autoSetParameters();
	}

	// like this to perhaps be upgraded to something more heuristic in the future.
	protected void autoSetParameters() {
		comparex = 10;
		comparey = 10;
		factorA = 10;
		factorD = 10;
	}

	// set the parameters for use during change detection.
	public void setParameters(int x, int y, int factorA, int factorD) {
		this.comparex = x;
		this.comparey = y;
		this.factorA = factorA;
		this.factorD = factorD;
	}

	// want to see some stuff in the console as the comparison is happening?
	public void setDebugMode(int m) {
		this.debugMode = m;
	}

	// compare the two images in this object.
	public void compare() {
		// convert to gray images.
		img1 = imageToBufferedImage(GrayFilter.createDisabledImage(img1));
		img2 = imageToBufferedImage(GrayFilter.createDisabledImage(img2));
		// how big are each section
		int blocksx = img1.getWidth() / comparex;
		int blocksy = img1.getHeight() / comparey;
		// set to a match by default, if a change is found then flag non-match
		this.match = Boolean.TRUE;
		// loop through whole image and compare individual blocks of images
		for (int y = 0; y < comparey; y++) {
			if (debugMode > 0) {
				System.out.print("|");
			}
			for (int x = 0; x < comparex; x++) {
				int b1 = getAverageBrightness(img1.getSubimage(x * blocksx, y * blocksy, blocksx - 1, blocksy - 1));
				int b2 = getAverageBrightness(img2.getSubimage(x * blocksx, y * blocksy, blocksx - 1, blocksy - 1));
				int diff = Math.abs(b1 - b2);
				if (diff > factorA) { // the difference in a certain region has passed the threshold value of factorA
					this.diffMax = this.diffMax > diff ? this.diffMax : diff;
					this.match = false;
				}
				if (debugMode == 1) {
					System.out.print((diff > factorA ? "X" : " "));
				}
				if (debugMode == 2) {
					System.out.print(diff + (x < comparex - 1 ? "," : ""));
				}
			}
			if (debugMode > 0) {
				System.out.println("|");
			}
		}
	}

	// return the image that indicates the regions where changes where detected.
	public BufferedImage getChangeIndicator() {
		return imgc;
	}

	// returns a value specifying some kind of average brightness in the image.
	protected int getAverageBrightness(BufferedImage img) {
		Raster r = img.getData();
		int total = 0;
		for (int y = 0; y < r.getHeight(); y++) {
			for (int x = 0; x < r.getWidth(); x++) {
				total += r.getSample(r.getMinX() + x, r.getMinY() + y, 0);
			}
		}
		return total / ((r.getWidth() / factorD) * (r.getHeight() / factorD));
	}

	// returns true if image pair is considered a match
	public boolean match() {
		return this.match;
	}

	// buffered images are just better.
	protected static BufferedImage imageToBufferedImage(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);
		return bi;
	}

	// write a buffered image to a jpeg file.
	protected static void saveJPG(Image img, String filename) {
		BufferedImage bi = imageToBufferedImage(img);
		try (FileOutputStream out = new FileOutputStream(filename);) {
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
			param.setQuality(0.8f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bi);
		} catch (FileNotFoundException e) {
			logger.error("File Not Found");
		} catch (IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
	}

	// read a jpeg file into a buffered image
	protected static Image loadJPG(String filename) {
		BufferedImage bi = null;
		try (FileInputStream in = new FileInputStream(filename);) {
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
			bi = decoder.decodeAsBufferedImage();
		} catch (FileNotFoundException e) {
			logger.error("File Not Found");
		} catch (IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
		return bi;
	}

}
