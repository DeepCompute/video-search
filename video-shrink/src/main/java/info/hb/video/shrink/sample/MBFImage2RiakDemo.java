package info.hb.video.shrink.sample;

import info.hb.video.riak.client.Image2RiakCluster;
import info.hb.video.riak.common.VideoImageRiakConstant;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

public class MBFImage2RiakDemo {

	public static void main(String[] args) throws IOException {
		// http://hefei[08,09,10]:8098/riak/videos/colorimage
		MBFImage image = ImageUtilities.readMBF(new File("image/test.jpg"));
		BufferedImage bi = ImageUtilities.createBufferedImageForDisplay(image);
		Image2RiakCluster cluster = new Image2RiakCluster();
		cluster.writeImage(VideoImageRiakConstant.IMAGE_BUCKET_TYPE, "videos", "colorimage.png", bi, "png");
		// 关闭资源
		cluster.close();
	}

}
