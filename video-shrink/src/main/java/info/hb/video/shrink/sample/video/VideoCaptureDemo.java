package info.hb.video.shrink.sample.video;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

public class VideoCaptureDemo {

	public static void main(String[] args) throws VideoCaptureException {
		VideoCapture video = new VideoCapture(320, 240);
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
	}

}
