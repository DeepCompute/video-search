package info.hb.video.shrink.core;

import java.io.File;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;

/**
 * 处理Video主类
 *
 * @author wanggang
 *
 */
public class VideoTackle {

	public static void main(String[] args) {

		Video<MBFImage> video = new XuggleVideo(new File("/home/donglei/test-videos/test1.ts"));
		System.out.println(video.countFrames());
		for (MBFImage mbfImage : video) {
			System.out.println(mbfImage.getHeight() + "," + mbfImage.getWidth());
		}
		video.close();
	}

}
