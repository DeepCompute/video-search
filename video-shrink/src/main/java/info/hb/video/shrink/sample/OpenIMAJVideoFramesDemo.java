package info.hb.video.shrink.sample;

import java.io.File;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.xuggle.XuggleVideo;

public class OpenIMAJVideoFramesDemo {

	public static void main(String[] args) {
		Video<MBFImage> video = new XuggleVideo(new File("/home/wanggang/develop/deeplearning/test-videos/test1.ts"));
		VideoDisplay<MBFImage> videoDisplay = VideoDisplay.createVideoDisplay(video);
		videoDisplay.addVideoListener(new VideoDisplayListener<MBFImage>() {

			@Override
			public void beforeUpdate(MBFImage frame) {
				frame.processInplace(new CannyEdgeDetector());
			}

			@Override
			public void afterUpdate(VideoDisplay<MBFImage> display) {
				//
			}

		});

		//		for (MBFImage mbfImage : video) {
		//			System.out.println(video.getCurrentFrameIndex());
		//			DisplayUtilities.displayName(mbfImage.process(new CannyEdgeDetector()), "videoFrames");
		//		}
		//		video.close();

		// Iterator
		//		Iterator<MBFImage> iterator = video.iterator();
		//		while (iterator.hasNext()) {
		//			System.out.println(video.getCurrentFrameIndex());
		//			DisplayUtilities.displayName(iterator.next().process(new CannyEdgeDetector()), "videoFrames");
		//		}
	}

}
