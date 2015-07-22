package info.hb.video.shrink.sample;

import java.io.File;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.analysis.algorithm.histogram.HistogramAnalyser;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.math.statistics.distribution.Histogram;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.xuggle.XuggleVideo;

/**
 * 	A simple test harness for processing videos through the OpenIMAJ Video
 * 	APIs.  This demo draws a histogram at the bottom of the image.
 */
public class OpenIMAJVideoAPIDemo implements VideoDisplayListener<MBFImage> {

	public OpenIMAJVideoAPIDemo() {
		try {
			XuggleVideo v = new XuggleVideo(new File("/home/donglei/test-videos/test6.mp4"));
			//			VideoCapture v = new VideoCapture(320, 240);
			VideoDisplay<MBFImage> vd = VideoDisplay.createVideoDisplay(v);
			vd.addVideoListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new OpenIMAJVideoAPIDemo();
	}

	/**
	 *  @inheritDoc
	 *  @see org.openimaj.video.VideoDisplayListener#beforeUpdate(org.openimaj.image.Image)
	 */
	@Override
	public void beforeUpdate(MBFImage frame) {
		plotHisto(frame, frame.getBand(0), RGBColour.RED);
		plotHisto(frame, frame.getBand(1), RGBColour.GREEN);
		plotHisto(frame, frame.getBand(2), RGBColour.BLUE);
	}

	/**
	 *    Plot a histogram into an image of another image in a given colour.
	 *    @param img The image to plot into
	 *    @param img2 The image whose histogram is to be plotted
	 *    @param colour The colour in which to plot the histogram.
	 */
	public void plotHisto(MBFImage img, FImage img2, Float[] colour) {
		// The number of bins is set to the image width here, but
		// we could specify a specific amount here.
		int nBins = img.getWidth();

		// Calculate the histogram
		Histogram h = HistogramAnalyser.getHistogram(img2, nBins);
		h.normalise();

		// Find the maximum so we can scale the bins
		double max = h.max();

		// Work out how fat to draw the lines.
		double lineWidth = img.getWidth() / nBins;

		// Now draw all the bins.
		int x = 0;
		for (double d : h.getVector()) {
			img.drawLine(x, img.getHeight(), x, img.getHeight() - (int) (d / max * img.getHeight()), (int) lineWidth,
					colour);
			x += lineWidth;
		}
	}

	/**
	 *	@inheritDoc
	 * 	@see org.openimaj.video.VideoDisplayListener#afterUpdate(org.openimaj.video.VideoDisplay)
	 */
	@Override
	public void afterUpdate(VideoDisplay<MBFImage> display) {
		// No implementation
	}

}
