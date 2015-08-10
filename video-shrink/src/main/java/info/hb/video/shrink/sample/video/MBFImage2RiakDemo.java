package info.hb.video.shrink.sample.video;

import info.hb.riak.cluster.client.HBRiakClient;
import info.hb.riak.cluster.client.HBRiakClusterImpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

public class MBFImage2RiakDemo {

	public static void main(String[] args) throws IOException {
		// http://hefei[08,09,10]:8098/riak/videos/colorimage
		MBFImage image = ImageUtilities.readMBF(new File("image/test1.jpg"));
		BufferedImage bi = ImageUtilities.createBufferedImageForDisplay(image);
		HBRiakClient cluster = new HBRiakClusterImpl();
		cluster.writeImage("default", "videos", "colorimage", bi, "png");
		// 关闭资源
		cluster.close();
	}

}
