package info.hb.video.shrink.keyframes;

import info.hb.video.shrink.utils.ImageCompare;
import info.hb.video.shrink.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 视频关键帧提取
 *
 * @author wanggang
 *
 */
public class VideoKeyFrameExtraction {

	private static Logger logger = LoggerFactory.getLogger(VideoKeyFrameExtraction.class);

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		extractKeyFrame("/home/wanggang/develop/deeplearning/test-videos/test1.ts",
				"/home/wanggang/develop/deeplearning/test-videos/output");
		logger.info("Time spend：{}", System.currentTimeMillis() - startTime);
	}

	public static void extractKeyFrame(String path, String output) {
		Video<MBFImage> video = new XuggleVideo(new File(path));
		long totalFrame = video.countFrames();
		long fps = Math.round(video.getFPS());
		logger.info("video:{} has {} frames. FPS is {}", path, totalFrame, fps);
		int i = 0;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		BufferedImage lastImage = null;
		for (MBFImage mbfImage : video) {
			if (i % fps == 0) {
				BufferedImage bi = ImageUtilities.createBufferedImageForDisplay(mbfImage);
				executor.execute(new ExtractRunable(lastImage, bi));
				lastImage = bi;
			}
			i++;
		}
		video.close();
		executor.shutdown();
	}

	private static class ExtractRunable implements Runnable {

		private static Logger logger = LoggerFactory.getLogger(ExtractRunable.class);
		private BufferedImage cmpImage;
		private BufferedImage outputImage;

		public ExtractRunable(BufferedImage cmp, BufferedImage output) {
			this.cmpImage = cmp;
			this.outputImage = output;
		}

		@Override
		public void run() {
			if (outputImage == null) {
				return;
			}
			if (cmpImage == null) {
				ImageUtils.dumpImageToFile(outputImage, "/home/donglei/test-videos/output/thumbnails");
			} else {
				if (!ImageCompare.matchImage(cmpImage, outputImage)) {
					logger.info("输出关键帧！");
					ImageUtils.dumpImageToFile(outputImage, "/home/donglei/test-videos/output/thumbnails");
				}
			}
		}

	}

}
