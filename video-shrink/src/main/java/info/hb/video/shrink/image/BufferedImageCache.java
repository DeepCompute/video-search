package info.hb.video.shrink.image;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BufferedImageCache {

	private static List<BufferedImage> images = new LinkedList<>();

	public static void addBufferedImage(BufferedImage image) {
		synchronized (images) {
			images.add(image);
		}
	}

	public static BufferedImage getFirstBufferedImage(BufferedImage image) {
		synchronized (images) {
			return images.get(0);
		}
	}

}
