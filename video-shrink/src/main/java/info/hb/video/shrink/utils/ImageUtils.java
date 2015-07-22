package info.hb.video.shrink.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

	public static String dumpImageToFile(BufferedImage image, String output) {
		try {
			String outputFilename = output + System.currentTimeMillis() + ".png";
			ImageIO.write(image, "png", new File(outputFilename));
			return outputFilename;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
